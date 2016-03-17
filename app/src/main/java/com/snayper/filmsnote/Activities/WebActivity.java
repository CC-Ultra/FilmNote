package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.snayper.filmsnote.Parsers.AsyncParser;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.Utils.FileManager;
import com.snayper.filmsnote.Utils.Record_Serial;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;
import java.util.HashMap;

/**
 * Created by User on 16.02.2016.
 */
public class WebActivity extends AppCompatActivity implements WebTaskComleteListener
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
		 if( (extractedData!=null) && (extractedData.getTitle().length()!=0) )
			{
			 extractedData.setImgSrc(FileManager.getFilenameFromURL(extractedData.getImgSrc()));
			 if( FileManager.getStoredPicURI(this, extractedData.getImgSrc() ).length() == 0)
				 extractedData.setImgSrc("");
			 if(action == O.interaction.WEB_ACTION_ADD)
				 DbHelper.putRecord_Serial(extractedData,contentType);
			 if(action == O.interaction.WEB_ACTION_UPDATE)
				{
				 Record_Serial dbRecord= DbHelper.extractRecord_Serial(contentType,dbPosition);
				 HashMap<String,Object> data= new HashMap<>();
				 dbRecord.setTitle( extractedData.getTitle() );
				 data.put(O.db.FIELD_NAME_TITLE,dbRecord.getTitle());
				 if(extractedData.getAll()!=0)
					 dbRecord.setAll( extractedData.getAll() );
				 data.put(O.db.FIELD_NAME_ALL,dbRecord.getAll());
				 if(extractedData.getAll() < dbRecord.getWatched() )
					{
					 dbRecord.setWatched( extractedData.getAll() );
					 data.put(O.db.FIELD_NAME_WATCHED,dbRecord.getWatched() );
					 }
				 if(extractedData.getImgSrc().length()!=0)
					{
					 dbRecord.setImgSrc( extractedData.getImgSrc() );
					 data.put(O.db.FIELD_NAME_IMG, dbRecord.getImgSrc() );
					 }
				 DbHelper.updateRecord(contentType,dbPosition,data);
				 }
			 finish();
			 }
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.web_layout);
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

		 webView.setWebViewClient(new WebClient());
		 acceptButton.setOnClickListener(new AcceptButtonListener());
		 reloadButton.setOnClickListener(new NavigationButtonListener() );
		 backButton.setOnClickListener(new NavigationButtonListener());
		 forwardButton.setOnClickListener(new NavigationButtonListener() );
		 initAdapter(0);
		 siteList.setOnItemSelectedListener(new SiteListOnItemSelectedListener());
		 }
	 }
