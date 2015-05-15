package com.omnom.android.utils.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static com.omnom.android.utils.utils.AnimationUtils.animateAlpha;
import static com.omnom.android.utils.utils.AnimationUtils.animateAlpha2;
import static com.omnom.android.utils.utils.AnimationUtils.animateAlpha3;

/**
 * Created by Ch3D on 29.07.2014.
 */
public class ViewUtils {

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			view.setVisibility(value ? View.VISIBLE : View.GONE);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY2 = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			view.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY_ALPHA = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			animateAlpha(view, value);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY_ALPHA2 = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			animateAlpha2(view, value);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY_ALPHA3 = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			animateAlpha3(view, value);
		}
	};

	public static final ButterKnife.Setter<View, Boolean> VISIBLITY_ALPHA_NOW = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			if(view != null) {
				view.setAlpha(value ? 1 : 0);
				setVisibleGone(view, value);
			}
		}
	};

	public static final ButterKnife.Setter<View, Boolean> ENABLED = new ButterKnife.Setter<View, Boolean>() {
		@Override
		public void set(View view, Boolean value, int index) {
			view.setEnabled(value);
		}
	};

	private static final String TAG = ViewUtils.class.getSimpleName();

	public static int dipToPixels(final Context context, final float dips) {
		final float scale = context.getResources().getDisplayMetrics().density;
		final int paddingInPixels = (int) ((dips * scale) + 0.5f);
		return paddingInPixels;
	}

	public static boolean isVisible(View view) {
		return view.getVisibility() == View.VISIBLE;
	}

	public static void setMargins(View v, int l, int t, int r, int b) {
		if(v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
			p.setMargins(l, t, r, b);
			v.requestLayout();
		}
	}

	public static void setVisibleGone(View view, boolean visible) {
		if(view != null) {
			view.setVisibility(visible ? View.VISIBLE : View.GONE);
			view.setTag(visible);
		}
	}

	public static void setVisibleInvisible(View view, boolean visible) {
		if(view != null) {
			view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
			view.setTag(visible);
		}
	}

	public static void setVisibleGone(List<View> buttonViews, boolean visible) {
		ButterKnife.apply(buttonViews, VISIBLITY, visible);
	}

	public static void fixPasswordTypeface(EditText editText, EditText like) {
		editText.setTypeface(like.getTypeface());
	}

	public static String getTextValue(EditText view) {
		return view.getText().toString().trim();
	}

	public static void setHeight(final View view, final int value) {
		if(view == null || view.getLayoutParams() == null) {
			return;
		}
		view.getLayoutParams().height = value;
		view.requestLayout();
	}

	/**
	 * Checks if views are intersect. <br/>
	 *
	 * @param v1 first view
	 * @param v2 second view
	 * @return true if intersect
	 */
	public static boolean intersect(final View v1, final View v2) {
		Rect rect1 = new Rect(v1.getLeft(), v1.getTop(), v1.getRight(), v1.getBottom());
		Rect rect2 = new Rect(v2.getLeft(), v2.getTop(), v2.getRight(), v2.getBottom());
		return Rect.intersects(rect1, rect2);
	}

	public static ArrayList<View> getChildViews(final ViewGroup container) {
		final ArrayList<View> result = new ArrayList<View>();
		final int childCount = container.getChildCount();

		for(int i = 0; i < childCount; i++) {
			final View childAt = container.getChildAt(i);
			result.add(childAt);
		}
		return result;
	}

	public static ArrayList<View> getChilds(final ViewGroup container, final ViewFilter filter) {
		final ArrayList<View> result = new ArrayList<View>();
		final int childCount = container.getChildCount();

		for(int i = 0; i < childCount; i++) {
			final View childAt = container.getChildAt(i);
			if(filter.filter(childAt)) {
				result.add(childAt);
			}
		}
		return result;
	}

	public static void removeChilds(final LinearLayout container, final ViewFilter filter) {
		for(final View view : getChildViews(container)) {
			if(filter.filter(view)) {
				container.removeView(view);
			}
		}
	}

	public static void setDrawableColor(final GradientDrawable sd, final int color) {
		sd.setColor(color);
		sd.invalidateSelf();
	}

	public static void setBackgroundDrawableColor(final View view, final int color) {
		final GradientDrawable drawable = (GradientDrawable) view.getBackground();
		if(drawable == null) {
			Log.d(TAG, "Unable to set drawable color for view with id = " + view.getId());
			return;
		}
		setDrawableColor(drawable, color);
	}
}
