package com.snayper.filmsnote.Fragments;

import android.view.View;
import android.widget.AdapterView;
import com.snayper.filmsnote.Utils.Record_Film;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Films extends MainListFragment
	{
	 private class FilmsListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Record_Film record= dbConsumer.extractRecord_Film(position);
			 String txtLeft= (record.isWatched() ? "Не просмотрено" : "Просмотрено");
			 String txtRight="Удалить";
			 int listenerLeft= (record.isWatched() ? O.dialog.LISTENER_FILM_CANCEL : O.dialog.LISTENER_FILM_WATCH);
			 int listenerRight= O.dialog.LISTENER_MAIN_LIST_DEL;
			 ActionDialog dialog= new ActionDialog();
			 dialog.viceConstructor(Fragment_Films.this, contentType, position, txtLeft,txtRight,listenerLeft,listenerRight);
			 dialog.show(getActivity().getSupportFragmentManager(), "");
			 }
		 }
	 public Fragment_Films()
		{
		 super();
		 dbListFrom= new String[] {O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_DATE, O.db.FIELD_NAME_FILM_WATCHED};
		 dbListTo= new int[] {R.id.title, R.id.lastDate, R.id.watchedStatusImage};
		 }
	 @Override
	 protected void setListener_listOnClick()
		{
		 list.setOnItemClickListener(new FilmsListItemClickListener() );
		 }
	 @Override
	 protected void setListener_listOnLongClick()
		{
//		 list.setOnItemLongClickListener(new FilmsListItemLongClickListener() );
		 }
	 }
