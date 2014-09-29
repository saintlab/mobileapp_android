package com.omnom.android.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.util.activity.BaseActivity;
import com.omnom.util.utils.AndroidUtils;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.view.ErrorEdit;
import com.omnom.util.view.ErrorEditText;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class UserRegisterActivity extends BaseActivity {

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

	@Inject
	protected AuthService authenticator;

	@Override
	public void initUi() {
		textAgreement.setMovementMethod(LinkMovementMethod.getInstance());
		textAgreement.setText(Html.fromHtml(getResources().getString(R.string.register_agreement)));

		editBirth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage(R.string.birth_date);
				builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatePickerDialog dlg = (DatePickerDialog) dialog;
						editBirth.setText(dlg.getDatePicker().getYear() + "-" + dlg.getDatePicker().getMonth() + "-" + dlg.getDatePicker
								().getDayOfMonth());
						dialog.dismiss();
					}
				});
				builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
	}

	@OnClick(R.id.proceed)
	public void performRegister() {
		if(!validate()) {
			return;
		}
		// TODO: Fix
		final String date = "1987-06-14";
		AuthRegisterRequest request = AuthRegisterRequest.create(AndroidUtils.getInstallId(this),
		                                                         editName.getText(),
		                                                         StringUtils.EMPTY_STRING,
		                                                         editEmail.getText(),
		                                                         editPhone.getText(),
		                                                         date);
		authenticator.register(request).subscribe(new Action1<AuthRegisterResponse>() {
			@Override
			public void call(AuthRegisterResponse authRegisterResponse) {
				// TODO:
			}
		}, new Action1<Throwable>() {
			@Override
			public void call(Throwable throwable) {
				// TODO:
			}
		});
	}

	private boolean validate() {
		final String name = editName.getText();
		final String email = editEmail.getText();
		final String phone = editPhone.getText();
		final boolean emptyName = TextUtils.isEmpty(name);
		final boolean emptyPhone = TextUtils.isEmpty(phone);
		final boolean emptyEmail = TextUtils.isEmpty(email);
		if(emptyName) {
			editName.setError(R.string.you_forgot_to_enter_name);
		}
		if(emptyPhone) {
			editPhone.setError(R.string.you_forgot_to_enter_phone);
		}
		if(emptyEmail) {
			editEmail.setError(R.string.you_forgot_to_enter_email);
		}
		return !emptyEmail && !emptyName && !emptyPhone;
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
