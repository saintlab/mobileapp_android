package com.omnom.android.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
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

	private static final int LINES_COUNT_TO_APPLY_LEFT_ALIGN = 3;

	private TextView mTxtBottom;

	private LoaderView mLoader;

	private TextView mTxtError;

	private View mBtnBottom;

	private View mBtnDemo;

	private List<View> mErrorViews;

	public ErrorHelper(LoaderView loader, TextView txtError, View btnBottom, List<View> errorViews) {
		mLoader = loader;
		mTxtError = txtError;
		mBtnBottom = btnBottom;
		mErrorViews = errorViews;
		ViewTreeObserver vto = mTxtError.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				final Layout layout = mTxtError.getLayout();
				if (layout != null) {
					final int linesCount = layout.getLineCount();
					if (linesCount > LINES_COUNT_TO_APPLY_LEFT_ALIGN) {
						mTxtError.setGravity(Gravity.START);
					}
				}
			}
		});
	}

	public ErrorHelper(LoaderView loader, TextView txtError, View btnBottom, TextView txtBottom, View btnDemo, List<View> errorViews) {
		this(loader, txtError, btnBottom, errorViews);
		mTxtBottom = txtBottom;
		mBtnDemo = btnDemo;
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
		if (mTxtBottom != null) {
			mTxtBottom.setCompoundDrawablesWithIntrinsicBounds(error.getmBtnDrawableId(), 0, 0, 0);
			mTxtBottom.setText(error.getButtonTextId());
		}
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
		mTxtBottom.setCompoundDrawablesWithIntrinsicBounds(error.getmBtnDrawableId(), 0, 0, 0);
		mTxtBottom.setText(error.getButtonTextId());
		mBtnBottom.setOnClickListener(onClickListener);
	}

	public void showInternetError(View.OnClickListener onClickListener) {
		showError(LoaderError.NO_CONNECTION_TRY, onClickListener);
	}

	public void showUnknownError(View.OnClickListener onClickListener) {
		showError(LoaderError.UNKNOWN_ERROR, onClickListener);
	}

	public void showBackendError(View.OnClickListener onClickListener) {
		showError(LoaderError.BACKEND_ERROR, onClickListener);
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

	public void showPaymentDeclined(View.OnClickListener onClickListener) {
		showError(LoaderError.PAYMENT_DECLINED, onClickListener);
	}

	public void showNoOrders(View.OnClickListener onClickListener) {
		showError(LoaderError.NO_ORDERS, onClickListener);
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
