package com.omnom.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.CardsActivity;
import com.omnom.android.activity.OrdersActivity;
import com.omnom.android.adapter.OrderItemsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderHelper;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.OmnomListView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

import static butterknife.ButterKnife.findById;

public class OrderFragment extends Fragment {
	public static final int MODE_AMOUNT = 0;

	public static final int MODE_TIPS = 1;

	public static final int WRONG_VALUE = -1;

	public static final BigDecimal WRONG_AMOUNT = BigDecimal.valueOf(WRONG_VALUE);

	private BigDecimal mLastAmount = WRONG_AMOUNT;

	private int mMode = WRONG_VALUE;

	private int mCheckedId = WRONG_VALUE;

	public static final int LIST_TRASNLATION_ACTIVE = -380;

	public static final int LIST_HEIGHT = 800;

	public static final float FRAGMENT_SCALE_RATIO_SMALL = 0.8f;

	public static final float FRAGMENT_SCALE_RATIO_X_NORMAL = 1.0f;

	public static final int PICKER_MAX_VALUE = 200;

	public static final int PICKER_MIN_VALUE = 0;

	public static final int PAYMENT_TRANSLATION_Y = 200;

	public static final int LIST_TRANSLATION_Y = 600;

	public static class PaymentDetails implements Parcelable {
		public static final Creator<PaymentDetails> CREATOR = new Creator<PaymentDetails>() {
			@Override
			public PaymentDetails createFromParcel(Parcel in) {
				return new PaymentDetails(in);
			}

			@Override
			public PaymentDetails[] newArray(int size) {
				return new PaymentDetails[size];
			}
		};

		private double mAmount;

		private double mTip;

		public PaymentDetails(Parcel parcel) {
			mAmount = parcel.readDouble();
			mTip = parcel.readDouble();
		}

		public PaymentDetails(double amount, double tip) {
			mAmount = amount;
			mTip = tip;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			dest.writeDouble(mAmount);
			dest.writeDouble(mTip);
		}

		public double getAmount() {
			return mAmount;
		}
	}

	private static final String ARG_ORDER = "order";

	private static final String ARG_COLOR = "color";

	private static final String ARG_POSITION = "position";

	private static final String ARG_COUNT = "count";

	private static final String ARG_ANIMATE = "animate";

	public static Fragment newInstance(Order order, final int bgColor, final int postition, final int count,
	                                   final boolean animate) {
		final OrderFragment fragment = new OrderFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putInt(ARG_COLOR, bgColor);
		args.putInt(ARG_POSITION, postition);
		args.putInt(ARG_COUNT, count);
		args.putBoolean(ARG_ANIMATE, animate);
		fragment.setArguments(args);
		return fragment;
	}

	@Inject
	protected Bus mBus;

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

	@InjectView(R.id.txt_custom_tips)
	protected TextView txtCustomTips;

	@InjectView(R.id.txt_title)
	protected TextView txtTitle;

	@InjectView(R.id.tips_picker)
	protected com.omnom.android.utils.view.NumberPicker pickerTips;

	@InjectView(R.id.panel_order_payment)
	protected View panelPayment;

	@InjectView(R.id.btn_pay)
	protected TextView btnPay;

	@InjectView(R.id.btn_apply)
	protected ImageButton btnApply;

	@InjectView(R.id.btn_cancel)
	protected ImageButton btnCancel;

	@InjectView(R.id.txt_payment_title)
	protected TextView txtPaymentTitle;

	@InjectView(R.id.txt_already_paid)
	protected TextView txtAlreadyPaid;

	@InjectView(R.id.txt_tips_hint)
	protected TextView txtTipsHint;

	@InjectView(R.id.txt_tips_title)
	protected TextView txtTipsTitle;

	@InjectView(R.id.txt_tips_amount_hint)
	protected TextView txtTipsAmountHint;

	private Order mOrder;

	private boolean mCurrentKeyboardVisility = false;

	private boolean mApply = false;

	private boolean mPaymentTitleChanged;

	private int mAccentColor;

	private float mFontNormal;

	private float mFontSmall;

	private View mFragmentView;

	private int mPosition;

	private boolean mAnimate;

	private int mOrdersCount;

	private SparseBooleanArrayParcelable mCheckedStates = new SparseBooleanArrayParcelable();

	private View mFooterView1;

	private View mFooterView2;

	public OrderFragment() {
	}

