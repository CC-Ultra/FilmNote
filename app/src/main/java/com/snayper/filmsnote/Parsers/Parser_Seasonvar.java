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
 * @see O.web.seasonvar
 */
public class Parser_Seasonvar extends AsyncParser
	{
	/**
	 * @param _context Используется для {@code Toast}, {@code new ProgressDialog()}, {@code context.getResources()}, и методов
	 *                 {@link FileManager}
	 * @param _completeListener callback, который вызывается, когда парсер отработал
	 * @param _src адрес страницы для извлечения
	 * @param _enableDialog показывать ли диалог
	 */
	 public Parser_Seasonvar(Context _context,WebTaskComleteListener _completeListener,String _src,boolean _enableDialog)
		{
		 dialogEnabled=_enableDialog;
		 completeListener=_completeListener;
		 context=_context;
		 pageSrc=_src;
		 charset="UTF-8";
		 }

	/**
	 * Немного расширяет метод суперкласса
	 * @see AsyncParser#fillBasicURLparams(HttpURLConnection)
	 */
	 @Override
	 protected void fillBasicURLparams(HttpURLConnection urlConnn)
		{
		 super.fillBasicURLparams(urlConnn);
		 urlConnn.setRequestProperty("Host",O.web.seasonvar.HOST);
		 }

	/**
	 * Извлечние числовой информации из блока html-кода
	 * @param str строка с цифрами
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 private int extractEpisodeNumData(String str) throws Exception
		{
		 int result;
		 int endIndex=str.indexOf(" серия");
		 str= str.substring(0,endIndex);
		 int startIndex= (str.contains("-") ? (str.indexOf("-")+1) : str.lastIndexOf(' ')+1 );
		 result= Integer.parseInt(str.substring(startIndex).trim() );
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected int extractEpisodesNum() throws Exception
		{
		 int result;
		 Element container= docDOM.getElementsByAttributeValue("class","full-news").get(0);
		 container= container.getElementsMatchingOwnText("серия").first();
		 result= extractEpisodeNumData(container.text() );
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected String extractTitle() throws Exception
		{
		 String result;
		 Element container= docDOM.getElementsByAttributeValue("class","hname").get(0);
		 String rawTitle= container.text();
		 int startIdex= rawTitle.indexOf(" ")+1;
		 int endIdex= rawTitle.indexOf(" онлайн");
		 result= rawTitle.substring(startIdex,endIdex);
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected String extractImg() throws Exception
		{
		 String result;
		 Element contaner= docDOM.getElementsByAttributeValue("class","pg-s-lb").get(0);
		 contaner= contaner.getElementsByTag("img").get(0);
		 result= contaner.attr("src");
		 return result;
		 }
	 }
