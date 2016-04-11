package com.snayper.filmsnote.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.snayper.filmsnote.Activities.GlobalMenuOptions;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomSimpleAdapter_Settings extends SimpleAdapter
	{
	 private Context context;
	 private View.OnClickListener checkboxListener;
	 private int drawableRes;

	 public CustomSimpleAdapter_Settings(Context _context,View.OnClickListener _checkboxListener,List<? extends Map<String,?> > data,int resource,String[] from,int[] to)
		{
		 super(_context,data,resource,from,to);
		 context=_context;
		 checkboxListener=_checkboxListener;
		 switch(GlobalMenuOptions.themeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 drawableRes= R.drawable.list_selector_mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 drawableRes= R.drawable.list_selector_ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 drawableRes= R.drawable.list_selector_cow_settings;
				 break;
			 default:
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
			 convertView= inflater.inflate(R.layout.settings_list_element, parent, false);
			 }
		 convertView.setBackgroundResource(drawableRes);
		 HashMap<String,Object> item= (HashMap<String,Object>)getItem(position);
		 TextView title= (TextView)convertView.findViewById(R.id.title);
		 TextView valueTxt= (TextView)convertView.findViewById(R.id.value);
		 CheckBox checkBox= (CheckBox)convertView.findViewById(R.id.checkbox);
		 title.setText(item.get("Title").toString());
		 Object value= item.get("Value");
		 if(value.getClass() == Boolean.class)
			{
			 valueTxt.setVisibility(View.GONE);
			 checkBox.setVisibility(View.VISIBLE);
			 checkBox.setFocusable(false);
			 checkBox.setFocusableInTouchMode(false);
			 checkBox.setChecked( (boolean)value);
			 checkBox.setOnClickListener(checkboxListener);
			 }
		 else
			{
			 valueTxt.setVisibility(View.VISIBLE);
			 checkBox.setVisibility(View.GONE);
			 valueTxt.setText(value.toString());
			 }
		 return convertView;
		 }
	 }
