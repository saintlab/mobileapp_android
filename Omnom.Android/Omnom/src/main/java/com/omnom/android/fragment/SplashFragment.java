package com.omnom.android.fragment;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.activity.EnteringActivity;
import com.omnom.android.activity.ValidateActivity;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.AnimationBuilder;
import com.omnom.android.utils.utils.AnimationUtils;
import com.omnom.android.utils.view.MultiplyImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.omnom.android.utils.Extras.EXTRA_LOADER_ANIMATION_SCALE_DOWN;

/**
 * Created by mvpotter on 12/3/2014.
 */
public class SplashFragment extends Fragment {

	/**
	 * Listener for launch events.
	 */
	public interface LaunchListener {
		void launchEnteringScreen();
	}

	public static final float SCALE_FACTOR_FORK_LARGE = 0.4258f; // 104px / 178px = small_fork / large_fork

	public static final float SCALE_FACTOR_FORK_SMALL = 1 / SCALE_FACTOR_FORK_LARGE;

	private static final String ARG_DURATION_SPLASH = "durationSplash";

	public static SplashFragment newInstance(final int durationSplash) {
		final SplashFragment fragment = new SplashFragment();
		final Bundle args = new Bundle();
		args.putInt(ARG_DURATION_SPLASH, durationSplash);
		fragment.setArguments(args);
		return fragment;
	}

	@InjectView(R.id.img_logo)
	protected ImageView imgLogo;

	@InjectView(R.id.img_fork)
	protected ImageView imgFork;

	@InjectView(R.id.img_fork_large)
	protected ImageView imgForkLarge;

	@InjectView(R.id.img_bill)
	protected ImageView imgBill;

	@InjectView(R.id.img_ring)
	protected ImageView imgRing;

	@InjectView(R.id.img_cards)
	protected ImageView imgCards;

	@InjectView(R.id.img_multiply)
	protected MultiplyImageView imgMultiply;

	@InjectView(R.id.img_bg)
	protected ImageView imgBackground;

	private boolean mAnimate = true;

	private int durationSplash;

	private LaunchListener mLaunchListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			durationSplash = getArguments().getInt(ARG_DURATION_SPLASH, getResources().getInteger(R.integer.splash_screen_timeout));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mLaunchListener = (LaunchListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement LaunchListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_splash, container, false);

		ButterKnife.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		imgForkLarge.setScaleX(0.4258f);
		imgForkLarge.setScaleY(0.4258f);

