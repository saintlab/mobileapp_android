package com.omnom.android.socket;

import android.content.Context;
import android.support.annotation.Nullable;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.omnom.android.OmnomApplication;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.socket.event.BaseSocketEvent;
import com.omnom.android.socket.event.ConnectedSocketEvent;
import com.omnom.android.socket.event.HandshakeSocketEvent;
import com.omnom.android.socket.event.OrderCloseSocketEvent;
import com.omnom.android.socket.event.OrderCreateSocketEvent;
import com.omnom.android.socket.event.OrderUpdateSocketEvent;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.socket.event.SocketEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.net.URISyntaxException;

import rx.Subscriber;

/**
 * Created by Ch3D on 26.11.2014.
 */
public abstract class OmnomSocketBase implements OmnomSocket {

	public static OmnomSocketBase init(Context context, Order order, String url) throws URISyntaxException {
		return new OmnomOrderSocket(context, order, url);
	}

	public static OmnomSocketBase init(Context context, TableDataResponse table, String url) throws URISyntaxException {
		return new OmnomTableSocket(context, table, url);
	}

	public Subscriber<? super BaseSocketEvent> subscriber;

	@Nullable
	private Bus mBus;

	private Socket mSocket;

	protected Context mContext;

	private String mUrl;

	protected OmnomSocketBase(Context context, String url) throws URISyntaxException {
		mContext = context;
		mUrl = url;
		mSocket = IO.socket(getSocketUrl(OmnomApplication.get(mContext).getAuthToken()));
	}

	private String getSocketUrl(final String token) {
		return mUrl + "?token=" + token;
	}

	public void subscribe(Object listener) {
		mBus.register(listener);
	}

	public void unsubscribe(Object listener) {
		mBus.unregister(listener);
	}

	public void connect() {
		mSocket
				.on(SocketEvent.EVENT_HANDSHAKE, new Emitter.Listener() {
					@Override
					public void call(Object... args) {
						if(args.length == 2) {
							final String status = args[1].toString();
							if(status.contains(HandshakeSocketEvent.HANDSHAKE_SUCCESS)) {
								post(new HandshakeSocketEvent(true));
							} else if(status.contains(HandshakeSocketEvent.HANDSHAKE_ERROR)) {
								post(new HandshakeSocketEvent(false));
							}
						}
					}
				})
				.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
					@Override
					public void call(final Object... args) {
						post(new ConnectedSocketEvent());
					}
				})
				.on(SocketEvent.EVENT_PAYMENT, new Emitter.Listener() {
					@Override
					public void call(final Object... args) {
						post(new PaymentSocketEvent());
					}
				})
				.on(SocketEvent.EVENT_ORDER_CREATE, new Emitter.Listener() {
					@Override
					public void call(final Object... args) {
						post(new OrderCreateSocketEvent());
					}
				})
				.on(SocketEvent.EVENT_ORDER_UPDATE, new Emitter.Listener() {
					@Override
					public void call(final Object... args) {
						post(new OrderUpdateSocketEvent());
					}
				})
				.on(SocketEvent.EVENT_ORDER_CLOSE, new Emitter.Listener() {
					@Override
					public void call(final Object... args) {
						post(new OrderCloseSocketEvent());
					}
				});
		mBus = new Bus(ThreadEnforcer.MAIN);
		mSocket.connect();
	}

	public void disconnect() {
		mSocket.disconnect();
	}

	public void destroy() {
		mSocket = null;
		mBus = null;
	}

	protected void post(final BaseSocketEvent event) {
		mBus.post(event);
	}
}
