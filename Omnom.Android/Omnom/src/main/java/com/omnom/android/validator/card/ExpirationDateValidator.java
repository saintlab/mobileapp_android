package com.omnom.android.validator.card;

import com.omnom.android.validator.Validator;

/**
 * Created by mvpotter on 12/22/2014.
 */
public class ExpirationDateValidator implements Validator {

	@Override
	public boolean validate(final String expirationDate) {
		return expirationDate != null && expirationDate.length() >= 5;
	}

}
