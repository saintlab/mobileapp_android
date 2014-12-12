package com.omnom.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
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

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 09.12.2014.
 */
public class RestaurantsAdapter extends BaseAdapter {

	public static class RestaurantViewHolder {
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
			OmnomApplication.getPicasso(context)
			                .load(RestaurantHelper.getBackground(item, context.getResources().getDisplayMetrics()))
			                .placeholder(placeholder)
			                .into(imgCover);

			txtTitle.setText(item.getTitle());
			txtAddress.setText(RestaurantHelper.getAddressSmall(context, item));
			txtDistance.setText("~50м");
			txtSchedule.setText(RestaurantHelper.getOpenedTime(context, item, weekDay));
		}

		public void minimize(final int translationY) {
			imgCover.animate().translationYBy(translationY).start();
			txtTitle.animate().translationYBy(translationY).start();
			panelAddress.animate().translationYBy(translationY).start();
			txtSchedule.animate().translationYBy(translationY).start();
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
				convertView.animate().alpha(0).start();
			} else {
				ViewCompat.setHasTransientState(convertView, false);
			}
		} else {
			if(convertView.getAlpha() != 1.0f) {
				ViewCompat.setHasTransientState(convertView, true);
				convertView.setAlpha(0);
				convertView.animate().alpha(1).start();
			} else {
				ViewCompat.setHasTransientState(convertView, false);
			}
		}
	}

	public void setSelected(final int position) {
		mSelectedPosition = position;
	}
}