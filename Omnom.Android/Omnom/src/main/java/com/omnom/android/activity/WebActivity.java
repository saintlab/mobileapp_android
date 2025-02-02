package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
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

	public static void start(OmnomActivity context, final String url) {
		context.start(createIntent(context.getActivity(), url), R.anim.slide_in_up, R.anim.nothing, false);
	}

	public static Intent createIntent(Context context, final String url) {
		final Intent intent = new Intent(context, WebActivity.class);
		intent.putExtra(EXTRA_URI, url);
		return intent;
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
		mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		mWebView.getSettings().setJavaScriptEnabled(true);
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
