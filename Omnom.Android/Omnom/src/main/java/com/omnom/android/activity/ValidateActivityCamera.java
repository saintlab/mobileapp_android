package com.omnom.android.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.restaurateur.api.Protocol;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantsResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.utils.utils.AndroidUtils.showToastLong;

public class ValidateActivityCamera extends BaseOmnomActivity {
	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.btn_bottom)
	protected Button btnSettings;

	@InjectViews({R.id.txt_error, R.id.panel_bottom})
	protected List<View> errorViews;

	@InjectView(R.id.panel_errors)
	protected View panelErrors;

	@InjectView(R.id.panel_bottom)
	protected View panelBottom;

	@InjectView(R.id.img_holder)
	protected View panelHolder;

	@Inject
	protected RestaurateurObeservableApi api;

	private BaseErrorHandler onError = new OmnomBaseErrorHandler(this) {
		@Override
		protected void onThrowable(Throwable throwable) {
			loader.stopProgressAnimation(true);
			if(throwable instanceof RetrofitError) {
				final RetrofitError cause = (RetrofitError) throwable;
				if(cause.getResponse() != null) {
					// TODO: Refactor this ugly piece of ... code
					if(cause.getUrl().contains(Protocol.FIELD_LOGIN) && cause.getResponse().getStatus() != 200) {
						// TODO:
						// LoginActivity.start(getActivity(), mDataHolder, EXTRA_ERROR_WRONG_USERNAME);
						return;
					}
				}
			}
			if(throwable instanceof AuthServiceException) {
				final AuthServiceException authException = (AuthServiceException) throwable;
				getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
				// TODO:
				// LoginActivity.start(getActivity(), mDataHolder, authException.getCode());
				return;
			}
			showToastLong(getActivity(), R.string.error_unknown_server_error);
			finish();
		}
	};

	@Nullable
	private RestaurantsResponse mRestaurants = null;

	private ErrorHelper mErrorHelper;
	private int mAnimationType;

	private boolean mFirstRun = true;
	private Target mTarget;

	@Override
	protected void handleIntent(Intent intent) {
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_UP);
	}

	@Override
	public void initUi() {
		mErrorHelper = new ErrorHelper(loader, txtError, btnSettings, errorViews);
		ViewUtils.setVisible(panelBottom, false);
		mTarget = new Target() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
				final TransitionDrawable td = new TransitionDrawable(
						new Drawable[]{
								new ColorDrawable(Color.TRANSPARENT),
								new BitmapDrawable(getResources(), bitmap)
						}
				);
				td.setCrossFadeEnabled(true);
				getActivity().findViewById(R.id.img_holder).setBackgroundDrawable(td);
				td.startTransition(1000);
			}

			@Override
			public void onBitmapFailed(Drawable errorDrawable) {
			}

			@Override
			public void onPrepareLoad(Drawable placeHolderDrawable) {
			}
		};
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		postDelayed(getResources().getInteger(R.integer.default_animation_duration_quick), new Runnable() {
			@Override
			public void run() {
				validate();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.onDestroy();
	}

	@Override
	public void finish() {
		loader.onDestroy();
		if(mRestaurants == null) {
			super.finish();
			return;
		}
		if(mRestaurants.getItems().size() == 1) {
			super.finish();
			overridePendingTransition(android.R.anim.fade_in, R.anim.fake_fade_out);
		} else {
			loader.animateColor(Color.WHITE);
			loader.scaleUp(new Runnable() {
				@Override
				public void run() {
					ValidateActivityCamera.super.finish();
					overridePendingTransition(android.R.anim.fade_in, R.anim.fake_fade_out);
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mFirstRun) {
			loader.setColor(getResources().getColor(R.color.loader_bg));
			if(mAnimationType == EXTRA_LOADER_ANIMATION_SCALE_DOWN) {
				final int dpSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
				loader.setSize(dpSize, dpSize);
			} else {
				loader.setSize(0, 0);
			}
		}
	}

	@OnClick(R.id.btn_down)
	public void onDownPressed(final View v) {
		panelHolder.animate().translationY(-1200).start();
		loader.animate().translationY(-1200).start();
	}

	@Override
	public void onBackPressed() {
		if(panelHolder.getTranslationY() != 0) {
			panelHolder.animate().translationY(0).start();
			loader.animate().translationY(0).start();
		} else {
			super.onBackPressed();
		}
	}

	private void validate() {
		if(mFirstRun) {
			ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
			loader.animateLogoFast(R.drawable.ic_fork_n_knife);
			loader.showProgress(false);
			loader.scaleDown(null, new Runnable() {
				@Override
				public void run() {
					startLoader();
				}
			});
		}
		mFirstRun = false;
	}

	private void startLoader() {
		final Intent intent = new Intent(this, CaptureActivity.class);
		intent.putExtra(CaptureActivity.EXTRA_SHOW_BACK, false);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			final ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right,
			                                                                            R.anim.slide_out_left);
			startActivityForResult(intent, REQUEST_CODE_SCAN_QR, activityOptions.toBundle());
		} else {
			startActivityForResult(intent, REQUEST_CODE_SCAN_QR);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_CODE_SCAN_QR) {
				loader.startProgressAnimation(10000, new Runnable() {
					@Override
					public void run() {
					}
				});
				final String mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				api.checkQrCode(mQrData).flatMap(new Func1<TableDataResponse, Observable<Restaurant>>() {
					@Override
					public Observable<Restaurant> call(TableDataResponse tableDataResponse) {
						return api.getRestaurant(tableDataResponse.getRestaurantId());
					}
				}).subscribe(new Action1<Restaurant>() {
					@Override
					public void call(final Restaurant restaurant) {
						loader.animateLogo(restaurant.getDecoration().getLogo(), R.drawable.ic_fork_n_knife, 350);
						final int color = Color.parseColor("#" + restaurant.getDecoration().getBackgroundColor());
						loader.animateColor(color);
						Picasso.with(getActivity()).load(restaurant.getDecoration().getBackgroundImage()).into(mTarget);
						loader.stopProgressAnimation();
						loader.updateProgressMax(new Runnable() {
							@Override
							public void run() {
								ViewUtils.setVisible(panelBottom, true);
							}
						});
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						// TODO:
					}
				});
			}
		} else {
			finish();
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}