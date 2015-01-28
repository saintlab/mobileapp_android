package com.saintlab.android.linker;

import android.test.suitebuilder.annotation.SmallTest;

import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.beacon.BeaconDataResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;

import rx.functions.Action1;

/**
 * Created by Ch3D on 25.08.2014.
 */
public class LinkerApiTest {

	// TODO: Inject
	RestaurateurObeservableApi api;

	@SmallTest
	public void testBind() {
		// TODO: implement
//		authenticator.bindBeacon("A", 27, new Beacon.Builder().setId1("E2C56DB5-DFFB-48D2-B060-D0F5A7109E0").setId2("1").setId3("27").build())
//		   .subscribe(new Action1<BeaconDataResponse>() {
//			   @Override
//			   public void call(BeaconDataResponse data) {
//				   System.err.println("bind = " + data);
//			   }
//		   });
	}

	@SmallTest
	public void testQrBind() {
		// TODO: implement
		api.bindQrCode("A", 27, "test_qr_data")
		   .subscribe(new Action1<TableDataResponse>() {
			   @Override
			   public void call(TableDataResponse data) {
				   System.err.println("bind = " + data);
			   }
		   });
	}

	@SmallTest
	public void testBuild() {
		// TODO: implement
		api.buildBeacon("A", 27, "E2C56DB5-DFFB-48D2-B060-D0F5A7109E0").subscribe(new Action1<BeaconDataResponse>() {
			@Override
			public void call(BeaconDataResponse data) {
				System.err.println("buildBeacon = " + data);
			}
		});
	}

}
