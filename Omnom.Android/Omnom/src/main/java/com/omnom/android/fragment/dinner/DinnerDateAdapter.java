package com.omnom.android.fragment.dinner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.omnom.android.utils.utils.DateUtils.DATE_FORMAT_DDMMYYYY;
import static com.omnom.android.utils.utils.DateUtils.getDayOfWeek;
import static com.omnom.android.utils.utils.DateUtils.isTomorrow;
import static com.omnom.android.utils.utils.DateUtils.parseDate;

/**
 * Created by Ch3D on 25.03.2015.
 */
public class DinnerDateAdapter extends DinnerDataAdapterBase<String, DinnerDateAdapter.ItemViewHolder> {

	public static class ItemViewHolder extends RecyclerView.ViewHolder {

		@InjectView(R.id.txt_title)
		protected TextView mTxtTitle;

		@InjectView(R.id.indicator)
		protected View mCheckedIndicator;

		public ItemViewHolder(final View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

	public DinnerDateAdapter(final Context context, ArrayList<String> data) {
		super(context, data);
	}

	@Override
	public DinnerDateAdapter.ItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		return new DinnerDateAdapter.ItemViewHolder(mInflater.inflate(R.layout.item_dinner_date, parent, false));
	}

	@Override
	public void onBindViewHolder(final DinnerDateAdapter.ItemViewHolder holder, final int position) {
		final String item = getItem(position);
		final Date date = parseDate(DATE_FORMAT_DDMMYYYY, item);

		if(date == null) {
			holder.mTxtTitle.setText(item);
			return;
		}

		String dayOfWeek = getDayOfWeek(date);
		final String firstLetter = new String(dayOfWeek.substring(0, 1));
		final String firstLetterUpper = firstLetter.toUpperCase();
		dayOfWeek = dayOfWeek.replace(firstLetter, firstLetterUpper);

		if(isTomorrow(date)) {
			holder.mTxtTitle.setText(mContext.getString(R.string.order_pick_date_related,
			                                            mContext.getString(R.string.Tomorrow),
			                                            dayOfWeek));
		} else {
			holder.mTxtTitle.setText(dayOfWeek);
		}

		ViewUtils.setVisible2(holder.mCheckedIndicator, isItemChecked(position));
	}
}
