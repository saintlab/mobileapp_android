package com.omnom.android.fragment.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.omnom.android.R;
import com.omnom.android.adapter.MenuCategoryItemsAdapter;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.omnom.android.adapter.MultiLevelRecyclerAdapter.Data;

/**
 * Created by Ch3D on 02.02.2015.
 */
public class MenuSubcategoryFragment extends BaseFragment {

	public static void show(final FragmentManager manager, final UserOrder order, final Menu menu, final int position, final float ypos) {
		manager.beginTransaction()
		       .addToBackStack(null)
		       .setCustomAnimations(R.anim.fold_in,
		                            R.anim.slide_out_right,
		                            R.anim.fold_in,
		                            R.anim.slide_out_right)
		       .add(R.id.fragment_container, MenuSubcategoryFragment.newInstance(order, menu, position, ypos))
		       .commit();
	}

	private static Fragment newInstance(final UserOrder order, final Menu menu, final int position, final float ypos) {
		assert order != null && menu != null && position >= 0;
		final MenuSubcategoryFragment fragment = new MenuSubcategoryFragment();
		final Bundle args = new Bundle();

		args.putParcelable(Extras.EXTRA_ORDER, order);
		args.putParcelable(Extras.EXTRA_RESTAURANT_MENU, menu);
		args.putInt(Extras.EXTRA_POSITION, position);
		args.putFloat(Extras.EXTRA_PIVOT_Y, ypos);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.content_recyclerview)
	protected RecyclerView mListView;

	@InjectView(R.id.btn_back)
	protected ImageView mImgBack;

	private UserOrder mOrder;

	private Menu mMenu;

	private int mPosition;

	private float mPivotY;

	private MenuAdapter mMenuAdapter;

	private LinearLayoutManager mLayoutManager;

	private View.OnClickListener mGroupClickListener;

	@Subscribe
	public void onOrderUpdate(final OrderUpdateEvent event) {
		final Item item = event.getItem();
		mOrder.itemsTable().put(item.id(), UserOrderData.create(event.getCount(), item));
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
					final Data itemData = mMenuAdapter.getItemAt(position);
					mMenuAdapter.insert(position, i, itemData, new ItemData(itemData.getParent(),
					                                                        recommendation,
					                                                        ItemData.getType(i, size)));
				}
			} else {
				for(final String id : item.recommendations()) {
					final int position = event.getPosition() + 1;
					final Data itemData = mMenuAdapter.getItemAt(position);
					mMenuAdapter.remove(itemData);
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// mAdapter.notifyDataSetChanged();
	}

	@Override
	public Animation onCreateAnimation(final int transit, final boolean enter, final int nextAnim) {
		if(!enter) {
			AnimationUtils.animateAlpha(mListView, false);
			AnimationUtils.animateAlpha(mImgBack, false, 100);
		}

		final ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, enter ? 0.0f : 1.0f,
		                                                         enter ? 1.0f : 0.0f,
		                                                         Animation.RELATIVE_TO_SELF, 0.5f,
		                                                         Animation.RELATIVE_TO_SELF, mPivotY);
		scaleAnimation.setDuration(getResources().getInteger(R.integer.default_animation_duration_short));
		scaleAnimation.setInterpolator(new LinearInterpolator());
		scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(final Animation animation) {
				if(enter) {
					ViewUtils.setVisible2(mListView, false);
					ViewUtils.setVisible2(mImgBack, false);
				}
			}

			@Override
			public void onAnimationEnd(final Animation animation) {
				if(enter) {
					AnimationUtils.animateAlpha(mListView, true);
					AnimationUtils.animateAlpha(mImgBack, true);
				}
			}

			@Override
			public void onAnimationRepeat(final Animation animation) {

			}
		});
		return scaleAnimation;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(Extras.EXTRA_ORDER);
			mMenu = getArguments().getParcelable(Extras.EXTRA_RESTAURANT_MENU);
			mPosition = getArguments().getInt(Extras.EXTRA_POSITION, -1);
			mPivotY = getArguments().getFloat(Extras.EXTRA_PIVOT_Y, -1);
		}
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle bundle) {
		return getActivity().getLayoutInflater().inflate(R.layout.fragment_menu_subcategory, null);
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ButterKnife.inject(this, view);

		mListView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mListView.setLayoutManager(mLayoutManager);

		final MenuData menuData = new MenuData(mMenu, mOrder);
		mGroupClickListener = new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final int childPosition = mListView.getChildPosition(v);
				final Data category = mMenuAdapter.getItemAt(childPosition);
				mMenuAdapter.toggleGroup(childPosition);

				// if top category - scroll to top
				// else scroll to new item's position
				if(category.getLevel() == 0) {
					mLayoutManager.scrollToPositionWithOffset(0, 0);
				} else {
					final int newPos = mMenuAdapter.getItemPosition(category);
					mLayoutManager.scrollToPositionWithOffset(newPos, 0);
				}
			}
		};
		mMenuAdapter = new MenuAdapter(getActivity(), mMenu, mOrder, mGroupClickListener, new View.OnClickListener() {
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

		mListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mMenuAdapter.expandGroup(mPosition);
			}
		}, 850);
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

	@OnClick(R.id.btn_back)
	public void onClose() {
		AnimationUtils.animateAlpha(mImgBack, false, 100);
		getFragmentManager().popBackStack();
	}

	public void showAddFragment(final Item item, int pos) {
		if(item == null) {
			return;
		}
		MenuItemAddFragment.show(getFragmentManager(), mMenu.modifiers(), mOrder, item, pos);
	}

}
