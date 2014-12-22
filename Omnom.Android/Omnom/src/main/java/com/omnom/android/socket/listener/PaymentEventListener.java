package com.omnom.android.socket.listener;

import android.util.Log;

import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.socket.OmnomSocketBase;
import com.omnom.android.socket.OmnomSocketFactory;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.CroutonHelper;
import com.omnom.android.utils.activity.OmnomActivity;
import com.squareup.otto.Subscribe;

import java.net.URISyntaxException;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Ch3D on 02.12.2014.
 *
 * Listen to #PaymentSocketEvent and notifies a user with #Crouton
 */
public class PaymentEventListener {
	private static final String TAG = PaymentEventListener.class.getSimpleName();

	protected OmnomActivity mActivity;

	private OmnomSocketBase mTableSocket;

	public PaymentEventListener(final OmnomActivity activity) {
		mActivity = activity;
	}

	@Subscribe
	public void onPaymentEvent(final PaymentSocketEvent event) {
		mActivity.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				CroutonHelper.showPaymentNotification(mActivity.getActivity(), event.getPaymentData());
			}
		});
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
		try {
			mTableSocket = OmnomSocketFactory.init(mActivity.getActivity(), table);
			mTableSocket.connect();
			mTableSocket.subscribe(this);
			mTableSocket.subscribe(mActivity);
		} catch(URISyntaxException e) {
			Log.e(TAG, "Unable to initiate socket connection");
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
		Crouton.cancelAllCroutons();
	}
}
