package com.omnom.android.view.subcategories;

import android.content.Context;
import android.graphics.Color;
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
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.OmnomRecyclerView;
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
		void onCollapsedSubcategoriesTouch(final MotionEvent e);
	}

	public static final int DURATION_ITEM_FLIP = 300;

	public static final int DURATION_ITEM_FLIP_STEP = 50;

	@InjectView(R.id.content_recyclerview)
	protected OmnomRecyclerView mListView;

	@InjectView(R.id.btn_close_menu)
	protected ImageView mImgClose;

	@InjectView(R.id.btn_search_menu)
	protected ImageView mImgSearch;

	@InjectView(R.id.menu_header)
	protected View mMenuHeader;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	@InjectView(R.id.view_sticky)
	protected View mFakeStickyHeader;

	private MenuSmoothScroller mSmoothScrollerTop;

	private HashMap<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder> headerStore =
			new HashMap<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder>();

	private UserOrder mOrder;

	private Menu mMenu;

	private MenuAdapter mMenuAdapter;

	private LinearLayoutManager mLayoutManager;

	private boolean mTouchEnabled = false;

	private MenuSmoothScroller mSmoothScroller;

	private HeaderItemDecorator mHeaderItemDecorator;

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

	private FlipInTopXDelayedAnimator mItemAnimator;

	private boolean mItemAnimationsSupported;

	private ItemTouchListenerBase mItemTouchListener;

	private OnCollapsedTouchListener mCollapsedTouchListener;

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

	public void resetState() {
		mMenuAdapter.resetState();
	}

	public void setOnCollapsedTouchListener(OnCollapsedTouchListener collapsedTouchListener) {
		mCollapsedTouchListener = collapsedTouchListener;
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.fragment_menu_subcategory, this);
		ButterKnife.inject(this);

		mItemAnimationsSupported = AndroidUtils.isRecyclerItemAnimationSupported();

		ViewUtils.setVisible(mFakeStickyHeader, false);

		mLayoutManager = new LinearLayoutManager(getContext());
		mSmoothScroller = new MenuSmoothScroller(getContext(), mLayoutManager, MenuSmoothScroller.MODE_DEFAULT);
		mSmoothScrollerTop = new MenuSmoothScroller(getContext(), mLayoutManager, MenuSmoothScroller.MODE_TOP);
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
				onGroupClick(v, false);
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
				mSmoothScrollerTop.setTargetPosition(position);
				mLayoutManager.startSmoothScroll(mSmoothScrollerTop);
				mMenuAdapter.setSelected(position);
				mMenuAdapter.notifyItemChanged(position);
				showAddFragment((Item) v.getTag(), position);
			}
		});
		mMenuAdapter.setHasStableIds(true);

		mListView.setAdapter(mMenuAdapter);
		mHeaderItemDecorator = new HeaderItemDecorator(this, mListView, mMenuAdapter, headerStore);
		mMenuAdapter.setDecorator(mHeaderItemDecorator);
		mItemAnimator = new FlipInTopXDelayedAnimator(DURATION_ITEM_FLIP, DURATION_ITEM_FLIP_STEP);
		mListView.addItemDecoration(mHeaderItemDecorator);
		if(mItemAnimationsSupported) {
			mListView.setItemAnimator(mItemAnimator);
		}
		mListView.setOverScrollMode(OVER_SCROLL_NEVER);

		mMenuAdapter.addAll(menuData.getData());
		mMenuAdapter.collapseAll();
		mPanelBottom.setBackgroundColor(Color.TRANSPARENT);
		final GestureDetector gestureDetector = new GestureDetector(getActivity(), new SingleTapDetector(this, mListView, mMenuAdapter));
		mItemTouchListener = ItemTouchListenerFactory.create(this, gestureDetector, mCollapsedTouchListener);
		mListView.addOnItemTouchListener(mItemTouchListener);
	}

	protected void restoreHeaderView(final RecyclerView.ViewHolder holder) {
		mHeaderItemDecorator.restoreHeaderView(holder);
	}

	protected final void onGroupClick(final View v, boolean force) {
		if(!mTouchEnabled && !force) {
			return;
		}

		final int childPosition = mListView.getChildPosition(v);
		final MultiLevelRecyclerAdapter.Data category = mMenuAdapter.getItemAt(childPosition);
		final boolean isGroup = category.isGroup();
		if(!isGroup) {
			final RecyclerView.ViewHolder holder = mListView.getChildViewHolder(v);
			restoreHeaderView(holder);
			mMenuAdapter.notifyDataSetChanged();
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
		if(!mMenuAdapter.hasExpandedGroups()) {
			ViewUtils.setVisible(mFakeStickyHeader, false);
		}
		restoreHeadersStyle();
	}

	private void showDetails(final View v) {
		final int childPosition = mListView.getChildPosition(v);
		final ItemData itemData = (ItemData) mMenuAdapter.getItemAt(childPosition);
		final int top = v.getTop();

		final View btnApply = findById(v, R.id.btn_apply);
		final int position = (Integer) btnApply.getTag(R.id.position);
		final View panelDescription = findById(v, R.id.panel_description);
		final int descrTop = panelDescription.getTop();
		final int btnMarginTop = getApplyMarginTop(btnApply, panelDescription, descrTop);

		if(top != 0) {
			mSmoothScroller.setTargetPosition(childPosition);
			mLayoutManager.startSmoothScroll(mSmoothScroller);
			postDelayed(new Runnable() {
				@Override
				public void run() {
					final int btnTranslation = btnApply.getTop() - findById(v, R.id.txt_title).getTop();
					final int contentTranslation = v.getTop();
					showDetails(itemData.getItem(), position, findById(v, R.id.txt_title).getHeight(), contentTranslation,
					            btnTranslation, btnMarginTop);
				}
			}, getResources().getInteger(R.integer.default_animation_duration_short));
		} else {
			final int btnTranslation = btnApply.getTop() - findById(v, R.id.txt_title).getTop();
			showDetails(itemData.getItem(), position, findById(v, R.id.txt_title).getHeight(), 0, btnTranslation, btnMarginTop);
		}
	}

	private int getApplyMarginTop(final View btnApply, final View panelDescription, final int descrTop) {
		int btnMarginTop = 0;
		if(panelDescription.getVisibility() == View.VISIBLE) {
			btnMarginTop = btnApply.getTop() - descrTop - ((MarginLayoutParams) btnApply.getLayoutParams()).topMargin;
		}
		return btnMarginTop;
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

	public void showDetails(final Item item, final int position, final int titleSize, final int contentTranslation, final int
			btnTranslation, final int btnMarginTop) {
		if(item == null) {
			return;
		}
		if(!(item instanceof MenuCategoryItems.HeaderItem) && !(item instanceof MenuCategoryItems.SubHeaderItem)) {
			MenuItemDetailsFragment.show(getFragmentManager(), mMenu, mOrder, item, position, titleSize,
			                             contentTranslation,
			                             btnTranslation,
			                             btnMarginTop);
		}
	}

	public void showDetails(final Item item) {
		if(item == null) {
			return;
		}
		if(!(item instanceof MenuCategoryItems.HeaderItem) && !(item instanceof MenuCategoryItems.SubHeaderItem)) {
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
		mItemTouchListener.setTouchEnabled(mTouchEnabled);
		AnimationUtils.animateAlpha3(mImgClose, false);
		AnimationUtils.animateAlpha3(mImgSearch, false);
	}

	@Override
	public void onPanelExpanded(final View panel) {
		mTouchEnabled = true;
		mItemTouchListener.setTouchEnabled(mTouchEnabled);
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

	private ValidateActivity getActivity() {
		return ((ValidateActivity) getContext());
	}

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
					mMenuAdapter
							.insert(position, i, itemData, new ItemData(itemData.getParent(), recommendation, ItemData.getType(i, size)));
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

	public void collapseInstant() {
		restoreHeadersStyle();
		ViewUtils.setVisible(mImgClose, false);
		ViewUtils.setVisible(mImgSearch, false);
	}

}
