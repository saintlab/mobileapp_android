package com.omnom.android.validator.card;

import com.omnom.android.validator.Validator;

/**
 * Created by mvpotter on 12/22/2014.
 */
public class PanValidator implements Validator {

	@Override
	public boolean validate(final String pan) {
		return pan != null && pan.length() >= 13;
	}

}
