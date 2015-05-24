package com.omnom.android.socket.listener;

import android.content.Context;

import com.omnom.android.restaurateur.model.table.TableDataResponse;

/**
 * Created by Ch3D on 22.05.2015.
 */
public class TableSocketListener {
	private final Context mContext;

	private PaymentEventListener mTableSocketListener;

	public TableSocketListener(final Context context) {
		mContext = context;
	}

	public void connect(final TableDataResponse table) {
		mTableSocketListener = new PaymentEventListener(mContext);
		mTableSocketListener.initTableSocket(table);
	}

	public void disconnect() {
		if(isConnected()) {
			mTableSocketListener.onPause();
			mTableSocketListener.onDestroy();
		}
	}

	public boolean isConnected() {
		return mTableSocketListener != null && mTableSocketListener.isConnected();
	}
}
