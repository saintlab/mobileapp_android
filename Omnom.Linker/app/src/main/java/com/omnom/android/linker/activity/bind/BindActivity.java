package com.omnom.android.linker.activity.bind;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.base.ValidationObservable;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.service.BluetoothLeService;
import com.omnom.android.linker.service.DataHolder;
import com.omnom.android.linker.service.RBLBluetoothAttributes;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderController;
import com.omnom.android.linker.widget.loader.LoaderView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import altbeacon.beacon.Identifier;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 14.08.2014.
 */
public class BindActivity extends BaseActivity {

	private static final String TAG = BindActivity.class.getSimpleName();
	private static final int REQUEST_CODE_SCAN_QR = 101;
	private static final long BLE_SCAN_PERIOD = 2000;

	public static void start(final Context context, Restaurant restaurant, final boolean showBack) {
		final Intent intent = new Intent(context, BindActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_SHOW_BACK, showBack);
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	private final BeaconParser parser = new BeaconParser();
	private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		@DebugLog
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					parser.setBeaconLayout(RBLBluetoothAttributes.REDBEAR_BEACON_LAYOUT);
					final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);

					if(beacon != null && beacon.getId1() != null) {
						Identifier id1 = beacon.getId1();
						final String beaconId = id1.toString().toLowerCase();
						if(RBLBluetoothAttributes.BEACON_ID.equals(beaconId)) {
							mBeacons.add(beacon);
						}
					}
				}
			});
		}
	};
	public volatile boolean gattConnected = false;
	public volatile boolean gattAvailable = false;

	@InjectView(R.id.loader)
	protected LoaderView mLoader;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	final Runnable beaconInteractionCallback = new Runnable() {
		@Override
		public void run() {
			if(!gattAvailable || !gattConnected) {
				onGattFailed();
			} else {
				mLoader.animateLogo(R.drawable.ic_camera_white);
				AnimationUtils.animateAlpha(mPanelBottom, true);
				mPanelBottom.setVisibility(View.VISIBLE);
			}
			mBluetoothLeService.disconnect();
			mBluetoothLeService.close();
		}
	};

	@InjectView(R.id.btn_bottom)
	protected Button mBtnBottom;

	@InjectView(R.id.btn_bind_table)
	protected Button mBtnBindTable;

	@InjectView(R.id.btn_back)
	protected View mBtnBack;

	@InjectView(R.id.txt_error)
	protected TextView mTxtError;

	@InjectViews({R.id.txt_error, R.id.panel_bottom})
	protected List<View> errorViews;

	@Inject
	protected LinkerObeservableApi api;

	protected BluetoothLeService mBluetoothLeService;
	private BroadcastReceiver gattConnectedReceiver = new GattBroadcastReceiver(this);
	private boolean mGattReceiverRegistered = false;
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
	private CountDownTimer cdt;
	private BluetoothAdapter mBluetoothAdapter;
	private int mLoaderTranslation;
	private String mQrData = StringUtils.EMPTY_STRING;

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
			}, BLE_SCAN_PERIOD);
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

	@OnClick(R.id.btn_bind_table)
	public void onBind() {
		AndroidUtils.hideKeyboard(findById(this, R.id.edit_table_number));
		AnimationUtils.animateAlpha(mBtnBindTable, false);
		api.bindBeacon(mRestaurant.getId(), mBeacon).subscribe(new Action1<Integer>() {
			@Override
			public void call(Integer integer) {
				api.bindQrCode(mRestaurant.getId(), mQrData).subscribe(new Action1<Integer>() {
					@Override
					public void call(Integer integer) {
						mLoaderController.setMode(LoaderView.Mode.NONE);
						connectToBeacon();
					}
				});
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
		initCountDownTimer(beaconInteractionCallback);
		cdt.start();
		ValidationObservable.validate(this, new Action1<Boolean>() {
			@Override
			public void call(Boolean valid) {
				if(valid) {
					if(!mBluetoothLeService.connect(mBeacon.getBluetoothAddress())) {
						cdt.cancel();
					}
				}
			}
		});
	}

	@Override
	public void finish() {
		mLoaderController.setMode(LoaderView.Mode.NONE);
		AnimationUtils.animateAlpha(mBtnBack, false);
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

	@DebugLog
	private void onRestaurantLoaded(Restaurant restaurant) {
		if(getIntent().getBooleanExtra(EXTRA_SHOW_BACK, false)) {
			mLoader.animateColor(Color.WHITE, getResources().getColor(R.color.loader_bg), AnimationUtils.DURATION_LONG);
			mLoader.setLogo(R.drawable.ic_mexico_logo);
			AnimationUtils.animateAlpha(mPanelBottom, true);
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
			mLoader.animateLogo(R.drawable.ic_mexico_logo);
			mLoader.postDelayed(new Runnable() {
				@Override
				public void run() {
					AnimationUtils.animateAlpha(mPanelBottom, true);
				}
			}, AnimationUtils.DURATION_LONG);
		}
		mRestaurant = restaurant;
		mBtnBottom.setText(R.string.bind_table);
		mBtnBottom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bindTable();
			}
		});
	}

	@DebugLog
	private void scanQrCode() {
		startActivityForResult(new Intent(this, CaptureActivity.class), REQUEST_CODE_SCAN_QR);
	}

	@DebugLog
	private void clearErrors() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
	}

	@Override
	@DebugLog
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_CODE_SCAN_QR) {
				mPanelBottom.setVisibility(View.GONE);
				mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				api.checkQrCode(mRestaurant.getId(), mQrData).subscribe(new Action1<Integer>() {
					@Override
					public void call(Integer result) {
						mLoaderController.setMode(LoaderView.Mode.ENTER_DATA);
						AnimationUtils.animateAlpha(mBtnBindTable, true);
					}
				});
			}
		}
	}

	private void initCountDownTimer(final Runnable runnable) {
		final int progressMax = getResources().getInteger(R.integer.loader_progress_max);
		final int timeMax = getResources().getInteger(R.integer.loader_time_max);
		final int tick = getResources().getInteger(R.integer.loader_tick_interval);
		final int ticksCount = timeMax / tick;
		final int magic = progressMax / ticksCount;
		mLoader.updateProgress(0);

		cdt = new CountDownTimer(timeMax, tick) {
			@Override
			public void onTick(long millisUntilFinished) {
				mLoader.addProgress(magic * 2);
			}

			@Override
			public void onFinish() {
				mLoader.updateProgress(progressMax);
				if(runnable != null) {
					runnable.run();
				}
			}
		};
	}

	private void showError(int logoResId, int errTextResId, int btnTextResId, View.OnClickListener onClickListener) {
		mLoader.updateProgress(0);
		mLoader.showProgress(false);
		cdt.cancel();
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, true);
		mLoader.animateLogo(logoResId);
		mTxtError.setText(errTextResId);
		mBtnBottom.setText(btnTextResId);
		mBtnBottom.setOnClickListener(onClickListener);
	}

	private void bindTable() {
		mLoader.animateColorDefault();
		mLoader.animateLogo(R.drawable.ic_mexico_logo);
		initCountDownTimer(new Runnable() {
			@Override
			public void run() {
				scanQrCode();
			}
		});
		clearErrors();
		cdt.start();

		ValidationObservable.validate(this, new Action1<Boolean>() {
			@Override
			public void call(Boolean valid) {
				if(valid) {
					scanBleDevices(true, new Runnable() {
						@Override
						public void run() {
							final int size = mBeacons.size();
							if(size == 0) {
								showError(R.drawable.ic_weak_signal, R.string.error_weak_beacon_signal, R.string.try_once_again,
								          new View.OnClickListener() {
									          @Override
									          public void onClick(View v) {
										          bindTable();
									          }
								          });
							} else if(size > 1) {
								showError(R.drawable.ic_weak_signal, R.string.error_more_than_one_beacon, R.string.try_once_again,
								          new View.OnClickListener() {
									          @Override
									          public void onClick(View v) {
										          bindTable();
									          }
								          });
							} else if(size == 1) {
								mBeacon = (Beacon) mBeacons.toArray()[0];
								api.checkBeacon(mRestaurant.getId(), mBeacon).subscribe(new Action1<Integer>() {
									@Override
									public void call(final Integer integer) {
									}
								});
							}
						}
					});
				}
			}
		});
	}

	private void onQrBindError() {
		AndroidUtils.showDialog(getActivity(), R.string.beacon_already_bound, R.string.proceed, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				scanQrCode();
			}
		}, R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
	}

	@Override
	public void initUi() {
		mLoaderController = new LoaderController(this, mLoader);
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mLoaderTranslation = ViewUtils.dipToPixels(this, 48);
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
	protected void onPostResume() {
		super.onPostResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		filter.addAction(BluetoothLeService.ACTION_GATT_FAILED);
		filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		filter.addAction(BluetoothLeService.ACTION_CHARACTERISTIC_UPDATE);
		registerReceiver(gattConnectedReceiver, filter);
		mGattReceiverRegistered = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mGattReceiverRegistered) {
			unregisterReceiver(gattConnectedReceiver);
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_bind;
	}

	@DebugLog
	public void writeBeaconData() {
		mBluetoothLeService.queueCharacteristic(new DataHolder(RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD_SERVICE,
		                                                       RBLBluetoothAttributes.UUID_BLE_REDBEAR_PASSWORD,
		                                                       RBLBluetoothAttributes.RBL_PASSKEY.getBytes()));

		mBluetoothLeService.queueCharacteristic(new DataHolder(RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                                                       RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_MAJOR_ID, 312));

		mBluetoothLeService.queueCharacteristic(new DataHolder(RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_SERVICE,
		                                                       RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_MINOR_ID, 256));

		mBluetoothLeService.startWritingQueue(new Runnable() {
			@Override
			public void run() {
				api.commitBeacon(mRestaurant.getId(), mBeacon).subscribe(new Action1<Integer>() {
					@Override
					public void call(Integer integer) {
						mLoader.jumpProgress(1);
					}
				});
			}
		});
	}

	public void updateBeaconData(final UUID cUuid, final byte[] cValue) {
		if(BuildConfig.DEBUG) {
			if(mBeacon == null) {
				throw new AssertionError("Beacon cannot be null");
			}
		}
		if(cUuid.equals(RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_MAJOR_ID)) {
			mBeacon.getId2().updateValue(cValue);
		}
		if(cUuid.equals(RBLBluetoothAttributes.UUID_BLE_REDBEAR_BEACON_MINOR_ID)) {
			mBeacon.getId3().updateValue(cValue);
		}
	}

	public void onGattFailed() {
		showError(R.drawable.ic_weak_signal, R.string.error_weak_beacon_signal, R.string.try_once_again, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bindTable();
			}
		});
	}
}
