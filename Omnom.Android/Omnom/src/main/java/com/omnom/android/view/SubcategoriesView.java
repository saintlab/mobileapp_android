package com.omnom.android.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.omnom.android.R;
import com.omnom.android.activity.ValidateActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.adapter.MultiLevelRecyclerAdapter;
import com.omnom.android.fragment.menu.ItemData;
import com.omnom.android.fragment.menu.MenuAdapter;
import com.omnom.android.fragment.menu.MenuData;
import com.omnom.android.fragment.menu.MenuItemAddFragment;
import com.omnom.android.fragment.menu.MenuItemDetailsFragment;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.utils.utils.AnimationUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hugo.weaving.DebugLog;

/**
 * Created by Ch3D on 26.02.2015.
 */
public class SubcategoriesView extends RelativeLayout implements SlidingUpPanelLayout.PanelSlideListener {

	public interface OnCollapsedTouchListener {
		public void onCollapsedSubcategoriesTouch();
	}

	@InjectView(R.id.content_recyclerview)
	protected RecyclerView mListView;

	@InjectView(R.id.btn_close_menu)
	protected ImageView mImgClose;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	private UserOrder mOrder;

	private Menu mMenu;

	private MenuAdapter mMenuAdapter;

	private LinearLayoutManager mLayoutManager;

	private View.OnClickListener mGroupClickListener;

	private ArgbEvaluator mBackgroundEvaluator;

	private boolean mTouchEnabled = false;

	private OnCollapsedTouchListener mCollapsedTouchListener;

	@SuppressWarnings("UnusedDeclaration")
	public SubcategoriesView(Context context) {
		super(context);
		init(null);
	}

	@SuppressWarnings("UnusedDeclaration")
	public SubcategoriesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	@SuppressWarnings("UnusedDeclaration")
	public SubcategoriesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public void setOnCollapsedTouchListener(OnCollapsedTouchListener collapsedTouchListener) {
		mCollapsedTouchListener = collapsedTouchListener;
	}

	private void init(final AttributeSet attrs) {
		LayoutInflater.from(getContext()).inflate(R.layout.fragment_menu_subcategory, this);
		ButterKnife.inject(this);

		mBackgroundEvaluator = new ArgbEvaluator();

		mListView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mListView.setLayoutManager(mLayoutManager);
	}

