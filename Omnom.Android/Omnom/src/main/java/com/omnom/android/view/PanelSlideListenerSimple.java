package com.omnom.android.view;

import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by Ch3D on 01.03.2015.
 */
public abstract class PanelSlideListenerSimple implements SlidingUpPanelLayout.PanelSlideListener {
	@Override
	public void onPanelSlide(final View panel, final float slideOffset) {
		// Do nothing
	}

	@Override
	public void onPanelCollapsed(final View panel) {
		// Do nothing
	}

	@Override
	public void onPanelExpanded(final View panel) {
		// Do nothing
	}

	@Override
	public void onPanelAnchored(final View panel) {
		// Do nothing
	}

	@Override
	public void onPanelHidden(final View panel) {
		// Do nothing
	}
}
