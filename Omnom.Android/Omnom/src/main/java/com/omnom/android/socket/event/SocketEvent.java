package com.omnom.android.socket.event;

/**
 * Created by Ch3D on 26.11.2014.
 */
public interface SocketEvent {
	public static final String EVENT_HANDSHAKE = "handshake";

	public static final String EVENT_CONNECTED = "handshake";

	public static final String EVENT_PAYMENT = "payment";

	public static final String EVENT_ORDER_CREATE = "order_create";

	public static final String EVENT_ORDER_UPDATE = "order_update";

	public static final String EVENT_ORDER_CLOSE = "order_close";

	public static final String EVENT_JOIN = "join";

	public static final String EVENT_LEAVE = "leave";

	String getType();
}
