package com.omnom.android.view;

import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

/**
 * Created by Ch3D on 01.03.2015.
 */
public class PanelSlideListenerAdapter implements SlidingUpPanelLayout.PanelSlideListener {

	private ArrayList<SlidingUpPanelLayout.PanelSlideListener> mListeners = new ArrayList<SlidingUpPanelLayout.PanelSlideListener>();

	public PanelSlideListenerAdapter() {
		// Do nothing
	}

	public void addListener(SlidingUpPanelLayout.PanelSlideListener listener) {
		mListeners.add(listener);
	}

	public void removeListener(SlidingUpPanelLayout.PanelSlideListener listener) {
		mListeners.remove(listener);
	}

	@Override
	public void onPanelSlide(final View panel, final float slideOffset) {
		for(SlidingUpPanelLayout.PanelSlideListener listener : mListeners) {
			listener.onPanelSlide(panel, slideOffset);
		}
	}

	@Override
	public void onPanelCollapsed(final View panel) {
		for(SlidingUpPanelLayout.PanelSlideListener listener : mListeners) {
			listener.onPanelCollapsed(panel);
		}
	}

	@Override
	public void onPanelExpanded(final View panel) {
		for(SlidingUpPanelLayout.PanelSlideListener listener : mListeners) {
			listener.onPanelExpanded(panel);
		}
	}

	@Override
	public void onPanelAnchored(final View panel) {
		for(SlidingUpPanelLayout.PanelSlideListener listener : mListeners) {
			listener.onPanelAnchored(panel);
		}
	}

	@Override
	public void onPanelHidden(final View panel) {
		for(SlidingUpPanelLayout.PanelSlideListener listener : mListeners) {
			listener.onPanelHidden(panel);
		}
	}
}
