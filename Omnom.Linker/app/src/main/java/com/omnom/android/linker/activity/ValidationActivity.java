package com.omnom.android.linker.activity;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.model.RestaurantsResult;
import com.omnom.android.linker.service.BluetoothLeService;
import com.omnom.android.linker.service.RBLBluetoothAttributes;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderView;

import org.apache.http.auth.AuthenticationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import altbeacon.beacon.Beacon;
import altbeacon.beacon.BeaconParser;
import altbeacon.beacon.Identifier;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.linker.utils.AndroidUtils.showToast;

public class ValidationActivity extends BaseActivity /*implements Observer<String>*/ {

	public static final  String EXTRA_RESTAURANT = "com.omnom.android.linker.restaurant";
	private static final String TAG              = ValidationActivity.class.getSimpleName();

	private static final int  REQUEST_ENABLE_BT = 100;
	private static final long BLE_SCAN_PERIOD   = 2000;

	public static void start(final Context context, Restaurant restaurant) {
		final Intent intent = new Intent(context, ValidationActivity.class);
		intent.putExtra(ValidationActivity.EXTRA_RESTAURANT, restaurant);
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.btn_bottom)
	protected Button btnSettings;

	@InjectViews({R.id.txt_error, R.id.panel_bottom})
	protected List<View> errorViews;

	@InjectView(R.id.panel_bottom)
	protected View panelBottom;

	@Inject
	protected LinkerObeservableApi api;

	protected BluetoothLeService mBluetoothLeService;
	private   CountDownTimer     cdt;
	private BroadcastReceiver gattConnectedReceiver = new GattBroadcastReceiver(this);

