package com.omnom.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.CardsActivity;
import com.omnom.android.activity.LoginActivity;
import com.omnom.android.activity.OrdersActivity;
import com.omnom.android.adapter.OrderItemsAdapterSimple;
import com.omnom.android.auth.UserData;
import com.omnom.android.currency.Currency;
import com.omnom.android.currency.Money;
import com.omnom.android.entrance.TableEntranceData;
import com.omnom.android.fragment.events.OrderSplitCommitEvent;
import com.omnom.android.fragment.events.SplitHideEvent;
import com.omnom.android.listener.DecimalKeyListener;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.BillViewMixpanelEvent;
import com.omnom.android.mixpanel.model.MixpanelEvent;
import com.omnom.android.mixpanel.model.SplitWay;
import com.omnom.android.mixpanel.model.TipsWay;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderHelper;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.DialogUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.OmnomListView;
import com.omnom.android.view.AmountEditText;
import com.omnom.android.view.HeaderView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.Optional;

import static butterknife.ButterKnife.findById;

public class OrderFragment extends Fragment {
	public static final int MODE_AMOUNT = 0;

	public static final int MODE_TIPS = 1;

	public static final int WRONG_VALUE = -1;

	public static final BigDecimal WRONG_AMOUNT = BigDecimal.valueOf(WRONG_VALUE);

	public static final float FRAGMENT_SCALE_RATIO_SMALL = 0.8f;

	public static final float FRAGMENT_SCALE_RATIO_X_NORMAL = 1.0f;

	public static final int PICKER_MAX_VALUE = 200;

	public static final int PICKER_MIN_VALUE = 0;

	public static final int PICKER_DEFAULT_VALUE = 10;

	public static class TipData {

		public static int TYPE_PERCENT = 0;

		public static int TYPE_PREDEFINED = 1;

		public static int TYPE_NULL = -1;

		public static TipData fix(final TipData tips, Currency currency) {
			return new TipData(Money.getZero(currency), 0, tips.getType());
		}

		private final Money mAmount;

		private final int mValue;

		private final int mType;

		public TipData(Money amount, int value, int type) {
			mAmount = amount;
			mValue = value;
			mType = type;
		}

		public Money getAmount() {
			return mAmount;
		}

		public int getValue() {
			return mValue;
		}

		public int getType() {
			return mType;
		}

		public boolean validate() {
			if(mAmount.isLessThan(Money.getZero(mAmount.getCurrency()))) {
				return false;
			}
			if(mValue < 0) {
				return false;
			}
			return true;
		}
	}

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

		private Money mAmount;

		private Money mTip;

		private int mTipValue;

		private int mTipsWay;

		private int mSplitWay;

		private String tableId;

		private String mTransactionUrl = StringUtils.EMPTY_STRING;

		private long mTransactionTimestmap = 0;

		private String restaurantName;

		private String orderId;

		private int mBillId;

		public PaymentDetails(Parcel parcel) {
			mAmount = parcel.readParcelable(Money.class.getClassLoader());
			mTip = parcel.readParcelable(Money.class.getClassLoader());
			mTipValue = parcel.readInt();
			orderId = parcel.readString();
			tableId = parcel.readString();
			restaurantName = parcel.readString();
			mTipsWay = parcel.readInt();
			mSplitWay = parcel.readInt();
			mTransactionUrl = parcel.readString();
			mTransactionTimestmap = parcel.readLong();
			mBillId = parcel.readInt();
		}

		public PaymentDetails(Money amount, Money tip, Order order, TipsWay tipsWay, int tipValue, SplitWay splitWay) {
			mAmount = amount;
			mTip = tip;
			mTipValue = tipValue;
			tableId = order.getTableId();
			restaurantName = order.getRestaurantId();
			orderId = order.getId();
			mTipsWay = tipsWay.ordinal();
			mSplitWay = splitWay.ordinal();
		}

		public PaymentDetails(Money amount, Money tip, TipsWay tipsWay, int tipValue, SplitWay splitWay) {
			mAmount = amount;
			mTip = tip;
			mTipValue = tipValue;
			mTipsWay = tipsWay.ordinal();
			mSplitWay = splitWay.ordinal();
		}

		@Override
		public String toString() {
			return "PaymentDetails{" +
					"mAmount=" + mAmount +
					", mTip=" + mTip +
					", mTipValue=" + mTipValue +
					", mTipsWay=" + mTipsWay +
					", mSplitWay=" + mSplitWay +
					", tableId='" + tableId + '\'' +
					", mTransactionUrl='" + mTransactionUrl + '\'' +
					", mTransactionTimestmap=" + mTransactionTimestmap +
					", restaurantName='" + restaurantName + '\'' +
					", orderId='" + orderId + '\'' +
					", mBillId=" + mBillId +
					'}';
		}

		public boolean isSimilar(PaymentDetails details) {
			return details != null &&
					getBillId() == details.getBillId() &&
					getAmount() == details.getAmount() &&
					getTip() == details.getTip() &&
					getTipValue() == details.getTipValue();
		}

		public String getOrderId() {
			return orderId;
		}

		public String getRestaurantName() {
			return restaurantName;
		}

		public String getTableId() {
			return tableId;
		}

		public int getSplitWay() {
			return mSplitWay;
		}

		public int getTipsWay() {
			return mTipsWay;
		}

		public int getTipValue() {
			return mTipValue;
		}

		public Money getTip() {
			return mTip;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			dest.writeParcelable(mAmount, flags);
			dest.writeParcelable(mTip, flags);
			dest.writeInt(mTipValue);
			dest.writeString(orderId);
			dest.writeString(tableId);
			dest.writeString(restaurantName);
			dest.writeInt(mTipsWay);
			dest.writeInt(mSplitWay);
			dest.writeString(mTransactionUrl);
			dest.writeLong(mTransactionTimestmap);
			dest.writeInt(mBillId);
		}

