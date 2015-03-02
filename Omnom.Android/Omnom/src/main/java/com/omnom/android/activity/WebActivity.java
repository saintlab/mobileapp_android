package com.omnom.android.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.utils.activity.OmnomActivity;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Ch3D on 02.03.2015.
 */
public class WebActivity extends BaseOmnomActivity {

	public static final String SCHEME_PREFIX_HTTP = "http://";

	public static void start(OmnomActivity context, final String url) {
		final Intent intent = new Intent(context.getActivity(), WebActivity.class);
		intent.putExtra(EXTRA_URI, url);
		context.start(intent, R.anim.slide_in_up, R.anim.nothing, false);
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
		if(!mUriString.startsWith(SCHEME_PREFIX_HTTP)) {
			mUriString = SCHEME_PREFIX_HTTP + mUriString;
		}
		mWebView.loadUrl(mUriString);
	}

	@OnClick(R.id.btn_close)
	public void onClose() {
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.nothing, R.anim.slide_out_down);
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
