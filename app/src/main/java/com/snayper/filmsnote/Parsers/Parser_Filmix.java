package com.snayper.filmsnote.Parsers;

import android.content.Context;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.O;
import org.jsoup.nodes.Element;

import java.net.HttpURLConnection;

/**
 * Created by snayper on 29.02.2016.
 */
public class Parser_Filmix extends AsyncParser
	{
	 public Parser_Filmix(Context _context,WebTaskComleteListener _completeListener,String _pageSrc)
		{
		 completeListener=_completeListener;
		 context=_context;
		 pageSrc=_pageSrc;
		 }

	 @Override
	 protected void fillBasicURLparams(HttpURLConnection urlConnn)
		{
		 super.fillBasicURLparams(urlConnn);
		 urlConnn.setRequestProperty("Host",O.web.filmix.HOST);
		 }
	 private int extractEpisodeNumData(String str)
		{
		 int result;
		 str= str.substring(0,str.indexOf("Серия") );
		 int startIndex= (str.contains("-") ? (str.indexOf("-")+1) : 0);
		 result= Integer.parseInt(str.substring(startIndex).trim());
		 return result;
		 }
	 @Override
	 protected String extractTitle()
		{
		 String result;
		 Element container= docDOM.getElementsByAttributeValue("class","name-block").get(0);
		 container= container.getElementsByAttributeValue("class","name").get(0);
		 result= container.text();
		 return result;
		 }
	 @Override
	 protected String extractImg()
		{
		 Element container= docDOM.getElementsByAttributeValue("class","fancybox").get(0);
		 container= container.getElementsByTag("img").get(0);
		 String result= O.web.filmix.HOST_FULL + container.attr("src");
		 return result;
		 }
	 @Override
	 protected int extractEpisodesNum()
		{
		 int result;
		 Element container= docDOM.getElementsByAttributeValue("class","full min").first();
		 container= container.getElementsByAttributeValue("class","added-info").first();
		 String episodeInfo= container.text();
		 result= extractEpisodeNumData(episodeInfo);
		 return result;
		 }
	 }
