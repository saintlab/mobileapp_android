package com.omnom.android.fragment.menu;

import android.support.annotation.Nullable;

import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Menu;

import java.util.ArrayList;
import java.util.List;

import static com.omnom.android.adapter.MultiLevelRecyclerAdapter.Data;

/**
 * Created by Ch3D on 20.02.2015.
 */
public class CategoryData implements Data {

	private Data mParent;

	private Menu mMenu;

	private Category mCategory;

	private int mLevel;

	private ArrayList<Data> mChildren;

	private boolean mIsGroup;

	public CategoryData(Data parent, Menu menu, Category category, int level) {
		mParent = parent;
		mMenu = menu;
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
					mChildren.add(new ItemData(this, mMenu.findItem(id)));
				}
			}
			if(mCategory.children() != null) {
				for(Category subCategory : mCategory.children()) {
					final CategoryData categoryData = new CategoryData(this, mMenu, subCategory, mLevel + 1);
					categoryData.prepareChildren();
					mChildren.add(categoryData);
				}
			}
		}
		return mChildren;
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

	public int getLevel() {
		return mLevel;
	}
}
