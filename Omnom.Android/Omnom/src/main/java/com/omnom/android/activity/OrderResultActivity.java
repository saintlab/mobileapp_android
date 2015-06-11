package com.omnom.android.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.BuildConfig;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.restaurateur.model.restaurant.WishResponseItem;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.view.HeaderView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.functions.Action1;

/**
 * Created by xCh3Dx on 27.04.2015.
 */
public class OrderResultActivity extends BaseOmnomActivity {

	public static final String PARAM_STATUS = "status";

	public static final String PARAM_ID = "id";

	public static final String WISH_STATUS_CANCELED = "canceled";

	public static final String WISH_STATUS_READY = "ready";

	public static final int DURATION_ITEM_TRANSITION = 150;

	private static final String TAG = OrderResultActivity.class.getSimpleName();

	public class ItemViewHolder {
		@InjectView(R.id.txt_title)
		TextView txtTitle;

		@InjectView(R.id.txt_info)
		TextView txtInfo;

		private ItemViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	@InjectView(R.id.order_number_continer)
	protected View viewOrderNumber;

	@InjectView(R.id.panel_top)
	protected HeaderView viewHeader;

	@InjectView(R.id.txt_title)
	protected TextView txtTitle;

	@InjectView(R.id.txt_pin_code)
	protected TextView txtPinCode;

	@InjectView(R.id.txt_order_number)
	protected TextView txtOrderNumber;

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	@InjectView(R.id.pin_code_container)
	protected View viewPinCode;

	@InjectView(R.id.main_content)
	protected LinearLayout viewContent;

	@Inject
	protected RestaurateurObservableApi api;

	@Nullable
	private Uri mData;

	@Nullable
	private String mStatus;

	@Nullable
	private String mId;

	private boolean mFirstRun = true;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// parse URL data - started by url (sms message)
		mData = getIntent().getData();
		if(mData != null) {
			mStatus = mData.getQueryParameter(PARAM_STATUS);
			mId = mData.getQueryParameter(PARAM_ID);
		}

		// parse extras data - started by push
		if(getIntent().hasExtra(EXTRA_WISH_STATUS) && getIntent().hasExtra(EXTRA_WISH_ID)) {
			final Bundle extras = getIntent().getExtras();
			mStatus = extras.getString(EXTRA_WISH_STATUS);
			mId = extras.getString(EXTRA_WISH_ID);
		}

		// for debug purposes
		if(BuildConfig.DEBUG && (TextUtils.isEmpty(mStatus) || TextUtils.isEmpty(mId))) {
			mId = "553f487f8a4755212208f087";
			mStatus = WISH_STATUS_CANCELED;
		}
	}

	@Override
	public void initUi() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mFirstRun) {
			if(TextUtils.isEmpty(mId)) {
				Log.d(TAG, "Unable to show data for wishId = " + mId + " status = " + mStatus);
				finish();
				return;
			}

			if(WISH_STATUS_CANCELED.equals(mStatus)) {
				txtTitle.setText(getString(R.string.wish_your_order_canceled));
				txtTitle.setTextColor(getResources().getColor(R.color.error_red));
				txtInfo.setText(getString(R.string.wish_order_canceled_info));
			} else {
				txtTitle.setText(getString(R.string.wish_your_order_ready));
				txtTitle.setTextColor(getResources().getColor(R.color.order_accepted_color));
				txtInfo.setText(getString(R.string.wish_order_ready_info));
			}

			AnimationUtils.animateAlpha(viewOrderNumber, false, 0);
			AnimationUtils.animateAlpha(viewPinCode, false, 0);

			viewHeader.setButtonLeft(R.string.close, new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					onBackPressed();
				}
			})
			          .showProgress(true);
			subscribe(api.getWish(mId),
			          new Action1<WishResponse>() {
				          @Override
				          public void call(final WishResponse wishResponse) {
					          viewHeader.showProgress(false);
					          AnimationUtils.animateAlpha(viewOrderNumber, true);
					          AnimationUtils.animateAlpha(viewPinCode, true);
					          txtPinCode.setText(wishResponse.code());
					          txtOrderNumber.setText(wishResponse.internalTableId());
					          addItems(wishResponse.items());
				          }
			          }, new Action1<Throwable>() {
						@Override
						public void call(final Throwable throwable) {
							viewHeader.showProgress(false);
							Log.e(TAG, "api.getWish id = " + mId, throwable);
						}
					});

			mFirstRun = false;
		}

	}

	private void addItems(final List<WishResponseItem> items) {
		if(items == null) {
			return;
		}
		final int delay = getResources().getInteger(R.integer.default_animation_duration_short);
		for(final WishResponseItem item : items) {
			postDelayed(delay, new Runnable() {
				@Override
				public void run() {
					addItem(item);
				}
			});
		}
	}

	private void addItem(final WishResponseItem item) {
		if(item == null) {
			return;
		}
		final View view = LayoutInflater.from(this).inflate(R.layout.item_order_result, viewContent, false);
		final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
		itemViewHolder.txtTitle.setText(item.title());
		itemViewHolder.txtInfo.setText(getString(R.string.wish_result_item, item.quantity(), AmountHelper.format(item.pricePerItem())
				+ getString(R.string.currency_suffix_ruble)));
		AnimationUtils.scaleHeight(view, 0, 0);
		viewContent.addView(view);
		AnimationUtils.scaleHeight(view, getResources().getDimensionPixelSize(R.dimen.order_ready_item_height), DURATION_ITEM_TRANSITION);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_result;
	}
}
