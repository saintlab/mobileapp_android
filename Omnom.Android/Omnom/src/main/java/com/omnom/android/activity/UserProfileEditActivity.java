package com.omnom.android.activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.camera.CropImageIntentBuilder;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomFragmentActivity;
import com.omnom.android.auth.AuthService;
import com.omnom.android.auth.UserData;
import com.omnom.android.auth.response.RecoveryResponse;
import com.omnom.android.auth.response.UserResponse;
import com.omnom.android.fragment.UserPhotoOptionsFragment;
import com.omnom.android.restaurateur.model.UserProfile;
import com.omnom.android.utils.ObservableUtils;
import com.omnom.android.utils.activity.OmnomActivity;
import com.omnom.android.utils.drawable.RoundTransformation;
import com.omnom.android.utils.drawable.RoundedDrawable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.DateUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.utils.view.ErrorEdit;
import com.omnom.android.utils.view.ErrorEditText;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.mime.TypedFile;
import rx.functions.Action1;
import rx.functions.Func2;

import static com.omnom.android.utils.utils.AndroidUtils.showToast;

public class UserProfileEditActivity extends BaseOmnomFragmentActivity {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static final int FLAG_CHANGE_AVATAR = 1;

	private static final int TAKE_PHOTO_CODE = 11;

	private static final int ANDROID_CROP_IMAGE = 12;

	private static final int PICK_IMAGE = 13;

	private static final String TAG = UserProfileEditActivity.class.getSimpleName();

	public static void start(final OmnomActivity activity) {
		final Intent intent = new Intent(activity.getActivity(), UserProfileEditActivity.class);
		activity.startForResult(intent, R.anim.slide_in_right, R.anim.slide_out_left, REQUEST_CODE_USER_PROFILE_EDIT);
	}

	public static void start(final OmnomActivity activity, final int flags) {
		final Intent intent = new Intent(activity.getActivity(), UserProfileEditActivity.class);
		intent.putExtra(EXTRA_FLAGS, flags);
		activity.startForResult(intent, R.anim.slide_in_right, R.anim.slide_out_left, REQUEST_CODE_USER_PROFILE_EDIT);
	}

	@InjectView(R.id.edit_name)
	protected ErrorEdit mEditName;

	@InjectView(R.id.edit_email)
	protected ErrorEdit mEditEmail;

	@InjectView(R.id.txt_phone)
	protected TextView mTxtPhone;

	@InjectView(R.id.edit_birth)
	protected ErrorEditText mEditBirth;

	@InjectView(R.id.img_user)
	protected ImageView mImgUser;

	@Inject
	protected AuthService authenticator;

	private GregorianCalendar mCalendar;

	private Uri mCapturedImageURI;

	private File mCroppedImageFile;

	private boolean mImageChanged = false;

	private ObservableUtils.BaseOnErrorHandler mBaseOnErrorHandler;

	private int mAvatarSize;

	private boolean mAvatarCleared = false;

	private int mFlags;

