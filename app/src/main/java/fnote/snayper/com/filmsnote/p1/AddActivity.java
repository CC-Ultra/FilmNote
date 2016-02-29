package fnote.snayper.com.filmsnote.p1;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import fnote.snayper.com.filmsnote.R;

import java.util.Calendar;

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
				 DbHelper.putRecord_Films(new Record_Film(titleInput.getText().toString() ), contentType);
			 else
				 DbHelper.putRecord_Serial(new Record_Serial(titleInput.getText().toString() ), contentType);
			 onBackPressed();
			 }
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
