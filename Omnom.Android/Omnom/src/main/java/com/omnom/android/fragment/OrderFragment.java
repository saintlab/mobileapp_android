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
import android.graphics.drawable.GradientDrawable;
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
import com.omnom.android.adapter.OrderItemsAdapterSimple;
import com.omnom.android.auth.UserData;
import com.omnom.android.fragment.events.OrderSplitCommitEvent;
import com.omnom.android.fragment.events.SplitHideEvent;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.BillViewMixpanelEvent;
import com.omnom.android.mixpanel.model.MixpanelEvent;
import com.omnom.android.mixpanel.model.SplitWay;
import com.omnom.android.mixpanel.model.TipsWay;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderHelper;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.utils.AmountHelper;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.OmnomListView;
import com.omnom.android.view.HeaderView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.Optional;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

import static butterknife.ButterKnife.findById;

public class OrderFragment extends Fragment {
	public static final int MODE_AMOUNT = 0;

	public static final int MODE_TIPS = 1;

	public static final int WRONG_VALUE = -1;

	public static final BigDecimal WRONG_AMOUNT = BigDecimal.valueOf(WRONG_VALUE);

	private BigDecimal mLastAmount = WRONG_AMOUNT;

	private int mMode = WRONG_VALUE;

	private int mCheckedId = WRONG_VALUE;

	public static final float FRAGMENT_SCALE_RATIO_SMALL = 0.8f;

	public static final float FRAGMENT_SCALE_RATIO_X_NORMAL = 1.0f;

	public static final int PICKER_MAX_VALUE = 200;

	public static final int PICKER_MIN_VALUE = 0;

	public static final int PICKER_DEFAULT_VALUE = 10;

	public static class TipData {

		public static int TYPE_PERCENT = 0;

		public static int TYPE_PREDEFINED = 1;

		public static int TYPE_NULL = -1;

		private final BigDecimal mAmount;

		private final int mValue;

		private final int mType;

		public TipData(BigDecimal amount, int value, int type) {
			mAmount = amount;
			mValue = value;
			mType = type;
		}

		public BigDecimal getAmount() {
			return mAmount;
		}

		public int getValue() {
			return mValue;
		}

