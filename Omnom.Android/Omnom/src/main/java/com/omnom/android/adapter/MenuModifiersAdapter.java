package com.omnom.android.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.Modifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by Ch3D on 05.02.2015.
 */
public class MenuModifiersAdapter extends BaseExpandableListAdapter implements CompoundButton.OnCheckedChangeListener {

	public static final String TYPE_SELECT = "select";

	public static final String TYPE_MULTISELECT = "multiselect";

	public static final String TYPE_CHECKBOX = "checkbox";

	static class ChildViewHolder {
		@InjectView(R.id.txt_title)
		@Optional
		protected TextView txtTitle;

		@InjectView(R.id.checkbox)
		@Optional
		protected CheckBox cbSelected;

		private CompoundButton.OnCheckedChangeListener mListener;

		ChildViewHolder(View convertView, CompoundButton.OnCheckedChangeListener listener) {
			mListener = listener;
			ButterKnife.inject(this, convertView);
		}

		public void bindChild(
				@Nullable final Modifier modifierGroup,
				@Nullable final Modifier modifierItem, boolean forceUncheck, final Boolean isSelected) {
			if(modifierItem != null) {
				txtTitle.setText(modifierItem.name());
			}
			cbSelected.setTag(R.id.group, modifierGroup);
			cbSelected.setTag(R.id.item, modifierItem);
			cbSelected.setOnCheckedChangeListener(mListener);
			if(forceUncheck) {
				cbSelected.setChecked(false);
			} else {
				cbSelected.setChecked(isSelected);
			}
		}

		public void bindGroup(final Modifier modifierGroup) {
			txtTitle.setText(modifierGroup.name());
		}

		public void alpha(final float alpha, final long duration) {
			txtTitle.animate().alpha(alpha).setDuration(duration).start();
			cbSelected.animate().alpha(alpha).setDuration(duration).start();
		}
	}

	private static final int GROUPS_TYPE_COUNT = 2;

	private static final int GROUP_TYPE_EXPANDABLE = 0;

	private static final int GROUP_TYPE_UNEXPANDABLE = 1;

	private final LayoutInflater mInflater;

	private HashMap<String, Boolean> mSelection = new HashMap<String, Boolean>();

	private Context mContext;

	private Modifiers mModifiers;

	@Nullable
	private List<Modifier> mModifierList;

	public MenuModifiersAdapter(Context context, Modifiers modifiers, List<Modifier> modifiersList, List<String> selectedIds) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mModifiers = modifiers;
		mModifierList = modifiersList;
		for(final String selectedId : selectedIds) {
			mSelection.put(selectedId, Boolean.TRUE);
		}
	}

	@Override
	public int getGroupCount() {
		return mModifierList == null ? 0 : mModifierList.size();
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
		if(TYPE_SELECT.equals(group.type()) || TYPE_MULTISELECT.equals(group.type())) {
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
		if(modifier == null) {
			return -1;
		}
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

		ChildViewHolder holder;

		if(convertView == null) {
			switch(groupType) {
				case GROUP_TYPE_EXPANDABLE:
					convertView = mInflater.inflate(R.layout.item_modifier_group, parent, false);
					holder = new ChildViewHolder(convertView, this);
					convertView.setTag(holder);
					break;

				case GROUP_TYPE_UNEXPANDABLE:
					convertView = mInflater.inflate(R.layout.item_modifier_item, parent, false);
					holder = new ChildViewHolder(convertView, this);
					convertView.setTag(holder);
					break;
			}
		}

		final ImageView imgIndicator = (ImageView) convertView.findViewById(R.id.indicator);
		if(imgIndicator != null) {
			imgIndicator.animate().rotation(isExpanded ? 0 : 180).start();
		}

		bindGroup(convertView, groupPosition, groupType, groupModifier);
		return convertView;
	}

	private void bindGroup(final View convertView, final int groupPosition, final int groupType, final Modifier groupModifier) {
		final ChildViewHolder holder = (ChildViewHolder) convertView.getTag();
		if(groupType == GROUP_TYPE_EXPANDABLE) {
			holder.bindGroup(groupModifier);
		} else {
			final Modifier modifier = mModifiers.items().get(groupModifier.id());
			holder.bindChild(modifier, modifier, false, mSelection.get(modifier.id()));
		}
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView,
	                         final ViewGroup parent) {
		ChildViewHolder holder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_modifier_item, parent, false);
			holder = new ChildViewHolder(convertView, this);
			convertView.setTag(holder);
			holder.alpha(0, 0);
		}
		bindChild(convertView, groupPosition, childPosition);
		return convertView;
	}

	private void bindChild(final View convertView, final int groupPosition, final int childPosition) {
		final ChildViewHolder holder = (ChildViewHolder) convertView.getTag();
		final Modifier group = (Modifier) getGroup(groupPosition);
		final Modifier modifier = (Modifier) getChild(groupPosition, childPosition);
		if(modifier != null && group != null) {
			final boolean hasSelectionData = mSelection.get(modifier.id()) != null;
			holder.bindChild(group,
			                 modifier,
			                 !hasSelectionData || (hasSelectionData && !mSelection.get(modifier.id())),
			                 mSelection.get(modifier.id()));
			holder.alpha(1, mContext.getResources().getInteger(R.integer.default_animation_duration_quick));
		}
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
		return false;
	}

	@Override
	public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
		Modifier itemModifier = (Modifier) buttonView.getTag(R.id.item);
		Modifier groupModifier = (Modifier) buttonView.getTag(R.id.group);
		if(itemModifier != null) {
			mSelection.put(itemModifier.id(), isChecked);
			if(TYPE_SELECT.equals(groupModifier.type()) && buttonView.isChecked()) {
				final ArrayList<String> resetIds = new ArrayList<String>();
				resetIds.addAll(groupModifier.list());
				resetIds.remove(itemModifier.id());
				for(String id : resetIds) {
					mSelection.remove(id);
				}
			}
		}
		notifyDataSetChanged();
	}

	public List<String> getSelectedIds() {
		ArrayList<String> result = new ArrayList<String>();
		for(final Map.Entry<String, Boolean> entry : mSelection.entrySet()) {
			if(entry.getValue()) {
				result.add(entry.getKey());
			}
		}
		return result;
	}
}
