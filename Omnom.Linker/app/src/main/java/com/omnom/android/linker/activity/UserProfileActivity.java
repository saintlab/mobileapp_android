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
import com.omnom.android.linker.model.UserProfile;
import com.omnom.android.linker.observable.BaseErrorHandler;
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

import static com.omnom.android.linker.utils.AndroidUtils.showToastLong;

public class UserProfileActivity extends BaseActivity {
	public static final long DURATION = 500;

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

	private Subscription profileObservable;

	@Override
	public void initUi() {
		RoundedDrawable.setRoundedDrawable(mImgUser, BitmapFactory.decodeResource(getResources(), R.drawable.empty_avatar));
		final UserProfile userProfile = LinkerApplication.get(getActivity()).getUserProfile();
		if(userProfile != null) {
			initUserData(userProfile);
		} else {
			final String token = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).getString(AUTH_TOKEN, StringUtils.EMPTY_STRING);
			if(TextUtils.isEmpty(token)) {
				LoginActivity.start(this);
				return;
			}
			profileObservable = AndroidObservable.bindActivity(this, api.getUserProfile(token)).subscribe(new Action1<UserProfile>() {
				@Override
				public void call(UserProfile userProfile) {
					LinkerApplication.get(getActivity()).cacheUserProfile(userProfile);
					initUserData(userProfile);
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
		OmnomObservable.unsubscribe(profileObservable);
	}

	private void initUserData(UserProfile userProfile) {
		mTxtInfo.setText(userProfile.getUser().getPhone());
		mTxtLogin.setText(userProfile.getUser().getEmail());
		mTxtUsername.setText(userProfile.getUser().getName());
		int dimension = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);
		final Bitmap placeholderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_avatar);
		final RoundedDrawable placeholder = new RoundedDrawable(placeholderBmp, dimension, 0);
		Picasso.with(this).load(userProfile.getImageUrl()).placeholder(placeholder)
		       .resize(dimension, dimension).centerCrop()
		       .transform(RoundTransformation.create(dimension, 0)).into(mImgUser);
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
		final int dimension = (int) getResources().getDimension(R.dimen.profile_avatar_size);
		postDelayed(AnimationUtils.DURATION_SHORT, new Runnable() {
			@Override
			public void run() {
				AnimationUtils.scaleHeight(mImgUser, dimension, DURATION);
				AnimationUtils.scaleWidth(mImgUser, dimension, DURATION, new Runnable() {
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
		AnimationUtils.scaleHeight(mImgUser, 0, DURATION);
		AnimationUtils.scaleWidth(mImgUser, 0, DURATION, new Runnable() {
			@Override
			public void run() {
				UserProfileActivity.super.finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}

	@OnClick(R.id.btn_bottom)
	public void onLogout() {
		// TODO:
		// api.logout();
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_user_profile;
	}
}
