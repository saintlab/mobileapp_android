package com.omnom.android.modules;

import com.omnom.android.MainActivity;
import com.omnom.android.OmnomApplication;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.SplashActivity;
import com.omnom.android.activity.UserRegisterActivity;

import dagger.Module;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {OmnomApplication.class, MainActivity.class, SplashActivity.class, UserRegisterActivity.class, EnteringActivity.class},
        complete = false)
public class ApplicationModule {}
