package com.omnom.android.view.subcategories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.activity.validate.ValidateActivity;
import com.omnom.android.adapter.MenuCategoryItems;
import com.omnom.android.adapter.MultiLevelRecyclerAdapter;
import com.omnom.android.fragment.menu.CategoryData;
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
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.MenuSmoothScroller;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.FlipInTopXDelayedAnimator;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 26.02.2015.
 */
public class SubcategoriesView extends RelativeLayout implements SlidingUpPanelLayout.PanelSlideListener {

	public interface OnCollapsedTouchListener {
		void onCollapsedSubcategoriesTouch();
	}

	public static final int DURATION_ITEM_FLIP = 300;

	public static final int DURATION_ITEM_FLIP_STEP = 50;

	private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
			super.onScrollStateChanged(recyclerView, newState);
		}

		@Override
		public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
			for(Map.Entry<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder> entry : headerStore.entrySet()) {
				mHeaderItemDecorator.applyHeaderStyle(entry.getValue());
			}
		}
	};

	@InjectView(R.id.content_recyclerview)
	protected RecyclerView mListView;

	@InjectView(R.id.btn_close_menu)
	protected ImageView mImgClose;

	@InjectView(R.id.btn_search_menu)
	protected ImageView mImgSearch;

	@InjectView(R.id.menu_header)
	protected View mMenuHeader;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	@InjectView(R.id.view_subheader)
	protected View mFakeStickyHeader;

	private HashMap<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder> headerStore =
			new HashMap<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder>();

	private UserOrder mOrder;

	private Menu mMenu;

	private MenuAdapter mMenuAdapter;

	private LinearLayoutManager mLayoutManager;

	private boolean mTouchEnabled = false;

	private OnCollapsedTouchListener mCollapsedTouchListener;

	private MenuSmoothScroller mSmoothScroller;

	private GestureDetector mGestureDetector;

	private HeaderItemDecorator mHeaderItemDecorator;

	@SuppressWarnings("UnusedDeclaration")
	public SubcategoriesView(Context context) {
		super(context);
		init();
	}

	@SuppressWarnings("UnusedDeclaration")
	public SubcategoriesView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@SuppressWarnings("UnusedDeclaration")
	public SubcategoriesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setOnCollapsedTouchListener(OnCollapsedTouchListener collapsedTouchListener) {
		mCollapsedTouchListener = collapsedTouchListener;
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.fragment_menu_subcategory, this);
		ButterKnife.inject(this);

		ViewUtils.setVisible(mFakeStickyHeader, false);

		mLayoutManager = new LinearLayoutManager(getContext());
		mSmoothScroller = new MenuSmoothScroller(getContext(), mLayoutManager, MenuSmoothScroller.MODE_DEFAULT);
		mListView.setLayoutManager(mLayoutManager);
		mListView.setRecyclerListener(new RecyclerView.RecyclerListener() {
			@Override
			public void onViewRecycled(final RecyclerView.ViewHolder holder) {
				mHeaderItemDecorator.restoreHeaderView(holder);
			}
		});
		mListView.setOnScrollListener(mOnScrollListener);
		mListView.setHasFixedSize(true);
	}

	public void bind(Menu menu, UserOrder order) {
		mMenu = menu;
		mOrder = order;

		final MenuData menuData = new MenuData(mMenu, mOrder);
		OnClickListener mGroupClickListener = new OnClickListener() {
			@Override
			public void onClick(final View v) {
				onGroupClick(v);
			}
		};
		mMenuAdapter = new MenuAdapter(getContext(), mMenu, mOrder, headerStore, mGroupClickListener, new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showDetails(v);
			}
		}, new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final int position = (Integer) v.getTag(R.id.position);
				showAddFragment((Item) v.getTag(), position);
			}
		});
		mGestureDetector = new GestureDetector(getActivity(), new SingleTapDetector(this, mListView, mMenuAdapter));

		mListView.setAdapter(mMenuAdapter);
		mHeaderItemDecorator = new HeaderItemDecorator(this, mListView, mMenuAdapter, headerStore);
		mListView.addItemDecoration(mHeaderItemDecorator);
		mListView.setItemAnimator(new FlipInTopXDelayedAnimator(DURATION_ITEM_FLIP, DURATION_ITEM_FLIP_STEP));
		mListView.setOverScrollMode(OVER_SCROLL_NEVER);

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
				} else {
					return mGestureDetector.onTouchEvent(e);
				}
				return !mTouchEnabled;
			}

			@Override
			public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
				// Do nothing
			}
		});
	}

	protected void restoreHeaderView(final RecyclerView.ViewHolder holder) {
		mHeaderItemDecorator.restoreHeaderView(holder);
	}

	private void onGroupClick(final View v) {
		final int childPosition = mListView.getChildPosition(v);
		final MultiLevelRecyclerAdapter.Data category = mMenuAdapter.getItemAt(childPosition);
		final boolean isGroup = category.isGroup();
		if(!isGroup) {
			final RecyclerView.ViewHolder holder = mListView.getChildViewHolder(v);
			restoreHeaderView(holder);
		}

		mMenuAdapter.toggleGroup(childPosition, new MultiLevelRecyclerAdapter.DataFilter() {
			@Override
			public boolean filter(final MultiLevelRecyclerAdapter.Data data) {
				if(category instanceof CategoryData) {
					CategoryData cd = (CategoryData) category;
					return data.getLevel() == cd.getLevel() + 1;
				}
				return false;
			}
		});
		final int newPos = mMenuAdapter.getItemPosition(category);
		mLayoutManager.scrollToPositionWithOffset(newPos, 0);
		mMenuAdapter.notifyItemChanged(newPos);
		postDelayed(new Runnable() {
			@Override
			public void run() {
				if(isGroup && mLayoutManager.getDecoratedTop(v) == 0) {
					mHeaderItemDecorator.animateHeaderStyle(mListView.getChildViewHolder(v));
				}
			}
		}, getResources().getInteger(R.integer.default_animation_duration_medium));
		if(!mMenuAdapter.hasExpandedGroups()) {
			ViewUtils.setVisible(mFakeStickyHeader, false);
		}
	}

	private void showDetails(final View v) {
		final int childPosition = mListView.getChildPosition(v);
		final ItemData itemData = (ItemData) mMenuAdapter.getItemAt(childPosition);
		final int top = v.getTop();

		final View btnApply = findById(v, R.id.btn_apply);
		final int position = (Integer) btnApply.getTag(R.id.position);

		if(top != 0) {
			mSmoothScroller.setTargetPosition(childPosition);
			mLayoutManager.startSmoothScroll(mSmoothScroller);
			postDelayed(new Runnable() {
				@Override
				public void run() {
					final int btnTranslation = btnApply.getTop() - findById(v, R.id.txt_title).getTop();
					final int contentTranslation = v.getTop();
					showDetails(itemData.getItem(), position, findById(v, R.id.txt_title).getHeight(), contentTranslation, btnTranslation);
				}
			}, getResources().getInteger(R.integer.default_animation_duration_short));
		} else {
			final int btnTranslation = btnApply.getTop() - findById(v, R.id.txt_title).getTop();
			showDetails(itemData.getItem(), position, findById(v, R.id.txt_title).getHeight(), 0, btnTranslation);
		}
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

	public void showDetails(final Item item, final int position, final int titleSize, final int contentTranslation,
	                        final int btnTranslation) {
		if(item == null) {
			return;
		}
		if(!(item instanceof MenuCategoryItems.HeaderItem) &&
				!(item instanceof MenuCategoryItems.SubHeaderItem)) {
			MenuItemDetailsFragment.show(getFragmentManager(), mMenu, mOrder, item, position,
			                             titleSize,
			                             contentTranslation,
			                             btnTranslation);
		}
	}

	public void showDetails(final Item item) {
		if(item == null) {
			return;
		}
		if(!(item instanceof MenuCategoryItems.HeaderItem) &&
				!(item instanceof MenuCategoryItems.SubHeaderItem)) {
			final int position = mMenuAdapter.getItemPosition(item);
			MenuItemDetailsFragment.show(getFragmentManager(), mMenu, mOrder, item, position);
		}
	}

	@Override
	public void onPanelSlide(final View panel, final float slideOffset) {
	}

	@Override
	public void onPanelCollapsed(final View panel) {
		mTouchEnabled = false;
		AnimationUtils.animateAlpha3(mImgClose, false);
		AnimationUtils.animateAlpha3(mImgSearch, false);
	}

	@Override
	public void onPanelExpanded(final View panel) {
		mTouchEnabled = true;
		AnimationUtils.animateAlpha3(mImgClose, true);
		AnimationUtils.animateAlpha3(mImgSearch, true);

	}

	@OnClick(R.id.menu_header)
	public void onMenuHeader() {
		if(SlidingUpPanelLayout.PanelState.EXPANDED == getActivity().getSlidingPanelState()) {
			onClose();
		} else {
			getActivity().expandSlidingPanel();
		}
	}

	@OnClick(R.id.btn_close_menu)
	public void onClose() {
		mHeaderItemDecorator.restoreHeadersStyle();
		ViewUtils.setVisible(mFakeStickyHeader, false);
		getActivity().collapseSlidingPanel();
		AnimationUtils.animateAlpha3(mImgClose, false);
		AnimationUtils.animateAlpha3(mImgSearch, false);
	}

	@OnClick(R.id.btn_search_menu)
	public void onSearch() {
		getActivity().showSearchFragment();
	}

	private ValidateActivity getActivity() {return ((ValidateActivity) getContext());}

	@Override
	public void onPanelAnchored(final View panel) {
		// Do nothing
	}

	@Override
	public void onPanelHidden(final View panel) {
		// Do nothing
	}

	public void refresh(final OrderUpdateEvent event) {
		if(event == null || event.getItem() == null) {
			return;
		}
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

	public void collapse() {
		mMenuAdapter.collapseExpandedGroups();
	}

	public RecyclerView.ViewHolder getFakeHeader() {
		return mHeaderItemDecorator.getFakeHeader();
	}

	public boolean hasExpandedGroups() {
		return mMenuAdapter.hasExpandedGroups();
	}

	public void restoreHeadersStyle() {
		ViewUtils.setVisible(mFakeStickyHeader, false);
		mHeaderItemDecorator.restoreHeadersStyle();
	}
}
