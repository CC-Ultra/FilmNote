package fnote.snayper.com.filmsnote.p1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import fnote.snayper.com.filmsnote.R;

public class MainActivity extends AppCompatActivity
	{
	 DbHelper dbHelper;
	 static SQLiteDatabase db;
	 Toolbar toolbar;
	 ViewPager viewPager;
	 static Cursor cursor[]= new Cursor[3];

	 class JmpButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Intent jumper= new Intent(MainActivity.this, EditActivity.class);
			 jumper.putExtra("Content type",2);
			 jumper.putExtra("Title","Game of thrones");
			 jumper.putExtra("Watched episodes",7);
			 jumper.putExtra("All episodes",10);
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
		 viewPager = (ViewPager) findViewById(R.id.viewPager);
		 TabsFragmentAdapter adapter = new TabsFragmentAdapter(this, getSupportFragmentManager() );
		 viewPager.setAdapter(adapter);

		 TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		 tabLayout.setupWithViewPager(viewPager);
		 }
	 static void initCursors()
		{
		 String fields[][]=
				{
					{DbHelper._ID, O.FIELD_NAME_TITLE, O.FIELD_NAME_DATE, O.FIELD_NAME_FLAG},
					{DbHelper._ID, O.FIELD_NAME_TITLE, O.FIELD_NAME_ALL, O.FIELD_NAME_WATCHED, O.FIELD_NAME_DATE}
				 };
		 cursor[0] = db.query(O.TABLE_NAME[0], fields[0], null, null, null, null, null);
		 cursor[1] = db.query(O.TABLE_NAME[1], fields[1], null, null, null, null, null);
		 cursor[2] = db.query(O.TABLE_NAME[2], fields[1], null, null, null, null, null);
//		 cursor[1].get
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.main_layout);

		 dbHelper= new DbHelper(this,null,null,O.DB_VERSION);
		 db = dbHelper.getWritableDatabase();
		 initCursors();
		 initToolbar();
		 initTabs();
		 Button jmpButton= (Button)findViewById(R.id.jmpButton);
		 jmpButton.setOnClickListener(new JmpButtonListener() );
		 }
	 }
