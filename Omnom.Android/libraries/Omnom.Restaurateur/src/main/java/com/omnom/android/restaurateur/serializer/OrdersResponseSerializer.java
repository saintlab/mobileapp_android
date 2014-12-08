package com.omnom.android.restaurateur.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.OrdersResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvpotter on 12/8/2014.
 */
public class OrdersResponseSerializer implements JsonDeserializer<OrdersResponse> {

	@Override
	public OrdersResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		Type type = new TypeToken<ArrayList<Order>>(){}.getType();
		List<Order> orders = context.deserialize(json, type);
		OrdersResponse ordersResponse = new OrdersResponse();
		ordersResponse.setOrders(orders);
		return ordersResponse;
	}

}
