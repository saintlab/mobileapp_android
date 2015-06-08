package com.omnom.android.activity;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.UserRecoverPhoneRequest;
import com.omnom.android.auth.response.AuthResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ErrorUtils;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.view.HeaderView;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.functions.Action1;

public class ChangePhoneActivity extends BaseOmnomActivity {

	public static final int ERROR_AUTH_UNKNOWN_USER = 101;

	private static final String TAG = ChangePhoneActivity.class.getSimpleName();

	private static final int CHANGE_PHONE_SUCCESS_REQUEST = 100;

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@InjectView(R.id.edit_phone)
	protected ErrorEdit editPhone;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstStart = true;

	@Override
	public void initUi() {
		topPanel.setTitleBig(R.string.change_phone_title)
		        .setButtonLeftDrawable(R.drawable.ic_action_previous_item, new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
				        finish();
			        }
		        })
		        .setButtonRight(R.string.proceed, new View.OnClickListener() {
			        @Override
			        public void onClick(View v) {
				        doProceed();
			        }
		        });

		editPhone.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					doProceed();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(mFirstStart) {
			postDelayed(getResources().getInteger(android.R.integer.config_longAnimTime) + 200, new Runnable() {
				@Override
				public void run() {
					final EditText editText = editPhone.getEditText();
					editText.setText(getString(R.string.phone_country_code));
					AndroidUtils.moveCursorEnd(editText);
					AndroidUtils.showKeyboard(editText);
				}
			});
		}

		if(topPanel.isAlphaVisible()) {
			topPanel.postDelayed(new Runnable() {
				@Override
				public void run() {
					topPanel.setContentVisibility(true, false);
				}

			}, mFirstStart ? getResources().getInteger(android.R.integer.config_longAnimTime) :
					                     getResources().getInteger(android.R.integer.config_mediumAnimTime));
		}
		mFirstStart = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		AndroidUtils.hideKeyboard(this);
	}

	public void doProceed() {
		if(isBusy()) {
			return;
		}
		busy(true);
		topPanel.showProgress(true);
		subscribe(authenticator.changePhone(new UserRecoverPhoneRequest(editPhone.getText())),
		          new Action1<AuthResponse>() {
			          @Override
			          public void call(AuthResponse authResponse) {
				          if(!authResponse.hasError()) {
					          topPanel.setContentVisibility(false, false);
					          postDelayed(getResources().getInteger(
							          R.integer.default_animation_duration_short), new Runnable() {
						          @Override
						          public void run() {
							          final Intent intent = new Intent(ChangePhoneActivity.this,
							                                           ChangePhoneSuccessActivity.class);
							          startForResult(intent, R.anim.slide_in_right, R.anim.slide_out_left,
							                         CHANGE_PHONE_SUCCESS_REQUEST);
						          }
					          });
				          } else {
					          final AuthError error = authResponse.getError();
					          if(error != null) {
						          editPhone.setError(error.getMessage());
					          }
					          topPanel.showProgress(false);
					          busy(false);
				          }
			          }
		          }, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
					@Override
					public void onError(Throwable throwable) {
						Log.e(TAG, ":changePhone doProceed ", throwable);
						if(ErrorUtils.isConnectionError(throwable)) {
							showError(getString(R.string.err_no_internet));
						} else {
							showError(getString(R.string.something_went_wrong));
						}
					}
				});
	}

	private void showError(final String message) {
		editPhone.setError(message);
		topPanel.showProgress(false);
		busy(false);
	}

	@Override
	protected void onStop() {
		super.onStop();
		topPanel.showProgress(false);
		busy(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == CHANGE_PHONE_SUCCESS_REQUEST && resultCode == RESULT_OK) {
			finish();
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_change_phone;
	}
}
