package com.omnom.android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

		@InjectView(R.id.root)
		protected View root;

		@InjectView(R.id.divider)
		protected View divider;

		private ViewHolder(View convertView) {
			ButterKnife.inject(this, convertView);
		}
	}

	private final Context mContext;

	private final List<? extends Card> mCards;

	private final LayoutInflater mInflater;

	private int mAnimDuration;

	private int mAnimDelay;

	private Drawable mDrawableRight;

	private PreferenceProvider mPreferences;

	private int mColorSelected;

	private int mColorPanDefault;

	private int mColorTypeDefault;

	private int mColorPanUnregistered;

	private boolean mIsDemo;

	private int mLastAnimated = -1;

	private Card mSelectedItem;

	public CardsAdapter(final Context context, List<? extends Card> cards, boolean isDemo) {
		mContext = context;
		mIsDemo = isDemo;
		mInflater = LayoutInflater.from(context);
		mCards = cards;
		init(context);
	}

	private void init(final Context context) {
		mPreferences = OmnomApplication.get(mContext).getPreferences();
		mAnimDuration = context.getResources().getInteger(R.integer.default_animation_duration_short);
		mAnimDelay = context.getResources().getInteger(R.integer.listview_animation_delay);
		mDrawableRight = mContext.getResources().getDrawable(R.drawable.ic_card_active);
		mColorSelected = mContext.getResources().getColor(R.color.card_selected);
		mColorPanDefault = mContext.getResources().getColor(R.color.card_number_default);
		mColorTypeDefault = mContext.getResources().getColor(R.color.card_type_default);
		mColorPanUnregistered = mContext.getResources().getColor(R.color.card_unregistered);
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

	public Card getSelectedCard() {
		return mSelectedItem;
	}

	public void remove(final Card card) {
		mCards.remove(card);
		notifyDataSetChanged();
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

	@SuppressLint("NewApi")
	private void bindView(final View convertView, final Card item, final int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		boolean isAnimate = position > mLastAnimated;
		if(isAnimate) {
			holder.divider.setAlpha(0);
			convertView.setAlpha(0);
			convertView.setTranslationY(-20);
		}

		holder.txtCardNumber.setText(item.getMaskedPan());
		holder.txtType.setText(item.getAssociation());

		final String cardId = mPreferences.getCardId(mContext);
		final boolean isSelected = !TextUtils.isEmpty(cardId) && cardId.equals(item.getExternalCardId());

		if(isSelected || mIsDemo) {
			mSelectedItem = item;
			holder.root.setBackgroundColor(mColorSelected);
			holder.txtCardNumber.setTextColor(Color.WHITE);
			holder.txtType.setTextColor(Color.WHITE);
			holder.txtConfirm.setText(StringUtils.EMPTY_STRING);
			holder.txtConfirm.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawableRight, null);
		} else {
			holder.root.setBackgroundColor(Color.TRANSPARENT);
			holder.txtCardNumber.setTextColor(mColorPanDefault);
			holder.txtType.setTextColor(mColorTypeDefault);
			holder.txtConfirm.setText(StringUtils.EMPTY_STRING);
			holder.txtConfirm.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}

		if(item.isRegistered()) {
			holder.txtConfirm.setText(StringUtils.EMPTY_STRING);
		} else {
			holder.txtCardNumber.setTextColor(mColorPanUnregistered);
			holder.txtType.setTextColor(mColorPanUnregistered);
			holder.txtConfirm.setText(R.string.confirm);
			holder.txtConfirm.setCompoundDrawables(null, null, null, null);
		}

		if(isAnimate) {
			mLastAnimated = position;
			final boolean isLast = position == getCount() - 1;

			ViewPropertyAnimator viewPropertyAnimator = convertView.animate()
			                                                       .alpha(1)
			                                                       .translationY(0)
			                                                       .setDuration(mAnimDuration)
			                                                       .setStartDelay(mAnimDelay);
			// do not display divider for last item
			if(!isLast) {
				holder.divider.animate().alpha(1).setStartDelay(mAnimDelay * 2);
			}
			viewPropertyAnimator.start();
		}
	}
}
