package com.snayper.filmsnote.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import com.snayper.filmsnote.Activities.AddActivity;
import com.snayper.filmsnote.Activities.WebActivity;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Interfaces.AdapterInterface;
import com.snayper.filmsnote.Utils.ConfirmDialog;
import com.snayper.filmsnote.Utils.O;
import com.snayper.filmsnote.Utils.Record_Serial;
import com.snayper.filmsnote.Utils.DateUtil;

import java.util.HashMap;

/**
 * <p>Диалог, который строися в зависимости от переданных ему параметров в {@link #viceConstructor}</p>
 * Потенциально может включать в себя 3 кнопки с текстом и Listener-ами и заголовок, но по задаче кнопки всего две или одна.
 * Реализован на базе {@link DialogFragment} и не уходит с активности, в которой был вызван. Может править базу. Если база
 * была изменена, а вызывающая активность или фрагмент реализуют интерфейс {@link AdapterInterface}, то после изменения базы
 * через {@link #parent} перегружается адаптер. Также есть свой {@link #viceConstructor} для {@link ConfirmDialog} и средства
 * его построения
 * <p><s>Если очень хочется добавить третью кнопку, можно дописать трехкнопочный {@link #viceConstructor}</s></p>
 * <p><sub>(24.02.2016)</sub></p>
 * @author CC-Ultra
 * @see ConfirmDialog
 * @see AdapterInterface
 */
public class ActionDialog extends DialogFragment
	{
	 private DbConsumer dbConsumer;
	 private AdapterInterface parent;
	 private int contentType;
	 private int position;
	 public String message="";
	 private int buttonsNum;
	 private String leftText,rightText="", centralText="";
	 private int leftListener,rightListener=0, centralListener=0;
	 DialogInterface.OnClickListener leftConfirmListener, rightConfirmListener;

	/**
	 * Listener для смены статуса фильма на {@code "Не просмотрено"}
	 */
	 private class FilmCancelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_FILM_WATCHED,false);
			 dbConsumer.updateRecord(position,data);
			 parent.initAdapter();
			 }
		 }

	/**
	 * Listener для смены статуса фильма на {@code "Просмотрено"}
	 */
	 private class FilmWatchListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_FILM_WATCHED,true);
			 data.put(O.db.FIELD_NAME_DATE, DateUtil.getCurrentDate().getTime() );
			 dbConsumer.updateRecord(position,data);
			 parent.initAdapter();
			 }
		 }

	/**
	 * Listener для отмены последней просмотренной серии сериала. Проверка, чтобы {@code watched} не падала ниже 0 присутствует
	 */
	 private class SerialCancelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Record_Serial record= dbConsumer.extractRecord_Serial(position);
			 if(record.getWatched() > 0)
				{
				 record.setWatched(record.getWatched()-1);
				 HashMap<String,Object> data= new HashMap<>();
				 data.put(O.db.FIELD_NAME_WATCHED,record.getWatched() );
				 dbConsumer.updateRecord(position,data);
				 parent.initAdapter();
				 }
			 }
		 }

	/**
	 * Listener для удаления последней серии сериала. Проверки, чтобы {@code watched} не превышала {@code all}, и {@code all}
	 * не падала ниже 0 присутствуют
	 */
	 private class SerialDelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 Record_Serial record= dbConsumer.extractRecord_Serial(position);
			 if(record.getAll() > 0)
				{
				 record.setAll(record.getAll()-1);
				 if(record.getAll() < record.getWatched() )
					 record.setWatched(record.getAll() );
				 HashMap<String,Object> data= new HashMap<>();
				 data.put(O.db.FIELD_NAME_ALL,record.getAll() );
				 data.put(O.db.FIELD_NAME_WATCHED,record.getWatched() );
				 dbConsumer.updateRecord(position,data);
				 parent.initAdapter();
				 }
			 }
		 }

	/**
	 * Listener для удаления фильма или сериала из главного списка и базы
	 */
	 private class MainDelListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 dbConsumer.deleteRecord(position);
			 parent.initAdapter();
			 }
		 }

	/**
	 * Listener для добавления сериала online
	 */
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

	/**
	 * Listener для добавления сериала offline
	 */
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

	/**
	 * Пустой конструктор, требуется, кажется, {@code FragmentManager}-ом. Короче, так требуют
	 */
	 public ActionDialog()
		{
		 super();
		 }

	/**
	 * Получение Listener-а для кнопки по его id
	 * @param listener id Listener-а
	 * @return запрашиваемый {@link DialogInterface.OnClickListener} для кнопки
	 * @see O.dialog
	 */
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

	/**
	 * Замена конструктору в котором инициализируются callback {@link AdapterInterface}, Listener и текст единственной кнопки
	 * @param _parent callback {@link AdapterInterface}
	 * @param _position позиция в базе
	 */
	 public void viceConstructor(AdapterInterface _parent,int _contentType,int _position,String _leftText,int _leftListener)
		{
		 buttonsNum=1;
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 leftListener=_leftListener;
		 leftText=_leftText;
		 }

	/**
	 * Замена конструктору в котором инициализируются callback {@link AdapterInterface}, Listener-ы и тексты кнопок
	 * @param _parent callback {@link AdapterInterface}
	 * @param _position позиция в базе
	 */
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

	/**
	 * Замена конструктору в котором инициализируются callback {@link AdapterInterface}, Listener-ы и тексты кнопок, сообщение
	 * @param _parent callback {@link AdapterInterface}
	 * @param _position позиция в базе
	 * @param _message заголовок диалога
	 */
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

	/**
	 * Замена конструктору. Эта инициализация нужна специально для {@link ConfirmDialog}. Listener-ы реализованы в самом
	 * {@code ConfirmDialog}, так что передаются они сами, а не их коды
	 */
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

	/**
	 * Создаю {@link #dbConsumer}, чтобы править базу. В зависимости от количества кнопок строится диалог
	 */
	 @NonNull
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState)
		{
		 dbConsumer= new DbConsumer(getActivity(), getActivity().getContentResolver(),contentType);
		 AlertDialog.Builder adb= new AlertDialog.Builder(getActivity() );
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
