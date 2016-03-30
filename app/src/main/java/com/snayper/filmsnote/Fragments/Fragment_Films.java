package com.snayper.filmsnote.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import com.snayper.filmsnote.Adapters.CustomCursorAdapter;
import com.snayper.filmsnote.Interfaces.AdapterInterface;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.Utils.Record_Film;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Films extends MainListFragment implements AdapterInterface
	{
	 private class FilmsListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Record_Film record= DbHelper.extractRecord_Film(contentType,position);
			 String txtLeft= (record.getWatched()=='t' ? "Не просмотрено" : "Просмотрено");
			 String txtRight="Удалить";
			 int listenerLeft= (record.getWatched()=='t' ? O.dialog.LISTENER_FILM_CANCEL : O.dialog.LISTENER_FILM_WATCH);
			 int listenerRight= O.dialog.LISTENER_MAIN_LIST_DEL;
			 ActionDialog dialog= new ActionDialog();
			 dialog.viceConstructor(Fragment_Films.this, contentType, position, txtLeft,txtRight,listenerLeft,listenerRight);
			 dialog.show(getActivity().getSupportFragmentManager(), "");
			 return true;
			 }
		 }
	 public Fragment_Films()
		{
		 super();
		 dbListFrom= new String[] {O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_DATE, O.db.FIELD_NAME_FLAG};
		 dbListTo= new int[] {R.id.title, R.id.lastDate, R.id.watchedStatusImage};
		 }
	 @Override
	 protected void setListener_listOnClick() {}
	 @Override
	 protected void setListener_listOnLongClick()
		{
		 list.setOnItemLongClickListener(new FilmsListItemLongClickListener() );
		 }
	 @Override
	 @SuppressWarnings("deprecation")
	 public void initAdapter()
		{
		 adapter= new CustomCursorAdapter(getActivity(), listElementLayout, DbHelper.cursors[contentType], dbListFrom, dbListTo);
		 list.setAdapter(adapter);
		 ColorDrawable divcolor = new ColorDrawable(Color.parseColor("#FF12212f"));
		 list.setDivider(divcolor);
		 list.setDividerHeight(2);
		 }

	 }
