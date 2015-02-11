package com.omnom.android.socket;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omnom.android.BuildConfig;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.restaurateur.model.order.Order;
import com.omnom.android.restaurateur.model.order.PaymentData;
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

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by Ch3D on 26.11.2014.
 */
public abstract class OmnomSocketBase implements OmnomSocket {

	private static final String TAG = OmnomSocketBase.class.getSimpleName();

	public abstract class SafeEmitterListener implements Emitter.Listener {
		@Override
		public final void call(final Object... args) {
			try {
			safeCall(args);
			} catch(Throwable e) {
				Log.e(TAG, "SafeEmitterListener.call", e);
			}
		}

		public abstract void safeCall(final Object... args);
	}

	private final Gson mGson;

	private final String mBaseUrl;

	protected Context mContext;

	@Nullable
	private Bus mBus;

	private Socket mSocket;

	protected OmnomSocketBase(Context context) {
		mContext = context;
		mBaseUrl = mContext.getString(R.string.endpoint_restaurateur) + "?token=" + OmnomApplication.get(context).getAuthToken();
		final GsonBuilder builder = new GsonBuilder();
		builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		mGson = builder.create();
	}

	public void subscribe(Object listener) {
		mBus.register(listener);
	}

	public void unsubscribe(Object listener) {
		mBus.unregister(listener);
	}

	public void connect() throws URISyntaxException {
		mSocket = IO.socket(mBaseUrl);
		mSocket.on(SocketEvent.EVENT_HANDSHAKE, new SafeEmitterListener() {
			@Override
			public void safeCall(Object... args) {
				logEvent(SocketEvent.EVENT_HANDSHAKE, args);
				if(args.length == 2) {
					final String status = args[1].toString();
					if(status.contains(HandshakeSocketEvent.HANDSHAKE_SUCCESS)) {
						post(new HandshakeSocketEvent(true));
						mSocket.emit(SocketEvent.EVENT_JOIN, getRoomId());
					} else if(status.contains(HandshakeSocketEvent.HANDSHAKE_ERROR)) {
						post(new HandshakeSocketEvent(false));
					}
				}
			}
		}).on(Socket.EVENT_CONNECT, new SafeEmitterListener() {
			@Override
			public void safeCall(final Object... args) {
				logEvent(Socket.EVENT_CONNECT, args);
				post(new ConnectedSocketEvent());
			}
		}).on(SocketEvent.EVENT_PAYMENT, new SafeEmitterListener() {
			@Override
			public void safeCall(final Object... args) {
				logEvent(SocketEvent.EVENT_PAYMENT, args);
				final JSONObject json = (JSONObject) args[0];
				final PaymentData paymentData = mGson.fromJson(json.toString(), PaymentData.class);
				post(new PaymentSocketEvent(paymentData));
			}
		}).on(SocketEvent.EVENT_ORDER_CREATE, new SafeEmitterListener() {
			@Override
			public void safeCall(final Object... args) {
				logEvent(SocketEvent.EVENT_ORDER_CREATE, args);
				final JSONObject json = (JSONObject) args[0];
				final Order order = mGson.fromJson(json.toString(), Order.class);
				post(new OrderCreateSocketEvent(order));
			}
		}).on(SocketEvent.EVENT_ORDER_UPDATE, new SafeEmitterListener() {
			@Override
			public void safeCall(final Object... args) {
				logEvent(SocketEvent.EVENT_ORDER_UPDATE, args);
				final JSONObject json = (JSONObject) args[0];
				final Order order = mGson.fromJson(json.toString(), Order.class);
				post(new OrderUpdateSocketEvent(order));
			}
		}).on(SocketEvent.EVENT_ORDER_CLOSE, new SafeEmitterListener() {
			@Override
			public void safeCall(final Object... args) {
				logEvent(SocketEvent.EVENT_ORDER_CLOSE, args);
				final JSONObject json = (JSONObject) args[0];
				final Order order = mGson.fromJson(json.toString(), Order.class);
				post(new OrderCloseSocketEvent(order));
			}
		});
		mBus = new Bus(ThreadEnforcer.ANY);
		if(BuildConfig.DEBUG) {
			Log.d(TAG, "Connecting to " + mBaseUrl);
		}
		mSocket.connect();
	}

	private void logEvent(String event, Object[] args) {
		if(BuildConfig.DEBUG) {
			Log.d(TAG, "Event [" + event + "] data = " + Arrays.toString(args));
		}
	}

	protected abstract String getRoomId();

	public void disconnect() {
		if(BuildConfig.DEBUG) {
			Log.d(TAG, "Disconnecting from socket for roomId = " + getRoomId());
		}
		mSocket.disconnect();
	}

	public void destroy() {
		if(BuildConfig.DEBUG) {
			Log.d(TAG, "Destroying socket for roomId = " + getRoomId());
		}
		mSocket.off();
		mSocket = null;
		mBus = null;
	}

	protected void post(final BaseSocketEvent event) {
		if(mBus == null) {
			Log.w(TAG, "skip socket event = " + event);
			return;
		}
		mBus.post(event);
	}
}
