package com.omnom.android.fragment.dinner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.holder.DeliveryEntranceData;
import com.omnom.android.activity.order.BaseOrderAcceptedActivity;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.DateUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.view.ErrorEditText;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DeliveryDetailsFragment extends BaseFragment {

	private static final String ARG_RESTAURANT = "param1";

	public static DeliveryDetailsFragment newInstance(Restaurant restaurant) {
		final DeliveryDetailsFragment fragment = new DeliveryDetailsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_RESTAURANT, restaurant);
		fragment.setArguments(args);
		return fragment;
	}

	public static void show(final FragmentManager fragmentManager, final @IdRes int containerId, Restaurant restaurant) {
		fragmentManager.beginTransaction()
		               .addToBackStack(null)
		               .setCustomAnimations(R.anim.fade_in,
		                                    R.anim.nothing_long,
		                                    R.anim.fade_in,
		                                    R.anim.nothing_long)
		               .add(containerId, DeliveryDetailsFragment.newInstance(restaurant))
		               .commit();
	}

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	@InjectView(R.id.txt_address)
	protected ErrorEditText txtAddress;

	@InjectView(R.id.txt_date)
	protected ErrorEditText txtDate;

	@InjectView(R.id.root)
	protected View rootView;

	@InjectView(R.id.content)
	protected View contentView;

	@Nullable
	private Restaurant mRestaurant;

	private boolean mFirstStart = true;

	private DeliveryEntranceData entranceData;

	public DeliveryDetailsFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mRestaurant = getArguments().getParcelable(ARG_RESTAURANT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_delivery_details, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(view.getContext(), (ViewGroup) view, OmnomFont.LSF_LE_REGULAR);
		contentView.setTranslationY(AndroidUtils.getScreenHeightPixels(getActivity()));
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		if(mFirstStart) {
			contentView.postDelayed(new Runnable() {
				@Override
				public void run() {
					contentView.animate().translationY(0).start();
				}
			}, getResources().getInteger(R.integer.default_animation_duration_short));
		}
		mFirstStart = false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		contentView.animate().translationY(rootView.getHeight()).setListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				rootView.animate().alpha(0).start();
			}
		}).start();
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Date now = new Date();
		entranceData = DeliveryEntranceData.create(now, StringUtils.EMPTY_STRING, now);
		onDeliveryDate(DateUtils.parseDate(getMockDateData().get(0)));
		onDeliveryAddress(getMockAddressData().get(0));
	}

	@OnClick(R.id.txt_date)
	public void onDate() {
		DeliveryOptionsFragment.showDate(getFragmentManager(), R.id.fragment_container, getMockDateData());
	}

	@OnClick(R.id.txt_address)
	public void onAddress() {
		DeliveryOptionsFragment.showAddress(getFragmentManager(), R.id.fragment_container, getMockAddressData());
	}

	@OnClick(R.id.btn_close)
	public void onClose() {
		getFragmentManager().popBackStack();
	}

	@OnClick(R.id.btn_ok)
	public void onOk() {
		if(validate()) {
			// TODO: launch validate activity and pass entrance data as intent (Extras.EXTRA_ENTRANCE_DATA)
			BaseOrderAcceptedActivity.start(getActivity(), entranceData, 0, 0);
		}
	}

	private boolean validate() {
		boolean hasErrors = false;
		if(TextUtils.isEmpty(txtAddress.getText())) {
			txtAddress.setError(true, getResources().getString(R.string.select_address));
			hasErrors |= true;
		}
		if(TextUtils.isEmpty(txtDate.getText())) {
			txtDate.setError(true, getResources().getString(R.string.select_date));
			hasErrors |= true;
		}
		return !hasErrors;
	}

	@Subscribe
	public void onDeliveryDate(Date date) {
		txtDate.setError(false);
		if(DateUtils.isTomorrow(date)) {
			txtDate.setText(getResources().getString(R.string.order_date, getString(R.string.tomorrow), DateUtils.getDayAndMonth(date)));
		} else {
			txtDate.setText(getResources().getString(R.string.order_date, DateUtils.getDayOfWeek(date), DateUtils.getDayAndMonth(date)));
		}
		entranceData = DeliveryEntranceData.create(entranceData.orderTime(), entranceData.deliveryAddress(), date);
	}

	@Subscribe
	public void onDeliveryAddress(DeliveryAddressData addressData) {
		txtDate.setError(false);
		final String deliveryAddress = addressData.name() + StringUtils.WHITESPACE + addressData.address();
		txtAddress.setText(addressData.name() + StringUtils.WHITESPACE + addressData.address());
		entranceData = DeliveryEntranceData.create(entranceData.orderTime(), deliveryAddress, entranceData.deliveryTime());
	}

	private ArrayList<String> getMockDateData() {
		final ArrayList<String> data = new ArrayList<String>();
		data.add("26/03/2015");
		data.add("27/03/2015");
		data.add("28/03/2015");
		data.add("29/03/2015");
		data.add("30/03/2015");
		data.add("31/03/2015");
		data.add("01/04/2015");
		return data;
	}

	private ArrayList<DeliveryAddressData> getMockAddressData() {
		final ArrayList<DeliveryAddressData> data = new ArrayList<DeliveryAddressData>();
		data.add(DeliveryAddressData.create("Банк Интеза", "Жопа жопенная, 2"));
		data.add(DeliveryAddressData.create("Банк Рога Илоны", "Жопа жопенная, ff"));
		return data;
	}

}
