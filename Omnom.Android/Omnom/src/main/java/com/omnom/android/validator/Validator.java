package com.omnom.android.validator;

/**
 * Created by mvpotter on 12/22/2014.
 */
public interface Validator {

	/**
	 * Checks if given value is valid.
	 *
	 * @param value value to check
	 * @return true if valid
	 */
	boolean validate(String value);

}
