package com.omnom.android.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.restaurateur.model.order.Transaction;
import com.omnom.android.utils.utils.AmountHelper;

import java.util.List;

/**
 * Created by mvpotter on 2/17/2015.
 */
public class OrderPayersAdapter extends BaseAdapter {

	private final List<Transaction> payments;
	private final String currencySuffix;

	private final LayoutInflater layoutInflater;

	public OrderPayersAdapter(final Activity activity, final List<Transaction> payments, final String currencySuffix) {
		this.payments = payments;
		this.currencySuffix = currencySuffix;
		this.layoutInflater = activity.getLayoutInflater();
	}

	@Override
	public int getCount() {
		return payments.size();
	}

	@Override
	public Object getItem(int position) {
		return payments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.item_payer, parent, false);
		}

		final Transaction payment = getPayment(position);
		((TextView) view.findViewById(R.id.txt_name)).setText(String.valueOf(payment.getUserId()));
		((TextView) view.findViewById(R.id.txt_amount)).setText(AmountHelper.format(payment.getAmount()) + currencySuffix);

		return view;
	}

	protected Transaction getPayment(final int position) {
		return (Transaction) getItem(position);
	}

}
