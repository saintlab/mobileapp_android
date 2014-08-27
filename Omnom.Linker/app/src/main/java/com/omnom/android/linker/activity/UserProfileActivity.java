package com.omnom.android.linker.activity;

import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.api.observable.LinkerObeservableApi;
import com.omnom.android.linker.drawable.RoundedDrawable;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

public class UserProfileActivity extends BaseActivity {
	@InjectView(R.id.img_user)
	protected ImageView mImgUser;

	@InjectView(R.id.txt_username)
	protected TextView mTxtUsername;

	@InjectView(R.id.txt_login)
	protected TextView mTxtLogin;

	@InjectView(R.id.txt_info)
	protected TextView mTxtInfo;

	@Inject
	protected LinkerObeservableApi api;

	@Override
	public void initUi() {
		RoundedDrawable.setRoundedDrawable(mImgUser, BitmapFactory.decodeResource(getResources(), R.drawable.empty_avatar));
	}

	@OnClick(R.id.btn_back)
	public void onBack() {
		onBackPressed();
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
