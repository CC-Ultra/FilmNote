package fnote.snayper.com.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import fnote.snayper.com.Activities.AddActivity;
import fnote.snayper.com.Activities.WebActivity;
import fnote.snayper.com.Utils.DbHelper;
import fnote.snayper.com.Utils.O;
import fnote.snayper.com.Utils.Record_Serial;
import fnote.snayper.com.Utils.Util;

import java.util.HashMap;

/**
 * Created by snayper on 24.02.2016.
 */
public class ActionDialog extends DialogFragment
	{
	 private AdapterInterface parent;
	 private int contentType;
	 public int action;
	 private int position;
	 public String message;
	 private int buttonsNum;
	 private String leftText,rightText, centralText;
	 private int leftListener,rightListener, centralListener;

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
			 DbHelper.deleteRecord(contentType,position);
			 parent.initAdapter();
			 }
		 }
	 private class MainUpdateListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Intent jumper= new Intent(getActivity(), WebActivity.class);
			 jumper.putExtra("Content type",contentType);
			 jumper.putExtra("Position",position);
			 jumper.putExtra("Action",O.interaction.WEB_ACTION_UPDATE);
			 startActivity(jumper);
			 }
		 }
	 private class AddOnlineListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Intent jumper= new Intent(getActivity(), WebActivity.class);
			 jumper.putExtra("Content type",contentType);
			 jumper.putExtra("Action",O.interaction.WEB_ACTION_ADD);
			 startActivity(jumper);
			 }
		 }
	 private class AddOfflineListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Intent jumper= new Intent(getActivity(), AddActivity.class);
			 jumper.putExtra("Content type",contentType);
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
			 default:
				 result=null;
			 }
		 return result;
		 }

	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState)
		{
		 Bundle bundleParams= getArguments();
		 ActionDialogParams params= bundleParams.getParcelable("Params");
		 parent= params.parent;
		 contentType= params.contentType;
		 position= params.position;
		 message= params.message;
		 buttonsNum= params.buttonsNum;
		 leftListener= params.leftListener;
		 rightListener= params.rightListener;
		 centralListener= params.centralListener;
		 leftText= params.leftText;
		 rightText= params.rightText;
		 centralText= params.centralText;

		 AlertDialog.Builder adb = new AlertDialog.Builder(getActivity() );
		 if(buttonsNum==2)
			{
			 adb.setNegativeButton(leftText,getListener(leftListener) );
			 adb.setPositiveButton(rightText,getListener(rightListener) );
			 }
		 else
			{
			 adb.setNegativeButton(leftText,getListener(leftListener) );
			 adb.setPositiveButton(rightText,getListener(rightListener) );
			 adb.setNeutralButton(centralText,getListener(centralListener) );
			 }
		 if(message.length()!=0)
			 adb.setMessage(message);
		 return adb.create();

		 }
	 }
