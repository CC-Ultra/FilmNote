package com.snayper.filmsnote.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.snayper.filmsnote.Adapters.CustomCursorAdapter_Serial;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Db.DbCursorLoader;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.*;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by snayper on 17.04.2016.
 */
public class TestActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>
	{
	 CustomCursorAdapter_Serial adapter;
	 DbConsumer dbConsumer;
	 EditText input;

	 protected String dbListFrom[]= {O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_ALL, O.db.FIELD_NAME_WATCHED, O.db.FIELD_NAME_DATE};
	 protected int dbListTo[]= {R.id.title, R.id.newEpisodes, R.id.watchedEpisodes, R.id.lastDate};

	 private class DeleteListener implements ListView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 dbConsumer.deleteRecord(position);
			 }
		 }
	 private class TestListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 if(!isInputValid() )
				{
				 Toast.makeText(TestActivity.this,"Херовый ввод",Toast.LENGTH_SHORT).show();
				 return;
				 }
			 String line= input.getText().toString();
			 String params[]= line.split(", ");
			 String title= params[0];
			 int watched= Integer.parseInt(params[1] );
			 int all= Integer.parseInt(params[2] );
			 Date date= DateUtil.getCurrentDate();
			 Record_Serial record= new Record_Serial();
			 record.setDate(date);
			 record.setTitle(title);
			 record.setWatched(watched);
			 record.setAll(all);

			 switch(v.getId() )
				{
				 case R.id.insertButton:
					 dbConsumer.putRecord(record,O.interaction.CONTENT_SERIAL);
					 break;
				 case R.id.clearButton:
					 dbConsumer.clear();
					 break;
				 case R.id.updateButton:
					 int position;
					 try
						{
						 position= Integer.parseInt(params[3] );
						 }
					 catch(Exception e)
						{
						 Toast.makeText(TestActivity.this,"Херовый ввод",Toast.LENGTH_SHORT).show();
						 return;
						 }
					 HashMap<String,Object> data= new HashMap<>();
					 data.put(O.db.FIELD_NAME_TITLE,title);
					 data.put(O.db.FIELD_NAME_WATCHED,watched);
					 data.put(O.db.FIELD_NAME_UPDATE_MARK,true);
					 dbConsumer.updateRecord(O.interaction.CONTENT_SERIAL,position,data);
					 break;
				 }
			 }
		 }

	 boolean isInputValid()
		{
		 String line= input.getText().toString();
		 String params[]= line.split(", ");
		 if(params.length!=3 && params.length!=4)
			 return false;
		 if(params[0].length()==0)
			 return false;
		 try
			{
			 Integer.parseInt(params[1] );
			 Integer.parseInt(params[2] );
			 }
		 catch(Exception e)
			{
			 return false;
			 }
		 return true;
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.test_layout);

		 ListView list= (ListView)findViewById(R.id.list);
		 Button insertButton= (Button)findViewById(R.id.insertButton);
		 Button updateButton= (Button)findViewById(R.id.updateButton);
		 Button clearButton= (Button)findViewById(R.id.clearButton);
		 input= (EditText)findViewById(R.id.paramsInput);

		 insertButton.setOnClickListener(new TestListener() );
		 updateButton.setOnClickListener(new TestListener());
		 clearButton.setOnClickListener(new TestListener());
		 list.setOnItemClickListener(new DeleteListener());

		 getSupportLoaderManager().initLoader(0,null,this);
		 dbConsumer= new DbConsumer(this, getContentResolver(), getSupportLoaderManager().getLoader(0), O.interaction.CONTENT_SERIAL);
		 adapter= new CustomCursorAdapter_Serial(this,O.interaction.CONTENT_SERIAL,R.layout.main_list_element_serial,null,dbListFrom,dbListTo);
		 list.setAdapter(adapter);
		 }
	 @Override
	 public Loader<Cursor> onCreateLoader(int id,Bundle args)
		{
		 return new DbCursorLoader(this, getContentResolver(), O.interaction.CONTENT_SERIAL);
		 }
	 @Override
	 public void onLoadFinished(Loader<Cursor> loader,Cursor cursor)
		{
		 adapter.swapCursor(cursor);
		 }
	 @Override
	 public void onLoaderReset(Loader<Cursor> loader) {}
	 }
