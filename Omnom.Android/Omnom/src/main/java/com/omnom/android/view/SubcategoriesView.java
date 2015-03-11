package com.omnom.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.ValidateActivity;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;

/**
 * Created by Ch3D on 26.02.2015.
 */
public class SubcategoriesView extends RelativeLayout implements SlidingUpPanelLayout.PanelSlideListener {

	public interface OnCollapsedTouchListener {
		void onCollapsedSubcategoriesTouch();
	}

	private static final boolean DEBUG_HEADERS = true;

	private class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {

		public SingleTapDetector() {
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if(e.getY() >= 96) {
				return false;
			}
			final View firstChild = mListView.getChildAt(0);
			final int childPosition = mListView.getChildPosition(firstChild);
			final MultiLevelRecyclerAdapter.Data item = mMenuAdapter.getItemAt(childPosition);
			if(item instanceof ItemData) {
				final MultiLevelRecyclerAdapter.Data parent = item.getParent();
				final int headerPosition = mMenuAdapter.getItemPosition(parent);
				mMenuAdapter.collapseExpandedGroup(headerPosition);
				return true;
			}
			return false;
		}
	}

	private HashMap<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder> headerStore =
			new HashMap<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder>();

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

	private boolean mTouchEnabled = false;

	private OnCollapsedTouchListener mCollapsedTouchListener;

	private RecyclerView.ViewHolder mFakeHeader;

	private int mLastBottom = Integer.MAX_VALUE;

	private GestureDetector gd;

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

		gd = new GestureDetector(getActivity(), new SingleTapDetector());

		mListView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getContext());
		mListView.setLayoutManager(mLayoutManager);
	}

	public void bind(Menu menu, UserOrder order) {
		mMenu = menu;
		mOrder = order;

		final MenuData menuData = new MenuData(mMenu, mOrder);
		OnClickListener mGroupClickListener = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final int childPosition = mListView.getChildPosition(v);
				final MultiLevelRecyclerAdapter.Data category = mMenuAdapter.getItemAt(childPosition);
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
		final FlipInTopXAnimator animator = new FlipInTopXAnimator();
		animator.setAddDuration(500);
		animator.setRemoveDuration(500);
		mListView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void onDrawOver(final Canvas c, final RecyclerView parent, final RecyclerView.State state) {
				super.onDrawOver(c, parent, state);
				drawHeaders(c, parent, state);
			}

			@Override
			public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, final RecyclerView.State state) {
			}
		});
		mListView.setItemAnimator(animator);

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
					return gd.onTouchEvent(e);
				}
				return !mTouchEnabled;
			}

			@Override
			public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {
				// Do nothing
			}
		});
	}

	private int getHeaderY(View item, RecyclerView.LayoutManager lm) {
		return lm.getDecoratedTop(item) < 0 ? 0 : lm.getDecoratedTop(item);
	}

	private void drawHeaders(final Canvas c, final RecyclerView parent, final RecyclerView.State state) {
		final RecyclerView.LayoutManager lm = parent.getLayoutManager();

		View child = parent.getChildAt(0);
		final View nextChild = parent.getChildAt(1);
		RecyclerView.ViewHolder childViewHolder = parent.getChildViewHolder(child);
		RecyclerView.ViewHolder nextChildViewHolder = parent.getChildViewHolder(nextChild);
		final boolean nextIsHeader = nextChildViewHolder instanceof MenuAdapter.CategoryViewHolder;
		final boolean isHeader = childViewHolder instanceof MenuAdapter.CategoryViewHolder;

		final int decoratedBottom = lm.getDecoratedBottom(child);
		if(DEBUG_HEADERS) {
			System.err.println(">>>> mLastBottom = " + mLastBottom);
			System.err.println(">>>> decoratedBottom = " + decoratedBottom);
		}

		if(decoratedBottom - mLastBottom > 100) {
			mLastBottom = Integer.MAX_VALUE;
		}

		mLastBottom = decoratedBottom;

		if(!isHeader) {
			RecyclerView.ViewHolder header = getHeaderViewByItem(childViewHolder);
			if(!mMenuAdapter.isHeader(header)) {
				return;
			}
			if(mFakeHeader == null || (mFakeHeader != null && header != mFakeHeader)) {
				mFakeHeader = header;
				mMenuAdapter.notifyDataSetChanged();
			}
			if(mMenuAdapter.hasExpandedGroups()) {
				mFakeHeader.itemView.setBackgroundColor(Color.GRAY);
			}
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) header.itemView.getLayoutParams();
			if(!lp.isItemRemoved() && !lp.isViewInvalid()) {
				header.itemView.setBackgroundColor(Color.GRAY);
				drawHeader(c, lm, getHeaderY(mFakeHeader.itemView, lm) - getDy(nextIsHeader, decoratedBottom), header.itemView);
			}
		} else {
			RecyclerView.ViewHolder header = getHeaderViewByItem(childViewHolder);
			mFakeHeader = childViewHolder;
			if(mMenuAdapter.hasExpandedGroups()) {
				header.itemView.setBackgroundColor(Color.GRAY);
			}
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) header.itemView.getLayoutParams();
			if(!lp.isItemRemoved() && !lp.isViewInvalid()) {
				drawHeader(c, lm, getHeaderY(header.itemView, lm) - getDy(nextIsHeader, decoratedBottom), header.itemView);
			}
		}
	}

	private int getDy(final boolean nextIsHeader, final int decoratedBottom) {
		int dy = 0;
		if(decoratedBottom < 96 && nextIsHeader) {
			dy = 96 - decoratedBottom;
		}
		return dy;
	}

	private void drawHeader(final Canvas c, final RecyclerView.LayoutManager lm, int dy, final View header) {
		final TextView viewById = (TextView) header.findViewById(R.id.txt_title);
		if(DEBUG_HEADERS) {
			System.err.println(">>>> header = " + viewById.getText());
		}
		if(header != null && header.getVisibility() == View.VISIBLE) {
			c.save();
			if(DEBUG_HEADERS) {
				System.err.println(">>>> dy = " + dy);
			}
			c.translate(0, dy);
			header.draw(c);
			c.restore();
		}
	}

	private void layoutHeader(View header) {
		int widthSpec = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.EXACTLY);
		int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		header.measure(widthSpec, heightSpec);
		header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
	}

	private CategoryData getParentData(final RecyclerView.ViewHolder holder) {
		final MultiLevelRecyclerAdapter.Data item = mMenuAdapter.getItemAt(holder.getPosition());
		return (CategoryData) item.getParent();
	}

	private RecyclerView.ViewHolder getHeaderViewByItem(final RecyclerView.ViewHolder holder) {
		final MultiLevelRecyclerAdapter.Data item = mMenuAdapter.getItemAt(holder.getPosition());
		if(item instanceof CategoryData) {
			headerStore.put(item, holder);
			return holder;
		}
		final MultiLevelRecyclerAdapter.Data parent = item.getParent();
		RecyclerView.ViewHolder result = headerStore.get(parent);
		if(result == null) {
			final int itemPosition = mMenuAdapter.getItemPosition(parent);
			result = mMenuAdapter.createViewHolder(mListView, MenuAdapter.VIEW_TYPE_SUBSUBCATEGORY);
			mMenuAdapter.bindViewHolder(result, itemPosition);
			layoutHeader(result.itemView);
			headerStore.put(parent, result);
		}
		return result;
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
	public void onPanelSlide(final View panel, final float slideOffset) {
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
