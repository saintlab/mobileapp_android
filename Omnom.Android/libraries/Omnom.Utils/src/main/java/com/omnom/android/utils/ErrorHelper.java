package com.omnom.android.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by Ch3D on 18.08.2014.
 */
public class ErrorHelper {

	private LoaderView mLoader;

	private TextView mTxtError;

	private Button mBtnBottom;

	private Button mBtnDemo;

	private List<View> mErrorViews;

	public ErrorHelper(LoaderView loader, TextView txtError, Button btnBottom, List<View> errorViews, CountDownTimer timer) {
		mLoader = loader;
		mTxtError = txtError;
		mBtnBottom = btnBottom;
		mErrorViews = errorViews;
	}

	public ErrorHelper(LoaderView loader, TextView txtError, Button btnBottom, List<View> errorViews) {
		mLoader = loader;
		mTxtError = txtError;
		mBtnBottom = btnBottom;
		mErrorViews = errorViews;
	}

	public ErrorHelper(LoaderView loader, TextView txtError, Button btnBottom, Button btnDemo, List<View> errorViews) {
		mLoader = loader;
		mTxtError = txtError;
		mBtnBottom = btnBottom;
		mBtnDemo = btnDemo;
		mErrorViews = errorViews;
	}

	public void showError(final LoaderError error, View.OnClickListener onClickListener) {
		mLoader.stopProgressAnimation(true);
		ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, true);
		mLoader.post(new Runnable() {
			@Override
			public void run() {
				mLoader.animateLogo2(error.getDrawableId());
			}
		});
		mTxtError.setText(error.getErrorId());
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mBtnBottom.getLayoutParams());
		params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mBtnBottom.setLayoutParams(params);
		mBtnBottom.setText(error.getButtonTextId());
		mBtnBottom.setOnClickListener(onClickListener);
		ViewUtils.setVisible(mBtnDemo, false);
	}

	public void showErrorDemo(final LoaderError error, View.OnClickListener onClickListener) {
		mLoader.stopProgressAnimation(true);
		ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, true);
		mLoader.post(new Runnable() {
			@Override
			public void run() {
				mLoader.animateLogo2(error.getDrawableId());
			}
		});
		mTxtError.setText(error.getErrorId());

		ViewUtils.setVisible(mBtnDemo, true);
		final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mBtnBottom.getLayoutParams());
		params.removeRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mBtnBottom.setLayoutParams(params);
		mBtnBottom.setText(error.getButtonTextId());
		mBtnBottom.setOnClickListener(onClickListener);
	}

	public void showInternetError(View.OnClickListener onClickListener) {
		showError(LoaderError.NO_CONNECTION_TRY, onClickListener);
	}

	public void showErrorBluetoothDisabled(final Activity activity, final int requestCode) {
		showError(LoaderError.BLE_DISABLED, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoader.scaleDown(null);
				ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, false);
				activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), requestCode);
			}
		});
	}

	public void showLocationError() {
		showError(LoaderError.LOCATION_DISABLED, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoader.scaleDown(null);
				ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, false);
				AndroidUtils.startLocationSettings(v.getContext());
			}
		});
	}

	public void hideError() {
		ButterKnife.apply(mErrorViews, ViewUtils.VISIBLITY, false);
	}
}
