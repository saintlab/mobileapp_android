package com.omnom.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
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
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.socket.listener.PaymentEventListener;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

/**
 * Created by mvpotter on 24/11/14.
 */
public class PaymentProcessActivity extends BaseOmnomActivity {

	private static final String TAG = PaymentProcessActivity.class.getSimpleName();

	private static final int REQUEST_THANKS = 100;

	public static void start(final Activity activity, final int code, final double amount,
	                         final Order order, final boolean isDemo, final int accentColor) {
		final Intent intent = new Intent(activity, PaymentProcessActivity.class);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);
		intent.putExtra(Extras.EXTRA_ORDER_AMOUNT, amount);
		intent.putExtra(Extras.EXTRA_ORDER, order);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);
		activity.startActivityForResult(intent, code);
	}

	@Inject
	protected RestaurateurObeservableApi api;

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

	protected ErrorHelper mErrorHelper;

	private Subscription mBillSubscription;

	private Subscription mPaySubscription;

	private Subscription mCheckSubscription;

	private double mAmount;

	private Order mOrder;

	private CardInfo mCardInfo;

	private boolean mIsDemo;

	private int mAccentColor;

	private PaymentEventListener mPaymentListener;

	private String mTransactionUrl;

	public static void start(final Activity activity, final int code, final double amount,
	                         final Order order, CardInfo cardInfo, final boolean isDemo,
	                         final int accentColor) {
		final Intent intent = new Intent(activity, PaymentProcessActivity.class);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);
		intent.putExtra(Extras.EXTRA_ORDER_AMOUNT, amount);
		intent.putExtra(Extras.EXTRA_ORDER, order);
		intent.putExtra(Extras.EXTRA_CARD_DATA, cardInfo);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);
		activity.startActivityForResult(intent, code);
	}

	@Override
	public void initUi() {
		mPaymentListener = new PaymentEventListener(this);
		mErrorHelper = new ErrorHelper(loader, txtError, btnBottom, txtBottom, btnDemo, errorViews);
		final int dpSize = getResources().getDimensionPixelSize(R.dimen.loader_size);
		loader.setSize(dpSize, dpSize);
		loader.setColor(getResources().getColor(android.R.color.black));
		loader.setProgressColor(getResources().getColor(R.color.payment_progress_color));
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPaymentListener.initTableSocket(mOrder.getTableId());
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPaymentListener.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null) {
			mTransactionUrl = savedInstanceState.getString(Extras.EXTRA_TRANSACTION_URL);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		pay(mAmount, 0);
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

		final BillRequest request = BillRequest.create(amount, mOrder);
		mBillSubscription = AndroidObservable.bindActivity(activity, api.bill(request)).subscribe(new Action1<BillResponse>() {
			@Override
			public void call(final BillResponse response) {
				if(!response.hasErrors()) {
					tryToPay(mCardInfo, response, amount, tip);
				} else {
					if(response.getError() != null) {
						showToast(activity, response.getError());
					} else if(response.getErrors() != null) {
						showToast(activity, response.getErrors().toString());
					}
					onPayError();
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				Log.w(TAG, throwable.getMessage());
				onInternetError();
			}
		});
	}

	private void tryToPay(final CardInfo card, BillResponse billData, final double amount, final double tip) {
		final com.omnom.android.auth.UserData cachedUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(cachedUser.getId()), cachedUser.getPhone());
		final AcquiringData acquiringData = OmnomApplication.get(getActivity()).getConfig().getAcquiringData();
		pay(billData, card, acquiringData, user, amount, tip);
	}

	private void pay(BillResponse billData, final CardInfo cardInfo, final AcquiringData acquiringData, UserData user, double amount, double tip) {
		final ExtraData extra = MailRuExtra.create(tip, billData.getMailRestaurantId());
		final OrderInfo order = OrderInfoMailRu.create(amount, String.valueOf(billData.getId()), "message");
		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU, user, cardInfo, extra, order);
		mPaySubscription = AndroidObservable.bindActivity(getActivity(), getAcquiring().pay(acquiringData, paymentInfo))
				.subscribe(new Action1<AcquiringResponse>() {
					@Override
					public void call(final AcquiringResponse response) {
						if (response.getError() != null) {
							Log.w(TAG, response.getError().toString());
							onPayError();
						} else {
							mTransactionUrl = response.getUrl();
							checkResult(response);
						}
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						Log.w(TAG, throwable.getMessage());
						onInternetError();
					}
				});
	}

	private void checkResult(final AcquiringResponse response) {
		mCheckSubscription = AndroidObservable.bindActivity(getActivity(), getAcquiring().checkResult(response))
		                                      .subscribe(new Action1<AcquiringPollingResponse>() {
			                                      @Override
			                                      public void call(final AcquiringPollingResponse response) {
				                                      processResponse(response);
			                                      }
		                                      }, new Action1<Throwable>() {
			                                      @Override
			                                      public void call(Throwable throwable) {
				                                      Log.w(TAG, throwable.getMessage());
				                                      onInternetError();
			                                      }
		                                      });
	}

	private void processResponse(AcquiringPollingResponse response) {
		Log.i(TAG, "status = " + response.getStatus());
		if(AcquiringPollingResponse.STATUS_OK.equals(response.getStatus())) {
			onPayOk(response);
		} else {
			onPayError();
		}
	}

	private void onPayOk(AcquiringPollingResponse response) {
		Log.d(TAG, "status = " + response.getStatus());
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				ThanksActivity.start(getActivity(), mOrder, REQUEST_THANKS, mAccentColor);
				overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
			}
		});
	}

	private void onPayError() {
		loader.showProgress(false);
		mErrorHelper.showPaymentDeclined(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
				overridePendingTransition(R.anim.nothing, R.anim.fade_out);
			}
		});
	}

	private void onInternetError() {
		mErrorHelper.showInternetError(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pay(mAmount, 0);
			}
		});
	}

	private Acquiring getAcquiring() {
		return mIsDemo ? mDemoAcquiring : mAcquiring;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
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
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
		mAmount = intent.getDoubleExtra(Extras.EXTRA_ORDER_AMOUNT, 0);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
		mCardInfo = intent.getParcelableExtra(Extras.EXTRA_CARD_DATA);
		mIsDemo = intent.getBooleanExtra(Extras.EXTRA_DEMO_MODE, false);
	}
}