package com.omnom.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.adapter.WishAdapter;
import com.omnom.android.fragment.menu.MenuItemAddFragment;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.restaurateur.model.restaurant.ModifierRequestItem;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.restaurant.WishRequest;
import com.omnom.android.restaurateur.model.restaurant.WishRequestItem;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.otto.Subscribe;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class WishActivity extends BaseOmnomFragmentActivity implements View.OnClickListener {

	public static int RESULT_CLEARED = 1;

	public static int RESULT_BILL = 2;

	public static int RESULT_OK = 4;

	public static void start(OmnomActivity activity, Restaurant restaurant, TableDataResponse table, Menu menu, UserOrder order,
	                         int code) {
		final Intent intent = new Intent(activity.getActivity(), WishActivity.class);
		intent.putExtra(EXTRA_ORDER, order);
		intent.putExtra(EXTRA_TABLE, table);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_RESTAURANT_MENU, menu);
		activity.startForResult(intent, R.anim.slide_in_up, R.anim.nothing, code);
	}

	private static void showOutOfSaleDialog(final Context context, final Collection<String> items) {
		if(items == null || items.isEmpty()) {
			return;
		}
		final String itemsStr = StringUtils.concat(",\n", items);
		final String message = context.getResources().getString(R.string.out_of_sale_message, itemsStr);
		final AlertDialog dialog = AndroidUtils.showDialog(context,
		                                                   R.string.out_of_sale_title, message, R.string.ok,
		                                                   new DialogInterface.OnClickListener() {
			                                                   @Override
			                                                   public void onClick(DialogInterface dialog, int which) {
				                                                   Toast.makeText(context, "ok", Toast.LENGTH_SHORT).show();
			                                                   }
		                                                   }, R.string.cancel,
		                                                   new DialogInterface.OnClickListener() {
			                                                   @Override
			                                                   public void onClick(DialogInterface dialog, int which) {
				                                                   dialog.dismiss();
			                                                   }
		                                                   });
		TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
	}

	private static WishRequest createWishRequest(TableDataResponse table, UserOrder order) {
		final WishRequest wishRequest = WishRequest.create(table);
		for(final UserOrderData data : order.getSelectedItems()) {
			wishRequest.addItem(createWishRequestItem(data));
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
	protected RecyclerView mList;

	@InjectView(R.id.progress)
	protected ProgressBar mProgressBar;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	@InjectView(R.id.panel_bottom_bar)
	protected View mPanelBottomBar;

	@Inject
	protected RestaurateurObservableApi api;

	private UserOrder mOrder;

	private WishAdapter mAdapter;

	private boolean mClear = false;

	private TableDataResponse mTable;

	private String TAG = WishActivity.class.getSimpleName();

	private Menu mMenu;

	private boolean mOrderChanged = false;

	private Restaurant mRestaurant;

	private LinearLayoutManager mLayoutManager;

	@OnClick(R.id.txt_close)
	public void onClose() {
		finish();
	}

	@OnClick(R.id.panel_bottom_bar)
	public void onReadyOrders() {
		WebActivity.start(this, RestaurantHelper.getBarUri(mRestaurant));
	}

	@OnClick(R.id.btn_bill)
	public void onBill() {
		setResult(RESULT_BILL | (mClear ? RESULT_CLEARED : RESULT_OK), getResultData());
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
	}

	@Override
	public void finish() {
		final boolean cleared = mClear || mOrder.getSelectedItems().size() == 0;
		setResult(cleared ? RESULT_CLEARED : RESULT_OK, getResultData());
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
		mTable = intent.getParcelableExtra(EXTRA_TABLE);
		mRestaurant = intent.getParcelableExtra(EXTRA_RESTAURANT);
		mMenu = intent.getParcelableExtra(EXTRA_RESTAURANT_MENU);
	}

	@Override
	public void initUi() {
		final boolean isBar = RestaurantHelper.isBar(mRestaurant);
		ViewUtils.setVisible(mPanelBottom, !isBar);
		ViewUtils.setVisible(mPanelBottomBar, isBar);
		ViewUtils.setVisible(mProgressBar, false);
		mAdapter = new WishAdapter(this, mOrder, Collections.EMPTY_LIST, this);
		mLayoutManager = new LinearLayoutManager(this);
		mList.setHasFixedSize(true);
		mList.setLayoutManager(mLayoutManager);
		mList.setAdapter(mAdapter);
		refresh();
	}

	private void refresh() {
		ViewUtils.setVisible(mProgressBar, true);
		api.getItems(mTable.getRestaurantId(), mTable.getId()).subscribe(new Action1<Collection<OrderItem>>() {
			@Override
			public void call(final Collection<OrderItem> response) {
				mList.setAdapter(null);
				final WishAdapter adapter = new WishAdapter(WishActivity.this, mOrder, response,
				                                            WishActivity.this);
				mList.setAdapter(adapter);
				mAdapter = adapter;
				ViewUtils.setVisible(mProgressBar, false);
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(final Throwable throwable) {
				Log.e(TAG, "Unable to get items on table", throwable);
				ViewUtils.setVisible(mProgressBar, false);
			}
		});
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

			case R.id.btn_refresh:
				refresh();
				break;

			case R.id.btn_apply:
				MenuItemAddFragment.show(getSupportFragmentManager(),
				                         R.id.fragment_container,
				                         mMenu.modifiers(),
				                         mOrder,
				                         getItem(v), -1);
				break;
		}
	}

	private void doWish() {
		if(RestaurantHelper.isBar(mRestaurant)) {
			doWishBar();
		} else {
			doWishDefault();
		}
	}

	private Item getItem(final View v) {
		final Object orderData = v.getTag();
		if(orderData instanceof OrderItem) {
			final OrderItem orderItem = (OrderItem) v.getTag();
			final Item item = mMenu.findItem(orderItem.getId());
			if(item == null) {
				// for uknown reason there could be no such item in menu
				return Item.NULL;
			}
			return item;
		}
		if(orderData instanceof UserOrderData) {
			final UserOrderData orderItem = (UserOrderData) v.getTag();
			return orderItem.item();
		}
		return Item.NULL;
	}

	@Subscribe
	public void onOrderUpdate(OrderUpdateEvent event) {
		if(mOrder == null) {
			return;
		}
		mOrderChanged = true;
		final Item item = event.getItem();
		mOrder.addItem(item, event.getCount());
		if(event.getCount() > 0) {
			mAdapter.clearCache();
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter.removeItem(item);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_WISH_LIST && resultCode == RESULT_OK) {
			finish();
		}
	}

	private void doWishBar() {
		AnimationUtils.animateAlpha(mProgressBar, true);
		final WishRequest wishRequest = createWishRequest(mTable, mOrder);
		api.createWish(wishRequest)
		   .flatMap(new Func1<WishResponse, Observable<BillResponse>>() {
			   @Override
			   public Observable<BillResponse> call(final WishResponse wishResponse) {
				   if(!wishResponse.hasErrors()) {
					   if(wishResponse.getItems().size() > 0) {
						   // TODO: show out of sale dialog in accordance with server response
						   // showOutOfSaleDialog(this, Arrays.asList("Пиво Guinness", "Пиво Bud"));
						   return Observable.empty();
					   } else {
						   return api.bill(BillRequest.create(AmountHelper.format(mOrder.getTotalPrice()), mTable.getRestaurantId(),
						                                      mTable.getId(), wishResponse.getRestaurateurOrderId()));
					   }
				   } else {
					   return Observable.empty();
				   }
			   }
		   }).subscribe(new Action1<BillResponse>() {
			@Override
			public void call(final BillResponse billResponse) {
				// show cards and pay
			}
		}, OmnomObservable.loggerOnError(TAG));
	}

	private void doWishDefault() {
		final WishRequest wishRequest = createWishRequest(mTable, mOrder);
		api.wishes(mRestaurant.id(), wishRequest).subscribe(new Action1() {
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
		for(UserOrderData orderData : mOrder.getSelectedItems()) {
			mAdapter.remove(orderData);
		}
		mOrder.itemsTable().clear();
	}
}
