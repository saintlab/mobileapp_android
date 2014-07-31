package com.omnom.android.linker.activity;

import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.activity.base.BaseActivity;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.widget.LoaderView;

import org.jetbrains.annotations.Nullable;

import butterknife.InjectView;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

	@InjectView(R.id.img_logo_left)
	protected ImageView imgLogoLeft;

	@InjectView(R.id.img_logo_right)
	protected ImageView imgLogoRight;

	@InjectView(R.id.stub_login)
	protected ViewStub viewStub;

	@InjectView(R.id.loader)
	protected LoaderView viewLoader;

	@Nullable
	private Button btnLogin;

	private boolean mAnimated;

	public void performLogin() {
		findViewById(R.id.view_login_root).setVisibility(View.GONE);
		viewLoader.performLogin(new LoaderView.Callback() {
			@Override
			public void execute() {
				startActivity(ValidationActivity.class);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mAnimated) {
			startAnimation();
		}
	}

	private void startAnimation() {
		mAnimated = true;
		viewLoader.post(new Runnable() {
			@Override
			public void run() {
				viewLoader.showProgress(true);
				viewLoader.scaleDown(new LoaderView.Callback() {
					@Override
					public void execute() {
						showLoginForm();
					}
				}, new LoaderView.Callback() {
					@Override
					public void execute() {
						AnimationUtils.animateAlpha(imgLogoLeft, false);
						AnimationUtils.animateAlpha(imgLogoRight, false);
					}
				});
			}
		});
	}

	private void showLoginForm() {
		viewStub.setVisibility(View.VISIBLE);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_login:
				performLogin();
				break;
		}
	}

	@Override
	public void initUi() {

	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_splash;
	}
}