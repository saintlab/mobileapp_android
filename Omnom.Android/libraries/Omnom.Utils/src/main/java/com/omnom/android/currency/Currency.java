package com.omnom.android.currency;

/**
 * Created by Ch3D on 26.05.2015.
 */
public class Currency {

	public static final Currency RU = new Currency(100, true, "\uF5FC");

	private final float mFactor;

	private final boolean mIsFractional;

	private final String mSymbol;

	private Currency(float factor, boolean isFractional, String symbol) {

		mFactor = factor;
		mIsFractional = isFractional;
		mSymbol = symbol;
	}

	public boolean isFractional() {
		return mIsFractional;
	}

	public float getFractionalFactor() {
		return mFactor;
	}

	public String getSymbol() {
		return mSymbol;
	}
}
