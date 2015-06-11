package com.omnom.android.activity.animation;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.fragment.menu.MenuItemDetailsFragment;
import com.omnom.android.menu.model.Item;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Ch3D on 15.05.2015.
 */
public class MenuItemTransitionController extends FragmentActivityTransitionController {

	public MenuItemTransitionController(final WeakReference<FragmentActivity> activityWeakReference) {
		super(activityWeakReference);
	}

	public void animateClose(final MenuItemDetailsFragment.TransitionParams transitionParams, boolean hasPhoto, final int duration) {
		final FragmentActivity activity = mActivityRef.get();
		if(activity == null) {
			return;
		}

		final View btnApply = activity.findViewById(R.id.btn_apply);

		final int imgSize = activity.getResources().getDimensionPixelSize(R.dimen.menu_dish_image_height);
		final int applyMarginTop = transitionParams.getApplyMarginTop();
		final int translationTop = transitionParams.getTranslationTop();
		final int paddingTop = transitionParams.getPaddingTop();
		final int applyTop = transitionParams.getApplyTop();
		final int top = btnApply.getTop();

		final View mFragmentView = activity.findViewById(R.id.root_frame);
		final View panelRecommendations = mFragmentView.findViewById(R.id.panel_bottom);
		final View title = mFragmentView.findViewById(R.id.txt_title);
		final TextView info = (TextView) mFragmentView.findViewById(R.id.txt_info);
		final TextView additional = (TextView) mFragmentView.findViewById(R.id.txt_info_additional);
		final TextView energy = (TextView) mFragmentView.findViewById(R.id.txt_info_energy);
		final View logo = mFragmentView.findViewById(R.id.img_logo);
		final View rl = mFragmentView.findViewById(R.id.panel_container);
		final ImageView iv = (ImageView) mFragmentView.findViewById(R.id.img_icon);

		AnimationUtils.animateAlphaGone(panelRecommendations, false);
		if(applyMarginTop == 0 && !hasPhoto) {
			// Do nothing
		} else {
			if(applyTop != 0 && !hasPhoto) {
				btnApply.animate().translationY(applyTop + (title.getTop() - top) - applyMarginTop).setDuration(
						duration).start();
			}
		}
		if(applyMarginTop != 0) {
			btnApply.animate().translationY(applyMarginTop).setDuration(duration).start();
		}
		if(translationTop > 0) {
			mFragmentView.findViewById(R.id.root).animate().translationY(translationTop).setDuration(duration).start();
		}
		if(hasPhoto) {
			ViewUtils.setVisibleGone(info, false);
			ViewUtils.setVisibleGone(additional, false);
			ViewUtils.setVisibleGone(energy, false);
			AnimationUtils.scaleHeight(logo, imgSize, duration);
			rl.animate().translationY(paddingTop).setDuration(duration).start();
			title.animate().translationY(-imgSize).setDuration(duration).start();
		} else {
			if(!TextUtils.isEmpty(info.getText())) {
				ViewUtils.setVisibleInvisible(info, false);
			}
			if(!TextUtils.isEmpty(additional.getText())) {
				ViewUtils.setVisibleInvisible(additional, false);
			}
			if(!TextUtils.isEmpty(energy.getText())) {
				ViewUtils.setVisibleInvisible(energy, false);
			}
		}

		iv.postDelayed(new Runnable() {
			@Override
			public void run() {
				activity.getSupportFragmentManager().popBackStack();
			}
		}, duration);
	}

	public void onResume(final MenuItemDetailsFragment.TransitionParams transitionParams, final Item item, final int duration) {
		final FragmentActivity activity = mActivityRef.get();
		if(activity == null) {
			return;
		}

		final View mFragmentView = activity.findViewById(R.id.root_frame);
		final View panelRecommendations = mFragmentView.findViewById(R.id.panel_bottom);
		final View title = mFragmentView.findViewById(R.id.txt_title);
		final TextView info = (TextView) mFragmentView.findViewById(R.id.txt_info);
		final TextView additional = (TextView) mFragmentView.findViewById(R.id.txt_info_additional);
		final TextView energy = (TextView) mFragmentView.findViewById(R.id.txt_info_energy);
		final ImageView logo = (ImageView) mFragmentView.findViewById(R.id.img_logo);
		final View rl = mFragmentView.findViewById(R.id.panel_container);
		final ImageView iv = (ImageView) mFragmentView.findViewById(R.id.img_icon);

		final int[] mImgHeight = new int[1];

		if(transitionParams.getTranslationTop() > 0) {
			mFragmentView.findViewById(R.id.root).animate().translationY(transitionParams.getTranslationTop()).setDuration(0).start();
		}

		ViewUtils.setVisibleGone(info, false);
		ViewUtils.setVisibleGone(additional, false);
		ViewUtils.setVisibleGone(energy, false);
		ViewUtils.setVisibleGone(panelRecommendations, false);

		final View btnApply = mFragmentView.findViewById(R.id.btn_apply);
		btnApply.setTranslationY(transitionParams.getApplyMarginTop());
		boolean hasPhoto = !TextUtils.isEmpty(item.photo());

		if(!hasPhoto) {
			ViewUtils.setVisibleGone(rl, false);
		} else {
			rl.animate().translationY(transitionParams.getPaddingTop()).setDuration(0).start();
			title.animate().translationY(-activity.getResources().getDimensionPixelSize(R.dimen.menu_dish_image_height)).setDuration(0)
			     .start();
		}
		OmnomApplication.getPicasso(activity).load(item.photo()).into(logo);
		iv.post(new Runnable() {
			@Override
			public void run() {
				mImgHeight[0] = iv.getHeight();
				ViewUtils.setVisibleGone(iv, false);
			}
		});
		iv.postDelayed(new Runnable() {
			@Override
			public void run() {
				mFragmentView.findViewById(R.id.root).animate().translationY(0).setDuration(duration).start();
				rl.animate().translationY(0).setDuration(duration).start();
				title.animate().translationY(0).setDuration(duration).start();
				btnApply.animate().translationY(0).setDuration(duration).start();

				AnimationUtils.scaleHeight(logo, mImgHeight[0], duration);
				AnimationUtils.animateAlpha(info, info.getText().length() > 0);
				AnimationUtils.animateAlpha(additional, additional.getText().length() > 0);
				AnimationUtils.animateAlpha(energy, energy.getText().length() > 0);
				AnimationUtils.animateAlpha(panelRecommendations, item.hasRecommendations());
			}
		}, !hasPhoto ? 0 : activity.getResources().getInteger(R.integer.default_animation_duration_quick));
	}
}
