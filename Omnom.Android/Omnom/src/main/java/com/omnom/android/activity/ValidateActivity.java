package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
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
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.restaurateur.model.WaiterCallResponse;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.table.DemoTableData;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.drawable.TransitionDrawable;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.BluetoothUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToastLong;

/**
 * Created by Ch3D on 08.10.2014.
 */
public abstract class ValidateActivity extends BaseOmnomActivity {

	private static final String TAG = ValidateActivity.class.getSimpleName();

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType, boolean isDemo) {
		final boolean hasBle = BluetoothUtils.hasBleSupport(context);
		final Intent intent = new Intent(context, hasBle ? ValidateActivityBle.class : ValidateActivityCamera.class);
		if(context instanceof ConfirmPhoneActivity) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		intent.putExtra(EXTRA_DEMO_MODE, isDemo);
		context.start(intent, enterAnim, exitAnim, !isDemo);
	}

	protected final View.OnClickListener mInternetErrorClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startLoader();
		}
	};

	protected final View.OnClickListener mInternetErrorClickBillListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			onBill(findViewById(R.id.btn_bill));
		}
	};

	protected BaseErrorHandler onError = new OmnomBaseErrorHandler(this) {
		@Override
		protected void onThrowable(Throwable throwable) {
			loader.stopProgressAnimation(true);
			if(throwable instanceof RetrofitError) {
				throwable.printStackTrace();
				final RetrofitError cause = (RetrofitError) throwable;
				if(cause.getResponse() != null) {
					// TODO: Refactor this ugly piece of ... code
					if(cause.getUrl().contains(Protocol.FIELD_LOGIN) && cause.getResponse().getStatus() != 200) {
						EnteringActivity.start(ValidateActivity.this);
						return;
					}
				}
			}
			if(throwable instanceof AuthServiceException) {
				getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
				EnteringActivity.start(ValidateActivity.this);
				return;
			}
			throwable.printStackTrace();
			showToastLong(getActivity(), R.string.error_unknown_server_error);
			finish();
		}
	};

	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.btn_bottom)
	protected View btnErrorRepeat;

	@InjectView(R.id.txt_error_repeat)
	protected TextView txtErrorRepeat;

	@InjectView(R.id.btn_demo)
	protected View btnDemo;

	@InjectView(R.id.btn_down)
	protected Button btnDownPromo;

	@InjectView(R.id.stub_bottom_menu)
	protected ViewStub stubBottomMenu;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectViews({R.id.txt_error, R.id.panel_errors})
	protected List<View> errorViews;

	@InjectView(R.id.img_profile)
	protected View imgProfile;

	@InjectView(R.id.txt_demo_leave)
	protected TextView txtLeave;

	@Inject
	protected RestaurateurObeservableApi api;

	@Inject
	protected AuthService authenticator;

	protected ErrorHelper mErrorHelper;

	protected Target mTarget;

	protected boolean mFirstRun = true;

	protected Restaurant mRestaurant;

	protected TableDataResponse mTable;

	protected boolean mIsDemo = false;

	protected Picasso mPicasso;

	protected Func1<Restaurant, Restaurant> mPreloadBackgroundFunction;

	private int mAnimationType;

	private boolean mWaiterCalled;

	private Subscription mOrdersSubscription;

	private Subscription mWaiterCallSubscribtion;

	private Subscription mUserSubscription;

	private Subscription mGuestSubscribtion;

	private View bottomView;

	private com.omnom.android.utils.drawable.TransitionDrawable bgTransitionDrawable;

	@Override
	protected void handleIntent(Intent intent) {
		mAnimationType = intent.getIntExtra(EXTRA_LOADER_ANIMATION, EXTRA_LOADER_ANIMATION_SCALE_DOWN);
		mIsDemo = intent.getBooleanExtra(EXTRA_DEMO_MODE, false);
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

	@OnClick(R.id.txt_demo_leave)
	protected void onLeave() {
		onBackPressed();
	}

	protected void clearErrors() {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		if(mRestaurant == null) {
			loader.animateLogo(R.drawable.ic_fork_n_knife);
		} else {
			loader.animateLogo(RestaurantHelper.getLogo(mRestaurant), R.drawable.ic_fork_n_knife);
		}
		if(bgTransitionDrawable.isTransitioned()) {
			bgTransitionDrawable.reverseTransition();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.onDestroy();
		OmnomObservable.unsubscribe(mOrdersSubscription);
		OmnomObservable.unsubscribe(mWaiterCallSubscribtion);
		OmnomObservable.unsubscribe(mUserSubscription);
		OmnomObservable.unsubscribe(mGuestSubscribtion);
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
		bgTransitionDrawable = new com.omnom.android.utils.drawable.TransitionDrawable(
				getResources().getInteger(R.integer.default_animation_duration_short),
				new Drawable[]{
						new ColorDrawable(getResources().getColor(R.color.transparent)),
						new ColorDrawable(getResources().getColor(R.color.error_bg_white_transparent))
				}
		);

		bgTransitionDrawable.setCrossFadeEnabled(true);
		rootView.setBackgroundDrawable(bgTransitionDrawable);
		mPicasso = Picasso.with(getApplicationContext());
		btnDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mErrorHelper.hideError();
				ValidateActivity.start(ValidateActivity.this,
				                       R.anim.fake_fade_in_short,
				                       R.anim.fake_fade_out_short,
				                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, true);
			}
		});
		mErrorHelper = new ErrorHelper(loader, txtError, btnErrorRepeat, txtErrorRepeat, btnDemo, errorViews);

		mPreloadBackgroundFunction = new Func1<Restaurant, Restaurant>() {
			@Override
			public Restaurant call(final Restaurant restaurant) {
				final String bgImgUrl = RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics());
				if(!TextUtils.isEmpty(bgImgUrl)) {
					try {
						mPicasso.load(bgImgUrl).get();
					} catch(IOException e) {
						Log.e(TAG, "unable to load img = " + bgImgUrl);
					}
				}
				return restaurant;
			}
		};

		mTarget = new Target() {
			@Override
			public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
				final BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
				final TransitionDrawable td = new TransitionDrawable(
						1000,
						new Drawable[]{
								getResources().getDrawable(R.drawable.bg_wood),
								drawable
						}
				);
				td.setCrossFadeEnabled(true);
				getWindow().getDecorView().setBackgroundDrawable(td);
				// getActivity().findViewById(R.id.img_holder).setBackgroundDrawable(td);
				td.startTransition();
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

	public void onBill(final View v) {
		v.setEnabled(false);
		ValidationObservable.validateSmart(this)
		                    .map(OmnomObservable.getValidationFunc(this, mErrorHelper, mInternetErrorClickBillListener))
		                    .isEmpty()
		                    .subscribe(new Action1<Boolean>() {
			                    @Override
			                    public void call(final Boolean hasNoErrors) {
				                    if(hasNoErrors) {
					                    clearErrors();
					                    getPanelBottom().animate().translationY(0).start();
					                    mOrdersSubscription = AndroidObservable.bindActivity(getActivity(),
					                                                                         api.getOrders(mTable.getRestaurantId(),
					                                                                                       mTable.getId()))
					                                                           .subscribe(new Action1<List<Order>>() {
						                                                           @Override
						                                                           public void call(List<Order> orders) {
							                                                           v.setEnabled(true);
							                                                           if(!orders.isEmpty()) {
								                                                           OrdersActivity.start(ValidateActivity.this,
								                                                                                new ArrayList<Order>(
										                                                                                orders),
								                                                                                mRestaurant.getDecoration()
								                                                                                           .getBackgroundColor(),
								                                                                                mIsDemo);
							                                                           } else {
								                                                           showToastLong(getActivity(),
								                                                                         R.string
										                                                                         .there_are_no_orders_on_this_table);
							                                                           }
						                                                           }
					                                                           }, new Action1<Throwable>() {
						                                                           @Override
						                                                           public void call(Throwable throwable) {
							                                                           v.setEnabled(true);
						                                                           }
					                                                           });
				                    } else {
					                    startErrorBackroundTransition();
					                    mErrorHelper.showInternetError(mInternetErrorClickBillListener);
					                    getPanelBottom().animate().translationY(200).start();
					                    v.setEnabled(true);
				                    }
			                    }
		                    }, new Action1<Throwable>() {
			                    @Override
			                    public void call(final Throwable throwable) {
				                    startErrorBackroundTransition();
				                    mErrorHelper.showInternetError(mInternetErrorClickBillListener);
				                    v.setEnabled(true);
			                    }
		                    });
	}

	protected void startErrorBackroundTransition() {
		bgTransitionDrawable.startTransition();
	}

	public void onWaiter(final View v) {
		final Observable<WaiterCallResponse> observable;
		if(!mWaiterCalled) {
			observable = api.waiterCall(mRestaurant.getId(), mTable.getId());
		} else {
			observable = api.waiterCallStop(mRestaurant.getId(), mTable.getId());
		}
		mWaiterCallSubscribtion = AndroidObservable.bindActivity(this, observable)
		                                           .subscribe(new Action1<WaiterCallResponse>() {
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
		// imgHolder.animate().translationY(-height).setDuration(duration).setInterpolator(interpolator).start();
		loader.animate().translationY(-height).setDuration(duration).setInterpolator(interpolator).start();
		AnimationUtils.animateAlpha(btnDownPromo, false);
	}

	@Override
	public void onBackPressed() {
		//if(imgHolder.getTranslationY() != 0) {
		//	imgHolder.animate().translationY(0).start();
		//	loader.animate().translationY(0).start();
		//	AnimationUtils.animateAlpha(btnDownPromo, RestaurantHelper.isPromoEnabled(mRestaurant));
		//} else {
		super.onBackPressed();
		//}
	}

	@OnClick(R.id.img_profile)
	protected void onProfile(View v) {
		UserProfileActivity.startSliding(this, mTable.getInternalId());
	}

	protected final void onDataLoaded(final Restaurant restaurant, TableDataResponse table) {
		mRestaurant = restaurant;
		mTable = table;

		final String token = OmnomApplication.get(getActivity()).getAuthToken();
		mUserSubscription = AndroidObservable.bindActivity(this, authenticator.getUser(token))
		                                     .subscribe(new Action1<UserResponse>() {
			                                     @Override
			                                     public void call(UserResponse userResponse) {
				                                     OmnomApplication.get(getActivity()).cacheUserProfile(new UserProfile(userResponse));
			                                     }
		                                     }, new Action1<Throwable>() {
			                                     @Override
			                                     public void call(Throwable throwable) {

			                                     }
		                                     });

		mGuestSubscribtion = AndroidObservable.bindActivity(this, api.newGuest(mTable.getRestaurantId(), mTable.getId()))
		                                      .subscribe(
				                                      new Action1<ResponseBase>() {
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
		mPicasso.setIndicatorsEnabled(true);
		mPicasso.setLoggingEnabled(true);
		mPicasso.load(RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics())).into(mTarget);
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				configureScreen(mRestaurant);
				// ViewUtils.setVisible(imgHolder, true);
				ViewUtils.setVisible(imgProfile, !mIsDemo);
				ViewUtils.setVisible(txtLeave, mIsDemo);
				ViewUtils.setVisible(getPanelBottom(), true);
				getPanelBottom().animate()
				                .translationY(0)
				                .setInterpolator(new DecelerateInterpolator())
				                .setDuration(getResources().getInteger(R.integer.default_animation_duration_short))
				                .start();
			}
		});
	}

	private void configureScreen(final Restaurant restaurant) {
		final boolean promoEnabled = RestaurantHelper.isPromoEnabled(restaurant);
		final boolean waiterEnabled = RestaurantHelper.isWaiterEnabled(restaurant);

		if(bottomView == null) {
			stubBottomMenu.setLayoutResource(waiterEnabled ? R.layout.layout_bill_waiter : R.layout.layout_bill);
			bottomView = stubBottomMenu.inflate();
		}

		if(waiterEnabled) {
			final View btnWaiter = findById(bottomView, R.id.btn_waiter);
			btnWaiter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					onWaiter(v);
				}
			});
		}

		final View btnBill = findById(bottomView, R.id.btn_bill);
		btnBill.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onBill(v);
			}
		});

		ViewUtils.setVisible(btnDownPromo, promoEnabled);
		// getPanelBottom().setTranslationY(100);
	}

	public View getPanelBottom() {
		return findById(this, R.id.panel_bottom);
	}

	protected boolean validateDemo() {
		if(mIsDemo) {
			if(mRestaurant == null || mTable == null) {
				loader.startProgressAnimation(10000, new Runnable() {
					@Override
					public void run() {
					}
				});
				api.getDemoTable().subscribe(
						new Action1<List<DemoTableData>>() {
							@Override
							public void call(final List<DemoTableData> response) {
								final DemoTableData data = response.get(0);
								onDataLoaded(data.getRestaurant(), data.getTable());
							}
						}, onError);
				mFirstRun = false;
			}
			return true;
		}
		return false;
	}
}
