package com.snayper.filmsnote.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.snayper.filmsnote.Activities.GlobalMenuOptions;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.DateUtil;
import com.snayper.filmsnote.Utils.O;

import java.util.Date;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomCursorAdapter_Serial extends SimpleCursorAdapter
	{
	 private Context context;
	 private int contentType;
	 private int drawableRes;
	 private int updatedColor,textColor;

	 @SuppressWarnings("deprecation")
	 public CustomCursorAdapter_Serial(Context _context,int _contentType,int layout,Cursor c,String[] from,int[] to)
		{
		 super(_context,layout,c,from,to,0);
		 context=_context;
		 contentType=_contentType;
		 Resources resources= context.getResources();
		 switch(GlobalMenuOptions.themeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 textColor= resources.getColor(R.color.list_text_mentor);
				 updatedColor= R.color.list_background_updated_mentor;
				 drawableRes= R.drawable.list_selector_mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 textColor= resources.getColor(R.color.list_text_ultra);
				 updatedColor= R.color.list_background_updated_ultra;
				 drawableRes= R.drawable.list_selector_ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 textColor= resources.getColor(R.color.list_text_cow);
				 updatedColor= R.color.list_background_updated_cow;
				 drawableRes= R.drawable.list_selector_cow;
				 break;
			 default:
				 textColor= resources.getColor(R.color.list_text_mentor);
				 updatedColor= R.color.list_background_updated_mentor;
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
			 convertView= inflater.inflate(R.layout.main_list_element_serial, parent, false);
			 }
		 convertView.setBackgroundResource(drawableRes);
		 TextView titleTxt= (TextView)convertView.findViewById(R.id.title);
		 TextView allTxt= (TextView)convertView.findViewById(R.id.newEpisodes);
		 TextView watchedTxt= (TextView)convertView.findViewById(R.id.watchedEpisodes);
		 TextView allTxtInfo= (TextView)convertView.findViewById(R.id.newInfo);
		 TextView watchedTxtInfo= (TextView)convertView.findViewById(R.id.watchedInfo);
		 TextView dateTxt= (TextView)convertView.findViewById(R.id.lastDate);

		 titleTxt.setTextColor(textColor);
		 allTxt.setTextColor(textColor);
		 watchedTxt.setTextColor(textColor);
		 allTxtInfo.setTextColor(textColor);
		 watchedTxtInfo.setTextColor(textColor);
		 dateTxt.setTextColor(textColor);

		 Cursor cursor= getCursor();
		 cursor.moveToPosition(position);
		 String title= cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 Date date= new Date(cursor.getLong(cursor.getColumnIndex(O.db.FIELD_NAME_DATE) ) );
		 if(date.getTime()==0)
			 date=null;
		 int all= cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_ALL) );
		 int watched= cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_WATCHED) );
		 boolean updated= cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_UPDATE_MARK) )==1;

		 dateTxt.setText(DateUtil.dateToString(date) );
		 titleTxt.setText(title);
		 allTxt.setText(""+ all);
		 watchedTxt.setText(""+ watched);
		 if(updated)
			 convertView.setBackgroundResource(updatedColor);
		 return convertView;
		 }
	}
