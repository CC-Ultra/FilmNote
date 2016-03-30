package com.snayper.filmsnote.Parsers;

import android.content.Context;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.O;
import org.jsoup.nodes.Element;

import java.net.HttpURLConnection;

/**
 * Created by snayper on 29.02.2016.
 */
public class Parser_Seasonvar extends AsyncParser
	{
	 public Parser_Seasonvar(Context _context,WebTaskComleteListener _completeListener,String _src)
		{
		 completeListener=_completeListener;
		 context=_context;
		 pageSrc=_src;
		 charset="UTF-8";
		 }

	 @Override
	 protected void fillBasicURLparams(HttpURLConnection urlConnn)
		{
		 super.fillBasicURLparams(urlConnn);
		 urlConnn.setRequestProperty("Host",O.web.seasonvar.HOST);
		 }
	 private int extractEpisodeNumData(String str)
		{
		 int result;
		 int endIndex=str.indexOf(" серия");
		 str= str.substring(0,endIndex);
		 int startIndex= (str.contains("-") ? (str.indexOf("-")+1) : str.lastIndexOf(' ')+1 );
		 result= Integer.parseInt(str.substring(startIndex).trim() );
		 return result;
		 }
	 @Override
	 protected int extractEpisodesNum()
		{
		 int result;
		 Element container= docDOM.getElementsByAttributeValue("class","full-news").get(0);
		 container= container.getElementsMatchingOwnText("серия").first();
		 result= extractEpisodeNumData(container.text() );
		 return result;
		 }
	 @Override
	 protected String extractTitle()
		{
		 String result;
		 Element container= docDOM.getElementsByAttributeValue("class","hname").get(0);
		 String rawTitle= container.text();
		 int startIdex= rawTitle.indexOf(" ")+1;
		 int endIdex= rawTitle.indexOf(" онлайн");
		 result= rawTitle.substring(startIdex,endIdex);
		 return result;
		 }
	 @Override
	 protected String extractImg()
		{
		 String result;
		 Element contaner= docDOM.getElementsByAttributeValue("class","pg-s-lb").get(0);
		 contaner= contaner.getElementsByTag("img").get(0);
		 result= contaner.attr("src");
		 return result;
		 }
	 }
