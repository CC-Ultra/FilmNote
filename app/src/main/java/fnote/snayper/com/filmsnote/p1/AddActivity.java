package fnote.snayper.com.filmsnote.p1;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import fnote.snayper.com.filmsnote.R;

/**
 * Created by snayper on 18.02.2016.
 */
public class AddActivity extends AppCompatActivity
	{
	 EditText titleInput;
	 Button okButton;
	 int contentType;

	 class OkButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 if(contentType==0)
				 putRecord_Films(new Record_Film(titleInput.getText().toString() ), contentType);
			 else
				 putRecord_Serial(new Record_Serial(titleInput.getText().toString() ), contentType);
			 onBackPressed();
			 }
		 }

	 void putRecord_Serial(Record_Serial rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.FIELD_NAME_TITLE, rec.title);
		 newRecord.put(O.FIELD_NAME_DATE, rec.date);
		 newRecord.put(O.FIELD_NAME_ALL, ""+ rec.all);
		 newRecord.put(O.FIELD_NAME_WATCHED, ""+ rec.watched);
		 newRecord.put(O.FIELD_NAME_FLAG, "");
		 MainActivity.db.insert(O.TABLE_NAME[tableNum], null, newRecord);
		 }
	 void putRecord_Films(Record_Film rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.FIELD_NAME_TITLE, rec.title);
		 newRecord.put(O.FIELD_NAME_DATE, rec.date);
		 newRecord.put(O.FIELD_NAME_ALL, "");
		 newRecord.put(O.FIELD_NAME_WATCHED, "");
		 newRecord.put(O.FIELD_NAME_FLAG, ""+ rec.watched);
		 MainActivity.db.insert(O.TABLE_NAME[tableNum], null, newRecord);
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.add_layout);

		 contentType= getIntent().getIntExtra("Content type",-1);
		 okButton= (Button)findViewById(R.id.okButton);
		 titleInput= (EditText)findViewById(R.id.titleInput);
		 okButton.setOnClickListener(new OkButtonListener() );
		 }
	 }
