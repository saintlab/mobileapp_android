package com.omnom.android.socket;

import android.content.Context;

import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

/**
 * Created by xCh3Dx on 30.11.2014.
 */
public class OmnomSocketFactory {
	public static OmnomSocketBase init(Context context, Order order) {
		return new OmnomOrderSocket(context, order);
	}

	public static OmnomSocketBase init(Context context, TableDataResponse table) {
		return new OmnomTableSocket(context, table);
	}
}
