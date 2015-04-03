package com.omnom.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.PaymentChecker;
import com.omnom.android.R;
import com.omnom.android.acquiring.AcquiringType;
import com.omnom.android.acquiring.ExtraData;
import com.omnom.android.acquiring.OrderInfo;
import com.omnom.android.acquiring.PaymentInfoFactory;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.api.PaymentInfo;
import com.omnom.android.acquiring.demo.DemoAcquiring;
import com.omnom.android.acquiring.mailru.OrderInfoMailRu;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MailRuExtra;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.activity.base.BaseOmnomModeSupportActivity;
import com.omnom.android.activity.holder.BarEntranceData;
import com.omnom.android.activity.holder.EntranceData;
import com.omnom.android.activity.holder.TableEntranceData;
import com.omnom.android.activity.order.BaseOrderAcceptedActivity;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.OmnomErrorHelper;
import com.omnom.android.mixpanel.model.acquiring.PaymentMixpanelEvent;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.socket.listener.SilentPaymentEventListener;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.validator.LongValidator;
import com.omnom.android.validator.Validator;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;

/**
 * Created by mvpotter on 24/11/14.
 */
public class PaymentProcessActivity extends BaseOmnomModeSupportActivity implements SilentPaymentEventListener.PaymentListener {

	public static final int SIMILAR_PAYMENTS_TIMEOUT = 60 * 1000;

	public static final String TRANSACTION_ALREADY_PROCESSED_EARLIER = "TRANSACTION_ALREADY_PROCESSED_EARLIER";

	private static final String TAG = PaymentProcessActivity.class.getSimpleName();

	private static final int REQUEST_THANKS = 100;

