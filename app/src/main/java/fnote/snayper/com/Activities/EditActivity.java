package fnote.snayper.com.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import fnote.snayper.com.Adapters.CustomSimpleAdapter;
import fnote.snayper.com.Fragments.ActionDialog;
import fnote.snayper.com.Fragments.ActionDialogParams;
import fnote.snayper.com.Fragments.AdapterInterface;
import fnote.snayper.com.Utils.DbHelper;
import fnote.snayper.com.Utils.Record_Serial;
import fnote.snayper.com.filmsnote.R;
import fnote.snayper.com.Utils.O;
import fnote.snayper.com.Utils.Util;

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
				 episodeList.setSelection(watched);
				 }
			 }
		 }
	 private class ListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			 {
			  String txtLeft="Cancel";
			  String txtRight="Delete";
			  int listenerLeft=O.dialog.LISTENER_SERIAL_CANCEL;
			  int listenerRight=O.dialog.LISTENER_SERIAL_DEL;
			  ActionDialog dialog= new ActionDialog();
			  Bundle paramsBundle= new Bundle();
			  ActionDialogParams params= new ActionDialogParams(EditActivity.this,contentType,dbPosition,txtLeft,txtRight,listenerLeft,listenerRight);
			  paramsBundle.putParcelable("Params",params);
			  dialog.setArguments(paramsBundle);
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

		 watchButton= (Button)findViewById(R.id.watchButton);
		 episodeList= (ListView)findViewById(R.id.episodeList);
		 titleTxtView= (TextView)findViewById(R.id.title);
		 dateTxtView= (TextView)findViewById(R.id.date);
		 img= (ImageView)findViewById(R.id.img);

		 initAdapter();
		 episodeList.setOnItemClickListener(new ListItemClickListener_Watch() );
		 episodeList.setOnItemLongClickListener(new ListItemLongClickListener());
		 titleTxtView.setText(titleSrc);
		 dateTxtView.setText(dateSrc);
		 watchButton.setOnClickListener(new AddButtonListener() );
		 }
	}
