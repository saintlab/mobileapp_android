package com.omnom.android.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.view.ViewPagerIndicatorCircle;
import com.omnom.util.utils.AndroidUtils;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.utils.ViewUtils;
import com.omnom.util.view.ErrorEdit;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import rx.functions.Action1;

public class LoginActivity extends BaseOmnomActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();

	@InjectView(R.id.edit_phone)
	protected ErrorEdit editPhone;

	@InjectView(R.id.page_indicator)
	protected ViewPagerIndicatorCircle pageIndicator;

	@InjectView(R.id.btn_right)
	protected Button btnRight;

	@InjectView(R.id.title)
	protected TextView textTitle;

	@InjectViews({R.id.title, R.id.page_indicator, R.id.btn_right})
	protected List<View> topViews;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstStart = true;

	@Override
	public void initUi() {
		ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA_NOW, false);
		btnRight.setText(R.string.proceed);
		textTitle.setText(R.string.enter);
		pageIndicator.setFake(true, UserRegisterActivity.FAKE_PAGE_COUNT);
		pageIndicator.setCurrentItem(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(pageIndicator.getAlpha() == 0) {
			pageIndicator.postDelayed(new Runnable() {
				@Override
				public void run() {
					ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA, true);
					AndroidUtils.showKeyboard(editPhone.getEditText());
				}

			}, mFirstStart ? getResources().getInteger(android.R.integer.config_longAnimTime) :
					                          getResources().getInteger(android.R.integer.config_mediumAnimTime));
		}
		mFirstStart = false;
	}

	@Override
	protected void handleIntent(Intent intent) {
	}

	@OnClick(R.id.btn_right)
	public void doProceed(final View view) {
		if(!validate()) {
			return;
		}
		view.setEnabled(false);
		authenticator.authorizePhone(editPhone.getText(), StringUtils.EMPTY_STRING).subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse authResponse) {
				if(!authResponse.hasError()) {
					ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA, false);
					postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
						@Override
						public void run() {
							final Intent intent = new Intent(LoginActivity.this, ConfirmPhoneActivity.class);
							intent.putExtra(EXTRA_PHONE, editPhone.getText());
							intent.putExtra(EXTRA_CONFIRM_TYPE, ConfirmPhoneActivity.TYPE_LOGIN);
							startActivity(intent, R.anim.slide_in_right, R.anim.slide_out_left, false);
						}
					});
				} else {
					editPhone.setError(authResponse.getError().getMessage());
				}
				view.setEnabled(true);
				track(TAG + ":authorizePhone", authResponse);
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				view.setEnabled(true);
				Log.e(TAG + ":authorizePhone", "doProceed", throwable);
				track(TAG + ":authorizePhone", throwable);
			}
		});
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	private boolean validate() {
		// TODO: check
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login;
	}
}
