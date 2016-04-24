package com.snayper.filmsnote.Fragments;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import com.snayper.filmsnote.Activities.MainActivity;
import com.snayper.filmsnote.Utils.Record_Film;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

/**
 * <p>Класс фрагмента для {@link ViewPager} в {@link MainActivity}</p>
 * Основная логика в {@link MainListFragment}, а здесь только описаны действия по обычному нажатию на элемент списка и
 * переопределяются массивы {@link #dbListFrom} и {@link #dbListTo} для адаптера, ориентированного на другой {@link #listElementLayout}
 * <p><sub>(16.02.2016)</sub></p>
 * @author CC-Ultra
 * @see MainListFragment
 */
public class Fragment_Films extends MainListFragment
	{
	/**
	 * <p>Listener для обычного нажатия на элемент списка</p>
	 * Обычное нажатие порождает {@link ActionDialog}. Чтобы это сделать, нужно определиться с требованиями к нему, для чего
	 * нужно извлечь запись (с помощью {@link #dbConsumer}) и узнать статус фильма. От статуса зависит тект и Listener на
	 * кнопках диалога. Когда все понятно, диалогу передается {@code contentType} и {@code position} (позиция в списке
	 * соответствует позиции в базе) и указываются требования:
	 * <p>Две кнопки с текстом {@code "Просмотрено"/"Не просмотрено"} и {@code "Удалить"}, на которые будут привязаны
	 * {@link ActionDialog.FilmWatchListener}/{@link ActionDialog.FilmCancelListener} и {@link ActionDialog.MainDelListener}</p>
	 * @see ActionDialog
	 */
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

	/**
	 * Здесь переопределяются массивы {@link #dbListFrom} и {@link #dbListTo} для адаптера, ориентированного на другой
	 * {@link #listElementLayout}
	 * @see SimpleCursorAdapter
	 */
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
