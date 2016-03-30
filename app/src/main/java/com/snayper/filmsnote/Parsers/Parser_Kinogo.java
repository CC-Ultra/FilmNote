package com.snayper.filmsnote.Parsers;

import android.content.Context;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.O;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;

/**
 * Created by snayper on 29.02.2016.
 */
public class Parser_Kinogo extends AsyncParser
	{
	 public Parser_Kinogo(Context _context,WebTaskComleteListener _completeListener,String _src)
		{
		 completeListener=_completeListener;
		 context=_context;
		 pageSrc=_src;
		 }

	 @Override
	 protected void fillBasicURLparams(HttpURLConnection urlConnn)
		{
		 super.fillBasicURLparams(urlConnn);
		 urlConnn.setRequestProperty("Host",O.web.kinogo.HOST);
		 }
	 private String extractSerialId()
		{
		 String result;
		 int startIndex= O.web.kinogo.HOST_FULL.length()+1;
		 int endIndex= pageSrc.indexOf('-');
		 result= pageSrc.substring(startIndex,endIndex);
		 return result;
		 }
	 protected int extractEpisodeNumData(String str)
		{
		 int result;
		 String words[]= str.split(" ");
		 result= Integer.parseInt(words[2]);
		 return result;
		 }
	 @Override
	 protected int extractEpisodesNum()
		{
		 int result;
		 Elements serialBlocks= docDOM.getElementsByAttributeValue("class","cont");
		 if(serialBlocks.size() > 1)
			 throw new RuntimeException();
		 Element container = serialBlocks.get(0);
		 result= extractEpisodeNumData(container.text());
		 return result;
		 }
	 @Override
	 protected String extractTitle()
		{
		 String result;
		 Element container= docDOM.getElementsByTag("title").get(0);
		 String episodeInfo= container.text();
		 result= episodeInfo.substring(0, episodeInfo.indexOf("смотреть онлайн") );
		 return result;
		 }
	 @Override
	 protected String extractImg()
		{
		 String result;
		 Element container = docDOM.getElementsByAttributeValue("id","news-id-"+ extractSerialId() ).get(0);
		 container= container.getElementsByTag("a").first();
		 result= container.attr("href");
		 return result;
		 }
	 }
