package com.omnom.android.mixpanel;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.mixpanel.model.SimpleMixpanelEvent;
import com.omnom.android.utils.ErrorHelper;
import com.omnom.android.utils.UserHelper;
import com.omnom.android.utils.loader.LoaderError;
import com.omnom.android.utils.loader.LoaderView;

import java.util.List;

/**
 * Created by Ch3D on 22.12.2014.
 */
public class OmnomErrorHelper extends ErrorHelper {
	public OmnomErrorHelper(final LoaderView loader, final TextView txtError, final Button btnBottom, final List<View> errorViews,
	                        final CountDownTimer timer) {
		super(loader, txtError, btnBottom, errorViews, timer);
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
		reportMixPanel(error);
		super.showError(error, onClickListener);
	}

	private void reportMixPanel(final LoaderError error) {
		OmnomApplication.getMixPanelHelper(mLoader.getContext())
		                .track(
				                new SimpleMixpanelEvent(UserHelper.getUserData(mLoader.getContext()), error.getEventName()));
	}
}
