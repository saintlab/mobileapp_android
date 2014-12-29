package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.google.zxing.client.android.CaptureActivity;
import com.omnom.android.BuildConfig;
import com.omnom.android.R;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.mixpanel.MixPanelHelper;
import com.omnom.android.mixpanel.model.OnTableMixpanelEvent;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.decode.QrDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.table.TableDataResponse;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.observable.ValidationObservable;
import com.omnom.android.utils.utils.AndroidUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class ValidateActivityCamera extends ValidateActivity {
	public static final String DEVICE_ID_GENYMOTION = "000000000000000";

	private static final String TAG = ValidateActivityCamera.class.getSimpleName();

	public static void start(BaseActivity context, int enterAnim, int exitAnim, int animationType, final int userEnterType) {
		Intent intent = createIntent(context, animationType, false, userEnterType);
		if(context instanceof ConfirmPhoneActivity) {
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		context.start(intent, enterAnim, exitAnim, false);
	}

	private static Intent createIntent(Context context, int animationType, boolean isDemo, int userEnterType) {
		final Intent intent = new Intent(context, ValidateActivityCamera.class);
		intent.putExtra(EXTRA_LOADER_ANIMATION, animationType);
		intent.putExtra(EXTRA_DEMO_MODE, isDemo);
		intent.putExtra(EXTRA_CONFIRM_TYPE, userEnterType);
		return intent;
	}

	@Inject
	protected RestaurateurObeservableApi api;

	private Subscription mCheckQrSubscribtion;

	private String mQrData;

	private Subscription mValidateSubscribtion;

	private int mOutAnimation;


	@Override
	protected void startLoader() {
		clearErrors(true);

		if(BuildConfig.DEBUG && AndroidUtils.getDeviceId(this).equals(DEVICE_ID_GENYMOTION)) {
			// findTableForQr("http://www.riston.ru/wishes"); // mehico
			findTableForQr("http://m.2gis.ru/os/"); // mehico
			// findTableForQr("http://omnom.menu/qr/00e7232a4d9d2533e7fa503620c4431b"); // shashlikoff
			return;
		}

		final int tableNumber = mTable != null ? mTable.getInternalId() : 0;
		final String tableId = mTable != null ? mTable.getId() : null;
		OmnomQRCaptureActivity.start(this, tableNumber, tableId, REQUEST_CODE_SCAN_QR);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCheckQrSubscribtion);
		OmnomObservable.unsubscribe(mValidateSubscribtion);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mOutAnimation = intent.getIntExtra(EXTRA_ANIMATION_EXIT, -1);
	}

	@Override
	public void finish() {
		super.finish();
		if(mOutAnimation == EXTRA_ANIMATION_SLIDE_OUT_RIGHT) {
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		}
	}

	@Override
	protected void validate() {
		if(validateDemo()) {
			return;
		}
		if(TextUtils.isEmpty(mQrData)) {
			super.validate();
		} else if(mRestaurant == null || mTable == null) {
			clearErrors(true);
			findTableForQr(mQrData);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE_SCAN_QR) {
			if(resultCode == RESULT_OK) {
				loader.startProgressAnimation(10000, new Runnable() {
					@Override
					public void run() {
					}
				});
				mQrData = data.getExtras().getString(CaptureActivity.EXTRA_SCANNED_URI);
				findTableForQr(mQrData);
			} else {
				finish();
			}
		}
	}

	private void findTableForQr(final String mQrData) {
		final TableDataResponse[] table = new TableDataResponse[1];

		mValidateSubscribtion = AndroidObservable.bindActivity(this, ValidationObservable.validateSmart(this, mIsDemo)
		                                                                                 .map(OmnomObservable.getValidationFunc(this,
		                                                                                                                        mErrorHelper,
		                                                                                                                        mInternetErrorClickListener))
		                                                                                 .isEmpty())
		                                         .subscribe(new Action1<Boolean>() {
			                                         @Override
			                                         public void call(Boolean hasNoErrors) {
				                                         if(hasNoErrors) {
					                                         loadTable(mQrData);
				                                         } else {
					                                         reportMixPanel(table[0]);
					                                         startErrorTransition();
					                                         final View viewById = findViewById(R.id.panel_bottom);
					                                         if(viewById != null) {
						                                         viewById.animate().translationY(200).start();
					                                         }
				                                         }
			                                         }
		                                         }, new Action1<Throwable>() {
			                                         @Override
			                                         public void call(Throwable throwable) {
				                                         startErrorTransition();
				                                         mErrorHelper.showInternetError(mInternetErrorClickListener);
			                                         }
		                                         });

	}

	private void reportMixPanel(final TableDataResponse tableDataResponse) {
		if(tableDataResponse != null) {
			getMixPanelHelper().track(MixPanelHelper.Project.OMNOM,
									  OnTableMixpanelEvent.createEventQr(getUserData(), tableDataResponse.getRestaurantId(),
			                                                             tableDataResponse.getId()));
		}
	}

	private void loadTable(final String mQrData) {
		mCheckQrSubscribtion = AndroidObservable
				.bindActivity(this, api.decode(new QrDecodeRequest(mQrData), mPreloadBackgroundFunction)).subscribe(
						new Action1<RestaurantResponse>() {
							@Override
							public void call(final RestaurantResponse decodeResponse) {
								if(decodeResponse.hasAuthError()) {
									throw new AuthServiceException(EXTRA_ERROR_WRONG_USERNAME | EXTRA_ERROR_WRONG_PASSWORD,
									                               new AuthError(EXTRA_ERROR_AUTHTOKEN_EXPIRED,
									                                             decodeResponse.getError()));
								}
								if(!TextUtils.isEmpty(decodeResponse.getError())) {
									mErrorHelper.showError(LoaderError.UNKNOWN_QR_CODE, mInternetErrorClickListener);
								} else {
									handleDecodeResponse(decodeResponse);
								}
							}
						}, onError);
	}

	private void onWrongQr() {
		startErrorTransition();
		mErrorHelper.showWrongQrError(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				startLoader();
			}
		});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_validate;
	}
}