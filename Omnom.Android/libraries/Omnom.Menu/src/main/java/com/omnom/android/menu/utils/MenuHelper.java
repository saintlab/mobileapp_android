package com.omnom.android.menu.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.omnom.android.menu.R;
import com.omnom.android.menu.model.Details;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.utils.utils.ViewUtils;

/**
 * Created by Ch3D on 04.02.2015.
 */
public class MenuHelper {
	public static void bindDetails(final Context context, Details details, TextView txtDetails, boolean large) {
		final boolean hasDetails = details != null;
		StringBuilder sb = new StringBuilder();
		if(hasDetails) {
			if(details.energyTotal() > 0 && details.weight() > 0) {
				sb.append(context.getString(R.string.dish_details, details.energyTotal(), details.weight()));
			}
			if(large) {
				final int persons = details.persons();
				if(persons > 0) {
					sb.append("\n");
					if(persons > 4) {
						sb.append(context.getString(R.string.dish_details_persons_count_large, persons));
					} else {
						int pers = R.string.persons_one;
						switch(persons) {
							case 1:
								pers = R.string.persons_one;
								break;
							case 2:
								pers = R.string.persons_two;
								break;
							case 3:
								pers = R.string.persons_three;
								break;
							case 4:
								pers = R.string.persons_four;
								break;
						}
						sb.append(context.getString(R.string.dish_details_persons_count, context.getString(pers)));
					}
				}
				if(details.cookingTime() > 0) {
					sb.append("\n");
					sb.append(context.getString(R.string.dish_details_cooking_time, details.cookingTime()));
				}
			}
		}

		final String result = sb.toString();
		final boolean empty = TextUtils.isEmpty(result);
		ViewUtils.setVisible(txtDetails, !empty);
		if(!empty) {
			txtDetails.setText(result);
		}
	}

	public static Item getItem(final Menu menu, final String recId) {
		if(menu == null || menu.items() == null || menu.items().items() == null) {
			return null;
		}
		return menu.items().items().get(recId);
	}
}
