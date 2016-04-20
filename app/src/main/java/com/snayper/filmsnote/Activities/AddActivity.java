package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.content.res.Resources;
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
import com.snayper.filmsnote.Db.DbConsumer;
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
	 private Toolbar toolbar;
	 private DbConsumer dbConsumer;
	 private int contentType;
	 private int toolbarTextColor,toolbarBackgroundColor;
	 private boolean updated;

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
		 toolbar=(Toolbar) findViewById(R.id.toolbar);
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
		 if(contentType==O.interaction.CONTENT_FILMS)
			{
			 Record_Film record= new Record_Film();
			 record.setTitle(title);
			 dbConsumer.putRecord(record);
			 }
		 else
			{
			 Record_Serial record= new Record_Serial();
			 record.setTitle(title);
			 record.setUpdated(updated);
			 dbConsumer.putRecord(record,contentType);
			 }
		 onBackPressed();
		 }
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.add_menu,menu);
		 }
	 @Override
	 protected void putIntentExtra(Intent reset)
		{
		 reset.putExtra(O.mapKeys.extra.CONTENT_TYPE,contentType);
		 }
	 @Override
	 protected void initLayoutThemeCustoms()
		{
		 super.initLayoutThemeCustoms();
		 toolbarTextColor=lightTextColor;
		 toolbarBackgroundColor=panelColor;
		 }
	 @Override
	 protected void setLayoutThemeCustoms()
		{
		 super.setLayoutThemeCustoms();
		 toolbar.setTitleTextColor(toolbarTextColor);
		 toolbar.setBackgroundColor(toolbarBackgroundColor);
	 	 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.add_layout);

		 contentType= getIntent().getIntExtra(O.mapKeys.extra.CONTENT_TYPE,-1);
		 updated= getIntent().getBooleanExtra("Updated",false);

		 Button okButton= (Button)findViewById(R.id.okButton);
		 titleInput= (EditText)findViewById(R.id.titleInput);

		 dbConsumer= new DbConsumer(this,getContentResolver(),contentType);
		 okButton.setOnClickListener(new OkButtonListener() );
		 titleInput.setOnEditorActionListener(new SubmitListener() );
		 initToolbar();
		 setLayoutThemeCustoms();
		 }
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
		{
		 menu.findItem(R.id.menu_convert).setVisible(contentType!=0);
		 return super.onPrepareOptionsMenu(menu);
		 }
	@Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 switch(item.getItemId() )
			{
			 case R.id.menu_convert:
				 toOnline();
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
