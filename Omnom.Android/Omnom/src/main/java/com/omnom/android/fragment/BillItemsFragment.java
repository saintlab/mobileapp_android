package com.omnom.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.adapter.OrderItemsAdapter;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.otto.Bus;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Ch3D on 11.11.2014.
 */
public class BillItemsFragment extends ListFragment implements SplitFragment {

	private static final String ARG_ORDER = "order";
	private static final String ARG_STATES = "states";

	public static Fragment newInstance(final Order order, final SparseBooleanArrayParcelable states) {
		final BillItemsFragment fragment = new BillItemsFragment();
		final Bundle args = new Bundle();
		args.putParcelable(ARG_ORDER, order);
		args.putParcelable(ARG_STATES, states);
		fragment.setArguments(args);
		return fragment;
	}

	@Inject
	protected Bus mBus;

	private Order mOrder;

	private int mAmount;

	private OrderItemsAdapter mAdapter;

	private SparseBooleanArrayParcelable mStates;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			mOrder = getArguments().getParcelable(ARG_ORDER);
			mStates = getArguments().getParcelable(ARG_STATES);
		}
	}

	@Override
	public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = new OrderItemsAdapter(getActivity(), mOrder.getItems(), mStates, false);
		getListView().setDividerHeight(ViewUtils.dipToPixels(getActivity(), 1));
		getListView().setDivider(getResources().getDrawable(R.drawable.divider_list_padding));
		final View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_footer_empty, null, false);
		getListView().addFooterView(footerView);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				final Boolean tagSelected = (Boolean) view.getTag(R.id.selected);
				final boolean checked = tagSelected != null && tagSelected;
				final HeaderViewListAdapter adapter = (HeaderViewListAdapter) getListView().getAdapter();
				final OrderItemsAdapter wrappedAdapter = (OrderItemsAdapter) adapter.getWrappedAdapter();
				final boolean selected = !checked;
				wrappedAdapter.setSelected(position, selected);
				mBus.post(new OrderItemSelectedEvent(position, selected));
				view.setTag(R.id.selected, selected);
				wrappedAdapter.notifyDataSetChanged();
				updateAmount();
			}
		});
		updateAmount();
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	private BigDecimal getAmount() {
		int result = 0;
		final List<OrderItem> selectedItems = mAdapter.getSelectedItems();
		for(final OrderItem item : selectedItems) {
			result += item.getPricePerItem();
		}
		return new BigDecimal(result);
	}

	@Override
	public void updateAmount() {
		final Button btnCommit = (Button) getActivity().findViewById(R.id.btn_commit);
		final BigDecimal amount = getAmount();
		if(amount.compareTo(BigDecimal.ZERO) > 0) {
			btnCommit.setText(getString(R.string.bill_split_amount_, StringUtils.formatCurrency(amount)));
			btnCommit.setTag(R.id.edit_amount, amount);
			AnimationUtils.animateAlpha(btnCommit, true);
		} else {
			AnimationUtils.animateAlpha(btnCommit, false);
		}
	}
}
