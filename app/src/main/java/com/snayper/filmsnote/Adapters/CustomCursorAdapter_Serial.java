package com.snayper.filmsnote.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.*;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomCursorAdapter_Serial extends SimpleCursorAdapter
	{
	 private Context context;
	 private int contentType;
	 @SuppressWarnings("deprecation")
	 public CustomCursorAdapter_Serial(Context _context,int _contentType,int layout,Cursor c,String[] from,int[] to)
		{
		 super(_context,layout,c,from,to);
		 context=_context;
		 contentType=_contentType;
		 }

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
		{
		 if(convertView==null)
			{
			 LayoutInflater inflater;
			 inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView= inflater.inflate(R.layout.main_list_element_serial, parent, false);
			 }
		 Record_Serial record= DbHelper.extractRecord_Serial(contentType, position);
		 TextView titleTxt= (TextView)convertView.findViewById(R.id.title);
		 TextView allTxt= (TextView)convertView.findViewById(R.id.newEpisodes);
		 TextView watchedTxt= (TextView)convertView.findViewById(R.id.watchedEpisodes);
		 TextView dateTxt= (TextView)convertView.findViewById(R.id.lastDate);
		 dateTxt.setText(DateUtil.dateToString(record.getDate() ) );
		 titleTxt.setText(record.getTitle() );
		 allTxt.setText(""+ record.getAll() );
		 watchedTxt.setText(""+ record.getWatched() );
		 if(record.isUpdated() )
			 convertView.setBackgroundResource(R.color.list_background_updated);
		 return convertView;
		 }
	}
