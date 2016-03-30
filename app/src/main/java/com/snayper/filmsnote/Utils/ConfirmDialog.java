package com.snayper.filmsnote.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Interfaces.DialogDecision;

/**
 * Created by snayper on 30.03.2016.
 */
public class ConfirmDialog
	{
	 AppCompatActivity activity;
	 DialogDecision parent;
	 int yesId,noId=-1;

	 class YesListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 parent.sayYes(yesId);
			 }
		 }
	 class NoListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 parent.sayNo(noId);
			 }
		 }

	 public ConfirmDialog(AppCompatActivity _activity,DialogDecision _parent,int _yesId)
		{
		 activity=_activity;
		 parent=_parent;
		 yesId=_yesId;
		 action();
		 }
	 public ConfirmDialog(AppCompatActivity _activity,DialogDecision _parent,int _yesId,int _noId)
		{
		 this(_activity,_parent,_yesId);
		 noId=_noId;
		 action();
		 }
	 void action()
		{
		 ActionDialog dialog= new ActionDialog();
		 dialog.viceConstructor("Ты точно уверен?","Конечно!","Не надо",new YesListener(),new NoListener() );
		 dialog.show(activity.getSupportFragmentManager(),"");
		 }
	 }