package com.snayper.filmsnote.Fragments;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Activities.MainActivity;
import com.snayper.filmsnote.Utils.O;

import java.util.HashMap;

/**
 * <p>Класс фрагмента для {@link ViewPager} в {@link MainActivity}</p>
 * Основная логика в {@link MainListFragment}, а здесь только описаны действия по обычному и длинному нажатию на элемент
 * списка
 * <p><sub>(16.02.2016)</sub></p>
 * @author CC-Ultra
 * @see MainListFragment
 */
public class Fragment_Serial extends MainListFragment
	{
	/**
	 * <p>Listener для обычного нажатия на элемент списка</p>
	 * Обычное нажатие приводит к переходу в {@link EditActivity}. В {@link Intent} упаковывается {@code contentType} и позиция
	 * в списке, которая соответствует позиции записи в базе. Если на серии была пометка об обновлении, переход ее снимает,
	 * подтверждая, что пользователь заметил ее
	 */
	 private class SerialListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Intent jumper= new Intent(getActivity(), EditActivity.class);
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_UPDATE_MARK,false);
			 dbConsumer.updateRecord(position,data);
			 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
			 jumper.putExtra(O.mapKeys.extra.POSITION, position);
			 startActivity(jumper);
			 }
		 }

	/**
	 * <p>Listener для длинного нажатия на элемент списка</p>
	 * Длинное нажатие порождает {@link ActionDialog}, передавая ему {@code contentType} и {@code position} (позиция в списке
	 * соответствует позиции в базе), а также указывая ему требования к диалогу:
	 * <p>Одна кнопка с текстом {@code "Удалить"}, на которую будет привязан {@link ActionDialog.MainDelListener}</p>
	 * @see ActionDialog
	 */
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
