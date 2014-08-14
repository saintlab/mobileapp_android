package com.omnom.android.linker.activity;

import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.model.Restaurant;
import com.omnom.android.linker.service.RBLBluetoothAttributes;
import com.omnom.android.linker.utils.AnimationBuilder;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderController;
import com.omnom.android.linker.widget.loader.LoaderView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.linker.utils.AndroidUtils.showToast;

/**
 * Created by Ch3D on 14.08.2014.
 */
public class BindActivity extends BaseActivity {

	private static final int REQUEST_CODE_SCAN_QR = 101;
	private static final long BLE_SCAN_PERIOD = 2000;

	public static void start(final Context context, Restaurant restaurant, final boolean showBack) {
		final Intent intent = new Intent(context, BindActivity.class); intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_SHOW_BACK, showBack);
		context.startActivity(intent, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fake_fade_out).toBundle());
	}

	@InjectView(R.id.loader)
	protected LoaderView mLoader;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	@InjectView(R.id.btn_bottom)
	protected Button mBtnBottom;

	@InjectView(R.id.btn_back)
	protected View mBtnBack;

	@InjectView(R.id.txt_error)
	protected TextView mTxtError;

	@InjectViews({R.id.txt_error, R.id.panel_bottom})
	protected List<View> errorViews;

	@Inject
	protected LinkerObeservableApi api;

	@NotNull
	private Restaurant mRestaurant;

	@Nullable
	private Beacon mBeacon = null;

	private Set<Beacon> mBeacons = new HashSet<Beacon>();
	private LoaderController mLoaderController;
	private CountDownTimer cdt;

	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final BeaconParser parser = new BeaconParser();
					parser.setBeaconLayout(RBLBluetoothAttributes.REDBEAR_BEACON_LAYOUT);
					final Beacon beacon = parser.fromScanData(scanRecord, rssi, device);
					if (beacon != null && beacon.getId1() != null) {
						Identifier id1 = beacon.getId1();
						final String beaconId = id1.toString().toLowerCase();
						if (RBLBluetoothAttributes.BEACON_ID.equals(beaconId)) {
							mBeacons.add(beacon);
						}
					}
				}
			});
		}
	};
	private BluetoothAdapter mBluetoothAdapter;

	private void scanBleDevices(final boolean enable, final Runnable endCallback) {
		if (enable) {
			mBeacons.clear();
			findViewById(android.R.id.content).postDelayed(new Runnable() {
				@Override
				public void run() {
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					if (endCallback != null) {
						endCallback.run();
					}
				}
			}, BLE_SCAN_PERIOD);
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			if (endCallback != null) {
				endCallback.run();
			}
		}
	}

	@Override
	protected void handleIntent(Intent intent) {
		assert intent.hasExtra(EXTRA_RESTAURANT) && intent.getParcelableExtra(EXTRA_RESTAURANT) != null;
		onRestaurantLoaded((Restaurant) intent.getParcelableExtra(EXTRA_RESTAURANT));
		ViewUtils.setVisible(mBtnBack, intent.getBooleanExtra(EXTRA_SHOW_BACK, false));
	}

	@OnClick(R.id.btn_back)
	public void onBack() {
		onBackPressed();
	}

	private void onRestaurantLoaded(Restaurant restaurant) {
		mRestaurant = restaurant;
		mLoader.animateColor(Color.WHITE, getResources().getColor(R.color.loader_bg), AnimationUtils.DURATION_LONG);
		mLoader.setLogo(R.drawable.ic_mexico_logo);
		ViewUtils.setVisible(mPanelBottom, false);
		mLoader.post(new Runnable() {
			@Override
			public void run() {
				mLoader.scaleDown(null, new AnimationBuilder.Action() {
					@Override
					public void invoke() {
						AnimationUtils.animateAlpha(findById(getActivity(), R.id.panel_bottom), true);
						AnimationUtils.animateAlpha(findById(getActivity(), R.id.btn_back), true);
					}
				});
			}
		});

		AnimationUtils.animateAlpha(mPanelBottom, true);
		mBtnBottom.setText(R.string.bind_table);
		mBtnBottom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bindTable();
			}
		});
	}

	private void scanQrCode() {
		startActivityForResult(new Intent(this, CaptureActivity.class), REQUEST_CODE_SCAN_QR);
	}

	private void clearErrors() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SCAN_QR) {
				final String qrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				api.checkQrCode(mRestaurant.getId(), qrData).subscribe(new Action1<Integer>() {
					@Override
					public void call(Integer result) {
						api.bindQrCode(mRestaurant.getId(), qrData).subscribe(new Action1<Integer>() {
							@Override
							public void call(Integer integer) {
								showToast(getActivity(), R.string.qr_bound);
								api.bindBeacon(mRestaurant.getId(), mBeacon).subscribe(new Action1<Integer>() {
									@Override
									public void call(Integer integer) {
										showToast(getActivity(), R.string.beacon_bound);
										mLoaderController.setMode(LoaderView.Mode.ENTER_DATA);
									}
								});
							}
						});
					}
				});
			}
		}
	}

	private void initCountDownTimer() {
		final int progressMax = getResources().getInteger(R.integer.loader_progress_max);
		final int timeMax = getResources().getInteger(R.integer.loader_time_max);
		final int tick = getResources().getInteger(R.integer.loader_tick_interval);
		final int ticksCount = timeMax / tick;
		final int magic = progressMax / ticksCount;

		cdt = new CountDownTimer(timeMax, tick) {

			@Override
			public void onTick(long millisUntilFinished) {
				mLoader.addProgress(magic * 2);
			}

			@Override
			public void onFinish() {
				mLoader.updateProgress(progressMax);
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
		initCountDownTimer();
		clearErrors();
		cdt.start();
		scanBleDevices(true, new Runnable() {
			@Override
			public void run() {
				if (mBeacons.size() == 0) {
					showError(R.drawable.ic_weak_signal, R.string.error_weak_beacon_signal, R.string.try_once_again,
					          new View.OnClickListener() {
						          @Override
						          public void onClick(View v) {
							          bindTable();
						          }
					          });
				} else if (mBeacons.size() > 1) {
					showError(R.drawable.ic_weak_signal, R.string.error_more_than_one_beacon, R.string.try_once_again,
					          new View.OnClickListener() {
						          @Override
						          public void onClick(View v) {
							          bindTable();
						          }
					          });
				} else if (mBeacons.size() == 1) {
					mBeacon = (Beacon) mBeacons.toArray()[0];
					api.checkBeacon(mRestaurant.getId(), mBeacon).subscribe(new Action1<Integer>() {
						@Override
						public void call(final Integer integer) {
							scanQrCode();
							// TODO: error case
							//							AndroidUtils.showDialog(ValidationActivity.this, R.string.beacon_already_bound,
							// R.string.proceed,
							//							                        new DialogInterface.OnClickListener() {
							//								                        @Override
							//								                        public void onClick(DialogInterface dialog,
							// int which) {
							//									                        scanQrCode();
							//								                        }
							//							                        }, R.string.cancel, new DialogInterface.OnClickListener() {
							//										@Override
							//										public void onClick(DialogInterface dialog, int which) {
							//											finish();
							//										}
							//									});
						}
					});
				}
			}
		});
	}

	@Override
	public void initUi() {
		mLoaderController = new LoaderController(this, mLoader);

		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_bind;
	}
}
