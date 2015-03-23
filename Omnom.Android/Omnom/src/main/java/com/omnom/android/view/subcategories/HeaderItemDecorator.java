package com.omnom.android.view.subcategories;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.adapter.MultiLevelRecyclerAdapter;
import com.omnom.android.fragment.menu.CategoryData;
import com.omnom.android.fragment.menu.MenuAdapter;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 17.03.2015.
 */
public class HeaderItemDecorator extends RecyclerView.ItemDecoration {

	public static final int DECORATION_THRESHOLD = 100;

	public static final int TEXT_COLOR_SELECTED = Color.BLACK;

	public static final int TEXT_COLOR_DEFAULT = Color.WHITE;

	private final int mBgColorSelected;

	private final int mAnimationDurationDefault;

	private final OmnomArgbEvaluator mEvaluator;

	private SubcategoriesView mSubcategoriesView;

	private RecyclerView mRecycleView;

	private MenuAdapter mMenuAdapter;

	private HashMap<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder> mHeaderStore;

	private RecyclerView.ViewHolder mFakeHeader;

	private int mLastBottom = Integer.MAX_VALUE;

	HeaderItemDecorator(SubcategoriesView subcategoriesView, final RecyclerView recyclerView, MenuAdapter menuAdapter,
	                    final HashMap<MultiLevelRecyclerAdapter.Data,
			                    RecyclerView.ViewHolder> headerStore) {
		mSubcategoriesView = subcategoriesView;
		mRecycleView = recyclerView;
		mMenuAdapter = menuAdapter;
		mHeaderStore = headerStore;
		mBgColorSelected = subcategoriesView.getResources().getColor(R.color.lighter_grey);
		mAnimationDurationDefault = subcategoriesView.getResources().getInteger(R.integer.default_animation_duration_short);
		mEvaluator = OmnomArgbEvaluator.getInstance();
	}

	@Override
	public void onDrawOver(final Canvas c, final RecyclerView parent, final RecyclerView.State state) {
		super.onDrawOver(c, parent, state);
		if(mMenuAdapter.hasExpandedGroups()) {
			drawHeaders(c, parent);
		}
	}

	private int getCategoryBgColor(final RecyclerView.ViewHolder holder) {
		return MenuAdapter.getCategoryColor(holder.getItemViewType());
	}

	protected void restoreHeaderView(final RecyclerView.ViewHolder holder) {
		if(MenuAdapter.isHeader(holder)) {
			final View v = holder.itemView;
			v.setBackgroundColor(getCategoryBgColor(holder));
			((TextView) v.findViewById(R.id.txt_title)).setTextColor(TEXT_COLOR_DEFAULT);
		}
	}

	void drawSelectedBackground(final View view) {
		if(mMenuAdapter.hasExpandedGroups()) {
			((TextView) view.findViewById(R.id.txt_title)).setTextColor(TEXT_COLOR_SELECTED);
			view.setBackgroundColor(mBgColorSelected);
		}
	}

	void animateHeaderStyle(final RecyclerView.ViewHolder holder) {
		if(MenuAdapter.isHeader(holder)) {
			AnimationUtils.animateTextColor((TextView) findById(holder.itemView, R.id.txt_title),
			                                TEXT_COLOR_SELECTED,
			                                mAnimationDurationDefault);
			AnimationUtils.animateBackground(holder.itemView, getCategoryBgColor(holder), mBgColorSelected, mAnimationDurationDefault);
		}
	}

	void applyHeaderStyle(final RecyclerView.ViewHolder holder) {
		if(!MenuAdapter.isHeader(holder)) {
			return;
		}

		final View view = holder.itemView;
		final int decoratedTop = mRecycleView.getLayoutManager().getDecoratedTop(view);
		final int position = holder.getPosition();
		final int headerHeight = mSubcategoriesView.getResources().getDimensionPixelSize(R.dimen.view_size_default);
		if(decoratedTop <= headerHeight && position >= 0) {
			final CategoryData data = (CategoryData) mMenuAdapter.getItemAt(position);
			if(!data.isGroup()) {
				if(decoratedTop > 0) {
					final TextView txtTitle = (TextView) view.findViewById(R.id.txt_title);
					final float fraction = (float) decoratedTop / (float) headerHeight;
					final Integer bgColor = (Integer) mEvaluator.evaluate(fraction, mBgColorSelected, getCategoryBgColor(holder));
					final Integer textColor = (Integer) mEvaluator.evaluate(fraction, TEXT_COLOR_SELECTED, TEXT_COLOR_DEFAULT);
					txtTitle.setTextColor(textColor);
					view.setBackgroundColor(bgColor);
				} else {
					drawSelectedBackground(view);
				}
			}
		} else {
			restoreHeaderView(holder);
		}
	}

