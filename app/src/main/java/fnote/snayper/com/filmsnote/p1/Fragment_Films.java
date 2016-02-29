package fnote.snayper.com.filmsnote.p1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import fnote.snayper.com.filmsnote.R;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Films extends MainListFragment implements AdapterInterface
	{
	 class FilmsListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Record_Film record= DbHelper.extractRecord_Film(contentType,position);
			 String txtLeft= (record.watched=='t' ? "Cancel" : "Watch");
			 String txtRight="Update";
			 String txtCentral="Delete";
			 int listenerLeft= (record.watched=='t' ? O.dialog.LISTENER_FILM_CANCEL : O.dialog.LISTENER_FILM_WATCH);
			 int listenerRight= O.dialog.LISTENER_MAIN_LIST_UPDATE;
			 int listenerCentral= O.dialog.LISTENER_MAIN_LIST_DEL;
			 ActionDialog dialog= new ActionDialog();
			 Bundle paramsBundle= new Bundle();
			 ActionDialogParams params= new ActionDialogParams(Fragment_Films.this, contentType, position, txtLeft,txtRight,txtCentral,listenerLeft,listenerRight,listenerCentral);
			 paramsBundle.putParcelable("Params",params);
			 dialog.setArguments(paramsBundle);
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
	 void setListener_listOnClick() {}
	 @Override
	 void setListener_listOnLongClick()
		{
		 list.setOnItemLongClickListener(new FilmsListItemLongClickListener() );
		 }
	 @Override
	 @SuppressWarnings("deprecation")
	 public void initAdapter()
		{
		 adapter= new CustomCursorAdapter(getActivity(), listElementLayout, DbHelper.cursors[contentType], dbListFrom, dbListTo);
		 list.setAdapter(adapter);
		 }

	 @Nullable
	 @Override
	 public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
		{
		 View v= super.onCreateView(inflater,container,savedInstanceState);
		 return v;
		 }
	 }
