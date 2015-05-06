package com.omnom.android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.omnom.android.R;
import com.omnom.android.utils.activity.OmnomActivity;

/**
 * Created by Ch3D on 21.04.2015.
 */
public class ThreeDSWebActivity extends WebActivity {

	public static final String PATH_API_PAGE_RESULT = "api/page/result";

	public static final String QUERY_PARAM_SUCCESS = "Success";

	private class ThreeDSWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			if(url.contains(PATH_API_PAGE_RESULT)) {
				final Uri uri = Uri.parse(url);
				final String strSuccess = uri.getQueryParameter(QUERY_PARAM_SUCCESS);
				if(Boolean.TRUE.toString().equalsIgnoreCase(strSuccess)) {
					setResult(RESULT_OK);
				} else {
					setResult(RESULT_CANCELED, new Intent().setData(uri));
				}
				finish();
				return true;
			}
			return false;
		}
	}

	public static void start(OmnomActivity context, final String url, final int requestCode) {
		context.startForResult(createIntent(context.getActivity(), url), R.anim.slide_in_up, R.anim.nothing, requestCode);
	}

	public static Intent createIntent(Context context, final String url) {
		final Intent intent = new Intent(context, ThreeDSWebActivity.class);
		intent.putExtra(EXTRA_URI, url);
		return intent;
	}

	@Override
	public void initUi() {
		super.initUi();
		mWebView.setWebViewClient(new ThreeDSWebViewClient());
	}
}
