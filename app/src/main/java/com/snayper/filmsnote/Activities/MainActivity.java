package com.snayper.filmsnote.Activities;

import android.util.Log;
import android.widget.Toast;
import android.content.Context;
import android.os.*;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import com.snayper.filmsnote.Adapters.TabsFragmentAdapter;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.Interfaces.DialogDecision;
import com.snayper.filmsnote.Services.Updater;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;

/**
 * <p>Заглавная активность и вход в приложение</p>
 * Содержит {@link ViewPager} на 3 вкладки, подгружающий с помощью {@link TabsFragmentAdapter} фрагменты типа {@link MainListFragment}.
 * Здесь хранятся {@link SharedPreferences} для всего приложения, т.к. базовая активность не убивается, и они должны жить
 * всегда. Сервис стартует здесь, а закрывается в {@link #exit()}. Активность реализует интерфейс {@link DialogDecision},
 * а значит, здесь можно вызывать {@link ConfirmDialog}
 * <p><sub>(16.02.2016)</sub></p>
 * @author CC-Ultra
 */
public class MainActivity extends GlobalMenuOptions implements DialogDecision
	{
	 public static SharedPreferences prefs;
	 private TabLayout tabLayout;
	 private Toolbar toolbar;
	 private TabsFragmentAdapter tabsFragmentAdapter;
	 private int toolbarTextColor,tabBackgroundColor,tabIndicatorColor,tabTextColor,tabTextColorSelected;

/*/тестовое, отладочное
	 private boolean cowThemeStart=true;

	 private class testButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 PendingIntent pendingIntent= createPendingResult(112,new Intent(),0);
			 Intent serv= new Intent(MainActivity.this,Updater.class);
			 serv.putExtra(O.mapKeys.extra.PENDING_INTENT_AS_EXTRA,pendingIntent);
			 if(!isServiceRunning(Updater.class) )
				 startService(serv);
			 else
				 Toast.makeText(MainActivity.this,"Сервис и так запущен",Toast.LENGTH_SHORT).show();
			 }
		 }
	 private class undoTestButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Intent serv= new Intent(MainActivity.this,Updater.class);
			 if(isServiceRunning(Updater.class) )
				 stopService(serv);
			 else
				 Toast.makeText(MainActivity.this,"Сервис не запущен, чтобы его останавливать",Toast.LENGTH_SHORT).show();
			 }
		 }
/*/

	/**
	 * Упаковка {@link PendingIntent} (который так и не случилось воспользоваться) и запуск сервиса, если он не был еще
	 * запущен. А он мог бы, если вход в приложение произведен при работающем фоново сервисе
	 */
	 private void startService()
		{
		 PendingIntent pendingIntent= createPendingResult(112,new Intent(),0);
		 Intent serv= new Intent(MainActivity.this,Updater.class);
		 serv.putExtra(O.mapKeys.extra.PENDING_INTENT_AS_EXTRA,pendingIntent);
		 if(!isServiceRunning(Updater.class) )
			 startService(serv);
		 }

	/**
	 * Тушит сервис, если он был запущен, и делает {@link android.os.Process#killProcess}
	 */
	 @Override
	 protected void exit()
		{
		 if(isServiceRunning(Updater.class) )
			 stopService(new Intent(this,Updater.class));
		 android.os.Process.killProcess(android.os.Process.myPid());
		 }

	/**
	 * Очистка списка в текущей вкладке. Для этого получаю {@link DbConsumer}, чтобы вызвать {@link DbConsumer#clear()},
	 * а потом {@link MainListFragment#initAdapter()} у выбранного фрагмента
	 */
	 private void clearMainList()
		{
		 int contentType= tabLayout.getSelectedTabPosition();
		 DbConsumer consumer= new DbConsumer(this,getContentResolver(),contentType);
		 MainListFragment fragment= (MainListFragment)tabsFragmentAdapter.getItem(contentType);
		 consumer.clear();
		 fragment.initAdapter();
		 }

	/**
	 * Расширение метода базового класса. Чтобы инициализировать тему нужно сначала получить {@link #themeSwitcher}, но
	 * сделать это нужно при входе в приложение в заглавной активности (здесь как раз). Для этого вызывается {@link #initPrefs()}
	 * @see #initPrefs()
	 */
	 @Override
	 protected void initTheme()
		{
		 initPrefs();
		 super.initTheme();
		 }

	/**
	 * Получает {@link SharedPreferences} по флагу {@link Context#MODE_MULTI_PROCESS}, чтобы работать с общими настройками
	 * параллельно с сервисом, который в другом процессе. {@code @SuppressWarnings("deprecation")} нужен, чтобы использовать
	 * этот флаг
	 */
	 @SuppressWarnings("deprecation")
	 private void initPrefs()
		{
		 prefs= getSharedPreferences(O.mapKeys.prefs.PREFS_FILENAME, MODE_MULTI_PROCESS);
/*/коровий старт
		 if(cowThemeStart)
			{
			 prefs.edit().putInt(O.mapKeys.prefs.THEME,2).apply();
			 cowThemeStart=!cowThemeStart;
			 }
/*/
		 themeSwitcher= prefs.getInt(O.mapKeys.prefs.THEME, 0);
		 }

	/**
	 * Привязывает к {@link #toolbar} меню и пишет заголовок
	 */
	 private void initToolbar()
		{
		 toolbar= (Toolbar)findViewById(R.id.toolbar);
		 setSupportActionBar(toolbar);
		 toolbar.setTitle("Looker");
		 }

	/**
	 * Инициализация {@link ViewPager} через {@link TabsFragmentAdapter}, который будет поставлять ему фрагменты, и привязка
	 * его к {@link #tabLayout}
	 */
	 private void initTabs()
		{
		 ViewPager viewPager=(ViewPager) findViewById(R.id.viewPager);
		 tabsFragmentAdapter= new TabsFragmentAdapter(getSupportFragmentManager() );
		 viewPager.setAdapter(tabsFragmentAdapter);

		 tabLayout= (TabLayout)findViewById(R.id.tabLayout);
		 tabLayout.setupWithViewPager(viewPager);
		 }

	/**
	 * Реализация интерфейса {@link DialogDecision}
	 */
	 @Override
	 public void sayNo(int noId) {}
	/**
	 * Реализация интерфейса {@link DialogDecision}
	 * @param yesId указывает на какое именно действие было сказано {@code "Да"}
	 */
	 @Override
	 public void sayYes(int yesId)
		{
		 switch(yesId)
			{
			 case 0:
				 clearMainList();
				 break;
			 }
		 }

	/**
	 * Установка содержимого меню
	 * @see GlobalMenuOptions#onCreateOptionsMenu
	 */
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.main_menu,menu);
		 }

	/**
	 * В зависимости от выбранной темы инициализирует цвета разных элементов для последующего применения их в методе
	 * {@link #setLayoutThemeCustoms()}.
	 * @see #setLayoutThemeCustoms()
	 */
	 @Override
	 protected void initLayoutThemeCustoms()
		{
		 super.initLayoutThemeCustoms();
		 tabBackgroundColor=panelColor;
		 toolbarTextColor=lightTextColor;
		 tabTextColor=lightTextColor;
		 tabTextColorSelected=lightTextColor;
		 switch(localThemeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 tabIndicatorColor=lightTextColor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 tabTextColorSelected=thirdTextColor;
				 tabIndicatorColor=thirdTextColor;
				 break;
			 case O.prefs.THEME_ID_COW:
				 tabTextColor=thirdTextColor;
				 break;
			 default:
				 tabIndicatorColor=lightTextColor;
			 }
		 }

	/**
	 * Покраска {@link #tabLayout} и {@link #toolbar} согласно выбранной теме
	 * @see #initLayoutThemeCustoms()
	 */
	 @Override
	 protected void setLayoutThemeCustoms()
		{
		 super.setLayoutThemeCustoms();
		 if(localThemeSwitcher!=O.prefs.THEME_ID_COW)
			 tabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);
		 toolbar.setTitleTextColor(toolbarTextColor);
		 tabLayout.setBackgroundColor(tabBackgroundColor);
		 tabLayout.setTabTextColors(tabTextColor,tabTextColorSelected);
		 }

	/**
	 * Почти ничего не происходит. Только {@link #initTabs()}, {@link #initToolbar()}, покраска согласно теме и запуск сервиса
	 * @see #initTabs()
	 * @see #initToolbar()
	 * @see #startService()
	 * @see #setLayoutThemeCustoms()
	 */
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
//		 Log.d(O.TAG,"onCreate: main");
		 setContentView(R.layout.main_layout);

		 initToolbar();
		 initTabs();

