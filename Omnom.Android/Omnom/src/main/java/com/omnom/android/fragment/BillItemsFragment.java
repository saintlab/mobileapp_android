package com.omnom.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.Button;

import com.omnom.android.R;
import com.omnom.android.adapter.OrderItemsAdapter;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.List;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillItemsFragment extends ListFragment implements SplitFragment {

	private static final String ARG_ORDER = "order";

	public static Fragment newInstance(final Order order) {
		final BillItemsFragment fragment = new BillItemsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		fragment.setArguments(args);
		return fragment;
	}

	private Order mOrder;

	private int mAmount;

	private OrderItemsAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
		}
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getListView().setDividerHeight(ViewUtils.dipToPixels(getActivity(), 1));
		getListView().setDivider(getResources().getDrawable(R.drawable.divider_list_padding));
		mAdapter = new OrderItemsAdapter(getActivity(), mOrder.getItems());
		setListAdapter(mAdapter);
		updateAmount();
	}

	private double getAmount() {
		int result = 0;
		final List<OrderItem> selectedItems = mAdapter.getSelectedItems();
		for(final OrderItem item : selectedItems) {
			result += item.getPricePerItem();
		}
		return result;
	}

	@Override
	public void updateAmount() {
		final Button btnCommit = (Button) getActivity().findViewById(R.id.btn_commit);
		final double amount = getAmount();
		if(amount > 0) {
			btnCommit.setText(StringUtils.formatCurrency(amount));
			AnimationUtils.animateAlpha(btnCommit, true);
		} else {
			AnimationUtils.animateAlpha(btnCommit, false);
		}
	}
}
