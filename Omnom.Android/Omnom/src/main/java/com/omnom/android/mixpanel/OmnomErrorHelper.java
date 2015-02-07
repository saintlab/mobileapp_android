package com.omnom.android.mixpanel;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.mixpanel.model.MixpanelEvent;
import com.omnom.android.mixpanel.model.SimpleMixpanelEvent;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.loader.LoaderView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class OmnomErrorHelper extends ErrorHelper {

	public static final List<String> RELEASE_ERRORS = Arrays.asList(LoaderError.EVENT_NO_SERVER_CONNECTION,
																	LoaderError.EVENT_BACKEND_ERROR,
																	LoaderError.EVENT_GEOLOCATION_DISABLED,
																	LoaderError.EVENT_LOW_SIGNAL,
																	LoaderError.BLUETOOTH_DISABLED,
																	LoaderError.EVENT_NO_TABLE);

	public OmnomErrorHelper(final LoaderView loader, final TextView txtError, final Button btnBottom, final List<View> errorViews) {
		super(loader, txtError, btnBottom, errorViews);
	}

	public OmnomErrorHelper(final LoaderView loader, final TextView txtError, final View btnBottom, final List<View> errorViews) {
		super(loader, txtError, btnBottom, errorViews);
	}

	public OmnomErrorHelper(final LoaderView loader, final TextView txtError, final View btnBottom, final TextView txtBottom, final View
			btnDemo, final List<View> errorViews) {
		super(loader, txtError, btnBottom, txtBottom, btnDemo, errorViews);
	}

	@Override
	public void showError(final LoaderError error, final View.OnClickListener onClickListener) {
		showError(null, error, onClickListener);
	}

	public void showError(final String requestId, final LoaderError error, final View.OnClickListener onClickListener) {
		reportMixPanel(requestId, error);
		super.showError(error, onClickListener);
	}

	private void reportMixPanel(final String requestId, final LoaderError error) {
		final MixpanelEvent event = new SimpleMixpanelEvent(UserHelper.getUserData(mLoader.getContext()),
															error.getEventName(), requestId);
		final MixPanelHelper.Project project = RELEASE_ERRORS.contains(error.getEventName()) ?
																MixPanelHelper.Project.OMNOM :
																MixPanelHelper.Project.OMNOM_ANDROID;
		OmnomApplication.getMixPanelHelper(mLoader.getContext()).track(project, event);
	}

	public void showUnknownPlace() {

	}

	public void showWrongQrError(final String requestId, View.OnClickListener onClickListener) {
		showError(requestId, LoaderError.UNKNOWN_QR_CODE, onClickListener);
	}
}
