package com.omnom.android.socket.event;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class ConnectedSocketEvent extends BaseSocketEvent {

	public ConnectedSocketEvent() {
	}

	@Override
	public String getType() {
		return SocketEvent.EVENT_CONNECTED;
	}
}
