package com.snayper.filmsnote.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.snayper.filmsnote.Adapters.CustomSimpleAdapter;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Fragments.AdapterInterface;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by snayper on 22.02.2016.
 */
public class EditActivity extends AppCompatActivity implements AdapterInterface,WebTaskComleteListener
	{
	 private ListView episodeList;
	 private ImageView img;
	 private TextView titleTxtView, dateTxtView;
	 private int watched,all;
	 private int contentType,dbPosition;
	 private String webSrc;

	 private class AddButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 all++;
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_ALL,all);
			 DbHelper.updateRecord(contentType,dbPosition,data);
			 updateDate();
			 initAdapter();
			 }
		 }
	 private class UpdateButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 AsyncParser parser=null;
			 if(webSrc.contains(O.web.filmix.HOST) )
				 parser= new Parser_Filmix(EditActivity.this, EditActivity.this, webSrc);
			 if(webSrc.contains(O.web.seasonvar.HOST) )
				 parser= new Parser_Seasonvar(EditActivity.this, EditActivity.this, webSrc);
			 if(webSrc.contains(O.web.kinogo.HOST) )
				 parser= new Parser_Kinogo(EditActivity.this, EditActivity.this, webSrc);
			 if(webSrc.contains(O.web.onlineLife.HOST) )
				 parser= new Parser_OnlineLife(EditActivity.this, EditActivity.this, webSrc);
			 if(parser==null)
				{
				 Log.d(O.TAG,"onClick: парсер не был инициализирован, а значит и запущен");
				 return;
				 }
			 parser.execute();
			 }
		 }
	 private class WatchOnlineButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
