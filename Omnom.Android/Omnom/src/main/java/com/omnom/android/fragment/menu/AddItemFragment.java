package com.omnom.android.fragment.menu;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.omnom.android.R;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.UserOrder;

public class AddItemFragment extends Fragment {

	private static final String ARG_ORDER = "order";

	private static final String ARG_ITEM = "item";

	public static AddItemFragment newInstance(UserOrder order, Item item) {
		final AddItemFragment fragment = new AddItemFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putParcelable(ARG_ITEM, item);
		fragment.setArguments(args);
		return fragment;
	}

	private Item mItem;

	private Parcelable mOrder;

	public AddItemFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mItem = getArguments().getParcelable(ARG_ITEM);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menu_add_item, container, false);
	}
}
