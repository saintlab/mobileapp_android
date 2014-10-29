package com.omnom.android.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.adapter.CardsAdapter;
import com.omnom.android.restaurateur.api.observable.RestaurateurObeservableApi;
import com.omnom.android.restaurateur.model.cards.Card;
import com.omnom.android.restaurateur.model.cards.CardsResponse;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.preferences.PreferenceProvider;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.StringUtils;
import com.omnom.android.view.LoginPanelTop;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class CardsActivity extends BaseOmnomActivity implements CardsAdapter.AnimationEndListener {

	@SuppressLint("NewApi")
	public static void start(final Context activity, final double amount, final int accentColor) {
		final Intent intent = new Intent(activity, CardsActivity.class);
		intent.putExtra(Extras.EXTRA_ORDER_AMOUNT_TEXT, amount);
		intent.putExtra(Extras.EXTRA_ACCENT_COLOR, accentColor);

		if(AndroidUtils.isJellyBean()) {
			Bundle extras = ActivityOptions.makeCustomAnimation(activity,
			                                                    R.anim.slide_in_up,
			                                                    R.anim.fake_fade_out_long).toBundle();
			activity.startActivity(intent, extras);
		} else {
			activity.startActivity(intent);
		}
	}

	@Inject
	protected RestaurateurObeservableApi api;

	@InjectView(R.id.panel_top)
	protected LoginPanelTop mPanelTop;

	@InjectView(R.id.btn_pay)
	protected Button mBtnPay;

	@InjectView(R.id.list)
	protected ListView mList;

	private double mAmount;

	private int mAccentColor;

	private Subscription mCardsSubscription;

	private PreferenceProvider mPreferences;

	private ValueAnimator dividerAnimation;

	@Override
	public void initUi() {
		mPreferences = OmnomApplication.get(getActivity()).getPreferences();

		initDividerAnimation();

		mPanelTop.setButtonLeft(R.string.cancel, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				finish();
			}
		});
		mPanelTop.setButtonRight(R.string.add_card, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onAdd();
			}
		});

		mPanelTop.showProgress(true);
		mPanelTop.showButtonRight(false);

		mCardsSubscription = AndroidObservable.bindActivity(this, api.getCards().delaySubscription(1000, TimeUnit.MILLISECONDS)).subscribe(
				new Action1<CardsResponse>() {
					@Override
					public void call(final CardsResponse cards) {
						mList.setAdapter(new CardsAdapter(getActivity(), cards.getCards(), CardsActivity.this));
						mPanelTop.showProgress(false);
						mPanelTop.showButtonRight(true);
					}
				}, new Action1<Throwable>() {
					@Override
					public void call(final Throwable throwable) {
						mPanelTop.showProgress(false);
						mPanelTop.showButtonRight(true);
					}
				});

		final String text = StringUtils.formatCurrency(mAmount) + getString(R.string.currency_ruble);
		mBtnPay.setText(getString(R.string.pay_amount, text));

		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				CardsAdapter adapter = (CardsAdapter) mList.getAdapter();
				Card card = (Card) adapter.getItem(position);
				mPreferences.setCardId(getActivity(), card.getExternalCardId());
				adapter.notifyDataSetChanged();
			}
		});

		GradientDrawable sd = (GradientDrawable) mBtnPay.getBackground();
		sd.setColor(mAccentColor);
		sd.invalidateSelf();
	}

	private void initDividerAnimation() {
		final ColorDrawable dividerDrawable = new ColorDrawable(getResources().getColor(R.color.profile_hint));
		mList.setDivider(dividerDrawable);
		dividerAnimation = ValueAnimator.ofInt(0, 255);
		dividerAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				Integer animatedValue = (Integer) animation.getAnimatedValue();
				dividerDrawable.setAlpha(animatedValue);
				mList.setDividerHeight(1);
			}
		});
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_cards;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.fake_fade_out_long, R.anim.slide_out_down);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OmnomObservable.unsubscribe(mCardsSubscription);
	}

	public void onAdd() {
		AddCardActivity.start(this);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		mAmount = intent.getDoubleExtra(Extras.EXTRA_ORDER_AMOUNT_TEXT, 0);
		mAccentColor = intent.getIntExtra(Extras.EXTRA_ACCENT_COLOR, Color.WHITE);
	}

	@Override
	public void onAnimationEnd() {
		dividerAnimation.setDuration(1000);
		dividerAnimation.start();
	}
}
