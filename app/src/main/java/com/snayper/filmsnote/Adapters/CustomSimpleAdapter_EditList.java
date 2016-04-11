package com.snayper.filmsnote.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.snayper.filmsnote.Activities.GlobalMenuOptions;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomSimpleAdapter_EditList extends SimpleAdapter
	{
	 private Context context;
	 private int drawableRes;

	 public CustomSimpleAdapter_EditList(Context _context,List<? extends Map<String,?> > data,int resource,String[] from,int[] to)
		{
		 super(_context,data,resource,from,to);
		 context=_context;
		 switch(GlobalMenuOptions.themeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 drawableRes= R.color.list_background_mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 drawableRes= R.color.list_background_ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 drawableRes= R.color.list_divider_cow;
				 break;
			 default:
				 drawableRes= R.color.list_background_mentor;
			 }
		 }

	 @Override
	 public View getView(int position,View convertView,ViewGroup parent)
		{
		 if(convertView==null)
			{
			 LayoutInflater inflater;
			 inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView= inflater.inflate(R.layout.edit_list_element, parent, false);
			 }
		 convertView.setBackgroundResource(drawableRes);
		 HashMap<String,Object> item= (HashMap<String,Object>)getItem(getCount()-1-position);
		 TextView txt= (TextView)convertView.findViewById(R.id.episode);
		 ImageView img= (ImageView)convertView.findViewById(R.id.img);
		 txt.setText(item.get("Episode").toString());
		 if( (boolean)item.get("Pic") )
			 img.setImageResource(R.drawable.list_watched);
		 else
			 img.setImageResource(R.drawable.list_not_watched);
		 return convertView;
		 }
	}
