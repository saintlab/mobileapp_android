package com.omnom.android.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.UserProfileHelper;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.fragment.ChangeTableFragment;
import com.omnom.android.notifier.api.observable.NotifierObservableApi;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.SupportInfoResponse;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.drawable.RoundTransformation;
import com.omnom.android.utils.drawable.RoundedDrawable;
import com.omnom.android.utils.observable.BaseErrorHandler;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.DialogUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static butterknife.ButterKnife.findById;
import static com.omnom.android.utils.utils.AndroidUtils.showToast;
import static com.omnom.android.utils.utils.AndroidUtils.showToastLong;

public class UserProfileActivity extends BaseOmnomFragmentActivity {

	private static final String TAG = UserProfileActivity.class.getSimpleName();

	public static void startSliding(OmnomActivity activity, final int tableNumber, final String tableId) {
		final Intent intent = new Intent(activity.getActivity(), UserProfileActivity.class);
		intent.putExtra(EXTRA_ANIMATE, false);
		intent.putExtra(EXTRA_TABLE_NUMBER, tableNumber);
		intent.putExtra(EXTRA_TABLE_ID, tableId);
		activity.startForResult(intent, R.anim.slide_in_up, R.anim.fake_fade_out_long, REQUEST_CODE_CHANGE_TABLE);
	}

	@InjectView(R.id.img_user)
	protected ImageView mImgUser;

	@InjectView(R.id.txt_username)
	protected TextView mTxtUsername;

	@InjectView(R.id.txt_login)
	protected TextView mTxtLogin;

	@InjectView(R.id.txt_sign_in)
	protected TextView mTxtSignIn;

	@InjectView(R.id.txt_edit)
	protected TextView mTxtEdit;

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

	@InjectViews({R.id.img_user, R.id.txt_username, R.id.txt_login, R.id.txt_info,
			R.id.delimiter_cards_top, R.id.delimiter_cards_bottom, R.id.btn_my_cards, R.id.btn_bottom, R.id.delimiter_logout})
	protected List<View> mUserViews;

	@InjectView(R.id.dark_transparent_background)
	protected FrameLayout darkTransparentBackground;

	@Inject
	protected AuthService authenticator;

	@Inject
	protected RestaurateurObservableApi api;

	@Inject
	protected NotifierObservableApi notifierApi;

	private int mTableNumber;

	private String mTableId;

	@Nullable
	private String supportPhone;

	private int mAvatarSize;

	@Override
	protected void handleIntent(Intent intent) {
		mTableNumber = intent.getIntExtra(EXTRA_TABLE_NUMBER, 0);
		mTableId = intent.getStringExtra(EXTRA_TABLE_ID);
	}

	@OnClick(R.id.btn_my_cards)
	protected void onMyCards() {
		CardsActivity.start(this, null, mTableId);
	}

	@OnClick(R.id.btn_support)
	protected void onSupport() {
		if(!TextUtils.isEmpty(supportPhone)) {
			AndroidUtils.openDialer(this, supportPhone);
		} else {
			showToastLong(this, R.string.support_unavailable);
		}
	}

	@OnClick(R.id.btn_feedback)
	protected void onFeedback() {
		AndroidUtils.sendFeedbackEmail(this, R.string.send_feedback, com.omnom.android.utils.R.string.email_subject_feedback);
	}

	@OnClick(R.id.txt_sign_in)
	protected void onSignIn() {
		LoginActivity.start(this, AndroidUtils.getDevicePhoneNumber(this, R.string.phone_country_code), REQUEST_CODE_LOGIN);
	}

