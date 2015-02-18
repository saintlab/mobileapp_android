package com.omnom.android.activity;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.PreferencesActivity;
import com.google.zxing.client.android.camera.FrontLightMode;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.fragment.QrHintFragment;
import com.omnom.android.menu.api.observable.MenuObservableApi;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.MenuResponse;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.decode.HashDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by Ch3D on 14.11.2014.
 */
public class OmnomQRCaptureActivity extends CaptureActivity implements QrHintFragment.FragmentCloseListener {

	public static final int RESULT_RESTAURANT_FOUND = 2;

	private static final String TAG = OmnomQRCaptureActivity.class.getSimpleName();

	private static final int LAUNCH_DELAY = 2000;

	private static final int SCAN_DELAY = 5000;

	private class LaunchAnimationListener implements Animator.AnimatorListener {

		private final View background;

		public LaunchAnimationListener(final View background) {
			this.background = background;
		}

		@Override
		public void onAnimationStart(Animator animation) {

		}

		@Override
		public void onAnimationEnd(Animator animation) {
			ViewUtils.setVisible(background, false);
		}

		@Override
		public void onAnimationCancel(Animator animation) {

		}

		@Override
		public void onAnimationRepeat(Animator animation) {

		}
	}

	public static void start(final BaseOmnomActivity activity, final int code) {
		final Intent intent = getIntent(activity);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			final ActivityOptions activityOptions = ActivityOptions
					.makeCustomAnimation(activity, com.omnom.android.zxing.R.anim.slide_in_right,
					                     com.omnom.android.zxing.R.anim.slide_out_left);
			activity.startActivityForResult(intent, code, activityOptions.toBundle());
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	private static Intent getIntent(final Context context) {
		Intent intent = new Intent(context, OmnomQRCaptureActivity.class);
		intent.setAction(Intents.Scan.ACTION);
		intent.putExtra(Intents.Scan.FORMATS, BarcodeFormat.QR_CODE.name());
		intent.putExtra(Intents.Scan.SAVE_HISTORY, false);
		return intent;
	}

	public static void start(final BaseOmnomFragmentActivity activity, final int code) {
		final Intent intent = getIntent(activity);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			final ActivityOptions activityOptions = ActivityOptions
					.makeCustomAnimation(activity, com.omnom.android.zxing.R.anim.slide_in_right,
					                     com.omnom.android.zxing.R.anim.slide_out_left);
			activity.startActivityForResult(intent, code, activityOptions.toBundle());
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	@InjectView(R.id.btn_not_scanning)
	protected View btnNotScanning;

	@InjectView(R.id.scan_frame_container)
	protected View scanFrameContainer;

	@InjectView(R.id.scan_frame)
	protected View scanFrame;

	@InjectView(R.id.panel_enter_hash)
	protected View panelEnterHash;

	@InjectView(R.id.edit_hash)
	protected EditText editHash;

	@InjectView(R.id.background)
	protected View background;

	@InjectView(R.id.txt_enter_hash)
	protected TextView txtEnterHash;

	@InjectView(R.id.hash_underline)
	protected TextView hashUnderline;

	@InjectView(R.id.btn_flash_light)
	protected ImageView btnFlashLight;

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected MenuObservableApi menuApi;

	protected Func1<RestaurantResponse, RestaurantResponse> mPreloadBackgroundFunction;

	private Subscription mCheckQrSubscription;

	private boolean isError = false;

	private boolean isBusy = false;

	private boolean isFlashTurnedOn = false;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PreferencesActivity.KEY_FRONT_LIGHT_MODE, FrontLightMode.AUTO.name());
		editor.apply();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_capture_qr;
	}

	@Override
	protected void initUI() {
		super.initUI();
		playLaunchAnimation();
		initPreloadBackgroundFunction();
		initEditHash();
		final TextView txtHint = (TextView) findViewById(R.id.txt_hint);
		AndroidUtils.clickify(txtHint, getString(R.string.navigate_qr_code_mark),
		                      new ClickSpan.OnClickListener() {
			                      @Override
			                      public void onClick() {
				                      showHint();
			                      }
		                      });

	}