	private boolean mBound;
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
	private BluetoothAdapter mBluetoothAdapter;
	private Set<Beacon> beacons = new HashSet<Beacon>();

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final BeaconParser parser = new BeaconParser();
					parser.setBeaconLayout(RBLBluetoothAttributes.REDBEAR_BEACON_LAYOUT);
					final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
					if(beacon != null && beacon.getId1() != null) {
						Identifier id1 = beacon.getId1();
						final String beaconId = id1.toString().toLowerCase();
						if(RBLBluetoothAttributes.BEACON_ID.equals(beaconId)) {
							beacons.add(beacon);
						}
					}
				}
			});
		}
	};

	private boolean           mGattReceiverRegistered = false;
	private String            mUsername               = null;
	private String            mPassword               = null;
	private RestaurantsResult restaurants             = null;
	private Restaurant        mRestaurant             = null;

	@Override
	public void initUi() {

		mUsername = getIntent().getStringExtra(LoginActivity.EXTRA_USERNAME);
		mPassword = getIntent().getStringExtra(LoginActivity.EXTRA_PASSWORD);

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	private boolean checkBluetoothEnabled() {
		return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
	}

	private void scanBleDevices(final boolean enable, final Runnable endCallback) {
		if(enable) {
			beacons.clear();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_ENABLE_BT) {
			// TODO:
			//			if(mBtEnabled) {
			//				scanBleDevices(true);
			//			}
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validation;
	}

	public void onProgress(int progress) {
		if(progress == 0 || progress >= getResources().getInteger(R.integer.loader_progress_max)) {
			loader.showProgress(false);
		} else {
			loader.showProgress(true);
		}
		loader.updateProgress(progress);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mGattReceiverRegistered) {
			unregisterReceiver(gattConnectedReceiver);
		}
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

		IntentFilter filter = new IntentFilter(BluetoothLeService.ACTION_GATT_CONNECTED);
		filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		registerReceiver(gattConnectedReceiver, filter);
		mGattReceiverRegistered = true;

		validate();
	}

	@OnClick(R.id.btn_back)
	public void onBack() {
		onBackPressed();
	}

	@Override
	public void finish() {
		loader.animateColor(Color.WHITE);
		loader.scaleUp(new LoaderView.Callback() {
			@Override
			public void execute() {
				ValidationActivity.super.finish();
				overridePendingTransition(android.R.anim.fade_in, R.anim.fake_fade_out);
			}
		});
	}

	private void validate() {
		if(getIntent().hasExtra(EXTRA_RESTAURANT)) {
			mRestaurant = getIntent().getParcelableExtra(EXTRA_RESTAURANT);
			loader.setLogo(R.drawable.ic_mexico_logo);
			onRestaurantLoaded(mRestaurant);
			ViewUtils.setVisible(panelBottom, false);
			loader.post(new Runnable() {
				@Override
				public void run() {
					loader.animateColor(Color.WHITE, getResources().getColor(R.color.loader_bg), AnimationUtils.DURATION_LONG);
					loader.scaleDown(null, new AnimationBuilder.Action() {
						@Override
						public void invoke() {
							AnimationUtils.animateAlpha(findById(ValidationActivity.this, R.id.panel_bottom), true);
							AnimationUtils.animateAlpha(findById(ValidationActivity.this, R.id.btn_back), true);
						}
					});
				}
			});
			return;
		}

		loader.animateColor(getResources().getColor(R.color.loader_bg));
		loader.animateLogo(R.drawable.ic_fork_n_knife);
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		initCountDownTimer();
		loader.showProgress(false);
		loader.scaleDown(null, new AnimationBuilder.Action() {
			@Override
			public void invoke() {
				startLoader();
			}
		});
	}

	private void startLoader() {
		cdt.start();

		if(!AndroidUtils.hasConnection(ValidationActivity.this)) {
			showInternetError();
			return;
		}

		if(!AndroidUtils.isLocationEnabled(ValidationActivity.this)) {
			showLocationError();
			return;
		}
		if(!checkBluetoothEnabled()) {
			showErrorBluetoothDisabled();
			return;
		}

		api.authenticate(mUsername, mPassword).map(new Func1<String, Observable<RestaurantsResult>>() {
			@Override
			public Observable<RestaurantsResult> call(String s) {
				api.setAuthToken(s);
				return api.getRestaurants();
			}
		}).onErrorReturn(new Func1<Throwable, Observable<RestaurantsResult>>() {
			@Override
			public Observable<RestaurantsResult> call(Throwable throwable) {
				cdt.cancel();
				loader.showProgress(false);
				showToast(ValidationActivity.this, R.string.msg_error);
				Log.e(TAG, "validate()", throwable);
				if(throwable instanceof AuthenticationException) {
					onAuthError(throwable);
				}
				return Observable.empty();
			}
		}).subscribe(new Action1<Observable<RestaurantsResult>>() {
			@Override
			public void call(Observable<RestaurantsResult> restaurantsResultObservable) {
				restaurantsResultObservable.subscribe(new Observer<RestaurantsResult>() {
					@Override
					public void onCompleted() {
					}

					@Override
					public void onError(Throwable e) {
						cdt.cancel();
						Log.e(TAG, "getRestaurants()", e);
					}

					@Override
					public void onNext(RestaurantsResult restaurantsResult) {
						restaurants = restaurantsResult;
					}
				});
			}
		});
	}

	private void initCountDownTimer() {
		final int progressMax = getResources().getInteger(R.integer.loader_progress_max);
		final int timeMax = getResources().getInteger(R.integer.loader_time_max);
		final int tick = getResources().getInteger(R.integer.loader_tick_interval);
		final int ticksCount = timeMax / tick;
		final int magic = progressMax / ticksCount;

		cdt = new CountDownTimer(timeMax, tick) {
			int j = 0;

			@Override
			public void onTick(long millisUntilFinished) {
				j += magic * 2;
				onProgress(j);
			}

			@Override
			public void onFinish() {
				onProgress(progressMax);
				if(restaurants == null) {
					// TODO: error happened
					return;
				}
				final List<Restaurant> items = restaurants.getItems();
				int size = items.size();
				if(items.isEmpty()) {
					return;
				}

				if(size == 1) {
					onRestaurantLoaded(items.get(0));
					ViewUtils.setVisible(findById(ValidationActivity.this, R.id.btn_back), false);
				} else {
					loader.animateColor(Color.WHITE, AnimationUtils.DURATION_LONG);
					loader.scaleUp(new LoaderView.Callback() {
						@Override
						public void execute() {
							ViewUtils.setVisible(panelBottom, false);
							RestaurantsListActivity.start(ValidationActivity.this, items);
							finish();
						}
					});
				}

			}
		};
	}

	private void onRestaurantLoaded(Restaurant restaurant) {
		mRestaurant = restaurant;
		AnimationUtils.animateAlpha(panelBottom, true);
		btnSettings.setText(R.string.bind_table);
		btnSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bindTable();
			}
		});
	}

	private void bindTable() {
		loader.animateColorDefault();
		loader.animateLogo(R.drawable.ic_mexico_logo);
		initCountDownTimer();
		clearErrors();
		cdt.start();
		scanBleDevices(true, new Runnable() {
			@Override
			public void run() {
				if(beacons.size() == 0) {
					showError(R.drawable.ic_weak_signal, R.string.error_weak_beacon_signal, R.string.try_once_again,
					          new View.OnClickListener() {
						          @Override
						          public void onClick(View v) {
							          bindTable();
						          }
					          });
				} else if(beacons.size() > 1) {
					showError(R.drawable.ic_weak_signal, R.string.error_more_than_one_beacon, R.string.try_once_again,
					          new View.OnClickListener() {
						          @Override
						          public void onClick(View v) {
							          bindTable();
						          }
					          });
				} else if(beacons.size() == 1) {
					startActivity(new Intent(ValidationActivity.this, CaptureActivity.class));
				}
			}
		});
	}

	private void clearErrors() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
	}

	private void onAuthError(Throwable e) {
		final Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra(LoginActivity.EXTRA_ERROR_CODE, LoginActivity.EXTRA_ERROR_WRONG_USERNAME);
		intent.putExtra(LoginActivity.EXTRA_USERNAME, mUsername);
		intent.putExtra(LoginActivity.EXTRA_PASSWORD, mPassword);
		startActivity(intent);
		finish();
	}

	private void showError(int logoResId, int errTextResId, int btnTextResId, View.OnClickListener onClickListener) {
		loader.showProgress(false);
		cdt.cancel();
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, true);
		loader.animateLogo(logoResId);
		txtError.setText(errTextResId);
		btnSettings.setText(btnTextResId);
		btnSettings.setOnClickListener(onClickListener);
	}

	private void showInternetError() {
		showError(R.drawable.ic_no_connection, R.string.error_you_have_no_internet_connection, R.string.try_once_again,
		          new View.OnClickListener() {
			          @Override
			          public void onClick(View v) {
				          validate();
			          }
		          });
	}

	private void showErrorBluetoothDisabled() {
		showError(R.drawable.ic_bluetooth_white, R.string.error_bluetooth_disabled, R.string.open_settings, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loader.scaleDown(null);
				ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
				startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
			}
		});
	}

	private void showLocationError() {
		showError(R.drawable.ic_geolocation_white, R.string.error_location_disabled, R.string.open_settings, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loader.scaleDown(null);
				ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
				AndroidUtils.startLocationSettings(v.getContext());
			}
		});
	}

	/*@Override
	public void onCompleted() {
		if(!AndroidUtils.hasConnection(ValidationActivity.this)) {
			showInternetError();
			return;
		}
		if(!AndroidUtils.isLocationEnabled(ValidationActivity.this)) {
			showLocationError();
			return;
		}
		if(!checkBluetoothEnabled()) {
			showErrorBluetoothDisabled();
			return;
		}
		loader.animateLogo(R.drawable.ic_fork_n_knife);
	}

	@Override
	public void onError(Throwable e) {
		onAuthError(e);
		cdt.cancel();
		Log.e(TAG, "authenticate()", e);
	}

	@Override
	public void onNext(String result) {
		if(!TextUtils.isEmpty(result)) {
			api.setAuthToken(result);
			api.getRestaurants().subscribe();
		}
	}*/
}
