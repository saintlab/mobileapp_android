package com.omnom.android.view.subcategories;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.adapter.MultiLevelRecyclerAdapter;
import com.omnom.android.fragment.menu.ItemData;
import com.omnom.android.fragment.menu.MenuAdapter;
import com.omnom.android.utils.utils.ViewUtils;

/**
 * Created by Ch3D on 17.03.2015.
 */
class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {

	private SubcategoriesView mSubcategoriesView;

	private RecyclerView mListView;

	private MenuAdapter mMenuAdapter;

	public SingleTapDetector(final SubcategoriesView subcategoriesView, final RecyclerView view, MenuAdapter menuAdapter) {
		mSubcategoriesView = subcategoriesView;
		mListView = view;
		mMenuAdapter = menuAdapter;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		if(e.getY() >= mSubcategoriesView.getResources().getDimensionPixelSize(R.dimen.view_size_default)) {
			return false;
		}
		final View firstChild = mListView.getChildAt(0);
		final int childPosition = mListView.getChildPosition(firstChild);
		final MultiLevelRecyclerAdapter.Data item = mMenuAdapter.getItemAt(childPosition);
		if(item instanceof ItemData) {
			final MultiLevelRecyclerAdapter.Data parent = item.getParent();
			final int headerPosition = mMenuAdapter.getItemPosition(parent);
			mMenuAdapter.collapseExpandedGroup(headerPosition);
			mSubcategoriesView.restoreHeaderView(mSubcategoriesView.getFakeHeader());
			final View child = mListView.getChildAt(headerPosition);
			if(child != null) {
				mSubcategoriesView.restoreHeaderView(mListView.getChildViewHolder(child));
			}
			if(!mMenuAdapter.hasExpandedGroups()) {
				ViewUtils.setVisible(mSubcategoriesView.mFakeStickyHeader, false);
			}
			return true;
		}
		return false;
	}
}
