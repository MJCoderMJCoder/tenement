package com.lzf.tenement.fragment;

import com.lzf.tenement.R;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class FragmentRecommend extends Fragment {

	private WebView wView;
	private ProgressBar pBar;
	private String url;

	public FragmentRecommend(String url) {
		super();
		this.url = url;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recommend, container, false);

		pBar = (ProgressBar) view.findViewById(R.id.progressBar);
		wView = (WebView) view.findViewById(R.id.lzfWeb);
		wView.setInitialScale(5);
		WebSettings wSettings = wView.getSettings();
		wSettings.setJavaScriptEnabled(true);

		wSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		wSettings.setUseWideViewPort(true);// 设定支持viewport
		wSettings.setLoadWithOverviewMode(true); // 自适应屏幕
		// 设置支持缩放
		wSettings.setSupportZoom(true);
		// 设置缩放工具的显示
		wSettings.setBuiltInZoomControls(true);

		wView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				pBar.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				pBar.setVisibility(View.VISIBLE);
			}
		});
		wView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				pBar.setProgress(newProgress);
			}
		});
		wView.loadUrl(url);

		return view;
	}
}
