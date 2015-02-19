package com.omnom.android.fragment.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;

import java.util.List;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 19.02.2015.
 */
public class MenuAdapterMiddle extends BaseExpandableListAdapter {
	private final Context mContext;

	private final Menu mMenu;

	private final Category mChild;

	private final LayoutInflater mInflater;

	public MenuAdapterMiddle(final Context context, final Menu menu, final Category child) {
		mContext = context;
		mMenu = menu;
		mChild = child;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getGroupCount() {
		final List<Category> children = mChild.children();
		return getRawItemsCount() + (children == null ? 0 : children.size());
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		final int itemCount = getRawItemsCount();
		if(groupPosition < itemCount) {
			return 0;
		}
		return mChild.children().get(groupPosition - itemCount).items().size();
	}

	@Override
	public Object getGroup(final int groupPosition) {
		final int itemCount = getRawItemsCount();
		if(groupPosition < itemCount) {
			return mChild.items().get(groupPosition);
		}
		return mChild.children().get(groupPosition - itemCount);
	}

	/**
	 * @return number of item without category
	 */
	private int getRawItemsCount() {
		if(mChild.items() == null) {
			return 0;
		}
		return mChild.items().size();
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		final Object group = getGroup(groupPosition);
		if(group instanceof Category) {
			final Category cat = (Category) group;
			return cat.items().get(childPosition);
		}
		return null;
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return getGroup(groupPosition).hashCode();
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return getChild(groupPosition, childPosition).hashCode();
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
		final Object group = getGroup(groupPosition);
		if(group instanceof Category) {
			final Category cat = (Category) group;
			convertView.setTag(group);
			((TextView) findById(convertView, R.id.txt_title)).setText(cat.name());
		}
		if(group instanceof Item) {
			final Item item = (Item) group;
			convertView.setTag(group);
			((TextView) findById(convertView, R.id.txt_title)).setText(item.name());
		}
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView,
	                         final ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_menu_subheader, null, false);
		}
		final Item item = (Item) getChild(groupPosition, childPosition);
		((TextView) findById(convertView, R.id.txt_title)).setText(item.name());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return false;
	}
}