/*/тестовое, отладочное
		 Button testButton= (Button)findViewById(R.id.testButton);
		 testButton.setOnClickListener(new testButtonListener() );
		 Button doButton= (Button)findViewById(R.id.doButton);
		 doButton.setOnClickListener(new testButtonListener() );
		 Button undoButton= (Button)findViewById(R.id.undoButton);
		 undoButton.setOnClickListener(new undoTestButtonListener() );
/*/
		 setLayoutThemeCustoms();
		 startService();
		 }

	/**
	 * Создаю {@link DbConsumer}, чтобы узнать сколько записей в базе по данной вкладке. Если 0, то скрываю один пункт меню
	 */
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
		{
		 int contentType= tabLayout.getSelectedTabPosition();
		 DbConsumer consumer= new DbConsumer(this,getContentResolver(),contentType);
		 boolean x= consumer.getCount()!=0;
		 menu.findItem(R.id.menu_deleteAll).setVisible(x);
		 return super.onPrepareOptionsMenu(menu);
		 }

	/**
	 * Срабатывает по нажатию на пункт меню
	 * @param item по нему определяется кто был нажат и что теперь делать
	 */
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 switch(item.getItemId() )
			{
			 case R.id.menu_deleteAll:
				 new ConfirmDialog(this,this,0);
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