	private void initEditHash() {
		editHash.post(new Runnable() {
			@Override
			public void run() {
				editHash.setSelection(editHash.getText().toString().length());
			}
		});
		editHash.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					if(!isBusy() && !editHash.getText().toString().isEmpty()) {
						setBusy(true);
						editHash.setTextColor(getResources().getColor(R.color.enter_hash_color));
						loadTable(editHash.getText().toString());
					}
					return true;
				}
				return false;
			}
		});
		editHash.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if(isBusy()) {
					editHash.removeTextChangedListener(this);
					editHash.setText(s);
					editHash.setSelection(s.length());
					editHash.addTextChangedListener(this);
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(isError) {
					onHashChange();
				}
			}
		});
	}

	private void initPreloadBackgroundFunction() {
		mPreloadBackgroundFunction = new Func1<RestaurantResponse, RestaurantResponse>() {
			@Override
			public RestaurantResponse call(final RestaurantResponse decodeResponse) {
				final List<Restaurant> restaurants = decodeResponse.getRestaurants();
				if(restaurants.size() == 1) {
					final Restaurant restaurant = restaurants.get(0);
					if(restaurant != null) {
						final String bgImgUrl = RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics());
						if(!TextUtils.isEmpty(bgImgUrl)) {
							try {
								OmnomApplication.getPicasso(getActivity()).load(bgImgUrl).get();
							} catch(IOException e) {
								Log.e(TAG, "unable to load img = " + bgImgUrl);
							}
						}
					}
				}
				return decodeResponse;
			}
		};
	}

	private void playLaunchAnimation() {
		final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		scanFrameContainer.setTranslationY(-displayMetrics.heightPixels);
		postDelayed(LAUNCH_DELAY, new Runnable() {
			@Override
			public void run() {
				final int duration = getResources().getInteger(R.integer.default_animation_duration_medium);
				scanFrameContainer.animate()
				                  .translationYBy(displayMetrics.heightPixels)
				                  .setDuration(duration)
				                  .start();
				background.animate()
				          .translationYBy(displayMetrics.heightPixels)
				          .setDuration(duration)
				          .setListener(new LaunchAnimationListener(background))
				          .start();

				launchScanningDelayHandler(btnNotScanning);
			}
		});

		ViewTreeObserver viewTreeObserver = scanFrame.getViewTreeObserver();
		if(viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					AndroidUtils.removeOnGlobalLayoutListener(scanFrame, this);
					final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
					setFramingRect(new Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels));
					btnNotScanning.setTranslationY(btnNotScanning.getHeight());
					ViewUtils.setVisible(btnNotScanning, false);
				}
			});
		}
	}

	private void launchScanningDelayHandler(final View btnNotScanning) {
		postDelayed(SCAN_DELAY, new Runnable() {
			@Override
			public void run() {
				setNotScanningButtonVisible(true);
			}
		});
	}

	@OnClick(R.id.btn_not_scanning)
	protected void onNotScanning() {
		setNotScanningButtonVisible(false);
		ViewUtils.setVisible(panelEnterHash, true);
		AndroidUtils.showKeyboard(editHash);
	}

	@OnClick(R.id.btn_close)
	protected void onBtnClose() {
		closeHashEnterPanel();
	}

	@OnClick(R.id.btn_flash_light)
	protected void onBtnFlash() {
		isFlashTurnedOn = !isFlashTurnedOn;
		btnFlashLight.setImageResource(isFlashTurnedOn ? R.drawable.ic_flashlight_off :
				                               R.drawable.ic_flashlight_on);
		setTorch(isFlashTurnedOn);
	}

	private void showHint() {
		setNotScanningButtonVisible(false);
		getSupportFragmentManager().beginTransaction()
		                           .addToBackStack(null)
		                           .setCustomAnimations(R.anim.slide_in_up,
		                                                R.anim.slide_out_down,
		                                                R.anim.slide_in_up,
		                                                R.anim.slide_out_down)
		                           .replace(R.id.fragment_container, QrHintFragment.newInstance())
		                           .commit();
	}

	private void setNotScanningButtonVisible(final boolean isVisible) {
		if(ViewUtils.isVisible(btnNotScanning) == isVisible) {
			return;
		}
		if(isVisible) {
			ViewUtils.setVisible(btnNotScanning, true);
		}
		final int duration = getResources().getInteger(R.integer.not_scanning_animation_duration);
		btnNotScanning.animate()
		              .translationYBy(btnNotScanning.getHeight() * (isVisible ? -1 : 1))
		              .setDuration(duration)
		              .setListener(new Animator.AnimatorListener() {
			              @Override
			              public void onAnimationStart(Animator animation) {

			              }

			              @Override
			              public void onAnimationEnd(Animator animation) {
				              if(!isVisible) {
					              ViewUtils.setVisible(btnNotScanning, false);
				              }
			              }

			              @Override
			              public void onAnimationCancel(Animator animation) {

			              }

			              @Override
			              public void onAnimationRepeat(Animator animation) {

			              }
		              })
		              .start();
	}

	@Override
	public void onFragmentClose() {
		launchScanningDelayHandler(btnNotScanning);
	}

	@Override
	protected void onStop() {
		super.onStop();
		closeHashEnterPanel();
		setBusy(false);
	}

	private void closeHashEnterPanel() {
		editHash.setText(StringUtils.EMPTY_STRING);
		onHashChange();
		ViewUtils.setVisible(panelEnterHash, false);
		AndroidUtils.hideKeyboard(editHash, new ResultReceiver(new Handler()));
		launchScanningDelayHandler(btnNotScanning);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCheckQrSubscription);
	}

	private void loadTable(final String hash) {
		final Observable<RestaurantResponse> decodeObservable = api.decode(new HashDecodeRequest(hash), mPreloadBackgroundFunction);

		final Observable<Pair<RestaurantResponse, MenuResponse>> restMenuObservable = decodeObservable.mergeMap(
				new Func1<RestaurantResponse, Observable<MenuResponse>>() {
					@Override
					public Observable<MenuResponse> call(final RestaurantResponse restaurantResponse) {
						if(restaurantResponse.hasOnlyRestaurant()) {
							final Restaurant restaurant = restaurantResponse.getRestaurants().get(0);
							return menuApi.getMenu(restaurant.id());
						}
						return Observable.empty();
					}
				}, new Func2<RestaurantResponse, MenuResponse, Pair<RestaurantResponse, MenuResponse>>() {
					@Override
					public Pair<RestaurantResponse, MenuResponse> call(final RestaurantResponse restaurant, final MenuResponse menu) {
						return Pair.create(restaurant, menu);
					}
				});

		mCheckQrSubscription = AndroidObservable
				.bindActivity(this, restMenuObservable).subscribe(
						new Action1<Pair<RestaurantResponse, MenuResponse>>() {
							@Override
							public void call(final Pair<RestaurantResponse, MenuResponse> restMenuResponse) {
								final RestaurantResponse decodeResponse = restMenuResponse.first;

								if(decodeResponse.hasAuthError()) {
									throw new AuthServiceException(EXTRA_ERROR_WRONG_USERNAME | EXTRA_ERROR_WRONG_PASSWORD,
									                               new AuthError(EXTRA_ERROR_AUTHTOKEN_EXPIRED,
									                                             decodeResponse.getError()));
								}
								if(!TextUtils.isEmpty(decodeResponse.getError())) {
									showError(getString(R.string.error_unknown_hash));
								} else if(decodeResponse.hasOnlyRestaurant()) {
									editHash.setTextColor(getResources().getColor(android.R.color.black));
									Restaurant restaurant = decodeResponse.getRestaurants().get(0);

									Menu menu = null;
									if(restMenuResponse.second != null) {
										menu = restMenuResponse.second.getMenu();
									}

									finish(decodeResponse.getRequestId(), restaurant, menu);
								} else {
									showError(getString(R.string.error_unknown_hash));
								}
							}
						}, new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								showError(getString(R.string.something_went_wrong));
							}
						});
	}

	private void onHashChange() {
		isError = false;
		txtEnterHash.setText(getString(R.string.enter_hash));
		GradientDrawable drawable = (GradientDrawable) hashUnderline.getBackground();
		drawable.setColor(getResources().getColor(R.color.enter_hash_color));
		editHash.setTextColor(getResources().getColor(android.R.color.black));
		txtEnterHash.setTextColor(getResources().getColor(R.color.qr_hint_color));
	}

	private void showError(final String message) {
		setBusy(false);
		isError = true;
		txtEnterHash.setText(message);
		GradientDrawable drawable = (GradientDrawable) hashUnderline.getBackground();
		int color = getResources().getColor(R.color.cadre_border);
		drawable.setColor(color);
		editHash.setTextColor(color);
		txtEnterHash.setTextColor(color);
	}

	private boolean isBusy() {
		return isBusy;
	}

	private void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	private void finish(final String requestId, final Restaurant restaurant, Menu menu) {
		Intent data = new Intent();
		data.putExtra(EXTRA_REQUEST_ID, requestId);
		data.putExtra(EXTRA_RESTAURANT, restaurant);
		data.putExtra(EXTRA_RESTAURANT_MENU, menu);
		setResult(RESULT_RESTAURANT_FOUND, data);
		finish();
	}

}
