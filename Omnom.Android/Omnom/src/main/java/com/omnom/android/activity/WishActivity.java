package com.omnom.android.activity;

import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.WishListAdapter;
import com.omnom.android.fragment.menu.MenuItemAddFragment;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.restaurant.ModifierRequestItem;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.restaurant.WishRequest;
import com.omnom.android.restaurateur.model.restaurant.WishRequestItem;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.otto.Subscribe;

import java.util.Collections;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class WishActivity extends BaseOmnomFragmentActivity implements View.OnClickListener {

	public static int RESULT_CLEARED = 1;

	public static int RESULT_BILL = 2;

	public static int RESULT_OK = 4;

	public static void start(OmnomActivity activity, Restaurant restaurant, Menu menu, UserOrder order, int code) {
		final Intent intent = new Intent(activity.getActivity(), WishActivity.class);
		intent.putExtra(EXTRA_ORDER, order);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_RESTAURANT_MENU, menu);
		activity.startForResult(intent, R.anim.slide_in_up, R.anim.nothing, code);
	}

	private static WishRequest createWishRequest(Restaurant restaurant, UserOrder order) {
		final WishRequest wishRequest = new WishRequest();
		wishRequest.setInternalTableId(RestaurantHelper.getTable(restaurant).getInternalId());

		for(final UserOrderData data : order.getSelectedItems()) {
			final WishRequestItem item = createWishRequestItem(data);
			wishRequest.addItem(item);
		}
		return wishRequest;
	}

	private static WishRequestItem createWishRequestItem(final UserOrderData data) {
		if(data == null || data.item() == null) {
			return null;
		}
		final Item dish = data.item();
		final WishRequestItem item = new WishRequestItem(dish.id(), data.amount());
		if(dish.modifiers() != null && dish.modifiers().size() > 0) {
			for(Modifier modifier : dish.modifiers()) {
				if(modifier != null) {
					item.modifiers.add(new ModifierRequestItem(modifier.id()));
				}
			}
		}
		return item;
	}

	@InjectView(android.R.id.list)
	protected ListView mList;

	@InjectView(R.id.progress)
	protected ProgressBar mProgressBar;

	@Inject
	protected RestaurateurObservableApi api;

	private UserOrder mOrder;

	private WishListAdapter mAdapter;

	private boolean mClear = false;

	private Restaurant mRestaurant;

	private String TAG = WishActivity.class.getSimpleName();

	private Menu mMenu;

	private SwingBottomInAnimationAdapter swAdapter;

	private boolean mOrderChanged = false;

	@OnClick(R.id.txt_close)
	public void onClose() {
		finish();
	}

	@OnClick(R.id.btn_bill)
	public void onBill() {
		setResult(RESULT_BILL | (mClear ? RESULT_CLEARED : RESULT_OK), getResultData());
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
	}

	@Override
	public void finish() {
		setResult(mClear ? RESULT_CLEARED : RESULT_OK, getResultData());
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
	}

	private Intent getResultData() {
		if(mOrderChanged) {
			final Intent data = new Intent();
			data.putExtra(EXTRA_ORDER, mOrder);
			return data;
		}
		return null;
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mOrder = intent.getParcelableExtra(EXTRA_ORDER);
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
		mMenu = intent.getParcelableExtra(EXTRA_RESTAURANT_MENU);
	}

	@Override
	public void initUi() {
		ViewUtils.setVisible(mProgressBar, false);
		mAdapter = new WishListAdapter(this, mOrder, Collections.EMPTY_LIST, this);
		swAdapter = new SwingBottomInAnimationAdapter(mAdapter);
		swAdapter.setAbsListView(mList);
		mList.setAdapter(swAdapter);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_wish;
	}

	@Override
	public void onClick(final View v) {
		switch(v.getId()) {
			case R.id.btn_clear:
				doClear();
				break;

			case R.id.btn_send:
				doWish();
				break;

			case R.id.txt_price:
				final UserOrderData orderData = (UserOrderData) v.getTag();
				MenuItemAddFragment.show(getSupportFragmentManager(), R.id.fragment_container, mMenu.modifiers(), mOrder,
				                         orderData.item());
				break;
		}
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		if(mOrder == null) {
			return;
		}
		mOrderChanged = true;
		final Item item = event.getItem();
		mOrder.itemsTable().put(item.id(), UserOrderData.create(event.getCount(), item));
		mAdapter.notifyDataSetChanged();
		swAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_WISH_LIST && resultCode == RESULT_OK) {
			finish();
		}
	}

	private void doWish() {
		AnimationUtils.animateAlpha(mProgressBar, true);
		api.wishes(mRestaurant.id(), createWishRequest(mRestaurant, mOrder)).subscribe(new Action1() {
			@Override
			public void call(final Object o) {
				ViewUtils.setVisible(mProgressBar, false);
				WishSentActivity.start(WishActivity.this, REQUEST_CODE_WISH_LIST);
				doClear();
				showToast(getActivity(), getString(R.string.your_wish_processed));
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			protected void onError(final Throwable throwable) {
				ViewUtils.setVisible(mProgressBar, false);
				Log.e(TAG, "restaurateur.wishes", throwable);
			}
		});
	}

	private void doClear() {
		mClear = true;
		mOrder.itemsTable().clear();

		final int childCount = mList.getChildCount();
		for(int i = 0; i < childCount - 1; i++) {
			final View childAt = mList.getChildAt(i);
			if(childAt != null) {
				childAt.animate().alpha(0).start();
				AnimationUtils.scaleHeight(childAt, 0, new Runnable() {
					@Override
					public void run() {
						final Object tag = childAt.getTag(R.id.item);
						mAdapter.remove(tag);
						mAdapter.notifyDataSetChanged();
						childAt.animate().alpha(1).setDuration(0).start();
						if(ViewCompat.hasTransientState(childAt)) {
							ViewCompat.setHasTransientState(childAt, false);
						}
					}
				}, getResources().getInteger(R.integer.default_animation_duration_short));
				if(!ViewCompat.hasTransientState(childAt)) {
					ViewCompat.setHasTransientState(childAt, true);
				}
			}
		}
	}
}
