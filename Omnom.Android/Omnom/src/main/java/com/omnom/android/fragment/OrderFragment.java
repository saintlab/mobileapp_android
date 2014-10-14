package com.omnom.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.omnom.android.restaurateur.model.order.Order;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

public class OrderFragment extends Fragment {
	private static final String ARG_ORDER = "param1";
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
	private Order mOrder;
	private ListView list = null;
	private RadioGroup radioGroup;

	private RadioButton btnTips1;
	private RadioButton btnTips2;
	private RadioButton btnTips3;
	private RadioButton btnTips4;
	private EditText editAmount;

	public OrderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		return inflater.inflate(R.layout.fragment_order, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		ButterKnife.inject(view);
		list = findById(view, android.R.id.list);
		editAmount = findById(view, R.id.edit_payment_amount);
		editAmount.setText(String.valueOf(mOrder.getTotalAmount()) + "\uf5fc");

		radioGroup = findById(view, R.id.radio_tips);
		final CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
				final String tag = (String) btn.getTag();
				if(isChecked) {
					btn.setText(tag + "%\n" + mOrder.getTipsAmount(Integer.parseInt(tag)));
				} else {
					btn.setText(tag + "%");
				}
			}
		};
		btnTips1 = findById(view, R.id.radio_tips_1);
		btnTips1.setOnCheckedChangeListener(listener);
		btnTips2 = findById(view, R.id.radio_tips_2);
		btnTips2.setOnCheckedChangeListener(listener);
		btnTips3 = findById(view, R.id.radio_tips_3);
		btnTips3.setOnCheckedChangeListener(listener);
		btnTips4 = findById(view, R.id.radio_tips_4);
		btnTips4.setOnCheckedChangeListener(listener);
		btnTips2.setChecked(true);

		final Button btnPay = findById(view, R.id.btn_pay);
		btnPay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				pay();
			}
		});

		list.setAdapter(new OrderItemsAdapter(getActivity(), mOrder.getItems()));
	}

	private void pay() {
		//		final CardInfo card = new CardInfo();
		//		card.setAddCard(true);
		//		card.setPan("4245380000355928");
		//		card.setCvv("358");
		//		card.setExpDate("07.2015");
		//		card.setHolder("DMITRY CHERTENKO");
		//
		//		final UserData user = UserData.create("13", "+79133952320");

		// final String cardId = OmnomApplication.get(getActivity()).getPreferences().getCardId(getActivity());
		final String cardId = "30142837667150364462";
		if(!TextUtils.isEmpty(cardId)) {
			tryToPay(cardId);
		} else {
			startActivity(new Intent(getActivity(), AddCardActivity.class));
		}
	}

	private void tryToPay(final String cardId) {
		//		final CardInfo card = CardInfo.createTestCard(getActivity());
		final CardInfo card = new CardInfo();
		card.setAddCard(true);
		card.setPan("4245380000355928");
		card.setCvv("358");
		card.setExpDate("07.2015");
		card.setHolder("DMITRY CHERTENKO");
		// card.setCardId(cardId);

		final UserData user = UserData.create("13", "89133952320");
		final MerchantData merchant = new MerchantData(getActivity());
		pay(card, merchant, user);
	}

	private void pay(final CardInfo cardInfo, MerchantData merchant, UserData user) {
		final ExtraData extra = MailRuExtra.create(0, mOrder.getRestaurantId());
		final OrderInfo order = OrderInfoMailRu.create(1.0, mOrder.getInternalId(), "message");
		final PaymentInfo paymentInfo = PaymentInfoFactory.create(AcquiringType.MAIL_RU, user, cardInfo, extra, order);
		mAcquiring.pay(merchant, paymentInfo, new Acquiring.PaymentListener<AcquiringPollingResponse>() {
			@Override
			public void onPaymentSettled(AcquiringPollingResponse response) {
				Log.d(TAG, "status = " + response.getStatus());
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
		}
	}
}
