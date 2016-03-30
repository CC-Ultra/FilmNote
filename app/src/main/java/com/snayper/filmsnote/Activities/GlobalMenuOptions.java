package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.snayper.filmsnote.R;

/**
 * Created by snayper on 29.03.2016.
 */
public class GlobalMenuOptions extends AppCompatActivity
	{
//	 int a;

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
		 android.os.Process.killProcess(android.os.Process.myPid() );
		 }
	 protected void goToSettings()
		{
		 Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
//		 Intent jumper= new Intent(this,SettigsActivity.class);
//		 startActivity(jumper);
		 }
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.basic_menu,menu);
		 }
	 protected void putIntentExtra(Intent reset) {}

	 @Override
	 public boolean onCreateOptionsMenu(Menu menu)
		{
		 setMenuLayout(menu);
		 return super.onCreateOptionsMenu(menu);
		 }
//	 @Override
//	 public boolean onPrepareOptionsMenu(Menu menu)
//		{
//		 menu.findItem(R.id.action_settings).setCheckable(true);
//		 return super.onPrepareOptionsMenu(menu);
//		 }
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 int id = item.getItemId();
		 switch(id)
			{
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
