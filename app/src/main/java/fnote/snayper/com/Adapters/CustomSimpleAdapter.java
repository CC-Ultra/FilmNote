package fnote.snayper.com.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import fnote.snayper.com.filmsnote.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomSimpleAdapter extends SimpleAdapter
	{
	 Context ctx;
	 public CustomSimpleAdapter(Context context,List<? extends Map<String,?>> data,int resource,String[] from,int[] to)
		{
		 super(context,data,resource,from,to);
		 ctx=context;
		 }

	 @Override
	 public View getView(int position,View convertView,ViewGroup parent)
		{
		 if(convertView==null)
			{
			 LayoutInflater inflater;
			 inflater= (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView= inflater.inflate(R.layout.edit_list_element, parent, false);
			 }
		 HashMap<String,Object> item= (HashMap<String,Object>)getItem(position);
		 TextView txt= (TextView)convertView.findViewById(R.id.episode);
		 ImageView img= (ImageView)convertView.findViewById(R.id.img);
		 txt.setText(item.get("Episode").toString());
		 if( (boolean)item.get("Pic") )
			 img.setImageResource(R.drawable.list_watched);
//			 img.setImageResource(R.mipmap.watched);
		 else
			 img.setImageResource(R.drawable.list_not_watched);
//			 img.setImageResource(R.mipmap.not_watched);
		 return convertView;
		 }
	}
