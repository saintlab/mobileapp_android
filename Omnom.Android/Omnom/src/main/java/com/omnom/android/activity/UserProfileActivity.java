package com.omnom.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthService;
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
import com.squareup.picasso.Picasso;

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

	public static void startSliding(OmnomActivity activity, final int tableNumber) {
		final Intent intent = new Intent(activity.getActivity(), UserProfileActivity.class);
		intent.putExtra(EXTRA_ANIMATE, false);
		intent.putExtra(EXTRA_TABLE_NUMBER, tableNumber);
		activity.startActivity(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, false);
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

	@InjectView(R.id.txt_table_number)
	protected TextView mTxtTableNumber;

	@Inject
	protected RestaurateurObeservableApi api;

	@Inject
	protected AuthService authenticator;

	private Subscription profileSubscription;

	private Subscription logoutSubscription;

	private int mTableNumber;

	@Override
	protected void handleIntent(Intent intent) {
		mTableNumber = intent.getIntExtra(EXTRA_TABLE_NUMBER, 0);
	}

	@OnClick(R.id.btn_feedback)
	protected void onFeedback() {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		String email = "team@omnom.menu";
		intent.setData(Uri.parse("mailto:" + email));
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
		intent.putExtra(Intent.EXTRA_SUBJECT, "Обратная связь");
		startActivity(Intent.createChooser(intent, "Написать отзыв"));
	}

	@Override
	public void initUi() {
		initAppInfo();

		mTxtTableNumber.setText(String.valueOf(mTableNumber));

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

	private void forwardToIntro() {EnteringActivity.start(this);}

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
			Picasso.with(this).load(url).placeholder(getPlaceholderDrawable(dimension))
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
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@OnClick(R.id.btn_bottom)
	public void onLogout() {
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