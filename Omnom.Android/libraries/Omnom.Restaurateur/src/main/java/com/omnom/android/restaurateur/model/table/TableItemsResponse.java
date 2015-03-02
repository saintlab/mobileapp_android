package com.omnom.android.restaurateur.model.table;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;
import com.omnom.android.restaurateur.model.order.OrderItem;

/**
 * Created by mvpotter on 12/8/2014.
 */
public class TableItemsResponse extends ResponseBase {

	@Expose
	public OrderItem[] items;
}
