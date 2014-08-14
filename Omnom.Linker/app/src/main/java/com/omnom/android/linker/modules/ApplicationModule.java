package com.omnom.android.linker.modules;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.activity.BindActivity;
import com.omnom.android.linker.activity.LoginActivity;
import com.omnom.android.linker.activity.RestaurantActivity;
import com.omnom.android.linker.activity.RestaurantsListActivity;
import com.omnom.android.linker.activity.SimpleSplashActivity;
import com.omnom.android.linker.activity.ValidationActivity;

import dagger.Module;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {LinkerApplication.class, LoginActivity.class, SimpleSplashActivity.class, ValidationActivity.class, BindActivity.class,
		RestaurantActivity.class, RestaurantsListActivity.class},
        complete = false)
public class ApplicationModule {}
