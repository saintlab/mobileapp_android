package com.omnom.android.linker.activity.bind;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.linker.BuildConfig;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.ErrorHelper;
import com.omnom.android.linker.activity.UserProfileActivity;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.ResponseBase;
import com.omnom.android.linker.model.beacon.BeaconDataResponse;
import com.omnom.android.linker.model.restaurant.Restaurant;
import com.omnom.android.linker.model.restaurant.RestaurantHelper;
import com.omnom.android.linker.model.table.TableDataResponse;
import com.omnom.android.linker.observable.BaseErrorHandler;
import com.omnom.android.linker.observable.OmnomObservable;
import com.omnom.android.linker.observable.ValidationObservable;
import com.omnom.android.linker.service.BluetoothLeService;
import com.omnom.android.linker.service.CharacteristicHolder;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderController;
import com.omnom.android.linker.widget.loader.LoaderView;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action0;
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 14.08.2014.
 */
public class BindActivity extends BaseActivity {
	private static final String TAG = BindActivity.class.getSimpleName();
	private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;
	private static final int REQUEST_CODE_SCAN_QR = 101;

	public static void start(final Context context, Restaurant restaurant, final boolean showBack) {
		final Intent intent = new Intent(context, BindActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_SHOW_BACK, showBack);
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	private final View.OnClickListener mInternetErrorClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			bindTable();
		}
	};
	private final View.OnClickListener mConnectBeaconClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			connectToBeacon();
		}
	};

	protected volatile boolean gattConnected = false;
	protected volatile boolean gattAvailable = false;

	@InjectView(R.id.loader)
	protected LoaderView mLoader;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	@InjectView(R.id.btn_bottom)
	protected Button mBtnBottom;

	@InjectView(R.id.btn_bind_table)
	protected Button mBtnBindTable;

	@InjectView(R.id.btn_back)
	protected View mBtnBack;

	@InjectView(R.id.btn_profile)
	protected View mBtnProfile;

	@InjectView(R.id.txt_error)
	protected TextView mTxtError;

	@InjectViews({R.id.txt_error, R.id.panel_bottom, R.id.btn_profile})
	protected List<View> errorViews;

	@Inject
	protected LinkerObeservableApi api;

	protected BluetoothLeService mBluetoothLeService;
	private BluetoothAdapter.LeScanCallback mLeScanCallback = null;
	private BeaconParser parser;
	private boolean mBound = false;

	private final ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if(!mBluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize Bluetooth");
				finish();
			}
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
			mBound = false;
		}
	};

	@NotNull
	private Restaurant mRestaurant;

	@Nullable
	private Beacon mBeacon = null;

	private Set<Beacon> mBeacons = new HashSet<Beacon>();
	private LoaderController mLoaderController;
	private BluetoothAdapter mBluetoothAdapter;
	private int mLoaderTranslation;
	private Subscription mErrValidationSubscription;
	private Subscription mErrBindSubscription;
	private ErrorHelper mErrorHelper;
	private boolean mBindClicked = false;
	private String mQrData = StringUtils.EMPTY_STRING;
	private BeaconDataResponse mBeaconData = BeaconDataResponse.NULL;
	private boolean mApiBindComplete = false;
	private Subscription mApiBindingSubscription;

	final Runnable beaconTimeoutCallback = new Runnable() {
		@Override
		public void run() {
			if(!gattAvailable || !gattConnected) {
				OmnomObservable.unsubscribe(mApiBindingSubscription);
				mErrorHelper.showError(R.drawable.ic_weak_signal, R.string.error_maintenance_mode_off, R.string.try_once_again,
				                       mInternetErrorClickListener);
			} else if(!mApiBindComplete) {
				OmnomObservable.unsubscribe(mApiBindingSubscription);
				mErrorHelper.showError(R.drawable.ic_no_connection, R.string.error_unknown_server_error, R.string.bind_table,
				                       mInternetErrorClickListener);
			} else {
				mLoader.updateProgressMax(new Runnable() {
					@Override
					public void run() {
						mLoader.animateLogo(R.drawable.ic_done_white);
						mLoader.postDelayed(new Runnable() {
							@Override
							public void run() {
								mLoader.animateLogo(RestaurantHelper.getLogo(mRestaurant), R.drawable.ic_fork_n_knife);
							}
						}, getResources().getInteger(R.integer.binding_done_icon_delay));
						mLoader.showProgress(false, true);
						AnimationUtils.animateAlpha(mPanelBottom, true);
						mBtnBottom.setText(R.string.bind_table);
						mPanelBottom.setVisibility(View.VISIBLE);
						mBtnProfile.setVisibility(View.VISIBLE);
					}
				});
			}
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
		}
	};

	private void scanBleDevices(final boolean enable, final Runnable endCallback) {
		if(enable) {
			mBeacons.clear();
			findViewById(android.R.id.content).postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					if(endCallback != null) {
						endCallback.run();
					}
				}
			}, getResources().getInteger(R.integer.ble_scan_duration));
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			if(endCallback != null) {
				endCallback.run();
			}
		}
	}

	@Override
	protected void handleIntent(Intent intent) {
		if(BuildConfig.DEBUG) {
			if(!intent.hasExtra(EXTRA_RESTAURANT) && intent.getParcelableExtra(EXTRA_RESTAURANT) == null) {
				throw new AssertionError("EXTRA_RESTAURANT must be specified and cannot be null");
			}
		}
		onRestaurantLoaded((Restaurant) intent.getParcelableExtra(EXTRA_RESTAURANT));
		ViewUtils.setVisible(mBtnBack, intent.getBooleanExtra(EXTRA_SHOW_BACK, false));
	}

	@OnClick(R.id.btn_back)
	public void onBack() {
		onBackPressed();
	}

	@OnClick(R.id.btn_profile)
	public void onProfile() {
		mBindClicked = false;
		final int profileAnimDuration = getResources().getInteger(R.integer.user_profile_animation_duration);
		mLoader.hideLogo(profileAnimDuration);
		mLoader.scaleDown(0, profileAnimDuration, new Runnable() {
			@Override
			public void run() {
				UserProfileActivity.start(BindActivity.this);
			}
		});
	}

	@OnClick(R.id.btn_bind_table)
	public void onBind() {
		AndroidUtils.hideKeyboard(findById(this, R.id.edit_table_number));
		AnimationUtils.animateAlpha(mBtnBindTable, false);
		api.buildBeacon(mRestaurant.getId(), mLoader.getTableNumber(), mBeacon.getIdValue(0))
		   .subscribe(new Action1<BeaconDataResponse>() {
			   @Override
			   public void call(final BeaconDataResponse beaconData) {
				   mBeaconData = beaconData;
				   mLoaderController.setMode(LoaderView.Mode.NONE);
				   connectToBeacon();
			   }
		   }, new BaseErrorHandler(this) {
			   @Override
			   protected void onThrowable(Throwable throwable) {
				   mLoaderController.hideEnterData();
				   mErrorHelper.showInternetError(mInternetErrorClickListener);
			   }
		   });
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(EXTRA_BEACON, mBeacon);
	}

	@Override
	protected void handleSavedState(Bundle savedInstanceState) {
		mBeacon = savedInstanceState.getParcelable(EXTRA_BEACON);
	}

	@DebugLog
	private void connectToBeacon() {
		mLoader.startProgressAnimation(getResources().getInteger(R.integer.bind_beacon_writing_duration), beaconTimeoutCallback);
		mErrValidationSubscription = AndroidObservable.bindActivity(this, ValidationObservable.validate(this).map(
				OmnomObservable.getValidationFunc(this, mErrorHelper, mInternetErrorClickListener)).isEmpty())
		                                              .subscribe(
				                                              new Action1<Boolean>() {
					                                              @Override
					                                              public void call(Boolean hasNoErrors) {
						                                              if(hasNoErrors) {
							                                              if(mBeacon == null || !mBluetoothLeService.connect(
									                                              mBeacon.getBluetoothAddress())) {
								                                              mLoader.stopProgressAnimation(true);
							                                              }
						                                              }
					                                              }
				                                              }, new BaseErrorHandler(getActivity()) {
					                                              @Override
					                                              protected void onThrowable(Throwable throwable) {
						                                              mErrorHelper.showInternetError(mConnectBeaconClickListener);
					                                              }
				                                              });
	}

	@Override
	public void finish() {
		mLoaderController.setMode(LoaderView.Mode.NONE);
		AnimationUtils.animateAlpha(mBtnBack, false);
		AnimationUtils.animateAlpha(mBtnProfile, false);
		AnimationUtils.animateAlpha(mBtnBindTable, false);
		if(getIntent().getBooleanExtra(EXTRA_SHOW_BACK, false)) {
			mLoader.animateColor(Color.WHITE);
			ButterKnife.apply(errorViews, ViewUtils.VISIBLITY_ALPHA, false);
			AnimationUtils.animateAlpha(mBtnBack, false);
			mLoader.scaleUp(new Runnable() {
				@Override
				public void run() {
					BindActivity.super.finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out_half);
				}
			});
		} else {
			super.finish();
		}
	}

	private void onRestaurantLoaded(Restaurant restaurant) {
		if(getIntent().getBooleanExtra(EXTRA_SHOW_BACK, false)) {
			mLoader.animateColor(Color.WHITE,
			                     getResources().getColor(R.color.loader_bg),
			                     getResources().getInteger(R.integer.default_animation_duration_long));
			// mLoader.setLogo(R.drawable.ic_mexico_logo);
			AnimationUtils.animateAlpha(mPanelBottom, true);
			AnimationUtils.animateAlpha(mBtnProfile, true);
			mLoader.post(new Runnable() {
				@Override
				public void run() {
					mLoader.scaleDown(null, new Runnable() {
						@Override
						public void run() {
							AnimationUtils.animateAlpha(mBtnBack, true);
						}
					});
				}
			});
		} else {
			mLoader.scaleDown();
			// mLoader.animateLogo(R.drawable.ic_mexico_logo);
			mLoader.postDelayed(new Runnable() {
				@Override
				public void run() {
					AnimationUtils.animateAlpha(mPanelBottom, true);
					AnimationUtils.animateAlpha(mBtnProfile, true);
				}
			}, getResources().getInteger(R.integer.default_animation_duration_long));
		}
		initRestaurantUi(restaurant);
	}

	private void initRestaurantUi(Restaurant restaurant) {
		mRestaurant = restaurant;
		mLoader.animateLogo(RestaurantHelper.getLogo(restaurant), R.drawable.ic_fork_n_knife);
		mBtnBottom.setText(R.string.bind_table);
		mBtnBottom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mBindClicked = true;
				bindTable();
			}
		});
		ViewUtils.setVisible(mPanelBottom, true);
		ViewUtils.setVisible(mBtnBottom, true);
	}

	private void scanQrCode() {
		startActivityForResult(new Intent(this, CaptureActivity.class), REQUEST_CODE_SCAN_QR);
	}

	private void clearErrors() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
				clearErrors();
			} else if(requestCode == REQUEST_CODE_SCAN_QR) {
				mPanelBottom.setVisibility(View.GONE);
				mBtnProfile.setVisibility(View.GONE);
				mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				api.checkQrCode(mQrData).onErrorReturn(OmnomObservable.getTableOnError()).subscribe(new Action1<TableDataResponse>() {
					@Override
					public void call(TableDataResponse result) {
						if(result == null) {
							mErrorHelper.showInternetError(mInternetErrorClickListener);
							return;
						}
						if(result != TableDataResponse.NULL) {
							onErrorQrCheck(result.getInternalId());
						} else {
							swithToEnterDataMode();
						}
					}
				});
			}
		} else {
			ViewUtils.setVisible(mPanelBottom, true);
			ViewUtils.setVisible(mBtnProfile, true);
		}
	}

	private void swithToEnterDataMode() {
		mLoaderController.setMode(LoaderView.Mode.ENTER_DATA);
		AnimationUtils.animateAlpha(mBtnBindTable, true);
	}

	private void bindTable() {
		mLoader.animateLogoFast(RestaurantHelper.getLogo(mRestaurant), R.drawable.ic_fork_n_knife);
		mLoader.animateColorDefault();
		clearErrors();
		mApiBindComplete = false;
		mLoader.startProgressAnimation(getResources().getInteger(R.integer.validation_duration), null);
		mErrBindSubscription = AndroidObservable.bindActivity(this, ValidationObservable.validate(this).map(
				OmnomObservable.getValidationFunc(this, mErrorHelper, mInternetErrorClickListener)).isEmpty()).subscribe(
				new Action1<Boolean>() {
					@Override
					public void call(Boolean hasNoErrors) {
						if(hasNoErrors) {
							scanBleDevices(true, new Runnable() {
								@Override
								public void run() {
									final int size = mBeacons.size();
									if(size == 0) {
										mErrorHelper.showError(R.drawable.ic_weak_signal, R.string.error_weak_beacon_signal,
										                       R.string.try_once_again,
										                       mInternetErrorClickListener);
									} else if(size > 1) {
										mErrorHelper.showError(R.drawable.ic_weak_signal, R.string.error_more_than_one_beacon,
										                       R.string.try_once_again,
										                       mInternetErrorClickListener);
									} else if(size == 1) {
										mBeacon = (Beacon) mBeacons.toArray()[0];
										api.findBeacon(mBeacon).onErrorReturn(OmnomObservable.getTableOnError()).subscribe(
												new Action1<TableDataResponse>() {
													@Override
													public void call(final TableDataResponse tableData) {
														if(tableData == null) {
															mErrorHelper.showInternetError(mInternetErrorClickListener);
															return;
														}
														mBindClicked = false;
														mLoader.updateProgressMax(new Runnable() {
															@Override
															public void run() {
																if(tableData != TableDataResponse.NULL) {
																	onErrorBeaconCheck(tableData.getInternalId());
																} else {
																	scanQrCode();
																}
															}
														});
													}
												}, new BaseErrorHandler(getActivity()) {
													@Override
													protected void onThrowable(Throwable throwable) {
														Log.e(TAG, "findBeacon", throwable);
														mErrorHelper.showInternetError(mInternetErrorClickListener);
													}
												});
									}
								}
							});
						}
					}
				}, new BaseErrorHandler(getActivity()) {
					@Override
					protected void onThrowable(Throwable throwable) {
						Log.e(TAG, "bindTable", throwable);
						mErrorHelper.showInternetError(mInternetErrorClickListener);
					}
				});
	}

	private void onErrorQrCheck(final int number) {
		mLoader.stopProgressAnimation(true);
		AndroidUtils.showDialog(getActivity(), getString(R.string.qr_already_bound, number), R.string.proceed,
		                        new DialogInterface.OnClickListener() {
			                        @Override
			                        public void onClick(DialogInterface dialog, int which) {
				                        swithToEnterDataMode();
			                        }
		                        }, R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						resetActivityState();
					}
				});
	}

	private void resetActivityState() {
		mBeaconData = BeaconDataResponse.NULL;
		mQrData = StringUtils.EMPTY_STRING;
		mBeacons.clear();
		mBindClicked = false;
		mLoader.updateProgress(0);
		initRestaurantUi(mRestaurant);
	}

	private void onErrorBeaconCheck(final int number) {
		mLoader.stopProgressAnimation(true);
		AndroidUtils.showDialog(getActivity(), getString(R.string.beacon_already_bound, number), R.string.proceed,
		                        new DialogInterface.OnClickListener() {
			                        @Override
			                        public void onClick(DialogInterface dialog, int which) {
				                        scanQrCode();
			                        }
		                        }, R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						resetActivityState();
					}
				});
	}

	@Override
	public void initUi() {
		mLoaderController = new LoaderController(this, mLoader);
		mErrorHelper = new ErrorHelper(mLoader, mTxtError, mBtnBottom, errorViews);
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mLoaderTranslation = ViewUtils.dipToPixels(this, 48);

		mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
			BeaconFilter mFilter = new BeaconFilter(BindActivity.this);

			@Override
			@DebugLog
			public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
						if(mFilter.check(beacon)) {
							mBeacons.add(beacon);
						}
					}
				});
			}
		};

		parser = new BeaconParser();
		parser.setBeaconLayout(getResources().getString(R.string.redbear_beacon_layout));
		final int btnTranslation = (int) (mLoaderTranslation * 1.75);
		final View activityRootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
		ViewTreeObserver.OnGlobalLayoutListener listener = AndroidUtils.createKeyboardListener(activityRootView,
		                                                                                       new AndroidUtils.KeyboardVisibilityListener
				                                                                                       () {
			                                                                                       @Override
			                                                                                       public void onVisibilityChanged(boolean
					                                                                                                                       isVisible) {
				                                                                                       if(isVisible) {
					                                                                                       AnimationUtils.translateUp(
							                                                                                       Collections
									                                                                                       .singletonList(
											                                                                                       (View)
													                                                                                       mBtnBindTable),
							                                                                                       btnTranslation, null);

					                                                                                       mLoader.translateUp(null,
					                                                                                                           mLoaderTranslation);
				                                                                                       } else {
					                                                                                       AnimationUtils.translateDown(
							                                                                                       Collections
									                                                                                       .singletonList(
											                                                                                       (View)
													                                                                                       mBtnBindTable),
							                                                                                       btnTranslation, null);
					                                                                                       mLoader.translateDown(null,
					                                                                                                             mLoaderTranslation);
				                                                                                       }
			                                                                                       }
		                                                                                       });
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mBluetoothLeService == null) {
			bindService(new Intent(this, BluetoothLeService.class), mServiceConnection, BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mBound) {
			unbindService(mServiceConnection);
			mBound = false;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus && mBindClicked) {
			postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
				@Override
				public void run() {
					bindTable();
				}
			});
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
			@Override
			public void run() {
				if(mLoader.getSize() == 0) {

					mLoader.scaleDown(null, new Runnable() {
						@Override
						public void run() {
							mLoader.showLogo(getResources().getInteger(R.integer.user_profile_animation_duration));
						}
					});
				}
			}
		});
	}

	@Subscribe
	public void onGattEvent(GattEvent event) {
		if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(event.getAction())) {
			gattAvailable = true;
			writeBeaconData();
		} else if(BluetoothLeService.ACTION_GATT_FAILED.equals(event.getAction())) {
			gattConnected = false;
			gattAvailable = false;
		} else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(event.getAction())) {
			gattConnected = false;
			gattAvailable = false;
		} else if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(event.getAction())) {
			gattConnected = true;
			mBluetoothLeService.getDiscoverGattService();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLoader.onDestroy();
		OmnomObservable.unsubscribe(mErrValidationSubscription);
		OmnomObservable.unsubscribe(mApiBindingSubscription);
		OmnomObservable.unsubscribe(mErrBindSubscription);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_bind;
	}

	@DebugLog
	public void writeBeaconData() {
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createPassword(getString(R.string.redbear_beacon_password)
				                                                                            .getBytes()));
		final byte txValue = (byte)Integer.parseInt(getString(R.string.redbear_beacon_tx));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createTx(new byte[] {txValue}));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createUuid(mBeaconData.getUuid()));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createMajorId(mBeaconData.getMajor()));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createMinorId(mBeaconData.getMinor()));
		mBluetoothLeService.startWritingQueue(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mApiBindingSubscription = AndroidObservable.bindActivity(getActivity(), Observable.merge(
								api.bindBeacon(mRestaurant.getId(), mLoader.getTableNumber(), mBeaconData, mBeacon),
								api.bindQrCode(mRestaurant.getId(), mLoader.getTableNumber(), mQrData)))
						                                           .subscribe(new Action1<ResponseBase>() {
							                                           @Override
							                                           public void call(ResponseBase responseBase) {
								                                           // Do nothing
							                                           }
						                                           }, new BaseErrorHandler(BindActivity.this) {
							                                           @Override
							                                           protected void onThrowable(Throwable throwable) {
								                                           if(throwable instanceof RetrofitError) {
									                                           final RetrofitError cause = (RetrofitError) throwable;
									                                           final Response response = cause.getResponse();
									                                           if(response != null
											                                           && response.getStatus() == HttpStatus.SC_CONFLICT) {
										                                           // TODO: Implement error handling
									                                           }
								                                           }
								                                           mErrorHelper.showError(R.drawable.ic_no_connection,
								                                                                  R.string.error_unknown_server_error,
								                                                                  R.string.bind_table,
								                                                                  mInternetErrorClickListener);
								                                           resetActivityState();
							                                           }
						                                           }, new Action0() {
							                                           @Override
							                                           public void call() {
								                                           mApiBindComplete = true;
							                                           }
						                                           });
					}
				});
			}
		});
	}

}
