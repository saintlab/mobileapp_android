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
import com.omnom.android.currency.Money;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrderItem;
import com.omnom.android.utils.SparseBooleanArrayParcelable;
import com.omnom.android.utils.utils.AnimationUtils;
import com.squareup.otto.Bus;

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
		mAdapter = new OrderItemsAdapter(getActivity(), OmnomApplication.getCurrency(getActivity()), mOrder.getItems(), mStates, false);
		getListView().setDividerHeight(0);
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

	private Money getAmount() {
		double result = 0;
		final List<OrderItem> selectedItems = mAdapter.getSelectedItems();
		for(final OrderItem item : selectedItems) {
			result += item.getPriceTotal();
		}
		return Money.createFractional(result, OmnomApplication.getCurrency(getActivity()));
	}

	@Override
	public void updateAmount() {
		final Button btnCommit = (Button) getActivity().findViewById(R.id.btn_commit);
		final View viewBehindBtn = getActivity().findViewById(R.id.view_behind_btn);
		final Money amount = getAmount();
		boolean showPayBtn = (mAdapter != null && !mAdapter.getSelectedItems().isEmpty());
		if(showPayBtn) {
			btnCommit.setTag(R.id.edit_amount, amount);
			btnCommit.setTag(R.id.split_type, BillSplitFragment.SPLIT_TYPE_ITEMS);
			final String text = getString(R.string.bill_split_amount_, amount.getReadableValue());
			btnCommit.setClickable(true);
			AnimationUtils.animateAlpha(viewBehindBtn, true);
			AnimationUtils.animateAlphaGone(btnCommit, true);
			btnCommit.setText(text);
		} else {
			btnCommit.setClickable(false);
			AnimationUtils.animateAlpha(viewBehindBtn, false);
			AnimationUtils.animateAlphaGone(btnCommit, false);
		}
	}

	@Override
	public void onOrderUpdate(final Order order) {
		if(mAdapter != null) {
			mAdapter.clearSelection();
			mAdapter = new OrderItemsAdapter(getActivity(), OmnomApplication.getCurrency(getActivity()), order.getItems(), mStates, false);
			setListAdapter(mAdapter);
			updateAmount();
		}
	}
}
