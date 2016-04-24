package com.snayper.filmsnote.Parsers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.Toast;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Utils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.snayper.filmsnote.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * <p>Базовый класс парсера</p>
 * Абстрактный класс, объединяющий в себе общий функционал и принципы работы всех потомков. На борту ссылка на {@link ProgressDialog}
 * - елемент, через который осуществляется связь с UI. А именно, возможность прервать работу парсера по
 * {@link DialogInterface.OnDismissListener#onDismiss}. И наоборот, парсер останавливает
 * диалог по завершению работы. Диалог, как элемент UI, можно и не отрисовывать, указав нужный флаг при создании. Также в
 * парсер передается callback от вызывающей активности или сервиса, который распоряжается извлеченными результатами. Извлечение
 * производится средствами класса {@link HttpURLConnection} и библиотеки {@code jsoup} ({@link Jsoup}, {@link Document}).
 * По поводу generic-праметров, используется только последний - результаты. Это {@link Record_Serial}
 * <p><sub>(01.03.2016)</sub></p>
 * @author CC-Ultra
 * @see Parser_Filmix
 * @see Parser_Seasonvar
 * @see Parser_Kinogo
 * @see Parser_OnlineLife
 */
public abstract class AsyncParser extends AsyncTask<Void,Void,Record_Serial>
	{
	 private boolean hasSomethingFound;
	 protected String charset="windows-1251";
	 protected String pageSrc;
	 protected Document docDOM;
	 protected Context context;
	 protected WebTaskComleteListener completeListener;
	 protected boolean dialogEnabled;
	 protected ProgressDialog dialog;

	/**
	 * Listener, который передается в {@link ProgressDialog#setOnDismissListener}. Призван сигналить парсеру об отмене,
	 * устанавливая в нем cancel-метку.
	 * @see AsyncTask#cancel(boolean)
	 * @see #doInBackground(Void...)
	 */
	 private class TaskInterruptor implements ProgressDialog.OnDismissListener
		{
		 @Override
		 public void onDismiss(DialogInterface dialog)
			{
			 AsyncParser.this.cancel(false);
			 }
		 }

	/**
	 * Заполнение всякой постоянной из раза в раз рутинной информации
	 */
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

	/**
	 * Здесь производится запрос по {@link #pageSrc}, а полученный ответ упаковывется в String, который потом используется в
	 * {@link #initDOM()}.
	 * @return Строка с html страницей, которую вернул сервер в ответе на запрос
	 * @throws IOException выбрасывается во многих случаях, но все они означают, проблемы с соединением (или битую ссылку)
	 * А плохое соединение означает, что страница так и не получена. Исключение просто выбрасывается выше, там разберутся.
	 * Это будет понятнее, чем вернуть пустую строку и поймать другое исключение, а потом дебажить часами в поиске причины
	 * @see #initDOM()
	 */
	 private String getHtmlString() throws IOException
		{
		 String result;
		 URL url;
		 try
			{
			 url=new URL(pageSrc);
			 HttpURLConnection urlConnn=(HttpURLConnection) url.openConnection();
			 fillBasicURLparams(urlConnn);
			 urlConnn.setConnectTimeout( (int)O.date.MINUTE_MILLIS/4);
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
			 throw err;
			 }
		 return result;
		 }

	/**
	 * @throws IOException исключение из {@link #getHtmlString()}, выбрасывается выше
	 * @see #getHtmlString()
	 */
	 protected void initDOM() throws IOException
		{
		 docDOM= Jsoup.parse(getHtmlString() );
		 }

	/**
	 * Извлечение наименования. Логика зависит от сайта
	 * @throws Exception случайная неведомая ошибка при парсинге
	 */
	 protected abstract String extractTitle() throws Exception;
	/**
	 * Извлечение количества вышедших серий. Логика зависит от сайта
	 * @throws Exception случайная неведомая ошибка при парсинге
	 */
	 protected abstract int extractEpisodesNum() throws Exception;
	/**
	 * Извлечение ссылки на изображение. Логика зависит от сайта
	 * @throws Exception случайная неведомая ошибка при парсинге
	 */
	 protected abstract String extractImg() throws Exception;

	/**
	 * <p>Здесь описана основная работа парсеров</p>
	 * Сначала {@link #initDOM()}, который может выкинуть исключение, если не была получена страница. В таком случае просто
	 * выбрасываю null, а {@link ParserResultConsumer}, которому это обрабатывать, разберется. Потом попытка извлечь количество
	 * эпизодов, картинку и наименование. Любой из этих методов может породить исключение, ибо парсинг - дело очень ненадежное.
	 * В случае с наименованием, просто запишется пустая строка. Результаты остальных двух методов зададут значение для
	 * {@link #hasSomethingFound}, который может породить пустой ответ в результатах. При успешном извлечении изображения,
	 * оно кешируется, в запись пишется имя созданного файла и здесь же проверяется сохранилось ли. Через каждый шаг проверяется
	 * не пришел ли приказ об отмене парсинга, чтобы в таком случае вызвать {@code return null}
	 * @return {@code null}, если была ошибка подключения или парсинг был отменен. Или запись, качество которой определяется
	 * флагом {@link #hasSomethingFound}
	 * @see #initDOM()
	 * @see ParserResultConsumer
	 * @see FileManager#storeWebSrcPic(Context, String, int, int)
	 * @see FileManager#getFilenameFromURL(String)
	 * @see FileManager#getStoredPicURI(Context, String)
	 */
	 @Override
	 protected Record_Serial doInBackground(Void... params)
		{
		 Record_Serial extractedData= new Record_Serial();
		 extractedData.setWebSrc(pageSrc);
		 try
			{
			 initDOM();
			 }
		 catch(IOException e)
			{
			 Toaster.makeHomeToast(context,"Ошибка соединения");
			 return null;
			 }
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
			 extractedData.setTitle(extractTitle() );
			 }
		 catch(Exception e)
			{ extractedData.setTitle(""); }
		 hasSomethingFound= isImg || isEpisodesNum;

		 return extractedData;
		 }

	/**
	 * Если диалог разрешен, то он конструируется на основе темы, чтобы цвета были адекватными, и в него передается {@link TaskInterruptor},
	 * который может остановить весь парсер
	 * @see TaskInterruptor
	 */
	 @Override
	 protected void onPreExecute()
		{
		 if(dialogEnabled)
			{
			 Resources resources= context.getResources();
			 Resources.Theme x= resources.newTheme();
			 x.applyStyle(R.style.CustomDialog_forTitle,true);

			 dialog= new ProgressDialog(new ContextThemeWrapper(context, x));
			 dialog.setOnDismissListener(new TaskInterruptor() );
			 dialog.setIndeterminate(true);
			 dialog.setMessage("Извлекаю данные...");
			 dialog.setCancelable(true);
			 dialog.show();
			 }
		 super.onPreExecute();
		 }

	/**
	 * Если диалог был разрешен, значит был и запущен. Здесь же останавливается. Если в {@link #doInBackground(Void...)}
	 * {@link #extractEpisodesNum()} и {@link #extractImg()} выдали исключения, то в результат запишется пустая свежесозданная
	 * запись, выскочит {@code Toast} и хоть этот результат и будет передан в callback метод {@link WebTaskComleteListener#useParserResult},
	 * а оттуда в {@link ParserResultConsumer#useParserResult}, там на месте готовы к такому, все в порядке
	 * @see ParserResultConsumer
	 */
	 @Override
	 protected void onPostExecute(Record_Serial result)
		{
		 if(dialogEnabled)
			 dialog.dismiss();
		 if(!hasSomethingFound)
			{
			 Toast.makeText(context,"На этой странице нечего извлекать",Toast.LENGTH_LONG).show();
			 result= new Record_Serial();
			 }
		 completeListener.useParserResult(result);
		 super.onPostExecute(result);
		 }
	 }
