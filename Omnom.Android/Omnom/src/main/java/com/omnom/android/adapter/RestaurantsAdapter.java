package com.omnom.android.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
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
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 09.12.2014.
 */
public class RestaurantsAdapter extends BaseAdapter {

	static class ViewHolder {
		@InjectView(R.id.img_cover)
		protected ImageView imgCover;

		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		@InjectView(R.id.txt_address)
		protected TextView txtAddress;

		@InjectView(R.id.txt_distance)
		protected TextView txtDistance;

		@InjectView(R.id.txt_schedule)
		protected TextView txtSchedule;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private final Context mContext;

	private final List<Restaurant> mRestaurants;

	private final LayoutInflater mInflater;

	private final Picasso mPicasso;

	private final int mWeekDay;

	private final ColorDrawable mPlaceholderDrawable;

	private DisplayMetrics mDisplayMetrics;

	public RestaurantsAdapter(Context context, List<Restaurant> restaurants) {
		mContext = context;
		mRestaurants = restaurants;
		mInflater = LayoutInflater.from(mContext);
		mPicasso = OmnomApplication.getPicasso(mContext);
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
			ViewHolder holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		bindView(convertView, (Restaurant) getItem(position), position);
		return convertView;
	}

	private void bindView(final View convertView, final Restaurant item, final int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder == null) {
			return;
		}
		mDisplayMetrics = mContext.getResources().getDisplayMetrics();
		mPicasso.load(RestaurantHelper.getBackground(item, mDisplayMetrics))
		        .placeholder(mPlaceholderDrawable)
		        .into(holder.imgCover);
		holder.txtTitle.setText(item.getTitle());
		holder.txtAddress.setText(RestaurantHelper.getAddressSmall(mContext, item));
		holder.txtDistance.setText("~50м");
		holder.txtSchedule.setText(RestaurantHelper.getOpenedTime(mContext, item, mWeekDay));
	}
}
