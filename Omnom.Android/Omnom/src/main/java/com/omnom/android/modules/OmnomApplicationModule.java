package com.omnom.android.modules;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.OmnomApplication;
import com.omnom.android.activity.CardAddActivity;
import com.omnom.android.activity.CardConfirmActivity;
import com.omnom.android.activity.CardsActivity;
import com.omnom.android.activity.ConfirmPhoneActivity;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.LoginActivity;
import com.omnom.android.activity.OmnomQRCaptureActivity;
import com.omnom.android.activity.OrdersActivity;
import com.omnom.android.activity.SplashActivity;
import com.omnom.android.activity.ThanksActivity;
import com.omnom.android.activity.UserProfileActivity;
import com.omnom.android.activity.UserRegisterActivity;
import com.omnom.android.activity.ValidateActivityBle;
import com.omnom.android.activity.ValidateActivityCamera;
import com.omnom.android.service.bluetooth.BackgroundBleService;
import com.omnom.android.fragment.BillItemsFragment;
import com.omnom.android.fragment.BillSplitFragment;
import com.omnom.android.fragment.BillSplitPersonsFragment;
import com.omnom.android.fragment.OrderFragment;

import dagger.Module;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {OmnomApplication.class,
		SplashActivity.class, UserRegisterActivity.class, EnteringActivity.class,
		/* activities */
		ConfirmPhoneActivity.class, LoginActivity.class, ValidateActivityBle.class, ValidateActivityCamera.class,
		ValidateActivityBle.class, OrdersActivity.class, CardsActivity.class, UserProfileActivity.class, CardAddActivity.class,
		CardConfirmActivity.class, ThanksActivity.class, CaptureActivity.class, OmnomQRCaptureActivity.class,
		/* services */
		BackgroundBleService.class,
		/* fragments */
		OrderFragment.class, BillSplitFragment.class, BillItemsFragment.class, BillSplitPersonsFragment.class},
        complete = false)
public class OmnomApplicationModule {}
