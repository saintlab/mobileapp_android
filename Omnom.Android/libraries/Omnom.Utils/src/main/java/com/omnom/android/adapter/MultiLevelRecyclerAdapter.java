package com.omnom.android.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class MultiLevelRecyclerAdapter extends RecyclerView.Adapter {
	public interface Data {
		List<? extends Data> getChildren();

		boolean isGroup();

		Data getParent();

		Data getRoot();

		void setIsGroup(boolean value);

		void add(Data anchor, Data newItem, final int indexIncrement);

		void remove(Data item);

		int getLevel();
	}

	private boolean mNotifyOnChange;

	private List<Data> mData;

	private HashMap<Data, List<? extends Data>> mGroups;

	public MultiLevelRecyclerAdapter() {
		mData = new ArrayList<Data>();
		mGroups = new HashMap<Data, List<? extends Data>>();
		mNotifyOnChange = true;
	}

	public void add(Data item) {
		if(item != null) {
			mData.add(item);
			if(mNotifyOnChange) {
				notifyItemChanged(mData.size() - 1);
			}
		}
	}

	public void addAll(int position, Collection<? extends Data> data) {
		if(data != null && data.size() > 0) {
			mData.addAll(position, data);
			if(mNotifyOnChange) {
				notifyItemRangeInserted(position, data.size());
			}
		}
	}

	public void addAll(Collection<? extends Data> data) {
		addAll(mData.size(), data);
	}

	public void insert(int position, Data item) {
		mData.add(position, item);
		if(mNotifyOnChange) {
			notifyItemInserted(position);
		}
	}

	public void insert(int position, final int indexIncrement, Data anchor, Data newItem) {
		mData.add(position, newItem);
		final Data parent = anchor.getParent();
		parent.add(anchor, newItem, indexIncrement);
		if(mNotifyOnChange) {
			notifyItemInserted(position);
		}
	}

	public void clear() {
		if(mData.size() > 0) {
			int size = mData.size();
			mData.clear();
			mGroups.clear();
			if(mNotifyOnChange) {
				notifyItemRangeRemoved(0, size);
			}
		}
	}

	public boolean remove(Data item) {
		return remove(item, false);
	}

	public boolean remove(Data item, boolean expandGroupBeforeRemoval) {
		int index;
		boolean removed = false;
		if(item != null && (index = mData.indexOf(item)) != -1) {
			item.getParent().remove(item);
			if((removed = mData.remove(item))) {
				if(mGroups.containsKey(item)) {
					if(expandGroupBeforeRemoval) {
						expandGroup(index);
					}
					mGroups.remove(item);
				}
				if(mNotifyOnChange) {
					notifyItemRemoved(index);
				}
			}
		}
		return removed;
	}

	public Data getItemAt(int position) {
		return mData.get(position);
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	public void expandGroup(int position) {
		if(position < 0) {
			return;
		}
		Data firstItem = getItemAt(position);

		if(!firstItem.isGroup()) {
			return;
		}

		List<? extends Data> group = mGroups.remove(firstItem);

		firstItem.setIsGroup(false);

		notifyItemChanged(position);
		addAll(position + 1, group);

		int level = firstItem.getLevel();
		for(int i = 0; i < mData.size(); i++) {
			final Data data = mData.get(i);
			if(data == firstItem) {
				continue;
			}
			if(data.getChildren() != null && data.getChildren().size() > 0) {
				if(data.getLevel() >= level) {
					collapseGroup(i);
				}
			}
		}

		//for(int i = 0; i < mData.size(); i++) {
		//	final Data data = mData.get(i);
		//	if(data.getChildren() != null && data.getChildren().size() > 0) {
		//		if(firstItem != data.getRoot() || data == firstItem) {
		//			continue;
		//		}
		//		if(data.getLevel() > level) {
		//			level = data.getLevel();
		//			//if(data.isGroup()) {
		//			//	expandGroup(i);
		//			//}
		//		} else {
		//			collapseGroup(i);
		//		}
		//	}
		//}
	}

	public void collapseExpandedGroup(int position) {
		if(position < 0) {
			return;
		}
		Data firstItem = getItemAt(position);

		if(firstItem.getChildren() == null || firstItem.getChildren().isEmpty() || firstItem.isGroup()) {
			return;
		}
		collapseGroup(position);
	}

	public void collapseGroup(int position) {
		if(position < 0) {
			return;
		}
		Data firstItem = getItemAt(position);

		if(firstItem.getChildren() == null || firstItem.getChildren().isEmpty()) {
			return;
		}

		// group containing all the descendants of firstItem
		List<Data> group = new ArrayList<Data>();
		// stack for depth first search
		List<Data> stack = new ArrayList<Data>();
		int groupSize = 0;

		for(int i = firstItem.getChildren().size() - 1; i >= 0; i--) {
			stack.add(firstItem.getChildren().get(i));
		}

		while(!stack.isEmpty()) {
			Data item = stack.remove(stack.size() - 1);
			group.add(item);
			groupSize++;
			// stop when the item is a leaf or a group
			if(item.getChildren() != null && !item.getChildren().isEmpty() && !item.isGroup()) {
				for(int i = item.getChildren().size() - 1; i >= 0; i--) {
					stack.add(item.getChildren().get(i));
				}
			}

			mData.remove(item);
		}

		mGroups.put(firstItem, group);
		firstItem.setIsGroup(true);

		notifyItemChanged(position);
		notifyItemRangeRemoved(position + 1, groupSize);
	}

	public void toggleGroup(int position) {
		if(getItemAt(position).isGroup()) {
			expandGroup(position);
		} else {
			collapseGroup(position);
		}
	}

	public ArrayList<Integer> saveGroups() {
		boolean notify = mNotifyOnChange;
		mNotifyOnChange = false;
		ArrayList<Integer> groupsIndices = new ArrayList<Integer>();
		for(int i = 0; i < mData.size(); i++) {
			if(mData.get(i).isGroup()) {
				expandGroup(i);
				groupsIndices.add(i);
			}
		}
		mNotifyOnChange = notify;
		return groupsIndices;
	}

	public void restoreGroups(List<Integer> groupsNum) {
		if(groupsNum == null) {
			return;
		}
		boolean notify = mNotifyOnChange;
		mNotifyOnChange = false;
		for(int i = groupsNum.size() - 1; i >= 0; i--) {
			collapseGroup(groupsNum.get(i));
		}
		mNotifyOnChange = notify;
	}

	public void collapseAll() {
		for(int i = 0; i < mData.size(); i++) {
			collapseGroup(i);
		}
	}

	public void collapseExpandedGroups() {
		for(int i = 0; i < mData.size(); i++) {
			collapseExpandedGroup(i);
		}
	}

	public int getItemPosition(final Data category) {
		return mData.indexOf(category);
	}
}