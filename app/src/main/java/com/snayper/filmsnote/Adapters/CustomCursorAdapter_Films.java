package com.snayper.filmsnote.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.snayper.filmsnote.Activities.GlobalMenuOptions;
import com.snayper.filmsnote.Activities.MainActivity;
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
	 private int drawableRes,textColor;
	 private Context context;

	 @SuppressWarnings("deprecation")
	 public CustomCursorAdapter_Films(Context _context,int layout,Cursor c,String[] from,int[] to)
		{
		 super(_context,layout,c,from,to);
		 context=_context;
		 Resources resources= context.getResources();
		 switch(GlobalMenuOptions.themeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 textColor= resources.getColor(R.color.list_text_mentor);
				 drawableRes= R.drawable.list_selector_mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 textColor= resources.getColor(R.color.list_text_ultra);
				 drawableRes= R.drawable.list_selector_ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 textColor= resources.getColor(R.color.list_text_cow);
				 drawableRes= R.drawable.list_selector_cow;
				 break;
			 default:
				 textColor= resources.getColor(R.color.list_text_mentor);
				 drawableRes= R.drawable.list_selector_mentor;
			 }
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
		 convertView.setBackgroundResource(drawableRes);
		 Record_Film record= DbHelper.extractRecord_Film(O.interaction.CONTENT_FILMS, position);
		 TextView titleTxt= (TextView)convertView.findViewById(R.id.title);
		 TextView dateTxt= (TextView)convertView.findViewById(R.id.lastDate);
		 ImageView img= (ImageView)convertView.findViewById(R.id.watchedStatusImage);

		 titleTxt.setTextColor(textColor);
		 dateTxt.setTextColor(textColor);
		 titleTxt.setText( record.getTitle() );
		 dateTxt.setText(DateUtil.dateToString(record.getDate() ) );
		 if(record.isWatched() )
			 img.setImageResource(R.drawable.list_watched);
		 else
			 img.setImageResource(R.drawable.list_not_watched);
		 return convertView;
		 }
	}