	@OnClick(R.id.btn_facebook)
	protected void onFacebook() {
		try {
			Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_url_fb)));
			startActivity(facebookIntent);
		} catch(ActivityNotFoundException e) {
			Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.facebook_url_http)));
			startActivity(facebookIntent);
		}
	}

	@Override
	public void initUi() {
		mAvatarSize = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);

		initAppInfo();

		if(mTableNumber > 0) {
			ViewUtils.setVisibleGone(panelTableNumber, true);
			ViewUtils.setVisibleGone(delimiterTableNumber, true);
			mTxtTableNumber.setText(getString(R.string.table_number_format, String.valueOf(mTableNumber)));
		} else {
			ViewUtils.setVisibleGone(panelTableNumber, false);
			ViewUtils.setVisibleGone(delimiterTableNumber, false);
		}

		final OmnomApplication app = getApp();
		final String token = app.getAuthToken();
		showUserData(!TextUtils.isEmpty(token));

		// pre-init with cached data
		final UserData userData = getUserData();
		if(userData != null) {
			initUserData(userData);
		} else {
			subscribe(getProfileObservable(token),
			          new Action1<Pair<UserResponse, SupportInfoResponse>>() {
				          @Override
				          public void call(Pair<UserResponse, SupportInfoResponse> response) {
					          final UserResponse userResponse = response.first;
					          if(userResponse.hasError() && UserProfileHelper.hasAuthError(userResponse)) {
						          app.logout();
						          forwardToIntro();
						          return;
					          }
					          UserProfile profile = new UserProfile(userResponse);
					          app.cacheUserProfile(profile);

					          postDelayed(getResources().getInteger(android.R.integer.config_longAnimTime), new Runnable() {
						          @Override
						          public void run() {
							          initUserData(userResponse.getUser());
						          }
					          });

					          final SupportInfoResponse supportInfoResponse = response.second;
					          if(!supportInfoResponse.hasErrors()) {
						          supportPhone = supportInfoResponse.getPhone();
					          }
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

	@DebugLog
	private void showUserData(final boolean visible) {
		ViewUtils.setVisibleGone(mTxtSignIn, !visible);
		ViewUtils.setVisibleGone(mTxtEdit, visible);
		ButterKnife.apply(mUserViews, ViewUtils.VISIBLITY, visible);
	}

	private Observable<Pair<UserResponse, SupportInfoResponse>> getProfileObservable(final String token) {
		return Observable.zip(authenticator.getUser(token), api.getSupportInfo().onErrorReturn(
				                      new Func1<Throwable, SupportInfoResponse>() {
					                      @Override
					                      public SupportInfoResponse call(final Throwable throwable) {
						                      return SupportInfoResponse.NULL;
					                      }
				                      }),
		                      new Func2<UserResponse, SupportInfoResponse, Pair<UserResponse, SupportInfoResponse>>() {
			                      @Override
			                      public Pair<UserResponse, SupportInfoResponse> call(final UserResponse userResponse,
			                                                                          final SupportInfoResponse supportInfoResponse) {
				                      return new Pair<UserResponse, SupportInfoResponse>(userResponse, supportInfoResponse);
			                      }
		                      }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
	}

	private void initAppInfo() {
		mTxtAppInfo.setText(getString(R.string.app_version_build, AndroidUtils.getAppVersion(this)));
	}

	private void forwardToIntro() {
		EnteringActivity.start(this, true);
	}

	private void initUserData(UserData user) {
		if(user == null) {
			showToast(this, R.string.error_user_not_found);
			finish();
			return;
		}

		AnimationUtils.animateAlpha(mTxtInfo, false);
		AnimationUtils.animateAlpha(mTxtLogin, false);
		AnimationUtils.animateAlpha(mTxtUsername, false);

		mTxtInfo.setText(user.getPhone());
		mTxtLogin.setText(user.getEmail());
		mTxtUsername.setText(user.getName());
		updateUserImage(user.getAvatar());

		smartAnimateText(mTxtInfo);
		smartAnimateText(mTxtLogin);
		smartAnimateText(mTxtUsername);
	}

	/**
	 * Animate textView alpha is TextView has some text, otherwise set it's visibility to {@link android.view.View#GONE}
	 */
	private void smartAnimateText(final TextView txtView) {
		final boolean hasText = !TextUtils.isEmpty(txtView.getText());
		if(hasText) {
			AnimationUtils.animateAlpha(txtView, hasText);
		} else {
			ViewUtils.setVisibleGone(txtView, hasText);
		}
	}

	private void updateUserImage(String url) {
		final int dimension = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);
		final RoundedDrawable placeholderDrawable = getPlaceholderDrawable(dimension);
		if(TextUtils.isEmpty(url)) {
			AndroidUtils.setBackground(mImgUser, placeholderDrawable);
			mImgUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defolt_user));
			final int padding = ViewUtils.dipToPixels(this, 24);
			mImgUser.setPadding(padding, padding, padding, padding);
		} else {
			AndroidUtils.setBackground(mImgUser, null);
			mImgUser.setPadding(0, 0, 0, 0);
			OmnomApplication.getPicasso(this).load(url)
			                .placeholder(placeholderDrawable)
			                .resize(dimension, dimension).centerCrop()
			                .transform(RoundTransformation.create(dimension, 0))
			                .into(mImgUser);
		}
	}

	private RoundedDrawable getPlaceholderDrawable(int dimension) {
		final Bitmap placeholderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_avatar);
		return new RoundedDrawable(placeholderBmp, dimension, 0);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		busy(false);
		if(requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
			initUserData(getApp().getUserProfile().getUser());
			showUserData(true);
		}
		if(resultCode == RESULT_OK && requestCode == REQUEST_CODE_USER_PROFILE_EDIT) {
			final Picasso picasso = OmnomApplication.getPicasso(this);
			final String avaPath = data.getStringExtra(EXTRA_USER_AVATAR);
			if(!TextUtils.isEmpty(avaPath)) {
				mImgUser.setPadding(0, 0, 0, 0);
				RoundedDrawable.setScaledRoundedDrawable(mImgUser, BitmapFactory.decodeFile(avaPath), mAvatarSize);
			}

			final UserData userData = data.getParcelableExtra(EXTRA_USER_DATA);
			if(userData != null) {
				final UserProfile userProfile = new UserProfile(new UserResponse(userData, StringUtils.EMPTY_STRING));
				OmnomApplication.get(getActivity()).cacheUserProfile(userProfile);
				initUserData(userData);

				// load into the cache
				final String avatar = userData.getAvatar();
				if(!TextUtils.isEmpty(avatar)) {
					picasso.load(avatar).fetch();
				} else {
					updateUserImage(null);
				}
			}
		}
	}

	@OnClick(R.id.btn_back)
	public void onBack() {
		onBackPressed();
	}

	@OnClick(R.id.img_user)
	protected void onAvatar() {
		if(!isBusy()) {
			busy(true);
			UserProfileEditActivity.start(this, UserProfileEditActivity.FLAG_CHANGE_AVATAR);
		}
	}

	@OnClick(R.id.txt_edit)
	public void onEditProfile() {
		UserProfileEditActivity.start(this);
	}

	@Override
	public void onBackPressed() {
		if(getSupportFragmentManager().getBackStackEntryCount() != 0) {
			AnimationUtils.animateAlpha(darkTransparentBackground, false);
		}
		super.onBackPressed();
	}

	@Override
	public void finish() {
		UserProfileActivity.super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@OnClick(R.id.panel_table_number)
	public void onChangeTable() {
		getSupportFragmentManager()
				.beginTransaction()
				.addToBackStack(null)
				.setCustomAnimations(
						R.anim.slide_in_up,
						R.anim.slide_out_down,
						R.anim.slide_in_up,
						R.anim.slide_out_down)
				.replace(R.id.fragment_container, ChangeTableFragment.newInstance(mTableNumber))
				.commit();
		AnimationUtils.animateAlpha(darkTransparentBackground, true);
	}

	public void changeTable() {
		setResult(RESULT_CODE_TABLE_CHANGED);
		UserProfileActivity.super.finish();
		overridePendingTransition(R.anim.fake_fade_in, R.anim.slide_out_down);
		EnteringActivity.startNewTable(this);
	}

	@OnClick(R.id.btn_bottom)
	public void onLogout() {
		final AlertDialog alertDialog = DialogUtils.showDialog(this, R.string.are_you_to_quit,
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
		final Observable logoutObservable = notifierApi.unregister().flatMap(new Func1<Object, Observable<AuthResponse>>() {
			@Override
			public Observable<AuthResponse> call(final Object o) {
				return authenticator.logout(token);
			}
		});
		subscribe(logoutObservable,
		          new Action1<AuthResponse>() {
			          @Override
			          public void call(AuthResponse authResponseBase) {
				          if(!authResponseBase.hasError()) {
					          ((OmnomApplication) getApplication()).clearUserData();
					          showUserData(false);
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
		// TODO: unregister from push notifier
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_user_profile;
	}
}