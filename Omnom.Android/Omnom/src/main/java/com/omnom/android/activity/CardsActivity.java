package com.omnom.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.acquiring.AcquiringResponseException;
import com.omnom.android.acquiring.api.Acquiring;
import com.omnom.android.acquiring.mailru.model.CardInfo;
import com.omnom.android.acquiring.mailru.model.UserData;
import com.omnom.android.acquiring.mailru.response.AcquiringResponseError;
import com.omnom.android.activity.base.BaseOmnomModeSupportActivity;
import com.omnom.android.activity.holder.EntranceData;
import com.omnom.android.adapter.CardsAdapter;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.menu.model.UserOrder;
import com.omnom.android.mixpanel.model.acquiring.CardDeletedMixpanelEvent;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.cards.Card;
import com.omnom.android.restaurateur.model.cards.CardDeleteResponse;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.restaurateur.model.config.AcquiringData;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.WishResponse;
import com.omnom.android.socket.listener.PaymentEventListener;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.DialogUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.HeaderView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.mixpanel.MixPanelHelper.Project.OMNOM;

public class CardsActivity extends BaseOmnomModeSupportActivity {

	public static final int RESULT_PAY = 10;

	public static final int RESULT_ENTER_CARD_AND_PAY = 11;

	public static final String EVENT_CARD_DELETED = "card_deleted";

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

