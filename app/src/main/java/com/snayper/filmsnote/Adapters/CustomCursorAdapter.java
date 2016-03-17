package com.snayper.filmsnote.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import com.snayper.filmsnote.R;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomCursorAdapter extends SimpleCursorAdapter
	{
	 @SuppressWarnings("deprecation")
	 public CustomCursorAdapter(Context context,int layout,Cursor c,String[] from,int[] to)
		{
		 super(context,layout,c,from,to);
		 }

	 @Override
	 public void setViewImage(ImageView v,String value)
		{
		 super.setViewImage(v,value);
		 if(v.getId()==R.id.watchedStatusImage)
			{
			 if(value.equals("f") )
				 v.setImageResource(R.drawable.list_not_watched);
//				 v.setImageResource(R.mipmap.not_watched);
			 else
				 v.setImageResource(R.drawable.list_watched);
//				 v.setImageResource(R.mipmap.watched);
			 }
		 }

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
		{
		 return super.getView(position,convertView,parent);
		 }
	}
