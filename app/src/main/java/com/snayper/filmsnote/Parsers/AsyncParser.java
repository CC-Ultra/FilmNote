package com.snayper.filmsnote.Parsers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.snayper.filmsnote.Activities.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.FileManager;
import com.snayper.filmsnote.Utils.O;
import com.snayper.filmsnote.Utils.Record_Serial;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import com.snayper.filmsnote.R;

/**
 * Created by snayper on 01.03.2016.
 */
public abstract class AsyncParser extends AsyncTask<Void,Void,Record_Serial>
	{
	 private boolean hasSomethingFound;
	 protected String charset="windows-1251";
	 protected String pageSrc;
	 protected Document docDOM;
	 protected Context context;
	 protected WebTaskComleteListener completeListener;
	 protected ProgressDialog dialog;

	 private class TaskInterruptor implements ProgressDialog.OnDismissListener
		{
		 @Override
		 public void onDismiss(DialogInterface dialog)
			{
			 AsyncParser.this.cancel(false);
			 }
		 }

	 protected void fillBasicURLparams(HttpURLConnection urlConnn)
		{
		 urlConnn.setRequestProperty("Connection","keep-alive");
		 urlConnn.setRequestProperty("Accept","edit_text_selector/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		 urlConnn.setRequestProperty("User-Agent","bot");
		 urlConnn.setRequestProperty("Accept-Language","ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
		 urlConnn.setDoOutput(true);
		 urlConnn.setUseCaches(false);
		 urlConnn.setInstanceFollowRedirects(false);
		 }
	 private String getHtmlString()
		{
		 String result="";
		 URL url;
		 try
			{
			 url=new URL(pageSrc);
			 HttpURLConnection urlConnn=(HttpURLConnection) url.openConnection();
			 fillBasicURLparams(urlConnn);
			 urlConnn.setRequestMethod("GET");
			 BufferedReader htmlIn=new BufferedReader(new InputStreamReader(urlConnn.getInputStream(),charset) );
			 StringBuilder sb=new StringBuilder();
			 String line;
			 while( (line=htmlIn.readLine() ) != null)
				 sb.append(line+"\n");
			 result=sb.toString();
			 htmlIn.close();
			 urlConnn.disconnect();
			 }
		 catch(IOException err)
			{
			 Log.d(O.TAG,"getHtmlString: fail");
			 }
		 return result;
		 }
	 protected void initDOM()
		{
		 docDOM= Jsoup.parse(getHtmlString() );
		 }
	 protected abstract int extractEpisodesNum();
	 protected abstract String extractTitle();
	 protected abstract String extractImg();

	 @Override
	 protected Record_Serial doInBackground(Void... params)
		{
		 Record_Serial extractedData= new Record_Serial();
		 initDOM();
		 if(isCancelled() )
			 return null;
		 boolean isImg=false, isEpisodesNum=false;
		 try
			{
			 extractedData.setImgSrc(extractImg());
			 isImg=true;
			 FileManager.storeWebSrcPic(context, extractedData.getImgSrc(), R.dimen.img_w, R.dimen.img_h);
			 extractedData.setImgSrc(FileManager.getFilenameFromURL(extractedData.getImgSrc() ) );
			 if( FileManager.getStoredPicURI(context, extractedData.getImgSrc() ).length() == 0)
				{
				 extractedData.setImgSrc("");
				 isImg=false;
				 }
			 }
		 catch(Exception e) {}
		 if(isCancelled() )
			 return null;
		 try
			{
			 extractedData.setAll(extractEpisodesNum() );
			 isEpisodesNum=true;
			 }
		 catch(Exception e) {}
		 if(isCancelled() )
			 return null;
		 try
			{
			 extractedData.setTitle(extractTitle());
			 }
		 catch(Exception e) {}
		 hasSomethingFound= isImg || isEpisodesNum;
		 try
			{
			 TimeUnit.SECONDS.sleep(5);
			 }
		 catch(InterruptedException e) {}

		 return extractedData;
		 }

	 @Override
	 protected void onPreExecute()
		{
		 dialog= new ProgressDialog(context);
		 dialog.setOnDismissListener(new TaskInterruptor() );
		 dialog.setIndeterminate(true);
		 dialog.setMessage("Извлекаю...");
		 dialog.setCancelable(true);
		 dialog.show();
		 super.onPreExecute();
		 }

	 @Override
	 protected void onPostExecute(Record_Serial result)
		{
		 dialog.dismiss();
		 if(!hasSomethingFound)
			{
			 Toast.makeText(context,"На этой странице нечего извлекать",Toast.LENGTH_LONG).show();
			 result= new Record_Serial();
			 }
		 completeListener.useParserResult(result);
		 super.onPostExecute(result);
		 }

	@Override
	protected void onCancelled()
		{
		 super.onCancelled();
		 }
	}
