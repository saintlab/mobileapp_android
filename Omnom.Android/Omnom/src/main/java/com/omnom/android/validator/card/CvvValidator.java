package com.omnom.android.validator.card;

import com.omnom.android.validator.Validator;

/**
 * Created by mvpotter on 12/22/2014.
 */
public class CvvValidator implements Validator {

	@Override
	public boolean validate(final String cvv) {
		return cvv != null && cvv.length() >= 3;
	}

}
