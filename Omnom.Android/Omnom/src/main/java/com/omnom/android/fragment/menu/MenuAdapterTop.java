package com.omnom.android.fragment.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Menu;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 19.02.2015.
 */
public class MenuAdapterTop extends BaseExpandableListAdapter {

	private final Context mContext;

	private final Menu mMenu;

	private final LayoutInflater mInflater;

	public MenuAdapterTop(final Context context, final Menu menu) {
		mContext = context;
		mMenu = menu;

		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getGroupCount() {
		return mMenu.categories().size();
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		return mMenu.categories().get(groupPosition).children().size();
	}

	@Override
	public Category getGroup(final int groupPosition) {
		return mMenu.categories().get(groupPosition);
	}

	@Override
	public Category getChild(final int groupPosition, final int childPosition) {
		return getGroup(groupPosition).children().get(childPosition);
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return getGroup(groupPosition).id();
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return getChild(groupPosition, childPosition).id();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_menu_header, null, false);
		}
		final Category group = getGroup(groupPosition);
		convertView.setTag(group);
		((TextView) findById(convertView, R.id.txt_title)).setText(group.name());
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView,
	                         final ViewGroup parent) {

		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_menu_expandable_list, null, false);
		}
		final Category group = getChild(groupPosition, childPosition);
		convertView.setTag(group);
		((ExpandableListView) findById(convertView, R.id.expandable_list)).setAdapter(new MenuAdapterMiddle(mContext, mMenu, group));
		return convertView;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return false;
	}
}
