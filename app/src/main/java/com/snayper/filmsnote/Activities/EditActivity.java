package com.snayper.filmsnote.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.snayper.filmsnote.Adapters.CustomSimpleAdapter;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Fragments.AdapterInterface;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by snayper on 22.02.2016.
 */
public class EditActivity extends AppCompatActivity implements AdapterInterface
	{
	 private Button watchButton;
	 private ListView episodeList;
	 private CustomSimpleAdapter adapter;
	 private ImageView img;
	 private TextView titleTxtView, dateTxtView;
	 private int watched,all;
	 private int contentType,dbPosition;

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
			 episodeList.setSelection(episodeList.getCount());
			 }
		 }
	 private class ListItemClickListener_Watch implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 if(watched<all)
				{
				 watched++;
				 HashMap<String,Object> data= new HashMap<>();
				 data.put(O.db.FIELD_NAME_WATCHED,watched);
				 DbHelper.updateRecord(contentType,dbPosition,data);
				 initAdapter();
				 if(watched-3 > 0)
					 episodeList.setSelection(watched-3);
				 }
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
		 adapter= new CustomSimpleAdapter(EditActivity.this, listData, R.layout.edit_list_element, from, to);
		 episodeList.setAdapter(adapter);
		 ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FF12212f"));
		 episodeList.setDivider(divcolor);
		 episodeList.setDividerHeight(2);
		 }
	 private void updateDate()
		{
		 String dateSrc= Util.getCurentDate();
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_DATE,dateSrc);
		 DbHelper.updateRecord(contentType,dbPosition,data);
		 dateTxtView.setText(dateSrc);
		 }
	 @Override
	 public Context getContext()
		{
		 return this;
		 }

	@Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.edit_layout);

		 Intent intent=getIntent();
		 contentType=intent.getIntExtra("Content type",-1);
		 dbPosition=intent.getIntExtra("Db position",-1);
		 Record_Serial record= DbHelper.extractRecord_Serial(contentType,dbPosition);
		 String titleSrc= record.getTitle();
		 String dateSrc= record.getDate();
		 String imgSrc= FileManager.getStoredPicURI(this, record.getImgSrc() );

		 watchButton= (Button)findViewById(R.id.watchButton);
		 episodeList= (ListView)findViewById(R.id.episodeList);
		 titleTxtView= (TextView)findViewById(R.id.title);
		 dateTxtView= (TextView)findViewById(R.id.date);
		 img= (ImageView)findViewById(R.id.img);

		 initAdapter();
		 episodeList.setOnItemClickListener(new ListItemClickListener_Watch());
		 episodeList.setOnItemLongClickListener(new ListItemLongClickListener());
		 titleTxtView.setText(titleSrc);
		 dateTxtView.setText(dateSrc);
		 watchButton.setOnClickListener(new AddButtonListener() );
		 if(imgSrc.length() != 0)
			 img.setImageURI(Uri.parse(imgSrc) );
		 }
	}
