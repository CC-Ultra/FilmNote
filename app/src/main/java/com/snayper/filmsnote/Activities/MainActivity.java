package com.snayper.filmsnote.Activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.snayper.filmsnote.Adapters.TabsFragmentAdapter;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.Interfaces.DialogDecision;
import com.snayper.filmsnote.Services.Updater;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;

public class MainActivity extends GlobalMenuOptions implements DialogDecision
	{
	 public static SharedPreferences prefs;
	 private TabLayout tabLayout;
	 private Toolbar toolbar;
	 private TabsFragmentAdapter tabsFragmentAdapter;
	 private int toolbarTextColor,tabBackgroundColor,tabIndicatorColor,tabTextColor,tabTextColorSelected;
	 private static boolean cowThemeStart=true;

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

	 @Override
	 protected void exit()
		{
		 if(isServiceRunning(Updater.class) )
			 stopService(new Intent(this,Updater.class) );
		 android.os.Process.killProcess(android.os.Process.myPid() );
		 }
	 private void clearMainList()
		{
		 int contentType= tabLayout.getSelectedTabPosition();
		 MainListFragment fragment= (MainListFragment)tabsFragmentAdapter.getItem(contentType);
		 while(fragment.getListCount()!=0)
			{
			 DbHelper.deleteRecord(this,contentType,0);
			 fragment.initAdapter();
			 }
		 }
	 @Override
	 protected void initTheme()
		{
		 initPrefs();
		 super.initTheme();
		 }
	 private void initPrefs()
		{
		 prefs= getSharedPreferences(O.mapKeys.prefs.PREFS_FILENAME, MODE_PRIVATE);
//		 if(cowThemeStart)
//			{
//			 prefs.edit().putInt(O.mapKeys.prefs.THEME,2).apply();
//			 cowThemeStart=!cowThemeStart;
//			 }
		 themeSwitcher= prefs.getInt(O.mapKeys.prefs.THEME, 0);
		 }
	 private void initToolbar()
		{
		 toolbar= (Toolbar)findViewById(R.id.toolbar);
		 setSupportActionBar(toolbar);
		 toolbar.setTitle("Looker");
		 }
	 private void initTabs()
		{
		 ViewPager viewPager=(ViewPager) findViewById(R.id.viewPager);
		 tabsFragmentAdapter= new TabsFragmentAdapter(getSupportFragmentManager() );
		 viewPager.setAdapter(tabsFragmentAdapter);

		 tabLayout= (TabLayout)findViewById(R.id.tabLayout);
		 tabLayout.setupWithViewPager(viewPager);
		 }
	 @Override
	 public void sayNo(int noId) {}
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
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.main_menu,menu);
		 }
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

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 Log.d(O.TAG,"onCreate: main");
		 setContentView(R.layout.main_layout);

		 DbHelper dbHelper= new DbHelper(this);
		 dbHelper.initDb();
		 DbHelper.initCursors();
		 initToolbar();
		 initTabs();

//		 Button testButton= (Button)findViewById(R.id.testButton);
//		 testButton.setOnClickListener(new testButtonListener() );
		 Button doButton= (Button)findViewById(R.id.doButton);
		 doButton.setOnClickListener(new testButtonListener() );
		 Button undoButton= (Button)findViewById(R.id.undoButton);
		 undoButton.setOnClickListener(new undoTestButtonListener());
		 setLayoutThemeCustoms();
		 }
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
		{
		 boolean x= DbHelper.cursors[tabLayout.getSelectedTabPosition() ].getCount()!=0;
		 menu.findItem(R.id.menu_deleteAll).setVisible(x);
		 return super.onPrepareOptionsMenu(menu);
		 }
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 int id = item.getItemId();
		 switch(id)
			{
			 case R.id.menu_deleteAll:
				 new ConfirmDialog(this,this,0);
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data)
		{
		 super.onActivityResult(requestCode, resultCode, data);
		 switch(resultCode)
			{
//			 case O.interaction.SERVICE_RESULT_:
//				 break;
			 }
		 }
	 }
