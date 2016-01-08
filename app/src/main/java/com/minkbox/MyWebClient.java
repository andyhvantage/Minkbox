package com.minkbox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.minkbox.utils.DialogManager;

public class MyWebClient extends WebViewClient {
	
	private Activity activity;
	DialogManager dm = new DialogManager();
	
	public MyWebClient(Activity activity) {
			this.activity = activity;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		//dm.showProcessDialog(activity, "wait...");
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		//dm.stopProcessDialog();
	}
 }
