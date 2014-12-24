package com.omnom.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.UserProfileHelper;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.drawable.RoundTransformation;
import com.omnom.android.utils.drawable.RoundedDrawable;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;
import static com.omnom.android.utils.utils.AndroidUtils.showToastLong;

public class UserProfileActivity extends BaseOmnomActivity {

	private static final String TAG = UserProfileActivity.class.getSimpleName();

	public static void startSliding(OmnomActivity activity, final int tableNumber, final String tableId) {
		final Intent intent = new Intent(activity.getActivity(), UserProfileActivity.class);
		intent.putExtra(EXTRA_ANIMATE, false);
		intent.putExtra(EXTRA_TABLE_NUMBER, tableNumber);
		intent.putExtra(EXTRA_TABLE_ID, tableId);
		activity.start(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, false);
	}

	@InjectView(R.id.img_user)
	protected ImageView mImgUser;

	@InjectView(R.id.txt_username)
	protected TextView mTxtUsername;

	@InjectView(R.id.txt_login)
	protected TextView mTxtLogin;

	@InjectView(R.id.txt_info)
	protected TextView mTxtInfo;

	@InjectView(R.id.txt_app_info)
	protected TextView mTxtAppInfo;

	@InjectView(R.id.panel_table_number)
	protected View panelTableNumber;

	@InjectView(R.id.delimiter_table_number)
	protected View delimiterTableNumber;

	@InjectView(R.id.txt_table_number)
	protected TextView mTxtTableNumber;

	@Inject
	protected RestaurateurObeservableApi api;

	private Subscription profileSubscription;

	private Subscription logoutSubscription;

	private int mTableNumber;

	private String mTableId;

	@Override
	protected void handleIntent(Intent intent) {
		mTableNumber = intent.getIntExtra(EXTRA_TABLE_NUMBER, 0);
		mTableId = intent.getStringExtra(EXTRA_TABLE_ID);
	}

	@OnClick(R.id.btn_my_cards)
	protected void onMyCards() {
		final Intent intent = new Intent(this, CardsActivity.class);
		intent.putExtra(EXTRA_TABLE_ID, mTableId);
		startActivity(intent);
	}

	@OnClick(R.id.btn_feedback)
	protected void onFeedback() {
		AndroidUtils.sendFeedbackEmail(this, R.string.send_feedback);
	}

	@Override
	public void initUi() {
		initAppInfo();

		if(mTableNumber > 0) {
			ViewUtils.setVisible(panelTableNumber, true);
			ViewUtils.setVisible(delimiterTableNumber, true);
			mTxtTableNumber.setText(String.valueOf(mTableNumber));
		} else {
			ViewUtils.setVisible(panelTableNumber, false);
			ViewUtils.setVisible(delimiterTableNumber, false);
		}

		final UserProfile userProfile = OmnomApplication.get(getActivity()).getUserProfile();
		if(userProfile != null && userProfile.getUser() != null) {
			initUserData(userProfile.getUser(), userProfile.getImageUrl());
		} else {
			updateUserImage(StringUtils.EMPTY_STRING);
			final String token = getPreferences().getAuthToken(this);
			if(TextUtils.isEmpty(token)) {
				forwardToIntro();
				return;
			}
			profileSubscription = AndroidObservable.bindActivity(this, authenticator.getUser(token)).subscribe(
					new Action1<UserResponse>() {
						@Override
						public void call(UserResponse response) {
							if(response.hasError() && UserProfileHelper.hasAuthError(response)) {
								getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
								forwardToIntro();
								return;
							}
							UserProfile profile = new UserProfile(response);
							OmnomApplication.get(getActivity()).cacheUserProfile(profile);
							initUserData(response.getUser(), profile.getImageUrl());
						}
					}, new BaseErrorHandler(getActivity()) {
						@Override
						protected void onTokenExpired() {

						}

						@Override
						protected void onThrowable(Throwable throwable) {
							showToastLong(getActivity(), R.string.error_server_unavailable_please_try_again);
							Log.e(TAG, "getUserProfile", throwable);
							finish();
						}
					});
		}
	}

	private void initAppInfo() {
		mTxtAppInfo.setText(getString(R.string.app_version_build, AndroidUtils.getAppVersion(this)));
	}

	private void forwardToIntro() {
		EnteringActivity.start(this, true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(profileSubscription);
		OmnomObservable.unsubscribe(logoutSubscription);
	}

	private void initUserData(UserData user, String imgUrl) {
		if(user == null) {
			showToast(this, R.string.error_user_not_found);
			finish();
			return;
		}
		mTxtInfo.setText(user.getPhone());
		mTxtLogin.setText(user.getEmail());
		mTxtUsername.setText(user.getName());
		updateUserImage(imgUrl);
	}

	private void updateUserImage(String url) {
		final int dimension = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);
		if(TextUtils.isEmpty(url)) {
			final RoundedDrawable placeholderDrawable = getPlaceholderDrawable(dimension);
			mImgUser.setBackgroundDrawable(placeholderDrawable);
			mImgUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defolt_user));
			final int padding = ViewUtils.dipToPixels(this, 24);
			mImgUser.setPadding(padding, padding, padding, padding);
		} else {
			OmnomApplication.getPicasso(this).load(url).placeholder(getPlaceholderDrawable(dimension))
			       .resize(dimension, dimension).centerCrop()
			       .transform(RoundTransformation.create(dimension, 0)).into(mImgUser);
		}
	}

	private RoundedDrawable getPlaceholderDrawable(int dimension) {
		final Bitmap placeholderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_avatar);
		return new RoundedDrawable(placeholderBmp, dimension, 0);
	}

	@OnClick(R.id.btn_back)
	public void onBack() {
		onBackPressed();
	}

	@Override
	public void finish() {
		UserProfileActivity.super.finish();
		overridePendingTransition(R.anim.fake_fade_out_short, R.anim.slide_out_down);
	}

	@OnClick(R.id.btn_bottom)
	public void onLogout() {
		final AlertDialog alertDialog = AndroidUtils.showDialog(this, R.string.are_you_to_quit,
		                                                        R.string.quit, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						quit();
					}
				}, R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						dialog.dismiss();
					}
				});
		alertDialog.setCanceledOnTouchOutside(true);
		final float btnTextSize = getResources().getDimension(R.dimen.font_normal);
		final Button btn1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		btn1.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
		final Button btn2 = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		btn2.setTextSize(TypedValue.COMPLEX_UNIT_PX, btnTextSize);
	}

	private void quit() {
		final String token = getPreferences().getAuthToken(this);
		logoutSubscription = AndroidObservable.bindActivity(this, authenticator.logout(token)).subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse authResponseBase) {
				if(!authResponseBase.hasError()) {
					getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
					forwardToIntro();
				} else {
					showToast(getActivity(), R.string.error_unknown_server_error);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				showToast(getActivity(), R.string.error_unknown_server_error);
			}
		});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_user_profile;
	}
}