package com.omnom.android.fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.omnom.android.R;

import static butterknife.ButterKnife.findById;

public class EnteringPageFragment extends Fragment {
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
		return inflater.inflate(R.layout.fragment_entering_page, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Drawable drawable = getResources().getDrawable(R.drawable.bg_wood);
		drawable.mutate();
		drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
		findById(view, R.id.root).setBackgroundDrawable(drawable);
		drawable.invalidateSelf();
		((ImageView) findById(view, R.id.img_icon)).setImageResource(icon);
		((TextView) findById(view, R.id.text)).setText(strId);
	}
}
