package fnote.snayper.com.filmsnote.p1;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import fnote.snayper.com.filmsnote.R;

public class MainActivity extends AppCompatActivity
	{
	 DbHelper dbHelper;
	 Toolbar toolbar;
	 ViewPager viewPager;

	 class testButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 String txtLeft="Cancel";
			 String txtRight="Delete";
			 int contentType=1;
			 int dbPosition=1;
			 int listenerLeft=O.dialog.LISTENER_SERIAL_CANCEL;
			 int listenerRight=O.dialog.LISTENER_SERIAL_DEL;
			 ActionDialog dialog= new ActionDialog();
			 Bundle paramsBundle= new Bundle();
			 ActionDialogParams params= new ActionDialogParams(new EditActivity(),contentType,dbPosition,txtLeft,txtRight,listenerLeft,listenerRight);
			 paramsBundle.putParcelable("Params",params);
			 dialog.setArguments(paramsBundle);
			 Log.d("c123","Bundle задан");
			 dialog.show(getSupportFragmentManager(),"");
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

//		 Button testButton= (Button)findViewById(R.id.testButton);
//		 testButton.setOnClickListener(new testButtonListener() );
		 }
	 }
