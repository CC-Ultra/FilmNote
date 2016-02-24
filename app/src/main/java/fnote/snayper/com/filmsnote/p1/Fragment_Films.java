package fnote.snayper.com.filmsnote.p1;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import fnote.snayper.com.filmsnote.R;

import java.util.ArrayList;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Films extends MainListFragment
	{
	 ArrayList<Integer> dbIds= new ArrayList<>();

	 class FilmsActiveButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Intent jumper= new Intent(context, AddActivity.class);
			 jumper.putExtra("Content type",contentType);
			 startActivity(jumper);
			 }
		 }
	 class FilmsListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Toast.makeText(context, "\tFilms\nposition: "+ position,Toast.LENGTH_SHORT).show();
			 }
		 }

	 Fragment_Films(Context context,int _listElementLayout,String title,int _contentType)
		{
		 setArguments(new Bundle());
		 setContext(context);
		 setTitle(title);
		 listElementLayout=_listElementLayout;
		 contentType=_contentType;
		 dbListFrom= new String[] {O.FIELD_NAME_TITLE, O.FIELD_NAME_DATE, O.FIELD_NAME_FLAG};
		 dbListTo= new int[] {R.id.title, R.id.lastDate, R.id.watchedStatusImage};
		 }

	 @Override
	 void setListener_activeButton()
		{
		 activeButton.setOnClickListener(new FilmsActiveButtonListener() );
		 }
	 @Override
	 void setListener_listOnClick()
		{
		 list.setOnItemClickListener(new FilmsListItemClickListener() );
		 }
	 @Override
	 @SuppressWarnings("deprecation")
	 void initAdapter()
		{
		 adapter= new CustomCursorAdapter(context, listElementLayout, MainActivity.cursor[contentType], dbListFrom, dbListTo, dbIds);
		 }
	 @Override
	 public void onResume()
		{
		 MainActivity.initCursors();
		 initAdapter();
		 list.setAdapter(adapter);
		 super.onResume();
		 }

	 @Nullable
	 @Override
	 public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
		{
		 View v= super.onCreateView(inflater,container,savedInstanceState);
		 return v;
		 }
	 }
