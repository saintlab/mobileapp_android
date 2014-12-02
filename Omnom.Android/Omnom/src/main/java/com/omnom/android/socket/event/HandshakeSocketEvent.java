package com.omnom.android.socket.event;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class HandshakeSocketEvent extends BaseSocketEvent {
	public static final String HANDSHAKE_ERROR = "error";

	public static final String HANDSHAKE_SUCCESS = "success";

	private boolean mSuccess;

	public HandshakeSocketEvent(final boolean success) {
		mSuccess = success;
	}

	public boolean isSuccess() {
		return mSuccess;
	}
}
