package com.omnom.android.linker.modules;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.activity.LoginActivity;
import com.omnom.android.linker.activity.SimpleSplashActivity;
import com.omnom.android.linker.activity.UserProfileActivity;
import com.omnom.android.linker.activity.ValidationActivity;
import com.omnom.android.linker.activity.bind.BindActivity;
import com.omnom.android.linker.activity.restaurant.RestaurantActivity;
import com.omnom.android.linker.activity.restaurant.RestaurantsListActivity;
import com.omnom.android.linker.beacon.BeaconRssiProviderSimple;
import com.omnom.android.linker.service.BluetoothLeService;

import dagger.Module;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {LinkerApplication.class, LoginActivity.class, SimpleSplashActivity.class, ValidationActivity.class, BindActivity.class,
		RestaurantActivity.class, RestaurantsListActivity.class, UserProfileActivity.class, BluetoothLeService.class,
		BeaconRssiProviderSimple.class},
        complete = false)
public class ApplicationModule {}
