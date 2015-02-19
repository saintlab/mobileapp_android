package com.omnom.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.omnom.android.R;
import com.omnom.android.activity.ValidateActivity;
import com.omnom.android.adapter.MenuCategoriesAdapter;
import com.omnom.android.fragment.menu.MenuSubcategoryFragment;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.OmnomListView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 17.02.2015.
 */
public class MenuCategoriesView extends FrameLayout implements SlidingUpPanelLayout.PanelSlideListener, android.support.v4.app
		.FragmentManager.OnBackStackChangedListener {

	public interface SlideListener {
		public void onPanelSlide(final View panel, final float slideOffset);
	}

	public static final int DURATION_INCREMENT_MOVE_UP = 75;

	public static final int DURATION_INCREMENT_MOVE_DOWN = 50;

	public static final int TITLE_PADDING_TOP = 8;

	private static final String TAG = MenuCategoriesView.class.getSimpleName();

	@InjectView(android.R.id.list)
	protected OmnomListView mList;

	private SlideListener mListener;

	private int mScreenHeight;

	private int mCategoryItemHeight;

	private int mTitleAnimationPadding;

	private LinearInterpolator mInterpolator;

	private View mSelectedView;

	private Menu mMenu;

	@SuppressWarnings("UnusedDeclaration")
	public MenuCategoriesView(Context context) {
		super(context);
		init(null);
	}

	@SuppressWarnings("UnusedDeclaration")
	public MenuCategoriesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	@SuppressWarnings("UnusedDeclaration")
	public MenuCategoriesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public void setSlideListener(SlideListener listener) {
		mListener = listener;
	}

	private void init(final AttributeSet attrs) {
		LayoutInflater.from(getContext()).inflate(R.layout.view_menu_categories, this);
		ButterKnife.inject(this);

		final ValidateActivity validateActivity = (ValidateActivity) getContext();
		getFragmentManager().addOnBackStackChangedListener(this);

		mScreenHeight = getResources().getDisplayMetrics().heightPixels;
		mCategoryItemHeight = getResources().getDimensionPixelSize(R.dimen.menu_category_height);
		mTitleAnimationPadding = ViewUtils.dipToPixels(getContext(), TITLE_PADDING_TOP);
		mInterpolator = new LinearInterpolator();

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				mSelectedView = mList.getChildAt(position);

				final float heightPixels = mList.getContext().getResources().getDisplayMetrics().heightPixels;
				final int anchorY = mSelectedView.getBottom() + (mCategoryItemHeight / 2);
				final int ty = -mSelectedView.getBottom() + mCategoryItemHeight + mTitleAnimationPadding;

				mSelectedView.animate().translationY(ty)
				             .setInterpolator(mInterpolator)
				             .setDuration(getResources().getInteger(R.integer.default_animation_duration_short)
						                          + DURATION_INCREMENT_MOVE_UP)
				             .start();

				for(int i = position + 1; i <= mList.getLastVisiblePosition(); i++) {
					final View v = mList.getChildAt(i);
					v.animate().translationY(mScreenHeight - v.getBottom()).alpha(0)
					 .setInterpolator(mInterpolator)
					 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_UP)
					 .start();
				}

				for(int i = mList.getFirstVisiblePosition(); i <= position - 1; i++) {
					final View v = mList.getChildAt(i);
					v.animate().translationY(-(v.getTop() + mCategoryItemHeight)).alpha(0)
					 .setInterpolator(mInterpolator)
					 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_UP)
					 .start();
				}

				final float v = anchorY / heightPixels;

				MenuSubcategoryFragment.show(getFragmentManager(), validateActivity.getUserOrder(), mMenu,
				                             position - mList.getHeaderViewsCount(), v);
			}
		});
	}

	@Override
	public void onPanelSlide(final View panel, final float slideOffset) {
		if(mListener != null) {
			mListener.onPanelSlide(panel, slideOffset);
		}
	}

	@Override
	public void onPanelCollapsed(final View panel) {
		mList.setEnabled(false);
	}

	@Override
	public void onPanelExpanded(final View panel) {
		mList.setEnabled(true);
	}

	@Override
	public void onPanelAnchored(final View panel) {

	}

	@Override
	public void onPanelHidden(final View panel) {

	}

	public void bindData(final Menu menu) {
		if(menu == null || menu.isEmpty()) {
			Log.d(TAG, "Skip empty menu binding");
			return;
		}
		mMenu = menu;
		View header = LayoutInflater.from(getContext()).inflate(R.layout.item_menu_categories_header, null);
		mList.addHeaderView(header, null, false);
		mList.setAdapter(new MenuCategoriesAdapter(getContext(), menu.categories()));
		mList.setEnabled(false);
	}

	public android.support.v4.app.FragmentManager getFragmentManager() {
		final ValidateActivity validateActivity = (ValidateActivity) getContext();
		return validateActivity.getSupportFragmentManager();
	}

	@Override
	public void onBackStackChanged() {
		if(getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() == 0) {
			final int positionForView = mList.getPositionForView(mSelectedView);

			for(int i = positionForView + 1; i <= mList.getLastVisiblePosition(); i++) {
				final View v = mList.getChildAt(i);
				v.animate().translationY(0).alpha(1)
				 .setInterpolator(mInterpolator)
				 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_UP)
				 .start();
			}

			for(int i = 0; i <= positionForView - 1; i++) {
				final View v = mList.getChildAt(i);
				v.animate().translationY(0).alpha(1)
				 .setInterpolator(mInterpolator)
				 .setDuration(getResources().getInteger(R.integer.default_animation_duration_short) + DURATION_INCREMENT_MOVE_DOWN)
				 .start();
			}

			mSelectedView.animate().translationY(0)
			             .setInterpolator(mInterpolator)
			             .setDuration(getResources().getInteger(R.integer.default_animation_duration_short)
					                          + DURATION_INCREMENT_MOVE_DOWN).start();
		}
	}
}
