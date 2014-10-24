package com.omnom.android.linker.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.UserProfileHelper;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.utils.drawable.RoundTransformation;
import com.omnom.android.utils.drawable.RoundedDrawable;
import com.omnom.android.linker.R;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.activity.BaseActivity;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
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

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;
import static com.omnom.android.utils.utils.AndroidUtils.showToastLong;

public class UserProfileActivity extends BaseActivity {
	public static final int ROTATION_VALUE = 180;
	public static final int TRANSLATION_Y = -100;
	private static final String TAG = UserProfileActivity.class.getSimpleName();

	public static void start(OmnomActivity activity) {
		start(activity, false);
	}

	public static void start(OmnomActivity activity, boolean animate) {
		final Intent intent = new Intent(activity.getActivity(), UserProfileActivity.class);
		intent.putExtra(Extras.EXTRA_ANIMATE, animate);
		activity.startActivity(intent, false);
	}

	@InjectView(R.id.img_user)
	protected ImageView mImgUser;

	@InjectView(R.id.txt_username)
	protected TextView mTxtUsername;

	@InjectView(R.id.txt_login)
	protected TextView mTxtLogin;

	@InjectView(R.id.txt_info)
	protected TextView mTxtInfo;

	@InjectView(R.id.panel_bottom)
	protected View mPanelBottom;

	@InjectView(R.id.btn_back)
	protected View mBtnBack;

	@InjectViews({R.id.txt_username, R.id.txt_login, R.id.txt_info})
	protected List<View> mTxtViews;

	@Inject
	protected RestaurateurObeservableApi api;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstRun = true;
	private int mAnimDuration;
	private boolean mAnimate;
	private Subscription profileSubscription;
	private Subscription logoutSubscription;

	@Override
	protected void handleIntent(Intent intent) {
		mAnimate = intent.getBooleanExtra(EXTRA_ANIMATE, false);
	}

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
			profileSubscription = AndroidObservable.bindActivity(this, authenticator.getUser(token)).subscribe(
					new Action1<UserResponse>() {
						@Override
						public void call(UserResponse response) {
							if(response.hasError() && UserProfileHelper.hasAuthError(response)) {
								getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
								ButterKnife.apply(mTxtViews, ViewUtils.VISIBLITY_ALPHA, false);
								AnimationUtils.animateAlpha(mPanelBottom, false);
								findById(getActivity(), R.id.btn_back).animate().rotation(ROTATION_VALUE).translationY(TRANSLATION_Y)
								                                      .start();
								AnimationUtils.scaleHeight(mImgUser, 0, mAnimDuration);
								AnimationUtils.scaleWidth(mImgUser, 0, mAnimDuration, new Runnable() {
									@Override
									public void run() {
										LoginActivity.start(getActivity());
									}
								});
								return;
							}
							UserProfile profile = new UserProfile(response);
							LinkerApplication.get(getActivity()).cacheUserProfile(profile);
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
			mImgUser.setBackground(placeholderDrawable);
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
	protected void onStart() {
		super.onStart();
		ButterKnife.apply(mTxtViews, ViewUtils.VISIBLITY2, false);
		if(mFirstRun) {
			if(mAnimate) {
				findById(this, R.id.btn_back).setTranslationY(TRANSLATION_Y);
				findById(this, R.id.btn_back).setRotation(ROTATION_VALUE);
			}
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
				if(mAnimate) {
					findById(getActivity(), R.id.btn_back).animate().rotation(0).translationY(0).start();
				}
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
		if(mAnimate) {
			findById(this, R.id.btn_back).animate().rotation(ROTATION_VALUE).translationY(TRANSLATION_Y).start();
		}
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
		logoutSubscription = AndroidObservable.bindActivity(this, authenticator.logout(token)).subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse authResponseBase) {
				if(!authResponseBase.hasError()) {
					getPreferences().setAuthToken(getActivity(), StringUtils.EMPTY_STRING);
					ButterKnife.apply(mTxtViews, ViewUtils.VISIBLITY_ALPHA, false);
					AnimationUtils.animateAlpha(mPanelBottom, false);
					findById(getActivity(), R.id.btn_back).animate().rotation(ROTATION_VALUE).translationY(TRANSLATION_Y).start();
					AnimationUtils.scaleHeight(mImgUser, 0, mAnimDuration);
					AnimationUtils.scaleWidth(mImgUser, 0, mAnimDuration, new Runnable() {
						@Override
						public void run() {
							LoginActivity.start(getActivity(), null, EXTRA_ERROR_LOGOUT);
						}
					});
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
