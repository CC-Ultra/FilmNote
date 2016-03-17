package com.snayper.filmsnote.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.Utils.O;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Serial extends MainListFragment implements AdapterInterface
	{
	 private class SerialListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Intent jumper= new Intent(getActivity(), EditActivity.class);
			 jumper.putExtra("Content type",contentType);
			 jumper.putExtra("Db position",position);
			 startActivity(jumper);
			 }
		 }
	 private class SerialListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			{
			 String txtLeft="Удалить";
			 String txtRight="Обновить информацию";
			 String message="";
			 int listenerLeft= O.dialog.LISTENER_MAIN_LIST_DEL;
			 int listenerRight= O.dialog.LISTENER_MAIN_LIST_UPDATE;
			 ActionDialog dialog= new ActionDialog();
			 dialog.viceConstructor(Fragment_Serial.this,contentType,position,message,txtLeft,txtRight,listenerLeft,listenerRight);
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
	 @Override
	 @SuppressWarnings("deprecation")
	 public void initAdapter()
		{
		 adapter= new SimpleCursorAdapter(getActivity(), listElementLayout, DbHelper.cursors[contentType], dbListFrom, dbListTo);
		 list.setAdapter(adapter);
		 ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FF12212f"));
		 list.setDivider(divcolor);
		 list.setDividerHeight(2);
		 }
	 }
