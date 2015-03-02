package com.omnom.android.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.activity.OmnomActivity;

import butterknife.InjectView;

/**
 * Created by Ch3D on 02.03.2015.
 */
public class WebActivity extends BaseOmnomActivity {

	public static void start(OmnomActivity context, final String url) {
		final Intent intent = new Intent(context.getActivity(), WebActivity.class);
		intent.putExtra(EXTRA_URI, url);
		context.start(intent, R.anim.slide_in_up, R.anim.slide_out_down, false);
	}

	@InjectView(R.id.web_view)
	protected WebView mWebView;

	private String mUriString;

	@Override
	public void initUi() {
		if(TextUtils.isEmpty(mUriString)) {
			finish();
			return;
		}
		mWebView.clearCache(true);
		mWebView.loadUrl(mUriString);
	}

	@Override
	protected void handleIntent(final Intent intent) {
		super.handleIntent(intent);
		mUriString = intent.getStringExtra(EXTRA_URI);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_web;
	}
}
