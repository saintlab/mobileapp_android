package com.omnom.android.activity;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.omnom.android.R;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.util.activity.BaseActivity;
import com.omnom.util.utils.AndroidUtils;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.omnom.util.utils.AndroidUtils.showToast;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class UserRegisterActivity extends BaseActivity {

	@InjectView(R.id.edit_name)
	protected EditText editName;

	@InjectView(R.id.edit_nick)
	protected EditText editNick;

	@InjectView(R.id.edit_email)
	protected EditText editEmail;

	@InjectView(R.id.edit_phone)
	protected EditText editPhone;

	@InjectView(android.R.id.button1)
	protected Button btnSubmit;

	@InjectView(R.id.picker_birth)
	protected DatePicker pickerBirth;

	@Inject
	protected AuthService authenticator;

	@Override
	public void initUi() {

	}

	@OnClick(android.R.id.button1)
	public void performRegister() {
		if(!validate()) {
			showToast(this, R.string.acquiring_mailru_cardholder);
			return;
		}
		// TODO: Fix
		final String date = "1987-06-14";
		AuthRegisterRequest request = AuthRegisterRequest.create(AndroidUtils.getInstallId(this),
		                                                         editName.getText().toString(),
		                                                         editNick.getText().toString(),
		                                                         editEmail.getText().toString(),
		                                                         editPhone.getText().toString(),
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
		return false;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_register_user;
	}
}
