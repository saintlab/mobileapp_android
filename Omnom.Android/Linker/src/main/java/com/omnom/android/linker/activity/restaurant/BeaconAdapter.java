package com.omnom.android.linker.activity.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.bind.BeaconFilter;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 15.09.2014.
 */
public class BeaconAdapter extends BaseAdapter {
	static class ViewHolder {
		@InjectView(R.id.txt_title)
		protected TextView title;

		@InjectView(R.id.txt_rssi)
		protected TextView rssi;

		@InjectView(R.id.txt_tx)
		protected TextView tx;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	public class Item {
		private final String mTitle;
		private final int mMinRssi;
		private final int mMaxRssi;
		private final int mAvgRssi;
		private final int mTxLevel;
		private int mColor;

		public Item(String title, int minRssi, int maxRssi, int avgRssi, int txLevel, int color) {
			mTitle = title;
			mMinRssi = minRssi;
			mMaxRssi = maxRssi;
			mAvgRssi = avgRssi;
			mTxLevel = txLevel;
			mColor = color;
		}

	}

	private final ArrayList<Item> items;
	private final LayoutInflater inflater;
	private Context mContext;
	private ViewHolder holder;

	@DebugLog
	public BeaconAdapter(Context context, Collection<BeaconsChartActivity.BeaconDataSerie> values) {
		mContext = context;
		inflater = LayoutInflater.from(context);
		items = new ArrayList<Item>();
		for(final BeaconsChartActivity.BeaconDataSerie v : values) {
			if(v.size() > 0) {
				final int maxRssi = BeaconFilter.getMaxRssi(v.rssiList);
				final int minRssi = BeaconFilter.getMinRssi(v.rssiList);
				final int avgRssi = BeaconFilter.getAvgRssi(v.rssiList);
				final int txPower = v.getTxPower();
				items.add(new Item(v.getTitle(), minRssi, maxRssi, avgRssi, txPower, v.getColor()));
			}
		}
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null) {
			view = inflater.inflate(R.layout.item_beacon, parent, false);
			holder = new ViewHolder(view);
			// holder.ratingBar.setProgress(5);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		bindView((Item) getItem(position), holder);
		return view;
	}

	private void bindView(Item item, final ViewHolder holder) {
		holder.title.setText("minor: " + item.mTitle);
		holder.title.setTextColor(item.mColor);
		holder.rssi.setText("min: " + item.mMinRssi + " max: " + item.mMaxRssi + " avg: " + item.mAvgRssi);
		holder.rssi.setTextColor(item.mColor);
		holder.tx.setText("tx: " + item.mTxLevel);
		holder.tx.setTextColor(item.mColor);
	}
}
