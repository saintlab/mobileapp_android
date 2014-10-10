package com.omnom.android.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

import com.omnom.android.adapter.OrderItemsAdapter;
import com.omnom.android.restaurateur.model.order.Order;

public class OrderFragment extends ListFragment {
	private static final String ARG_ORDER = "param1";

	public static Fragment newInstance(Parcelable parcelable) {
		final OrderFragment fragment = new OrderFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, parcelable);
		fragment.setArguments(args);
		return fragment;
	}

	private Order mOrder;

	public OrderFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
		}

		setListAdapter(new OrderItemsAdapter(getActivity(), mOrder.getItems()));
	}
}
