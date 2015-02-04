package com.omnom.android.menu.utils;

import android.content.Context;
import android.widget.TextView;

import com.omnom.android.menu.R;
import com.omnom.android.menu.model.Details;
import com.omnom.android.utils.utils.ViewUtils;

/**
 * Created by Ch3D on 04.02.2015.
 */
public class MenuHelper {
	public static void bindDetails(final Context context, Details details, TextView txtDetails) {
		final boolean hasDetails = details != null;
		ViewUtils.setVisible(txtDetails, hasDetails);
		if(hasDetails) {
			txtDetails.setText(context.getString(R.string.dish_details, details.energyTotal(), details.weight()));
		}
	}
}
