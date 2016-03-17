package com.snayper.filmsnote.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import com.snayper.filmsnote.Activities.AddActivity;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

/**
 * Created by snayper on 16.02.2016.
 */
public abstract class MainListFragment extends Fragment implements AdapterInterface
	{
	 private String title;
	 protected ListView list;
	 protected FloatingActionButton activeButton;
	 protected SimpleCursorAdapter adapter;
	 protected int contentType;
	 protected int listElementLayout;

	 protected String dbListFrom[]= {O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_ALL, O.db.FIELD_NAME_WATCHED, O.db.FIELD_NAME_DATE};
	 protected int dbListTo[]= {R.id.title, R.id.newEpisodes, R.id.watchedEpisodes, R.id.lastDate};

	 protected class ActiveButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 if(contentType == O.interaction.CONTENT_FILMS)
				{
				 Intent jumper= new Intent(getActivity(),AddActivity.class);
				 jumper.putExtra("Content type",contentType);
				 startActivity(jumper);
				 }
			 else
				{
				 String txtLeft="Online";
				 String txtRight="Offline";
				 String message="Выбери способ добавления";
				 int position=0;
				 int listenerLeft=O.dialog.LISTENER_ADD_ONLINE;
				 int listenerRight=O.dialog.LISTENER_ADD_OFFLINE;
				 ActionDialog dialog=new ActionDialog();
				 dialog.viceConstructor(MainListFragment.this,contentType,position,message,txtLeft,txtRight,listenerLeft,listenerRight);
				 dialog.show(getActivity().getSupportFragmentManager(),"");
				 }
			 }
		 }

	 protected abstract void setListener_listOnClick();
	 protected abstract void setListener_listOnLongClick();

	 public void initFragment(String _title,int _contentType,int _listElementLayout)
		{
		 title=_title;
		 contentType=_contentType;
		 listElementLayout=_listElementLayout;
		 }
	 public String getTitle()
		{
		 return title;
		 }

	 @Nullable
	 @Override
	 public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
		{
		 super.onCreateView(inflater,container,savedInstanceState);
		 View view= inflater.inflate(R.layout.main_list_fragment,container, false);

		 list= (ListView)view.findViewById(R.id.list);
		 initAdapter();
		 activeButton= (FloatingActionButton)view.findViewById(R.id.activeButton);
		 activeButton.setOnClickListener(new ActiveButtonListener() );
		 setListener_listOnClick();
		 setListener_listOnLongClick();
		 return view;
		 }
	 @Override
	 public void onResume()
		{
		 initAdapter();
		 super.onResume();
		 }
	 }
