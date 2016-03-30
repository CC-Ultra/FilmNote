package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Parsers.AsyncParser;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;

/**
 * Created by User on 16.02.2016.
 */
public class WebActivity extends GlobalMenuOptions implements WebTaskComleteListener
	{
	 private boolean loading;
	 private ImageButton reloadButton;
	 private WebView webView;
	 private Spinner siteList;
	 private ProgressBar progressBar;
	 private int spinnerPosition=0;
	 private int contentType=0;
	 private int action=0;
	 private int dbPosition=0;
	 private String[] siteListSrc= {O.web.filmix.HOST_FULL, O.web.seasonvar.HOST_FULL, O.web.kinogo.HOST_FULL, O.web.onlineLife.HOST_FULL};

	 private class AcceptButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 String resultWebSrc;
			 resultWebSrc= webView.getUrl();
			 AsyncParser parser=null;
			 spinnerPosition= nowSelected(resultWebSrc);
			 switch(spinnerPosition)
				{
				 case O.web.filmix.ID:
					 parser= new Parser_Filmix(WebActivity.this, WebActivity.this, resultWebSrc);
					 break;
				 case O.web.seasonvar.ID:
					 parser= new Parser_Seasonvar(WebActivity.this, WebActivity.this, resultWebSrc);
					 break;
				 case O.web.kinogo.ID:
					 parser= new Parser_Kinogo(WebActivity.this, WebActivity.this, resultWebSrc);
					 break;
				 case O.web.onlineLife.ID:
					 parser= new Parser_OnlineLife(WebActivity.this, WebActivity.this, resultWebSrc);
				 }
			 parser.execute();
			 }
		 }
	 private class NavigationButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 switch(v.getId() )
				{
				 case R.id.webBack:
					 if(webView.canGoBack() )
						 webView.goBack();
					 break;
				 case R.id.webForward:
					 if(webView.canGoForward() )
						 webView.goForward();
					 break;
				 case R.id.webReload:
					 if(loading)
						{
						 loading=false;
						 reloadButton.setImageResource(R.drawable.web_reload);
						 webView.stopLoading();
						 progressBar.setProgress(0);
						 }
					 else
						{
						 loading=true;
						 reloadButton.setImageResource(R.drawable.web_cancel);
						 webView.reload();
						 }
				 }
			 }
		 }
	 private class SiteListOnItemSelectedListener implements AdapterView.OnItemSelectedListener
		{
		 @Override
		 public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
			 String host;
			 host= siteList.getItemAtPosition(position).toString();
			 spinnerPosition= position;
			 webView.getSettings().setJavaScriptEnabled(true);
			 webView.loadUrl(host);
			 }
		 @Override
		 public void onNothingSelected(AdapterView<?> arg0) {}
		 }
	 private class WebClient extends WebViewClient
		{
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

	 private void toOffline()
		{
		 finish();
		 Intent jumper= new Intent(this,AddActivity.class);
		 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
		 startActivity(jumper);
		 }
	 private int nowSelected(String url)
		{
		 for(int i=0; i<siteListSrc.length; i++)
			 if(url.contains(siteListSrc[i] ) )
				 return i;
		 return 0;
		 }
	 private void initAdapter(int selected)
		{
		 ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,siteListSrc);
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 siteList.setPrompt("Список сайтов");
		 siteList.setAdapter(adapter);
		 siteList.setSelection(selected);
		 }
	 @Override
	 public void useParserResult(Record_Serial extractedData)
		{
		 ParserResultConsumer.useParserResult(this,extractedData,action,contentType,dbPosition);
		 finish();
		 }
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.web_menu, menu);
		 }
	 @Override
	 protected void putIntentExtra(Intent reset)
		{
		 reset.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
		 reset.putExtra(O.mapKeys.extra.POSITION, dbPosition);
		 reset.putExtra(O.mapKeys.extra.ACTION, action);
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.web_add_layout);
		 Intent intent= getIntent();
		 contentType= intent.getIntExtra(O.mapKeys.extra.CONTENT_TYPE, -1);
		 action= intent.getIntExtra(O.mapKeys.extra.ACTION, -1);
		 dbPosition= intent.getIntExtra(O.mapKeys.extra.POSITION, -1);

		 Button acceptButton;
		 ImageButton backButton;
		 ImageButton forwardButton;
		 siteList= (Spinner) findViewById(R.id.siteList);
		 acceptButton= (Button)findViewById(R.id.acceptButton);
		 reloadButton= (ImageButton)findViewById(R.id.webReload);
		 backButton= (ImageButton)findViewById(R.id.webBack);
		 forwardButton= (ImageButton)findViewById(R.id.webForward);
		 progressBar = (ProgressBar) findViewById(R.id.progressBar);
		 webView= (WebView) findViewById(R.id.webView);

		 reloadButton.setOnClickListener(new NavigationButtonListener());
		 webView.setWebViewClient(new WebClient() );
		 acceptButton.setOnClickListener(new AcceptButtonListener());
		 backButton.setOnClickListener(new NavigationButtonListener());
		 forwardButton.setOnClickListener(new NavigationButtonListener() );
		 initAdapter(0);
		 siteList.setOnItemSelectedListener(new SiteListOnItemSelectedListener());
		 }
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 int id = item.getItemId();
		 switch(id)
			{
			 case R.id.menu_convert:
				 toOffline();
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
