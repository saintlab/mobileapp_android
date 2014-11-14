package com.omnom.android.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.AcquiringType;
import com.omnom.android.acquiring.ExtraData;
import com.omnom.android.acquiring.OrderInfo;
import com.omnom.android.acquiring.PaymentInfoFactory;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.api.PaymentInfo;
import com.omnom.android.acquiring.mailru.OrderInfoMailRu;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.MailRuExtra;
import com.omnom.android.acquiring.mailru.model.MerchantData;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.CardsAdapter;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.cards.Card;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.HeaderView;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class CardsActivity extends BaseOmnomActivity implements CardsAdapter.AnimationEndListener {

	private static final int REQUEST_CODE_CARD_CONFIRM = 100;

	private static final int REQUEST_CODE_CARD_ADD = 101;

	private static final int REQUEST_CODE_PAYMENT_OK = 102;

	private static final String TAG = CardsActivity.class.getSimpleName();

	@SuppressLint("NewApi")
	public static void start(final Activity activity, final Order order, final OrderFragment.PaymentDetails details,
	                         final int accentColor, final int code, boolean isDemo) {
		final Intent intent = new Intent(activity, CardsActivity.class);
		intent.putExtra(Extras.EXTRA_PAYMENT_DETAILS, details);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);
		intent.putExtra(Extras.EXTRA_ORDER, order);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);

		if(AndroidUtils.isJellyBean()) {
			Bundle extras = ActivityOptions.makeCustomAnimation(activity,
			                                                    R.anim.slide_in_up,
			                                                    R.anim.fake_fade_out_long).toBundle();
			activity.startActivityForResult(intent, code, extras);
		} else {
			activity.startActivityForResult(intent, code);
		}
	}

	@Inject
	protected RestaurateurObeservableApi api;

	@InjectView(R.id.panel_top)
	protected HeaderView mPanelTop;

	@InjectView(R.id.btn_pay)
	protected Button mBtnPay;

	@InjectView(R.id.list)
	protected ListView mList;

	@Inject
	protected Acquiring mAcquiring;

	private int mAccentColor;

	private Subscription mCardsSubscription;

	private PreferenceProvider mPreferences;

	private ValueAnimator dividerAnimation;

	private Subscription mPaySubscription;

	private Subscription mBillSubscription;

	private Order mOrder;

	private OrderFragment.PaymentDetails mDetails;

	private boolean mIsDemo;

	@Override
	public void initUi() {
		mPreferences = OmnomApplication.get(getActivity()).getPreferences();

		initDividerAnimation();

		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});
		if(!mIsDemo) {
			mPanelTop.setButtonRight(R.string.add_card, new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					onAdd();
				}
			});
		}

		mPanelTop.showProgress(true);
		mPanelTop.showButtonRight(false);

		loadCards();

		final String text = StringUtils.formatCurrency(mDetails.getAmount()) + getString(R.string.currency_ruble);
		mBtnPay.setText(getString(R.string.pay_amount, text));

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				final Activity activity = getActivity();
				final CardsAdapter adapter = (CardsAdapter) mList.getAdapter();
				final Card card = (Card) adapter.getItem(position);
				if(card.isRegistered()) {
					mPreferences.setCardId(activity, card.getExternalCardId());
					adapter.notifyDataSetChanged();
				} else {
					final CardInfo cardInfo = CardInfo.create(activity, card.getExternalCardId());
					CardConfirmActivity.startConfirm(CardsActivity.this, cardInfo, REQUEST_CODE_CARD_CONFIRM);
				}
			}
		});

		GradientDrawable sd = (GradientDrawable) mBtnPay.getBackground();
		sd.setColor(getResources().getColor(R.color.btn_pay_green));
		sd.invalidateSelf();
	}

	private void loadCards() {
		if(mCardsSubscription != null) {
			OmnomObservable.unsubscribe(mCardsSubscription);
		}
		mCardsSubscription = AndroidObservable.bindActivity(this, api.getCards().delaySubscription(1000, TimeUnit.MILLISECONDS)).subscribe(
				new Action1<CardsResponse>() {
					@Override
					public void call(final CardsResponse cards) {
						mList.setAdapter(new CardsAdapter(getActivity(), cards.getCards(), CardsActivity.this));
						mPanelTop.showProgress(false);
						mPanelTop.showButtonRight(true);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						mPanelTop.showProgress(false);
						mPanelTop.showButtonRight(true);
					}
				});
	}

	@OnClick(R.id.btn_pay)
	protected void onPay() {
		pay(mDetails.getAmount(), 0);
	}

	private void tryToPay(final CardInfo card, BillResponse billData, final double amount, final double tip) {
		final com.omnom.android.auth.UserData cachedUser = OmnomApplication.get(getActivity()).getUserProfile().getUser();
		final UserData user = UserData.create(String.valueOf(cachedUser.getId()), cachedUser.getPhone());
		final MerchantData merchant = new MerchantData(getActivity());
		pay(billData, card, merchant, user, amount, tip);
	}

	private void pay(BillResponse billData, final CardInfo cardInfo, MerchantData merchant, UserData user, double amount, double tip) {
		final ExtraData extra = MailRuExtra.create(tip, billData.getMailRestaurantId());
		final OrderInfo order = OrderInfoMailRu.create(amount, String.valueOf(billData.getId()), "message");
		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU, user, cardInfo, extra, order);
		mPaySubscription = AndroidObservable.bindActivity(getActivity(), mAcquiring.pay(merchant, paymentInfo))
		                                    .subscribe(new Action1<AcquiringPollingResponse>() {
			                                    @Override
			                                    public void call(AcquiringPollingResponse response) {
				                                    onPayOk(response);
			                                    }
		                                    }, new Action1<Throwable>() {
			                                    @Override
			                                    public void call(Throwable throwable) {
				                                    onPayError(throwable);
			                                    }
		                                    });
	}

	private void onPayError(Throwable throwable) {
		Log.d(TAG, "status = " + throwable);
		mBtnPay.setEnabled(true);
		showToast(getActivity(), "Unable to pay");
	}

	private void onPayOk(AcquiringPollingResponse response) {
		Log.d(TAG, "status = " + response.getStatus());
		mBtnPay.setEnabled(true);
		ThanksActivity.start(this, REQUEST_CODE_PAYMENT_OK);
	}

	private void pay(final double amount, final int tip) {
		final Activity activity = getActivity();

		final String cardId = getPreferences().getCardId(activity);
		final CardInfo cardInfo = CardInfo.create(activity, cardId);
		mBtnPay.setEnabled(false);

		final BillRequest request = BillRequest.create(amount, mOrder);
		mBillSubscription = AndroidObservable.bindActivity(activity, api.bill(request)).subscribe(new Action1<BillResponse>() {
			@Override
			public void call(final BillResponse response) {
				if(!response.hasErrors()) {
					tryToPay(cardInfo, response, amount, tip);
				} else {
					if(response.getError() != null) {
						showToast(activity, response.getError());
					} else if(response.getErrors() != null) {
						showToast(activity, response.getErrors().toString());
					}
					mBtnPay.setEnabled(true);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				mBtnPay.setEnabled(true);
			}
		});
	}

	private BigDecimal getEnteredAmount() {
		return null;
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_CODE_PAYMENT_OK) {
				setResult(RESULT_OK);
				finish();
			}
			if(requestCode == REQUEST_CODE_CARD_CONFIRM || requestCode == REQUEST_CODE_CARD_ADD) {
				AnimationUtils.animateAlpha(mList, false, new Runnable() {
					@Override
					public void run() {
						mList.setAdapter(null);
						AnimationUtils.animateAlpha(mList, true);
						loadCards();
					}
				});
			}
		}
	}

	private void initDividerAnimation() {
		final ColorDrawable dividerDrawable = new ColorDrawable(getResources().getColor(R.color.profile_hint));
		mList.setDivider(dividerDrawable);
		dividerAnimation = ValueAnimator.ofInt(0, 255);
		dividerAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				Integer animatedValue = (Integer) animation.getAnimatedValue();
				dividerDrawable.setAlpha(animatedValue);
				mList.setDividerHeight(ViewUtils.dipToPixels(getActivity(), 1));
			}
		});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_cards;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCardsSubscription);
	}

	public void onAdd() {
		CardAddActivity.start(this, REQUEST_CODE_CARD_ADD);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mDetails = intent.getParcelableExtra(Extras.EXTRA_PAYMENT_DETAILS);
		if(mDetails == null) {
			finish();
			return;
		}
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
		mIsDemo = intent.getBooleanExtra(Extras.EXTRA_DEMO_MODE, false);
	}

	@Override
	public void onAnimationEnd() {
		dividerAnimation.setDuration(1000);
		dividerAnimation.start();
	}
}
