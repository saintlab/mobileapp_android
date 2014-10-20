package com.omnom.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
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
import com.omnom.android.activity.AddCardActivity;
import com.omnom.android.adapter.OrderItemsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderHelper;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.OmnomListView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class OrderFragment extends Fragment {
	public static final String CURRENCY_RUBLE = "\uf5fc";
	public static final int WRONG_VALUE = -1;
	private double mLastAmount = WRONG_VALUE;
	private static final String ARG_ORDER = "order";
	private static final String TAG = OrderFragment.class.getSimpleName();

	public static Fragment newInstance(Parcelable parcelable) {
		final OrderFragment fragment = new OrderFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, parcelable);
		fragment.setArguments(args);
		return fragment;
	}

	@Inject
	protected Acquiring mAcquiring;

	@Inject
	protected RestaurateurObeservableApi api;

	@InjectViews({R.id.btn_pay, R.id.radio_tips})
	protected List<View> viewsAmountHide;
	@InjectView(android.R.id.list)
	protected OmnomListView list = null;
	@InjectView(R.id.radio_tips)
	protected RadioGroup radioGroup;
	@InjectView(R.id.radio_tips_1)
	protected RadioButton btnTips1;
	@InjectView(R.id.radio_tips_2)
	protected RadioButton btnTips2;
	@InjectView(R.id.radio_tips_3)
	protected RadioButton btnTips3;
	@InjectView(R.id.radio_tips_4)
	protected RadioButton btnTips4;
	@InjectView(R.id.edit_payment_amount)
	protected EditText editAmount;
	@InjectView(R.id.btn_pay)
	protected Button btnPay;
	private Order mOrder;
	private Gson gson;
	private Subscription mBillSubscription;
	private Subscription mPaySubscription;
	private TextView txtFooterAmount;
	private TextView txtFooterToPay;
	private boolean mCurrentKeyboardVisility = false;
	private boolean mApply = false;

	public OrderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		return inflater.inflate(R.layout.fragment_order, container, false);
	}

	private String getCurrencySuffix() {
		return CURRENCY_RUBLE;
	}

	@Override
	public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.inject(this, view);
		list.setAdapter(new OrderItemsAdapter(getActivity(), mOrder.getItems()));
		list.setSelection(mOrder.getItems().size() - 1);
		// list.setTranslationY(-600);
		// list.setEnabled(false);
		// list.setScrollingEnabled(false);

		btnPay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				final double amount = Double.parseDouble(editAmount.getText().toString());
				pay(amount);
			}
		});

		final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton btn, final boolean isChecked) {
				if(isChecked) {
					final double amount = getEnteredAmount();
					updatePaymentTipsAmount(btn, amount);
					btnPay.setText("Оплатить " + (amount + getSelectedTips(btn, amount)));
				} else {
					final double amount = getEnteredAmount();
					updatePaymentTipsAmount(btn, amount);
				}
			}
		};

		btnTips1.setOnCheckedChangeListener(listener);
		btnTips1.setTag(R.id.tip, 30);
		btnTips2.setOnCheckedChangeListener(listener);
		btnTips2.setTag(R.id.tip, 50);
		btnTips3.setOnCheckedChangeListener(listener);
		btnTips3.setTag(R.id.tip, 100);
		btnTips4.setOnCheckedChangeListener(listener);
		btnTips4.setTag(R.id.tip, WRONG_VALUE);

		final View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_footer, null, false);
		list.addFooterView(footerView);
		txtFooterAmount = (TextView) footerView.findViewById(R.id.txt_overall);
		txtFooterAmount.setText(getString(R.string.order_overall, StringUtils.formatCurrency(mOrder.getTotalAmount())));

		txtFooterToPay = (TextView) footerView.findViewById(R.id.txt_to_pay);
		txtFooterToPay.setText(getString(R.string.order_paid, StringUtils.formatCurrency(mOrder.getPaidAmount())));

		final View activityRootView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						if(mCurrentKeyboardVisility != isVisible) {
							AnimationUtils.animateAlpha(radioGroup, !isVisible);
							AnimationUtils.animateAlpha(btnPay, !isVisible);
							list.animate().translationYBy(isVisible ? -600 : 600).start();
							final View byId = findById(view, R.id.panel_order_payment);
							byId.animate().yBy(isVisible ? 200 : -200).start();
							mCurrentKeyboardVisility = isVisible;
							editAmount.setCursorVisible(isVisible);

							if(isVisible) {
								mLastAmount = getEnteredAmount();
							} else {
								if(!mApply) {
									editAmount.setText(StringUtils.formatCurrency(mLastAmount, getCurrencySuffix()));
								} else {
									editAmount.setText(StringUtils.formatCurrency(editAmount.getText().toString(), getCurrencySuffix()));
								}
								mApply = false;
								mLastAmount = WRONG_VALUE;
							}
						}
					}
				}));

		editAmount.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				final String str = s.toString();
				if(!str.endsWith(getCurrencySuffix())) {
					final String text = editAmount.getText() + getCurrencySuffix();
					editAmount.setText(text);
					editAmount.setSelection(text.length() - 1);
				}
				final double amount = getEnteredAmount();
				updatePaymentTipsAmount(amount);
				btnPay.setText("Оплатить " + (amount + getSelectedTips(amount)));
			}
		});
		editAmount.setText(StringUtils.formatCurrency(mOrder.getAmountToPay()));
		editAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					int length = editAmount.getText().length();
					if(length >= 2) {
						editAmount.setSelection(length - 2);
					}
				}
			}
		});

		btnTips2.setChecked(true);
		final double amount = getEnteredAmount();

		updatePaymentTipsAmount(btnTips1, amount);
		updatePaymentTipsAmount(btnTips2, amount);
		updatePaymentTipsAmount(btnTips3, amount);
		updatePaymentTipsAmount(btnTips4, amount);
	}

	private double getEnteredAmount() {
		return Double.parseDouble(editAmount.getText().subSequence(0, editAmount.length() - 1).toString());
	}

	@OnClick(R.id.btn_apply)
	protected void doApply(View view) {
		mApply = true;
		AndroidUtils.hideKeyboard(getActivity());
	}

	@OnClick(R.id.btn_cancel)
	protected void doCancel(View view) {
		mApply = false;
		AndroidUtils.hideKeyboard(getActivity());
	}

	private double getSelectedTips(final double amount) {
		final CompoundButton btn = findById(getActivity(), radioGroup.getCheckedRadioButtonId());
		if(btn == null) {
			return 0;
		}
		if(OrderHelper.isPercentTips(mOrder, amount)) {
			final String tag = (String) btn.getTag();
			final int percent = Integer.parseInt(tag);
			return OrderHelper.getTipsAmount(amount, percent);
		} else {
			return (Integer) btn.getTag(R.id.tip);
		}
	}

	private double getSelectedTips(final CompoundButton btn, final double amount) {
		if(btn == null) {
			return 0;
		}
		if(OrderHelper.isPercentTips(mOrder, amount)) {
			final String tag = (String) btn.getTag();
			final int percent = Integer.parseInt(tag);
			return OrderHelper.getTipsAmount(amount, percent);
		} else {
			return (Integer) btn.getTag(R.id.tip);
		}
	}

	private void updatePaymentTipsAmount(double amount) {
		updatePaymentTipsAmount(btnTips1, amount);
		updatePaymentTipsAmount(btnTips2, amount);
		updatePaymentTipsAmount(btnTips3, amount);
		updatePaymentTipsAmount(btnTips4, amount);
	}

	private void updatePaymentTipsAmount(final CompoundButton btn, final double amount) {
		if(OrderHelper.isPercentTips(mOrder, amount)) {
			final String tag = (String) btn.getTag();
			final int percent = Integer.parseInt(tag);
			if(percent == WRONG_VALUE) {
				btn.setText(getString(R.string.tips_another));
				return;
			}
			if(btn.isChecked()) {
				btn.setText(percent + "%\n" + OrderHelper.getTipsAmount(amount, percent));
			} else {
				btn.setText(percent + "%");
			}
		} else {
			final int fixedTip = (Integer) btn.getTag(R.id.tip);
			if(fixedTip == WRONG_VALUE) {
				btn.setText(getString(R.string.tips_another));
				return;
			}
			btn.setText(String.valueOf(fixedTip));
		}
	}

	private void pay(final double amount) {
		final String cardSaved = OmnomApplication.get(getActivity()).getPreferences().getCardData(getActivity());
		if(!TextUtils.isEmpty(cardSaved)) {
			final CardInfo cardData = gson.fromJson(cardSaved, CardInfo.class);
			cardData.setCardId(StringUtils.EMPTY_STRING);
			btnPay.setEnabled(false);
			final BillRequest request = BillRequest.create(Double.toString(amount), mOrder);
			mBillSubscription = AndroidObservable.bindActivity(getActivity(), api.bill(request))
			                                     .subscribe(new Action1<BillResponse>() {
				                                     @Override
				                                     public void call(final BillResponse response) {
					                                     if(!response.hasErrors()) {
						                                     tryToPay(cardData, amount, response);
					                                     } else {
						                                     if(response.getError() != null) {
							                                     showToast(getActivity(), response.getError());
						                                     } else if(response.getErrors() != null) {
							                                     showToast(getActivity(), response.getErrors().toString());
						                                     }
						                                     btnPay.setEnabled(true);
					                                     }
				                                     }
			                                     }, new Action1<Throwable>() {
				                                     @Override
				                                     public void call(Throwable throwable) {
					                                     btnPay.setEnabled(true);
				                                     }
			                                     });
		} else {
			startActivity(new Intent(getActivity(), AddCardActivity.class));
		}
	}

	private void tryToPay(final CardInfo card, final double amount, BillResponse billData) {
		final com.omnom.android.auth.UserData cachedUser = OmnomApplication.get(getActivity()).getCachedUser();
		final UserData user = UserData.create(String.valueOf(cachedUser.getId()), cachedUser.getPhone());
		final MerchantData merchant = new MerchantData(getActivity());
		pay(billData, card, merchant, user, amount);
	}

	private void pay(BillResponse billData, final CardInfo cardInfo, MerchantData merchant, UserData user, double amount) {
		final ExtraData extra = MailRuExtra.create(0, billData.getMailRestaurantId());
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
		btnPay.setEnabled(true);
		showToast(getActivity(), "Unable to pay");
	}

	private void onPayOk(AcquiringPollingResponse response) {
		Log.d(TAG, "status = " + response.getStatus());
		btnPay.setEnabled(true);
		showToast(getActivity(), "Payment complete");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mBillSubscription);
		OmnomObservable.unsubscribe(mPaySubscription);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gson = new Gson();
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
		}
	}
}
