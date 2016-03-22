package com.snayper.filmsnote.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.snayper.filmsnote.Adapters.TabsFragmentAdapter;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

public class MainActivity extends AppCompatActivity
	{
	 private TabLayout tabLayout;
	 private TabsFragmentAdapter tabsFragmentAdapter;

	 private class testButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 class YN implements DialogInterface.OnClickListener
				{
				 @Override
				 public void onClick(DialogInterface dialog,int which)
					{
					 switch(which)
						{
						 case DialogInterface.BUTTON_NEGATIVE:
							 return;
						 case DialogInterface.BUTTON_POSITIVE:
							 clearMainList();
						 }
					 }
				 }
			 AlertDialog.Builder adb= new AlertDialog.Builder(MainActivity.this);
			 adb.setMessage("Ты точно уверен?");
			 adb.setNegativeButton("Ой, нет...",new YN() );
			 adb.setPositiveButton("Конечно!",new YN() );
			 adb.create().show();
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
	 private void initToolbar()
		{
		 Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
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
	 }
