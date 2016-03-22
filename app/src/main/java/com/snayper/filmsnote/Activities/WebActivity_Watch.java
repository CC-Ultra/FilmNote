package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import com.snayper.filmsnote.R;

/**
 * Created by snayper on 21.03.2016.
 */
public class WebActivity_Watch extends AppCompatActivity
	{
	 WebView webView;
	 ProgressBar progressBar;
	 Boolean loading= new Boolean(true);

	 private void back()
		{
		 if(webView.canGoBack() )
			 webView.goBack();
		 }
	 private void forward()
		{
		 if(webView.canGoForward() )
			 webView.goForward();
		 }
	 private void stop()
		{
		 loading=false;
		 webView.stopLoading();
		 progressBar.setProgress(0);
		 }
	 private void reload()
		{
		 loading=true;
		 webView.reload();
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.web_watch_layout);
		 Intent intent= getIntent();
		 String url= intent.getStringExtra("Serial URL");

		 ImageButton button= (ImageButton)findViewById(R.id.webReload);
		 progressBar= (ProgressBar)findViewById(R.id.progressBar);
		 webView= (WebView)findViewById(R.id.webView);

		 webView.setWebChromeClient(new WebChromeClient() );
//		 webView.setWebViewClient(new WebClient(button,progressBar,loading) );
		 webView.getSettings().setJavaScriptEnabled(true);
		 webView.loadUrl(url);
		 }
	 }
