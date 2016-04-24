package com.snayper.filmsnote.Parsers;

import android.content.Context;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.FileManager;
import com.snayper.filmsnote.Utils.O;
import org.jsoup.nodes.Element;

import java.net.HttpURLConnection;

/**
 * <p><sub>(29.02.2016)</sub></p>
 * @author CC-Ultra
 * @see AsyncParser
 * @see O.web.onlineLife
 */
public class Parser_OnlineLife extends AsyncParser
	{
	/**
	 * @param _context Используется для {@code Toast}, {@code new ProgressDialog()}, {@code context.getResources()}, и методов
	 *                 {@link FileManager}
	 * @param _completeListener callback, который вызывается, когда парсер отработал
	 * @param _src адрес страницы для извлечения
	 * @param _enableDialog показывать ли диалог
	 */
	 public Parser_OnlineLife(Context _context,WebTaskComleteListener _completeListener,String _src,boolean _enableDialog)
		{
		 dialogEnabled=_enableDialog;
		 completeListener=_completeListener;
		 context=_context;
		 pageSrc=_src;
		 }

	/**
	 * Немного расширяет метод суперкласса
	 * @see AsyncParser#fillBasicURLparams(HttpURLConnection)
	 */
	 @Override
	 protected void fillBasicURLparams(HttpURLConnection urlConnn)
		{
		 super.fillBasicURLparams(urlConnn);
		 urlConnn.setRequestProperty("Host",O.web.onlineLife.HOST);
		 }

	/**
	 * Извлечние числовой информации из блока html-кода
	 * @param str строка с цифрами
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 private int extractEpisodeNumData(String str) throws Exception
		{
		 int result;
		 int startIndex= str.indexOf("x")+1;
		 int endIndex= str.indexOf("]");
		 str= str.substring(startIndex,endIndex);
		 startIndex= (str.contains("-") ? str.indexOf('-')+1 : 0);
		 result= Integer.parseInt(str.substring(startIndex) );
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected int extractEpisodesNum() throws Exception
		{
		 int result;
		 Element container= docDOM.getElementsByAttributeValue("style","font-weight:bold;font-size:18px;").get(0);
		 String episodeInfo= container.text();
		 result=extractEpisodeNumData(episodeInfo);
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected String extractTitle() throws Exception
		{
		 String result;
		 Element container= docDOM.getElementsByAttributeValue("style","font-weight:bold;font-size:18px;").get(0);
		 String episodeInfo= container.text();
		 result= episodeInfo.substring(0, episodeInfo.indexOf("[") );
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected String extractImg() throws Exception
		{
		 String result;
		 Element container= docDOM.getElementsByAttributeValue("class","full-story").get(0);
		 container= container.getElementsByTag("img").first();
		 result= container.attr("src");
		 return result;
		 }
	 }
