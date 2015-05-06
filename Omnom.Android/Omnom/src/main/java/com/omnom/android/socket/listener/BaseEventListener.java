package com.omnom.android.socket.listener;

import android.util.Log;

import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.socket.OmnomSocketBase;
import com.omnom.android.socket.OmnomSocketFactory;
import com.omnom.android.utils.activity.OmnomActivity;

import java.net.URISyntaxException;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class BaseEventListener {

	private static final String TAG = PaymentEventListener.class.getSimpleName();

	protected final OmnomActivity mActivity;

	protected OmnomSocketBase mTableSocket;

	public BaseEventListener(final OmnomActivity activity) {
		mActivity = activity;
	}

	public void initTableSocket(final String tableId) {
		try {
			mTableSocket = OmnomSocketFactory.initTable(mActivity.getActivity(), tableId);
			mTableSocket.connect();
			mTableSocket.subscribe(this);
			mTableSocket.subscribe(mActivity);
		} catch(URISyntaxException e) {
			Log.e(TAG, "Unable to initiate socket connection");
		}
	}

	public void initTableSocket(final TableDataResponse table) {
		if(table != null && table != TableDataResponse.NULL) {
			try {
				mTableSocket = OmnomSocketFactory.init(mActivity.getActivity(), table);
				mTableSocket.connect();
				mTableSocket.subscribe(this);
				mTableSocket.subscribe(mActivity);
			} catch(URISyntaxException e) {
				Log.e(TAG, "Unable to initiate socket connection");
			}
		} else {
			Log.d(TAG, "unable to init websocket for table = " + table);
		}
	}

	public void onPause() {
		if(mTableSocket != null) {
			mTableSocket.unsubscribe(this);
			mTableSocket.unsubscribe(mActivity);
			mTableSocket.disconnect();
			mTableSocket.destroy();
			mTableSocket = null;
		}
	}

	public void onDestroy() {

	}

}
