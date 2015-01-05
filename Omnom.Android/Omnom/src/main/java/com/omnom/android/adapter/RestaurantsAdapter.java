package com.omnom.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
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

		@InjectView(R.id.img_cover)
		public ImageView imgCover;

		@InjectView(R.id.txt_title)
		public TextView txtTitle;

		@InjectView(R.id.panel_linear)
		public View panelAddress;

		@InjectView(R.id.txt_address)
		public TextView txtAddress;

		@InjectView(R.id.txt_distance)
		public TextView txtDistance;

		@InjectView(R.id.txt_schedule)
		public TextView txtSchedule;

		public RestaurantViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}

		public RestaurantViewHolder(Activity activity) {
			ButterKnife.inject(this, activity);
		}

		public void bindData(final Context context, final Restaurant item, Drawable placeholder, int weekDay) {
			final String background = RestaurantHelper.getBackground(item, context.getResources().getDisplayMetrics());
			if(!TextUtils.isEmpty(background)) {
				OmnomApplication.getPicasso(context)
				                .load(background)
				                .placeholder(placeholder)
				                .into(imgCover);
			} else {
				imgCover.setImageDrawable(placeholder);
			}

			txtTitle.setText(item.title());
			final String addressSmall = RestaurantHelper.getAddressSmall(context, item);
			if(!TextUtils.isEmpty(addressSmall)) {
				ViewUtils.setVisible(txtAddress, true);
				txtAddress.setText(addressSmall);
			} else {
				ViewUtils.setVisible(txtAddress, false);
			}
			// TODO: Implement
			// txtDistance.setText("~50м");
			txtSchedule.setText(RestaurantHelper.getOpenedTime(context, item, weekDay));
		}

		public void alpha(final int alpha) {
			panelAddress.animate().alpha(alpha).setDuration(ANIMATION_DURATION).start();
			txtSchedule.animate().alpha(alpha).setDuration(ANIMATION_DURATION).start();
			txtTitle.animate().alpha(alpha).setDuration(ANIMATION_DURATION).start();
			imgCover.animate().alpha(alpha).setDuration(ANIMATION_DURATION).start();
		}
	}

	private final Context mContext;

	private final List<Restaurant> mRestaurants;

	private final LayoutInflater mInflater;

	private final int mWeekDay;

	private final ColorDrawable mPlaceholderDrawable;

	private int mSelectedPosition = -1;

	public RestaurantsAdapter(Context context, List<Restaurant> restaurants) {
		mContext = context;
		mRestaurants = restaurants;
		mInflater = LayoutInflater.from(mContext);
		mWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		mPlaceholderDrawable = new ColorDrawable(mContext.getResources().getColor(R.color.order_item_price));
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
		holder.bindData(mContext, item, mPlaceholderDrawable, mWeekDay);
		if(mSelectedPosition != -1) {
			if(mSelectedPosition != position) {
				ViewCompat.setHasTransientState(convertView, true);
				holder.alpha(0);
			} else {
				if(ViewCompat.hasTransientState(convertView)) {
					ViewCompat.setHasTransientState(convertView, false);
				}
			}
		} else {
			if(convertView.getAlpha() != 1.0f) {
				ViewCompat.setHasTransientState(convertView, false);
				holder.alpha(1);
			} else {
				if(ViewCompat.hasTransientState(convertView)) {
					ViewCompat.setHasTransientState(convertView, false);
				}
			}
		}
	}

	public void setSelected(final int position) {
		mSelectedPosition = position;
	}
}
