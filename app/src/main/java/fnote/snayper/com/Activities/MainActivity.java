package fnote.snayper.com.Activities;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import fnote.snayper.com.Adapters.TabsFragmentAdapter;
import fnote.snayper.com.Utils.DbHelper;
import fnote.snayper.com.filmsnote.R;
import fnote.snayper.com.Utils.O;

public class MainActivity extends AppCompatActivity
	{
	 private DbHelper dbHelper;
	 private Toolbar toolbar;
	 private ViewPager viewPager;

	 private class testButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Intent jumper= new Intent(MainActivity.this, ImageActivity.class);
			 startActivity(jumper);
			 }
		 }

	 private void initToolbar()
		{
		 toolbar = (Toolbar) findViewById(R.id.toolbar);
		 toolbar.setTitle("Note");
		 }
	 private void initTabs()
		{
		 viewPager= (ViewPager)findViewById(R.id.viewPager);
		 TabsFragmentAdapter adapter= new TabsFragmentAdapter(getSupportFragmentManager() );
		 viewPager.setAdapter(adapter);

		 TabLayout tabLayout= (TabLayout)findViewById(R.id.tabLayout);
		 tabLayout.setupWithViewPager(viewPager);
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.main_layout);

		 dbHelper= new DbHelper(this, O.db.DB_FILENAME, null, O.db.DB_VERSION);
		 dbHelper.initDb();
		 DbHelper.initCursors();
		 initToolbar();
		 initTabs();

		 Button testButton= (Button)findViewById(R.id.testButton);
		 testButton.setOnClickListener(new testButtonListener() );
		 }
	 }
