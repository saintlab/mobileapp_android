package com.omnom.android.activity.validate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.RestaurantsListActivity;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.entrance.EntranceDataHelper;
import com.omnom.android.fragment.NoOrdersFragment;
import com.omnom.android.fragment.SearchFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.mixpanel.OmnomErrorHelper;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.drawable.TransitionDrawable;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.PanelSlideListenerAdapter;
import com.omnom.android.view.PanelSlideListenerSimple;
import com.omnom.android.view.subcategories.SubcategoriesView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 13.03.2015.
 */
public class ValidateViewHelper implements SubcategoriesView.OnCollapsedTouchListener {

	private static final String TAG_SEARCH_FRAGMENT = "search_fragment";

	private final ValidateActivity mActivity;

	private final Handler mHandler;

	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.txt_error_additional)
	protected TextView txtErrorAdditional;

	@InjectView(R.id.btn_bottom)
	protected View btnErrorRepeat;

	@InjectView(R.id.txt_bar)
	protected TextView txtBar;

	@InjectView(R.id.sliding_layout)
	protected SlidingUpPanelLayout slidingPanel;

	@InjectView(R.id.menu_gradient)
	protected View menuGradientPanel;

	@InjectView(R.id.menu_subcategories)
	protected SubcategoriesView menuCategories;

	@InjectView(R.id.txt_bottom)
	protected TextView txtErrorRepeat;

	@InjectView(R.id.btn_demo)
	protected View btnDemo;

	@InjectView(R.id.stub_bottom_menu)
	protected ViewStub stubBottomMenu;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.content)
	protected View contentView;

	@InjectViews({R.id.txt_error, R.id.panel_errors})
	protected List<View> errorViews;

	@InjectView(R.id.img_profile)
	protected ImageView imgProfile;

	@InjectView(R.id.btn_previous)
	protected ImageView imgPrevious;

	@InjectView(R.id.txt_table)
	protected TextView txtTable;

	@InjectView(R.id.txt_demo_leave)
	protected TextView txtLeave;

	protected View bottomView;

	@Nullable
	private EntranceData mEntranceData;

	private ValueAnimator mColorAnimator;

	private OmnomErrorHelper mErrorHelper;

	private TableDataResponse mTable;

	private boolean mTableAnimated = false;

	public ValidateViewHelper(ValidateActivity activity) {
		mActivity = activity;
		ButterKnife.inject(this, activity);
		mHandler = new Handler();
	}

	public void onResume() {
		menuCategories.onResume();
	}

	public void onPause() {
		loader.animateLogoFast(R.drawable.ic_fork_n_knife);
	}

	public void clearErrors(Restaurant restaurant, final boolean animateLogo) {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		ViewUtils.setVisibleGone(txtErrorAdditional, false);
		if(animateLogo) {
			if(restaurant == null) {
				loader.animateLogo(R.drawable.ic_fork_n_knife);
			} else {
				loader.animateLogo(RestaurantHelper.getLogo(restaurant),
				                   R.drawable.ic_fork_n_knife);
			}
		}
	}

	public View.OnClickListener getBillClickListener() {
		if(EntranceDataHelper.isBar(mEntranceData) || RestaurantHelper.isLunch(mActivity.mRestaurant)) {
			return mActivity.mOnOrderClickListener;
		}
		return mActivity.mOnBillClickListener;
	}

	public void onDestroy() {
		loader.onDestroy();
	}

	public void scaleDown() {
		loader.scaleDown();
	}

	public void setSize(final int w, final int h) {
		loader.setSize(w, h);
	}

	public void onPrevious() {
		if(slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			collapseSlidingPanel();
		} else {
			RestaurantsListActivity.startLeft(mActivity);
		}
	}

	public void collapseSlidingPanel() {
		menuCategories.collapse();
		menuCategories.restoreHeadersStyle();
		slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
	}

	public void collapseSlidingPanelInstant() {
		menuCategories.collapseInstant();
		slidingPanel.smoothSlideToInstant(0);
		imgProfile.setAlpha(1.0f);
		imgPrevious.setAlpha(1.0f);
		ViewUtils.setVisibleGone(imgProfile, true);
		ViewUtils.setVisibleGone(imgPrevious, true); // TODO: RestaurantHelper.isLunch?
		loader.showLogo();
	}

	public void expandSlidingPanel() {
		slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
	}

	public boolean onBackPressed() {
		if(mActivity.getSupportFragmentManager().getBackStackEntryCount() == 0
				&& slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			if(menuCategories.hasExpandedGroups()) {
				menuCategories.restoreHeadersStyle();
				menuCategories.collapse();
			} else {
				collapseSlidingPanel();
			}
			return true;
		}
		return false;
	}

	public void init() {
		mErrorHelper = new OmnomErrorHelper(loader, txtError, btnErrorRepeat, txtErrorRepeat,
		                                    btnDemo, errorViews);
		slidingPanel.setPanelSlideListener(menuCategories);
		menuCategories.setOnCollapsedTouchListener(this);
		final PanelSlideListenerAdapter listener = new PanelSlideListenerAdapter();
		listener.addListener(new PanelSlideListenerSimple() {
			@Override
			public void onPanelSlide(final View panel, final float slideOffset) {
				if(slideOffset == 1) {
					if(mColorAnimator == null) {
						mColorAnimator = ValueAnimator
								.ofInt(Color.TRANSPARENT, mActivity.getResources().getColor(R.color
										                                                            .transparent_black));
						mColorAnimator.setDuration(350);
						mColorAnimator.setEvaluator(new ArgbEvaluator());
						mColorAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
						mColorAnimator
								.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
									@Override
									public void onAnimationUpdate(ValueAnimator animation) {
										slidingPanel.setBackgroundColor(
												(Integer) animation.getAnimatedValue());
									}
								});
					}
					mColorAnimator.start();
				} else if(slideOffset == 0) {
					if(mColorAnimator != null) {
						mColorAnimator.reverse();
					}
				}

				final float loaderFactor = 1.0f - slideOffset;

				if(loaderFactor < 0.85f) {
					AnimationUtils.animateAlphaGone(imgProfile, false);
					AnimationUtils.animateAlphaGone(imgPrevious, false);
					AnimationUtils.animateAlphaGone(txtBar, false);
					AnimationUtils.animateAlphaGone(txtTable, false);
					loader.hideLogo();
				} else {
					AnimationUtils.animateAlphaGone(imgProfile, true);
					AnimationUtils.animateAlphaGone(imgPrevious, true);
					AnimationUtils.animateAlphaGone(txtBar, EntranceDataHelper.isBar(mEntranceData));
					loader.showLogo();
				}
				loader.scaleDown((int) (loader.getLoaderSizeDefault() * loaderFactor), 0, null);
			}

			@Override
			public void onPanelCollapsed(final View panel) {
				setSlidingTouchEnabled(true);
			}

			@Override
			public void onPanelExpanded(final View panel) {
				setSlidingTouchEnabled(false);
			}
		});
		listener.addListener(menuCategories);
		slidingPanel.setPanelSlideListener(listener);

		loader.setLogo(R.drawable.ic_fork_n_knife);
		loader.setColor(mActivity.getResources().getColor(R.color.loader_bg));

		btnDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				mErrorHelper.hideError();
				ValidateActivity.startDemo(mActivity, R.anim.fake_fade_in_instant,
				                           R.anim.fake_fade_out_instant,
				                           ValidateActivity.EXTRA_LOADER_ANIMATION_SCALE_DOWN);
			}
		});
	}

	private void setSlidingTouchEnabled(final boolean enabled) {
		slidingPanel.setTouchEnabled(enabled);
	}

	@Override
	public void onCollapsedSubcategoriesTouch(final MotionEvent e) {
		slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
	}

	public void setBackground(final TransitionDrawable bgTransitionDrawable) {
		AndroidUtils.setBackground(contentView, bgTransitionDrawable);
	}

	public void startProgressAnimation(final int duration) {
		loader.startProgressAnimation(duration);
	}

	public void showRestaurants() {
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				RestaurantsListActivity.start(mActivity, true);
			}
		});
	}

	public void validate(final Runnable runnable) {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		loader.animateLogoFast(R.drawable.ic_fork_n_knife);
		loader.showProgress(false);
		loader.scaleDown(null, runnable);
	}

	protected void onBillStep1(final View v) {
		if(slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
			collapseSlidingPanel();
		}
	}

	protected void updateLightProfile(final boolean visible) {
		updateProfile(R.drawable.ic_profile_white, visible);
	}

	private void updateProfile(final int backgroundResource, final boolean visible) {
		imgProfile.setImageResource(backgroundResource);
		ViewUtils.setVisibleGone(imgProfile, visible);
		if(mTable != null) {
			final boolean isTable = mEntranceData.getType() == EntranceData.TYPE_TABLE;
			if(isTable) {
				txtTable.setText(String.valueOf(mTable.getInternalId()));
				if(visible && !ViewUtils.isVisible(txtTable)) {
					if(txtTable.getTranslationX() == 0) {
						ViewUtils.setVisibleGone(imgProfile, true);
					}
					animateTable();
				} else {
					ViewUtils.setVisibleGone(txtTable, visible);
				}
			} else {
				ViewUtils.setVisibleGone(txtTable, false);
			}
		} else {
			ViewUtils.setVisibleGone(txtTable, false);
			imgProfile.setImageResource(backgroundResource);
			ViewUtils.setVisibleGone(imgProfile, visible);
		}
	}

	private void animateTable() {
		final ViewTreeObserver viewTreeObserver = imgProfile.getViewTreeObserver();
		if(viewTreeObserver.isAlive() && !mTableAnimated) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					AndroidUtils.removeOnGlobalLayoutListener(imgProfile, this);
					final int animationDuration = mActivity.getResources().getInteger(R.integer.default_animation_duration_long);
					AnimationUtils.animateAlpha(txtTable, true, new Runnable() {
						@Override
						public void run() {
							final int translation = (imgProfile.getLeft() - txtTable.getLeft()) -
									(txtTable.getWidth() - imgProfile.getWidth());
							mTableAnimated = true;
							txtTable.animate()
							        .translationX(translation)
							        .setDuration(animationDuration)
							        .setListener(new AnimatorListenerAdapter() {
								        @Override
								        public void onAnimationEnd(final Animator animation) {
									        mHandler.postDelayed(new Runnable() {
										        @Override
										        public void run() {
											        final boolean isMenuExpanded =
													        slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
											        AnimationUtils.animateAlphaSkipTag(txtTable, false, new Runnable() {
												        @Override
												        public void run() {
													        AnimationUtils.animateAlphaSkipTag(imgProfile,
													                                           !isMenuExpanded,
													                                           null,
													                                           animationDuration);
												        }
											        }, animationDuration);
										        }
									        }, animationDuration);
								        }
							        })
							        .start();
							AnimationUtils.animateAlphaSkipTag(imgProfile, false, null, animationDuration);
						}
					}, animationDuration);
				}
			});
		}
	}

	protected void hideProfile() {
		ViewUtils.setVisibleGone(txtTable, false);
		ViewUtils.setVisibleGone(imgProfile, false);
	}

	public View getPanelBottom() {
		return findById(mActivity, R.id.panel_validate_bottom);
	}

	public void beforeShowOrders() {
		hideProfile();
		ViewUtils.setVisibleGone(imgPrevious, false);
		ViewUtils.setVisibleGone(getPanelBottom(), false);
		AnimationUtils.animateAlpha(slidingPanel, false);
		AnimationUtils.animateAlpha(menuGradientPanel, false);
		ViewUtils.setVisibleGone(txtLeave, false);
		loader.animateLogo(R.drawable.ic_bill_white_normal);
		loader.startProgressAnimation(10000, null);
	}

	public void configureScreen(Restaurant restaurant) {
		// FIXME: uncomment the code below when promo is implemented
		final boolean promoEnabled = false; //RestaurantHelper.isPromoEnabled(restaurant);
		// FIXME: uncomment the code below when waiter call is implemented
		final boolean waiterEnabled = false; //RestaurantHelper.isWaiterEnabled(restaurant);
		final boolean isBar = EntranceDataHelper.isBar(mEntranceData);
		final boolean isLunch = RestaurantHelper.isLunch(restaurant);

		if(bottomView == null) {
			ViewUtils.setVisibleGone(findById(mActivity, R.id.txt_bar), isBar);
			if(isBar || isLunch) {
				stubBottomMenu.setLayoutResource(R.layout.layout_bar);
			} else {
				stubBottomMenu.setLayoutResource(
						waiterEnabled ? R.layout.layout_bill_waiter : R.layout.layout_bill);
			}
			bottomView = stubBottomMenu.inflate();
			AndroidUtils.applyFont(mActivity, (ViewGroup) bottomView, OmnomFont.LSF_LE_REGULAR);
		}

		if(waiterEnabled) {
			final View btnWaiter = findById(bottomView, R.id.btn_waiter);
			btnWaiter.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					mActivity.onWaiter(v);
				}
			});
		}

		findById(bottomView, R.id.btn_bill).setOnClickListener(getBillClickListener());
		findById(bottomView, R.id.btn_order).setOnClickListener(mActivity.mOnOrderClickListener);
	}

	public void translatePanelBottom(final int value) {
		getPanelBottom().animate().translationY(value).start();
	}

	public void stopProgressAnimation() {
		loader.stopProgressAnimation();
	}

	public void onOrderError(final Restaurant restaurant, final TableDataResponse table,
	                         final boolean isDemo) {
		txtErrorAdditional.setText(mActivity.getString(R.string.there_are_no_orders_additional,
		                                               String.valueOf(table.getInternalId())));
		AndroidUtils.clickify(txtErrorAdditional,
		                      mActivity.getString(R.string.there_are_no_orders_additional_mark),
		                      new ClickSpan.OnClickListener() {
			                      @Override
			                      public void onClick() {
				                      showNoOrdersInfo(table);
			                      }
		                      });
		ViewUtils.setVisibleGone(txtErrorAdditional, true);
		final int tableNumber = table != null ? table.getInternalId() : 0;
		mErrorHelper.showNoOrders(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onErrorClose(restaurant, isDemo);
			}
		}, tableNumber);
		txtError.setText(StringUtils.EMPTY_STRING);
	}

	public void updateProgressMax(Runnable callback) {
		loader.updateProgressMax(callback);
	}

	protected void onErrorClose(Restaurant mRestaurant, boolean mIsDemo) {
		mActivity.clearErrors(true);
		ViewUtils.setVisibleGone(txtErrorAdditional, false);
		loader.animateLogoFast(RestaurantHelper.getLogo(mRestaurant),
		                       R.drawable.ic_bill_white_normal);
		loader.showProgress(false);
		mActivity.configureScreen(mRestaurant);
		updateLightProfile(!mIsDemo);
		ViewUtils.setVisibleGone(imgPrevious, !mIsDemo);
		ViewUtils.setVisibleGone(txtLeave, mIsDemo);
		ViewUtils.setVisibleGone(getPanelBottom(), true);
		AnimationUtils.animateAlpha(menuGradientPanel, true);
		AnimationUtils.animateAlpha(slidingPanel, true);
	}

	private void showNoOrdersInfo(TableDataResponse table) {
		mActivity.getSupportFragmentManager().beginTransaction()
		         .addToBackStack(null)
		         .setCustomAnimations(R.anim.slide_in_up,
		                              R.anim.slide_out_down,
		                              R.anim.slide_in_up,
		                              R.anim.slide_out_down)
		         .replace(R.id.fragment_container,
		                  NoOrdersFragment.newInstance(table.getInternalId()))
		         .commit();
	}

	public void scaleUp(final int duration, final Runnable callback) {
		loader.scaleUp(duration, callback);
	}

	public void showOrders(final Runnable callback) {
		loader.hideLogo(new Runnable() {
			@Override
			public void run() {
				scaleUp(mActivity.getResources()
				                 .getInteger(R.integer.default_animation_duration_medium), callback);
			}
		});
	}

	public void onReturnToValidation(final Restaurant restaurant, final boolean isDemo) {
		loader.scaleDown(null, new Runnable() {
			@Override
			public void run() {
				ViewUtils.setVisibleGone(getPanelBottom(), true);
				AnimationUtils.animateAlpha(menuGradientPanel, true);
				AnimationUtils.animateAlpha(slidingPanel, true);
				updateLightProfile(!isDemo);
				ViewUtils.setVisibleGone(imgPrevious, !isDemo);
				ViewUtils.setVisibleGone(txtLeave, isDemo);
				loader.animateLogo(RestaurantHelper.getLogo(restaurant),
				                   R.drawable.ic_fork_n_knife);
				loader.showLogo();
			}
		});
	}

	public void onDataLoaded(final Restaurant restaurant, final TableDataResponse table,
	                         final boolean forwardToBill, final boolean
			                         mIsDemo, final String requestId) {
		mTable = table;
		animateRestaurantLogo(restaurant);
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				mActivity.configureScreen(restaurant);
				updateLightProfile(!mIsDemo);
				ViewUtils.setVisibleGone(imgPrevious, !mIsDemo);
				ViewUtils.setVisibleGone(txtLeave, mIsDemo);
				ViewUtils.setVisibleGone(getPanelBottom(), true);
				AnimationUtils.animateAlpha(menuGradientPanel, true);
				AnimationUtils.animateAlpha(slidingPanel, true);
				getPanelBottom().animate().translationY(0)
				                .setInterpolator(new DecelerateInterpolator())
				                .setDuration(mActivity.getResources()
				                                      .getInteger(R.integer.default_animation_duration_short)).start();
				if(forwardToBill) {
					loader.postDelayed(new Runnable() {
						@Override
						public void run() {
							mActivity.onBill(mActivity.findViewById(R.id.btn_bill));
							// mContext.showOrders(restaurant.orders(), requestId);
						}
					}, mActivity.getResources()
					            .getInteger(R.integer.default_animation_duration_short));
				}
			}
		});
	}

	public void animateRestaurantLogo(final Restaurant restaurant) {
		loader.post(new Runnable() {
			@Override
			public void run() {
				loader.animateLogo(RestaurantHelper.getLogo(restaurant), R.drawable.ic_fork_n_knife,
				                   mActivity.getResources()
				                            .getInteger(R.integer.default_animation_duration_short));
			}
		});
		loader.animateColor(RestaurantHelper.getBackgroundColor(restaurant));
	}

	public void handleRestaurants(final Runnable callback) {
		loader.stopProgressAnimation();
		loader.updateProgressMax(callback);
	}

	public void showProgress(final boolean visible, final boolean animate,
	                         final Runnable callback) {
		loader.showProgress(visible, animate, callback);
	}

	public void onChangeTable() {
		AnimationUtils.animateAlpha(imgPrevious, false);
		AnimationUtils.animateAlpha(txtTable, false);
		bottomView.animate().translationY(bottomView.getHeight());
		loader.animateLogoFast(R.drawable.ic_fork_n_knife);
		AnimationUtils.animateAlpha(imgProfile, false);
	}

	public void showBack(final boolean visible) {
		ViewUtils.setVisibleGone(imgPrevious, visible);
	}

	public void bindMenuData(final Menu menu, ValidateOrderHelper orderHelper) {
		final boolean hasMenu = menu != null && !menu.isEmpty();
		ViewUtils.setVisibleGone(slidingPanel, hasMenu);
		ViewUtils.setVisibleGone(menuCategories, hasMenu);
		slidingPanel.setTouchEnabled(hasMenu);
		if(menu != null) {
			menuCategories.bind(menu, orderHelper.insureOrder());
		}
	}

	public OmnomErrorHelper getErrorHelper() {
		return mErrorHelper;
	}

	public SlidingUpPanelLayout.PanelState getSlidingPanelState() {
		return slidingPanel.getPanelState();
	}

	public void showSearchFragment(Map<String, Item> itemsByName) {
		ViewUtils.setVisibleGone(bottomView, false);
		mActivity.getSupportFragmentManager().beginTransaction()
		         .addToBackStack(null)
		         .setCustomAnimations(R.anim.slide_in_up,
		                              R.anim.slide_out_down,
		                              R.anim.slide_in_up,
		                              R.anim.slide_out_down)
		         .replace(R.id.fragment_container, SearchFragment.newInstance(itemsByName),
		                  TAG_SEARCH_FRAGMENT)
		         .commit();
	}

	public void onSearchFragmentClose() {
		ViewUtils.setVisibleGone(bottomView, true);
	}

	public void showMenuItemDetails(final Item item) {
		menuCategories.showDetails(item);
	}

	public void updateLoader(final Restaurant restaurant) {
		loader.setBgColor(RestaurantHelper.getBackgroundColor(restaurant));
		loader.animateLogoInstant(RestaurantHelper.getLogo(restaurant), R.drawable.ic_fork_n_knife);
	}

	public void resetMenuState() {
		menuCategories.resetState();
	}

	public void hideMenu() {
		slidingPanel.setTouchEnabled(false);
		ViewUtils.setVisibleGone(slidingPanel, false);
		ViewUtils.setVisibleGone(menuCategories, false);
	}

	public void setEntranceData(final EntranceData entranceData) {
		mEntranceData = entranceData;
	}

	public void onProfile() {
		AnimationUtils.animateAlpha(imgProfile, true);
		AnimationUtils.animateAlpha(txtTable, false);
	}
}