//			 Intent jumper= new Intent(EditActivity.this,WebActivity_Watch.class);
//			 jumper.putExtra("Serial URL",webSrc);
//			 startActivity(jumper);
			 Uri address = Uri.parse(webSrc);
			 Intent openlinkIntent = new Intent(Intent.ACTION_VIEW, address);
			 startActivity(openlinkIntent);
			 }
		 }
	 private class WatchButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 watchEpisode();
			 }
		 }
	 private class ListItemClickListener_Watch implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 watchEpisode();
			 }
		 }
	 private class ListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			 {
			  String txtLeft="Отменить последнюю";
			  String txtRight="Удалить последнюю";
			  int listenerLeft=O.dialog.LISTENER_SERIAL_CANCEL;
			  int listenerRight=O.dialog.LISTENER_SERIAL_DEL;
			  ActionDialog dialog= new ActionDialog();
			  dialog.viceConstructor(EditActivity.this,contentType,dbPosition,txtLeft,txtRight,listenerLeft,listenerRight);
			  dialog.show(getSupportFragmentManager(),"");
			  return true;
			  }
		 }

	 private void convert()
		{
		 finish();
		 if(webSrc.length()==0)
			{
			 Intent jumper= new Intent(this,WebActivity_Add.class);
			 jumper.putExtra("Content type",contentType);
			 jumper.putExtra("Action",O.interaction.WEB_ACTION_UPDATE);
			 jumper.putExtra("Position",dbPosition);
			 startActivity(jumper);
			 }
		 else
			{
			 webSrc="";
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_WEB,webSrc);
			 DbHelper.updateRecord(contentType,dbPosition,data);
			 Intent reset= new Intent(this,this.getClass() );
			 reset.putExtra("Content type",contentType);
			 reset.putExtra("Db position",dbPosition);
			 reset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 reset.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 startActivity(reset);
			 }
		 }
	 private void watchAll()
		{
		 watched=all;
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_WATCHED,watched);
		 DbHelper.updateRecord(contentType,dbPosition,data);
		 initAdapter();
		 }
	 private void unwatchAll()
		{
		 watched=0;
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_WATCHED,watched);
		 DbHelper.updateRecord(contentType,dbPosition,data);
		 initAdapter();
		 }
	 private void clear()
		{
		 watched=0;
		 all=0;
		 String dateSrc="";
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_WATCHED,watched);
		 data.put(O.db.FIELD_NAME_ALL,all);
		 data.put(O.db.FIELD_NAME_DATE,dateSrc);
		 DbHelper.updateRecord(contentType,dbPosition,data);
		 initAdapter();
		 dateTxtView.setText(dateSrc);
		 }
	 private void watchEpisode()
		{
		 if(watched<all)
			{
			 watched++;
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_WATCHED,watched);
			 DbHelper.updateRecord(contentType,dbPosition,data);
			 initAdapter();
			 if(watched+3 < all)
				 episodeList.setSelection(all- (watched+3) );
			 }
		 }
	 private void updateDate()
		{
		 String dateSrc= Util.getCurentDate();
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_DATE,dateSrc);
		 DbHelper.updateRecord(contentType,dbPosition,data);
		 dateTxtView.setText(dateSrc);
		 }
	 private void initPageByRecord()
		{
		 Record_Serial record= DbHelper.extractRecord_Serial(contentType,dbPosition);
		 String titleSrc=record.getTitle();
		 String dateSrc=record.getDate();
		 String imgSrc=FileManager.getStoredPicURI(this,record.getImgSrc());
		 webSrc= record.getWebSrc();
		 initAdapter();
		 titleTxtView.setText(titleSrc);
		 dateTxtView.setText(dateSrc);
		 if(imgSrc.length() != 0)
			 img.setImageURI(Uri.parse(imgSrc) );
		 }
	 public void initAdapter()
		{
		 Record_Serial record= DbHelper.extractRecord_Serial(contentType,dbPosition);
		 all= record.getAll();
		 watched= record.getWatched();
		 String from[]= {"Episode", "Pic"};
		 int to[]= {R.id.episode, R.id.img};
		 ArrayList< HashMap<String,Object> > listData= new ArrayList<>();
		 HashMap<String,Object> listElementMap;
		 for(int i=0; i<all; i++)
			{
			 listElementMap= new HashMap<>();
			 listElementMap.put("Episode", (i+1) +" Серия");
			 if(i>=watched)
				 listElementMap.put("Pic", false);
			 else
				 listElementMap.put("Pic", true);
			 listData.add(listElementMap);
			 }
		 CustomSimpleAdapter adapter=new CustomSimpleAdapter(EditActivity.this,listData,R.layout.edit_list_element,from,to);
		 episodeList.setAdapter(adapter);
		 ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FF12212f"));
		 episodeList.setDivider(divcolor);
		 episodeList.setDividerHeight(2);
		 }
	 @Override
	 public Context getContext()
		{
		 return this;
		 }
	 @Override
	 public void useParserResult(Record_Serial extractedData)
		{
		 if(extractedData.getAll()!=all)
			 extractedData.setDate(Util.getCurentDate() );
		 ParserResultConsumer.useParserResult(this,extractedData,O.interaction.WEB_ACTION_UPDATE,contentType,dbPosition);
		 initPageByRecord();
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.edit_layout);

		 Intent intent=getIntent();
		 contentType=intent.getIntExtra("Content type",-1);
		 dbPosition=intent.getIntExtra("Db position",-1);

		 Button watchButton=(Button) findViewById(R.id.watchButton);
		 Button allButton=(Button) findViewById(R.id.allButton);
		 episodeList= (ListView)findViewById(R.id.episodeList);
		 titleTxtView= (TextView)findViewById(R.id.title);
		 dateTxtView= (TextView)findViewById(R.id.date);
		 img= (ImageView)findViewById(R.id.img);

		 initPageByRecord();
		 episodeList.setOnItemClickListener(new ListItemClickListener_Watch());
		 episodeList.setOnItemLongClickListener(new ListItemLongClickListener());
		 if(webSrc.length()!=0)
			{
			 allButton.setText("Обновить информацию");
			 allButton.setOnClickListener(new UpdateButtonListener());
			 watchButton.setText("Просмотреть серию");
			 watchButton.setOnClickListener(new WatchOnlineButtonListener() );
			 }
		 else
			{
			 allButton.setText("Добавить серию");
			 allButton.setOnClickListener(new AddButtonListener());
			 watchButton.setText("Отметить серию");
			 watchButton.setOnClickListener(new WatchButtonListener());
			 }
		 }
	}
