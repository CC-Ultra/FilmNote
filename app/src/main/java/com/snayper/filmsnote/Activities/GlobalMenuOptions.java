package com.snayper.filmsnote.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.LinearLayout;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Services.Updater;
import com.snayper.filmsnote.Utils.O;

import java.lang.reflect.Method;

/**
 * <p>Суперкласс для активностей, реализующий базовое меню</p>
 * Здесь же происходит обобщенная работа с темами. Далеко не все в вопросах тем решается стилями в {@code .xml}. Много чего
 * делается или слишком сложно, или только через код. Для этого здесь определены методы {@link #initLayoutThemeCustoms()}
 * и {@link #setLayoutThemeCustoms()}, в которых в зависимости от выбранной темы инициализируются (как правило, из {@code .xml})
 * цвета, стили и прочие ресурсы, и устанавливаются по месту назначения. Каждая наследующая активность дополняет базовый
 * функционал, переопределяя эти методы.
 * <p><sub>(29.03.2016)</sub></p>
 * @author CC-Ultra
 */
public class GlobalMenuOptions extends AppCompatActivity
	{
	 public static int themeSwitcher;
	 protected static boolean exitOrder=false;
	 protected int localThemeSwitcher=-1;
	 protected LinearLayout basicLayout;
	 protected int backgroundRes;
	 protected int lightTextColor,darkTextColor,thirdTextColor;
	 protected int panelColor,buttonPressedColor,selectionColor;
	 private int themeResource;

	/**
	 * Проверка запущен ли сервис. Сервис всего один, и важно знать его статус. Как он проверяется я не знаю, это колдовство
	 * подсказал мне гугл
	 * @return статус сервиса
	 */
	 protected boolean isServiceRunning(Class<?> serviceClass)
		{
		 ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		 for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE) )
			if(serviceClass.getName().equals(service.service.getClassName() ) )
				 return true;
		 return false;
		 }

	/**
	 * В зависимости от выбранной темы инициализирует цвета и ресурс темы для последующего применения их в классах потомках
	 * и методах {@link #initTheme()} и {@link #setLayoutThemeCustoms()}. {@code @SuppressWarnings("deprecation")} нужен,
	 * чтобы пользоваться {@link Resources#getColor(int)}
	 * @see #initTheme()
	 * @see #setLayoutThemeCustoms()
	 */
	 @SuppressWarnings("deprecation")
	 protected void initLayoutThemeCustoms()
		{
		 Resources resources= getResources();
		 switch(localThemeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 themeResource= R.style.Theme_Mentor;
				 lightTextColor= resources.getColor(R.color.light_text_mentor);
				 darkTextColor= resources.getColor(R.color.dark_text_mentor);
				 thirdTextColor= resources.getColor(R.color.third_text_mentor);
				 panelColor= resources.getColor(R.color.panel_mentor);
				 buttonPressedColor= resources.getColor(R.color.button_pressed_mentor);
				 selectionColor= resources.getColor(R.color.selection_mentor);
				 backgroundRes= R.color.background_mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 themeResource= R.style.Theme_Ultra;
				 lightTextColor= resources.getColor(R.color.light_text_ultra);
				 darkTextColor= resources.getColor(R.color.dark_text_ultra);
				 thirdTextColor= resources.getColor(R.color.third_text_ultra);
				 panelColor= resources.getColor(R.color.panel_ultra);
				 buttonPressedColor= resources.getColor(R.color.button_pressed_ultra);
				 selectionColor= resources.getColor(R.color.selection_ultra);
				 backgroundRes= R.color.background_ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 themeResource= R.style.Theme_Cow;
				 lightTextColor= resources.getColor(R.color.light_text_cow);
				 darkTextColor= resources.getColor(R.color.dark_text_cow);
				 thirdTextColor= resources.getColor(R.color.third_text_cow);
				 panelColor= resources.getColor(R.color.panel_cow);
				 buttonPressedColor= resources.getColor(R.color.button_pressed_cow);
				 selectionColor= resources.getColor(R.color.selection_cow);
				 backgroundRes= R.drawable.cow_background;
				 break;
			 default:
				 themeResource= R.style.Theme_Mentor;
				 lightTextColor= resources.getColor(R.color.light_text_mentor);
				 darkTextColor= resources.getColor(R.color.dark_text_mentor);
				 thirdTextColor= resources.getColor(R.color.third_text_mentor);
				 panelColor= resources.getColor(R.color.panel_mentor);
				 buttonPressedColor= resources.getColor(R.color.button_pressed_mentor);
				 selectionColor= resources.getColor(R.color.selection_mentor);
				 backgroundRes= R.color.background_mentor;
			 }
		 }

	/**
	 * В классах потомках здесь свой переопределенный функционал, а пока что просто устанавливаю фон, установленный в
	 * {@link #initLayoutThemeCustoms()}
	 * @see #initLayoutThemeCustoms()
	 */
	 protected void setLayoutThemeCustoms()
		{
		 basicLayout= (LinearLayout)findViewById(R.id.basicLayout);
		 basicLayout.setBackgroundResource(backgroundRes);
		 }

	/**
	 * Установка темы. Различия {@link #localThemeSwitcher} и {@link #themeSwitcher} описаны тут: {@link #onResume()}
	 */
	 protected void initTheme()
		{
		 localThemeSwitcher=themeSwitcher;
		 initLayoutThemeCustoms();
		 setTheme(themeResource);
		 }

	/**
	 * Завершение и сразу же перезапуск текущей активности. Если в запускающем активность {@link Intent}-е были данные,
	 * их можно дописать к базовым, переопределив метод {@link #putIntentExtra}
	 * @see #putIntentExtra
	 */
	 protected void resetActivity()
		{
		 finish();
		 Intent reset= new Intent(this, getClass() );
		 reset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 putIntentExtra(reset);
		 startActivity(reset);
		 }
	 protected void exit()
		{
		 finish();
		 }
	 protected void goToSettings()
		{
		 Intent jumper= new Intent(this,SettingsActivity.class);
		 startActivity(jumper);
		 }
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.basic_menu,menu);
		 }
	 protected void putIntentExtra(Intent reset) {}

	/**
	 * Базовый {@code onCreate} только ставит тему
	 */
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 initTheme();
		 }

	/**
	 * Первая проверка позволит выйти из всех активностей при установленном флаге.
	 * <p> Устанавливать тему можно перед {@link AppCompatActivity#setContentView}, а если он уже был вызван, нужно перегрузить
	 * активность. {@link #themeSwitcher} - статическая переменная, единая для всех, а {@link #localThemeSwitcher} - она
	 * же, только индивидуальная. Смысл в том, что когда в {@link SettingsActivity} устанавливается другая тема, глобальная
	 * переменная меняется, а локальные остаются в прежнем состоянии, показывая какие активности нужно перегрузить с новой
	 * темой, возвращаясь в них</p>
	 */
	 @Override
	 protected void onResume()
		{
		 if(exitOrder)
			 exit();
		 if(localThemeSwitcher!=themeSwitcher)
			{
			 localThemeSwitcher=themeSwitcher;
			 resetActivity();
			 }
		 super.onResume();
		 }

	/**
	 * У каждой активности свой {@code layout_menu}, определяющий содержание меню. Чтобы установить свой, метод {@link #setMenuLayout}
	 * переопределяется
	 */
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu)
		{
		 setMenuLayout(menu);
		 return super.onCreateOptionsMenu(menu);
		 }

	/**
	 * Срабатывает по нажатию на пункт меню. В потомках метод переопределяется
	 * @param item по нему определяется кто был нажат и что теперь делать
	 */
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 switch(item.getItemId() )
			{
			 case R.id.menu_settings:
				 goToSettings();
				 return true;
			 case R.id.menu_exit:
				 exitOrder=true;
				 exit();
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