	private int getDy(final boolean nextIsHeader, final int decoratedBottom) {
		int dy = 0;
		final int height = mSubcategoriesView.getResources().getDimensionPixelSize(R.dimen.view_size_default);
		if(decoratedBottom < height && nextIsHeader) {
			dy = height - decoratedBottom;
		}
		return dy;
	}

	private void drawHeader(final Canvas c, final RecyclerView.LayoutManager lm, int dy, final View header) {
		if(header != null && header.getVisibility() == View.VISIBLE) {
			c.save();
			c.translate(0, dy);
			header.draw(c);
			c.restore();
		}
	}

	private void drawHeaders(final Canvas c, final RecyclerView parent) {
		final RecyclerView.LayoutManager lm = parent.getLayoutManager();
		final View child = parent.getChildAt(0);
		final View nextChild = parent.getChildAt(1);

		if(child == null || nextChild == null) {
			return;
		}

		RecyclerView.ViewHolder childViewHolder = parent.getChildViewHolder(child);
		RecyclerView.ViewHolder nextChildViewHolder = parent.getChildViewHolder(nextChild);
		final boolean nextIsHeader = nextChildViewHolder instanceof MenuAdapter.CategoryViewHolder;
		final boolean isHeader = childViewHolder instanceof MenuAdapter.CategoryViewHolder;

		final int decoratedBottom = lm.getDecoratedBottom(child);

		if(decoratedBottom - mLastBottom > DECORATION_THRESHOLD) {
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
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) header.itemView.getLayoutParams();
			if(lp != null && !lp.isItemRemoved() && !lp.isViewInvalid()) {
				final int dy = getDy(nextIsHeader, decoratedBottom);
				drawHeader(c, lm, -dy, header.itemView);
				mSubcategoriesView.mFakeStickyHeader.setTranslationY(-dy);
				final MultiLevelRecyclerAdapter.Data item = mMenuAdapter.getItemAt(childViewHolder.getPosition());
				if(item.getParent() instanceof CategoryData) {
					final CategoryData cat = (CategoryData) item.getParent();
					ViewUtils.setVisible(mSubcategoriesView.mFakeStickyHeader, true);
					drawSelectedBackground(mSubcategoriesView.mFakeStickyHeader);
					((TextView) mSubcategoriesView.mFakeStickyHeader.findViewById(R.id.txt_title)).setText(cat.getName());
				} else {
					AnimationUtils.animateAlpha(mSubcategoriesView.mFakeStickyHeader, false);
				}
			}
		} else {
			ViewUtils.setVisible(mSubcategoriesView.mFakeStickyHeader, false);
			RecyclerView.ViewHolder header = getHeaderViewByItem(childViewHolder);
			mFakeHeader = childViewHolder;
			final MultiLevelRecyclerAdapter.Data itemAt = mMenuAdapter.getItemAt(header.getPosition());
			if(itemAt.isGroup()) {
				return;
			}
			RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) header.itemView.getLayoutParams();
			if(!lp.isItemRemoved() && !lp.isViewInvalid()) {
				drawHeader(c, lm, (int) ViewCompat.getTranslationY(header.itemView) - getDy(nextIsHeader, decoratedBottom),
				           header.itemView);
			}
		}
	}

	private RecyclerView.ViewHolder getHeaderViewByItem(final RecyclerView.ViewHolder holder) {
		final MultiLevelRecyclerAdapter.Data item = mMenuAdapter.getItemAt(holder.getPosition());
		if(item instanceof CategoryData) {
			mHeaderStore.put(item, holder);
			return holder;
		}
		final MultiLevelRecyclerAdapter.Data parent = item.getParent();
		RecyclerView.ViewHolder result = mHeaderStore.get(parent);
		if(result == null) {
			final int itemPosition = mMenuAdapter.getItemPosition(parent);
			result = mMenuAdapter.createViewHolder(mRecycleView, MenuAdapter.VIEW_TYPE_SUBSUBCATEGORY);
			mMenuAdapter.bindViewHolder(result, itemPosition);
			layoutHeader(result.itemView);
			mHeaderStore.put(parent, result);
		}
		return result;
	}

	private void layoutHeader(View header) {
		int widthSpec = View.MeasureSpec.makeMeasureSpec(mRecycleView.getWidth(), View.MeasureSpec.EXACTLY);
		int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		header.measure(widthSpec, heightSpec);
		header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
	}

	@Override
	public void getItemOffsets(final Rect outRect, final View view, final RecyclerView parent, final RecyclerView.State state) {
	}

	public RecyclerView.ViewHolder getFakeHeader() {
		return mFakeHeader;
	}

	public void restoreHeadersStyle() {
		for(Map.Entry<MultiLevelRecyclerAdapter.Data, RecyclerView.ViewHolder> entry : mHeaderStore.entrySet()) {
			restoreHeaderView(entry.getValue());
		}
	}
}
