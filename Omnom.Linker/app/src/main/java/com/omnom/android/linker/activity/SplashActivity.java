package com.omnom.android.linker.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AnimationUtils;
import com.omnom.android.linker.widget.LoaderView;

import org.jetbrains.annotations.Nullable;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.omnom.android.linker.utils.AnimationUtils.DURATION_LONG;

public class SplashActivity extends Activity implements View.OnClickListener {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ButterKnife.inject(this);
	}

	public void performLogin() {
		findViewById(R.id.view_login_root).setVisibility(View.GONE);
		viewLoader.performLogin(new LoaderView.Callback() {
			@Override
			public void execute() {
				startValidationActivity();
			}
		});
	}

	private void startValidationActivity() {
		final Intent intent = new Intent(this, ValidationActivity.class);
		if (Build.VERSION.SDK_INT >= 16) {
			ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
			startActivity(intent, activityOptions.toBundle());
			finish();
		} else {
			finish();
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(!mAnimated) {
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
}