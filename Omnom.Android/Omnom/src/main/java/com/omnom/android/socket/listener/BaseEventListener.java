package com.omnom.android.socket.listener;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.socket.OmnomSocketBase;
import com.omnom.android.socket.OmnomSocketFactory;

import java.net.URISyntaxException;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class BaseEventListener {

	private static final String TAG = PaymentEventListener.class.getSimpleName();

	protected final Context mContext;

	protected final Handler mHandler;

	protected OmnomSocketBase mTableSocket;

	public BaseEventListener(final Context context) {
		mContext = context;
		mHandler = new Handler();
	}

	public void initTableSocket(final String tableId) {
		try {
			mTableSocket = OmnomSocketFactory.initTable(mContext, tableId);
			mTableSocket.connect();
			mTableSocket.subscribe(this);
			mTableSocket.subscribe(mContext);
		} catch(URISyntaxException e) {
			Log.e(TAG, "Unable to initiate socket connection");
		}
	}

	public void initTableSocket(final TableDataResponse table) {
		if(table != null && table != TableDataResponse.NULL) {
			try {
				mTableSocket = OmnomSocketFactory.init(mContext, table);
				mTableSocket.connect();
				mTableSocket.subscribe(this);
				mTableSocket.subscribe(mContext);
			} catch(URISyntaxException e) {
				Log.e(TAG, "Unable to initiate socket connection");
			}
		} else {
			Log.d(TAG, "unable to connect websocket for table = " + table);
		}
	}

	public void onPause() {
		if(mTableSocket != null) {
			mTableSocket.unsubscribe(this);
			mTableSocket.unsubscribe(mContext);
			mTableSocket.disconnect();
			mTableSocket.destroy();
			mTableSocket = null;
		}
	}

	public boolean isConnected() {
		return mTableSocket != null && mTableSocket.isConnected();
	}

	public void onDestroy() {

	}

}
