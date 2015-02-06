package com.omnom.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.omnom.android.R;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.Modifiers;

import java.util.List;

/**
 * Created by Ch3D on 05.02.2015.
 */
public class MenuModifiersAdapter extends BaseExpandableListAdapter {

	private static final int GROUPS_TYPE_COUNT = 2;

	private static final int GROUP_TYPE_EXPANDABLE = 0;

	private static final int GROUP_TYPE_UNEXPANDABLE = 1;

	private final LayoutInflater mInflater;

	private Context mContext;

	private Modifiers mModifiers;

	private List<Modifier> mModifierList;

	public MenuModifiersAdapter(Context context, Modifiers modifiers, List<Modifier> modifiersList) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mModifiers = modifiers;
		mModifierList = modifiersList;
	}

	@Override
	public int getGroupCount() {
		return mModifierList.size();
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
		return mModifierList.get(groupPosition).list().size();
	}

	@Override
	public Object getGroup(final int groupPosition) {
		return mModifierList.get(groupPosition);
	}

	@Override
	public int getChildType(final int groupPosition, final int childPosition) {
		return 0;
	}

	@Override
	public int getChildTypeCount() {
		return 1;
	}

	@Override
	public int getGroupType(final int groupPosition) {
		final Modifier group = (Modifier) getGroup(groupPosition);
		if("select".equals(group.type()) || "multiselect".equals(group.type())) {
			return GROUP_TYPE_EXPANDABLE;
		}
		return GROUP_TYPE_UNEXPANDABLE;
	}

	@Override
	public int getGroupTypeCount() {
		return GROUPS_TYPE_COUNT;
	}

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
		final String id = mModifierList.get(groupPosition).list().get(childPosition);
		return mModifiers.items().get(id);
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return getGroup(groupPosition).hashCode();
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		final Modifier modifier = (Modifier) getChild(groupPosition, childPosition);
		return modifier.hashCode();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
		final Modifier groupModifier = (Modifier) getGroup(groupPosition);
		final int groupType = getGroupType(groupPosition);

		if(convertView == null) {
			switch(groupType) {
				case GROUP_TYPE_EXPANDABLE:
					convertView = mInflater.inflate(R.layout.item_modifier_group, parent, false);

					break;

				case GROUP_TYPE_UNEXPANDABLE:
					convertView = mInflater.inflate(R.layout.item_modifier_item, parent, false);
					break;

			}
		}

		final ImageView imgIndicator = (ImageView) convertView.findViewById(R.id.indicator);
		if(imgIndicator != null) {
			imgIndicator.animate().rotation(isExpanded ? 0 : 180).start();
			// imgIndicator.setImageResource(isExpanded ? R.drawable.ic_decrease_qty_normal : R.drawable.ic_increase_qty_normal);
		}

		bindGroup(convertView, groupPosition, groupType, groupModifier);
		return convertView;
	}

	private void bindGroup(final View convertView, final int groupPosition, final int groupType, final Modifier groupModifier) {

	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView,
	                         final ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_modifier_item, parent, false);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return false;
	}
}
