package com.omnom.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.activity.holder.TakeawayEntranceData;
import com.omnom.android.activity.order.BaseOrderAcceptedActivity;
import com.omnom.android.activity.validate.ValidateActivity;
import com.omnom.android.adapter.RestaurantsAdapter;
import com.omnom.android.fragment.delivery.DeliveryDetailsFragment;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.restaurant.schedule.DailySchedule;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.DialogUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.InjectView;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;

public class RestaurantActivity extends BaseOmnomFragmentActivity {

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, final int topTranslation) {
		start(activity, restaurant, false, topTranslation);
	}

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, boolean finish) {
		start(activity, restaurant, finish, 0);
	}

	public static void start(BaseOmnomFragmentActivity activity, Restaurant restaurant, boolean finish) {
		start(activity, restaurant, finish, 0);
	}

	public static void start(BaseOmnomActivity activity, Restaurant restaurant, boolean finish, final int topTranslation) {
		final Intent intent = getIntent(activity, restaurant, topTranslation);
		activity.startForResult(intent,
		                        com.omnom.android.utils.R.anim.fade_in,
		                        com.omnom.android.utils.R.anim.fake_fade_out,
		                        REQUEST_CODE_CHANGE_TABLE);
	}

	public static void start(BaseOmnomFragmentActivity activity, Restaurant restaurant, boolean finish, final int topTranslation) {
		final Intent intent = getIntent(activity, restaurant, topTranslation);
		activity.startForResult(intent,
		                        com.omnom.android.utils.R.anim.fade_in,
		                        com.omnom.android.utils.R.anim.fake_fade_out,
		                        REQUEST_CODE_CHANGE_TABLE);
	}

	private static Intent getIntent(final Context context, final Restaurant restaurant, final int topTranslation) {
		final Intent intent = new Intent(context, RestaurantActivity.class);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_TRANSLATION_TOP, topTranslation);
		return intent;
	}

	protected RestaurantsAdapter.RestaurantViewHolder mRestaurantViewHolder;

	@InjectView(R.id.btn_call)
	protected Button btnCall;

	@InjectView(R.id.cover)
	protected LoaderView viewCover;

	@InjectView(R.id.scroll)
	protected ScrollView scrollView;

	@InjectView(R.id.txt_bar)
	protected TextView txtBar;

	@InjectView(R.id.txt_im_inside)
	protected TextView txtImInside;

	@InjectView(R.id.txt_takeaway)
	protected TextView txtTakeaway;

	@InjectView(R.id.main_content)
	protected View viewMain;

	@InjectView(R.id.txt_lunch)
	protected View txtLunch;

	private Restaurant mRestaurant;

	private boolean mFinishing = false;

	private int mTopTranslation;

	private int logoSizeSmall;

	private int logoSizeLarge;

	private boolean mEnterBar = false;

	@OnClick(R.id.txt_bar)
	protected void doBar() {
		// TODO: BarEntranceData.create("#orderNumber", "#pinCode")
		if(!mFinishing) {
			mEnterBar = true;
			scrollView.smoothScrollTo(0, 0);
			final int duration = getResources().getInteger(R.integer.default_animation_duration_short);
			AnimationUtils.animateAlpha(findById(this, R.id.txt_info), false, duration);
			AnimationUtils.animateAlpha(findById(this, R.id.txt_schedule), false, duration);
			AnimationUtils.animateAlpha(btnCall, false, duration);
			postDelayed(duration, new Runnable() {
				@Override
				public void run() {
					ValidateActivity.start(RestaurantActivity.this, R.anim.fade_in, R.anim.fake_fade_out,
					                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, mRestaurant);
				}
			});
		}
	}

	@OnClick(R.id.txt_im_inside)
	protected void doImInside() {
		// TODO:
		//if(BuildConfig.DEBUG) {
		//	// DeliveryDetailsFragment.show(getSupportFragmentManager(), R.id.fragment_container, mRestaurant);
		//	// TakeawayTimeFragment.show(getSupportFragmentManager(), R.id.fragment_container);
		//	return;
		//}
		if(!mFinishing) {
			ValidateActivityShortcut.start(this);
		}
	}

	@OnClick(R.id.txt_lunch)
	protected void doOrderLunch() {
		if(!mFinishing) {
			if (validateOrderTime()) {
				DeliveryDetailsFragment.show(getSupportFragmentManager(), R.id.fragment_container, mRestaurant);
			}
		}
	}

	@OnClick(R.id.txt_takeaway)
	protected void doTakeAway() {
		if(!mFinishing) {
			if (validateOrderTime()) {
				// TODO: create TakeawayEntranceData and pass to ValidateActivity intent (Extras.EXTRA_ENTRANCE_DATA)
				final Date orderDate = new Date();
				final String takeawayAfter = "????? 1.5 ????";
				final String takeawayAddress = "??????? 13";
				BaseOrderAcceptedActivity.start(getActivity(), TakeawayEntranceData.create(orderDate, takeawayAddress, takeawayAfter), 0, 0);
			}
		}
	}

	@OnClick(R.id.btn_call)
	protected void doCall() {
		if(!mFinishing) {
			AndroidUtils.openDialer(this, mRestaurant.phone());
		}
	}

	@OnClick(R.id.btn_close)
	protected void doClose() {
		if(!mFinishing) {
			finish();
		}
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
		mTopTranslation = intent.getIntExtra(EXTRA_TRANSLATION_TOP, 0);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_CHANGE_TABLE && resultCode == RESULT_CODE_TABLE_CHANGED) {
			setResult(RESULT_CODE_TABLE_CHANGED);
			RestaurantActivity.super.finish();
		}
	}

	@Override
	public void finish() {
		if(mFinishing) {
			// skip : already finishing
			return;
		}
		mFinishing = true;

		if(mEnterBar) {
			ActivityCompat.finishAffinity(this);
			return;
		}

		if(mRestaurantViewHolder == null) {
			finishSimple();
			return;
		}

		scrollView.smoothScrollTo(0, 0);

		final View panelBottom = findViewById(R.id.panel_bottom);
		final int paddingDiff = (int) (getResources().getDimension(R.dimen.image_button_size) +
				getResources().getDimension(R.dimen.activity_vertical_margin) * 2 -
				getResources().getDimension(R.dimen.activity_vertical_margin_large) + 0.5);
		final int topBarHeight = getResources().getDimensionPixelSize(R.dimen.restaurants_topbar_height);
		final int translationY = topBarHeight - mTopTranslation - paddingDiff;

		final int duration = getResources().getInteger(R.integer.default_animation_duration_medium);
		btnCall.animate().translationYBy(translationY).setDuration(duration).start();
		btnCall.animate().alpha(0).setDuration(duration).start();
		panelBottom.animate().translationYBy(translationY).setDuration(duration).start();
		viewMain.animate().translationYBy(translationY).setDuration(duration).start();
		AnimationUtils.animateAlpha(panelBottom, false, duration);
		AnimationUtils.scale(viewCover, logoSizeSmall, duration, new Runnable() {
			@Override
			public void run() {
			}
		});
		viewCover.scaleDown(logoSizeSmall, duration, true, new Runnable() {
			@Override
			public void run() {
				finishSimple();
			}
		});
	}

	private void finishSimple() {
		RestaurantActivity.super.finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	public void initUi() {
		if(mRestaurant == null) {
			finish();
			return;
		}

		
		ViewUtils.setVisible(txtBar, RestaurantHelper.hasBar(mRestaurant));
		ViewUtils.setVisible(txtImInside, RestaurantHelper.hasTableOrder(mRestaurant));
		ViewUtils.setVisible(txtLunch, RestaurantHelper.hasPreOrder(mRestaurant));
		ViewUtils.setVisible(txtTakeaway, RestaurantHelper.hasTakeaway(mRestaurant));

		final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		logoSizeSmall = (int) (displayMetrics.widthPixels * RestaurantsListActivity.LOGO_SCALE_SMALL + 0.5);
		logoSizeLarge = viewCover.getLoaderSizeDefault();
		viewCover.resetMargins();

		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewCover.getLayoutParams();
		layoutParams.width = logoSizeLarge;
		layoutParams.height = logoSizeLarge;
		viewCover.scaleDown();

		final int weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		mRestaurantViewHolder = new RestaurantsAdapter.RestaurantViewHolder(this);
		mRestaurantViewHolder.bindData(this, mRestaurant, weekDay);
		final String phone = mRestaurant.phone();
		if(!TextUtils.isEmpty(phone)) {
			btnCall.setText(PhoneNumberUtils.formatNumber(phone));
		} else {
			ViewUtils.setVisible(btnCall, false);
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_restaurant;
	}

	private boolean validateOrderTime() {
		final int weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		final DailySchedule dailySchedule = RestaurantHelper.getOrderSchedule(mRestaurant, weekDay);
		if(dailySchedule.isClosed()) {
			showScheduleDialog(dailySchedule.getOpenTime(), dailySchedule.getCloseTime());
		}
		return !dailySchedule.isClosed();
	}

	private void showScheduleDialog(final String from, final String to) {
		final String message = getString(R.string.orders_are_accepted_only_from_to, from, to);
		final AlertDialog dialog = DialogUtils.showDialog(this, message, R.string.ok,
		                                                  new DialogInterface.OnClickListener() {
			                                                  @Override
			                                                  public void onClick(DialogInterface dialog, int which) {
				                                                  dialog.dismiss();
			                                                  }
		                                                  });
		TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
	}

}