	public static void start(final Activity activity, Restaurant restaurant, final Order order, final OrderFragment.PaymentDetails details,
	                         final int accentColor, EntranceData entranceData, final int code, boolean isDemo) {
		final Intent intent = new Intent(activity, CardsActivity.class);
		intent.putExtra(Extras.EXTRA_PAYMENT_DETAILS, details);
		intent.putExtra(Extras.EXTRA_RESTAURANT, restaurant);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);
		intent.putExtra(Extras.EXTRA_ORDER, order);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, isDemo);
		intent.putExtra(Extras.EXTRA_ENTRANCE_DATA, entranceData);
		startActivity(activity, intent, code);
	}

	public static void start(final Activity activity, Restaurant restaurant, final String tableId) {
		final Intent intent = new Intent(activity, CardsActivity.class);
		intent.putExtra(Extras.EXTRA_RESTAURANT, restaurant);
		intent.putExtra(EXTRA_TABLE_ID, tableId);
		startActivity(activity, intent, -1);
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

	public static void start(final WishActivity activity, Restaurant restaurant, final UserOrder order, WishResponse wishResponse,
	                         EntranceData entranceData, final OrderFragment.PaymentDetails paymentDetails,
	                         final int accentColor, final int code) {
		final Intent intent = new Intent(activity, CardsActivity.class);
		intent.putExtra(Extras.EXTRA_PAYMENT_DETAILS, paymentDetails);
		intent.putExtra(Extras.EXTRA_RESTAURANT, restaurant);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);
		intent.putExtra(Extras.EXTRA_WISH_RESPONSE, wishResponse);
		intent.putExtra(Extras.EXTRA_USER_ORDER, order);
		intent.putExtra(Extras.EXTRA_DEMO_MODE, false);
		intent.putExtra(Extras.EXTRA_ENTRANCE_DATA, entranceData);
		startActivity(activity, intent, code);
	}

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected Acquiring mAcquiring;

	@InjectView(R.id.panel_top)
	protected HeaderView mPanelTop;

	@InjectView(R.id.footer)
	protected LinearLayout cardsFooter;

	@Optional
	@InjectView(R.id.btn_pay)
	protected Button mBtnPay;

	@InjectView(R.id.list)
	protected ListView mList;

	@InjectView(R.id.delimiter)
	protected View mDelimiter;

	private int mAccentColor;

	private Subscription mCardsSubscription;

	private Subscription mDeleteCardSubscription;

	private PreferenceProvider mPreferences;

	@Nullable
	private Order mOrder;

	private OrderFragment.PaymentDetails mDetails;

	private boolean mIsDemo;

	private String mTableId;

	private PaymentEventListener mPaymentListener;

	private boolean isPaymentRequest = true;

	@Nullable
	private UserOrder mUserOrder;

	private WishResponse mWishResponse;

	@Nullable
	private Restaurant mRestaurant;

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@Override
	public int getLayoutResource() {
		// There is an issue with SDK 4.0.4 (15). If we change layout properties from code
		// a number of controls become hidden. It is not reproduced on further version of SDK.
		// Thus, as a solution the view is duplicated and changed to have appropriate layout.
		// https://github.com/saintlab/mobileapp_android/issues/262
		if(getIntent().getParcelableExtra(Extras.EXTRA_PAYMENT_DETAILS) == null) {
			return R.layout.activity_cards_profile;
		} else {
			return R.layout.activity_cards;
		}
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mDetails = intent.getParcelableExtra(Extras.EXTRA_PAYMENT_DETAILS);
		mRestaurant = intent.getParcelableExtra(Extras.EXTRA_RESTAURANT);
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
		mOrder = intent.getParcelableExtra(Extras.EXTRA_ORDER);
		mUserOrder = intent.getParcelableExtra(Extras.EXTRA_USER_ORDER);
		mWishResponse = intent.getParcelableExtra(Extras.EXTRA_WISH_RESPONSE);
		mIsDemo = intent.getBooleanExtra(Extras.EXTRA_DEMO_MODE, false);
		mTableId = intent.getStringExtra(Extras.EXTRA_TABLE_ID);
	}

	@Override
	public void initUi() {
		ViewUtils.setVisible(mDelimiter, true);
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

		if(mDetails != null && mBtnPay != null) {
			final String text = AmountHelper.format(mDetails.getAmount()) + getString(R.string.currency_suffix_ruble);
			mBtnPay.setText(getString(R.string.pay_amount, text));
			GradientDrawable sd = (GradientDrawable) mBtnPay.getBackground();
			sd.setColor(getResources().getColor(R.color.btn_pay_green));
			sd.invalidateSelf();
			if(!mIsDemo) {
				mBtnPay.setEnabled(false);
			}
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
					final CardInfo cardInfo = new CardInfo.Builder()
							.cardId(card.getExternalCardId())
							.cvv(cvv)
							.build();
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

	}

	private void askForRemoval(final Card card) {
		final String title = getString(R.string.card_removal_confirmation, card.getMaskedPan(), card.getAssociation());
		DialogUtils.showDeleteDialog(this, title, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeCard(card);
			}
		});
	}

	private void removeCard(final Card card) {
		final AcquiringData acquiringData = OmnomApplication.get(getActivity()).getConfig().getAcquiringData();
		UserData userData = UserData.create(OmnomApplication.get(getActivity()).getUserProfile().getUser());
		String cvv = OmnomApplication.get(getActivity()).getConfig().getAcquiringData().getTestCvv();
		final CardInfo cardInfo = new CardInfo.Builder()
				.cardId(card.getExternalCardId())
				.pan(card.getMaskedPan())
				.mixpanelPan(card.getMaskedPanMixpanel())
				.cvv(cvv)
				.build();
		mDeleteCardSubscription = AppObservable.bindActivity(this, mAcquiring.deleteCard(acquiringData, userData, cardInfo))
		                                       .flatMap(new Func1<com.omnom.android.acquiring.mailru.response.CardDeleteResponse,
				                                       Observable<CardDeleteResponse>>() {
			                                       @Override
			                                       public Observable<CardDeleteResponse> call(com.omnom.android.acquiring.mailru
					                                                                                  .response.CardDeleteResponse
					                                                                                  cardDeleteResponse) {
				                                       if(cardDeleteResponse.isSuccess()) {
					                                       reportMixPanelSuccess(cardInfo);
					                                       return api.deleteCard(card.getId());
				                                       } else {
					                                       if(cardDeleteResponse.getError() != null) {
						                                       reportMixPanelFail(cardInfo, cardDeleteResponse.getError());
					                                       }
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
				}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
					@Override
					public void onError(Throwable throwable) {
						cardRemovalError();
					}
				});
	}

	private void reportMixPanelSuccess(final CardInfo cardInfo) {
		getMixPanelHelper().track(OMNOM, new CardDeletedMixpanelEvent(getUserData(), cardInfo));
	}

	private void reportMixPanelFail(final CardInfo cardInfo, final AcquiringResponseError error) {
		getMixPanelHelper().track(OMNOM, new CardDeletedMixpanelEvent(getUserData(), cardInfo, error));
	}

	private void onRemoveSuccess(final Card card) {
		final CardsAdapter adapter = (CardsAdapter) mList.getAdapter();
		adapter.remove(card);
		updateCardsSelection(adapter, card);
	}

	private void updateCardsSelection(CardsAdapter adapter, Card card) {
		final String selectedId = mPreferences.getCardId(getActivity());
		if(card.getExternalCardId().equals(selectedId)) {
			if(mBtnPay != null) {
				mBtnPay.setEnabled(selectCard(adapter, selectedId));
			}
		}
	}

	private void cardRemovalError() {
		cardRemovalError(null);
	}

	private void cardRemovalError(String message) {
		Log.w(TAG, "error = " + message);
		if(message != null) {
			Toast.makeText(this, getString(R.string.unable_to_remove_card_number_try_againt_later, message),
			               Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, getString(R.string.unable_to_remove_card), Toast.LENGTH_LONG).show();
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
			mCardsSubscription = AppObservable.bindActivity(this, api.getCards().delaySubscription(1000, TimeUnit.MILLISECONDS))
			                                  .subscribe(
					                                  new Action1<CardsResponse>() {
						                                  @Override
						                                  public void call(final CardsResponse cards) {
							                                  final List<Card> cardsList = cards.getCards();
							                                  mList.setAdapter(new CardsAdapter(getActivity(), cardsList, false));
							                                  boolean isSelected = selectCard((CardsAdapter) mList.getAdapter(),
							                                                                  mPreferences.getCardId(getActivity()));
							                                  if(mBtnPay != null) {
								                                  mBtnPay.setEnabled(isSelected);
							                                  }
							                                  mPanelTop.showProgress(false);
							                                  mPanelTop.showButtonRight(true);
						                                  }
					                                  }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
						                                  @Override
						                                  public void onError(Throwable throwable) {
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

	@Optional
	@OnClick(R.id.btn_pay)
	protected void onPay() {
		final String cardId = getPreferences().getCardId(this);
		if(mList != null && mList.getAdapter() != null) {
			final Card card = ((CardsAdapter) mList.getAdapter()).getSelectedCard();
			if(card != null) {
				String cvv = OmnomApplication.get(getActivity()).getConfig().getAcquiringData().getTestCvv();
				final CardInfo cardInfo = new CardInfo.Builder()
						.cardId(cardId)
						.pan(card.getMaskedPan())
						.mixpanelPan(card.getMaskedPanMixpanel())
						.cvv(cvv)
						.build();
				pay(cardInfo);
			}
		}
	}

	private void pay(final CardInfo cardInfo) {
		if(mDetails != null) {
			if(mOrder != null) {
				PaymentProcessActivity.start(getActivity(), REQUEST_PAYMENT, mDetails, mOrder, cardInfo, mIsDemo, mRestaurant);
			} else {
				PaymentProcessActivity.start(getActivity(), REQUEST_PAYMENT, mDetails, mUserOrder, cardInfo, mWishResponse, mIsDemo,
				                             mRestaurant);
			}
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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
		} else if(resultCode == RESULT_ENTER_CARD_AND_PAY) {
			CardAddActivity.start(this, mDetails != null ? mDetails.getAmount() : 0,
			                      CardAddActivity.TYPE_ENTER_AND_PAY, entranceData,
								  REQUEST_CODE_CARD_ADD);
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
		int type = CardAddActivity.TYPE_BIND;
		if(mDetails != null) {
			type = CardAddActivity.TYPE_BIND_OR_PAY;
		}
		CardAddActivity.start(this, mDetails != null ? mDetails.getAmount() : 0,
		                      type, entranceData, REQUEST_CODE_CARD_ADD);
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
