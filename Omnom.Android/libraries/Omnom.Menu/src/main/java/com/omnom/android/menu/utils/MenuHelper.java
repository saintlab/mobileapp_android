package com.omnom.android.menu.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.widget.TextView;

import com.omnom.android.menu.R;
import com.omnom.android.menu.model.Details;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.utils.utils.ViewUtils;

/**
 * Created by Ch3D on 04.02.2015.
 */
public class MenuHelper {

	public static final String ENERGY_DELIMITER = "|";

	public static void putValue(final SparseIntArray array, int key, int value) {
		if(value > 0) {
			array.put(key, value);
		}
	}

	public static void bindNutritionalValue(final Context context, Details details, TextView txtDetails) {
		final String text = getNutritionalString(context, details);
		final boolean notEmpty = !TextUtils.isEmpty(text);
		ViewUtils.setVisible(txtDetails, notEmpty);
		txtDetails.setText(notEmpty ? text : StringUtils.EMPTY_STRING);
	}

	private static String getNutritionalString(final Context context, final Details details) {
		if(details == null) {
			return StringUtils.EMPTY_STRING;
		}
		final StringBuilder sb = new StringBuilder();
		final SparseIntArray sparseArray = new SparseIntArray();
		putValue(sparseArray, R.string.dish_nutritional_calories, details.energy_100());
		putValue(sparseArray, R.string.dish_nutritional_fat, details.fat_100());
		putValue(sparseArray, R.string.dish_nutritional_carbohydrates, details.carbohydrate_100());
		putValue(sparseArray, R.string.dish_nutritional_protein, details.protein_100());

		processSparseData(context, sb, sparseArray, R.string.dish_nutritional_title_100);

		sparseArray.clear();
		putValue(sparseArray, R.string.dish_nutritional_calories, details.energyTotal());
		putValue(sparseArray, R.string.dish_nutritional_fat, details.fatTotal());
		putValue(sparseArray, R.string.dish_nutritional_carbohydrates, details.carbohydrateTotal());
		putValue(sparseArray, R.string.dish_nutritional_protein, details.proteinTotal());

		processSparseData(context, sb, sparseArray, R.string.dish_nutritional_title_portion);

		return sb.toString();
	}

	private static void processSparseData(final Context context, final StringBuilder sb, final SparseIntArray data, int titleId) {
		if(data.size() > 0) {
			if(sb.length() > 0) {
				sb.append(StringUtils.NEXT_STRING + StringUtils.NEXT_STRING);
			}
			sb.append(context.getString(titleId));
			sb.append(StringUtils.NEXT_STRING);
			writeToBuffer(context, sb, data);
		}
	}

	private static void writeToBuffer(final Context context, final StringBuilder sb, final SparseIntArray sparse100) {
		for(int i = 0; i < sparse100.size(); i++) {
			final int resId = sparse100.keyAt(i);
			final int value = sparse100.get(resId);
			if(i > 0) {
				sb.append(ENERGY_DELIMITER);
			}
			sb.append(context.getString(resId, value));
		}
	}

	public static void bindDetails(final Context context, Details details, TextView txtDetails, boolean large) {
		final boolean hasDetails = details != null;
		StringBuilder sb = new StringBuilder();
		if(hasDetails) {
			if(details.volume() > 0) {
				sb.append(context.getString(R.string.dish_details_volume, details.volume()));
			} else if(details.weight() > 0) {
				sb.append(context.getString(R.string.dish_details_weight, details.weight()));
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