	public static void start(final Activity activity, final int code, final OrderFragment.PaymentDetails details,
	                         final Order order, CardInfo cardInfo, final boolean isDemo,
	                         final Restaurant restaurant) {
		final Intent intent = new Intent(activity, PaymentProcessActivity.class);
		intent.putExtra(Extras.EXTRA_RESTAURANT, restaurant);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, RestaurantHelper.getBackgroundColor(restaurant));
		intent.putExtra(Extras.EXTRA_PAYMENT_DETAILS, details);
		intent.putExtra(Extras.EXTRA_ORDER, order);
		intent.putExtra(Extras.EXTRA_PAYMENT_TYPE, MailRuExtra.PAYMENT_TYPE_ORDER);
		intent.putExtra(Extras.EXTRA_CARD_DATA, cardInfo);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);
		activity.startActivityForResult(intent, code);
	}

	public static void start(final Activity activity, final int code, final OrderFragment.PaymentDetails details,
	                         final UserOrder order, CardInfo cardInfo, WishResponse wishResponse, final boolean isDemo,
	                         final Restaurant restaurant) {
		final Intent intent = new Intent(activity, PaymentProcessActivity.class);
		intent.putExtra(Extras.EXTRA_RESTAURANT, restaurant);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, RestaurantHelper.getBackgroundColor(restaurant));
		intent.putExtra(Extras.EXTRA_PAYMENT_DETAILS, details);
		intent.putExtra(Extras.EXTRA_USER_ORDER, order);
		intent.putExtra(Extras.EXTRA_PAYMENT_TYPE, MailRuExtra.PAYMENT_TYPE_WISH);
		intent.putExtra(Extras.EXTRA_CARD_DATA, cardInfo);
		intent.putExtra(Extras.EXTRA_WISH_RESPONSE, wishResponse);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);
		activity.startActivityForResult(intent, code);
	}

	final View.OnClickListener mFinishClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			setResult(RESULT_CANCELED);
			finish();
			overridePendingTransition(R.anim.nothing, R.anim.fade_out);
		}
	};

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected Acquiring mAcquiring;

	@Inject
	protected DemoAcquiring mDemoAcquiring;

	@InjectView(R.id.loader)
	protected LoaderView loader;

	@InjectView(R.id.btn_bottom)
	protected View btnBottom;

	@InjectView(R.id.txt_error)
	protected TextView txtError;

	@InjectView(R.id.txt_bottom)
	protected TextView txtBottom;

	@InjectView(R.id.btn_demo)
	protected View btnDemo;

	@InjectViews({R.id.txt_error, R.id.panel_errors})
	protected List<View> errorViews;

	protected OmnomErrorHelper mErrorHelper;

	private Subscription mBillSubscription;

	private Subscription mPaySubscription;

	private Subscription mCheckSubscription;

	private OrderFragment.PaymentDetails mDetails;

	@Nullable
	private Order mOrder;

	private CardInfo mCardInfo;

	private boolean mIsDemo;

	private int mAccentColor;

	private SilentPaymentEventListener mPaymentListener;

	private String mTransactionUrl;

	@Nullable
	private PaymentSocketEvent mPaymentEvent;

	private int mBillId;

	private BillResponse mBillData;

	private Validator restaurantIdValidator;

	@Nullable
	private PaymentChecker mPayChecker;

	@Nullable
	private UserOrder mUserOrder;

	private String mType;

	private WishResponse mWishResponse;

	@Nullable
	private Restaurant mRestaurant;

	@Override
	public void initUi() {
		mPayChecker = new PaymentChecker(this);

		mPaymentListener = new SilentPaymentEventListener(this, this);
		mErrorHelper = new OmnomErrorHelper(loader, txtError, btnBottom, txtBottom, btnDemo, errorViews);
		loader.scaleDown();
		loader.setColor(getResources().getColor(android.R.color.black));
		loader.setProgressColor(getResources().getColor(R.color.payment_progress_color));
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mOrder != null) {
			mPaymentListener.initTableSocket(mOrder.getTableId());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		restaurantIdValidator = new LongValidator();
		if(savedInstanceState != null) {
			mTransactionUrl = savedInstanceState.getString(Extras.EXTRA_TRANSACTION_URL);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		pay(mDetails.getAmount(), mDetails.getTip());
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPaymentListener.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(Extras.EXTRA_TRANSACTION_URL, mTransactionUrl);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPaymentListener.onDestroy();
		loader.onDestroy();
		OmnomObservable.unsubscribe(mBillSubscription);
		OmnomObservable.unsubscribe(mPaySubscription);
		OmnomObservable.unsubscribe(mCheckSubscription);
	}

	private void pay(final double amount, final int tip) {
		ButterKnife.apply(errorViews, ViewUtils.VISIBLITY, false);
		loader.setLogo(R.drawable.ic_flying_credit_card);
		loader.startProgressAnimation(getResources().getInteger(R.integer.payment_duration), new Runnable() {
			@Override
			public void run() {
			}
		});
		if(mTransactionUrl == null) {
			processPayment(amount, tip);
		} else {
			AcquiringResponse acquiringResponse = new AcquiringResponse();
			acquiringResponse.setUrl(mTransactionUrl);
			checkResult(acquiringResponse);
		}
	}

	private void processPayment(final double amount, final int tip) {
		final Activity activity = getActivity();

		final BillRequest request = createBillRequest(amount);
		mBillSubscription = AppObservable.bindActivity(activity, api.bill(request)).subscribe(new Action1<BillResponse>() {
			@Override
			public void call(final BillResponse response) {
				final String status = response.getStatus();
				if(BillResponse.STATUS_NEW.equals(status) && !response.hasErrors()) {
					tryToPay(mCardInfo, response, amount, tip);
				} else {
					Log.w(TAG, "processPayment status = " + status);
					if(response.getError() != null) {
						Log.w(TAG, response.getError());
					} else if(response.getErrors() != null) {
						Log.w(TAG, response.getErrors().toString());
					}
					if(BillResponse.STATUS_RESTAURANT_NOT_AVAILABLE.equals(status)) {
						mErrorHelper.showError(LoaderError.RESTAURANT_UNAVAILABLE, mFinishClickListener);
					} else if(BillResponse.STATUS_PAID.equals(status) || BillResponse.STATUS_ORDER_CLOSED.equals(status)) {
						onOrderClosed();
					} else {
						onUnknownError();
					}

				}
			}
		}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			public void onError(Throwable throwable) {
				Log.w(TAG, "processPayment", throwable);
				onUnknownError();
			}
		});
	}

	private BillRequest createBillRequest(final double amount) {
		if(mOrder != null) {
			return BillRequest.create(amount, mOrder);
		}
		return BillRequest.createForWish(mWishResponse.restaurantId(), mWishResponse.id());
	}

	private void tryToPay(final CardInfo card, BillResponse billData, final double amount, final int tip) {
		final com.omnom.android.auth.UserData cachedUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(cachedUser.getId()), cachedUser.getPhone());
		final AcquiringData acquiringData = OmnomApplication.get(getActivity()).getConfig().getAcquiringData();
		pay(billData, card, acquiringData, user, amount, tip);
	}

	private void pay(BillResponse billData, final CardInfo cardInfo, final AcquiringData acquiringData, UserData user, double amount,
	                 int tip) {
		final String mailRestaurantId = billData.getMailRestaurantId();
		if(!restaurantIdValidator.validate(mailRestaurantId) && !mIsDemo) {
			AcquiringResponseError error = new AcquiringResponseError();
			error.setDescr("Mail restaurant id is invalid: " + mailRestaurantId);
			onPayError(error);
			return;
		}

		final ExtraData extra = MailRuExtra.create(tip, mailRestaurantId, mType);
		mBillData = billData;
		mBillId = billData.getId();
		final OrderInfo order = OrderInfoMailRu.create(amount, String.valueOf(billData.getId()), "message");
		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU, user, cardInfo, extra, order);

		mDetails.setBillId(billData.getId());

		if(mPayChecker != null) {
			final OrderFragment.PaymentDetails prevDetails = mPayChecker.check(mDetails);
			if(prevDetails != null) {
				final String prevDetailsTransactionUrl = prevDetails.getTransactionUrl();
				if(!TextUtils.isEmpty(prevDetailsTransactionUrl)) {
					AcquiringResponse acquiringResponse = new AcquiringResponse();
					acquiringResponse.setUrl(prevDetailsTransactionUrl);
					checkResult(acquiringResponse);
					mPayChecker.clearCache(prevDetails);
					return;
				} else {
					if(System.currentTimeMillis() - prevDetails.getTransactionTimestmap() < SIMILAR_PAYMENTS_TIMEOUT) {
						final AcquiringResponseError error = new AcquiringResponseError();
						error.setCode(TRANSACTION_ALREADY_PROCESSED_EARLIER);
						error.setDescr(getString(R.string.attempt_to_perform_similar_payment));
						onSimilarPayment(error);
						return;
					}
				}
			}

			mDetails.setTransactionTimestmap(System.currentTimeMillis());
			mPayChecker.onPrePayment(mDetails);
		}

		mPaySubscription = AppObservable.bindActivity(getActivity(), getAcquiring().pay(acquiringData, paymentInfo))
		                                .subscribe(new Action1<AcquiringResponse>() {
			                                @Override
			                                public void call(final AcquiringResponse response) {
				                                if(response.getError() != null) {
					                                Log.w(TAG, response.getError().toString());
					                                onPayError(response.getError());
				                                } else {
					                                mTransactionUrl = response.getUrl();
					                                mDetails.setTransactionUrl(mTransactionUrl);
					                                if(mPayChecker != null) {
						                                mPayChecker.onPaymentRequested(mDetails);
					                                }
					                                checkResult(response);
				                                }
			                                }
		                                }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			                                @Override
			                                public void onError(Throwable throwable) {
				                                Log.w(TAG, "pay", throwable);
				                                onUnknownError();
			                                }
		                                });
	}

	private void onSimilarPayment(final AcquiringResponseError error) {
		reportMixPanelFail(error);
		loader.showProgress(false);
		mErrorHelper.showSimilarPaymentDeclined(mFinishClickListener);
	}

	private void checkResult(final AcquiringResponse response) {
		mCheckSubscription = AppObservable.bindActivity(getActivity(), getAcquiring().checkResult(response))
		                                  .subscribe(new Action1<AcquiringPollingResponse>() {
			                                  @Override
			                                  public void call(final AcquiringPollingResponse response) {
				                                  processResponse(response);
			                                  }
		                                  }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			                                  @Override
			                                  public void onError(Throwable throwable) {
				                                  Log.w(TAG, "checkResult", throwable);
				                                  onUnknownError();
			                                  }
		                                  });
	}

	private void processResponse(AcquiringPollingResponse response) {
		Log.i(TAG, "status = " + response.getStatus());
		if(AcquiringPollingResponse.STATUS_OK.equals(response.getStatus())) {
			onPayOk(response);
		} else {
			onPayError(response.getError());
		}
	}

	private void onPayOk(final AcquiringPollingResponse response) {
		Log.d(TAG, "status = " + response.getStatus());
		reportMixPanelSuccess();
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				if(mIsDemo) {
					ThanksDemoActivity.start(getActivity(), mOrder, REQUEST_THANKS, mAccentColor, mDetails.getAmount(), mDetails.getTip());
				} else {
					if(mWishResponse != null) {
						OrderAcceptedActivity.start(getActivity(), mRestaurant, mWishResponse, REQUEST_THANKS, mAccentColor);
					} else {
						if(entranceData instanceof TableEntranceData) {
							ThanksActivity.start(getActivity(), mOrder, mPaymentEvent, REQUEST_THANKS, mAccentColor);
						} else {
							if(entranceData instanceof BarEntranceData) {
								if(mWishResponse != null) {
									final EntranceData barEntranceData = BarEntranceData.create("#orderNumber", mWishResponse.code());
									BaseOrderAcceptedActivity.start(getActivity(), barEntranceData, REQUEST_THANKS, mAccentColor);
								}
							} else {
								// TODO: fill appropriate entrance data fields if necessary
								BaseOrderAcceptedActivity.start(getActivity(), entranceData, REQUEST_THANKS, mAccentColor);
							}
						}
					}
					overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
				}
			}
		});
	}

	private void onPayError(final AcquiringResponseError error) {
		reportMixPanelFail(error);
		loader.showProgress(false);
		mErrorHelper.showPaymentDeclined(mFinishClickListener);
	}

	private void onUnknownError() {
		mErrorHelper.showUnknownError(mFinishClickListener);
	}

	private void onOrderClosed() {
		mErrorHelper.showOrderClosed(mFinishClickListener);
	}

	private void reportMixPanelSuccess() {
		if(!mIsDemo) {
			getMixPanelHelper().track(MixPanelHelper.Project.OMNOM,
			                          new PaymentMixpanelEvent(getUserData(), mDetails, mBillId, mCardInfo));
			getMixPanelHelper().trackRevenue(MixPanelHelper.Project.OMNOM,
			                                 String.valueOf(getUserData().getId()), mDetails, mBillData);
		}
	}

	private void reportMixPanelFail(final AcquiringResponseError error) {
		if(!mIsDemo) {
			getMixPanelHelper().track(MixPanelHelper.Project.OMNOM,
			                          new PaymentMixpanelEvent(getUserData(), mDetails, mBillId,
			                                                   mCardInfo, error));
		}
	}

	private Acquiring getAcquiring() {
		return mIsDemo ? mDemoAcquiring : mAcquiring;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == REQUEST_THANKS) {
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}

	@Override
	protected void handleIntent(Intent intent) {
		super.handleIntent(intent);
		mRestaurant = intent.getParcelableExtra(Extras.EXTRA_RESTAURANT);
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
		mDetails = intent.getParcelableExtra(EXTRA_PAYMENT_DETAILS);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
		mWishResponse = intent.getParcelableExtra(Extras.EXTRA_WISH_RESPONSE);
		mType = intent.getStringExtra(Extras.EXTRA_PAYMENT_TYPE);
		if(TextUtils.isEmpty(mType)) {
			mType = MailRuExtra.PAYMENT_TYPE_ORDER;
		}
		mUserOrder = intent.getParcelableExtra(Extras.EXTRA_USER_ORDER);
		mCardInfo = intent.getParcelableExtra(Extras.EXTRA_CARD_DATA);
		mIsDemo = intent.getBooleanExtra(Extras.EXTRA_DEMO_MODE, false);
	}

	@Override
	public void onPaymentEvent(final PaymentSocketEvent event) {
		mPaymentEvent = event;
	}
}
