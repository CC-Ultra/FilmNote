package fnote.snayper.com.filmsnote.p1;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import fnote.snayper.com.filmsnote.R;

/**
 * Created by snayper on 16.02.2016.
 */
public abstract class MainListFragment extends Fragment
	{
	 private String title;
	 protected Context context;
	 protected View view;
	 ListView list;
	 FloatingActionButton activeButton;
	 SimpleCursorAdapter adapter;
	 int contentType;
	 int listElementLayout;

	 String dbListFrom[]= {O.FIELD_NAME_TITLE, O.FIELD_NAME_ALL, O.FIELD_NAME_WATCHED, O.FIELD_NAME_DATE};
	 int dbListTo[]= {R.id.title, R.id.newEpisodes, R.id.watchedEpisodes, R.id.lastDate};

//		{
//		Bundle args = new Bundle();
//		DbListFragment fragment= new DbListFragment();
//		fragment.setArguments(args);
//		fragment.setContext(context);
//		fragment.setTitle("DB file");
//		return fragment;
//		}
	 abstract void setListener_activeButton();
	 abstract void setListener_listOnClick();
	 abstract void initAdapter();
	 public String getTitle()
		{
		 return title;
		 }
	 public void setTitle(String _title)
		{
		 title= _title;
		 }
	 public void setContext(Context _context)
		{
		 context=_context;
		 }

	 @Nullable
	 @Override
	 public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
		{
		 super.onCreateView(inflater,container,savedInstanceState);
		 view = inflater.inflate(R.layout.main_list_fragment,container, false);

		 initAdapter();
		 list= (ListView)view.findViewById(R.id.list);
		 list.setAdapter(adapter);
		 activeButton= (FloatingActionButton)view.findViewById(R.id.activeButton);
		 setListener_activeButton();
		 setListener_listOnClick();
		 return view;
		 }
	 }
