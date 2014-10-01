package com.omnom.android.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.view.LoginPanelTop;
import com.omnom.util.activity.BaseActivity;
import com.omnom.util.utils.AndroidUtils;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.view.ErrorEdit;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.functions.Action1;

public class LoginActivity extends BaseActivity {

	private static final String TAG = LoginActivity.class.getSimpleName();

	@InjectView(R.id.edit_phone)
	protected ErrorEdit editPhone;

	@InjectView(R.id.panel_top)
	protected LoginPanelTop topPanel;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstStart = true;

	@Override
	public void initUi() {
		topPanel.setContentVisibility(false, true);
		topPanel.setTitle(R.string.enter);
		topPanel.setButtonRight(R.string.proceed, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doProceed(v);
			}
		});
		topPanel.setPaging(UserRegisterActivity.FAKE_PAGE_COUNT, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(topPanel.isAlphaVisible()) {
			topPanel.postDelayed(new Runnable() {
				@Override
				public void run() {
					topPanel.setContentVisibility(true, false);
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

	public void doProceed(final View view) {
		if(!validate()) {
			return;
		}
		view.setEnabled(false);
		authenticator.authorizePhone(editPhone.getText(), StringUtils.EMPTY_STRING).subscribe(new Action1<AuthResponse>() {
			@Override
			public void call(AuthResponse authResponse) {
				if(!authResponse.hasError()) {
					topPanel.setContentVisibility(false, false);
					postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
						@Override
						public void run() {
							final Intent intent = new Intent(LoginActivity.this, ConfirmPhoneActivity.class);
							intent.putExtra(EXTRA_PHONE, editPhone.getText());
							intent.putExtra(EXTRA_CONFIRM_TYPE, ConfirmPhoneActivity.TYPE_LOGIN);
							startActivity(intent, R.anim.slide_in_right, R.anim.slide_out_left, false);
							view.setEnabled(true);
						}
					});
				} else {
					editPhone.setError(authResponse.getError().getMessage());
					view.setEnabled(true);
				}
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				view.setEnabled(true);
				Log.e(TAG, "doProceed", throwable);
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
