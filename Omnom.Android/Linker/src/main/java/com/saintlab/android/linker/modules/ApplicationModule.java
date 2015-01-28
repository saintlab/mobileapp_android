package com.saintlab.android.linker.modules;

import com.google.zxing.client.android.CaptureActivity;
import com.saintlab.android.linker.LinkerApplication;
import com.saintlab.android.linker.activity.LoginActivity;
import com.saintlab.android.linker.activity.SimpleSplashActivity;
import com.saintlab.android.linker.activity.ValidationActivity;
import com.saintlab.android.linker.activity.bind.BindActivity;
import com.saintlab.android.linker.activity.restaurant.RestaurantsListActivity;
import com.omnom.android.beacon.BeaconRssiProviderSimple;
import com.saintlab.android.linker.service.BluetoothLeService;
import com.saintlab.android.linker.activity.UserProfileActivity;
import com.saintlab.android.linker.activity.restaurant.RestaurantActivity;

import dagger.Module;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {LinkerApplication.class, LoginActivity.class, SimpleSplashActivity.class, ValidationActivity.class, BindActivity.class,
		RestaurantActivity.class, RestaurantsListActivity.class, UserProfileActivity.class, BluetoothLeService.class,
		BeaconRssiProviderSimple.class, CaptureActivity.class},
        complete = false)
public class ApplicationModule {}
