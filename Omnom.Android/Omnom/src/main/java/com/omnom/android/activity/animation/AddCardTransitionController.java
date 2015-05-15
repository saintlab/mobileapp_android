package com.omnom.android.activity.animation;

import android.app.Activity;
import android.view.View;

import com.omnom.android.R;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Ch3D on 15.05.2015.
 */
public class AddCardTransitionController extends ActivityTransitionController {
	int panelY = 0;

	int cameraY = 0;

	int cameraX = 0;

	int panelX = 0;

	private boolean mMinimized = false;

	public AddCardTransitionController(WeakReference<Activity> activityWeakReference) {
		super(activityWeakReference);
	}

	public void animteCamera(final boolean minimize) {
		final Activity activity = mActivityRef.get();
		if(activity != null) {

			final View panelCard = activity.findViewById(R.id.panel_card);
			final View imgCamera = activity.findViewById(R.id.img_camera);
			final View txtCamera = activity.findViewById(R.id.txt_camera);
			final View editCardNumber = activity.findViewById(R.id.txt_card_number);
			final View editCardExpDate = activity.findViewById(R.id.txt_exp_date);
			final View editCardCvv = activity.findViewById(R.id.txt_cvv);

			int[] sp = new int[2];
			panelCard.getLocationOnScreen(sp);
			if(panelY == 0) {
				panelY = sp[1];
			}
			if(panelX == 0) {
				panelX = sp[0];
			}

			imgCamera.getLocationOnScreen(sp);
			if(cameraY == 0) {
				cameraY = sp[1];
			}
			if(cameraX == 0) {
				cameraX = sp[0];
			}

			if(mMinimized != minimize) {
				final int v = (int) ((cameraY - panelY) / 1.5f);
				if(minimize) {
					AndroidUtils.setBackground(imgCamera, null);
					AnimationUtils.animateAlpha(txtCamera, false);
					imgCamera.animate().x(panelCard.getMeasuredWidth() - imgCamera.getMeasuredWidth()).start();
					imgCamera.animate().translationYBy(-v).start();
					AnimationUtils.translationY(-v, editCardCvv, editCardExpDate, editCardNumber);
				} else {
					AnimationUtils.animateAlpha(txtCamera, true);
					imgCamera.animate().x(cameraX - panelX).start();
					AnimationUtils.translationY(0, imgCamera, editCardCvv, editCardExpDate, editCardNumber);
					AndroidUtils.setBackground(imgCamera, activity.getResources().getDrawable(R.drawable.scan_frame));
				}
				mMinimized = minimize;
			}
		}
	}
}
