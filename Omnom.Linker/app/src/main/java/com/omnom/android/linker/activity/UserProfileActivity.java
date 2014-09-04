package com.omnom.android.linker.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.base.OmnomActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.drawable.RoundTransformation;
import com.omnom.android.linker.drawable.RoundedDrawable;
import com.omnom.android.linker.model.User;
import com.omnom.android.linker.model.UserProfile;
import com.omnom.android.linker.model.auth.AuthResponseBase;
import com.omnom.android.linker.observable.BaseErrorHandler;
import com.omnom.android.linker.observable.OmnomObservable;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.StringUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

import static com.omnom.android.linker.utils.AndroidUtils.showToast;
import static com.omnom.android.linker.utils.AndroidUtils.showToastLong;

public class UserProfileActivity extends BaseActivity {

	public static void start(OmnomActivity activity) {
		activity.startActivity(UserProfileActivity.class, false);
	}

	@InjectView(R.id.img_user)
	protected ImageView mImgUser;

	@InjectView(R.id.txt_username)
	protected TextView mTxtUsername;

	@InjectView(R.id.txt_login)
	protected TextView mTxtLogin;

	@InjectView(R.id.txt_info)
	protected TextView mTxtInfo;

	@InjectViews({R.id.txt_username, R.id.txt_login, R.id.txt_info})
	protected List<View> mTxtViews;

	@Inject
	protected LinkerObeservableApi api;

	private boolean mFirstRun = true;
	private int mAnimDuration;

	private Subscription profileSubscription;
	private Subscription logoutSubscription;

	@Override
	public void initUi() {
		mAnimDuration = getResources().getInteger(R.integer.user_profile_animation_duration);
		final UserProfile userProfile = LinkerApplication.get(getActivity()).getUserProfile();
		if(userProfile != null && userProfile.getUser() != null) {
			initUserData(userProfile.getUser(), userProfile.getImageUrl());
		} else {
			updateUserImage(StringUtils.EMPTY_STRING);
			final String token = getPreferences().getAuthToken(this);
			if(TextUtils.isEmpty(token)) {
				LoginActivity.start(this);
				return;
			}
			profileSubscription = AndroidObservable.bindActivity(this, api.getUserProfile(token)).subscribe(new Action1<UserProfile>() {
				@Override
				public void call(UserProfile userProfile) {
					LinkerApplication.get(getActivity()).cacheUserProfile(userProfile);
					initUserData(userProfile.getUser(), userProfile.getImageUrl());
				}
			}, new BaseErrorHandler(getActivity()) {
				@Override
				protected void onThrowable(Throwable throwable) {
					showToastLong(getActivity(), R.string.error_server_unavailable_please_try_again);
					finish();
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(profileSubscription);
		OmnomObservable.unsubscribe(logoutSubscription);
	}

	private void initUserData(User user, String imgUrl) {
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
			mImgUser.setImageDrawable(getPlaceholderDrawable(dimension));
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
	protected void onStart() {
		super.onStart();
		ButterKnife.apply(mTxtViews, ViewUtils.VISIBLITY2, false);
		if(mFirstRun) {
			mImgUser.getLayoutParams().width = 0;
			mImgUser.getLayoutParams().height = 0;
			mImgUser.requestLayout();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final int dimension = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);
		postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
			@Override
			public void run() {
				AnimationUtils.scale(mImgUser, dimension, mAnimDuration, new Runnable() {
					@Override
					public void run() {
						ButterKnife.apply(mTxtViews, ViewUtils.VISIBLITY_ALPHA, true);
					}
				});
			}
		});
	}

	@Override
	public void finish() {
		ButterKnife.apply(mTxtViews, ViewUtils.VISIBLITY_ALPHA, false);
		AnimationUtils.scaleHeight(mImgUser, 0, mAnimDuration);
		AnimationUtils.scaleWidth(mImgUser, 0, mAnimDuration, new Runnable() {
			@Override
			public void run() {
				UserProfileActivity.super.finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}

	@OnClick(R.id.btn_bottom)
	public void onLogout() {
		final String token = getPreferences().getAuthToken(this);
		logoutSubscription = AndroidObservable.bindActivity(this, api.logout(token)).subscribe(new Action1<AuthResponseBase>() {
			@Override
			public void call(AuthResponseBase authResponseBase) {
				if(!authResponseBase.isError()) {
					getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
					LoginActivity.start(getActivity(), null, EXTRA_ERROR_LOGOUT);
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
