package com.omnom.android.activity;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.auth.response.AuthRegisterResponse;
import com.omnom.util.activity.BaseActivity;
import com.omnom.util.utils.AndroidUtils;
import com.omnom.util.utils.StringUtils;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.functions.Action1;

import static com.omnom.util.utils.AndroidUtils.showToast;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class UserRegisterActivity extends BaseActivity {

	@InjectView(R.id.edit_name)
	protected EditText editName;

	@InjectView(R.id.edit_email)
	protected EditText editEmail;

	@InjectView(R.id.edit_phone)
	protected EditText editPhone;

	@InjectView(R.id.edit_birth)
	protected EditText editBirth;

	@InjectView(R.id.text_agreement)
	protected TextView textAgreement;

	@Inject
	protected AuthService authenticator;

	@Override
	public void initUi() {
		textAgreement.setMovementMethod(LinkMovementMethod.getInstance());
		textAgreement.setText(Html.fromHtml(getResources().getString(R.string.register_agreement)));
	}

	public void performRegister() {
		if(!validate()) {
			showToast(this, R.string.acquiring_mailru_cardholder);
			return;
		}
		// TODO: Fix
		final String date = "1987-06-14";
		AuthRegisterRequest request = AuthRegisterRequest.create(AndroidUtils.getInstallId(this),
		                                                         editName.getText().toString(),
		                                                         StringUtils.EMPTY_STRING,
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

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}
}