	private boolean mFirstStart = true;

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mFlags = intent.getIntExtra(EXTRA_FLAGS, -1);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mFirstStart && mFlags == FLAG_CHANGE_AVATAR) {
			busy(true);
			postDelayed(getResources().getInteger(R.integer.default_animation_duration_short),
			            new Runnable() {
				            @Override
				            public void run() {
					            busy(false);
					            onImage();
				            }
			            });
		}
		mFirstStart = false;
	}

	@Override
	public void initUi() {
		mAvatarSize = getResources().getDimensionPixelSize(R.dimen.profile_avatar_size);

		mBaseOnErrorHandler = new ObservableUtils.BaseOnErrorHandler(getActivity()) {
			@Override
			protected void onError(final Throwable throwable) {
				Log.e(TAG, "updateUser", throwable);
			}
		};

		mCalendar = new GregorianCalendar();
		mCalendar.add(Calendar.YEAR, -UserRegisterActivity.YEAR_OFFSET);

		mEditBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
				if(hasFocus) {
					showDatePickerDialog();
				}
			}
		});

		mEditBirth.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				if(mEditBirth.isFocused()) {
					showDatePickerDialog();
				}
			}
		});

		updateUserImage(StringUtils.EMPTY_STRING);
		initUserData(getUserData());
	}

	@OnClick(R.id.img_user)
	protected void onImage() {
		if(!isBusy()) {
			UserPhotoOptionsFragment.show(getSupportFragmentManager(), R.id.fragment_container);
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK) {
			if(requestCode == TAKE_PHOTO_CODE) {
				cropImage(mCapturedImageURI);
			} else if(requestCode == ANDROID_CROP_IMAGE) {
				mImageChanged = true;
				mAvatarCleared = false;
				mImgUser.setPadding(0, 0, 0, 0);
				RoundedDrawable.setScaledRoundedDrawable(mImgUser,
				                                         BitmapFactory.decodeFile(mCroppedImageFile.getAbsolutePath()),
				                                         mAvatarSize);
			} else if(requestCode == PICK_IMAGE) {
				cropImage(data.getData());
			}
		}
	}

	private void cropImage(final Uri data) {
		mCroppedImageFile = new File(getFilesDir(), "crop.jpg");
		final Uri croppedImage = Uri.fromFile(mCroppedImageFile);

		CropImageIntentBuilder cropImage = new CropImageIntentBuilder(640, 640, croppedImage);
		cropImage.setOutlineColor(getResources().getColor(R.color.transparent_black));
		cropImage.setSourceImage(data);

		startActivityForResult(cropImage.getIntent(this), ANDROID_CROP_IMAGE);
	}

	private void showDatePickerDialog() {
		AndroidUtils.hideKeyboard(mEditBirth);

		final Date date = DateUtils.parseDate(DATE_FORMAT, mEditBirth.getText().toString());
		mCalendar.setTime(date);

		DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			}
		}, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		Calendar minPickerDate = Calendar.getInstance();
		minPickerDate.set(Calendar.YEAR, UserRegisterActivity.START_YEAR);
		dialog.getDatePicker().setMinDate(minPickerDate.getTimeInMillis());
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DatePickerDialog dlg = (DatePickerDialog) dialog;
				mCalendar.set(dlg.getDatePicker().getYear(), dlg.getDatePicker().getMonth(), dlg.getDatePicker().getDayOfMonth());
				CharSequence dateFormatted = DATE_FORMAT.format(mCalendar.getTime());
				mEditBirth.setText(dateFormatted);
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

	@OnClick(R.id.txt_cancel)
	public void onCancel() {
		finish();
	}

	@OnClick(R.id.txt_change_phone)
	public void onChangePhone() {
		final String authToken = OmnomApplication.get(getActivity()).getAuthToken();
		authenticator.getRecoveryUri(authToken).subscribe(new Action1<RecoveryResponse>() {
			@Override
			public void call(final RecoveryResponse recoveryResponse) {
				if(recoveryResponse.isSuccess()) {
					WebActivity.start(UserProfileEditActivity.this, recoveryResponse.getLink());
				} else {
					showToast(getActivity(), R.string.something_went_wrong_try_again);
				}
			}
		}, mBaseOnErrorHandler);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@OnClick(R.id.txt_done)
	public void onDone() {
		if(validate()) {
			final String authToken = OmnomApplication.get(getActivity()).getAuthToken();
			if(mImageChanged) {
				final rx.Observable<UserResponse> avatarObservable = authenticator.updateAvatar(authToken, new TypedFile("image/jpeg",
				                                                                                                         mCroppedImageFile));

				final rx.Observable<UserResponse> userResponseObservable = authenticator.updateUser(authToken,
				                                                                                    mEditName.getText(),
				                                                                                    mEditEmail.getText(),
				                                                                                    mEditBirth.getText().toString(),
				                                                                                    StringUtils.EMPTY_STRING);

				rx.Observable.zip(avatarObservable, userResponseObservable,
				                  new Func2<UserResponse, UserResponse, Pair<UserResponse, UserResponse>>() {
					                  @Override
					                  public Pair<UserResponse, UserResponse> call(final UserResponse avatarResponse,
					                                                               final UserResponse userResponse) {
						                  return Pair.create(avatarResponse, userResponse);
					                  }
				                  })
				             .subscribe(new Action1<Pair<UserResponse, UserResponse>>() {
					             @Override
					             public void call(final Pair<UserResponse, UserResponse> pair) {
						             final UserProfile userProfile = new UserProfile(pair.second);
						             userProfile.getUser().setAvatar(pair.first.getUser().getAvatar());

						             OmnomApplication.get(getActivity()).cacheUserProfile(userProfile.getUser());

						             final Intent data = new Intent();
						             data.putExtra(EXTRA_USER_DATA, pair.second.getUser());
						             data.putExtra(EXTRA_USER_AVATAR, mCroppedImageFile.getAbsolutePath());

						             setResult(RESULT_OK, data);
						             finish();
					             }
				             }, mBaseOnErrorHandler);
			} else {
				authenticator.updateUser(authToken, mEditName.getText(), mEditEmail.getText(), mEditBirth.getText().toString(),
				                         mAvatarCleared ? StringUtils.EMPTY_STRING : getUserData().getAvatar())
				             .subscribe(
						             new Action1<UserResponse>() {
							             @Override
							             public void call(final UserResponse userResponse) {
								             final Intent data = new Intent();
								             data.putExtra(EXTRA_USER_DATA, userResponse.getUser());
								             setResult(RESULT_OK, data);
								             finish();
							             }
						             }, mBaseOnErrorHandler);
			}
		}
	}

	private boolean validate() {
		boolean valid = true;
		if(TextUtils.isEmpty(mEditBirth.getText())) {
			valid &= false;
			mEditBirth.setError(true, getString(R.string.you_forgot_to_enter_birth_date));
		}
		if(TextUtils.isEmpty(mEditName.getText())) {
			valid &= false;
			mEditBirth.setError(true, getString(R.string.you_forgot_to_enter_name));
		}
		if(TextUtils.isEmpty(mEditEmail.getText())) {
			valid &= false;
			mEditBirth.setError(true, getString(R.string.you_forgot_to_enter_email));
		}
		if(!AndroidUtils.isValidEmail(mEditEmail.getText())) {
			valid &= false;
			mEditBirth.setError(true, getString(R.string.invalid_email));
		}
		return valid;
	}

	private void initUserData(UserData user) {
		if(user == null) {
			showToast(this, R.string.error_user_not_found);
			finish();
			return;
		}
		mTxtPhone.setText(user.getPhone());
		mEditEmail.setText(user.getEmail());
		mEditName.setText(user.getName());
		mEditBirth.setText(user.getBirthDate().replace("-", "/"));

		updateUserImage(user.getAvatar());
	}

	@Deprecated
	private RoundedDrawable getPlaceholderDrawable(int dimension) {
		final Bitmap placeholderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.empty_avatar);
		return new RoundedDrawable(placeholderBmp, dimension, 0);
	}

	@Deprecated
	private void updateUserImage(String url) {
		final RoundedDrawable placeholderDrawable = getPlaceholderDrawable(mAvatarSize);
		if(TextUtils.isEmpty(url)) {
			AndroidUtils.setBackground(mImgUser, placeholderDrawable);
			mImgUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defolt_user));
			final int padding = ViewUtils.dipToPixels(this, 24);
			mImgUser.setPadding(padding, padding, padding, padding);
		} else {
			AndroidUtils.setBackground(mImgUser, null);
			mImgUser.setPadding(0, 0, 0, 0);
			OmnomApplication.getPicasso(this).load(url)
			                .placeholder(placeholderDrawable)
			                .resize(mAvatarSize, mAvatarSize).centerCrop()
			                .transform(RoundTransformation.create(mAvatarSize, 0))
			                .into(mImgUser);
		}
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_user_profile_edit;
	}

	public void takePhoto() {
		final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "temp.jpg");
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
		if(cameraIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
		}
	}

	public void loadMedia() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
	}

	public void deleteCurrent() {
		mAvatarCleared = true;
		final RoundedDrawable placeholderDrawable = getPlaceholderDrawable(mAvatarSize);
		AndroidUtils.setBackground(mImgUser, placeholderDrawable);
		mImgUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_defolt_user));
		final int padding = ViewUtils.dipToPixels(getActivity(), 24);
		mImgUser.setPadding(padding, padding, padding, padding);
	}
}
