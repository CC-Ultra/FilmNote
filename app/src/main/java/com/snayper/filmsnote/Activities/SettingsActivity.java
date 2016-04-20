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
 * Created by snayper on 03.04.2016.
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

	 private class TimeIntervalListener implements SeekBar.OnSeekBarChangeListener
		{
		 @Override
		 public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {}
		 @Override
		 public void onStartTrackingTouch(SeekBar seekBar) {}
		 @Override
		 public void onStopTrackingTouch(SeekBar seekBar)
			{
			 updateInterval=seekBar.getProgress()+1;
			 SharedPreferences.Editor editor= MainActivity.prefs.edit();
			 editor.putInt(O.mapKeys.prefs.UPDATE_INTERVAL,updateInterval).apply();
			 }
		 }
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
	 private class CheckboxListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 checkboxClick(v);
			 }
		 }
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
					 int hours= DateUtil.getHours(new Date(updateTime) );
					 TimePickerDialog timeDialog= new TimePickerDialog(SettingsActivity.this,new TimeListener(),hours,minutes,true);
					 timeDialog.show();
					 break;
//				 case O.prefs.SETTINGS_LIST_UPDATE_INTERVAL:
//					 Toast.makeText(SettingsActivity.this,"Update interval",Toast.LENGTH_SHORT).show();
//					 seek dialog;
//					 break;
				 default:
					 launchDialog(position);
				 }
			 initAdapter();
			 }
		 }

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
	 private void checkboxClick(View view)
		{
		 CheckBox checkBox= (CheckBox)view.findViewById(R.id.checkbox);
		 gsmOrder=!gsmOrder;
		 checkBox.setChecked(gsmOrder);
		 SharedPreferences.Editor editor= MainActivity.prefs.edit();
		 editor.putBoolean(O.mapKeys.prefs.GSM_ORDER, gsmOrder).apply();
		 }
	 private void initPrefs()
		 {
		 updateTime=MainActivity.prefs.getLong(O.mapKeys.prefs.UPDATE_TIME,DateUtil.buildTime(20,0).getTime());
		 updateInterval=MainActivity.prefs.getInt(O.mapKeys.prefs.UPDATE_INTERVAL, O.prefs.UPDATE_INTERVAL_DEFAULT);
		 notificationType= MainActivity.prefs.getInt(O.mapKeys.prefs.NOTIFICATION_TYPE,O.prefs.NOTIFICATION_TYPE_ID_DEFAULT);
		 theme= MainActivity.prefs.getInt(O.mapKeys.prefs.THEME,O.prefs.THEME_ID_MENTOR);
		 gsmOrder=MainActivity.prefs.getBoolean(O.mapKeys.prefs.GSM_ORDER,false);
		 }
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
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
		{
		 menu.findItem(R.id.menu_settings).setVisible(false);
		 return super.onPrepareOptionsMenu(menu);
		 }
	 }
