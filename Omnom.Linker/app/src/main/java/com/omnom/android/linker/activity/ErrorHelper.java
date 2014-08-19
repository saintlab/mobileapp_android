package com.omnom.android.linker.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.linker.R;
import com.omnom.android.linker.utils.AndroidUtils;
import com.omnom.android.linker.utils.ViewUtils;
import com.omnom.android.linker.widget.loader.LoaderView;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Ch3D on 18.08.2014.
 */
public class ErrorHelper {

	private LoaderView mLoader;
	private TextView mTxtError;
	private Button mBtnBottom;
	private List<View> mErrorViews;
	private CountDownTimer mTimer;

	public ErrorHelper(LoaderView loader, TextView txtError, Button btnBottom, List<View> errorViews, CountDownTimer timer) {
		mLoader = loader;
		mTxtError = txtError;
		mBtnBottom = btnBottom;
		mErrorViews = errorViews;
		mTimer = timer;
	}

	public void showError(final int logoResId, int errTextResId, int btnTextResId, View.OnClickListener onClickListener) {
		mLoader.updateProgress(0);
		mTimer.cancel();
		ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, true);
		mLoader.post(new Runnable() {
			@Override
			public void run() {
				mLoader.animateLogo(logoResId);
			}
		});
		mTxtError.setText(errTextResId);
		mBtnBottom.setText(btnTextResId);
		mBtnBottom.setOnClickListener(onClickListener);
	}

	public void showInternetError(View.OnClickListener onClickListener) {
		showError(R.drawable.ic_no_connection, R.string.error_you_have_no_internet_connection, R.string.try_once_again,
		          onClickListener);
	}

	public void showErrorBluetoothDisabled(final Activity activity, final int requestCode) {
		showError(R.drawable.ic_bluetooth_white, R.string.error_bluetooth_disabled, R.string.open_settings, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoader.scaleDown(null);
				ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, false);
				activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestCode);
			}
		});
	}

	public void showLocationError() {
		showError(R.drawable.ic_geolocation_white, R.string.error_location_disabled, R.string.open_settings, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoader.scaleDown(null);
				ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, false);
				AndroidUtils.startLocationSettings(v.getContext());
			}
		});
	}

	public void setTimer(CountDownTimer timer) {
		mTimer = timer;
	}
}