	public void bind(Menu menu, UserOrder order) {
		mMenu = menu;
		mOrder = order;

		final MenuData menuData = new MenuData(mMenu, mOrder);
		mGroupClickListener = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final int childPosition = mListView.getChildPosition(v);
				final MultiLevelRecyclerAdapter.Data category = mMenuAdapter.getItemAt(childPosition);
				mMenuAdapter.toggleGroup(childPosition);

				final int newPos = mMenuAdapter.getItemPosition(category);
				mLayoutManager.scrollToPositionWithOffset(newPos, 0);
				mMenuAdapter.notifyItemChanged(newPos);
			}
		};
		mMenuAdapter = new MenuAdapter(getContext(), mMenu, mOrder, mGroupClickListener, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				int position = mListView.getChildPosition(v);
				final ItemData itemData = (ItemData) mMenuAdapter.getItemAt(position);
				showDetails(itemData.getItem());
			}
		}, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final int position = (Integer) v.getTag(R.id.position);
				showAddFragment((Item) v.getTag(), position);
			}
		});
		mListView.setAdapter(mMenuAdapter);
		mMenuAdapter.addAll(menuData.getData());
		mMenuAdapter.collapseAll();
		mPanelBottom.setBackgroundColor(Color.TRANSPARENT);

		mListView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
			public Point mPoint = new Point();

			@Override
			public boolean onInterceptTouchEvent(final RecyclerView rv, final MotionEvent e) {
				if(!mTouchEnabled) {
					switch(e.getAction()) {
						case MotionEvent.ACTION_DOWN:
							mPoint.set((int) e.getX(), (int) e.getY());
							break;

						case MotionEvent.ACTION_UP:
							if(mPoint.equals(new Point((int) e.getX(), (int) e.getY()))) {
								if(mCollapsedTouchListener != null) {
									mCollapsedTouchListener.onCollapsedSubcategoriesTouch();
								}
							}
							mPoint.set(-1, -1);
							break;
					}
				}
				return !mTouchEnabled;
			}

			@Override
			public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
				// Do nothing
			}
		});
	}

	public void showAddFragment(final Item item, int pos) {
		if(item == null) {
			return;
		}
		MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, item, pos);
	}

	private FragmentManager getFragmentManager() {
		BaseOmnomFragmentActivity activity = (BaseOmnomFragmentActivity) getContext();
		return activity.getSupportFragmentManager();
	}

	private void showDetails(final Item item) {
		if(item == null) {
			return;
		}
		if(!(item instanceof MenuCategoryItemsAdapter.HeaderItem) &&
				!(item instanceof MenuCategoryItemsAdapter.SubHeaderItem)) {
			MenuItemDetailsFragment.show(getFragmentManager(), mMenu, mOrder, item);
		}
	}

	@Override
	@DebugLog
	public void onPanelSlide(final View panel, final float slideOffset) {
		//mMenuAdapter.setTextColor((int) mBackgroundEvaluator.evaluate(slideOffset, Color.WHITE, Color.BLACK));
		//mMenuAdapter.setCategoriesBackground((int) mBackgroundEvaluator.evaluate(slideOffset, Color.TRANSPARENT,
		//                                                                         getResources().getColor(
		//		                                                                         R.color.panel_background_grey_light)));
		//mPanelBottom.setBackgroundColor((Integer) mBackgroundEvaluator.evaluate(slideOffset, Color.TRANSPARENT, Color.WHITE));
	}

	@Override
	public void onPanelCollapsed(final View panel) {
		mTouchEnabled = false;
		AnimationUtils.animateAlpha3(mImgClose, false);
	}

	@Override
	public void onPanelExpanded(final View panel) {
		mTouchEnabled = true;
		AnimationUtils.animateAlpha3(mImgClose, true);

	}

	@OnClick(R.id.btn_close_menu)
	public void onClose() {
		mMenuAdapter.collapseExpandedGroups();
		getActivity().collapseSlidingPanel();
		AnimationUtils.animateAlpha3(mImgClose, false);
	}

	private ValidateActivity getActivity() {return ((ValidateActivity) getContext());}

	@Override
	public void onPanelAnchored(final View panel) {

	}

	@Override
	public void onPanelHidden(final View panel) {

	}

	public void refresh(final OrderUpdateEvent event) {
		final Item item = event.getItem();
		mOrder.addItem(item, event.getCount());
		mMenuAdapter.notifyItemChanged(event.getPosition());

		if(event.getPosition() > 0 && item.hasRecommendations()) {
			if(event.getCount() > 0) {
				final List<String> recommendations = item.recommendations();
				final int size = recommendations.size();
				for(int i = 0; i < size; i++) {
					final String id = recommendations.get(i);
					final Item recommendation = mMenu.findItem(id);
					final int indexIncrement = i + 1;
					final int position = event.getPosition() + indexIncrement;
					final MultiLevelRecyclerAdapter.Data itemData = mMenuAdapter.getItemAt(position);
					mMenuAdapter.insert(position, i, itemData, new ItemData(itemData.getParent(),
					                                                        recommendation,
					                                                        ItemData.getType(i, size)));
				}
			} else {
				for(final String id : item.recommendations()) {
					final int position = event.getPosition() + 1;
					final MultiLevelRecyclerAdapter.Data itemData = mMenuAdapter.getItemAt(position);
					mMenuAdapter.remove(itemData);
				}
			}
		}
	}

	public void onResume() {
		if(mMenuAdapter != null) {
			mMenuAdapter.notifyDataSetChanged();
		}
	}
}
