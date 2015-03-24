package com.omnom.android.view.subcategories;

import android.view.GestureDetector;

import com.omnom.android.utils.utils.AndroidUtils;

/**
 * Created by Ch3D on 24.03.2015.
 */
public class ItemTouchListenerFactory {
	public static ItemTouchListenerBase create(SubcategoriesView view, final GestureDetector gestureDetector, final SubcategoriesView
			.OnCollapsedTouchListener collapsedTouchListener) {
		if(AndroidUtils.isJellyBean()) {
			return new ItemTouchListenerJB(view, gestureDetector, collapsedTouchListener);
		}
		return new ItemTouchListenerICS(view, gestureDetector, collapsedTouchListener);
	}
}
