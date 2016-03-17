package com.snayper.filmsnote.Parsers;

import android.content.Context;
import com.snayper.filmsnote.Activities.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.O;
import org.jsoup.nodes.Element;

import java.net.HttpURLConnection;

/**
 * Created by snayper on 29.02.2016.
 */
public class Parser_OnlineLife extends AsyncParser
	{
	 public Parser_OnlineLife(Context _context,WebTaskComleteListener _completeListener,String _src)
		{
		 completeListener=_completeListener;
		 context=_context;
		 pageSrc=_src;
		 }

	 @Override
	 protected void fillBasicURLparams(HttpURLConnection urlConnn)
		{
		 super.fillBasicURLparams(urlConnn);
		 urlConnn.setRequestProperty("Host",O.web.onlineLife.HOST);
		 }
	 private int extractEpisodeNumData(String str)
		{
		 int result;
		 int startIndex= str.indexOf("x")+1;
		 int endIndex= str.indexOf("]");
		 str= str.substring(startIndex,endIndex);
		 startIndex= (str.contains("-") ? str.indexOf('-')+1 : 0);
		 result= Integer.parseInt(str.substring(startIndex) );
		 return result;
		 }
	 @Override
	 protected int extractEpisodesNum()
		{
		 int result;
		 Element container= docDOM.getElementsByAttributeValue("style","font-weight:bold;font-size:18px;").get(0);
		 String episodeInfo= container.text();
		 result=extractEpisodeNumData(episodeInfo);
		 return result;
		 }
	 @Override
	 protected String extractTitle()
		{
		 String result;
		 Element container= docDOM.getElementsByAttributeValue("style","font-weight:bold;font-size:18px;").get(0);
		 String episodeInfo= container.text();
		 result= episodeInfo.substring(0, episodeInfo.indexOf("[") );
		 return result;
		 }
	 @Override
	 protected String extractImg()
		{
		 String result;
		 Element container= docDOM.getElementsByAttributeValue("class","full-story").get(0);
		 container= container.getElementsByTag("img").first();
		 result= container.attr("src");
		 return result;
		 }
	 }