		imgMultiply.setRadius(getResources().getDimensionPixelSize(R.dimen.loader_size_huge) / 2);
	}

	public void animateValidation() {
		if(!mAnimate) {
			return;
		}

		final int durationShort = getResources().getInteger(R.integer.default_animation_duration_short);
		final int animationDuration = getResources().getInteger(R.integer.splash_animation_duration);
		final int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.loader_logo_size);
		final EnteringActivity activity = (EnteringActivity) getActivity();

		activity.findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				AnimationUtils.animateAlpha(imgBill, false, durationShort);
				AnimationUtils.animateAlpha(imgCards, false, durationShort);
				AnimationUtils.animateAlpha(imgLogo, false, durationShort);
				AnimationUtils.animateAlpha(imgRing, false, durationShort);
				animateMultiply();
				// transitionDrawable.startTransition(durationShort);

				// AnimationUtils.scaleWidth(imgFork, dimensionPixelSize, durationShort, null);
				activity.findViewById(android.R.id.content).postDelayed(new Runnable() {
					@Override
					public void run() {
						if(isAdded() && !activity.isFinishing()) {
							ValidateActivity.start(activity, R.anim.fake_fade_in, R.anim.fake_fade_out_instant,
							                       EXTRA_LOADER_ANIMATION_SCALE_DOWN, activity.getType());
						}
					}
				}, animationDuration);
			}
		}, durationSplash);
		if(isAdded()) {
			activity.getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		}
		mAnimate = false;
	}

	/**
	 * Animate of fork_n_knife logo and loader
	 */
	private void animateMultiply() {
		if(!isAdded()) {
			return;
		}
		final float upperLogoPoint = getResources().getDimension(R.dimen.loader_margin_top);
		final int animationDuration = getResources().getInteger(R.integer.splash_animation_duration);

		// translating up animation
		final AnimationBuilder translationSmallBuilder = AnimationBuilder.create(getActivity(), 0, (int) upperLogoPoint);
		translationSmallBuilder.setDuration(animationDuration);
		final List<View> views = new ArrayList<View>();
		views.add(imgFork);
		views.add(imgForkLarge);
		final ValueAnimator translationAnimator = AnimationUtils.prepareTranslation(views, null, translationSmallBuilder);
		translationAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgMultiply.setOffset(-(Integer) animation.getAnimatedValue());
			}
		});

		// loader/circle downscaling animation
		final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		float end = (int) (displayMetrics.widthPixels * LoaderView.LOADER_WIDTH_SCALE + 0.5);
		float start = getResources().getDimension(R.dimen.loader_size_huge);
		AnimationBuilder builder = AnimationBuilder.create(getActivity(), (int) start, (int) end);
		builder.setDuration(getResources().getInteger(R.integer.splash_animation_duration));
		builder.addListener(new AnimationBuilder.UpdateLisetener() {
			@Override
			public void invoke(ValueAnimator animation) {
				imgMultiply.setRadius((Integer) animation.getAnimatedValue() / 2);
				imgMultiply.invalidate();
			}
		});
		final ValueAnimator multiplyAnimator = builder.build();

		// main color animation
		ValueAnimator alphaAnimator = ValueAnimator.ofInt(0, 255);
		alphaAnimator.setDuration(animationDuration);
		alphaAnimator.setInterpolator(new AccelerateInterpolator(2.2f));
		alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgMultiply.setFillAlpha((Integer) animation.getAnimatedValue());
			}
		});

		ValueAnimator alphaAnimtorForkSmall = ValueAnimator.ofFloat(1, 0);
		alphaAnimtorForkSmall.setDuration(animationDuration);
		alphaAnimtorForkSmall.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgFork.setAlpha((Float) animation.getAnimatedValue());
			}
		});

		ValueAnimator scaleAnimator = ValueAnimator.ofFloat(SCALE_FACTOR_FORK_LARGE, 1.0f);
		scaleAnimator.setDuration(animationDuration);
		scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float animatedValue = (Float) animation.getAnimatedValue();
				imgForkLarge.setScaleX(animatedValue);
				imgForkLarge.setScaleY(animatedValue);
				final float s = 1 + ((animatedValue - SCALE_FACTOR_FORK_LARGE) * SCALE_FACTOR_FORK_SMALL);
				imgFork.setScaleX(s);
				imgFork.setScaleY(s);
			}
		});

		//		imgForkLarge.animate().setDuration(animationDuration).translationXBy(ViewUtils.dipToPixels(this, 7)).start();
		//		imgFork.animate().setDuration(animationDuration).translationXBy(ViewUtils.dipToPixels(this, 7)).start();

		ValueAnimator alphaAnimtorForkLarge = ValueAnimator.ofFloat(0, 1);
		alphaAnimtorForkLarge.setDuration(animationDuration);
		alphaAnimtorForkLarge.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				imgForkLarge.setAlpha((Float) animation.getAnimatedValue());
			}
		});

		// simultaneous animation playback
		final AnimatorSet as = new AnimatorSet();
		as.playTogether(translationAnimator, alphaAnimator, alphaAnimtorForkSmall, alphaAnimtorForkLarge, multiplyAnimator, scaleAnimator);
		as.start();
	}

	public void animateLogin() {
		if(!mAnimate || !isAdded()) {
			return;
		}
		getActivity().findViewById(android.R.id.content).postDelayed(new Runnable() {
			@Override
			public void run() {
				mLaunchListener.launchEnteringScreen();

			}
		}, getResources().getInteger(R.integer.splash_screen_timeout));
		getActivity().getWindow().setBackgroundDrawableResource(R.drawable.bg_wood);
		mAnimate = false;
	}

}
