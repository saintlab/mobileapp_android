package com.omnom.android.linker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.model.Place;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 31.07.2014.
 */
public class MockPlacesAdapter extends ArrayAdapter<Place> {

	static class ViewHolder {
		@InjectView(R.id.name)
		protected TextView name;

		@InjectView(R.id.location)
		protected TextView location;

		@InjectView(R.id.type)
		protected TextView type;

		@InjectView(R.id.rating)
		protected RatingBar ratingBar;

		private ViewHolder(View convertView) {
			ButterKnife.inject(convertView);
		}
	}

	private final LayoutInflater inflater;
	private ViewHolder holder;

	public MockPlacesAdapter(Context context, List<Place> objects) {
		super(context, R.layout.item_place, objects);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.item_place, parent, false);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		return view;
	}
}
