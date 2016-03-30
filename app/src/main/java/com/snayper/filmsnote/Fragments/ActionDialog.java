package com.snayper.filmsnote.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import com.snayper.filmsnote.Activities.AddActivity;
import com.snayper.filmsnote.Activities.WebActivity;
import com.snayper.filmsnote.Interfaces.AdapterInterface;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.Utils.O;
import com.snayper.filmsnote.Utils.Record_Serial;
import com.snayper.filmsnote.Utils.Util;

import java.util.HashMap;

/**
 * Created by snayper on 24.02.2016.
 */
public class ActionDialog extends DialogFragment
	{
	 private AdapterInterface parent;
	 private int contentType;
	 private int position;
	 public String message="";
	 private int buttonsNum;
	 private String leftText,rightText="", centralText="";
	 private int leftListener,rightListener=0, centralListener=0;
	 DialogInterface.OnClickListener leftConfirmListener, rightConfirmListener;

	 private class FilmCancelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_FLAG,'f');
			 DbHelper.updateRecord(contentType,position,data);
			 parent.initAdapter();
			 }
		 }
	 private class FilmWatchListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_FLAG,'t');
			 data.put(O.db.FIELD_NAME_DATE,Util.getCurentDate() );
			 DbHelper.updateRecord(contentType,position,data);
			 parent.initAdapter();
			 }
		 }
	 private class SerialCancelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Record_Serial record= DbHelper.extractRecord_Serial(contentType,position);
			 if(record.getWatched() > 0)
				{
				 record.setWatched( record.getWatched()-1 );
				 HashMap<String,Object> data= new HashMap<>();
				 data.put(O.db.FIELD_NAME_WATCHED,record.getWatched() );
				 DbHelper.updateRecord(contentType,position,data);
				 parent.initAdapter();
				 }
			 }
		 }
	 private class SerialDelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Record_Serial record= DbHelper.extractRecord_Serial(contentType,position);
			 if(record.getAll() > 0)
				{
				 record.setAll(record.getAll()-1);
				 if(record.getAll() < record.getWatched() )
					 record.setWatched(record.getAll() );
				 HashMap<String,Object> data= new HashMap<>();
				 data.put(O.db.FIELD_NAME_ALL,record.getAll() );
				 data.put(O.db.FIELD_NAME_WATCHED,record.getWatched() );
				 DbHelper.updateRecord(contentType,position,data);
				 parent.initAdapter();
				 }
			 }
		 }
	 private class MainDelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 DbHelper.deleteRecord(parent.getContext(),contentType,position);
			 parent.initAdapter();
			 }
		 }
	 private class MainUpdateListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Intent jumper= new Intent(getActivity(), WebActivity.class);
			 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
			 jumper.putExtra(O.mapKeys.extra.POSITION, position);
			 jumper.putExtra(O.mapKeys.extra.ACTION, O.interaction.WEB_ACTION_UPDATE);
			 startActivity(jumper);
			 }
		 }
	 private class AddOnlineListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Intent jumper= new Intent(getActivity(), WebActivity.class);
			 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
			 jumper.putExtra(O.mapKeys.extra.ACTION, O.interaction.WEB_ACTION_ADD);
			 startActivity(jumper);
			 }
		 }
	 private class AddOfflineListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Intent jumper= new Intent(getActivity(), AddActivity.class);
			 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
			 startActivity(jumper);
			 }
		 }

	 public ActionDialog()
		{
		 super();
		 }
	 private DialogInterface.OnClickListener getListener(int listener)
		{
		 DialogInterface.OnClickListener result;
		 switch(listener)
			{
			 case O.dialog.LISTENER_FILM_CANCEL:
				 result= new FilmCancelListener();
				 break;
			 case O.dialog.LISTENER_FILM_WATCH:
				 result= new FilmWatchListener();
				 break;
			 case O.dialog.LISTENER_SERIAL_CANCEL:
				 result= new SerialCancelListener();
				 break;
			 case O.dialog.LISTENER_SERIAL_DEL:
				 result= new SerialDelListener();
				 break;
			 case O.dialog.LISTENER_MAIN_LIST_DEL:
				 result= new MainDelListener();
				 break;
			 case O.dialog.LISTENER_MAIN_LIST_UPDATE:
				 result= new MainUpdateListener();
				 break;
			 case O.dialog.LISTENER_ADD_ONLINE:
				 result= new AddOnlineListener();
				 break;
			 case O.dialog.LISTENER_ADD_OFFLINE:
				 result= new AddOfflineListener();
				 break;
			 case O.dialog.LISTENER_CONFIRM_YES:
				 result=leftConfirmListener;
				 break;
			 case O.dialog.LISTENER_CONFIRM_NO:
				 result=rightConfirmListener;
				 break;
			 default:
				 result=null;
			 }
		 return result;
		 }
	 public void viceConstructor(AdapterInterface _parent,int _contentType,int _position,String _leftText,int _leftListener)
		{
		 buttonsNum=1;
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 leftListener=_leftListener;
		 leftText=_leftText;
		 }
	 public void viceConstructor(AdapterInterface _parent,int _contentType,int _position,String _leftText,String _rightText,int _leftListener,int _rightListener)
		{
		 buttonsNum=2;
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 leftListener=_leftListener;
		 rightListener=_rightListener;
		 leftText=_leftText;
		 rightText=_rightText;
		 }
	 public void viceConstructor(AdapterInterface _parent,int _contentType,int _position,String _message,String _leftText,String _rightText,int _leftListener,int _rightListener)
		{
		 buttonsNum=2;
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 message=_message;
		 leftListener=_leftListener;
		 rightListener=_rightListener;
		 leftText=_leftText;
		 rightText=_rightText;
		 }
	 public void viceConstructor(String _message,String _leftText,String _rightText,DialogInterface.OnClickListener _leftConfirmListener,DialogInterface.OnClickListener _rightConfirmListener)
		{
		 buttonsNum=2;
		 message=_message;
		 leftListener= O.dialog.LISTENER_CONFIRM_YES;
		 rightListener= O.dialog.LISTENER_CONFIRM_NO;
		 leftText=_leftText;
		 rightText=_rightText;
		 leftConfirmListener=_leftConfirmListener;
		 rightConfirmListener=_rightConfirmListener;
		 }

	 @NonNull
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState)
		{
		 AlertDialog.Builder adb = new AlertDialog.Builder(getActivity() );
		 switch(buttonsNum)
			{
			 case 1:
				 adb.setNegativeButton(leftText,getListener(leftListener) );
				 break;
			 case 2:
				 adb.setNegativeButton(leftText,getListener(leftListener) );
				 adb.setPositiveButton(rightText,getListener(rightListener) );
				 break;
			 case 3:
				 adb.setNegativeButton(leftText,getListener(leftListener) );
				 adb.setPositiveButton(rightText,getListener(rightListener) );
				 adb.setNeutralButton(centralText,getListener(centralListener) );
			 }
		 if(message.length()!=0)
			 adb.setMessage(message);
		 return adb.create();

		 }
	 }
