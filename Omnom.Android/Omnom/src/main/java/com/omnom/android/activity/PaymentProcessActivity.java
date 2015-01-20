package com.omnom.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.OmnomErrorHelper;
import com.omnom.android.mixpanel.model.acquiring.PaymentMixpanelEvent;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.socket.listener.SilentPaymentEventListener;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.ObservableUtils;
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

/**
 * Created by mvpotter on 24/11/14.
 */
public class PaymentProcessActivity extends BaseOmnomActivity implements SilentPaymentEventListener.PaymentListener {

	private static final String TAG = PaymentProcessActivity.class.getSimpleName();

	private static final int REQUEST_THANKS = 100;

	public static void start(final Activity activity, final int code, final OrderFragment.PaymentDetails details,
	                         final Order order, CardInfo cardInfo, final boolean isDemo,
	                         final int accentColor) {
		final Intent intent = new Intent(activity, PaymentProcessActivity.class);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);
		intent.putExtra(Extras.EXTRA_PAYMENT_DETAILS, details);
		intent.putExtra(Extras.EXTRA_ORDER, order);
		intent.putExtra(Extras.EXTRA_CARD_DATA, cardInfo);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);
		activity.startActivityForResult(intent, code);
	}

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

	@Override
	public void initUi() {
		mPaymentListener = new SilentPaymentEventListener(this, this);
		mErrorHelper = new OmnomErrorHelper(loader, txtError, btnBottom, txtBottom, btnDemo, errorViews);
		loader.scaleDown();
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
		pay(mDetails.getAmount(), mDetails.getTip());
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
						Log.w(TAG, response.getError());
					} else if(response.getErrors() != null) {
						Log.w(TAG, response.getErrors().toString());
					}
					onUnknownError();
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

	private void tryToPay(final CardInfo card, BillResponse billData, final double amount, final int tip) {
		final com.omnom.android.auth.UserData cachedUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(cachedUser.getId()), cachedUser.getPhone());
		final AcquiringData acquiringData = OmnomApplication.get(getActivity()).getConfig().getAcquiringData();
		pay(billData, card, acquiringData, user, amount, tip);
	}

	private void pay(BillResponse billData, final CardInfo cardInfo, final AcquiringData acquiringData, UserData user, double amount,
	                 int tip) {
		final ExtraData extra = MailRuExtra.create(tip, billData.getMailRestaurantId());
		mBillData = billData;
		mBillId = billData.getId();
		final OrderInfo order = OrderInfoMailRu.create(amount, String.valueOf(billData.getId()), "message");
		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU, user, cardInfo, extra, order);
		mPaySubscription = AndroidObservable.bindActivity(getActivity(), getAcquiring().pay(acquiringData, paymentInfo))
		                                    .subscribe(new Action1<AcquiringResponse>() {
			                                    @Override
			                                    public void call(final AcquiringResponse response) {
				                                    if(response.getError() != null) {
					                                    Log.w(TAG, response.getError().toString());
					                                    onPayError(response.getError());
				                                    } else {
					                                    mTransactionUrl = response.getUrl();
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

	private void checkResult(final AcquiringResponse response) {
		mCheckSubscription = AndroidObservable.bindActivity(getActivity(), getAcquiring().checkResult(response))
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

	private void onPayOk(AcquiringPollingResponse response) {
		Log.d(TAG, "status = " + response.getStatus());
		reportMixPanelSuccess();
		loader.stopProgressAnimation();
		loader.updateProgressMax(new Runnable() {
			@Override
			public void run() {
				if(mIsDemo) {
					ThanksDemoActivity.start(getActivity(), mOrder, REQUEST_THANKS, mAccentColor, mDetails.getAmount());
				} else {
					ThanksActivity.start(getActivity(), mOrder, mPaymentEvent, REQUEST_THANKS, mAccentColor);
				}
				overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
			}
		});
	}

	private void onPayError(final AcquiringResponseError error) {
		reportMixPanelFail(error);
		loader.showProgress(false);
		mErrorHelper.showPaymentDeclined(finishOnClick());
	}

	private void onUnknownError() {
		mErrorHelper.showUnknownError(finishOnClick());
	}

	private void reportMixPanelSuccess() {
		if (!mIsDemo) {
			getMixPanelHelper().track(MixPanelHelper.Project.OMNOM,
								      new PaymentMixpanelEvent(getUserData(), mDetails, mBillId, mCardInfo));
			getMixPanelHelper().trackRevenue(MixPanelHelper.Project.OMNOM,
											 String.valueOf(getUserData().getId()), mDetails, mBillData);
		}
	}

	private void reportMixPanelFail(final AcquiringResponseError error) {
		if (!mIsDemo) {
			getMixPanelHelper().track(MixPanelHelper.Project.OMNOM,
									  new PaymentMixpanelEvent(getUserData(), mDetails, mBillId,
									  mCardInfo, error));
		}
	}

	private View.OnClickListener finishOnClick() {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
				overridePendingTransition(R.anim.nothing, R.anim.fade_out);
			}
		};
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
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
		mDetails = intent.getParcelableExtra(EXTRA_PAYMENT_DETAILS);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
		mCardInfo = intent.getParcelableExtra(Extras.EXTRA_CARD_DATA);
		mIsDemo = intent.getBooleanExtra(Extras.EXTRA_DEMO_MODE, false);
	}

	@Override
	public void onPaymentEvent(final PaymentSocketEvent event) {
		mPaymentEvent = event;
	}
}
