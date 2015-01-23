package com.omnom.android.validator;

/**
 * Created by mvpotter on 1/23/2015.
 */
public class LongValidator implements Validator {

	@Override
	public boolean validate(final String value) {
		if (value == null) {
			return false;
		}
		try {
			Long.parseLong(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
