package com.omnom.android.activity;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;
import com.omnom.android.utils.utils.ErrorUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.utils.view.ErrorEditText;
import com.omnom.android.view.HeaderView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class UserRegisterActivity extends BaseOmnomActivity {

	public static final int START_YEAR = 1900;

	public static final int YEAR_OFFSET = 30;

	public static final String DELIMITER_DATE_UI = "/";

	public static final String DELIMITER_DATE_WICKET = "-";

	public static final int FAKE_PAGE_COUNT = 2;

	private static final String TAG = UserRegisterActivity.class.getSimpleName();

	@InjectView(R.id.edit_name)
	protected ErrorEdit editName;

	@InjectView(R.id.edit_email)
	protected ErrorEdit editEmail;

	@InjectView(R.id.edit_phone)
	protected ErrorEdit editPhone;

	@InjectView(R.id.edit_birth)
	protected ErrorEditText editBirth;

	@InjectView(R.id.text_agreement)
	protected TextView textAgreement;

	@InjectView(R.id.text_error)
	protected TextView textError;

	@InjectView(R.id.panel_top)
	protected HeaderView topPanel;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstStart = true;

	private GregorianCalendar gc;

	private Subscription mRegisterSubscription;

	@Override
	protected void handleIntent(final Intent intent) {
		final String stringExtra = intent.getStringExtra(EXTRA_PHONE);
		if (!TextUtils.isEmpty(stringExtra)) {
			editPhone.setText(stringExtra);
			editPhone.getEditText().setSelection(editPhone.getText().length());
		}
	}

	@Override
	public void initUi() {
		gc = new GregorianCalendar();
		gc.add(Calendar.YEAR, -YEAR_OFFSET);

		topPanel.setTitle(R.string.create_account);
		topPanel.setContentVisibility(false, true);
		topPanel.setButtonRight(R.string.proceed, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doRegister(v);
			}
		});
		topPanel.setPaging(FAKE_PAGE_COUNT, 0);

		editPhone.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if (hasFocus && TextUtils.isEmpty(editPhone.getText())) {
					final String value = AndroidUtils.getDevicePhoneNumber(getActivity(), R.string.phone_country_code);
					editPhone.setText(value);
					AndroidUtils.moveCursorEnd(editPhone);
				}
			}
		});

		editBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if (hasFocus) {
					showDatePickerDialog();
				}
			}
		});

		editPhone.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					editBirth.requestFocus();
					return true;
				}
				return false;
			}
		});

		editBirth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (editBirth.isFocused()) {
					showDatePickerDialog();
				}
			}
		});

		AndroidUtils.clickify(textAgreement, getString(R.string.register_agreement_mark), new ClickSpan.OnClickListener() {
			@Override
			public void onClick() {
				final Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(getString(R.string.register_agreement_url)));
				try {
					startActivity(i);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getActivity(), getString(R.string.register_agreement_fail), Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private void showDatePickerDialog() {
		AndroidUtils.hideKeyboard(editBirth);
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			}
		}, gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
        Calendar minPickerDate = Calendar.getInstance();
        minPickerDate.set(Calendar.YEAR, START_YEAR);
        dialog.getDatePicker().setMinDate(minPickerDate.getTimeInMillis());
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DatePickerDialog dlg = (DatePickerDialog) dialog;
				gc.set(dlg.getDatePicker().getYear(), dlg.getDatePicker().getMonth(), dlg.getDatePicker().getDayOfMonth());
				CharSequence dateFormatted = DateFormat.format("dd/MM/yyyy", gc);
				editBirth.setText(dateFormatted);
				dialog.dismiss();
			}
		});
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mFirstStart) {
			postDelayed(getResources().getInteger(android.R.integer.config_longAnimTime) + 200, new Runnable() {
				@Override
				public void run() {
					AndroidUtils.showKeyboard(editName.getEditText());
				}
			});
		}

		if (topPanel.isAlphaVisible()) {
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

	@OnClick(R.id.btn_right)
	public void doRegister(final View view) {
		textError.setText(StringUtils.EMPTY_STRING);

		if (!validate()) {
			return;
		}

		AndroidUtils.hideKeyboard(getActivity());
		topPanel.showProgress(true);
		final AuthRegisterRequest request = AuthRegisterRequest
				.create(AndroidUtils.getInstallId(this), editName.getText(), StringUtils.EMPTY_STRING, editEmail.getText(),
				        editPhone.getText(), editBirth.getText().toString().replace(DELIMITER_DATE_UI, DELIMITER_DATE_WICKET));
		mRegisterSubscription =
				AppObservable.bindActivity(this, authenticator.register(request)).subscribe(new Action1<AuthRegisterResponse>() {
					@Override
					public void call(final AuthRegisterResponse authRegisterResponse) {
						if (!authRegisterResponse.hasError()) {
							topPanel.setContentVisibility(false, false);
							postDelayed(getResources().getInteger(R.integer.default_animation_duration_short), new Runnable() {
								@Override
								public void run() {
									final Intent intent = new Intent(UserRegisterActivity.this, ConfirmPhoneActivity.class);
									intent.putExtra(EXTRA_PHONE, request.getPhone());
									intent.putExtra(EXTRA_CONFIRM_TYPE, ConfirmPhoneActivity.TYPE_REGISTER);
									start(intent, R.anim.slide_in_right, R.anim.slide_out_left, false);
									topPanel.showProgress(false);
								}
							});
						} else {
							handleRegisterError(authRegisterResponse);
						}
					}
				}, new ObservableUtils.BaseOnErrorHandler(getActivity()) {
					@Override
					public void onError(Throwable throwable) {
						if (ErrorUtils.isConnectionError(throwable)) {
							textError.setText(getString(R.string.err_no_internet));
							ViewUtils.setVisible(textError, true);
						}
						topPanel.showProgress(false);
						Log.e(TAG, "doRegister ", throwable);
					}
				});
	}

	private void handleRegisterError(AuthRegisterResponse authRegisterResponse) {
		topPanel.showProgress(false);
		final AuthError error = authRegisterResponse.getError();
		switch (error.getCode()) {
			case 102:
			case 103:
			case 107:
				String text = getString(R.string.error_can_enter, error.getMessage());
				textError.setText(text);
				AndroidUtils.clickify(textError, getString(R.string.can_enter), new ClickSpan.OnClickListener() {
					@Override
					public void onClick() {
						LoginActivity.start(UserRegisterActivity.this, editPhone.getText());
					}
				});
				ViewUtils.setVisible(textError, true);
				break;
			case 109:
				editPhone.setError(error.getMessage());
				break;

			default:
				textError.setText(error.getMessage());
				ViewUtils.setVisible(textError, true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mRegisterSubscription);
	}

	private boolean validate() {
		ViewUtils.setVisible(textError, false);
		final String name = editName.getText();
		final String email = editEmail.getText();
		final String phone = editPhone.getText();
		final boolean emptyName = TextUtils.isEmpty(name);
		final boolean emptyPhone = TextUtils.isEmpty(phone);
		final boolean emptyEmail = TextUtils.isEmpty(email);
		final boolean correctEmail = AndroidUtils.isValidEmail(email);
		if (emptyName) {
			editName.setError(R.string.you_forgot_to_enter_name);
		}
		if (emptyPhone) {
			editPhone.setError(R.string.you_forgot_to_enter_phone);
		}
		if (emptyEmail) {
			editEmail.setError(R.string.you_forgot_to_enter_email);
		} else if (!correctEmail) {
			editEmail.setError(R.string.invalid_email);
		}
		return !emptyEmail && !emptyName && !emptyPhone && correctEmail;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_register_user;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}
}
