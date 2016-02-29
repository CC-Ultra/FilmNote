package fnote.snayper.com.filmsnote.p1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Serial extends MainListFragment implements AdapterInterface
	{
	 class SerialListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Intent jumper= new Intent(getActivity(), EditActivity.class);
			 jumper.putExtra("Content type",contentType);
			 jumper.putExtra("Db position",position);
			 startActivity(jumper);
			 }
		 }
	 class SerialListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			{
			 String txtLeft="Delete";
			 String txtRight="Update";
			 int listenerLeft= O.dialog.LISTENER_MAIN_LIST_DEL;
			 int listenerRight= O.dialog.LISTENER_MAIN_LIST_UPDATE;
			 ActionDialog dialog= new ActionDialog();
			 Bundle paramsBundle= new Bundle();
			 ActionDialogParams params= new ActionDialogParams(Fragment_Serial.this,contentType,position,txtLeft,txtRight,listenerLeft,listenerRight);
			 paramsBundle.putParcelable("Params",params);
			 dialog.setArguments(paramsBundle);
			 dialog.show(getActivity().getSupportFragmentManager(), "");
			 return true;
			 }
		 }

	 public Fragment_Serial()
		{
		 super();
		 }
	 @Override
	 void setListener_listOnClick()
		{
		 list.setOnItemClickListener(new SerialListItemClickListener() );
		 }
	 @Override
	 void setListener_listOnLongClick()
		{
		 list.setOnItemLongClickListener(new SerialListItemLongClickListener() );
		 }
	 @Override
	 @SuppressWarnings("deprecation")
	 public void initAdapter()
		{
		 adapter= new SimpleCursorAdapter(getActivity(), listElementLayout, DbHelper.cursors[contentType], dbListFrom, dbListTo);
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
