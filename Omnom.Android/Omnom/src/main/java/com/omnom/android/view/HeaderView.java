package com.omnom.android.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.Arrays;

import butterknife.ButterKnife;

import static butterknife.ButterKnife.findById;

/**
 * Created by Ch3D on 01.10.2014.
 */
public class HeaderView extends RelativeLayout {

	private TextView txtTitle;

	private TextView txtTitleMedium;

	private TextView txtTitleBig;

	private Button btnRight;

	private Button btnLeft;

	private ProgressBar progress;

	private ViewPagerIndicatorCircle pageIndicator;

	private int mBtnRightLastVisibility;

	private ImageButton btnRightDrawable;

	private ImageButton btnLeftDrawable;

	private int mBtnRightDrawableLastVisibility;

	public HeaderView(Context context) {
		super(context);
		init();
	}

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		final View view = LayoutInflater.from(getContext()).inflate(R.layout.view_header, this);
		txtTitle = findById(view, R.id.title);
		txtTitleMedium = findById(view, R.id.title_medium);
		txtTitleBig = findById(view, R.id.title_big);
		btnRight = findById(view, R.id.btn_right);
		btnLeft = findById(view, R.id.btn_left);
		btnRightDrawable = findById(view, R.id.btn_right_drawable);
		btnLeftDrawable = findById(view, R.id.btn_left_drawable);
		pageIndicator = findById(view, R.id.page_indicator);
		progress = findById(view, R.id.progress);

		btnRight.setVisibility(View.GONE);
		btnLeft.setVisibility(View.GONE);
		setClickable(true);

		ViewTreeObserver viewTreeObserver = txtTitleBig.getViewTreeObserver();
		if(viewTreeObserver.isAlive()) {
			viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					AndroidUtils.removeOnGlobalLayoutListener(txtTitleBig, this);
					updateTitleRules();
				}
			});
		}
	}

	public HeaderView setTitle(final int resId) {
		txtTitle.setText(resId);
		ViewUtils.setVisibleGone(txtTitleBig, false);
		ViewUtils.setVisibleGone(txtTitleMedium, false);
		ViewUtils.setVisibleGone(txtTitle, true);
		return this;
	}

	public HeaderView setTxtTitleMedium(final int resId) {
		txtTitleMedium.setText(resId);
		ViewUtils.setVisibleGone(txtTitleBig, false);
		ViewUtils.setVisibleGone(txtTitleMedium, true);
		ViewUtils.setVisibleGone(txtTitle, false);
		return this;
	}

	public HeaderView setTitleBig(final int resId) {
		txtTitleBig.setText(resId);
		ViewUtils.setVisibleGone(txtTitleBig, true);
		ViewUtils.setVisibleGone(txtTitleMedium, false);
		ViewUtils.setVisibleGone(txtTitle, false);
		return this;
	}

	public HeaderView setTitleBig(final String title, OnClickListener listener) {
		txtTitleBig.setText(title);
		txtTitleBig.setOnClickListener(listener);
		ViewUtils.setVisibleGone(txtTitleBig, true);
		ViewUtils.setVisibleGone(txtTitle, false);
		return this;
	}

	public HeaderView setButtonRight(final int resId, OnClickListener listener) {
		setButton(btnRight, resId, listener);
		updateTitleRules();
		return this;
	}

	public Button getBtnRight() {
		return btnRight;
	}

	public HeaderView setButtonLeft(final int resId, OnClickListener listener) {
		setButton(btnLeft, resId, listener);
		return this;
	}

	private void updateTitleRules() {
		LayoutParams layoutParams = (LayoutParams) txtTitleBig.getLayoutParams();
		if(btnRight.getVisibility() == VISIBLE && ViewUtils.intersect(txtTitleBig, btnRight)) {
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.btn_right);
		} else {
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
			layoutParams.addRule(RelativeLayout.LEFT_OF, 0);
		}
	}

	private HeaderView setButton(Button btn, int resId, final OnClickListener listener) {
		btn.setText(resId);
		btn.setOnClickListener(listener);
		btn.setVisibility(View.VISIBLE);
		return this;
	}

	public HeaderView setButtonRightDrawable(final int resId, OnClickListener listener) {
		btnRight.setVisibility(View.GONE);
		setButtonDrawable(btnRightDrawable, resId, listener);
		return this;
	}

	public HeaderView setButtonLeftDrawable(final int resId, OnClickListener listener) {
		btnLeft.setVisibility(View.GONE);
		setButtonDrawable(btnLeftDrawable, resId, listener);
		return this;
	}

	private HeaderView setButtonDrawable(ImageButton btn, final int resId, final OnClickListener listener) {
		btn.setBackgroundResource(resId);
		btn.setOnClickListener(listener);
		btn.setVisibility(View.VISIBLE);
		return this;
	}

	public HeaderView setPaging(final int count, final int index) {
		pageIndicator.setFake(true, count);
		pageIndicator.setCurrentItem(index);
		return this;
	}

	public HeaderView setTitleBigDrawableRight(final @DrawableRes int resId) {
		txtTitleBig.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.drawable_padding_small));
		txtTitleBig.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
		return this;
	}

	public HeaderView setContentVisibility(final boolean visible, final boolean now) {
		final View[] views = new View[]{btnRight, txtTitle, pageIndicator, btnLeft};
		ButterKnife.apply(Arrays.asList(views), now ? ViewUtils.VISIBLITY_ALPHA_NOW : ViewUtils.VISIBLITY_ALPHA, visible);
		return this;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		btnRight.setEnabled(enabled);
		btnLeft.setEnabled(enabled);
	}

	public boolean isAlphaVisible() {
		return pageIndicator.getAlpha() == 0;
	}

	public HeaderView setRigthButtonVisibile(boolean visibile) {
		ViewUtils.setVisibleGone(btnRight, visibile);
		return this;
	}

	public HeaderView showProgress(final boolean show) {
		if(show) {
			mBtnRightLastVisibility = btnRight.getVisibility();
			mBtnRightDrawableLastVisibility = btnRightDrawable.getVisibility();
			ViewUtils.setVisibleGone(btnRight, false);
			ViewUtils.setVisibleGone(btnRightDrawable, false);
			ViewUtils.setVisibleGone(progress, true);
		} else {
			if(mBtnRightLastVisibility == VISIBLE) {
				ViewUtils.setVisibleGone(btnRight, true);
			}
			if(mBtnRightDrawableLastVisibility == VISIBLE) {
				ViewUtils.setVisibleGone(btnRightDrawable, true);
			}
			ViewUtils.setVisibleGone(progress, false);
		}
		updateTitleRules();
		return this;
	}

	public HeaderView showButtonRight(final boolean show) {
		AnimationUtils.animateAlpha(btnRight, show);
		return this;
	}

	public HeaderView setButtonRightEnabled(final boolean enabled) {
		btnRight.setEnabled(enabled);
		return this;
	}
}
