package com.omnom.android.modules;

import com.omnom.android.MainActivity;
import com.omnom.android.OmnomApplication;

import dagger.Module;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {OmnomApplication.class, MainActivity.class},
        complete = false)
public class ApplicationModule {}
