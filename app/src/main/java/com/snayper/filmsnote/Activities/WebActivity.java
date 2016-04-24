package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Parsers.AsyncParser;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;

/**
 * <p>Активность для добавления сериала online</p>
 * В основе лежит {@link WebView}, над которым выпадающий список сайтов, а под - кнопки навигации и кнопка подтверждения,
 * что искомый сериал находится на выбранной странице. По нажатию на эту кнопку запускается {@link AsyncParser}, пытается
 * извлечь данные и упаковать их в {@link Record_Serial}. В это время на экране кружит диалог (который можно отменить, прерывая
 * работу парсера). Если со страницы нечего извлекать, {@link ParserResultConsumer} в реализации интерфейса {@link WebTaskComleteListener},
 * которому {@link AsyncParser} передаст результат, сообщит об этом через {@code Toast} и больше ничего не произойдет. Если
 * же что-то нашлось, то его добавят в базу и произойдет выход из активности. Пользователь может передумать и через меню
 * уйти в offline добавление сериала. Для работы {@link WebView} имеется класс {@link WebClient}, описывающий его поведение.
 * <p><sub>(16.02.2016)</sub></p>
 * @author CC-Ultra
 * @see AsyncParser
 * @see ParserResultConsumer
 */
public class WebActivity extends GlobalMenuOptions implements WebTaskComleteListener
	{
	 private boolean loading;
	 private int buttonIcon_back,buttonIcon_forward,buttonIcon_reload,buttonIcon_cancel,progressColor;
	 private ImageButton backButton,forwardButton,reloadButton;
	 private WebView webView;
	 private Spinner siteList;
	 private ProgressBar progressBar;
	 private int spinnerPosition=0;
	 private int contentType=0;
	 private int action=0;
	 private int dbPosition=0;
	 private String[] siteListSrc= {O.web.filmix.HOST_FULL, O.web.seasonvar.HOST_FULL, O.web.kinogo.HOST_FULL, O.web.onlineLife.HOST_FULL};

	/**
	 * Listener для кнопки извлечения. Здесь из {@link #webView} извлекается url и в зависимости от ее содержания вызывается
	 * один из парсеров.
	 * @see #nowSelected(String)
	 * @see AsyncParser
	 */
	 private class AcceptButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 String resultWebSrc= webView.getUrl();
			 AsyncParser parser=null;
			 spinnerPosition= nowSelected(resultWebSrc);
			 switch(spinnerPosition)
				{
				 case O.web.filmix.ID:
					 parser= new Parser_Filmix(WebActivity.this, WebActivity.this, resultWebSrc, true);
					 break;
				 case O.web.seasonvar.ID:
					 parser= new Parser_Seasonvar(WebActivity.this, WebActivity.this, resultWebSrc, true);
					 break;
				 case O.web.kinogo.ID:
					 parser= new Parser_Kinogo(WebActivity.this, WebActivity.this, resultWebSrc, true);
					 break;
				 case O.web.onlineLife.ID:
					 parser= new Parser_OnlineLife(WebActivity.this, WebActivity.this, resultWebSrc, true);
				 }
			 if(parser==null)
				{
				 Log.d(O.TAG,"onClick: Парсер не был инициализирован, а значит и запущен");
				 return;
				 }
			 parser.execute();
			 }
		 }

	/**
	 * Listener для кнопок навигации. Определяется какая была нажата, и в соответствии с этим производится действие: или
	 * просто переход вперед/назад (с проверкой есть ли куда), или более комплексные действия. Для кнопки с {@code id==R.id.webReload}
	 * нужно сменить картинку на противоположную текущему состоянию, и возможно, сбросить {@link #progressBar}
	 */
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
						 reloadButton.setImageResource(buttonIcon_reload);
						 webView.stopLoading();
						 progressBar.setProgress(0);
						 }
					 else
						{
						 loading=true;
						 reloadButton.setImageResource(buttonIcon_cancel);
						 webView.reload();
						 }
				 }
			 }
		 }

	/**
	 * Listener для {@link #siteList}. Здесь извлекается url сайта, по которому начинается загрузка, и устанавливается
	 * {@link #spinnerPosition}
	 */
	 private class SiteListOnItemSelectedListener implements AdapterView.OnItemSelectedListener
		{
		 @Override
		 public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
			 String host= siteList.getItemAtPosition(position).toString();
			 spinnerPosition= position;
			 webView.loadUrl(host);
			 }
		 @Override
		 public void onNothingSelected(AdapterView<?> arg0) {}
		 }

	/**
	 * {@link WebViewClient} для {@link #webView}
	 */
	 private class WebClient extends WebViewClient
		{
		/**
		 * Не знаю что оно делает. Это не мой кусок кода
		 */
		 @Override
		 public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
			 view.loadUrl(url);
			 return true;
			 }

		/**
		 * Когда страница загружена, прогресс устанавливается на 100, картинка подменяется, флажок сбрасывается
		 */
		 @Override
		 public void onPageFinished(WebView view, String url)
			{
			 loading=false;
			 reloadButton.setImageResource(buttonIcon_reload);
			 progressBar.setProgress(100);
			 super.onPageFinished(view, url);
			 }

		/**
		 * Когда страница начинает грузиться, прогресс устанавливается на 50, картинка подменяется, флажок устанавливается
		 */
		 @Override
		 public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
			 loading=true;
			 reloadButton.setImageResource(buttonIcon_cancel);
			 progressBar.setProgress(50);
			 super.onPageStarted(view,url,favicon);
			 }
		 }

	/**
	 * Если пользователь решил добавить сериал, все-таки offline. Завершает эту активность и прыгает в {@link AddActivity}
	 * @see #onOptionsItemSelected
	 */
	 private void toOffline()
		{
		 finish();
		 Intent jumper= new Intent(this,AddActivity.class);
		 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
		 startActivity(jumper);
		 }

	/**
	 * Во входящем {@code url} нужно найти подстроку одного из сайтов для поиска, и выдать его номер
	 * @param url адрес, извлеченный из {@link #webView}
	 * @return номер парсера, соответствующий {@code url}. Если не был найден, то 0
	 */
	 private int nowSelected(String url)
		{
		 for(int i=0; i<siteListSrc.length; i++)
			 if(url.contains(siteListSrc[i] ) )
				 return i;
		 return 0;
		 }

	/**
	 * Инициализация адаптера для {@link #siteList}
	 */
	 private void initAdapter(int selected)
		{
		 ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,siteListSrc);
		 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		 siteList.setPrompt("Список сайтов");
		 siteList.setAdapter(adapter);
		 siteList.setSelection(selected);
		 }

	/**
	 * Инициализация цвета {@link #progressBar} и набора картинок для кнопок в соответствии с темой
	 * @see #setLayoutThemeCustoms()
	 */
	 @Override
	 protected void initLayoutThemeCustoms()
		{
		 super.initLayoutThemeCustoms();
		 progressColor=selectionColor;
		 switch(localThemeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 buttonIcon_back= R.drawable.web_back_mentor;
				 buttonIcon_forward= R.drawable.web_forward_mentor;
				 buttonIcon_reload= R.drawable.web_reload_mentor;
				 buttonIcon_cancel= R.drawable.web_cancel_mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 buttonIcon_back= R.drawable.web_back_ultra;
				 buttonIcon_forward= R.drawable.web_forward_ultra;
				 buttonIcon_reload= R.drawable.web_reload_ultra;
				 buttonIcon_cancel= R.drawable.web_cancel_ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 buttonIcon_back= R.drawable.web_back_cow;
				 buttonIcon_forward= R.drawable.web_forward_cow;
				 buttonIcon_reload= R.drawable.web_reload_cow;
				 buttonIcon_cancel= R.drawable.web_cancel_cow;
				 break;
			 default:
				 buttonIcon_back= R.drawable.web_back_mentor;
				 buttonIcon_forward= R.drawable.web_forward_mentor;
				 buttonIcon_reload= R.drawable.web_reload_mentor;
				 buttonIcon_cancel= R.drawable.web_cancel_mentor;
			 }
		 }

	/**
	 * Установка цвета {@link #progressBar} и картинок на кнопки
	 * @see #initLayoutThemeCustoms()
	 */
	 @Override
	 protected void setLayoutThemeCustoms()
		{
		 super.setLayoutThemeCustoms();
		 progressBar.getProgressDrawable().setColorFilter(progressColor, android.graphics.PorterDuff.Mode.SRC_IN);
		 reloadButton.setImageResource(buttonIcon_reload);
		 backButton.setImageResource(buttonIcon_back);
		 forwardButton.setImageResource(buttonIcon_forward);
		 }

	/**
	 * {@link ParserResultConsumer} добавит запись в базу и своим методом спровоцирует выход из активности, если запись
	 * извлеклась не пустая
	 * @param extractedData результат работы парсера
	 */
	 @Override
	 public void useParserResult(Record_Serial extractedData)
		{
		 if(ParserResultConsumer.useParserResult(this,getContentResolver(),extractedData,action,contentType,dbPosition) )
			 finish();
		 }

	/**
	 * Установка содержимого меню
	 * @see GlobalMenuOptions#onCreateOptionsMenu
	 */
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.web_menu, menu);
		 }

	/**
	 * При перезагрузке активности нужно упаковать в {@link Intent} те же данные, с которыми она была запущена
	 * @param reset перезапускающий активность {@link Intent}
	 * @see #resetActivity()
	 */
	 @Override
	 protected void putIntentExtra(Intent reset)
		{
		 reset.putExtra(O.mapKeys.extra.CONTENT_TYPE,contentType);
		 reset.putExtra(O.mapKeys.extra.POSITION,dbPosition);
		 reset.putExtra(O.mapKeys.extra.ACTION,action);
		 }

	/**
	 * Извлечение данных из {@link Intent}, получение элементов страницы, установка Listener-ов, {@code initAdapter(0)},
	 * {@link #setLayoutThemeCustoms()}, установка {@link WebClient}
	 */
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.web_layout);
		 Intent intent= getIntent();
		 contentType= intent.getIntExtra(O.mapKeys.extra.CONTENT_TYPE, -1);
		 action= intent.getIntExtra(O.mapKeys.extra.ACTION, -1);
		 dbPosition= intent.getIntExtra(O.mapKeys.extra.POSITION, -1);

		 Button acceptButton= (Button)findViewById(R.id.acceptButton);
		 backButton= (ImageButton)findViewById(R.id.webBack);
		 forwardButton= (ImageButton)findViewById(R.id.webForward);
		 progressBar = (ProgressBar) findViewById(R.id.progressBar);
		 webView= (WebView) findViewById(R.id.webView);
		 siteList= (Spinner) findViewById(R.id.siteList);
		 reloadButton= (ImageButton)findViewById(R.id.webReload);

		 setLayoutThemeCustoms();
		 reloadButton.setOnClickListener(new NavigationButtonListener());
		 webView.setWebViewClient(new WebClient() );
		 acceptButton.setOnClickListener(new AcceptButtonListener());
		 backButton.setOnClickListener(new NavigationButtonListener());
		 forwardButton.setOnClickListener(new NavigationButtonListener() );
		 initAdapter(0);
		 siteList.setOnItemSelectedListener(new SiteListOnItemSelectedListener());
		 }

	/**
	 * Срабатывает по нажатию на пункт меню
	 * @param item по нему определяется кто был нажат и что теперь делать
	 */
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 switch(item.getItemId() )
			{
			 case R.id.menu_convert:
				 toOffline();
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
