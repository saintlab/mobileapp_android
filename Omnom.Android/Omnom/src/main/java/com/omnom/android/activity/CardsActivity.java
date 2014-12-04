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
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.CardsAdapter;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.cards.Card;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.socket.listener.PaymentEventListener;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.HeaderView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class CardsActivity extends BaseOmnomActivity implements CardsAdapter.AnimationEndListener {

	private static final String TAG = CardsActivity.class.getSimpleName();

	private static final int REQUEST_CODE_CARD_CONFIRM = 100;

	private static final int REQUEST_CODE_CARD_ADD = 101;

	private static final int REQUEST_PAYMENT = 102;

	public class DemoCard extends Card {
		@Override
		public String getAssociation() {
			return "visa";
		}

		@Override
		public String getMaskedPan() {
			return "4111 .... .... 1111";
		}

		@Override
		public boolean isRegistered() {
			return true;
		}
	}

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

	private int mAccentColor;

	private Subscription mCardsSubscription;

	private PreferenceProvider mPreferences;

	private ValueAnimator dividerAnimation;

	private Order mOrder;

	private OrderFragment.PaymentDetails mDetails;

	private boolean mIsDemo;

	private PaymentEventListener mPaymentListener;

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_cards;
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

	private void initDividerAnimation() {
		final ColorDrawable dividerDrawable = new ColorDrawable(getResources().getColor(R.color.card_unregistered));
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
	public void initUi() {
		mPaymentListener = new PaymentEventListener(this);
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
					if(!mIsDemo) {
						mPreferences.setCardId(activity, card.getExternalCardId());
						adapter.notifyDataSetChanged();
					}
				} else {
					String cvv = OmnomApplication.get(activity).getConfig().getAcquiringData().getTestCvv();
					final CardInfo cardInfo = CardInfo.create(activity, card.getExternalCardId(), cvv);
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
		if(mIsDemo) {
			final List<DemoCard> demoCards = Arrays.asList(new DemoCard());
			mList.setAdapter(new CardsAdapter(getActivity(), demoCards, CardsActivity.this, true));
			mPanelTop.showProgress(false);
			mPanelTop.showButtonRight(true);
		} else {
			mCardsSubscription = AndroidObservable.bindActivity(this, api.getCards().delaySubscription(1000, TimeUnit.MILLISECONDS))
			                                      .subscribe(
					                                      new Action1<CardsResponse>() {
						                                      @Override
						                                      public void call(final CardsResponse cards) {
							                                      final List<Card> cardsList = cards.getCards();
							                                      if(cardsList.size() == 1) {
								                                      mPreferences.setCardId(getActivity(), cardsList.get(0)
								                                                                                     .getExternalCardId());
							                                      }
							                                      mList.setAdapter(new CardsAdapter(getActivity(), cardsList,
							                                                                        CardsActivity.this, false));
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
	}

	@OnClick(R.id.btn_pay)
	protected void onPay() {
		final String cardId = getPreferences().getCardId(this);
		String cvv = OmnomApplication.get(getActivity()).getConfig().getAcquiringData().getTestCvv();
		final CardInfo cardInfo = CardInfo.create(this, cardId, cvv);
		pay(cardInfo);
	}

	private void pay(final CardInfo cardInfo) {
		PaymentProcessActivity.start(getActivity(), REQUEST_PAYMENT, mDetails.getAmount(),
									 mOrder, cardInfo, mIsDemo, mAccentColor);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_PAYMENT) {
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(R.anim.nothing, R.anim.slide_out_up);
			} else if (requestCode == REQUEST_CODE_CARD_ADD && data != null) {
				CardInfo cardInfo = data.getParcelableExtra(EXTRA_CARD_DATA);
				if (cardInfo != null) {
					pay(cardInfo);
				} else {
					Log.w(TAG, "Card info is null");
				}
			} else if(requestCode == REQUEST_CODE_CARD_CONFIRM || requestCode == REQUEST_CODE_CARD_ADD) {
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

	public void onAdd() {
		CardAddActivity.start(this, REQUEST_CODE_CARD_ADD);
	}

	@Override
	public void onAnimationEnd() {
		dividerAnimation.setDuration(1000);
		dividerAnimation.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPaymentListener.onPause();
		OmnomObservable.unsubscribe(mCardsSubscription);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPaymentListener.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPaymentListener.initTableSocket(mOrder.getTableId());
	}
}
