package com.omnom.android.acquiring.mailru;

import android.net.http.AndroidHttpClient;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;
import com.omnom.android.BuildConfig;
import com.omnom.android.acquiring.mailru.response.AcquiringPollingResponse;
import com.omnom.android.acquiring.mailru.response.AcquiringResponse;
import com.omnom.android.acquiring.mailru.response.CardRegisterPollingResponse;
import com.omnom.android.acquiring.mailru.response.RegisterCardResponse;
import com.omnom.android.utils.EncryptionUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Ch3D on 23.09.2014.
 */
public class PollingObservable {

	private static final String TAG = PollingObservable.class.getSimpleName();

	private static void sleep() {
		SystemClock.sleep(1000);
	}

	public static Observable<CardRegisterPollingResponse> create(final RegisterCardResponse cardResponse) {
		return Observable.create(new Observable.OnSubscribe<CardRegisterPollingResponse>() {

			@Override
			public void call(Subscriber<? super CardRegisterPollingResponse> subscriber) {
				Gson gson = new Gson();
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android");

				try {
					CardRegisterPollingResponse next = null;
					while(next == null) {
						final HttpResponse execute = client.execute(new HttpPost(cardResponse.getUrl()));
						final HttpEntity entity = execute.getEntity();

						if(entity != null) {
							final Reader reader = new InputStreamReader(entity.getContent());
							final CardRegisterPollingResponse response = gson.fromJson(reader, CardRegisterPollingResponse.class);
							if(!response.getStatus().equals(AcquiringPollingResponse.STATUS_CONTINUE)) {
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
				} finally {
					client.close();
					client = null;
					gson = null;
				}
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static String readFully(InputStream inputStream, String encoding)
			throws IOException {
		return new String(readFully(inputStream), encoding);
	}

	private static byte[] readFully(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		return baos.toByteArray();
	}

	public static Observable<AcquiringResponse> create(final AcquiringResponse cardResponse) {
		return Observable.create(new Observable.OnSubscribe<AcquiringResponse>() {
			@Override
			public void call(Subscriber<? super AcquiringResponse> subscriber) {
				Gson gson = new Gson();
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
				try {
					AcquiringResponse next = null;
					while(next == null) {
						final HttpResponse execute = client.execute(new HttpPost(cardResponse.getUrl()));
						final HttpEntity entity = execute.getEntity();

						if(entity != null) {
							final String resp = readFully(entity.getContent(), EncryptionUtils.UTF_8);
							if(BuildConfig.DEBUG) {
								Log.d(TAG, "Mail_ru polling response = " + resp);
							}
							final AcquiringResponse response = gson.fromJson(resp, AcquiringResponse.class);
							if(BuildConfig.DEBUG && response != null) {
								Log.i(TAG, response.toString());
							}
							if(!response.getStatus().equals(AcquiringPollingResponse.STATUS_CONTINUE)) {
								next = response;
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
				} finally {
					client.close();
					client = null;
					gson = null;
				}
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	public static Observable create(final AcquiringResponse acquiringResponse, final String id) {
		return Observable.create(new Observable.OnSubscribe<AcquiringPollingResponse>() {
			@Override
			public void call(Subscriber<? super AcquiringPollingResponse> subscriber) {
				Gson gson = new Gson();
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
				try {
					CardRegisterPollingResponse next = null;
					while(next == null) {
						final HttpResponse execute = client.execute(new HttpPost(acquiringResponse.getUrl()));
						final HttpEntity entity = execute.getEntity();

						if(entity != null) {
							final Reader reader = new InputStreamReader(entity.getContent());
							final CardRegisterPollingResponse response = gson.fromJson(reader, CardRegisterPollingResponse.class);
							System.err.println(">>>> polling response = " + response.toString());
							if(!response.getStatus().equals(AcquiringPollingResponse.STATUS_CONTINUE)) {
								next = response;
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
				} finally {
					client.close();
					client = null;
					gson = null;
				}
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}
}
