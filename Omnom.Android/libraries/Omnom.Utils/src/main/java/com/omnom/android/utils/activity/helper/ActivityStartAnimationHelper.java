package com.omnom.android.utils.activity.helper;

import android.content.Intent;

/**
 * Created by Ch3D on 17.11.2014.
 */
public interface ActivityStartAnimationHelper {
	void start(Intent intent, int delay);

	void start(Intent intent);

	void start(Class<?> cls, int delay);

	void start(Class<?> cls, int animIn, int aninOut);

	void start(Class<?> cls, int animIn, int aninOut, boolean finish);

	void start(Intent intent, boolean finish);

	void start(Intent intent, int animIn, int aninOut);

	void start(Intent intent, int animIn, int aninOut, boolean finish);

	void start(Class<?> cls);

	void start(Class<?> cls, boolean finish);

	void startForResult(Intent intent, int animIn, int animOut, int code);
}
