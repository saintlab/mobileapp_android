package com.omnom.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.ValidateActivity;
import com.omnom.android.utils.Extras;
import com.omnom.android.utils.activity.BaseFragmentActivity;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ClickSpan;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

/**
 * Created by Ch3D on 06.01.2015.
 */
public class QrHintFragment extends Fragment {

	public static Fragment newInstance() {
		final QrHintFragment fragment = new QrHintFragment();
		return fragment;
	}

	@InjectView(R.id.panel_bottom)
	protected View panelBottom;

	@InjectView(R.id.txt_info)
	protected TextView txtInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_qr_hint, container, false);
		ButterKnife.inject(this, view);
		return view;
	}

	@OnClick(R.id.btn_close)
	protected void onClose() {
		getFragmentManager().popBackStack();
	}

	@Override
	public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		AndroidUtils.clickify(txtInfo, getString(R.string.qr_hint_mark), new ClickSpan.OnClickListener() {
			@Override
			public void onClick() {
                getActivity().getSupportFragmentManager().popBackStack();
                ValidateActivity.startDemo((BaseFragmentActivity) getActivity(),
                                           R.anim.fake_fade_in_instant,
                                           R.anim.fake_fade_out_instant,
                                           Extras.EXTRA_LOADER_ANIMATION_SCALE_DOWN);
			}
		});
		CalligraphyUtils.applyFontToTextView(getActivity(), txtInfo, "fonts/Futura-LSF-Omnom-LE-Regular.otf");
	}
}