		public int getType() {
			return mType;
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

		private double mAmount;

		private int mTip;

		private int mTipValue;

		private int mTipsWay;

		private int mSplitWay;

		private String tableId;

		private String restaurantName;

		private String orderId;

		public PaymentDetails(Parcel parcel) {
			mAmount = parcel.readDouble();
			mTip = parcel.readInt();
			mTipValue = parcel.readInt();
			orderId = parcel.readString();
			tableId = parcel.readString();
			restaurantName = parcel.readString();
			mTipsWay = parcel.readInt();
			mSplitWay = parcel.readInt();
		}

		public PaymentDetails(double amount, int tip, Order order, TipsWay tipsWay, int tipValue, SplitWay splitWay) {
			mAmount = amount;
			mTip = tip;
			mTipValue = tipValue;
			tableId = order.getTableId();
			restaurantName = order.getRestaurantId();
			orderId = order.getId();
			mTipsWay = tipsWay.ordinal();
			mSplitWay = splitWay.ordinal();
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

		public int getTip() {
			return mTip;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(final Parcel dest, final int flags) {
			dest.writeDouble(mAmount);
			dest.writeInt(mTip);
			dest.writeInt(mTipValue);
			dest.writeString(orderId);
			dest.writeString(tableId);
			dest.writeString(restaurantName);
			dest.writeInt(mTipsWay);
			dest.writeInt(mSplitWay);
		}

		public double getAmount() {
			return mAmount;
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
	protected RestaurateurObeservableApi api;

	@Optional
	@InjectViews({R.id.btn_pay, R.id.radio_tips, R.id.txt_tips_title})
	protected List<View> viewsAmountHide;

	@Optional
	@InjectViews({R.id.btn_apply, R.id.btn_cancel})
	protected List<View> viewsAmountShow;

	@InjectView(android.R.id.list)
	protected OmnomListView list = null;

	protected EditText editAmount;

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

	private OrderItemsAdapter mAdapter;

	private boolean mSplitRunning;

	private boolean isAmountModified;

	public OrderFragment() {
	}

	public View getFragmentView() {
		return mFragmentView;
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
	public void onSplitHide(SplitHideEvent event) {
		if(event.getOrderId().equals(mOrder.getId())) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Subscribe
	public void onSplitCommit(OrderSplitCommitEvent event) {
		if(event.getOrderId().equals(mOrder.getId())) {
			mTagSplitType = event.getSplitType();
			if(event.getSplitType() == BillSplitFragment.SPLIT_TYPE_PERSON) {
				mCheckedStates.clear();
				mAdapter.notifyDataSetChanged();
				// exclude case when split to single person
				if(event.getAmount().compareTo(new BigDecimal(mOrder.getTotalAmount())) == -1) {
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
			final BigDecimal amount = event.getAmount();
			final String s = AmountHelper.format(amount) + getCurrencySuffix();
			editAmount.setText(s);
			updatePaymentTipsAmount(amount);
		}
	}

	public void onPayment(final Order order) {
		if (order != null && order.getId().equals(mOrder.getId())) {
			mOrder = order;
			final double paidAmount = order.getPaidAmount();
			if (txtAlreadyPaid != null) {
				if (paidAmount > 0) {
					txtAlreadyPaid.setText(getString(R.string.already_paid, AmountHelper.format(paidAmount) + getCurrencySuffix()));
					ViewUtils.setVisible2(txtAlreadyPaid, true);
				} else {
					ViewUtils.setVisible2(txtAlreadyPaid, false);
				}
			}
			if (!isAmountModified && editAmount != null) {
				editAmount.setText(AmountHelper.format(order.getAmountToPay()) + getCurrencySuffix());
				final BigDecimal amount = getEnteredAmount();
				updatePaymentTipsAmount(amount, tipsButtons);
			}
		}
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

	public void downscale() {
		initFooter(false);
		if(mFooterView2 != null) {
			final View billSplit2 = mFooterView2.findViewById(R.id.panel_container);
			ViewUtils.setVisible(billSplit2, false);
		}
		ViewUtils.setVisible(mHeader, false);
		AnimationUtils.animateAlpha3(getPanelPayment(), false);
		list.setSwipeEnabled(false);
		getListClickAnimator(FRAGMENT_SCALE_RATIO_SMALL, 0).start();
		AnimationUtils.animateAlpha(txtTitle, true);
	}

	public View getPanelPayment() {
		final View panelPayment = findById(mFragmentView, R.id.panel_order_payment);
		if(panelPayment == null) {
			stubPaymentOptions.setLayoutResource(R.layout.view_order_payment_options);

			ViewGroup inflate = (ViewGroup) stubPaymentOptions.inflate();
			AndroidUtils.applyFont(getActivity(), inflate, "fonts/Futura-LSF-Omnom-LE-Regular.otf");

			editAmount = (EditText) inflate.findViewById(R.id.edit_payment_amount);
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
						final AlertDialog alertDialog = AndroidUtils.showDialog(getActivity(), R.string.amount_is_too_high, R.string.pay,
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
			GradientDrawable sd = (GradientDrawable) btnPay.getBackground();
			sd.setColor(getResources().getColor(android.R.color.white));
			sd.invalidateSelf();

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

			viewsAmountHide = new ArrayList<View>();
			viewsAmountHide.add(btnPay);
			viewsAmountHide.add(txtTipsTitle);
			viewsAmountHide.add(radioGroup);

			viewsAmountShow = new ArrayList<View>();
			viewsAmountShow.add(btnApply);
			viewsAmountShow.add(btnCancel);

			updateCustomTipsText(0);
			initKeyboardListener();
			initAmount();
			initRadioButtons();

			final BigDecimal amount = getEnteredAmount();
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
		ViewUtils.setVisible(mHeader, false);

		if(mAnimate) {
			mFragmentView.setScaleX(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.setScaleY(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.animate().translationYBy(-mListHeight).setDuration(0).start();
			mFragmentView.animate().translationYBy(mListHeight).setStartDelay((mPosition + 1) * 200).setDuration(500).start();
		} else if(mSingle) {
			ViewUtils.setVisible(getPanelPayment(), true);
			list.setTranslationY(mListTraslationActive);
			zoomInFragment((OrdersActivity) getActivity());
		} else {
			mFragmentView.setScaleX(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.setScaleY(FRAGMENT_SCALE_RATIO_SMALL);
		}

		pickerTips.setDividerDrawable(new ColorDrawable(mAccentColor));
		CalligraphyUtils.applyFontToTextView(getActivity(), txtTitle, "fonts/Futura-OSF-Omnom-Medium.otf");
		txtTitle.setText(billText);

		initPicker();
		initFooter(mSingle);
		initList();
	}

	private boolean amountIsTooHigh() {
		return getEnteredAmount().doubleValue() > 1.5 * mOrder.getAmountToPay();
	}

	private void showCardsActivity() {
		final BigDecimal amount = getEnteredAmount();
		final TipData tips = getSelectedTips(amount);
		final BigDecimal amountTips = tips.getAmount();
		final BigDecimal amountToPay = amount.add(amountTips);
		final PaymentDetails paymentDetails = new PaymentDetails(amountToPay.doubleValue(), amountTips.intValue() * 100,
		                                                         mOrder, mTipsWay, tips.getValue(),
		                                                         mSplitWay);
		final OrdersActivity activity = (OrdersActivity) getActivity();
		CardsActivity.start(getActivity(), mOrder, paymentDetails, mAccentColor, OrdersActivity.REQUEST_CODE_CARDS, activity.isDemo());
	}

	private void initList() {
		mAdapter = new OrderItemsAdapterSimple(getActivity(), mOrder.getItems(), mCheckedStates, true);

		// fix initial appearance - do not show bottom border (aka zubchiki)
		if(!mSingle) {
			list.setTranslationY(-48);
			list.animate().translationY(0).start();
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
				if (isDownscaled()) {
					if (activity.checkFragment(OrderFragment.this)) {
						zoomInFragment(activity);
					}
				} else {
					splitBill();
				}
			}
		});
	}

	private void zoomInFragment(final OrdersActivity activity) {
		OmnomApplication application = OmnomApplication.get(getActivity());
		if(application.getUserProfile() != null) {
			sendBillViewEvent(mRequestId, application.getUserProfile().getUser(), mOrder);
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
			ViewUtils.setVisible(billSplit2, true);
		}
		ViewUtils.setVisible(mHeader, true);
		AnimationUtils.animateAlpha(getPanelPayment(), true);
		AnimationUtils.animateAlpha(txtTitle, false);
		AndroidUtils.scrollEnd(list);
		list.setSwipeEnabled(true);
		activity.showOther(mPosition, false);
		getListClickAnimator(FRAGMENT_SCALE_RATIO_X_NORMAL, mListTraslationActive).start();
	}

	public boolean isDownscaled() {
		return list.getTranslationY() == 0;
	}

	private void sendBillViewEvent(String requestId, UserData user, Order order) {
		MixpanelEvent billViewEvent = new BillViewMixpanelEvent(UserHelper.getUserData(getActivity()), requestId, order, user);
		OmnomApplication.getMixPanelHelper(getActivity()).track(MixPanelHelper.Project.OMNOM, billViewEvent);
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

		final double paidAmount = mOrder.getPaidAmount();
		if (paidAmount > 0) {
			txtAlreadyPaid.setText(getString(R.string.already_paid, AmountHelper.format(paidAmount) + getCurrencySuffix()));
			ViewUtils.setVisible2(txtAlreadyPaid, true);
		} else {
			ViewUtils.setVisible2(txtAlreadyPaid, false);
		}
		editAmount.setText(AmountHelper.format(mOrder.getAmountToPay()) + getCurrencySuffix());
	}

	private void updateAmount(final Order order) {
		final double paidAmount = mOrder.getPaidAmount();
		if(paidAmount > 0) {
			txtAlreadyPaid.setText(getString(R.string.already_paid, AmountHelper.format(paidAmount) + getCurrencySuffix()));
			ViewUtils.setVisible2(txtAlreadyPaid, true);
		} else {
			ViewUtils.setVisible2(txtAlreadyPaid, false);
		}
		editAmount.setText(AmountHelper.format(mOrder.getAmountToPay()) + getCurrencySuffix());
	}

	private void updatePayButton(final BigDecimal amount) {
		btnPay.setEnabled(BigDecimal.ZERO.compareTo(amount) != 0);
		btnPay.setText(getString(R.string.pay_amount, AmountHelper.format(amount) + getCurrencySuffix()));
	}

	private void initKeyboardListener() {
		final View activityRootView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						editMode(isVisible);
						if(mCurrentKeyboardVisility != isVisible) {
							ButterKnife.apply(viewsAmountHide, ViewUtils.VISIBLITY_ALPHA, !isVisible);
							ButterKnife.apply(viewsAmountShow, ViewUtils.VISIBLITY_ALPHA2, isVisible);
							ViewUtils.setVisible(radioGroup, !isVisible);

							list.animate().translationYBy(isVisible ? mListTraslationActive : -mListTraslationActive).start();
							getPanelPayment().animate().yBy(isVisible ? mPaymentTranslationY : -mPaymentTranslationY).start();

							mCurrentKeyboardVisility = isVisible;
							editAmount.setCursorVisible(isVisible);

							if(isVisible) {
								mMode = MODE_AMOUNT;
								mLastAmount = getEnteredAmount();
								txtPaymentTitle.setText(R.string.i_m_going_to_pay);
								editAmount.setSelection(editAmount.getText().length() - 1);
							} else {
								if(!mApply) {
									editAmount.setText(AmountHelper.format(mLastAmount) + getCurrencySuffix());
								} else {
									editAmount.setText(AmountHelper.format(getEnteredAmount()) + getCurrencySuffix());
								}
								mApply = false;
								mLastAmount = WRONG_AMOUNT;
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
		final TextView txtOverall = (TextView) mFooterView1.findViewById(R.id.txt_overall);
		final View layoutOverall = mFooterView1.findViewById(R.id.layout_overall);
		txtOverall.setText(StringUtils.formatCurrencyWithSpace(mOrder.getTotalAmount(), getCurrencySuffix()));
		updateFooter(isZoomedIn);

		layoutOverall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isDownscaled()) {
					zoomInFragment((OrdersActivity) getActivity());
				}
			}
		});

		billSplit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (!isDownscaled()) {
					splitBill();
				} else {
					zoomInFragment((OrdersActivity) getActivity());
				}
			}
		});
	}

	private void updateFooter(final boolean isZoomedIn) {
		if(mFooterView1 != null) {
			final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
			final View layoutOverall = mFooterView1.findViewById(R.id.layout_overall);
			ViewUtils.setVisible(billSplit, isZoomedIn);
			ViewUtils.setVisible(layoutOverall, !isZoomedIn);
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
		mSplitWay = SplitWay.WASNT_USED;
		if(isEditMode) {
			return;
		}
		amountModified(false);
		mTagSplitType = BillSplitFragment.SPLIT_TYPE_ITEMS;
		mGuestsCount = 1;
		mCheckedStates.clear();
		mAdapter.notifyDataSetChanged();
		initFooter(true);
		if(resetAmount) {
			editAmount.setText(StringUtils.formatCurrency(AmountHelper.format(mOrder.getAmountToPay())));
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
		final BillSplitFragment billSplitFragment = BillSplitFragment.newInstance(mTagSplitType, mOrder, stateCopy, mGuestsCount);
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
					final BigDecimal amount = getEnteredAmount();
					final TipData selectedTips = getSelectedTips(amount, btn);
					updatePayButton(amount.add(selectedTips.getAmount()));
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
	}

	private void updateCustomTipsText(final int newVal) {
		txtCustomTips.setText(getString(R.string.tip_percent, newVal));
		final double tips = OrderHelper.getTipsAmount(getEnteredAmount(), newVal);
		final String tipsFormatted = AmountHelper.format(tips) + getCurrencySuffix();
		txtTipsAmountHint.setText(getString(R.string.tip_hint_or, tipsFormatted));
	}

	private void showCustomTips(boolean visible) {
		list.animate().translationYBy(visible ? mListTraslationActive : -mListTraslationActive).start();
		getPanelPayment().animate().yBy(visible ? -mTipsTranslationY : mTipsTranslationY).start();
		editMode(visible);

		ViewUtils.setVisible(editAmount, !visible);

		ViewUtils.setVisible(pickerTips, visible);
		ViewUtils.setVisible(txtTipsHint, visible);
		ViewUtils.setVisible(txtCustomTips, visible);
		ViewUtils.setVisible(txtTipsAmountHint, visible);

		ViewUtils.setVisible2(radioGroup, !visible);
		ViewUtils.setVisible2(txtPaymentTitle, !visible);
		ViewUtils.setVisible2(txtAlreadyPaid, mOrder.getPaidAmount() > 0 && !visible);
		ViewUtils.setVisible2(txtTipsTitle, !visible);
		ViewUtils.setVisible2(btnPay, !visible);

		ViewUtils.setVisible2(btnApply, visible);

		btnApply.setAlpha(1.0f);
		btnApply.invalidate();
		btnApply.invalidateDrawable(btnApply.getDrawable());
		ViewUtils.setVisible2(btnCancel, visible);
		btnCancel.setAlpha(1.0f);
		btnCancel.invalidate();
		btnCancel.invalidateDrawable(btnCancel.getDrawable());
		mMode = visible ? MODE_TIPS : WRONG_VALUE;

		updateCustomTipsText(pickerTips.getValue());
	}

	private void editMode(final boolean isActive) {
		isEditMode = isActive;
		final boolean orderControlsEnabled = !isActive;
		list.setSwipeEnabled(orderControlsEnabled);
		ViewUtils.setVisible(mHeader, orderControlsEnabled);
		if(mFooterView1 != null) {
			mFooterView1.findViewById(R.id.btn_bill_split).setEnabled(orderControlsEnabled);
		}
		if(mFooterView2 != null) {
			mFooterView2.findViewById(R.id.txt_edit).setEnabled(orderControlsEnabled);
			mFooterView2.findViewById(R.id.txt_cancel).setEnabled(orderControlsEnabled);
		}
	}

	private BigDecimal getEnteredAmount() {
		final String filtered = StringUtils.filterAmount(editAmount.getText().toString());
		if(TextUtils.isEmpty(filtered) || !StringUtils.hasDigits(filtered)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(filtered);
	}

	protected void doApply(View v) {
		if(mMode == MODE_AMOUNT) {
			cancelSplit(false);
			mApply = true;
			AndroidUtils.hideKeyboard(getActivity());
			mPaymentTitleChanged = true;
			txtPaymentTitle.setText(R.string.i_m_going_to_pay);
			mMode = WRONG_VALUE;
			final BigDecimal amount = getEnteredAmount();
			if (amount.compareTo(BigDecimal.valueOf(mOrder.getAmountToPay())) != 0) {
				amountModified(true);
			}
			editAmount.setText(AmountHelper.format(amount));
			updatePaymentTipsAmount(amount);
		}
		if(mMode == MODE_TIPS) {
			mTipsWay = TipsWay.MANUAL_PERCENTAGE;
			showCustomTips(false);
			otherTips.setChecked(true);
			mCheckedId = otherTips.getId();
			otherTips.setTag(pickerTips.getValue());
			final BigDecimal amount = getEnteredAmount();
			updatePaymentTipsAmount(amount);
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

	private TipData getSelectedTips(final BigDecimal amount) {
		final CompoundButton btn = findById(getActivity(), radioGroup.getCheckedRadioButtonId());
		return getSelectedTips(amount, btn);
	}

	private TipData getSelectedTips(final BigDecimal amount, final CompoundButton selectedTipsButton) {
		if(selectedTipsButton == null) {
			return new TipData(BigDecimal.ZERO, TipData.TYPE_NULL, TipData.TYPE_NULL);
		}
		if(OrderHelper.isPercentTips(mOrder, amount) || selectedTipsButton.getId() == otherTips.getId()) {
			final int percent = (Integer) selectedTipsButton.getTag();
			final int tipsAmount = OrderHelper.getTipsAmount(amount, percent);

			return new TipData(BigDecimal.valueOf(tipsAmount), percent, TipData.TYPE_PERCENT);
		} else {
			final int tag = (Integer) selectedTipsButton.getTag(R.id.tip);
			return new TipData(BigDecimal.valueOf(tag), tag, TipData.TYPE_PERCENT);
		}
	}

	private double getSelectedTips(final CompoundButton btn, final BigDecimal amount) {
		if(btn == null) {
			return 0;
		}
		if(OrderHelper.isPercentTips(mOrder, amount)) {
			final int percent = (Integer) btn.getTag();
			return OrderHelper.getTipsAmount(amount, percent);
		} else {
			return (Integer) btn.getTag(R.id.tip);
		}
	}

	private double getOtherTips(final BigDecimal amount) {
		if(otherTips == null) {
			return 0;
		}
		final int percent = (Integer) otherTips.getTag();
		return OrderHelper.getTipsAmount(amount, percent);
	}

	private void updatePaymentTipsAmount(BigDecimal amount) {
		updatePaymentTipsAmount(amount, tipsButtons);
	}

	private void updatePaymentTipsAmount(final BigDecimal amount, final List<CompoundButton> tipsButtons) {
		BigDecimal resultAmount = amount;
		final boolean percentTips = OrderHelper.isPercentTips(mOrder, amount);
		if(BigDecimal.ZERO.compareTo(amount) == 0 && mOrder.getPaidAmount() == 0) {
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
				final CompoundButton btn = findById(getActivity(), mCheckedId);
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
			final int tipsValue = OrderHelper.getTipsValue(mOrder, amount, i);
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
		final TipData selectedTips = getSelectedTips(resultAmount);
		updatePayButton(resultAmount.add(selectedTips.getAmount()));
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

	public String getOrderId() {
		return mOrder.getId();
	}

	private void amountModified(final boolean isModified) {
		isAmountModified = isModified;
	}

}
