package com.saintlab.android.linker.activity.bind;

import android.app.Activity;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.beacon.BeaconFilter;
import com.omnom.android.beacon.BeaconRssiProvider;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.table.RestaurateurObservable;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.loader.LoaderController;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.DialogUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.saintlab.android.linker.BuildConfig;
import com.saintlab.android.linker.LinkerApplication;
import com.saintlab.android.linker.R;
import com.saintlab.android.linker.activity.LinkerBaseErrorHandler;
import com.saintlab.android.linker.activity.UserProfileActivity;
import com.saintlab.android.linker.loader.LinkerLoaderError;
import com.saintlab.android.linker.service.BluetoothLeService;
import com.saintlab.android.linker.service.CharacteristicHolder;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ServiceConfigurationError;

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
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

/**
 * Created by Ch3D on 14.08.2014.
 */
public class BindActivity extends BaseActivity {
	private static final String TAG = BindActivity.class.getSimpleName();

	private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 100;

	private static final int REQUEST_CODE_SCAN_QR = 101;

	private static final String TAG_BEACONS = "BEACONS";

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
	protected RestaurateurObservableApi api;

	@Inject
	protected BeaconRssiProvider rssiProvider;

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

	private Restaurant mRestaurant;

	@Nullable
	private Beacon mBeacon = null;

