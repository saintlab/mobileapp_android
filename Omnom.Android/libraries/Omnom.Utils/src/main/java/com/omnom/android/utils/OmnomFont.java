package com.omnom.android.utils;

/**
 * Created by Ch3D on 12.03.2015.
 */
public class OmnomFont {

	public static final String PATH_FONTS = "fonts/";

	public static final OmnomFont LSF_LE_REGULAR = new OmnomFont(PATH_FONTS + "Futura-LSF-Omnom-LE-Regular.otf");

	public static final OmnomFont OSF_REGULAR = new OmnomFont(PATH_FONTS + "Futura-OSF-Omnom-Regular.otf");

	public static final OmnomFont OSF_MEDIUM = new OmnomFont(PATH_FONTS + "Futura-OSF-Omnom-Medium.otf");

	private String mPath;

	public OmnomFont(final String path) {
		mPath = path;
	}

	public String getPath() {
		return mPath;
	}
}
