package com.omnom.android.acquiring.mailru;

import android.net.http.AndroidHttpClient;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class PollingObservable extends Observable<CardRegisterPollingResponse> {

	private static void sleep() {
		SystemClock.sleep(1000);
	}

	protected PollingObservable(final RegisterCardResponse cardResponse) {
		super(new OnSubscribe<CardRegisterPollingResponse>() {

			private final Gson gson = new Gson();
			private final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");

			@Override
			public void call(Subscriber<? super CardRegisterPollingResponse> subscriber) {
				try {
					CardRegisterPollingResponse next = null;
					while(next == null) {
						final HttpResponse execute = client.execute(new HttpPost(cardResponse.getUrl()));
						final HttpEntity entity = execute.getEntity();

						if(entity != null) {
							final Reader reader = new InputStreamReader(entity.getContent());
							final CardRegisterPollingResponse response = gson.fromJson(reader, CardRegisterPollingResponse.class);
							System.err.println(Thread.currentThread().getName() + " polling status = " + response.getStatus());
							if(!response.getStatus().equals("OK_CONTINUE")) {
								next = response;
								next.setCardId(cardResponse.getCardId());
							} else {
								sleep();
							}
						} else {
							sleep();
						}
					}
					subscriber.onNext(next);
					subscriber.onCompleted();
				} catch(IOException e) {
					subscriber.onError(e);
				}
			}
		});
	}
}