	private ArrayList<Beacon> mBeacons = new ArrayList<Beacon>();

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
				mErrorHelper.showError(LinkerLoaderError.MAINTENANCE_DISABLED, mInternetErrorClickListener);
			} else if(!mApiBindComplete) {
				OmnomObservable.unsubscribe(mApiBindingSubscription);
				mErrorHelper.showError(LinkerLoaderError.NO_CONNECTION_BIND, mInternetErrorClickListener);
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

	private Subscription mBuildBeaconsSubscribtion;

	private Subscription mFindBeaconSubscription;

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
		ViewUtils.setVisibleGone(mBtnBack, intent.getBooleanExtra(EXTRA_SHOW_BACK, false));
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
		final int tableNumber = mLoader.getTableNumber();
		if(tableNumber == LoaderView.WRONG_TABLE_NUMBER) {
			showToast(this, R.string.enter_table_number);
			return;
		}
		AndroidUtils.hideKeyboard(findById(this, R.id.edit_table_number));
		AnimationUtils.animateAlpha(mBtnBindTable, false);
		mBuildBeaconsSubscribtion = AppObservable.bindActivity(this, api.buildBeacon(mRestaurant.id(), tableNumber,
		                                                                                 mBeacon.getIdValue(0))).subscribe(
				new RestaurateurObservable.AuthAwareOnNext<BeaconDataResponse>(getActivity()) {
					@Override
					public void perform(BeaconDataResponse beaconData) {
						mBeaconData = beaconData;
						mLoaderController.setMode(LoaderView.Mode.NONE);
						connectToBeacon();
					}
				}, new LinkerBaseErrorHandler(this) {
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
		final Func1<ValidationObservable.Error, Boolean> validationFunc = OmnomObservable.getValidationFunc(this,
		                                                                                                    mErrorHelper,
		                                                                                                    mInternetErrorClickListener);
		mErrValidationSubscription = AppObservable.bindActivity(this, ValidationObservable.validate(this)
		                                                                                      .map(validationFunc)
		                                                                                      .isEmpty())
		                                              .subscribe(new Action1<Boolean>() {
			                                              @Override
			                                              public void call(Boolean hasNoErrors) {
				                                              if(hasNoErrors) {
					                                              if(mBeacon == null
							                                              || !mBluetoothLeService.connect(mBeacon.getBluetoothAddress())) {
						                                              mLoader.stopProgressAnimation(true);
					                                              }
				                                              }
			                                              }
		                                              }, new LinkerBaseErrorHandler(getActivity()) {
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
		mLoader.clearLogo();
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
			mLoader.animateColor(Color.WHITE, getResources().getColor(R.color.loader_bg),
			                     getResources().getInteger(R.integer.default_animation_duration_long));
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
		ViewUtils.setVisibleGone(mPanelBottom, true);
		ViewUtils.setVisibleGone(mBtnBottom, true);
		rssiProvider.updateRssiThreshold(restaurant);
	}

	private void scanQrCode() {
		startActivityForResult(new Intent(this, CaptureActivity.class), REQUEST_CODE_SCAN_QR);
	}

	private void clearErrors() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
				clearErrors();
			} else if(requestCode == REQUEST_CODE_SCAN_QR) {
				mPanelBottom.setVisibility(View.GONE);
				mBtnProfile.setVisibility(View.GONE);
				mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				api.checkQrCode(mQrData).onErrorReturn(RestaurateurObservable.getTableOnError()).subscribe(
						new Action1<TableDataResponse>() {
							@Override
							public void call(TableDataResponse result) {
								if(result == null) {
									mErrorHelper.showInternetError(mInternetErrorClickListener);
									return;
								}
								final boolean sameRest = isSameRestaurant(result);
								if(result.hasErrors() || !sameRest) {
									swithToEnterDataMode();
								} else if(result != TableDataResponse.NULL) {
									onErrorQrCheck(result.getInternalId());
								} else {
									swithToEnterDataMode();
								}
							}
						});
			}
		} else {
			ViewUtils.setVisibleGone(mPanelBottom, true);
			ViewUtils.setVisibleGone(mBtnProfile, true);
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
		mLoader.startProgressAnimation(getResources().getInteger(R.integer.bind_validation_duration), null);
		mErrBindSubscription = AppObservable.bindActivity(this, ValidationObservable.validate(this)
		                                                                            .map(OmnomObservable.getValidationFunc(this,
		                                                                                                                   mErrorHelper,
		                                                                                                                   mInternetErrorClickListener))
		                                                                            .isEmpty())
		                                        .subscribe(new Action1<Boolean>() {
			                                        @Override
			                                        public void call(Boolean hasNoErrors) {
				                                        if(hasNoErrors) {
					                                        scanBleDevices(true, new Runnable() {
						                                        @Override
						                                        public void run() {
							                                        final Activity context = getActivity();
							                                        Log.d(TAG_BEACONS, "finded beacons size = " + mBeacons.size());
							                                        Log.d(TAG_BEACONS, "beacons = " + Arrays.toString(mBeacons.toArray()));
							                                        final BeaconFilter filter = new BeaconFilter(LinkerApplication.get(
									                                        context));
							                                        final List<Beacon> nearBeacons = filter.filterBeacons(mBeacons);
							                                        final int size = nearBeacons.size();
							                                        if(size == 0) {
								                                        mErrorHelper.showError(LinkerLoaderError.WEAK_SIGNAL,
								                                                               mInternetErrorClickListener);
							                                        } else if(size > 1) {
								                                        mErrorHelper.showError(LinkerLoaderError.TWO_BEACONS,
								                                                               mInternetErrorClickListener);
							                                        } else if(size == 1) {
								                                        mBeacon = nearBeacons.get(0);
								                                        mFindBeaconSubscription = AppObservable.bindActivity(
										                                        context,
										                                        api.findBeacon(mBeacon).onErrorReturn(
												                                        RestaurateurObservable
														                                        .getTableOnError())).subscribe(
										                                        new RestaurateurObservable
												                                        .AuthAwareOnNext<TableDataResponse>(
												                                        context) {
											                                        @Override
											                                        public void perform(final TableDataResponse
													                                                            tableData) {
												                                        if(tableData == null) {
													                                        mErrorHelper.showInternetError(
															                                        mInternetErrorClickListener);
													                                        return;
												                                        }
												                                        mBindClicked = false;
												                                        mLoader.stopProgressAnimation();
												                                        mLoader.updateProgressMax(new Runnable() {
													                                        @Override
													                                        public void run() {
														                                        final boolean sameRest =
																                                        isSameRestaurant(
																		                                        tableData);
														                                        if(tableData.hasErrors() || !sameRest) {
															                                        scanQrCode();
														                                        } else if(tableData
																                                        != TableDataResponse.NULL) {
															                                        final int tableId = tableData
																	                                        .getInternalId();
															                                        onErrorBeaconCheck(tableId);
														                                        } else {
															                                        scanQrCode();
														                                        }
													                                        }
												                                        });
											                                        }
										                                        },
										                                        new LinkerBaseErrorHandler(context) {
											                                        @Override
											                                        protected void onThrowable(Throwable throwable) {
												                                        Log.e(TAG, "findBeacon", throwable);
												                                        mErrorHelper.showInternetError(
														                                        mInternetErrorClickListener);
											                                        }
										                                        });
							                                        }
						                                        }
					                                        });
				                                        }
			                                        }
		                                        }, new LinkerBaseErrorHandler(getActivity()) {
			                                        @Override
			                                        protected void onThrowable(Throwable throwable) {
				                                        Log.e(TAG, "bindTable", throwable);
				                                        mErrorHelper.showInternetError(mInternetErrorClickListener);
			                                        }
		                                        });
	}

	private boolean isSameRestaurant(final TableDataResponse tableData) {
		if(BuildConfig.DEBUG) {
			if(mRestaurant == null || TextUtils.isEmpty(mRestaurant.id())) {
				throw new RuntimeException("Restaurant cannot be null and shall have non-empty id");
			}
		}
		return mRestaurant.id().equals(tableData.getRestaurantId());
	}

	private void onErrorQrCheck(final int number) {
		mLoader.stopProgressAnimation(true);
        DialogUtils.showDialog(getActivity(), getString(R.string.qr_already_bound, number), R.string.proceed,
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
        DialogUtils.showDialog(getActivity(), getString(R.string.beacon_already_bound, number), R.string.proceed,
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
			BeaconFilter mFilter = new BeaconFilter(LinkerApplication.get(getActivity()));

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
		ViewTreeObserver.OnGlobalLayoutListener listener =
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						if(isVisible) {
							AnimationUtils.translateUp(getActivity(), Collections.singletonList((View) mBtnBindTable),
							                           btnTranslation, null);

							mLoader.translateUp(null, mLoaderTranslation);
						} else {
							AnimationUtils
									.translateDown(getActivity(), Collections.singletonList((View) mBtnBindTable),
									               btnTranslation, null);
							mLoader.translateDown(null, mLoaderTranslation);
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
	public void onGattEvent(final GattEvent event) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
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
				} else if(BluetoothLeService.ACTION_BEACON_WRITE_FAILED.equals(event.getAction())) {
					gattConnected = false;
					mErrorHelper.showError(LinkerLoaderError.NO_CONNECTION_BIND, mInternetErrorClickListener);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLoader.onDestroy();
		OmnomObservable.unsubscribe(mErrValidationSubscription);
		OmnomObservable.unsubscribe(mApiBindingSubscription);
		OmnomObservable.unsubscribe(mErrBindSubscription);
		OmnomObservable.unsubscribe(mBuildBeaconsSubscribtion);
		OmnomObservable.unsubscribe(mFindBeaconSubscription);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_bind;
	}

	@DebugLog
	public void writeBeaconData() {
		mBluetoothLeService
				.queueCharacteristic(CharacteristicHolder.createPassword(getString(R.string.redbear_beacon_password).getBytes()));
		final byte txValue = (byte) Integer.parseInt(getString(R.string.redbear_beacon_tx));
		final byte batteryValue = (byte) Integer.parseInt(getString(R.string.redbear_battery_broadcast_enabled));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createTx(new byte[]{txValue}));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createUuid(mBeaconData.getUuid()));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createMajorId(mBeaconData.getMajor()));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createMinorId(mBeaconData.getMinor()));
		mBluetoothLeService.queueCharacteristic(CharacteristicHolder.createBattery(new byte[]{batteryValue}));
		mBluetoothLeService.startWritingQueue(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mApiBindingSubscription = AppObservable.bindActivity(getActivity(), api.bind(mRestaurant.id(),
						                                                                                 mLoader.getTableNumber(),
						                                                                                 mQrData, mBeaconData, mBeacon))
						                                           .subscribe(new Action1<ResponseBase>() {
							                                           @Override
							                                           public void call(ResponseBase responseBase) {
								                                           if(!TextUtils.isEmpty(responseBase.getError())) {
									                                           throw RetrofitError.unexpectedError(StringUtils
											                                                                               .EMPTY_STRING,
									                                                                               new
											                                                                               ServiceConfigurationError(
											                                                                               "There is a " +
													                                                                               "problem on server-side"));
								                                           }
							                                           }
						                                           }, new LinkerBaseErrorHandler(BindActivity.this) {
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
								                                           mErrorHelper.showError(LinkerLoaderError.NO_CONNECTION_BIND,
								                                                                  mInternetErrorClickListener);
								                                           beaconTimeoutCallback.run();
								                                           resetActivityState();
							                                           }
						                                           }, new Action0() {
							                                           @Override
							                                           public void call() {
								                                           mApiBindComplete = true;
								                                           if(mApiBindComplete && gattConnected && gattAvailable) {
									                                           mLoader.stopProgressAnimation();
								                                           }
								                                           beaconTimeoutCallback.run();
							                                           }
						                                           });
					}
				});
			}
		});
	}

}
