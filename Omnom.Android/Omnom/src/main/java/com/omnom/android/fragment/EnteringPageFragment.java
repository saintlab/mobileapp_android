package com.omnom.android.fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.fragment.base.BaseFragment;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.loader.LoaderView;
import com.omnom.android.utils.utils.AndroidUtils;

import static butterknife.ButterKnife.findById;

public class EnteringPageFragment extends BaseFragment {
	private static final String ARG_COLOR = "color";

	private static final String ARG_ICON = "icon";

	private static final String ARG_TEXT = "text";

	public static EnteringPageFragment newInstance(int color, int resId, final int strId) {
		EnteringPageFragment fragment = new EnteringPageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COLOR, color);
		args.putInt(ARG_ICON, resId);
		args.putInt(ARG_TEXT, strId);
		fragment.setArguments(args);
		return fragment;
	}

	private int color;

	private int icon;

	private int strId;

	public EnteringPageFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {
			color = getArguments().getInt(ARG_COLOR);
			icon = getArguments().getInt(ARG_ICON);
			strId = getArguments().getInt(ARG_TEXT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_entering_page, container, false);
		ViewGroup.LayoutParams layoutParams = findById(view, R.id.img_bg).getLayoutParams();
		final DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		final int loaderSize = (int) (displayMetrics.widthPixels * LoaderView.LOADER_WIDTH_SCALE + 0.5);
		layoutParams.height = loaderSize;
		layoutParams.width = loaderSize;
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Drawable drawable = getResources().getDrawable(R.drawable.bg_wood);
		drawable.mutate();
		drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		AndroidUtils.setBackground(findById(view, R.id.root), drawable);
		AndroidUtils.applyFont(getActivity(), (TextView) findById(view, R.id.text), OmnomFont.OSF_REGULAR);
		drawable.invalidateSelf();
		((ImageView) findById(view, R.id.img_icon)).setImageResource(icon);
		((TextView) findById(view, R.id.text)).setText(strId);
	}
}
