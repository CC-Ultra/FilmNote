package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;
import com.snayper.filmsnote.Parsers.AsyncParser;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;

/**
 * Created by User on 16.02.2016.
 */
public class WebActivity_Add extends AppCompatActivity implements WebTaskComleteListener
	{
	 private Boolean loading= new Boolean(true);
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
					 parser= new Parser_Filmix(WebActivity_Add.this, WebActivity_Add.this, resultWebSrc);
					 break;
				 case O.web.seasonvar.ID:
					 parser= new Parser_Seasonvar(WebActivity_Add.this, WebActivity_Add.this, resultWebSrc);
					 break;
				 case O.web.kinogo.ID:
					 parser= new Parser_Kinogo(WebActivity_Add.this, WebActivity_Add.this, resultWebSrc);
					 break;
				 case O.web.onlineLife.ID:
					 parser= new Parser_OnlineLife(WebActivity_Add.this, WebActivity_Add.this, resultWebSrc);
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

	 private void toOffline()
		{
		 finish();
		 Intent jumper= new Intent(this,AddActivity.class);
		 jumper.putExtra("Content type",contentType);
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
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.web_add_layout);
		 Intent intent= getIntent();
		 contentType= intent.getIntExtra("Content type",-1);
		 action= intent.getIntExtra("Action",-1);
		 dbPosition= intent.getIntExtra("Position",-1);

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
		 webView.setWebViewClient(new WebClient(reloadButton,progressBar,loading) );
		 acceptButton.setOnClickListener(new AcceptButtonListener());
		 backButton.setOnClickListener(new NavigationButtonListener());
		 forwardButton.setOnClickListener(new NavigationButtonListener() );
		 initAdapter(0);
		 siteList.setOnItemSelectedListener(new SiteListOnItemSelectedListener());
		 }
	 }
