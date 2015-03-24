package com.omnom.android.fragment.menu;

import android.support.annotation.Nullable;

import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;

import java.util.ArrayList;
import java.util.List;

import static com.omnom.android.adapter.MultiLevelRecyclerAdapter.Data;

/**
 * Created by Ch3D on 20.02.2015.
 */
public class CategoryData implements Data {

	private Data mParent;

	private Menu mMenu;

	private UserOrder mOrder;

	private Category mCategory;

	private int mLevel;

	private ArrayList<Data> mChildren;

	private boolean mIsGroup = true;

	public CategoryData(Data parent, Menu menu, UserOrder order, Category category, int level) {
		mParent = parent;
		mMenu = menu;
		mOrder = order;
		mCategory = category;
		mLevel = level;
	}

	@Override
	public List<? extends Data> getChildren() {
		return prepareChildren();
	}

	protected List<? extends Data> prepareChildren() {
		if(mChildren == null) {
			mChildren = new ArrayList<Data>();
			if(mCategory.items() != null) {
				for(String id : mCategory.items()) {
					final Item item = mMenu.findItem(id);
					mChildren.add(new ItemData(this, item));
					if(mOrder.contains(item) && item.hasRecommendations()) {
						addRecommendations(item);
					}
				}
			}
			if(mCategory.children() != null) {
				for(Category subCategory : mCategory.children()) {
					final CategoryData categoryData = new CategoryData(this, mMenu, mOrder, subCategory, mLevel + 1);
					categoryData.prepareChildren();
					mChildren.add(categoryData);
				}
			}
		}
		return mChildren;
	}

	private void addRecommendations(final Item item) {
		final List<String> recommendations = item.recommendations();
		for(int i = 0; i < recommendations.size(); i++) {
			final String recId = recommendations.get(i);
			final Item recommendation = mMenu.findItem(recId);
			mChildren.add(new ItemData(this, recommendation, ItemData.getType(i, recommendations.size())));
		}
	}

	@Override
	public boolean isGroup() {
		return mIsGroup;
	}

	@Override
	@Nullable
	public Data getParent() {
		return mParent;
	}

	@Override
	public Data getRoot() {
		if(mParent == null) {
			return this;
		}
		return mParent.getRoot();
	}

	@Override
	public void setIsGroup(final boolean value) {
		mIsGroup = value;
	}

	@Override
	public void add(final Data anchor, final Data newItem, final int indexIncrement) {
		mChildren.add(mChildren.indexOf(anchor), newItem);
	}

	@Override
	public void remove(final Data item) {
		mChildren.remove(item);
	}

	public String getName() {
		return mCategory.name();
	}

	@Override
	public int getLevel() {
		return mLevel;
	}
}
