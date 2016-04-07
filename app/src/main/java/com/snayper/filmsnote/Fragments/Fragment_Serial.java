package com.snayper.filmsnote.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Adapters.CustomCursorAdapter_Serial;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.Utils.O;

import java.util.HashMap;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Serial extends MainListFragment
	{
	 private class SerialListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Intent jumper= new Intent(getActivity(), EditActivity.class);
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_UPDATE_MARK,false);
			 DbHelper.updateRecord(contentType,position,data);
			 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
			 jumper.putExtra(O.mapKeys.extra.POSITION, position);
			 startActivity(jumper);
			 }
		 }
	 private class SerialListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			{
			 String txtLeft="Удалить";
			 int listenerLeft= O.dialog.LISTENER_MAIN_LIST_DEL;
			 ActionDialog dialog= new ActionDialog();
			 dialog.viceConstructor(Fragment_Serial.this,contentType,position,txtLeft,listenerLeft);
			 dialog.show(getActivity().getSupportFragmentManager(), "");
			 return true;
			 }
		 }

	 public Fragment_Serial()
		{
		 super();
		 }
	 @Override
	 protected void setListener_listOnClick()
		{
		 list.setOnItemClickListener(new SerialListItemClickListener() );
		 }
	 @Override
	 protected void setListener_listOnLongClick()
		{
		 list.setOnItemLongClickListener(new SerialListItemLongClickListener() );
		 }
	 }