	private String getCurrencySuffix() {
		return getString(R.string.currency_ruble);
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		OmnomApplication.get(getActivity()).inject(this);
		mBus.register(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mBus.unregister(this);
	}

	@Subscribe
	public void onSplitCommit(OrderSplitCommitEvent event) {
		final BigDecimal amount = event.getAmount();
		final String s = StringUtils.formatCurrency(amount, getCurrencySuffix());
		editAmount.setText(s);
		updatePaymentTipsAmount(amount);
		updatePayButton(amount.add(getSelectedTips(amount)));
	}

	@Subscribe
	public void onOrderItemSelected(OrderItemSelectedEvent event) {
		mCheckedStates.put(event.getPosition(), event.isSelected());
		if(hasSelectedItems()) {
			initFooter2();
		} else {
			initFooter(true);
		}
	}

	private boolean hasSelectedItems() {
		final int size = mCheckedStates.size();
		final ArrayList<OrderItem> result = new ArrayList<OrderItem>(size);
		for(int i = 0; i < size; i++) {
			int key = mCheckedStates.keyAt(i);
			if(mCheckedStates.get(key) && key < list.getCount() - 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final View view = inflater.inflate(R.layout.fragment_order, container, false);
		mFontNormal = getResources().getDimension(R.dimen.font_xlarge);
		mFontSmall = getResources().getDimension(R.dimen.font_large);
		ButterKnife.inject(this, view);
		return view;
	}

	private AnimatorSet getListClickAnimator(float scaleRation, int listTranslation) {
		final ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFragmentView,
		                                                     View.SCALE_X,
		                                                     mFragmentView.getScaleX(),
		                                                     scaleRation);
		final ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFragmentView,
		                                                     View.SCALE_Y,
		                                                     mFragmentView.getScaleY(),
		                                                     scaleRation);

		final ObjectAnimator ty = ObjectAnimator.ofFloat(list, View.TRANSLATION_Y, list.getTranslationY(), listTranslation);
		final AnimatorSet as = new AnimatorSet();
		as.playTogether(scaleX, scaleY, ty);
		return as;
	}

	public void downscale(final Runnable runnable) {
		if(mFooterView1 != null) {
			final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
			ViewUtils.setVisible(billSplit, false);
		}
		if(mFooterView2 != null) {
			final View billSplit2 = mFooterView2.findViewById(R.id.panel_container);
			ViewUtils.setVisible(billSplit2, false);
		}

		AnimationUtils.animateAlpha(panelPayment, false);
		list.setScrollingEnabled(false);
		final AnimatorSet as = getListClickAnimator(FRAGMENT_SCALE_RATIO_SMALL, 0);
		as.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				if(runnable != null) {
					runnable.run();
				}
			}
		});
		as.start();
		AnimationUtils.animateAlpha(txtTitle, true);
	}

	@Override
	public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
		mFragmentView = view;

		ViewUtils.setVisible(panelPayment, false);
		mFragmentView.setScaleX(FRAGMENT_SCALE_RATIO_SMALL);
		mFragmentView.setScaleY(FRAGMENT_SCALE_RATIO_SMALL);

		if(mAnimate) {
			mFragmentView.animate().translationYBy(-LIST_HEIGHT).setDuration(0).start();
			mFragmentView.animate().translationYBy(LIST_HEIGHT).setStartDelay((mPosition + 1) * 350).setDuration(850).start();
		}

		btnPay.setTextColor(mAccentColor);
		txtTitle.setText(getString(R.string.bill_number_, mPosition + 1));
		// rootView.setBackgroundColor(mAccentColor);

		initPicker();
		updateCustomTipsText(0);
		initList();

		initRadioButtons();
		initFooter(false);
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
		final PaymentDetails paymentDetails = new PaymentDetails(getEnteredAmount().doubleValue(), 0);
		CardsActivity.start(getActivity(), mOrder, paymentDetails, mAccentColor, OrdersActivity.REQUEST_CODE_CARDS);
	}

	private void initList() {
		list.setAdapter(new OrderItemsAdapter(getActivity(), mOrder.getItems(), true));
		list.setScrollingEnabled(false);
		list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		if(mAnimate) {
			AnimationUtils.scaleHeight(list, LIST_HEIGHT);
		} else {
			ViewUtils.setHeight(list, LIST_HEIGHT);
		}

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				final OrdersActivity activity = (OrdersActivity) getActivity();
				if(isDownscaled()) {
					if(activity.checkFragment(OrderFragment.this)) {
						zoomInFragment(activity);
					}
				} else {
					splitBill();
				}
			}
		});
	}

	private void zoomInFragment(final OrdersActivity activity) {
		if(mFooterView1 != null) {
			final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
			ViewUtils.setVisible(billSplit, true);
		}
		if(mFooterView2 != null) {
			final View billSplit2 = mFooterView2.findViewById(R.id.panel_container);
			ViewUtils.setVisible(billSplit2, true);
		}
		AnimationUtils.animateAlpha(panelPayment, true);
		AnimationUtils.animateAlpha(txtTitle, false);
		AndroidUtils.scrollEnd(list);
		if(isFirstItem() || isLastItem()) {
			activity.animatePageMargingFirstOrLast(isFirstItem());
		} else {
			activity.animatePageMarginMiddle();
		}
		getListClickAnimator(FRAGMENT_SCALE_RATIO_X_NORMAL, LIST_TRASNLATION_ACTIVE).start();
		// list.setScrollingEnabled(true);
	}

	private boolean isLastItem() {return mPosition == mOrdersCount - 1;}

	private boolean isFirstItem() {return mPosition == 0;}

	public boolean isDownscaled() {return list.getTranslationY() == 0;}

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
		txtAlreadyPaid.setText(getString(R.string.already_paid, StringUtils.formatCurrency(mOrder.getPaidAmount(), getCurrencySuffix())));
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

	private void initFooter(final boolean visible) {
		if(list.getFooterViewsCount() > 0) {
			list.removeFooterView(mFooterView1);
			list.removeFooterView(mFooterView2);
		}
		mFooterView1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_footer, null, false);
		list.addFooterView(mFooterView1);
		final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
		ViewUtils.setVisible(billSplit, visible);
		billSplit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(!isDownscaled()) {
					splitBill();
				} else {
					zoomInFragment((OrdersActivity) getActivity());
				}
			}
		});
	}

	private void initFooter2() {
		if(list.getFooterViewsCount() > 0) {
			list.removeFooterView(mFooterView1);
			list.removeFooterView(mFooterView2);
		}
		mFooterView2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_footer_cancel, null, false);
		list.addFooterView(mFooterView2);
		mFooterView2.findViewById(R.id.txt_edit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				splitBill();
			}
		});
		mFooterView2.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				cancelSplit();
			}
		});
	}

	private void cancelSplit() {
		mCheckedStates.clear();
		initFooter(true);
	}

	private void splitBill() {
		final BillSplitFragment billSplitFragment = BillSplitFragment.newInstance(mOrder, mCheckedStates);
		getFragmentManager().beginTransaction().add(android.R.id.content, billSplitFragment, BillSplitFragment.TAG).commit();
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
		pickerTips.setOnValueChangedListener(new com.omnom.android.utils.view.NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(final com.omnom.android.utils.view.NumberPicker picker, final int oldVal, final int newVal) {
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
		txtAlreadyPaid.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
		txtTipsTitle.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
		txtTipsAmountHint.setVisibility(visible ? View.VISIBLE : View.GONE);

		btnApply.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
		btnApply.setAlpha(1.0f);
		btnApply.invalidate();
		btnApply.invalidateDrawable(btnApply.getDrawable());
		btnCancel.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
		btnCancel.setAlpha(1.0f);
		btnCancel.invalidate();
		btnCancel.invalidateDrawable(btnCancel.getDrawable());
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
				btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontNormal);
			} else {
				btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSmall);
			}
		} else {
			if(isOther) {
				if(btn.isChecked()) {
					btn.setText(getString(R.string.tip_percent, btn.getTag()));
					btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontNormal);
				} else {
					btn.setText(getString(R.string.tips_another));
					btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSmall);
				}
			} else {
				final int fixedTip = (Integer) btn.getTag(R.id.tip);
				if(fixedTip == WRONG_VALUE) {
					btn.setText(getString(R.string.tips_another));
					btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSmall);
					return;
				}
				btn.setText(String.valueOf(fixedTip));
				btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontNormal);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mAccentColor = getArguments().getInt(ARG_COLOR);
			mAnimate = getArguments().getBoolean(ARG_ANIMATE, false);
			mPosition = getArguments().getInt(ARG_POSITION);
			mOrdersCount = getArguments().getInt(ARG_COUNT);
		}
	}

	public boolean isInPickerMode() {
		return mMode == MODE_TIPS;
	}

	public boolean onBackPressed() {
		if(isInPickerMode()) {
			doCancel(null);
			return true;
		}
		final Fragment fragmentByTag = getFragmentManager().findFragmentByTag(BillSplitFragment.TAG);
		if(fragmentByTag != null) {
			BillSplitFragment splitFragment = (BillSplitFragment) fragmentByTag;
			splitFragment.hide();
			return true;
		}
		return false;
	}

	public boolean isInSplitMode() {
		final Fragment splitFragment = getFragmentManager().findFragmentByTag(BillSplitFragment.TAG);
		return splitFragment != null;
	}
}
