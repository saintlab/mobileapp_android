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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.omnom.android.mixpanel.model.BillViewEvent;
import com.omnom.android.mixpanel.model.Event;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderHelper;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
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

		public PaymentDetails(Parcel parcel) {
			mAmount = parcel.readDouble();
			mTip = parcel.readInt();
		}

		public PaymentDetails(double amount, int tip) {
			mAmount = amount;
			mTip = tip;
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

	public static Fragment newInstance(Order order, String requestId, final int bgColor, final int postition, final boolean animate,
	                                   final boolean isSingle) {
		final OrderFragment fragment = new OrderFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putString(ARG_REQUEST_ID, requestId);
		args.putInt(ARG_COLOR, bgColor);
		args.putInt(ARG_POSITION, postition);
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

	private int mListTrasnlationActive;

	private int mPaymentTranslationY;

	private int mTipsTranslationY;

	private int mListHeight;

	private OrderItemsAdapter mAdapter;

	private int lastCheckedTipsButtonId = -1;

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
		if (event.getOrderId().equals(mOrder.getId())) {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Subscribe
	public void onSplitCommit(OrderSplitCommitEvent event) {
		if (event.getOrderId().equals(mOrder.getId())) {
			if (event.getSplitType() == BillSplitFragment.SPLIT_TYPE_PERSON) {
				cancelSplit(true);
			}
			if (event.getSplitType() == BillSplitFragment.SPLIT_TYPE_ITEMS) {
				mCheckedStates = event.getStates();
				mAdapter.setStates(mCheckedStates);
				mAdapter.notifyDataSetChanged();
				if (AndroidUtils.hasSelectedItems(mCheckedStates, list.getCount())) {
					initFooter2();
				} else {
					initFooter(true);
				}
			}
			final BigDecimal amount = event.getAmount();
			final String s = StringUtils.formatCurrency(amount, getCurrencySuffix());
			editAmount.setText(s);
			updatePaymentTipsAmount(amount);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_order, container, false);

		mFontNormal = getResources().getDimension(R.dimen.font_xlarge);
		mFontSmall = getResources().getDimension(R.dimen.font_large);

		mListTrasnlationActive = getResources().getDimensionPixelSize(R.dimen.order_list_trasnlation_active);
		mPaymentTranslationY = getResources().getDimensionPixelSize(R.dimen.order_payment_translation_y);
		mTipsTranslationY = getResources().getDimensionPixelSize(R.dimen.order_tips_translation_y);
		mListHeight = getResources().getDimensionPixelSize(R.dimen.order_list_height);

		ButterKnife.inject(this, view);
		return view;
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
		if (mFooterView1 != null) {
			final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
			ViewUtils.setVisible(billSplit, false);
		}
		if (mFooterView2 != null) {
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
		if (panelPayment == null) {
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
					if (amountIsTooHigh()) {
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
		if (((OrdersActivity) getActivity()).getOrdersCount() > 1) {
			mHeader.setTitleBig(billText, R.drawable.bg_card_title, new View.OnClickListener() {
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

		if (mAnimate) {
			mFragmentView.setScaleX(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.setScaleY(FRAGMENT_SCALE_RATIO_SMALL);
			mFragmentView.animate().translationYBy(-mListHeight).setDuration(0).start();
			mFragmentView.animate().translationYBy(mListHeight).setStartDelay((mPosition + 1) * 200).setDuration(500).start();
		} else if (mSingle) {
			ViewUtils.setVisible(getPanelPayment(), true);
			list.setTranslationY(mListTrasnlationActive);
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
		final BigDecimal tips = getSelectedTips(amount);
		final BigDecimal amountToPay = amount.add(tips);
		final PaymentDetails paymentDetails = new PaymentDetails(amountToPay.doubleValue(), tips.intValue() * 100);
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
		if (application.getUserProfile() != null) {
			sendBillViewEvent(mRequestId, application.getUserProfile().getUser(), mOrder);
		} else {
			Log.w(TAG, "UserProfile not set");
		}
		if (mFooterView1 != null) {
			final View billSplit = mFooterView1.findViewById(R.id.btn_bill_split);
			ViewUtils.setVisible(billSplit, true);
		}
		if (mFooterView2 != null) {
			final View billSplit2 = mFooterView2.findViewById(R.id.panel_container);
			ViewUtils.setVisible(billSplit2, true);
		}
		ViewUtils.setVisible(mHeader, true);
		AnimationUtils.animateAlpha(getPanelPayment(), true);
		AnimationUtils.animateAlpha(txtTitle, false);
		AndroidUtils.scrollEnd(list);
		list.setSwipeEnabled(true);
		activity.showOther(mPosition, false);
		getListClickAnimator(FRAGMENT_SCALE_RATIO_X_NORMAL, mListTrasnlationActive).start();
	}

	public boolean isDownscaled() {
		return list.getTranslationY() == 0;
	}

	private void sendBillViewEvent(String requestId, UserData user, Order order) {
		Event billViewEvent = new BillViewEvent(requestId, order, user);
		OmnomApplication.getMixPanelHelper(getActivity()).track(billViewEvent);
	}

	private void initAmount() {
		editAmount.setCursorVisible(false);
		editAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
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
				if (!str.endsWith(getCurrencySuffix())) {
					final String text = editAmount.getText() + getCurrencySuffix();
					editAmount.setText(text);
					editAmount.setSelection(text.length() - 1);
				}
			}
		});
		final double paidAmount = mOrder.getPaidAmount();
		if (paidAmount > 0) {
			txtAlreadyPaid.setText(getString(R.string.already_paid, StringUtils.formatCurrency(paidAmount, getCurrencySuffix())));
			ViewUtils.setVisible2(txtAlreadyPaid, true);
		} else {
			ViewUtils.setVisible2(txtAlreadyPaid, false);
		}
		editAmount.setText(StringUtils.formatCurrency(mOrder.getAmountToPay()));
		editAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					int length = editAmount.getText().length();
					if (length >= 2) {
						editAmount.setSelection(length - 2);
					}
				}
			}
		});
	}

	private void updatePayButton(final BigDecimal amount) {
		btnPay.setEnabled(BigDecimal.ZERO.compareTo(amount) != 0);
		btnPay.setText(getString(R.string.pay_amount, amount + getCurrencySuffix()));
	}

	private void initKeyboardListener() {
		final View activityRootView = ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				AndroidUtils.createKeyboardListener(activityRootView, new AndroidUtils.KeyboardVisibilityListener() {
					@Override
					public void onVisibilityChanged(boolean isVisible) {
						if (mCurrentKeyboardVisility != isVisible) {
							ButterKnife.apply(viewsAmountHide, ViewUtils.VISIBLITY_ALPHA, !isVisible);
							ButterKnife.apply(viewsAmountShow, ViewUtils.VISIBLITY_ALPHA2, isVisible);
							ViewUtils.setVisible(radioGroup, !isVisible);

							list.animate().translationYBy(isVisible ? mListTrasnlationActive : -mListTrasnlationActive).start();
							getPanelPayment().animate().yBy(isVisible ? mPaymentTranslationY : -mPaymentTranslationY).start();

							mCurrentKeyboardVisility = isVisible;
							editAmount.setCursorVisible(isVisible);

							if (isVisible) {
								mMode = MODE_AMOUNT;
								mLastAmount = getEnteredAmount();
								txtPaymentTitle.setText(R.string.i_m_going_to_pay);
								editAmount.setSelection(editAmount.getText().length() - 1);
							} else {
								if (!mApply) {
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
		if (list.getFooterViewsCount() > 0) {
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
				if (!isDownscaled()) {
					splitBill();
				} else {
					zoomInFragment((OrdersActivity) getActivity());
				}
			}
		});
	}

	private void initFooter2() {
		if (list.getFooterViewsCount() > 0) {
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
		mCheckedStates.clear();
		mAdapter.notifyDataSetChanged();
		initFooter(true);
		if (resetAmount) {
			editAmount.setText(StringUtils.formatCurrency(mOrder.getAmountToPay()));
		}
	}

	@SuppressLint("NewApi")
	private void splitBill() {
		final SparseBooleanArrayParcelable stateCopy = mCheckedStates.clone();
		final BillSplitFragment billSplitFragment = BillSplitFragment.newInstance(mOrder, stateCopy);
		getFragmentManager().beginTransaction().add(android.R.id.content, billSplitFragment, BillSplitFragment.TAG).commit();
		final ViewPropertyAnimator animator = list.animate();
		animator.translationY(-100).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				list.cancelRefreshing();
				list.setTranslationY(mListTrasnlationActive);
				animator.setListener(null);
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
				if (isChecked) {
					lastCheckedTipsButtonId = btn.getId();
					mCheckedId = btn.getId();
					updateTipsButtonState(btn);
					otherTips.setTag(WRONG_VALUE);
					otherTips.setChecked(false);
					updateTipsButtonState(otherTips);
					final BigDecimal amount = getEnteredAmount();
					final BigDecimal selectedTips = getSelectedTips(amount, btn);
					updatePayButton(amount.add(selectedTips));
				} else {
					updateTipsButtonState(btn);
				}
			}
		};

		for (CompoundButton tipsButton : tipsButtons) {
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
		final String tipsFormatted = StringUtils.formatCurrency(BigDecimal.valueOf(tips), getCurrencySuffix());
		txtTipsAmountHint.setText(getString(R.string.tip_hint_or, tipsFormatted));
	}

	private void showCustomTips(boolean visible) {
		list.animate().translationYBy(visible ? mListTrasnlationActive : -mListTrasnlationActive).start();
		getPanelPayment().animate().yBy(visible ? -mTipsTranslationY : mTipsTranslationY).start();

		ViewUtils.setVisible(mHeader, !visible);
		ViewUtils.setVisible(editAmount, !visible);

		ViewUtils.setVisible(pickerTips, visible);
		ViewUtils.setVisible(txtTipsHint, visible);
		ViewUtils.setVisible(txtCustomTips, visible);
		ViewUtils.setVisible(txtTipsAmountHint, visible);

		ViewUtils.setVisible2(radioGroup, !visible);
		ViewUtils.setVisible2(txtPaymentTitle, !visible);
		ViewUtils.setVisible2(txtAlreadyPaid, !visible);
		ViewUtils.setVisible2(txtTipsTitle, !visible);

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

	private BigDecimal getEnteredAmount() {
		final String filtered = StringUtils.filterAmount(editAmount.getText().toString());
		if (TextUtils.isEmpty(filtered) || !StringUtils.hasDigits(filtered)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(filtered);
	}

	protected void doApply(View v) {
		if (mMode == MODE_AMOUNT) {
			cancelSplit(false);
			mApply = true;
			AndroidUtils.hideKeyboard(getActivity());
			mPaymentTitleChanged = true;
			txtPaymentTitle.setText(R.string.i_m_going_to_pay);
			mMode = WRONG_VALUE;
			final BigDecimal amount = getEnteredAmount();
			updatePaymentTipsAmount(amount);
		}
		if (mMode == MODE_TIPS) {
			showCustomTips(false);
			otherTips.setChecked(true);
			lastCheckedTipsButtonId = otherTips.getId();
			otherTips.setTag(pickerTips.getValue());
			final BigDecimal amount = getEnteredAmount();
			updatePaymentTipsAmount(amount);
		}
	}

	protected void doCancel(View v) {
		if (mMode == MODE_AMOUNT) {
			mApply = false;
			AndroidUtils.hideKeyboard(getActivity());
			if (!mPaymentTitleChanged) {
				txtPaymentTitle.setText(R.string.to_be_paid);
			}
			mMode = WRONG_VALUE;
		}
		if (mMode == MODE_TIPS) {
			showCustomTips(false);
			if (otherTips.getTag().equals(WRONG_VALUE)) {
				return;
			}
			radioGroup.check(mCheckedId);
			otherTips.setTag(WRONG_VALUE);
			updateTipsButtonState(otherTips);
		}
	}

	private BigDecimal getSelectedTips(final BigDecimal amount) {
		final CompoundButton btn = findById(getActivity(), radioGroup.getCheckedRadioButtonId());
		return getSelectedTips(amount, btn);
	}

	private BigDecimal getSelectedTips(final BigDecimal amount, final CompoundButton selectedTipsButton) {
		if (selectedTipsButton == null) {
			return BigDecimal.ZERO;
		}
		if (OrderHelper.isPercentTips(mOrder, amount) || selectedTipsButton.getId() == otherTips.getId()) {
			final int percent = (Integer) selectedTipsButton.getTag();
			final int tipsAmount = OrderHelper.getTipsAmount(amount, percent);
			return BigDecimal.valueOf(tipsAmount);
		} else {
			final int tag = (Integer) selectedTipsButton.getTag(R.id.tip);
			return BigDecimal.valueOf(tag);
		}
	}

	private double getSelectedTips(final CompoundButton btn, final BigDecimal amount) {
		if (btn == null) {
			return 0;
		}
		if (OrderHelper.isPercentTips(mOrder, amount)) {
			final int percent = (Integer) btn.getTag();
			return OrderHelper.getTipsAmount(amount, percent);
		} else {
			return (Integer) btn.getTag(R.id.tip);
		}
	}

	private double getOtherTips(final BigDecimal amount) {
		if (otherTips == null) {
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
		if (BigDecimal.ZERO.compareTo(amount) == 0 && mOrder.getPaidAmount() == 0) {
			resultAmount = amount;
			radioGroup.clearCheck();
			radioGroup.setEnabled(false);
			for (CompoundButton tipsButton : tipsButtons) {
				tipsButton.setEnabled(false);
			}
			otherTips.setEnabled(false);
		} else {
			radioGroup.setEnabled(true);
			for (CompoundButton tipsButton : tipsButtons) {
				tipsButton.setEnabled(true);
			}
			otherTips.setEnabled(true);
			if (radioGroup.getCheckedRadioButtonId() == -1) {
				final CompoundButton btn = findById(getActivity(), lastCheckedTipsButtonId);
				if (btn != null) {
					btn.setChecked(true);
					updateTipsButtonState(btn);
				} else if (tipsButtons.size() > 1) {
					tipsButtons.get(1).setChecked(true);
				}
			}
		}

		for (int i = 0; i < tipsButtons.size(); i++) {
			CompoundButton tipsButton = tipsButtons.get(i);
			final int tipsValue = OrderHelper.getTipsValue(mOrder, amount, i);
			if (percentTips) {
				tipsButton.setTag(tipsValue);
				tipsButton.setText(getString(R.string.tip_percent, String.valueOf(tipsValue)));
			} else {
				tipsButton.setTag(R.id.tip, tipsValue);
				tipsButton.setText(String.valueOf(tipsValue));
			}
			updateTipsButtonState(tipsButton);
		}
		updateTipsButtonState(otherTips);
		final BigDecimal selectedTips = getSelectedTips(resultAmount);
		updatePayButton(resultAmount.add(selectedTips));
	}

	private void updateTipsButtonState(final CompoundButton tipsButton) {
		if (tipsButton.getId() == otherTips.getId()) {
			if (tipsButton.isChecked()) {
				tipsButton.setText(getString(R.string.tip_percent, tipsButton.getTag()));
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontNormal);
			} else {
				tipsButton.setText(getString(R.string.tips_another));
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSmall);
			}
		} else {
			if (tipsButton.isChecked()) {
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontNormal);
			} else {
				tipsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSmall);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
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
		if (isInPickerMode()) {
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
}
