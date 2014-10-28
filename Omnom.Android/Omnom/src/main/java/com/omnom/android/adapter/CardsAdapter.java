package com.omnom.android.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.restaurateur.model.cards.Card;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.StringUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Ch3D on 27.10.2014.
 */
public class CardsAdapter extends BaseAdapter {

	static class ViewHolder {
		@InjectView(R.id.txt_card_number)
		protected TextView txtCardNumber;

		@InjectView(R.id.txt_type)
		protected TextView txtType;

		@InjectView(R.id.txt_confirm)
		protected TextView txtConfirm;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private final Context mContext;

	private final List<Card> mCards;

	private final LayoutInflater mInflater;

	private final int mAnimDuration;

	private final int mAnimDelay;

	private final Drawable mDrawableRight;

	private final Gson mGson;

	private final PreferenceProvider mPreferences;

	private int mLastAnimated = -1;

	public CardsAdapter(final Context context, List<Card> cards) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mCards = cards;
		mAnimDuration = context.getResources().getInteger(R.integer.default_animation_duration_short);
		mAnimDelay = context.getResources().getInteger(R.integer.listview_animation_delay);
		mDrawableRight = mContext.getResources().getDrawable(
				R.drawable.selected_card_icon_blue);
		mGson = new Gson();
		mPreferences = OmnomApplication.get(mContext).getPreferences();
	}

	@Override
	public int getCount() {
		return mCards.size();
	}

	@Override
	public Object getItem(final int position) {
		return mCards.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.item_card, parent, false);
			ViewHolder holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		bindView(convertView, (Card) getItem(position), position);
		return convertView;
	}

	private void bindView(final View convertView, final Card item, final int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		boolean isAnimate = position > mLastAnimated;
		if(isAnimate) {
			convertView.setAlpha(0);
			convertView.setTranslationY(-20);
		}

		holder.txtCardNumber.setText(item.getMaskedPan());
		holder.txtType.setText(item.getAssociation());

		final String cardId = mPreferences.getCardId(mContext);
		final boolean isSelected = !TextUtils.isEmpty(cardId) && cardId.equals(item.getExternalCardId());

		if(isSelected) {
			holder.txtCardNumber.setTextColor(mContext.getResources().getColor(R.color.card_number_selected));
			holder.txtType.setTextColor(mContext.getResources().getColor(R.color.card_type_selected));
			holder.txtConfirm.setText(StringUtils.EMPTY_STRING);
			holder.txtConfirm.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawableRight, null);
		} else {
			holder.txtCardNumber.setTextColor(mContext.getResources().getColor(R.color.card_number_default));
			holder.txtType.setTextColor(mContext.getResources().getColor(R.color.card_type_default));
			holder.txtConfirm.setText(StringUtils.EMPTY_STRING);
			holder.txtConfirm.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}

		if(item.isRegistered()) {
			holder.txtConfirm.setText(StringUtils.EMPTY_STRING);
		} else {
			holder.txtConfirm.setText("Подтвердить");
			holder.txtConfirm.setCompoundDrawables(null, null, null, null);
		}

		if(isAnimate) {
			mLastAnimated = position;
			convertView.animate().alpha(1).translationY(0).setDuration(mAnimDuration).setStartDelay(mAnimDelay).start();
		}
	}
}
