package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.AcquiringResponseException;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.CardsAdapter;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.cards.Card;
import com.omnom.android.restaurateur.model.cards.CardDeleteResponse;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.socket.listener.PaymentEventListener;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.HeaderView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

public class CardsActivity extends BaseOmnomActivity {

	public static final int RESULT_PAY = 10;

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

	public static void start(final Activity activity, final Order order, final OrderFragment.PaymentDetails details,
	                         final int accentColor, final int code, boolean isDemo) {
		final Intent intent = new Intent(activity, CardsActivity.class);
		intent.putExtra(Extras.EXTRA_PAYMENT_DETAILS, details);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);
		intent.putExtra(Extras.EXTRA_ORDER, order);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);
		startActivity(activity, intent, code);
	}

	@SuppressLint("NewApi")
	private static void startActivity(final Activity activity, final Intent intent, final int code) {
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

	@Inject
	protected Acquiring mAcquiring;

	@InjectView(R.id.panel_top)
	protected HeaderView mPanelTop;

	@InjectView(R.id.footer)
	protected LinearLayout cardsFooter;

	@InjectView(R.id.btn_pay)
	protected Button mBtnPay;

	@InjectView(R.id.list)
	protected ListView mList;

	private int mAccentColor;

	private Subscription mCardsSubscription;

	private Subscription mDeleteCardSubscription;

	private PreferenceProvider mPreferences;

	private Order mOrder;

	private OrderFragment.PaymentDetails mDetails;

	private boolean mIsDemo;

	private String mTableId;

	private PaymentEventListener mPaymentListener;

	private boolean isPaymentRequest = true;

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
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
		mIsDemo = intent.getBooleanExtra(Extras.EXTRA_DEMO_MODE, false);
		mTableId = intent.getStringExtra(Extras.EXTRA_TABLE_ID);
	}

	@Override
	public void initUi() {
		mPreferences = OmnomApplication.get(getActivity()).getPreferences();

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

		if(!TextUtils.isEmpty(mTableId)) {
			mPaymentListener = new PaymentEventListener(this);
		}

		if(mDetails != null) {
			final String text = StringUtils.formatCurrency(mDetails.getAmount()) + getString(R.string.currency_ruble);
			mBtnPay.setText(getString(R.string.pay_amount, text));
			if(!mIsDemo) {
				mBtnPay.setEnabled(false);
			}
		} else {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cardsFooter.getLayoutParams();
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			ViewUtils.setVisible(mBtnPay, false);
		}

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
					CardConfirmActivity.startConfirm(CardsActivity.this, cardInfo, REQUEST_CODE_CARD_CONFIRM,
					                                 mDetails != null ? mDetails.getAmount() : 0);
				}
			}
		});

		mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if(!mIsDemo) {
					final CardsAdapter adapter = (CardsAdapter) mList.getAdapter();
					final Card card = (Card) adapter.getItem(position);
					askForRemoval(card);
					return true;
				}
				return false;
			}
		});

		GradientDrawable sd = (GradientDrawable) mBtnPay.getBackground();
		sd.setColor(getResources().getColor(R.color.btn_pay_green));
		sd.invalidateSelf();
	}

	private void askForRemoval(final Card card) {
		final AlertDialog alertDialog = AndroidUtils.showDialog(this, card.getMaskedPan(),
		                                                        R.string.delete, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						removeCard(card);
					}
				}, R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
					}
				});
		alertDialog.setCanceledOnTouchOutside(true);
		final float btnTextSize = getResources().getDimension(R.dimen.font_normal);
		final Button btn1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		btn1.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
		final Button btn2 = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		btn2.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
		TextView messageView = (TextView) alertDialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
	}

	private void removeCard(final Card card) {
		final AcquiringData acquiringData = OmnomApplication.get(getActivity()).getConfig().getAcquiringData();
		UserData userData = UserData.create(OmnomApplication.get(getActivity()).getUserProfile().getUser());
		String cvv = OmnomApplication.get(getActivity()).getConfig().getAcquiringData().getTestCvv();
		final CardInfo cardInfo = CardInfo.create(getActivity(), card.getExternalCardId(), cvv);
		mDeleteCardSubscription = AndroidObservable.bindActivity(this, mAcquiring.deleteCard(acquiringData, userData, cardInfo))
		                                           .flatMap(new Func1<com.omnom.android.acquiring.mailru.response.CardDeleteResponse,
				                                           Observable<CardDeleteResponse>>() {
			                                           @Override
			                                           public Observable<CardDeleteResponse> call(com.omnom.android.acquiring.mailru
					                                                                                      .response.CardDeleteResponse
					                                                                                      cardDeleteResponse) {
				                                           if(cardDeleteResponse.isSuccess()) {
					                                           return api.deleteCard(card.getId());
				                                           } else {
					                                           throw new AcquiringResponseException(cardDeleteResponse.getError());
				                                           }
			                                           }
		                                           }).subscribe(new Action1<CardDeleteResponse>() {
					@Override
					public void call(CardDeleteResponse cardDeleteResponse) {
						if(cardDeleteResponse.isSuccess()) {
							onRemoveSuccess(card);
						} else {
							if(cardDeleteResponse.getError() != null) {
								cardRemovalError();
							} else if(cardDeleteResponse.hasErrors()) {
								if(cardDeleteResponse.hasCommonError()) {
									cardRemovalError(cardDeleteResponse.getErrors().getCommon());
								}
							}
						}
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						Log.w(TAG, throwable.getMessage());
						cardRemovalError();
					}
				});
	}

	private void onRemoveSuccess(final Card card) {
		final CardsAdapter adapter = (CardsAdapter) mList.getAdapter();
		adapter.remove(card);
		updateCardsSelection(adapter, card);
	}

	private void updateCardsSelection(CardsAdapter adapter, Card card) {
		final String selectedId = mPreferences.getCardId(getActivity());
		if(card.getExternalCardId().equals(selectedId)) {
			mBtnPay.setEnabled(selectCard(adapter, selectedId));
		}
	}

	private void cardRemovalError() {
		cardRemovalError(null);
	}

	private void cardRemovalError(String message) {
		Log.w(TAG, message);
		if(message != null) {
			Toast.makeText(this, "Unable to remove card: " + message, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "Unable to remove card", Toast.LENGTH_LONG).show();
		}
	}

	private void loadCards() {
		OmnomObservable.unsubscribe(mCardsSubscription);

		mPanelTop.showProgress(true);
		mPanelTop.showButtonRight(false);

		final Drawable divider = mList.getDivider();
		if(divider != null) {
			divider.setAlpha(0);
			divider.invalidateSelf();
		}
		if(mIsDemo) {
			final List<DemoCard> demoCards = Arrays.asList(new DemoCard());
			mList.setAdapter(new CardsAdapter(getActivity(), demoCards, true));
			mPanelTop.showProgress(false);
			mPanelTop.showButtonRight(true);
		} else {
			mList.setAdapter(null);
			mCardsSubscription = AndroidObservable.bindActivity(this, api.getCards().delaySubscription(1000, TimeUnit.MILLISECONDS))
			                                      .subscribe(
					                                      new Action1<CardsResponse>() {
						                                      @Override
						                                      public void call(final CardsResponse cards) {
							                                      final List<Card> cardsList = cards.getCards();
							                                      mList.setAdapter(new CardsAdapter(getActivity(), cardsList, false));
							                                      boolean isSelected = selectCard((CardsAdapter) mList.getAdapter(),
							                                                                      mPreferences.getCardId(getActivity()));
							                                      mBtnPay.setEnabled(isSelected);
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

	private boolean selectCard(final CardsAdapter cardsAdapter, final String selectedCardId) {
		boolean isSelected;
		// open CardAddActivity if no card registered on bill payment
		if(cardsAdapter.isEmpty()) {
			if(mDetails != null && isPaymentRequest) {
				onAdd();
			}
			return false;
		} else {
			final Card cardToSelect = findCardToSelect(cardsAdapter, selectedCardId);
			// If appropriate card found - mark as selected
			if(cardToSelect != null) {
				// If selection is not changed - do nothing
				if(!cardToSelect.getExternalCardId().equals(selectedCardId)) {
					mPreferences.setCardId(getActivity(), cardToSelect.getExternalCardId());
					cardsAdapter.notifyDataSetChanged();
				}
				isSelected = true;
				// If not found - remove preference record
			} else {
				isSelected = false;
				if(!TextUtils.isEmpty(selectedCardId)) {
					mPreferences.setCardId(getActivity(), null);
				}
			}
		}

		return isSelected;
	}

	private Card findCardToSelect(CardsAdapter cardsAdapter, String selectedCardId) {
		Card cardToSelect = null;
		for(int i = 0; i < cardsAdapter.getCount(); i++) {
			final Card card = (Card) cardsAdapter.getItem(i);
			// Select only registered cards
			if(card.isRegistered()) {
				if(cardToSelect == null) {
					cardToSelect = card;
				}
				// Select the first registered card if nothing is set
				if(TextUtils.isEmpty(selectedCardId)) {
					break;
					// If selected card is found in a list - finish search
				} else if(selectedCardId.equals(card.getExternalCardId())) {
					cardToSelect = card;
					break;
				}
			}
		}
		return cardToSelect;
	}

	@OnClick(R.id.btn_pay)
	protected void onPay() {
		final String cardId = getPreferences().getCardId(this);
		String cvv = OmnomApplication.get(getActivity()).getConfig().getAcquiringData().getTestCvv();
		final CardInfo cardInfo = CardInfo.create(this, cardId, cvv);
		pay(cardInfo);
	}

	private void pay(final CardInfo cardInfo) {
		if(mDetails != null) {
			PaymentProcessActivity.start(getActivity(), REQUEST_PAYMENT, mDetails,
			                             mOrder, cardInfo, mIsDemo, mAccentColor);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		isPaymentRequest = false;
		if(resultCode == RESULT_OK) {
			if(requestCode == REQUEST_PAYMENT) {
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(R.anim.nothing, R.anim.slide_out_up);
			}
		} else if(resultCode == RESULT_PAY && data != null) {
			CardInfo cardInfo = data.getParcelableExtra(EXTRA_CARD_DATA);
			if(cardInfo != null) {
				pay(cardInfo);
			} else {
				Log.w(TAG, "Card info is null");
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mPaymentListener != null) {
			mPaymentListener.onPause();
		}
		OmnomObservable.unsubscribe(mCardsSubscription);
		OmnomObservable.unsubscribe(mDeleteCardSubscription);
	}

	public void onAdd() {
		CardAddActivity.start(this, mDetails != null ? mDetails.getAmount() : 0, REQUEST_CODE_CARD_ADD);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mPaymentListener != null) {
			mPaymentListener.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadCards();
		if(mPaymentListener != null) {
			mPaymentListener.initTableSocket(mTableId);
		}
	}
}
