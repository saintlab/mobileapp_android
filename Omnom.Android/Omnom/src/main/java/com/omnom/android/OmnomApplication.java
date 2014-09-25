package com.omnom.android;

import android.app.Application;
import android.content.Context;

import com.omnom.android.modules.AcquiringModuleMailRu;
import com.omnom.android.modules.AndroidModule;
import com.omnom.android.modules.ApplicationModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class OmnomApplication extends Application {
	public static OmnomApplication get(Context context) {
		return (OmnomApplication) context.getApplicationContext();
	}

	private final List<Object> injectList = new ArrayList<Object>();
	private ObjectGraph objectGraph;

	protected List<Object> getModules() {
		return Arrays.asList(new AndroidModule(this), new ApplicationModule(), new AcquiringModuleMailRu(this));
	}

	public void inject(final Object object) {
		if(objectGraph == null) {
			injectList.add(object);
		} else {
			objectGraph.inject(object);
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		objectGraph = ObjectGraph.create(getModules().toArray());
		for(final Object obj : injectList) {
			objectGraph.inject(obj);
		}
		injectList.clear();
		inject(this);
	}
}
