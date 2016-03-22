package com.snayper.filmsnote.Activities;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import com.snayper.filmsnote.R;

/**
 * Created by snayper on 21.03.2016.
 */
public class WebClient extends WebViewClient
	{
	 ImageButton reloadButton;
	 ProgressBar progressBar;
	 Boolean loading;

	 public WebClient(ImageButton _reloadButton,ProgressBar _progressBar,Boolean _loading)
		{
		 reloadButton=_reloadButton;
		 progressBar=_progressBar;
		 loading=_loading;
		 }
	 @Override
	 public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
		 view.loadUrl(url);
		 return true;
		 }
	 @Override
	 public void onPageFinished(WebView view, String url)
		{
		 loading=false;
		 reloadButton.setImageResource(R.drawable.web_reload);
		 progressBar.setProgress(100);
		 super.onPageFinished(view, url);
		 }

	 @Override
	 public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
		 loading=true;
		 reloadButton.setImageResource(R.drawable.web_cancel);
		 progressBar.setProgress(50);
		 super.onPageStarted(view,url,favicon);
		 }
	 }
