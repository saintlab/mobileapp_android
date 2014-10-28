package com.omnom.android.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
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
import com.omnom.android.activity.CardsActivity;
import com.omnom.android.adapter.OrderItemsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.bill.BillRequest;
import com.omnom.android.restaurateur.model.bill.BillResponse;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderHelper;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.OmnomListView;

import java.math.BigDecimal;
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
	public static final int MODE_AMOUNT = 0;

	public static final int MODE_TIPS = 1;

	public static final int WRONG_VALUE = -1;

	public static final BigDecimal WRONG_AMOUNT = BigDecimal.valueOf(WRONG_VALUE);

	private BigDecimal mLastAmount = WRONG_AMOUNT;

	private int mMode = WRONG_VALUE;

	private int mCheckedId = WRONG_VALUE;

	public static final int PICKER_MAX_VALUE = 200;

	public static final int PICKER_MIN_VALUE = 0;

	public static final int PAYMENT_TRANSLATION_Y = 200;

	public static final int LIST_TRANSLATION_Y = 600;

	private static final String ARG_ORDER = "order";

	private static final String ARG_COLOR = "color";

	private static final String TAG = OrderFragment.class.getSimpleName();

	public static Fragment newInstance(Parcelable parcelable, final int bgColor) {
		final OrderFragment fragment = new OrderFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, parcelable);
		args.putInt(ARG_COLOR, bgColor);
		fragment.setArguments(args);
		return fragment;
	}

	@Inject
	protected Acquiring mAcquiring;

	@Inject
	protected RestaurateurObeservableApi api;

	@InjectViews({R.id.btn_pay, R.id.radio_tips, R.id.txt_tips_title})
	protected List<View> viewsAmountHide;

	@InjectViews({R.id.btn_apply, R.id.btn_cancel})
	protected List<View> viewsAmountShow;

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

	@InjectView(R.id.edit_custom_tips)
	protected TextView txtCustomTips;

	@InjectView(R.id.tips_picker)
	protected NumberPicker pickerTips;

	@InjectView(R.id.panel_order_payment)
	protected View panelPayment;

	@InjectView(R.id.btn_pay)
	protected Button btnPay;

	@InjectView(R.id.btn_apply)
	protected Button btnApply;

	@InjectView(R.id.btn_cancel)
	protected Button btnCancel;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.txt_payment_title)
	protected TextView txtPaymentTitle;

	@InjectView(R.id.txt_tips_hint)
	protected TextView txtTipsHint;

	@InjectView(R.id.txt_tips_title)
	protected TextView txtTipsTitle;

	@InjectView(R.id.txt_tips_amount_hint)
	protected TextView txtTipsAmountHint;

	private Order mOrder;

	private Gson gson;

	private Subscription mBillSubscription;

	private Subscription mPaySubscription;

	private TextView txtFooterAmount;

	private TextView txtFooterToPay;

	private boolean mCurrentKeyboardVisility = false;

	private boolean mApply = false;

	private boolean mPaymentTitleChanged;

	private int mAccentColor;

	private float mFontNormal;

	private float mFontSmall;

	public OrderFragment() {
	}

	private String getCurrencySuffix() {
		return getString(R.string.currency_ruble);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final View view = inflater.inflate(R.layout.fragment_order, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
		mFontNormal = getResources().getDimension(R.dimen.font_small);
		mFontSmall = getResources().getDimension(R.dimen.font_xsmall);

		rootView.setBackgroundColor(mAccentColor);
		btnPay.setTextColor(mAccentColor);

		initPicker();
		updateCustomTipsText(0);
		initList();

		initRadioButtons();
		initFooter();
		initKeyboardListener();
		initAmount();

		btnTips2.setChecked(true);
		final BigDecimal amount = getEnteredAmount();
		updatePaymentTipsAmount(btnTips1, amount);
		updatePaymentTipsAmount(btnTips2, amount);
		updatePaymentTipsAmount(btnTips3, amount);
		updatePaymentTipsAmount(btnTips4, amount);
	}

	@OnClick(R.id.btn_pay)
	protected void onPay(View v) {
		pay(getEnteredAmount().doubleValue(), 0);
	}

	private void initList() {
		list.setAdapter(new OrderItemsAdapter(getActivity(), mOrder.getItems()));
		AndroidUtils.scrollEnd(list);
		// list.setTranslationY(-LIST_TRANSLATION_Y);
		// list.setEnabled(false);
		// list.setScrollingEnabled(false);
	}

	private void initAmount() {
		editAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					doApply(v);
					return true;
				}
				return false;
			}
		});
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
				final BigDecimal amount = getEnteredAmount();
				updatePaymentTipsAmount(amount);
				updatePayButton(amount.add(getSelectedTips(amount)));
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
	}

	private void updatePayButton(BigDecimal amount) {
		btnPay.setText(getString(R.string.pay_amount, amount + getCurrencySuffix()));
	}

	private void initKeyboardListener() {
		final View activityRootView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						if(mCurrentKeyboardVisility != isVisible) {
							ButterKnife.apply(viewsAmountHide, ViewUtils.VISIBLITY_ALPHA, !isVisible);
							ButterKnife.apply(viewsAmountShow, ViewUtils.VISIBLITY_ALPHA2, isVisible);

							list.animate().translationYBy(isVisible ? -LIST_TRANSLATION_Y : LIST_TRANSLATION_Y).start();
							panelPayment.animate().yBy(isVisible ? PAYMENT_TRANSLATION_Y : -PAYMENT_TRANSLATION_Y).start();

							mCurrentKeyboardVisility = isVisible;
							editAmount.setCursorVisible(isVisible);

							if(isVisible) {
								mMode = MODE_AMOUNT;
								mLastAmount = getEnteredAmount();
								txtPaymentTitle.setText(R.string.i_m_going_to_pay);
							} else {
								if(!mApply) {
									editAmount.setText(StringUtils.formatCurrency(mLastAmount, getCurrencySuffix()));
								} else {
									editAmount.setText(StringUtils.formatCurrency(editAmount.getText().toString(), getCurrencySuffix()));
								}
								mApply = false;
								mLastAmount = WRONG_AMOUNT;
							}
						}
					}
				}));
	}

	private void initFooter() {
		final View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_footer, null, false);
		list.addFooterView(footerView);
		txtFooterAmount = (TextView) footerView.findViewById(R.id.txt_overall);
		txtFooterAmount.setText(getString(R.string.order_overall, StringUtils.formatCurrency(mOrder.getTotalAmount())));

		txtFooterToPay = (TextView) footerView.findViewById(R.id.txt_to_pay);
		txtFooterToPay.setText(getString(R.string.order_paid, StringUtils.formatCurrency(mOrder.getPaidAmount())));
	}

	private void initRadioButtons() {
		btnTips4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				showCustomTips(true);
			}
		});
		final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton btn, final boolean isChecked) {
				if(isChecked) {
					final BigDecimal amount = getEnteredAmount();
					mCheckedId = btn.getId();
					updatePaymentTipsAmount(btn, amount);
					btnTips4.setTag(String.valueOf(WRONG_VALUE));
					btnTips4.setChecked(false);
					updatePaymentTipsAmount(btnTips4, amount);
					final double selectedTips = getSelectedTips(btn, amount);
					updatePayButton(amount.add(BigDecimal.valueOf(selectedTips)));
				} else {
					updatePaymentTipsAmount(btn, getEnteredAmount());
				}
			}
		};

		btnTips1.setOnCheckedChangeListener(listener);
		btnTips1.setTag(R.id.tip, 30);
		btnTips2.setOnCheckedChangeListener(listener);
		btnTips2.setTag(R.id.tip, 50);
		btnTips3.setOnCheckedChangeListener(listener);
		btnTips3.setTag(R.id.tip, 100);
		btnTips4.setTag(R.id.tip, WRONG_VALUE);
	}

	private void initPicker() {
		pickerTips.setMinValue(PICKER_MIN_VALUE);
		pickerTips.setMaxValue(PICKER_MAX_VALUE);
		pickerTips.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		pickerTips.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
				updateCustomTipsText(newVal);
			}
		});
		pickerTips.setValue(0);
	}

	private void updateCustomTipsText(final int newVal) {
		txtCustomTips.setText(getString(R.string.tip_percent, newVal));
		final double tips = OrderHelper.getTipsAmount(getEnteredAmount(), newVal);
		final String tipsFormatted = StringUtils.formatCurrency(BigDecimal.valueOf(tips), getCurrencySuffix());
		txtTipsAmountHint.setText(getString(R.string.tip_hint_or, tipsFormatted));
	}

	private void showCustomTips(boolean visible) {
		list.animate().translationYBy(visible ? -LIST_TRANSLATION_Y : LIST_TRANSLATION_Y).start();
		panelPayment.animate().yBy(visible ? -PAYMENT_TRANSLATION_Y : PAYMENT_TRANSLATION_Y).start();
		pickerTips.setVisibility(visible ? View.VISIBLE : View.GONE);

		txtTipsHint.setVisibility(visible ? View.VISIBLE : View.GONE);
		editAmount.setVisibility(visible ? View.GONE : View.VISIBLE);
		txtCustomTips.setVisibility(visible ? View.VISIBLE : View.GONE);
		radioGroup.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
		txtPaymentTitle.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
		txtTipsTitle.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
		txtTipsAmountHint.setVisibility(visible ? View.VISIBLE : View.GONE);

		btnApply.setAlpha(1);
		btnApply.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
		btnCancel.setAlpha(1);
		btnCancel.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
		mMode = visible ? MODE_TIPS : WRONG_VALUE;

		updateCustomTipsText(pickerTips.getValue());
	}

	private BigDecimal getEnteredAmount() {
		final String filtered = StringUtils.filterAmount(editAmount.getText().toString());
		if(TextUtils.isEmpty(filtered)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(filtered);
	}

	@OnClick(R.id.btn_apply)
	protected void doApply(View v) {
		if(mMode == MODE_AMOUNT) {
			mApply = true;
			AndroidUtils.hideKeyboard(getActivity());
			mPaymentTitleChanged = true;
			txtPaymentTitle.setText(R.string.i_m_going_to_pay);
			mMode = WRONG_VALUE;
		}
		if(mMode == MODE_TIPS) {
			showCustomTips(false);

			btnTips4.setChecked(true);
			btnTips4.setTag(String.valueOf(pickerTips.getValue()));
			final BigDecimal amount = getEnteredAmount();
			updatePaymentTipsAmount(btnTips4, amount);
			final double otherTips = getOtherTips(amount);
			updatePayButton(amount.add(BigDecimal.valueOf(otherTips)));
		}
	}

	@OnClick(R.id.btn_cancel)
	protected void doCancel(View v) {
		if(mMode == MODE_AMOUNT) {
			mApply = false;
			AndroidUtils.hideKeyboard(getActivity());
			if(!mPaymentTitleChanged) {
				txtPaymentTitle.setText(R.string.to_be_paid);
			}
			mMode = WRONG_VALUE;
		}
		if(mMode == MODE_TIPS) {
			showCustomTips(false);
			if(!btnTips4.getTag().equals(String.valueOf(WRONG_VALUE))) {
				return;
			}
			radioGroup.check(mCheckedId);
			mCheckedId = WRONG_VALUE;

			btnTips4.setTag(String.valueOf(WRONG_VALUE));
			updatePaymentTipsAmount(btnTips4, getEnteredAmount());
		}
	}

	private BigDecimal getSelectedTips(final BigDecimal amount) {
		final CompoundButton btn = findById(getActivity(), radioGroup.getCheckedRadioButtonId());
		if(btn == null) {
			return BigDecimal.ZERO;
		}
		if(OrderHelper.isPercentTips(mOrder, amount) || btn.getId() == R.id.radio_tips_4) {
			final String tag = (String) btn.getTag();
			final int percent = Integer.parseInt(tag);
			final int tipsAmount = OrderHelper.getTipsAmount(amount, percent);
			return BigDecimal.valueOf(tipsAmount);
		} else {
			final int tag = (Integer) btn.getTag(R.id.tip);
			return BigDecimal.valueOf(tag);
		}
	}

	private double getSelectedTips(final CompoundButton btn, final BigDecimal amount) {
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

	private double getOtherTips(final BigDecimal amount) {
		if(btnTips4 == null) {
			return 0;
		}
		final String tag = (String) btnTips4.getTag();
		final int percent = Integer.parseInt(tag);
		return OrderHelper.getTipsAmount(amount, percent);
	}

	private void updatePaymentTipsAmount(BigDecimal amount) {
		updatePaymentTipsAmount(btnTips1, amount);
		updatePaymentTipsAmount(btnTips2, amount);
		updatePaymentTipsAmount(btnTips3, amount);
		updatePaymentTipsAmount(btnTips4, amount);
	}

	private void updatePaymentTipsAmount(final CompoundButton btn, final BigDecimal amount) {
		final boolean percentTips = OrderHelper.isPercentTips(mOrder, amount);
		final boolean isOther = btn.getId() == R.id.radio_tips_4;
		if(percentTips && !isOther) {
			final String tag = (String) btn.getTag();
			final int percent = Integer.parseInt(tag);
			if(percent == WRONG_VALUE) {
				btn.setTextSize(mFontSmall);
				btn.setText(getString(R.string.tips_another));
				return;
			}
			btn.setText(getString(R.string.tip_percent, tag));
			if(btn.isChecked()) {
				btn.setTextSize(mFontNormal);
			} else {
				btn.setTextSize(mFontSmall);
			}
		} else {
			if(isOther) {
				if(btn.isChecked()) {
					btn.setText(getString(R.string.tip_percent, btn.getTag()));
					btn.setTextSize(mFontNormal);
				} else {
					btn.setText(getString(R.string.tips_another));
					btn.setTextSize(mFontSmall);
				}
			} else {
				final int fixedTip = (Integer) btn.getTag(R.id.tip);
				if(fixedTip == WRONG_VALUE) {
					btn.setText(getString(R.string.tips_another));
					btn.setTextSize(mFontSmall);
					return;
				}
				btn.setText(String.valueOf(fixedTip));
				btn.setTextSize(mFontNormal);
			}
		}
	}

	private void pay(final double amount, final double tip) {
		final FragmentActivity activity = getActivity();
		final String cardSaved = OmnomApplication.get(activity).getPreferences().getCardData(activity);
		if(!TextUtils.isEmpty(cardSaved)) {
			final CardInfo cardData = gson.fromJson(cardSaved, CardInfo.class);
			cardData.setCardId(StringUtils.EMPTY_STRING);
			btnPay.setEnabled(false);
			final BillRequest request = BillRequest.create(amount, mOrder);
			mBillSubscription = AndroidObservable.bindActivity(activity, api.bill(request)).subscribe(new Action1<BillResponse>() {
				@Override
				public void call(final BillResponse response) {
					if(!response.hasErrors()) {
						tryToPay(cardData, response, amount, tip);
					} else {
						if(response.getError() != null) {
							showToast(activity, response.getError());
						} else if(response.getErrors() != null) {
							showToast(activity, response.getErrors().toString());
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
			CardsActivity.start(activity, amount, mAccentColor);
		}
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
			mAccentColor = getArguments().getInt(ARG_COLOR);
		}
	}
}