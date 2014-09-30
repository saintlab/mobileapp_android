package com.omnom.android.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.request.AuthRegisterRequest;
import com.omnom.android.view.ViewPagerIndicatorCircle;
import com.omnom.util.activity.BaseActivity;
import com.omnom.util.utils.AndroidUtils;
import com.omnom.util.utils.StringUtils;
import com.omnom.util.utils.ViewUtils;
import com.omnom.util.view.ErrorEdit;
import com.omnom.util.view.ErrorEditText;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;

/**
 * Created by Ch3D on 28.09.2014.
 */
public class UserRegisterActivity extends BaseActivity {

	public static final int YEAR_OFFSET = 30;
	public static final String DELIMITER_DATE_UI = "/";
	public static final String DELIMITER_DATE_WICKET = "-";

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

	@InjectViews({R.id.title, R.id.page_indicator, R.id.proceed})
	protected List<View> topViews;

	@InjectView(R.id.page_indicator)
	protected ViewPagerIndicatorCircle pageIndicator;

	@Inject
	protected AuthService authenticator;

	private boolean mFirstStart = true;

	@Override
	public void initUi() {
		ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA_NOW, false);
		textAgreement.setMovementMethod(LinkMovementMethod.getInstance());
		textAgreement.setText(Html.fromHtml(getResources().getString(R.string.register_agreement)));

		editBirth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Calendar calendar = Calendar.getInstance();
				DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

					}
				}, calendar.get(Calendar.YEAR) - YEAR_OFFSET, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DatePickerDialog dlg = (DatePickerDialog) dialog;
						editBirth.setText(
								dlg.getDatePicker().getYear() + DELIMITER_DATE_UI + dlg.getDatePicker().getMonth() + DELIMITER_DATE_UI +
										dlg.getDatePicker().getDayOfMonth());
						dialog.dismiss();
					}
				});
				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.show();
			}
		});
		pageIndicator.setFake(true, 2);
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
				}

			}, mFirstStart ? getResources().getInteger(android.R.integer.config_longAnimTime) :
					                          getResources().getInteger(android.R.integer.config_mediumAnimTime));
		}
		mFirstStart = false;
	}

	@OnClick(R.id.proceed)
	public void performRegister(final View view) {
		if(!validate()) {
			return;
		}
		final AuthRegisterRequest request = AuthRegisterRequest.create(AndroidUtils.getInstallId(this),
		                                                               editName.getText(),
		                                                               StringUtils.EMPTY_STRING,
		                                                               editEmail.getText(),
		                                                               editPhone.getText(),
		                                                               editBirth.getText()
		                                                                        .toString()
		                                                                        .replace(DELIMITER_DATE_UI, DELIMITER_DATE_WICKET));
		//		view.setEnabled(false);
		//		authenticator.register(request).subscribe(new Action1<AuthRegisterResponse>() {
		//			@Override
		//			public void call(final AuthRegisterResponse authRegisterResponse) {
		//				view.setEnabled(true);
		//				if(!authRegisterResponse.hasError()) {
		ButterKnife.apply(topViews, ViewUtils.VISIBLITY_ALPHA, false);
		postDelayed(350, new Runnable() {
			@Override
			public void run() {
				final Intent intent = new Intent(UserRegisterActivity.this, ConfirmPhoneActivity.class);
				intent.putExtra(EXTRA_PHONE, request.getPhone());
				startActivity(intent, R.anim.slide_in_right, R.anim.slide_out_left, false);
			}
		});
		//				} else {
		//					textError.setText(authRegisterResponse.getError().getMessage());
		//				}
		//			}
		//		}, new Action1<Throwable>() {
		//			@Override
		//			public void call(Throwable throwable) {
		//				view.setEnabled(true);
		//				Log.e(TAG, "performRegister", throwable);
		//			}
		//		});
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
