package fnote.snayper.com.filmsnote.p1;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import fnote.snayper.com.filmsnote.R;

import java.util.ArrayList;

/**
 * Created by snayper on 19.02.2016.
 */
public class CustomCursorAdapter extends SimpleCursorAdapter
	{
	 ArrayList<Integer> idList;

	@SuppressWarnings("deprecation")
	 public CustomCursorAdapter(Context context,int layout,Cursor c,String[] from,int[] to, ArrayList<Integer> _idList)
		{
		 super(context,layout,c,from,to);
		 idList=_idList;
		 idList.clear();
		 }

	 @Override
	 public void setViewImage(ImageView v,String value)
		{
		 super.setViewImage(v,value);
		 if(v.getId()==R.id.watchedStatusImage)
			{
			 if(value.equals("f") )
				 v.setImageResource(R.mipmap.not_watched);
			 else
				 v.setImageResource(R.mipmap.watched);
			 }
		 }

	@Override
	public View getView(int position,View convertView,ViewGroup parent)
		{
//		 Cursor item= (Cursor)getItem(position);
		 Log.d("c123",""+ getItemId(position) );
//		 int p= item.getPosition();
		 return super.getView(position,convertView,parent);
		 }
	}
