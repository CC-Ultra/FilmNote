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
import com.snayper.filmsnote.Utils.DateUtil;
import com.snayper.filmsnote.Utils.DbHelper;
import com.snayper.filmsnote.Utils.O;
import com.snayper.filmsnote.Utils.Record_Film;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomCursorAdapter_Films extends SimpleCursorAdapter
	{
	 Context context;
	 @SuppressWarnings("deprecation")
	 public CustomCursorAdapter_Films(Context _context,int layout,Cursor c,String[] from,int[] to)
		{
		 super(_context,layout,c,from,to);
		 context=_context;
		 }

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
		{
		 if(convertView==null)
			{
			 LayoutInflater inflater;
			 inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView= inflater.inflate(R.layout.main_list_element_film, parent, false);
			 }
		 Record_Film record= DbHelper.extractRecord_Film(O.interaction.CONTENT_FILMS, position);
		 TextView titleTxt= (TextView)convertView.findViewById(R.id.title);
		 TextView dateTxt= (TextView)convertView.findViewById(R.id.lastDate);
		 ImageView img= (ImageView)convertView.findViewById(R.id.watchedStatusImage);
		 titleTxt.setText( record.getTitle() );
		 dateTxt.setText(DateUtil.dateToString(record.getDate() ) );
		 if(record.isWatched() )
			 img.setImageResource(R.drawable.list_watched);
		 else
			 img.setImageResource(R.drawable.list_not_watched);
		 return convertView;
		 }
	}
