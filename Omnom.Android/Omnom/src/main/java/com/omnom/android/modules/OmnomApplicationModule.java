package com.omnom.android.modules;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.OmnomApplication;
import com.omnom.android.activity.CardAddActivity;
import com.omnom.android.activity.CardConfirmActivity;
import com.omnom.android.activity.CardsActivity;
import com.omnom.android.activity.ChangePhoneActivity;
import com.omnom.android.activity.ChangePhoneSuccessActivity;
import com.omnom.android.activity.ConfirmPhoneActivity;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.LoginActivity;
import com.omnom.android.activity.OmnomQRCaptureActivity;
import com.omnom.android.activity.OrderResultActivity;
import com.omnom.android.activity.OrdersActivity;
import com.omnom.android.activity.PaymentProcessActivity;
import com.omnom.android.activity.RestaurantActivity;
import com.omnom.android.activity.RestaurantsListActivity;
import com.omnom.android.activity.ThanksActivity;
import com.omnom.android.activity.ThanksDemoActivity;
import com.omnom.android.activity.UserProfileActivity;
import com.omnom.android.activity.UserRegisterActivity;
import com.omnom.android.activity.ValidateActivityBle;
import com.omnom.android.activity.ValidateActivityBle21;
import com.omnom.android.activity.ValidateActivityCamera;
import com.omnom.android.activity.ValidateActivityShortcut;
import com.omnom.android.activity.WebActivity;
import com.omnom.android.activity.WishActivity;
import com.omnom.android.activity.WishSentActivity;
import com.omnom.android.activity.order.BarOrderAcceptedActivity;
import com.omnom.android.activity.order.BaseOrderAcceptedActivity;
import com.omnom.android.activity.order.LunchOrderAcceptedActivity;
import com.omnom.android.activity.order.TakeawayOrderAcceptedActivity;
import com.omnom.android.fragment.BillItemsFragment;
import com.omnom.android.fragment.BillSplitFragment;
import com.omnom.android.fragment.BillSplitPersonsFragment;
import com.omnom.android.fragment.EditHashFragment;
import com.omnom.android.fragment.EnteringFragment;
import com.omnom.android.fragment.OrderFragment;
import com.omnom.android.fragment.SplashFragment;
import com.omnom.android.fragment.delivery.DeliveryDetailsFragment;
import com.omnom.android.fragment.delivery.DeliveryOptionsFragment;
import com.omnom.android.fragment.menu.MenuItemAddFragment;
import com.omnom.android.fragment.menu.MenuItemDetailsFragment;
import com.omnom.android.fragment.takeaway.TakeawayTimeFragment;
import com.omnom.android.push.PushNotificationsManagerImpl;
import com.omnom.android.service.bluetooth.BackgroundBleService;

import dagger.Module;

/**
 * Created by Ch3D on 11.08.2014.
 */
@Module(injects = {
		OmnomApplication.class,
		PushNotificationsManagerImpl.class,
		/* activities */
		EnteringActivity.class, UserRegisterActivity.class, EnteringActivity.class,
		ConfirmPhoneActivity.class, LoginActivity.class, ChangePhoneActivity.class, ChangePhoneSuccessActivity.class,
		ValidateActivityBle.class, ValidateActivityCamera.class,
		ValidateActivityShortcut.class, ValidateActivityBle.class, ValidateActivityBle21.class, OrdersActivity.class, CardsActivity.class,
		UserProfileActivity.class,
		CardAddActivity.class, CardConfirmActivity.class,
		BaseOrderAcceptedActivity.class, BarOrderAcceptedActivity.class,
		LunchOrderAcceptedActivity.class, TakeawayOrderAcceptedActivity.class,
		ThanksActivity.class, ThanksDemoActivity.class, CaptureActivity.class, OmnomQRCaptureActivity.class,
		PaymentProcessActivity.class, RestaurantsListActivity.class, RestaurantActivity.class, WishActivity.class, WishSentActivity.class,
		WebActivity.class, OrderResultActivity.class,
		/* services */
		BackgroundBleService.class,
		/* fragments */
		SplashFragment.class, EnteringFragment.class, OrderFragment.class, BillSplitFragment.class, BillItemsFragment.class,
		BillSplitPersonsFragment.class, EditHashFragment.class, OrderFragment.class, MenuItemAddFragment.class,
		DeliveryDetailsFragment.class, MenuItemDetailsFragment.class, BillSplitFragment.class, BillItemsFragment.class,
		BillSplitPersonsFragment.class, DeliveryOptionsFragment.class, TakeawayTimeFragment.class},
		complete = false)
public class OmnomApplicationModule {}
