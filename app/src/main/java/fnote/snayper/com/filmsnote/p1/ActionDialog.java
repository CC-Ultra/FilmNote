package fnote.snayper.com.filmsnote.p1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;
import java.util.HashMap;

/**
 * Created by snayper on 24.02.2016.
 */
public class ActionDialog extends DialogFragment
	{
	 AdapterInterface parent;
	 int contentType;
	 int position;
	 int buttonsNum;
	 String leftText,rightText, centralText;
	 int leftListener,rightListener, centralListener;

	 class FilmCancelListener implements DialogInterface.OnClickListener
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
	 class FilmWatchListener implements DialogInterface.OnClickListener
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
	 class SerialCancelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Record_Serial record= DbHelper.extractRecord_Serial(contentType,position);
			 if(record.watched>0)
				{
				 record.watched--;
				 HashMap<String,Object> data= new HashMap<>();
				 data.put(O.db.FIELD_NAME_WATCHED,record.watched);
				 DbHelper.updateRecord(contentType,position,data);
				 parent.initAdapter();
				 }
			 }
		 }
	 class SerialDelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Record_Serial record= DbHelper.extractRecord_Serial(contentType,position);
			 if(record.all>0)
				{
				 record.all--;
				 if(record.all < record.watched)
					 record.watched=record.all;
				 HashMap<String,Object> data= new HashMap<>();
				 data.put(O.db.FIELD_NAME_ALL,record.all);
				 data.put(O.db.FIELD_NAME_WATCHED,record.watched);
				 DbHelper.updateRecord(contentType,position,data);
				 parent.initAdapter();
				 }
			 }
		 }
	 class MainDelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 DbHelper.deleteRecord(contentType,position);
			 parent.initAdapter();
			 }
		 }
	 class MainUpdateListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 String content= (contentType==0 ? "Film" : (contentType==1 ? "Serial" : "Mult") );
			 String str= content +"\nPosition: "+ position +"\nUpdate";
			 Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
			 parent.initAdapter();
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
		 return adb.create();
		 }
	 }
