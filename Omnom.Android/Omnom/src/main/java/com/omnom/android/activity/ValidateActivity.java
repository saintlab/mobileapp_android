package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.restaurateur.api.Protocol;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.WaiterCallResponse;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.BluetoothUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToastLong;

/**
 * Created by Ch3D on 08.10.2014.
 */
public abstract class ValidateActivity extends BaseOmnomActivity {

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType) {
		final boolean hasBle = BluetoothUtils.hasBleSupport(context);
		final Intent intent = new Intent(context, hasBle ? ValidateActivityBle.class : ValidateActivityCamera.class);
		if(context instanceof ConfirmPhoneActivity) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		context.startActivity(intent, enterAnim, exitAnim, true);
	}

	protected BaseErrorHandler onError = new OmnomBaseErrorHandler(this) {
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
	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.btn_bottom)
	protected Button btnSettings;

	@InjectViews({R.id.txt_error, R.id.panel_errors})
	protected List<View> errorViews;

	@InjectView(R.id.panel_bottom)
	protected View panelBottom;

	@InjectView(R.id.img_holder)
	protected View imgHolder;

	@InjectView(R.id.btn_down)
	protected Button btnDown;

	@Inject
	protected RestaurateurObeservableApi api;

	@Inject
	protected AuthService authenticator;

	protected ErrorHelper mErrorHelper;
	protected Target mTarget;
	protected boolean mFirstRun = true;

	private int mAnimationType;
	private Restaurant mRestaurant;
	private TableDataResponse mTable;
	private boolean mWaiterCalled;

	@Override
	protected void handleIntent(Intent intent) {
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
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
	protected void onPause() {
		super.onPause();
		if(mRestaurant == null) {
			loader.animateLogoFast(R.drawable.ic_fork_n_knife);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mFirstRun) {
			if(mAnimationType == EXTRA_LOADER_ANIMATION_SCALE_DOWN) {
				final int dpSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
				loader.setSize(dpSize, dpSize);
			} else {
				loader.setSize(0, 0);
			}
		}
	}

	@Override
	public void initUi() {
		mErrorHelper = new ErrorHelper(loader, txtError, btnSettings, errorViews);
		panelBottom.setTranslationY(100);
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

	protected void validate() {
		if(mFirstRun || mRestaurant == null) {
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

	protected abstract void startLoader();

	@OnClick(R.id.btn_bill)
	public void onBill(final View v) {
		api.getOrders(mTable.getRestaurantId(), mTable.getId()).subscribe(new Action1<List<Order>>() {
			@Override
			public void call(List<Order> orders) {
				if(!orders.isEmpty()) {
					OrdersActivity.start(ValidateActivity.this, new ArrayList<Order>(orders));
				} else {
					showToastLong(getActivity(), R.string.there_are_no_orders_on_this_table);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {

			}
		});
	}

	@OnClick(R.id.btn_waiter)
	public void onWaiter(final View v) {
		final Observable<WaiterCallResponse> observable;
		if(!mWaiterCalled) {
			observable = api.waiterCall(mRestaurant.getId(), mTable.getId());
		} else {
			observable = api.waiterCallStop(mRestaurant.getId(), mTable.getId());
		}
		observable.subscribe(new Action1<WaiterCallResponse>() {
			@Override
			public void call(WaiterCallResponse tableDataResponse) {
				if(tableDataResponse.isSuccess()) {
					mWaiterCalled = !mWaiterCalled;
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				// TODO:
			}
		});
	}

	@OnClick(R.id.btn_down)
	public void onDownPressed(final View v) {
		final Rect rect = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		final int height = rect.height();
		final Interpolator interpolator = new DecelerateInterpolator();
		final int duration = 700;
		imgHolder.animate().translationY(-height).setDuration(duration).setInterpolator(interpolator).start();
		loader.animate().translationY(-height).setDuration(duration).setInterpolator(interpolator).start();
		AnimationUtils.animateAlpha(btnDown, false);
	}

	@Override
	public void onBackPressed() {
		if(imgHolder.getTranslationY() != 0) {
			imgHolder.animate().translationY(0).start();
			loader.animate().translationY(0).start();
			AnimationUtils.animateAlpha(btnDown, true);
		} else {
			super.onBackPressed();
		}
	}

	protected final void onDataLoaded(final Restaurant restaurant, TableDataResponse table) {
		mRestaurant = restaurant;
		mTable = table;

		final String token = OmnomApplication.get(getActivity()).getAuthToken();
		authenticator.getUser(token).subscribe(new Action1<UserResponse>() {
			@Override
			public void call(UserResponse userResponse) {
				OmnomApplication.get(getActivity()).cacheUserProfile(userResponse.getUser());
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {

			}
		});

		api.newGuest(mTable.getRestaurantId(), mTable.getId()).subscribe(new Action1<ResponseBase>() {
			@Override
			public void call(ResponseBase responseBase) {

			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {

			}
		});

		loader.post(new Runnable() {
			@Override
			public void run() {
				loader.animateLogo(restaurant.getDecoration().getLogo(),
				                   R.drawable.ic_fork_n_knife,
				                   getResources().getInteger(R.integer.default_animation_duration_short));
			}
		});
		loader.animateColor(RestaurantHelper.getBackgroundColor(restaurant));
		Picasso.with(getActivity()).load(restaurant.getDecoration().getBackgroundImage()).into(mTarget);
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				AnimationUtils.animateAlpha(btnDown, true);
				ViewUtils.setVisible(imgHolder, true);
				ViewUtils.setVisible(panelBottom, true);
				panelBottom.animate()
				           .translationY(0)
				           .setInterpolator(new DecelerateInterpolator())
				           .setDuration(getResources().getInteger(R.integer.default_animation_duration_short))
				           .start();
			}
		});
	}
}
