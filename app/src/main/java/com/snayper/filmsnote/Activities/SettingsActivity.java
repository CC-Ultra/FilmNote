package com.snayper.filmsnote.Activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.snayper.filmsnote.Adapters.CustomSimpleAdapter_Settings;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.DateUtil;
import com.snayper.filmsnote.Utils.O;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Активность настроек</p>
 * В основе лежит ListView, который по активации элементов вызывает тот или иной диалог. Именно здесь
 * идет редактирование SharedPreferences, которые лежат в {@link MainActivity#prefs}.
 * <p><sub>(03.04.2016)</sub></p>
 * @author CC-Ultra
 */
public class SettingsActivity extends GlobalMenuOptions
	{
	 private ListView list;
	 private int updateInterval,theme,notificationType;
	 private boolean gsmOrder;
	 private long updateTime;
	 private int dividerColor;
	 private String activeThemes[]= {"Стандартная","Ultra"};
	 private String notificationTypes[]= {"Без уведомлений","Обычные","Toast-уведомления"};

	/**
	 * Listener для установки интервала обновления в днях для сервиса в соответствующем диалоге. Выдается диалогу в {@link #launchDialog(int)}
	 */
	 private class TimeIntervalListener implements SeekBar.OnSeekBarChangeListener
		{
		 @Override
		 public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {}
		 @Override
		 public void onStartTrackingTouch(SeekBar seekBar) {}
		 @Override
		 public void onStopTrackingTouch(SeekBar seekBar)
			{
			 updateInterval= seekBar.getProgress()+1;
			 SharedPreferences.Editor editor= MainActivity.prefs.edit();
			 editor.putInt(O.mapKeys.prefs.UPDATE_INTERVAL,updateInterval).apply();
			 }
		 }
	/**
	 * Listener для установки времени обновления для сервиса в соответствующем диалоге. Выдается диалогу в {@link MenuSelectedListener}.
	 * Не может повлиять на работу сервиса до перезагрузки, т.к. последний инициализируется при старте приложения и никакие
	 * последующие изменения не изменят назначенные таймеры
	 */
	 private class TimeListener implements  TimePickerDialog.OnTimeSetListener
		{
		 @Override
		 public void onTimeSet(TimePicker view,int hourOfDay,int minute)
			{
			 updateTime= DateUtil.buildTime(hourOfDay,minute).getTime();
			 SharedPreferences.Editor editor= MainActivity.prefs.edit();
			 editor.putLong(O.mapKeys.prefs.UPDATE_TIME, updateTime).apply();
			 initAdapter();
			 Toast.makeText(SettingsActivity.this,"Нужна перезагрузка программы, чтобы изменения вступили в силу",Toast.LENGTH_SHORT).show();
			 }
		 }
	/**
	 * Listener на кнопку Ok в соответствующих диалогах. Выдается диалогу в {@link #launchDialog(int)}. Если это был диалог
	 * смены темы, требуется перегрузить страницу под новой темой и в таком случае сработает метод {@link GlobalMenuOptions#resetActivity()}.
	 * Флаг {@code reset} нужен для того, чтобы не перепутать неинициализированное поле {@code selectedTheme} (для случаев,
	 * когда диалог вызывался не для смены темы) c {@code THEME_ID_MENTOR}
	 */
	 private class DialogOkListener implements DialogInterface.OnClickListener
		{
		 boolean reset=false;
		 int selectedTheme;

		 public DialogOkListener() {}
		 public DialogOkListener(int _selectedTheme)
			{
			 selectedTheme=_selectedTheme;
			 reset=true;
			 }

		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 if(reset && (theme!=selectedTheme) )
				{
				 if(theme == O.prefs.THEME_ID_ULTRA)
					 Toast.makeText(SettingsActivity.this,"Любишь мои контрасты?",Toast.LENGTH_SHORT).show();
				 resetActivity();
				 }
			 else
				 initAdapter();
			 }
		 }
	/**
	 * Передается в адаптер, чтобы проинициализировать соответствующий элемент списка
	 */
	 private class CheckboxListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 checkboxClick(v);
			 }
		 }
	/**
	 * Listener для выбора типа уведомлений от сервиса в соответствующем диалоге. Выдается диалогу в {@link #launchDialog(int)}
	 */
	 private class NotificationTypeSelectListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 notificationType=which;
			 SharedPreferences.Editor editor= MainActivity.prefs.edit();
			 editor.putInt(O.mapKeys.prefs.NOTIFICATION_TYPE, notificationType).apply();
			 }
		 }
	/**
	 * Listener для выбора темы приложения в соответствующем диалоге. Выдается диалогу в {@link #launchDialog(int)}. После выбора
	 * идет перезагрузка активности и всех предыдущих при возврате в них по {@code onResume()}
	 * @see DialogOkListener
	 */
	 private class ThemeSelectListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 theme=which;
			 SharedPreferences.Editor editor= MainActivity.prefs.edit();
			 editor.putInt(O.mapKeys.prefs.THEME, theme).apply();
			 MainActivity.themeSwitcher=theme;
			 }
		 }
	/**
	 * Listener для выбора элемента главного списка. Порождает вызов {@link #launchDialog(int)}, {@link #checkboxClick} или
	 * вызывает {@link TimePickerDialog}. Для адекватного отображения {@link TimePickerDialog} ему передается тема. После
	 * всего этого происходит моя неуклюжая попытка не сбросить визуальное положение списка в ноль
	 * @see #launchDialog
	 * @see TimePickerDialog
	 * @see AlertDialog
	 * @see TimeListener
	 */
	 private class MenuSelectedListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 switch(position)
				{
				 case O.prefs.SETTINGS_LIST_GSM_ORDER:
					 checkboxClick(view);
					 break;
				 case O.prefs.SETTINGS_LIST_UPDATE_TIME:
					 int minutes= DateUtil.getMinutes(new Date(updateTime));
					 int hours= DateUtil.getHours(new Date(updateTime));
					 Resources.Theme resTheme = getResources().newTheme();
					 resTheme.applyStyle(R.style.CustomDialog_forTitle,true);
					 ContextThemeWrapper themeWrapper= new ContextThemeWrapper(SettingsActivity.this, resTheme);
					 TimePickerDialog timeDialog= new TimePickerDialog(themeWrapper,new TimeListener(),hours,minutes,true);
					 timeDialog.show();
					 break;
				 default:
					 launchDialog(position);
				 }
			 if(position>4)
				 list.setSelection(position);
			 }
		 }

	/**
	 * Метод вызывает один из диалогов, согласно аргументу {@code type}. {@code @SuppressWarnings("deprecation")} нужен,
	 * чтобы пользоваться {@link Resources#getColor(int)}. Для контраста цвета текста заголовка с его фоном на некоторые
	 * диалоги устанавливается тема. {@code ViewGroup root} нужен, чтобы пользоваться методом {@link AppCompatActivity#getLayoutInflater()}.
	 * {@code View dialogView} определяет содержимое диалога (кроме диалогов-списков). Все колдовство с {@code PackageInfo}
	 * нужно для получения версии приложения и внедрения ее на страницу About. В случае темы {@code THEME_ID_COW} на фон
	 * диалогов Help и About ставится картинка, а иначе - заливка. В случае с выбором темы, идет проверка не установена ли
	 * в данный момент {@code THEME_ID_COW}, потому что для нее нет пункта в списке.
	 * @param type указывает на тип создаваемого диалога
	 * @see AlertDialog
	 * @see NotificationTypeSelectListener
	 * @see TimeIntervalListener
	 * @see ThemeSelectListener
	 * @see DialogOkListener
	 */
	 @SuppressWarnings("deprecation")
	 private void launchDialog(int type)
		{
		 AlertDialog.Builder adb;
		 if(type==O.prefs.SETTINGS_LIST_ABOUT || type==O.prefs.SETTINGS_LIST_HELP || type==O.prefs.SETTINGS_LIST_UPDATE_TIME)
			{
			 Resources resources= getResources();
			 Resources.Theme x= resources.newTheme();
			 x.applyStyle(R.style.CustomDialog_forTitle,true);
			 adb= new AlertDialog.Builder(new ContextThemeWrapper(this, x) );
			 }
		 else
			 adb= new AlertDialog.Builder(this);
		 ViewGroup root= (ViewGroup)list.getParent();
		 View dialogView;
		 switch(type)
			{
			 case O.prefs.SETTINGS_LIST_ABOUT:
				 dialogView= getLayoutInflater().inflate(R.layout.about_page,root,false);
				 try
					{
					 PackageInfo pInfo;
					 pInfo= getPackageManager().getPackageInfo(getPackageName(),0);
					 String version= pInfo.versionName;
					 TextView versionTxt= (TextView)dialogView.findViewById(R.id.version);
					 versionTxt.setText("Версия "+ version);
					 }
				 catch(PackageManager.NameNotFoundException e)
					{ Log.d(O.TAG,"launchDialog: package name не найден"); }
				 if(theme==O.prefs.THEME_ID_COW)
					 dialogView.setBackgroundResource(R.drawable.cow_background_dark);
				 else
					 dialogView.setBackgroundResource(backgroundRes);
				 if(theme==O.prefs.THEME_ID_ULTRA)
					{
					 TextView mailTxt= (TextView)dialogView.findViewById(R.id.mail);
					 mailTxt.setLinkTextColor(getResources().getColor(R.color.selection_ultra) );
					 TextView gitTxt= (TextView)dialogView.findViewById(R.id.git_link);
					 gitTxt.setLinkTextColor(getResources().getColor(R.color.selection_ultra) );
					 }
				 adb.setTitle("О программе");
				 adb.setView(dialogView);
				 adb.setIcon(R.mipmap.avatar);
				 break;
			 case O.prefs.SETTINGS_LIST_HELP:
				 dialogView= getLayoutInflater().inflate(R.layout.help_page,root,false);
				 adb.setTitle("Подсказки");
				 if(theme==O.prefs.THEME_ID_COW)
					 dialogView.setBackgroundResource(R.drawable.cow_background_dark);
				 else
					 dialogView.setBackgroundResource(backgroundRes);
				 adb.setView(dialogView);
				 adb.setIcon(R.mipmap.halp_icon);
				 break;
			 case O.prefs.SETTINGS_LIST_UPDATE_INTERVAL:
				 SeekBar seekBar= new SeekBar(this);
				 seekBar.setMax(14);
				 seekBar.setProgress(updateInterval-1);
				 seekBar.setOnSeekBarChangeListener(new TimeIntervalListener() );
				 adb.setPositiveButton("Ok",new DialogOkListener() );
				 adb.setView(seekBar);
				 break;
			 case O.prefs.SETTINGS_LIST_NOTIFICATION_TYPE:
				 adb.setSingleChoiceItems(notificationTypes,notificationType,new NotificationTypeSelectListener() );
				 adb.setPositiveButton("Ok",new DialogOkListener() );
				 break;
			 case O.prefs.SETTINGS_LIST_THEME:
				 int selectedTheme= (theme==O.prefs.THEME_ID_COW ? -1 : theme);
				 adb.setSingleChoiceItems(activeThemes,selectedTheme,new ThemeSelectListener() );
				 adb.setPositiveButton("Ok",new DialogOkListener(theme) );
				 break;
			 }
		 adb.show();
		 }

	/**
	 * Используется в {@link CheckboxListener}, который передается адаптеру, или вызвается непосредственно в {@link MenuSelectedListener}
	 * по нажатию на этот элемент
	 * @param view нужен для вызова метода {@link AppCompatActivity#findViewById(int)}
	 * @see CheckboxListener
	 * @see MenuSelectedListener
	 */
	 private void checkboxClick(View view)
		{
		 CheckBox checkBox= (CheckBox)view.findViewById(R.id.checkbox);
		 gsmOrder=!gsmOrder;
		 checkBox.setChecked(gsmOrder);
		 SharedPreferences.Editor editor= MainActivity.prefs.edit();
		 editor.putBoolean(O.mapKeys.prefs.GSM_ORDER, gsmOrder).apply();
		 }

	/**
	 * Инициализация полей настроек из {@link SharedPreferences}
	 * @see SharedPreferences
	 */
	 private void initPrefs()
		{
		 updateTime=MainActivity.prefs.getLong(O.mapKeys.prefs.UPDATE_TIME,DateUtil.buildTime(20,0).getTime() );
		 updateInterval=MainActivity.prefs.getInt(O.mapKeys.prefs.UPDATE_INTERVAL, O.prefs.UPDATE_INTERVAL_DEFAULT);
		 notificationType= MainActivity.prefs.getInt(O.mapKeys.prefs.NOTIFICATION_TYPE,O.prefs.NOTIFICATION_TYPE_ID_DEFAULT);
		 theme= MainActivity.prefs.getInt(O.mapKeys.prefs.THEME,O.prefs.THEME_ID_MENTOR);
		 gsmOrder=MainActivity.prefs.getBoolean(O.mapKeys.prefs.GSM_ORDER,false);
		 }

	/**
	 * Заполнение массива со значениями для карты для {@link CustomSimpleAdapter_Settings} в {@link #initAdapter()}.
	 * {@code @SuppressWarnings("deprecation")} нужен чтобы пользоваться {@link Resources#getColor(int)}.
	 * @param values заполняемый массив
	 * @see CustomSimpleAdapter_Settings
	 */
	 @SuppressWarnings("deprecation")
	 private void fillValues(String[] values)
		{
		 values[1]="";
		 values[2]=""+ DateUtil.timeToString(new Date(updateTime),false);
		 String updateIntervalSuffix;
		 if(updateInterval==1)
			 updateIntervalSuffix=" день";
		 else if(updateInterval>1 && updateInterval<5)
			 updateIntervalSuffix=" дня";
		 else
			 updateIntervalSuffix=" дней";
		 values[3]= updateInterval + updateIntervalSuffix;
		 values[4]= notificationTypes[notificationType];
		 values[5]="Нюансы работы программы";
		 values[6]="";
		 Resources resources= getResources();
		 switch(theme)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 values[0]=O.prefs.THEME_STR_MENTOR;
				 dividerColor= resources.getColor(R.color.list_divider_mentor);
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 values[0]=O.prefs.THEME_STR_ULTRA;
				 dividerColor= resources.getColor(R.color.list_divider_mentor);
				 break;
			 case O.prefs.THEME_ID_COW:
				 values[0]=O.prefs.THEME_STR_COW;
				 dividerColor= resources.getColor(R.color.list_background_cow);
				 break;
			 default:
				 values[0]=O.prefs.THEME_STR_MENTOR;
				 dividerColor= resources.getColor(R.color.list_divider_mentor);
			 }
		 }

	/**
	 * <p>Вызывается для обновления главного списка.</p>
	 * Сналача идет заполнение структуры {@code HashMap<String,Object> listElementMap}, потом создание адаптера, передача
	 * его списку и устанока толщины и цвета разделителя в списке
	 */
	 private void initAdapter()
		{
		 String titles[]= {"Сменить тему", "Разрешить обновление без wi-fi", "Время обновлений", "Интервал обновлений",
				 "Вид уведомлений", "Помощь", "О программе"};
		 String values[]= new String[titles.length];
		 fillValues(values);
		 String from[]= {"Title","Value"};
		 int to[]= {R.id.title,R.id.value};
		 ArrayList< HashMap<String,Object> > listData= new ArrayList<>();

		 HashMap<String,Object> listElementMap;
		 for(int i=0; i<titles.length; i++)
			{
			 listElementMap= new HashMap<>();
			 listElementMap.put(from[0],titles[i] );
			 if(titles[i].equals("Разрешить обновление без wi-fi") )
				 listElementMap.put(from[1],gsmOrder);
			 else
				 listElementMap.put(from[1],values[i] );
			 listData.add(listElementMap);
			 }

		 CustomSimpleAdapter_Settings adapter= new CustomSimpleAdapter_Settings(this,new CheckboxListener(),
				 listData,R.layout.settings_list_element,from,to);
		 list.setAdapter(adapter);
		 ColorDrawable divcolor= new ColorDrawable(dividerColor);
		 list.setDivider(divcolor);
		 list.setDividerHeight(2);
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 initPrefs();
		 setContentView(R.layout.settings_layout);

		 list= (ListView)findViewById(R.id.list);
		 initAdapter();
		 list.setOnItemClickListener(new MenuSelectedListener() );
		 }

	/**
	 * Здесь просто скрываю один элемент стандартного меню, потому что мы и так в настройках
	 */
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
		{
		 menu.findItem(R.id.menu_settings).setVisible(false);
		 return super.onPrepareOptionsMenu(menu);
		 }
	 }