		public Money getAmount() {
			return mAmount;
		}

		public String getTransactionUrl() {
			return mTransactionUrl;
		}

		public void setTransactionUrl(final String transactionUrl) {
			this.mTransactionUrl = transactionUrl;
		}

		public long getTransactionTimestmap() {
			return mTransactionTimestmap;
		}

		public void setTransactionTimestmap(final long transactionTimestmap) {
			mTransactionTimestmap = transactionTimestmap;
		}

		public int getBillId() {
			return mBillId;
		}

		public void setBillId(final int billId) {
			mBillId = billId;
		}
	}

	private static final String TAG = OrderFragment.class.getSimpleName();

	private static final String ARG_ORDER = "order";

	private static final String ARG_REQUEST_ID = "request_id";

	private static final String ARG_COLOR = "color";

	private static final String ARG_POSITION = "position";

	private static final String ARG_ANIMATE = "animate";

	private static final String ARG_SINGLE = "single";

	public static Fragment newInstance(Order order, String requestId, final int bgColor, final int position, final boolean animate,
	                                   final boolean isSingle) {
		final OrderFragment fragment = new OrderFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putString(ARG_REQUEST_ID, requestId);
		args.putInt(ARG_COLOR, bgColor);
		args.putInt(ARG_POSITION, position);
		args.putBoolean(ARG_ANIMATE, animate);
		args.putBoolean(ARG_SINGLE, isSingle);
		fragment.setArguments(args);
		return fragment;
	}

	@Inject
	protected Bus mBus;

	@Inject
	protected RestaurateurObservableApi api;

	@Optional
	@InjectViews({R.id.btn_pay, R.id.radio_tips, R.id.txt_tips_title})
	protected List<View> viewsAmountHide;

	@Optional
	@InjectViews({R.id.btn_apply, R.id.btn_cancel})
	protected List<View> viewsAmountShow;

	@InjectView(android.R.id.list)
	protected OmnomListView list = null;

	protected AmountEditText editAmount;

	protected View mPanelPayment;

	@Nullable
	protected TextView txtCustomTips;

	@InjectView(R.id.panel_top)
	protected HeaderView mHeader;

	@InjectView(R.id.txt_title)
	protected TextView txtTitle;

	@InjectView(R.id.tips_picker)
	protected com.omnom.android.utils.view.NumberPicker pickerTips;

	@InjectView(R.id.stub_payment_options)
	protected ViewStub stubPaymentOptions;

	@Nullable
	protected Button btnPay;

	@Nullable
	protected ImageButton btnApply;

	@Nullable
	protected ImageButton btnCancel;

	@Nullable
	protected TextView txtPaymentTitle;

	@Nullable
	protected TextView txtAlreadyPaid;

	@Nullable
	protected TextView txtTipsHint;

	@Nullable
	protected TextView txtTipsTitle;

	@Nullable
	protected TextView txtTipsAmountHint;

	private Money mLastAmount = null;

	private int mMode = WRONG_VALUE;

	private int mCheckedId = WRONG_VALUE;

	private int mTagSplitType = BillSplitFragment.SPLIT_TYPE_ITEMS;

	private int mGuestsCount = 1;

	private boolean isEditMode = false;

	private TipsWay mTipsWay = TipsWay.DEFAULT;

	private SplitWay mSplitWay = SplitWay.WASNT_USED;

	private List<CompoundButton> tipsButtons;

	@Nullable
	private RadioButton otherTips;

	@Nullable
	private RadioGroup radioGroup;

	private Order mOrder;

	private String mRequestId;

	private boolean mCurrentKeyboardVisility = false;

	private boolean mApply = false;

	private boolean mPaymentTitleChanged;

	private int mAccentColor;

	private float mFontNormal;

	private float mFontSmall;

	private View mFragmentView;

	private int mPosition;

	private boolean mAnimate;

	private SparseBooleanArrayParcelable mCheckedStates = new SparseBooleanArrayParcelable();

	private View mFooterView1;

	private View mFooterView2;

	private boolean mSingle;

	private int mListTraslationActive;

	private int mPaymentTranslationY;

	private int mTipsTranslationY;

	private int mListHeight;

	private OrderItemsAdapterSimple mAdapter;

	private boolean mSplitRunning;

	private boolean isAmountModified;

	private View btnEdit;

	private NumberFormat numberFormat;

	private char decimalSeparator;

	private Currency mCurrency;

	public OrderFragment() {
	}

	public View getFragmentView() {
		return mFragmentView;
	}

	private String getCurrencySuffix() {
		return getString(R.string.currency_suffix_ruble);
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
	public void onSplitHide(SplitHideEvent event) {
		if(event.getOrderId().equals(getOrder().getId())) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		updateDecimalSeparator();
		if(tipsButtons != null) {
			final Money amount = getEnteredAmount();
			updatePaymentTipsAmount(amount, tipsButtons);
		}
	}

	private void updateDecimalSeparator() {
		numberFormat = NumberFormat.getNumberInstance();
		decimalSeparator = ((DecimalFormat) numberFormat).getDecimalFormatSymbols().getDecimalSeparator();
		if(editAmount != null) {
			editAmount.updateSeparator();
		}
	}

	@Subscribe
	public void onSplitCommit(OrderSplitCommitEvent event) {
		if(event.getOrderId().equals(getOrder().getId())) {
			mTagSplitType = event.getSplitType();
			if(event.getSplitType() == BillSplitFragment.SPLIT_TYPE_PERSON) {
				mCheckedStates.clear();
				mAdapter.notifyDataSetChanged();
				// exclude case when split to single person
				if(event.getAmount().isLessThan(getOrder().getTotalMoney(mCurrency))) {
					mGuestsCount = event.getGuestsCount();
					initFooter2();
				}
				mSplitWay = SplitWay.BY_GUESTS;
			} else if(event.getSplitType() == BillSplitFragment.SPLIT_TYPE_ITEMS) {
				mCheckedStates = event.getStates();
				mAdapter.setStates(mCheckedStates);
				mAdapter.notifyDataSetChanged();
				if(AndroidUtils.hasSelectedItems(mCheckedStates, list.getCount())) {
					initFooter2();
				} else {
					initFooter(true);
				}
				mSplitWay = SplitWay.BY_POSITIONS;
			}
			final Money amount = event.getAmount();
			updateAmount(amount.getReadableCurrencyValue());
			updatePaymentTipsAmount(amount);
		}
	}

	public void onOrderUpdate(final Order order, final boolean skipDialog) {
		if(order != null && order.getId().equals(getOrder().getId())) {
			if(!isDownscaled() && !skipDialog) {
				mLastAmount = order.getMoneyToPay(mCurrency);
				DialogUtils.showDialog(getActivity(), R.string.order_updated, R.string.update,
				                       new DialogInterface.OnClickListener() {
					                       @Override
					                       public void onClick(final DialogInterface dialog,
					                                           final int which) {
						                       updateOrder(order);
					                       }
				                       });
			} else {
				updateOrder(order);
			}
		}
	}

	private void updateOrder(final Order order) {
		mOrder = order;
		mAdapter.updateItems(mOrder.getItems());
		AndroidUtils.scrollEnd(list);
		initAlreadyPaid();
		if(editAmount != null) {
			updateAmount(order.getMoneyToPay(mCurrency).getReadableCurrencyValue());
			final Money amount = getEnteredAmount();
			updatePaymentTipsAmount(amount, tipsButtons);
		}
		updateOverallAmount(mFooterView1);

		// update split fragment if it is present
		final BillSplitFragment splitFragment = (BillSplitFragment) getFragmentManager().findFragmentByTag(BillSplitFragment.TAG);
		if(splitFragment != null) {
			splitFragment.onOrderUpdate(order);
		}
	}

	private boolean isEverythingPaid(final Order order) {
		final Money paidMoney = Money.createFractional(order.getPaidAmount(), mCurrency);
		final Money totalMoney = order.getTotalMoney(mCurrency);
		return paidMoney.isGreatherOrEquals(totalMoney);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_order, container, false);

		mFontNormal = getResources().getDimension(R.dimen.font_xlarge);
		mFontSmall = getResources().getDimension(R.dimen.font_large);

		mPaymentTranslationY = getResources().getDimensionPixelSize(R.dimen.order_payment_translation_y);
		mTipsTranslationY = getResources().getDimensionPixelSize(R.dimen.order_tips_translation_y);

		configureSizing();

		ButterKnife.inject(this, view);
		return view;
	}

	private void configureSizing() {
		final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		final int heightPixels = displayMetrics.heightPixels;

		final int defaultPadding = ViewUtils.dipToPixels(getActivity(), 48);
		final int navigationBarHeight = AndroidUtils.getNavigationBarHeight(getActivity());
		mListHeight = heightPixels - navigationBarHeight - defaultPadding;

		final int bottomMin = getResources().getDimensionPixelSize(R.dimen.order_payment_height);
		mListTraslationActive = -bottomMin;

		// TODO: Find out generic solution for small devices like megafon login 1
		if(displayMetrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM) {
			final int mdpiPadding = ViewUtils.dipToPixels(getActivity(), 16);
			mListHeight -= mdpiPadding;
			mListTraslationActive += mdpiPadding;
		}
	}

	private AnimatorSet getListClickAnimator(float scaleRation, int listTranslation) {
		final ObjectAnimator scaleX = ObjectAnimator.ofFloat(mFragmentView, View.SCALE_X, mFragmentView.getScaleX(), scaleRation);
		final ObjectAnimator scaleY = ObjectAnimator.ofFloat(mFragmentView, View.SCALE_Y, mFragmentView.getScaleY(), scaleRation);

		final ObjectAnimator ty = ObjectAnimator.ofFloat(list, View.TRANSLATION_Y, list.getTranslationY(), listTranslation);
		final AnimatorSet as = new AnimatorSet();
		as.playTogether(scaleX, scaleY, ty);
		return as;
	}

	public void downscale(final boolean isAnimated) {
		initFooter(false);
		if(mFooterView2 != null) {
			final View billSplit2 = mFooterView2.findViewById(R.id.panel_container);
			ViewUtils.setVisibleGone(billSplit2, false);
		}
		ViewUtils.setVisibleGone(mHeader, false);
		list.setSwipeEnabled(false);
		if(isAnimated) {
			AnimationUtils.animateAlphaGone(getPanelPayment(), false);
			getListClickAnimator(FRAGMENT_SCALE_RATIO_SMALL, 0).start();
			AnimationUtils.animateAlpha(txtTitle, true);
		} else {
			ViewUtils.setVisibleGone(getPanelPayment(), false);
			ViewUtils.setVisibleGone(txtTitle, true);
			mFragmentView.setScaleX(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.setScaleY(FRAGMENT_SCALE_RATIO_SMALL);
		}
		if(mAdapter != null) {
			mAdapter.setIgnoreSelection(true);
		}
	}

	public View getPanelPayment() {
		final View panelPayment = findById(mFragmentView, R.id.panel_order_payment);
		if(panelPayment == null) {
			stubPaymentOptions.setLayoutResource(R.layout.view_order_payment_options);

			ViewGroup inflate = (ViewGroup) stubPaymentOptions.inflate();
			AndroidUtils.applyFont(getActivity(), inflate, OmnomFont.LSF_LE_REGULAR);

			mPanelPayment = inflate.findViewById(R.id.panel_linear);
			editAmount = (AmountEditText) inflate.findViewById(R.id.edit_payment_amount);
			txtCustomTips = (TextView) inflate.findViewById(R.id.txt_custom_tips);
			txtPaymentTitle = (TextView) inflate.findViewById(R.id.txt_payment_title);
			txtAlreadyPaid = (TextView) inflate.findViewById(R.id.txt_already_paid);

			txtTipsHint = (TextView) inflate.findViewById(R.id.txt_tips_hint);
			txtTipsTitle = (TextView) inflate.findViewById(R.id.txt_tips_title);
			txtTipsAmountHint = (TextView) inflate.findViewById(R.id.txt_tips_amount_hint);

			btnPay = (Button) inflate.findViewById(R.id.btn_pay);
			btnPay.setTextColor(AndroidUtils.createSelectableColor(mAccentColor));
			btnPay.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					if(amountIsTooHigh()) {
						final AlertDialog alertDialog = DialogUtils.showDialog(getActivity(), R.string.amount_is_too_high, R.string.pay,
						                                                       new DialogInterface.OnClickListener() {
							                                                       @Override
							                                                       public void onClick(final DialogInterface dialog,
							                                                                           final int which) {
								                                                       showCardsActivity();
							                                                       }
						                                                       }, R.string.refuse, new DialogInterface.OnClickListener() {
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
					} else {
						showCardsActivity();
					}
				}
			});

			ViewUtils.setBackgroundDrawableColor(btnPay, getResources().getColor(android.R.color.white));

			btnApply = (ImageButton) inflate.findViewById(R.id.btn_apply);
			btnApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					doApply(v);
				}
			});
			btnCancel = (ImageButton) inflate.findViewById(R.id.btn_cancel);
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					doCancel(v);
				}
			});
			RadioButton btnTips1 = (RadioButton) inflate.findViewById(R.id.radio_tips_1);
			RadioButton btnTips2 = (RadioButton) inflate.findViewById(R.id.radio_tips_2);
			RadioButton btnTips3 = (RadioButton) inflate.findViewById(R.id.radio_tips_3);
			tipsButtons = Arrays.asList((CompoundButton) btnTips1, (CompoundButton) btnTips2, (CompoundButton) btnTips3);
			otherTips = (RadioButton) inflate.findViewById(R.id.radio_tips_4);
			radioGroup = (RadioGroup) inflate.findViewById(R.id.radio_tips);
			btnEdit = inflate.findViewById(R.id.btn_edit);
			btnEdit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View v) {
					AndroidUtils.showKeyboard(editAmount);
				}
			});

			viewsAmountHide = new ArrayList<View>();
			viewsAmountHide.add(btnPay);
			viewsAmountHide.add(txtTipsTitle);
			viewsAmountHide.add(radioGroup);

			viewsAmountShow = new ArrayList<View>();
			viewsAmountShow.add(btnApply);
			viewsAmountShow.add(btnCancel);

			initAmount();
			updateCustomTipsText(0);
			initKeyboardListener();
			initRadioButtons();

			final Money amount = getEnteredAmount();
			updatePaymentTipsAmount(amount, tipsButtons);
			return inflate;
		} else {
			return panelPayment;
		}
	}

	@Override
	public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
		mFragmentView = view;
		mFragmentView.setTag("order_page_" + mPosition);
		updateDecimalSeparator();
		final String billText = getString(R.string.bill_number_, mPosition + 1);
		if(((OrdersActivity) getActivity()).getOrdersCount() > 1) {
			mHeader.setTitleBig(billText, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().onBackPressed();
				}
			});
		}
		mHeader.setButtonLeft(R.string.close, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				((OrdersActivity) getActivity()).close();
			}
		});
		if(!mSingle) {
			mHeader.setTitleBigDrawableRight(R.drawable.ic_modifier_group_dropdown);
		}
		ViewUtils.setVisibleGone(mHeader, false);

		if(mAnimate) {
			mFragmentView.setScaleX(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.setScaleY(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.animate().translationYBy(-mListHeight).setDuration(0).start();
			mFragmentView.animate().translationYBy(mListHeight).setStartDelay((mPosition + 1) * 200).setDuration(500).start();
		} else if(mSingle) {
			ViewUtils.setVisibleGone(getPanelPayment(), true);
			list.setTranslationY(mListTraslationActive);
			zoomInFragment((OrdersActivity) getActivity());
		} else {
			mFragmentView.setScaleX(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.setScaleY(FRAGMENT_SCALE_RATIO_SMALL);
		}

		pickerTips.setDividerDrawable(new ColorDrawable(mAccentColor));
		AndroidUtils.applyFont(getActivity(), txtTitle, OmnomFont.OSF_MEDIUM);
		txtTitle.setText(billText);

		initPicker();
		initFooter(mSingle);
		initList();
	}

	private boolean amountIsTooHigh() {
		final Money enteredAmount = getEnteredAmount();
		final Money moneyToPay = getOrder().getMoneyToPay(mCurrency);
		return enteredAmount.isGreatherThan(moneyToPay.multiply(1.5));
	}

	private boolean checkUser() {
		final OmnomApplication app = OmnomApplication.get(getActivity());
		if(TextUtils.isEmpty(app.getAuthToken())) {
			LoginActivity.start((OmnomActivity) getActivity(),
			                    AndroidUtils.getDevicePhoneNumber(getActivity(), R.string.phone_country_code),
			                    Extras.REQUEST_CODE_LOGIN);
			return true;
		}
		return false;
	}

	public void showCardsActivity() {
		if(checkUser()) {
			return;
		}

		final Money amount = getEnteredAmount();
		TipData tips = getSelectedTips(amount);
		final boolean validate = tips.validate();
		if(!validate) {
			reportWrongTips(tips, amount);
			tips = TipData.fix(tips, mCurrency);
		}
		final Money amountTips = tips.getAmount();
		final Money amountToPay = amount.add(amountTips);
		final PaymentDetails paymentDetails = new PaymentDetails(amountToPay, amountTips, getOrder(), mTipsWay, tips.getValue(),
		                                                         mSplitWay);
		if(!validate) {
			reportFixedTips(tips, paymentDetails);
		}
		final OrdersActivity activity = (OrdersActivity) getActivity();
		CardsActivity.start(getActivity(), activity.getRestaurant(), mOrder, paymentDetails, mAccentColor, TableEntranceData.create(),
		                    OrdersActivity.REQUEST_CODE_CARDS, activity.isDemo());
	}

	private void reportWrongTips(final TipData tips, final Money amount) {
		final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(getActivity());
		final Money amountTips = tips.getAmount();
		final Money amountToPay = amount.add(amountTips);
		final PaymentDetails paymentDetails = new PaymentDetails(amountToPay, amountTips,
		                                                         mOrder, mTipsWay, tips.getValue(),
		                                                         mSplitWay);
		mixPanelHelper.track(MixPanelHelper.Project.ALL, "WRONG_TIPS_VALUE", new Object[]{tips, paymentDetails});
	}

	private void reportFixedTips(final TipData tips, final PaymentDetails paymentDetails) {
		final MixPanelHelper mixPanelHelper = OmnomApplication.getMixPanelHelper(getActivity());
		mixPanelHelper.track(MixPanelHelper.Project.ALL, "WRONG_TIPS_VALUE_FIXED", new Object[]{tips, paymentDetails});
		mixPanelHelper.flush();
	}

	private void initList() {
		mAdapter = new OrderItemsAdapterSimple(getActivity(),
		                                       OmnomApplication.getCurrency(getActivity()),
		                                       getOrder().getItems(),
		                                       mCheckedStates,
		                                       true);

		// fix initial appearance - do not show bottom border (aka zubchiki)
		if(!mSingle) {
			list.setTranslationY(-48);
			list.animate().translationY(0).start();
		}

		// It is necessary to turn off hardware optimization for items list
		// as on scale there are font and hidden dividers artifacts on mdpi devices.
		final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		if(displayMetrics.densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
			list.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		list.setAdapter(mAdapter);
		list.setScrollingEnabled(false);
		list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		list.setSwipeEnabled(mSingle);
		list.setSwipeListener(new OmnomListView.SwipeListener() {
			@Override
			public void onRefresh() {
				splitBill();
			}
		});
		ViewUtils.setHeight(list, mListHeight);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				final OrdersActivity activity = (OrdersActivity) getActivity();
				if(isDownscaled()) {
					if(activity.checkFragment(OrderFragment.this)) {
						zoomInFragment(activity);
					}
				} else if(!isEverythingPaid(getOrder())) {
					splitBill();
				}
			}
		});
	}

	private void zoomInFragment(final OrdersActivity activity) {
		OmnomApplication application = OmnomApplication.get(getActivity());
		if(application.getUserProfile() != null) {
			sendBillViewEvent(mRequestId, application.getUserProfile().getUser(), getOrder());
		} else {
			Log.w(TAG, "UserProfile not set");
		}
		if(AndroidUtils.hasSelectedItems(mCheckedStates, list.getCount())) {
			initFooter2();
		} else {
			initFooter(true);
		}
		if(mFooterView2 != null) {
			final View billSplit2 = mFooterView2.findViewById(R.id.panel_container);
			ViewUtils.setVisibleGone(billSplit2, true);
		}
		ViewUtils.setVisibleGone(mHeader, true);
		AnimationUtils.animateAlpha(getPanelPayment(), true);
		AnimationUtils.animateAlpha(txtTitle, false);
		AndroidUtils.scrollEnd(list);
		list.setSwipeEnabled(true);
		activity.showOther(mPosition, false);
		getListClickAnimator(FRAGMENT_SCALE_RATIO_X_NORMAL, mListTraslationActive).start();
		if(mAdapter != null) {
			mAdapter.setIgnoreSelection(false);
		}
	}

	public boolean isDownscaled() {
		return list.getTranslationY() == 0;
	}

	private void sendBillViewEvent(String requestId, UserData user, Order order) {
		final OrdersActivity activity = (OrdersActivity) getActivity();
		if(!activity.isDemo()) {
			MixpanelEvent billViewEvent = new BillViewMixpanelEvent(UserHelper.getUserData(getActivity()), requestId, order, user);
			OmnomApplication.getMixPanelHelper(getActivity()).track(MixPanelHelper.Project.OMNOM, billViewEvent);
		}
	}

	private void initAmount() {
		editAmount.setKeyListener(new DecimalKeyListener());
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
		updateAmount(getOrder().getMoneyToPay(mCurrency).getReadableCurrencyValue());
		initAlreadyPaid();
	}

	private void updateAmount(final String amount) {
		final boolean isEverythingPaid = isEverythingPaid(getOrder());
		list.setSwipeEnabled(!isEverythingPaid);
		ViewUtils.setVisibleInvisible(btnEdit, !isEverythingPaid && mMode != MODE_AMOUNT);
		ViewUtils.setVisibleGone(txtPaymentTitle, !isEverythingPaid);
		editAmount.setFocusable(!isEverythingPaid);
		final float density = getResources().getDisplayMetrics().density;
		if(isEverythingPaid) {
			editAmount.setTextSize(getResources().getDimension(R.dimen.font_xlarge) / density);
			editAmount.setText(getString(R.string.everything_paid));
			((RelativeLayout.LayoutParams) mPanelPayment.getLayoutParams())
					.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.panel_payment_margin_bottom_on_paid));
			((RelativeLayout.LayoutParams) txtAlreadyPaid.getLayoutParams())
					.setMargins(0, getResources().getDimensionPixelSize(R.dimen.already_paid_margin_top_on_paid), 0, 0);
			((RelativeLayout.LayoutParams) txtTipsAmountHint.getLayoutParams())
					.setMargins(0, getResources().getDimensionPixelSize(R.dimen.already_paid_margin_top_on_paid), 0, 0);
		} else {
			editAmount.setText(amount);
		}
	}

	private void initAlreadyPaid() {
		final Money paidMoney = getOrder().getPaidMoney(mCurrency);
		if(txtAlreadyPaid != null) {
			if(!paidMoney.isNegativeOrZero()) {
				if(isEverythingPaid(getOrder())) {
					txtAlreadyPaid.setText(getString(R.string.paid_of,
					                                 paidMoney.getReadableCurrencyValue(),
					                                 getOrder().getTotalMoney(mCurrency).getReadableCurrencyValue()));
				} else {
					txtAlreadyPaid.setText(getString(R.string.already_paid, paidMoney.getReadableCurrencyValue()));
				}
				if(!isEditMode) {
					ViewUtils.setVisibleInvisible(txtAlreadyPaid, true);
				}
			} else {
				ViewUtils.setVisibleInvisible(txtAlreadyPaid, false);
			}
		}
	}

	private void updatePayButton(final Money amount, final CompoundButton selectedTipsButton) {
		final TipData selectedTips = getSelectedTips(amount, selectedTipsButton);
		updatePayButton(amount, selectedTips);
	}

	private void updatePayButton(final Money amount) {
		final TipData selectedTips = getSelectedTips(amount);
		updatePayButton(amount, selectedTips);
	}

	private void updatePayButton(final Money amount, final TipData tipData) {
		if(btnPay != null) {
			btnPay.setEnabled(!amount.isNegativeOrZero());
			btnPay.setText(getString(R.string.pay_amount, amount.add(tipData.getAmount()).getReadableCurrencyValue()));
		}
	}

	private void initKeyboardListener() {
		final View activityRootView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						editMode(isVisible);
						if(mCurrentKeyboardVisility != isVisible) {
							ButterKnife.apply(viewsAmountHide, ViewUtils.VISIBLITY_ALPHA2, !isVisible);
							ButterKnife.apply(viewsAmountShow, ViewUtils.VISIBLITY_ALPHA2, isVisible);

							ViewUtils.setVisibleGone(radioGroup, !isVisible);
							ViewUtils.setVisibleInvisible(btnEdit, !isVisible);
							ViewUtils.setVisibleInvisible(btnApply, isVisible);

							list.animate().translationYBy(isVisible ? mListTraslationActive : -mListTraslationActive).start();
							getPanelPayment().animate().yBy(isVisible ? mPaymentTranslationY : -mPaymentTranslationY).start();

							mCurrentKeyboardVisility = isVisible;
							editAmount.setCursorVisible(isVisible);

							if(isVisible) {
								mMode = MODE_AMOUNT;
								mLastAmount = getEnteredAmount();
								txtPaymentTitle.setText(R.string.i_m_going_to_pay);
								editAmount.setSelection(editAmount.getText().length() - getCurrencySuffix().length());
							} else {
								if(!mApply) {
									updateAmount(mLastAmount.getReadableCurrencyValue());
								} else {
									updateAmount(getEnteredAmount().getReadableCurrencyValue());
								}
								mApply = false;
								mLastAmount = Money.getZero(mLastAmount.getCurrency());
							}
						}
					}
				}));
	}

	private void initFooter(final boolean isZoomedIn) {
		if(list.getFooterViewsCount() > 0) {
			list.removeFooterView(mFooterView1);
			list.removeFooterView(mFooterView2);
		}
		mFooterView1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_footer, null, false);
		list.addFooterView(mFooterView1);
		final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
		updateOverallAmount(mFooterView1);
		updateFooter(isZoomedIn);
		final View layoutOverall = mFooterView1.findViewById(R.id.layout_overall);
		layoutOverall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final OrdersActivity activity = (OrdersActivity) getActivity();
				if(isDownscaled() && activity.checkFragment(OrderFragment.this)) {
					zoomInFragment(activity);
				}
			}
		});

		billSplit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(!isDownscaled()) {
					if(!isEverythingPaid(getOrder())) {
						splitBill();
					}
				} else {
					zoomInFragment((OrdersActivity) getActivity());
				}
			}
		});
	}

	private void updateOverallAmount(final View footerView) {
		if(footerView != null) {
			final TextView txtOverall = (TextView) mFooterView1.findViewById(R.id.txt_overall);
			if(txtOverall != null) {
				txtOverall.setText(getOrder().getTotalMoney(mCurrency).getReadableCurrencyValue());
			}
		}
	}

	private void updateFooter(final boolean isZoomedIn) {
		if(mFooterView1 != null) {
			final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
			final View layoutOverall = mFooterView1.findViewById(R.id.layout_overall);
			ViewUtils.setVisibleGone(billSplit, isZoomedIn);
			ViewUtils.setVisibleGone(layoutOverall, !isZoomedIn);
		}
	}

	private void initFooter2() {
		amountModified(true);
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
				cancelSplit(true);
			}
		});
	}

	private void cancelSplit(boolean resetAmount) {
		cancelSplit(resetAmount, false);
	}

	private void cancelSplit(boolean resetAmount, boolean force) {
		mSplitWay = SplitWay.WASNT_USED;
		if(isEditMode && !force) {
			return;
		}
		amountModified(false);
		mTagSplitType = BillSplitFragment.SPLIT_TYPE_ITEMS;
		mGuestsCount = 1;
		mCheckedStates.clear();
		mAdapter.notifyDataSetChanged();
		initFooter(true);
		if(resetAmount) {
			updateAmount(getOrder().getMoneyToPay(mCurrency).format(decimalSeparator));
			updatePaymentTipsAmount(getEnteredAmount());
		}
	}

	@SuppressLint("NewApi")
	private void splitBill() {
		if(mSplitRunning || isEditMode) {
			// skip double-tap
			return;
		}
		mSplitRunning = true;
		final SparseBooleanArrayParcelable stateCopy = mCheckedStates.clone();
		final BillSplitFragment billSplitFragment = BillSplitFragment.newInstance(mTagSplitType, getOrder(), stateCopy, mGuestsCount);
		getFragmentManager().beginTransaction().add(android.R.id.content, billSplitFragment, BillSplitFragment.TAG).commit();
		final ViewPropertyAnimator animator = list.animate();
		animator.translationY(-100).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				list.cancelRefreshing();
				list.setTranslationY(mListTraslationActive);
				animator.setListener(null);
				mSplitRunning = false;
			}
		}).setDuration(getResources().getInteger(R.integer.listview_animation_delay)).start();
	}

	private void initRadioButtons() {
		otherTips.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				showCustomTips(true);
			}
		});
		final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton btn, final boolean isChecked) {
				if(isChecked) {
					mTipsWay = TipsWay.BILL_SCREEN;
					mCheckedId = btn.getId();
					updateTipsButtonState(btn);
					otherTips.setTag(WRONG_VALUE);
					otherTips.setChecked(false);
					updateTipsButtonState(otherTips);
					updatePayButton(getEnteredAmount(), btn);
				} else {
					updateTipsButtonState(btn);
				}
			}
		};

		for(CompoundButton tipsButton : tipsButtons) {
			tipsButton.setOnCheckedChangeListener(listener);
			tipsButton.setTag(R.id.tip, WRONG_VALUE);
			tipsButton.setTag(WRONG_VALUE);
		}

		otherTips.setTag(R.id.tip, WRONG_VALUE);
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
		pickerTips.setValue(PICKER_DEFAULT_VALUE);
		pickerTips.setWrapSelectorWheel(false);
	}

	private void updateCustomTipsText(final int newVal) {
		txtCustomTips.setText(getString(R.string.tip_percent, newVal));
		final Money amount = isEverythingPaid(getOrder()) ? getOrder().getPaidMoney(mCurrency) : getEnteredAmount();
		final Money tips = OrderHelper.getTipsAmount(amount, newVal).round();
		txtTipsAmountHint.setText(getString(R.string.tip_hint_or, tips.getReadableCurrencyValue()));
	}

	private void showCustomTips(boolean visible) {
		list.animate().translationYBy(visible ? mListTraslationActive : -mListTraslationActive).start();
		getPanelPayment().animate().yBy(visible ? -mTipsTranslationY : mTipsTranslationY).start();
		editMode(visible);

		ViewUtils.setVisibleGone(editAmount, !visible);

		ViewUtils.setVisibleGone(pickerTips, visible);
		ViewUtils.setVisibleGone(txtTipsHint, visible);
		ViewUtils.setVisibleGone(txtCustomTips, visible);
		ViewUtils.setVisibleGone(txtTipsAmountHint, visible);

		ViewUtils.setVisibleInvisible(btnApply, visible);
		ViewUtils.setVisibleInvisible(btnEdit, !visible && !isEverythingPaid(getOrder()));
		ViewUtils.setVisibleInvisible(radioGroup, !visible);
		ViewUtils.setVisibleInvisible(txtPaymentTitle, !visible && !isEverythingPaid(getOrder()));
		ViewUtils.setVisibleInvisible(txtAlreadyPaid, getOrder().getPaidAmount() > 0 && !visible);
		ViewUtils.setVisibleInvisible(txtTipsTitle, !visible);
		ViewUtils.setVisibleInvisible(btnPay, !visible);

		btnApply.setAlpha(1.0f);
		btnApply.invalidate();
		btnApply.invalidateDrawable(btnApply.getDrawable());
		ViewUtils.setVisibleInvisible(btnCancel, visible);
		btnCancel.setAlpha(1.0f);
		btnCancel.invalidate();
		btnCancel.invalidateDrawable(btnCancel.getDrawable());
		mMode = visible ? MODE_TIPS : WRONG_VALUE;

		updateCustomTipsText(pickerTips.getValue());
	}

	private void editMode(final boolean isActive) {
		isEditMode = isActive;
		final boolean orderControlsEnabled = !isActive && isMenuVisible();
		list.setSwipeEnabled(orderControlsEnabled);
		ViewUtils.setVisibleGone(mHeader, orderControlsEnabled);
		if(mFooterView1 != null) {
			mFooterView1.findViewById(R.id.btn_bill_split).setEnabled(orderControlsEnabled);
		}
		if(mFooterView2 != null) {
			mFooterView2.findViewById(R.id.txt_edit).setEnabled(orderControlsEnabled);
			mFooterView2.findViewById(R.id.txt_cancel).setEnabled(orderControlsEnabled);
		}
	}

	private Money getEnteredAmount() {
		final String filtered = StringUtils.filterAmount(editAmount.getText().toString(), decimalSeparator);
		if(TextUtils.isEmpty(filtered) || !StringUtils.hasDigits(filtered)) {
			return Money.getZero(mCurrency);
		}
		try {
			return Money.create(numberFormat.parse(filtered).doubleValue(), mCurrency);
		} catch(ParseException e) {
			return Money.getZero(mCurrency);
		}
	}

	protected void doApply(View v) {
		if(mMode == MODE_AMOUNT) {
			cancelSplit(false, true);
			mApply = true;
			AndroidUtils.hideKeyboard(getActivity());
			mPaymentTitleChanged = true;
			txtPaymentTitle.setText(R.string.i_m_going_to_pay);
			mMode = WRONG_VALUE;
			final Money amount = getEnteredAmount();
			if(amount.equals(getOrder().getMoneyToPay(mCurrency))) {
				amountModified(true);
			}
			updateAmount(amount.getReadableCurrencyValue());
			updatePaymentTipsAmount(amount);
		}
		if(mMode == MODE_TIPS) {
			mTipsWay = TipsWay.MANUAL_PERCENTAGES;
			showCustomTips(false);
			otherTips.setChecked(true);
			mCheckedId = otherTips.getId();
			otherTips.setTag(pickerTips.getValue());
			updatePaymentTipsAmount(getEnteredAmount());
		}
	}

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
			radioGroup.check(mCheckedId);
			updateTipsButtonState(otherTips);
		}
	}

	private TipData getSelectedTips(final Money amount) {
		final CompoundButton btn = findById(mFragmentView, radioGroup.getCheckedRadioButtonId());
		return getSelectedTips(amount, btn);
	}

	private TipData getSelectedTips(final Money amount, final CompoundButton selectedTipsButton) {
		if(selectedTipsButton == null) {
			return new TipData(Money.getZero(mCurrency), 0, TipData.TYPE_NULL);
		}
		if(OrderHelper.isPercentTips(getOrder(), amount) || selectedTipsButton.getId() == otherTips.getId()) {
			final int percent = (Integer) selectedTipsButton.getTag();

			final Money amountToCountTips = isEverythingPaid(getOrder()) ?
					Money.createFractional(getOrder().getPaidAmount(), mCurrency) : amount;

			final Money tipsAmount = OrderHelper.getTipsAmount(amountToCountTips, percent);
			return new TipData(tipsAmount, percent, TipData.TYPE_PERCENT);
		} else {
			final int tag = (Integer) selectedTipsButton.getTag(R.id.tip);
			return new TipData(Money.create(tag, mCurrency), tag, TipData.TYPE_PREDEFINED);
		}
	}

	private void updatePaymentTipsAmount(Money amount) {
		updatePaymentTipsAmount(amount, tipsButtons);
	}

	private void updatePaymentTipsAmount(final Money amount, final List<CompoundButton> tipsButtons) {
		Money resultAmount = amount;
		final boolean percentTips = OrderHelper.isPercentTips(getOrder(), amount);
		if(amount.isZero() && getOrder().getPaidAmount() == 0) {
			resultAmount = amount;
			radioGroup.clearCheck();
			radioGroup.setEnabled(false);
			for(CompoundButton tipsButton : tipsButtons) {
				tipsButton.setEnabled(false);
			}
			otherTips.setEnabled(false);
		} else {
			radioGroup.setEnabled(true);
			for(CompoundButton tipsButton : tipsButtons) {
				tipsButton.setEnabled(true);
			}
			otherTips.setEnabled(true);
			if(radioGroup.getCheckedRadioButtonId() == -1) {
				final CompoundButton btn = findById(mFragmentView, mCheckedId);
				if(btn != null) {
					btn.setChecked(true);
					updateTipsButtonState(btn);
				} else if(tipsButtons.size() > 1) {
					tipsButtons.get(1).setChecked(true);
				}
			}
		}

		for(int i = 0; i < tipsButtons.size(); i++) {
			CompoundButton tipsButton = tipsButtons.get(i);
			final int tipsValue = OrderHelper.getTipsValue(getOrder(), amount, i);
			if(percentTips) {
				tipsButton.setTag(tipsValue);
				tipsButton.setText(getString(R.string.tip_percent, String.valueOf(tipsValue)));
			} else {
				tipsButton.setTag(R.id.tip, tipsValue);
				tipsButton.setText(String.valueOf(tipsValue));
			}
			updateTipsButtonState(tipsButton);
		}
		updateTipsButtonState(otherTips);
		updatePayButton(resultAmount);
	}

	private void updateTipsButtonState(final CompoundButton tipsButton) {
		if(tipsButton.getId() == otherTips.getId()) {
			if(tipsButton.isChecked()) {
				tipsButton.setText(getString(R.string.tip_percent, tipsButton.getTag()));
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontNormal);
			} else {
				tipsButton.setText(getString(R.string.tips_another));
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSmall);
			}
		} else {
			if(tipsButton.isChecked()) {
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontNormal);
			} else {
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSmall);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCurrency = OmnomApplication.getCurrency(getActivity());
		mLastAmount = Money.getZero(mCurrency);

		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mRequestId = getArguments().getString(ARG_REQUEST_ID);
			mAccentColor = getArguments().getInt(ARG_COLOR);
			mAnimate = getArguments().getBoolean(ARG_ANIMATE, false);
			mSingle = getArguments().getBoolean(ARG_SINGLE, false);
			mPosition = getArguments().getInt(ARG_POSITION);
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
			list.setSwipeEnabled(true);
			return true;
		}
		list.setSwipeEnabled(false);
		return false;
	}

	public boolean isInSplitMode() {
		final Fragment splitFragment = getFragmentManager().findFragmentByTag(BillSplitFragment.TAG);
		return splitFragment != null;
	}

	public Order getOrder() {
		return mOrder;
	}

	public String getOrderId() {
		return getOrder().getId();
	}

	private void amountModified(final boolean isModified) {
		isAmountModified = isModified;
	}
}