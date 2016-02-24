package fnote.snayper.com.filmsnote.p1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by snayper on 16.02.2016.
 */
public class Fragment_Serial extends MainListFragment
	{
	 ArrayList<Integer> dbIds= new ArrayList<>();

	 class SerialActiveButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Intent jumper= new Intent(context, AddActivity.class);
			 jumper.putExtra("Content type",contentType);
			 startActivity(jumper);
			 }
		 }
	 class MultActiveButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Intent jumper= new Intent(context, AddActivity.class);
			 jumper.putExtra("Content type",contentType);
			 startActivity(jumper);
			 }
		 }
	 class SerialListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Toast.makeText(context,"\tSerial\nposition: "+position,Toast.LENGTH_SHORT).show();
			 }
		 }
	 class MultListItemClickListener implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 Toast.makeText(context,"\tMult\nposition: "+position,Toast.LENGTH_SHORT).show();
			 }
		 }

	 Fragment_Serial(Context context,int _listElementLayout,String title,int _contentType)
		{
		 setArguments(new Bundle());
		 setContext(context);
		 setTitle(title);
		 listElementLayout=_listElementLayout;
		 contentType=_contentType;
		 }

	 @Override
	 void setListener_activeButton()
		{
		 if(contentType==1)
			 activeButton.setOnClickListener(new SerialActiveButtonListener() );
		 if(contentType==2)
			 activeButton.setOnClickListener(new MultActiveButtonListener() );
		 }
	 @Override
	 void setListener_listOnClick()
		{
		 if(contentType==1)
			 list.setOnItemClickListener(new SerialListItemClickListener() );
		 if(contentType==2)
			 list.setOnItemClickListener(new MultListItemClickListener() );
		 }
	@Override
	 @SuppressWarnings("deprecation")
	 void initAdapter()
		{
		 adapter= new SimpleCursorAdapter(context, listElementLayout, MainActivity.cursor[contentType], dbListFrom, dbListTo);
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
