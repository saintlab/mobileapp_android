package com.omnom.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.menu.model.Category;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 27.01.2015.
 */
public class MenuCategoriesAdapter extends BaseAdapter {

	static class ViewHolder {
		@InjectView(R.id.txt_title)
		protected TextView txtTitle;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private final LayoutInflater mInflater;

	private Context mContext;

	private List<Category> mCategories;

	public MenuCategoriesAdapter(Context context, List<Category> categories) {
		mContext = context;
		mCategories = categories;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return mCategories.size();
	}

	@Override
	public Category getItem(final int position) {
		return mCategories.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	protected void bindView(View convertView, final int position) {
		final Category item = getItem(position);
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder != null) {
			holder.txtTitle.setText(item.name());
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_menu_category, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		bindView(convertView, position);
		return convertView;
	}
}
