package com.omnom.android.socket.event;

import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by Ch3D on 26.11.2014.
 */
public class ConnectedSocketEvent extends BaseSocketEvent {
	@Override
	public String getName() {
		return Socket.EVENT_CONNECT;
	}
}
