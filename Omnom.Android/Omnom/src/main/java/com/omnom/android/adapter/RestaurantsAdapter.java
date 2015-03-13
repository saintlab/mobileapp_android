package com.omnom.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.RestaurantsListActivity;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 09.12.2014.
 */
public class RestaurantsAdapter extends BaseAdapter {

	public static class RestaurantViewHolder {

		public static final int ANIMATION_DURATION = 100;

		@InjectView(R.id.cover)
		public LoaderView cover;

		@InjectView(R.id.txt_info)
		public TextView txtInfo;

		@InjectView(R.id.txt_schedule)
		public TextView txtSchedule;

		public RestaurantViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}

		public RestaurantViewHolder(Activity activity) {
			ButterKnife.inject(this, activity);
		}

		public void bindData(final Context context, final Restaurant item, int weekDay) {
			if(item == null) {
				cover.setLogo(R.drawable.transparent);
				txtInfo.setText(StringUtils.EMPTY_STRING);
				txtSchedule.setText(StringUtils.EMPTY_STRING);
				return;
			}

			cover.setLogo(R.drawable.transparent);
			final String logo = RestaurantHelper.getLogo(item);
			cover.showProgress(false);
			cover.setColor(RestaurantHelper.getBackgroundColor(item));
			cover.animateLogo(logo, R.drawable.transparent, 0);

			final String addressSmall = RestaurantHelper.getAddressSmall(context, item);
			if(!TextUtils.isEmpty(addressSmall)) {
				ViewUtils.setVisible(txtInfo, true);
				txtInfo.setText(addressSmall);
				if(item.distance() != null) {
					final String distance = StringUtils.formatDistance(item.distance());
					final String addressWithDistance = addressSmall + " " + distance;
					SpannableString s = SpannableString.valueOf(addressWithDistance);
					s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.info_hint)),
					          addressWithDistance.indexOf(distance), addressWithDistance.length(),
					          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					txtInfo.setText(s);
				}
			} else {
				ViewUtils.setVisible(txtInfo, false);
			}
			final String openedTime = RestaurantHelper.getOpenedTime(context, item, weekDay);
			if(TextUtils.isEmpty(openedTime)) {
				ViewUtils.setVisible(txtSchedule, false);
			} else {
				ViewUtils.setVisible(txtSchedule, true);
				txtSchedule.setText(openedTime);
			}
		}

		public void alpha(final int alpha) {
			txtInfo.animate().alpha(alpha).setDuration(ANIMATION_DURATION).start();
			txtSchedule.animate().alpha(alpha).setDuration(ANIMATION_DURATION).start();
			cover.animate().alpha(alpha).setDuration(ANIMATION_DURATION).start();
		}

		public float alpha() {
			return cover.getAlpha();
		}
	}

	private final Context mContext;

	private final List<Restaurant> mRestaurants;

	private final LayoutInflater mInflater;

	private final int mWeekDay;

	private int mSelectedPosition = -1;

	private int logoSizeSmall;

	public RestaurantsAdapter(Context context, List<Restaurant> restaurants) {
		mContext = context;
		mRestaurants = restaurants;
		mInflater = LayoutInflater.from(mContext);
		mWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		logoSizeSmall = (int) (displayMetrics.widthPixels * RestaurantsListActivity.LOGO_SCALE_SMALL + 0.5);
	}

	@Override
	public int getCount() {
		return mRestaurants.size();
	}

	@Override
	public Object getItem(final int position) {
		return mRestaurants.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_restaurant, parent, false);
			final LoaderView loaderView = (LoaderView) convertView.findViewById(R.id.cover);
			loaderView.resetMargins();
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) loaderView.getLayoutParams();
			layoutParams.width = logoSizeSmall;
			layoutParams.height = logoSizeSmall;
			loaderView.setSize(logoSizeSmall, logoSizeSmall);
			RestaurantViewHolder holder = new RestaurantViewHolder(convertView);
			convertView.setTag(holder);
		}
		bindView(convertView, (Restaurant) getItem(position), position);
		return convertView;
	}

	private void bindView(final View convertView, final Restaurant item, final int position) {
		RestaurantViewHolder holder = (RestaurantViewHolder) convertView.getTag();
		if(holder == null) {
			return;
		}
		holder.bindData(mContext, item, mWeekDay);
		if(mSelectedPosition != -1) {
			if(mSelectedPosition != position) {
				holder.alpha(0);
			}
		} else if(holder.alpha() != 1.0f) {
			holder.alpha(1);
		}
	}

	public void setSelected(final int position) {
		mSelectedPosition = position;
		notifyDataSetChanged();
	}

}
