package com.omnom.android.linker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.model.restaurant.Restaurant;

import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class MockPlacesAdapter extends ArrayAdapter<Restaurant> {

	static class ViewHolder {
		@InjectView(R.id.name)
		protected TextView name;

		@InjectView(R.id.location)
		protected TextView location;

		@InjectView(R.id.type)
		protected TextView type;

//		@InjectView(R.id.rating)
//		protected RatingBar ratingBar;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private final LayoutInflater inflater;
	private final Random         mRandom;
	private       ViewHolder     holder;

	public MockPlacesAdapter(Context context, List<Restaurant> objects) {
		super(context, R.layout.item_restaurant, objects);
		inflater = LayoutInflater.from(context);
		mRandom = new Random();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null) {
			view = inflater.inflate(R.layout.item_restaurant, parent, false);
			holder = new ViewHolder(view);
			// holder.ratingBar.setProgress(5);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		bindView(getItem(position), holder);
		return view;
	}

	private void bindView(Restaurant item, ViewHolder holder) {
		holder.name.setText(item.getTitle());
		holder.location.setText(item.getAddress(getContext()));
		holder.type.setText(item.getDescription());
		// holder.ratingBar.setProgress(mRandom.nextInt(10));
	}
}
