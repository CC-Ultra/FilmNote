package fnote.snayper.com.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import fnote.snayper.com.Utils.DbHelper;
import fnote.snayper.com.Utils.O;
import fnote.snayper.com.Utils.Record_Film;
import fnote.snayper.com.Utils.Record_Serial;
import fnote.snayper.com.filmsnote.R;

/**
 * Created by snayper on 18.02.2016.
 */
public class AddActivity extends AppCompatActivity
	{
	 private EditText titleInput;
	 private Button okButton;
	 private int contentType;

	 private class OkButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 String title= titleInput.getText().toString();
			 if(contentType == O.interaction.CONTENT_FILMS)
				{
				 Record_Film record= new Record_Film();
				 record.setTitle(title);
				 DbHelper.putRecord_Films(record,contentType);
				 }
			 else
				{
				 Record_Serial record= new Record_Serial();
				 record.setTitle(title);
				 DbHelper.putRecord_Serial(record,contentType);
				 }
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
