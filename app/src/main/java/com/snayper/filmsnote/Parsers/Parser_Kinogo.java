package com.snayper.filmsnote.Parsers;

import android.content.Context;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.FileManager;
import com.snayper.filmsnote.Utils.O;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;

/**
 * <p><sub>(29.02.2016)</sub></p>
 * @author CC-Ultra
 * @see AsyncParser
 * @see O.web.kinogo
 */
public class Parser_Kinogo extends AsyncParser
	{
	/**
	 * @param _context Используется для {@code Toast}, {@code new ProgressDialog()}, {@code context.getResources()}, и методов
	 *                 {@link FileManager}
	 * @param _completeListener callback, который вызывается, когда парсер отработал
	 * @param _src адрес страницы для извлечения
	 * @param _enableDialog показывать ли диалог
	 */
	 public Parser_Kinogo(Context _context,WebTaskComleteListener _completeListener,String _src,boolean _enableDialog)
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
		 urlConnn.setRequestProperty("Host",O.web.kinogo.HOST);
		 }

	/**
	 * Извлечние числовой информации из блока html-кода
	 * @return некий {@code id} для составление запроса парсеру {@code jsoup}
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 private String extractSerialId() throws Exception
		{
		 String result;
		 int startIndex= O.web.kinogo.HOST_FULL.length()+1;
		 int endIndex= pageSrc.indexOf('-');
		 result= pageSrc.substring(startIndex,endIndex);
		 return result;
		 }

	/**
	 * Извлечние числовой информации из блока html-кода
	 * @param str строка с цифрами
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 protected int extractEpisodeNumData(String str) throws Exception
		{
		 int result;
		 String words[]= str.split(" ");
		 result= Integer.parseInt(words[2]);
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected int extractEpisodesNum() throws Exception
		{
		 int result;
		 Elements serialBlocks= docDOM.getElementsByAttributeValue("class","cont");
		 if(serialBlocks.size() > 1)
			 throw new RuntimeException();
		 Element container = serialBlocks.get(0);
		 result= extractEpisodeNumData(container.text());
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected String extractTitle() throws Exception
		{
		 String result;
		 Element container= docDOM.getElementsByTag("title").get(0);
		 String episodeInfo= container.text();
		 result= episodeInfo.substring(0, episodeInfo.indexOf("смотреть онлайн") );
		 return result;
		 }

	/**
	 * @throws Exception от этого метода я ожидаю любого исключения, если строка окажется немного не такой как ожидалось
	 */
	 @Override
	 protected String extractImg() throws Exception
		{
		 String result;
		 Element container = docDOM.getElementsByAttributeValue("id","news-id-"+ extractSerialId() ).get(0);
		 container= container.getElementsByTag("a").first();
		 result= container.attr("href");
		 return result;
		 }
	 }
