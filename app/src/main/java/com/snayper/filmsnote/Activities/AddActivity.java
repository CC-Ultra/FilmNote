package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.Utils.O;
import com.snayper.filmsnote.Utils.Record_Film;
import com.snayper.filmsnote.Utils.Record_Serial;
import com.snayper.filmsnote.R;

/**
 * Created by snayper on 18.02.2016.
 */
public class AddActivity extends GlobalMenuOptions
	{
	 private EditText titleInput;
	 private Button okButton;
	 private int contentType;

	 private class OkButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 acceptPress();
			 }
		 }
	 private class SubmitListener implements TextView.OnEditorActionListener
		{
		 @Override
		 public boolean onEditorAction(TextView v,int actionId,KeyEvent event)
			{
			 acceptPress();
			 return false;
			 }
		 }

	 private void toOnline()
		{
		 finish();
		 Intent jumper= new Intent(this,WebActivity.class);
		 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
		 jumper.putExtra(O.mapKeys.extra.ACTION, O.interaction.WEB_ACTION_ADD);
		 startActivity(jumper);
		 }
	 private boolean easterCheck(String secretKey)
		{
		 return secretKey.toLowerCase().trim().equals("955653 ограбить корован!");
		 }
	 private void initToolbar()
		{
		 Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
		 setSupportActionBar(toolbar);
		 toolbar.setTitle("Note");
		 }
	 private void acceptPress()
		{
		 String title= titleInput.getText().toString();
		 if(easterCheck(title) )
			{
			 Intent jumper= new Intent(AddActivity.this,EasterActivity.class);
			 startActivity(jumper);
			 return;
			 }
		 if(title.length()==0)
			 return;
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
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.add_menu, menu);
		 }
	 @Override
	 protected void putIntentExtra(Intent reset)
		{
		 reset.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
		 }

	@Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.add_layout);

		 contentType= getIntent().getIntExtra(O.mapKeys.extra.CONTENT_TYPE, -1);
		 okButton= (Button)findViewById(R.id.okButton);
		 titleInput= (EditText)findViewById(R.id.titleInput);
		 okButton.setOnClickListener(new OkButtonListener() );
		 titleInput.setOnEditorActionListener(new SubmitListener() );
		 initToolbar();
		 }
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 int id = item.getItemId();
		 switch(id)
			{
			 case R.id.menu_convert:
				 toOnline();
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
