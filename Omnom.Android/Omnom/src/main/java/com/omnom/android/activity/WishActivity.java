package com.omnom.android.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomModeSupportActivity;
import com.omnom.android.adapter.WishAdapter;
import com.omnom.android.entrance.EntranceData;
import com.omnom.android.entrance.TakeawayEntranceData;
import com.omnom.android.fragment.menu.MenuItemAddFragment;
import com.omnom.android.fragment.menu.OrderUpdateEvent;
import com.omnom.android.fragment.takeaway.TakeawayTimeFragment;
import com.omnom.android.fragment.takeaway.TakeawayTimePickedEvent;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.menu.model.UserOrderData;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.mixpanel.model.SplitWay;
import com.omnom.android.mixpanel.model.TipsWay;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.restaurateur.model.restaurant.ModifierRequestItem;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.restaurant.WishForbiddenResponse;
import com.omnom.android.restaurateur.model.restaurant.WishRequest;
import com.omnom.android.restaurateur.model.restaurant.WishRequestItem;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.DialogUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.support.ItemClickSupport;
import com.squareup.otto.Subscribe;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RetrofitError;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.fragment.OrderFragment.PaymentDetails;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class WishActivity extends BaseOmnomModeSupportActivity implements View.OnClickListener,
                                                                          ItemClickSupport.OnItemLongClickListener {

	public static final int RESULT_CLEARED = 1;

	public static final int RESULT_BILL = 2;

	public static final int RESULT_OK = 4;

	public static final int RESULT_ORDER_DONE = 8;

	public static void start(OmnomActivity activity, Restaurant restaurant, TableDataResponse table,
	                         Menu menu, UserOrder order, EntranceData entranceData,
	                         int code) {
		final Intent intent = new Intent(activity.getActivity(), WishActivity.class);
		intent.putExtra(EXTRA_ORDER, order);
		intent.putExtra(EXTRA_TABLE, table);
		intent.putExtra(EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_RESTAURANT_MENU, menu);
		intent.putExtra(EXTRA_ENTRANCE_DATA, entranceData);
		activity.startForResult(intent, R.anim.slide_in_up, R.anim.nothing, code);
	}

	private static WishRequest createWishRequest(UserOrder order) {
		final WishRequest wishRequest = new WishRequest();
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
				if(modifier != null && !TextUtils.isEmpty(modifier.id())) {
					item.getModifiers().add(new ModifierRequestItem(modifier.id()));
				}
			}
		}
		return item;
	}

	private static WishRequest createWishRequest(final UserOrder order, final int time) {
		final WishRequest wishRequest = createWishRequest(order);
		wishRequest.setTime(time);
		return wishRequest;
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

	private void showOutOfSaleDialog(final Context context,
	                                 final Collection<WishRequestItem> forbiddenItems) {
		if(forbiddenItems == null || forbiddenItems.isEmpty()) {
			return;
		}

		final Collection<String> titles = getStoppedItems(forbiddenItems);

		final String itemsStr = StringUtils.concat(",\n", titles);
		final String message = context.getResources()
		                              .getString(R.string.out_of_sale_message, itemsStr);
		final AlertDialog dialog = DialogUtils.showDialog(context,
		                                                  R.string.out_of_sale_title, message,
		                                                  R.string.ok,
		                                                  new DialogInterface.OnClickListener() {
			                                                  @Override
			                                                  public void onClick(DialogInterface dialog, int which) {
				                                                  for(final WishRequestItem item : forbiddenItems) {
					                                                  final String id = item.getId();
					                                                  mOrder.itemsTable().remove(id);
					                                                  mAdapter.removeItem(MenuHelper.getItem(mMenu, id));
				                                                  }
				                                                  doWish();
			                                                  }
		                                                  },
		                                                  R.string.cancel,
		                                                  new DialogInterface.OnClickListener() {
			                                                  @Override
			                                                  public void onClick(DialogInterface dialog, int which) {
				                                                  dialog.dismiss();
			                                                  }
		                                                  });
		TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
	}

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
	protected void onResume() {
		super.onResume();
		ViewUtils.setVisible(mProgressBar, false);
	}

	@Override
	public void initUi() {
		final boolean isBar = RestaurantHelper.isBar(mRestaurant);
		ViewUtils.setVisible(mPanelBottom, !isBar);
		ViewUtils.setVisible(mPanelBottomBar, isBar);
		ViewUtils.setVisible(mProgressBar, false);
		mAdapter = new WishAdapter(this, mOrder, Collections.EMPTY_LIST, mEntranceData, this);
		mLayoutManager = new LinearLayoutManager(this);
		mList.setHasFixedSize(true);
		mList.setLayoutManager(mLayoutManager);
		mList.setAdapter(mAdapter);
		ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mList);
		itemClickSupport.setOnItemLongClickListener(this);
		refresh();
	}

	private void refresh() {
		ViewUtils.setVisible(mProgressBar, true);
		api.getRecommendations(mRestaurant.id())
		   .flatMap(new Func1<Collection<OrderItem>, Observable<Collection<OrderItem>>>() {
			   @Override
			   public Observable<Collection<OrderItem>> call(Collection<OrderItem> orderItems) {
				   final Collection<OrderItem> filtered = new ArrayList<OrderItem>();
				   for(final OrderItem orderItem : orderItems) {
					   if(mMenu.hasItem(orderItem.getId())) {
						   filtered.add(orderItem);
					   }
				   }
				   return Observable.just(filtered);
			   }
		   })
		   .onErrorResumeNext(new Func1<Throwable, Observable<Collection<OrderItem>>>() {
			   @Override
			   public Observable<Collection<OrderItem>> call(final Throwable throwable) {
				   return Observable.just((Collection<OrderItem>) Collections.EMPTY_LIST);
			   }
		   })
		   .subscribe(new Action1<Collection<OrderItem>>() {
			   @Override
			   public void call(final Collection<OrderItem> response) {
				   final WishAdapter adapter = new WishAdapter(WishActivity.this, mOrder, response, mEntranceData, WishActivity.this);
				   mAdapter = adapter;
				   mList.swapAdapter(mAdapter, true);
				   ViewUtils.setVisible(mProgressBar, false);
				   fakeScroll();
			   }
		   }, new Action1<Throwable>() {
			   @Override
			   public void call(final Throwable throwable) {
				   Log.e(TAG, "Unable to get items on table", throwable);
				   ViewUtils.setVisible(mProgressBar, false);
			   }
		   });
	}

	private void fakeScroll() {
		// workaround for https://github.com/saintlab/mobileapp_android/issues/393
		// problem appears on cheap devices like huawei, zte and so on
		mList.smoothScrollBy(1, 0);
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
		if(TextUtils.isEmpty(OmnomApplication.get(getActivity()).getAuthToken())) {
			LoginActivity.start(this, AndroidUtils.getDevicePhoneNumber(this, R.string.phone_country_code), REQUEST_CODE_LOGIN);
			return;
		}

		if(RestaurantHelper.isBar(mRestaurant)) {
			doWishBar();
		} else if(mEntranceData instanceof TakeawayEntranceData) {
			doAskAboutTime();
		} else {
			doWishDefault();
		}
	}

	private void doAskAboutTime() {
		TakeawayTimeFragment.show(getSupportFragmentManager(), R.id.fragment_container);
	}

	@Subscribe
	public void onTakeawayTimePicked(TakeawayTimePickedEvent event) {
		AnimationUtils.animateAlpha(mProgressBar, true);
		final WishRequest wishRequest = createWishRequest(mOrder, event.getTimeValue());
		api.wishes(mRestaurant.id(), wishRequest).subscribe(new Action1<WishResponse>() {
			@Override
			public void call(final WishResponse wishResponse) {
				final PaymentDetails paymentDetails = new PaymentDetails(
						mOrder.getTotalPrice().doubleValue(),
						0,
						TipsWay.DEFAULT, 0,
						SplitWay.WASNT_USED);
				CardsActivity.start(WishActivity.this,
				                    mRestaurant,
				                    mOrder,
				                    wishResponse,
				                    mEntranceData,
				                    paymentDetails,
				                    RestaurantHelper.getBackgroundColor(mRestaurant),
				                    REQUEST_CODE_WISH_LIST);
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			protected void onError(final Throwable throwable) {
				ViewUtils.setVisible(mProgressBar, false);
				Log.e(TAG, "restaurateur.wishes", throwable);
			}
		});
	}

	@Override
	public boolean onItemLongClick(final RecyclerView parent, final View view, final int position,
	                               final long id) {
		final int itemType = mAdapter.getItemViewType(position);
		if(itemType == WishAdapter.VIEW_TYPE_WISH_ITEM) {
			final UserOrderData order = (UserOrderData) mAdapter.getItemAt(position);
			if(order != null && order.item() != null) {
				final String title = order.item().name();
				DialogUtils.showDeleteDialog(this, title, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onOrderUpdate(new OrderUpdateEvent(order.item(), 0, position));
					}
				});
				return true;
			}
		}
		return false;
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
		fakeScroll();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode,
	                                final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_WISH_LIST && resultCode == RESULT_OK) {
			setResult(RESULT_ORDER_DONE);
			super.finish();
		}
		if(requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
			doWish();
		}
	}

	private void doWishBar() {
		if(mOrder.getSelectedItems().isEmpty()) {
			showToast(this, getString(R.string.your_wish_is_empty));
			return;
		}

		AnimationUtils.animateAlpha(mProgressBar, true);
		final WishRequest wishRequest = createWishRequest(mOrder);

		api.wishes(mRestaurant.id(), wishRequest).subscribe(new Action1<WishResponse>() {
			@Override
			public void call(final WishResponse wishResponse) {
				final PaymentDetails paymentDetails = new PaymentDetails(
						mOrder.getTotalPrice().doubleValue(),
						0,
						TipsWay.DEFAULT,
						0,
						SplitWay.WASNT_USED);
				CardsActivity.start(WishActivity.this,
				                    mRestaurant,
				                    mOrder,
				                    wishResponse,
				                    mEntranceData,
				                    paymentDetails,
				                    RestaurantHelper.getBackgroundColor(mRestaurant),
				                    REQUEST_CODE_WISH_LIST);
			}
		}, OmnomObservable.loggerOnError(TAG, new OmnomObservable.RetrofitErrorHandle() {
			@Override
			public void onRetrofitError(final RetrofitError error) {
				AnimationUtils.animateAlpha(mProgressBar, false);
				if(error != null && error.getResponse() != null
						&& error.getResponse().getStatus() == HttpStatus.SC_CONFLICT) {
					final WishForbiddenResponse forbiddenResponse
							= (WishForbiddenResponse) error
							.getBodyAs(WishForbiddenResponse.class);
					showOutOfSaleDialog(getActivity(), forbiddenResponse.forbidden());
				}
			}
		}));
	}

	private Collection<String> getStoppedItems(final Collection<WishRequestItem> items) {
		final ArrayList<String> result = new ArrayList<String>();
		Observable.from(items).map(new Func1<WishRequestItem, String>() {
			@Override
			public String call(final WishRequestItem wishRequestItem) {
				final Item item = MenuHelper.getItem(mMenu, wishRequestItem.getId());
				return item != null ? item.name() : null;
			}
		}).subscribe(new Action1<String>() {
			@Override
			public void call(final String s) {
				if(!TextUtils.isEmpty(s)) {
					result.add(s);
				}
			}
		});
		return result;
	}

	private void doWishDefault() {
		final WishRequest wishRequest = createWishRequest(mOrder);
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
		fakeScroll();
	}

}
