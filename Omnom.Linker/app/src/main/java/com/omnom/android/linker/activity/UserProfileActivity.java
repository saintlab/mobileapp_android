package com.omnom.android.linker.activity;

import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.activity.base.OmnomActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.drawable.RoundedDrawable;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.utils.ViewUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

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

	@Override
	public void initUi() {
		RoundedDrawable.setRoundedDrawable(mImgUser, BitmapFactory.decodeResource(getResources(), R.drawable.empty_avatar));
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
		postDelayed(350, new Runnable() {
			@Override
			public void run() {
				AnimationUtils.scaleHeight(mImgUser, dimension);
				AnimationUtils.scaleWidth(mImgUser, dimension, null, new Runnable() {
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
		AnimationUtils.scaleHeight(mImgUser, 0);
		AnimationUtils.scaleWidth(mImgUser, 0, null, new Runnable() {
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
