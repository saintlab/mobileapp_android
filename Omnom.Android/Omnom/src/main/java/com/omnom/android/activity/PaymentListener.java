package com.omnom.android.activity;

import android.util.Log;

import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.socket.OmnomSocketBase;
import com.omnom.android.socket.OmnomSocketFactory;
import com.omnom.android.socket.event.PaymentSocketEvent;
import com.omnom.android.utils.CroutonHelper;
import com.squareup.otto.Subscribe;

import java.net.URISyntaxException;

import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by Ch3D on 02.12.2014.
 */
public class PaymentListener {
	private static final String TAG = PaymentListener.class.getSimpleName();

	private BaseOmnomActivity mActivity;

	private OmnomSocketBase mTableSocket;

	private Crouton mCrouton;

	public PaymentListener(final BaseOmnomActivity activity) {
		mActivity = activity;
	}

	@Subscribe
	public void onPaymentEvent(final PaymentSocketEvent event) {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mCrouton = CroutonHelper.createPaymentNotification(mActivity, event.getPaymentData());
				if(mCrouton != null) {
					mCrouton.show();
				}
			}
		});
	}

	public void initTableSocket(final TableDataResponse table) {
		try {
			mTableSocket = OmnomSocketFactory.init(mActivity, table);
			mTableSocket.connect();
			mTableSocket.subscribe(this);
		} catch(URISyntaxException e) {
			Log.e(TAG, "Unable to initiate socket connection");
		}
	}

	public void onPause() {
		if(mTableSocket != null) {
			mTableSocket.unsubscribe(this);
			mTableSocket.disconnect();
			mTableSocket.destroy();
			mTableSocket = null;
		}
	}

	public void onDestroy() {
		Crouton.cancelAllCroutons();
	}
}
