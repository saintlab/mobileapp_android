package com.omnom.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.Arrays;

import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 01.10.2014.
 */
public class LoginPanelTop extends RelativeLayout {
	private TextView txtTitle;

	private Button btnRight;

	private Button btnLeft;

	private ProgressBar progress;

	private ViewPagerIndicatorCircle pageIndicator;

	private int mBtnRightLastVisibility;

	public LoginPanelTop(Context context) {
		super(context);
		init();
	}

	public LoginPanelTop(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LoginPanelTop(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		final View view = LayoutInflater.from(getContext()).inflate(R.layout.view_enter_panel_top, this);
		txtTitle = findById(view, R.id.title);
		btnRight = findById(view, R.id.btn_right);
		btnLeft = findById(view, R.id.btn_left);
		pageIndicator = findById(view, R.id.page_indicator);
		progress = findById(view, R.id.progress);

		btnRight.setVisibility(View.GONE);
		btnLeft.setVisibility(View.GONE);
	}

	public void setTitle(final int resId) {
		txtTitle.setText(resId);
	}

	public void setButtonRight(final int resId, OnClickListener listener) {
		btnRight.setText(resId);
		btnRight.setOnClickListener(listener);
		btnRight.setVisibility(View.VISIBLE);
	}

	public void setButtonLeft(final int resId, OnClickListener listener) {
		btnLeft.setText(resId);
		btnLeft.setOnClickListener(listener);
		btnLeft.setVisibility(View.VISIBLE);
	}

	public void setPaging(final int count, final int index) {
		pageIndicator.setFake(true, count);
		pageIndicator.setCurrentItem(index);
	}

	public void setContentVisibility(final boolean visible, final boolean now) {
		final View[] views = new View[]{btnRight, txtTitle, pageIndicator, btnLeft};
		ButterKnife.apply(Arrays.asList(views), now ? ViewUtils.VISIBLITY_ALPHA_NOW : ViewUtils.VISIBLITY_ALPHA, visible);
	}

	public void setEnabled(final boolean enabled) {
		btnRight.setEnabled(enabled);
		btnLeft.setEnabled(enabled);
	}

	public boolean isAlphaVisible() {
		return pageIndicator.getAlpha() == 0;
	}

	public void setRigthButtonVisibile(boolean visibile) {
		ViewUtils.setVisible(btnRight, visibile);
	}

	public void showProgress(final boolean show) {
		if(show) {
			mBtnRightLastVisibility = btnRight.getVisibility();
			ViewUtils.setVisible(btnRight, !show);
			ViewUtils.setVisible(progress, show);
		} else {
			if(mBtnRightLastVisibility == VISIBLE) {
				ViewUtils.setVisible(btnRight, !show);
			}
			ViewUtils.setVisible(progress, show);
		}
	}

	public void showButtonRight(final boolean show) {
		AnimationUtils.animateAlpha(btnRight, show);
	}
}
