package com.snayper.filmsnote.Activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Services.Updater;
import com.snayper.filmsnote.Utils.O;

/**
 * Created by snayper on 29.03.2016.
 */
public class GlobalMenuOptions extends AppCompatActivity
	{
	 public static int themeSwitcher;

	 protected boolean isServiceRunning(Class<?> serviceClass)
		{
		 ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		 for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE) )
			if(serviceClass.getName().equals(service.service.getClassName() ) )
				 return true;
		 return false;
		 }
	 protected void setLayoutThemeCustoms() {}
	 protected void initTheme()
		{
		 int res=0;
		 switch(themeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 res= R.style.Theme_Mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 res= R.style.Theme_Ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 res= R.style.Theme_Cow;
				 break;
			 default:
				 res= R.style.Theme_Mentor;
			 }
		 setTheme(res);
		 setLayoutThemeCustoms();
		 }
	 protected void resetActivity()
		{
		 finish();
		 Intent reset= new Intent(this, this.getClass() );
		 reset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 reset.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 putIntentExtra(reset);
		 startActivity(reset);
		 }
	 protected void exit()
		{
		 if(isServiceRunning(Updater.class) )
			 stopService(new Intent(this,Updater.class) );
		 android.os.Process.killProcess(android.os.Process.myPid() );
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
	 }
