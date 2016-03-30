package com.snayper.filmsnote.Activities;

import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.snayper.filmsnote.Adapters.TabsFragmentAdapter;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.Interfaces.DialogDecision;
import com.snayper.filmsnote.Utils.ConfirmDialog;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

public class MainActivity extends GlobalMenuOptions implements DialogDecision
	{
	 public static SharedPreferences prefs;
	 private TabLayout tabLayout;
	 private TabsFragmentAdapter tabsFragmentAdapter;
	 private int themeSwitcher;

	 private class testButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 ;
			 }
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
	 private void initThemeSwither()
		{
		 prefs= getSharedPreferences(O.mapKeys.prefs.PREFS_FILENAME, MODE_PRIVATE);
		 themeSwitcher= prefs.getInt(O.mapKeys.prefs.THEME, 0);
		 }
	 private void updateThemeSwitcher()
		{
		 SharedPreferences.Editor editor= prefs.edit();
		 editor.putInt(O.mapKeys.prefs.THEME, themeSwitcher).apply();
		 }
	 private void initToolbar()
		{
		 Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
		 setSupportActionBar(toolbar);
		 toolbar.setTitle("Looker");
		 }
	 private void initTabs()
		{
		 ViewPager viewPager=(ViewPager) findViewById(R.id.viewPager);
		 tabsFragmentAdapter= new TabsFragmentAdapter(getSupportFragmentManager() );
		 viewPager.setAdapter(tabsFragmentAdapter);

		 tabLayout= (TabLayout)findViewById(R.id.tabLayout);
		 tabLayout.getSelectedTabPosition();
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
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.main_layout);

		 DbHelper dbHelper= new DbHelper(this, O.db.DB_FILENAME, null, O.db.DB_VERSION);
		 dbHelper.initDb();
		 DbHelper.initCursors();
		 initToolbar();
		 initTabs();

//		 Button testButton= (Button)findViewById(R.id.testButton);
//		 testButton.setOnClickListener(new testButtonListener() );
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
	 }
