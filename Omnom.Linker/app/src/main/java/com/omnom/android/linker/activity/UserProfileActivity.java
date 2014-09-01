package com.omnom.android.linker.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.linker.LinkerApplication;
import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.base.OmnomActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.drawable.RoundTransformation;
import com.omnom.android.linker.model.UserProfile;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

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

	@Override
	public void initUi() {
		final UserProfile userProfile = LinkerApplication.get(getActivity()).getUserProfile();
		mTxtInfo.setText(userProfile.getInfo());
		mTxtLogin.setText(userProfile.getLogin());
		mTxtUsername.setText(userProfile.getUsername());
		int dimension = (int) getResources().getDimension(R.dimen.profile_avatar_size);
		Picasso.with(this).load(userProfile.getImageUrl()).placeholder(R.drawable.empty_avatar).resize(dimension, dimension).centerCrop().transform(
				RoundTransformation.create(dimension, 0)).into(mImgUser);
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
				AnimationUtils.scale(mImgUser, dimension, DURATION, new Runnable() {
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
