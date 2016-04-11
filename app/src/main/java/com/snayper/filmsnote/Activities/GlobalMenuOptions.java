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
 * Created by snayper on 29.03.2016.
 */
public class GlobalMenuOptions extends AppCompatActivity
	{
	 public static int themeSwitcher;
	 protected int localThemeSwitcher=-1;
	 protected LinearLayout basicLayout;
	 protected int backgroundRes;
	 protected int lightTextColor,darkTextColor,thirdTextColor;
	 protected int panelColor,buttonPressedColor,selectionColor;
	 private int themeResource;

	 protected boolean isServiceRunning(Class<?> serviceClass)
		{
		 ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		 for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE) )
			if(serviceClass.getName().equals(service.service.getClassName() ) )
				 return true;
		 return false;
		 }
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
	 protected void setLayoutThemeCustoms()
		{
		 basicLayout= (LinearLayout)findViewById(R.id.basicLayout);
		 basicLayout.setBackgroundResource(backgroundRes);
		 }
	 protected void initTheme()
		{
		 localThemeSwitcher=themeSwitcher;
		 initLayoutThemeCustoms();
		 setTheme(themeResource);
		 }
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
		 if(isServiceRunning(Updater.class) )
			 stopService(new Intent(this,Updater.class) );
		 android.os.Process.killProcess(android.os.Process.myPid());
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

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 initTheme();
		 }
	 @Override
	 protected void onResume()
		{
		 if(localThemeSwitcher!=themeSwitcher)
			{
			 localThemeSwitcher=themeSwitcher;
			 resetActivity();
			 }
		 super.onResume();
		 }
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu)
		 {
		 setMenuLayout(menu);
		 menu.add(0,22,0,"Reset");
		 return super.onCreateOptionsMenu(menu);
		 }
	@Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 int id = item.getItemId();
		 switch(id)
			{
			 case 22:
				 resetActivity();
				 return true;
			 case R.id.menu_settings:
				 goToSettings();
				 return true;
			 case R.id.menu_exit:
				 exit();
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 @Override
	 public boolean onMenuOpened(int featureId, Menu menu)
		{
		 if(featureId == Window.FEATURE_ACTION_BAR && menu != null)
			 if(menu.getClass().getSimpleName().equals("MenuBuilder"))
				 try
					{
					 Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					 m.setAccessible(true);
					 m.invoke(menu, true);
					 }
				 catch(NoSuchMethodException e)
					{
					 Log.d(O.TAG,"onMenuOpened",e);
					 }
				 catch(Exception e)
					{
					 throw new RuntimeException(e);
					 }
		 return super.onMenuOpened(featureId, menu);
		 }
	}
