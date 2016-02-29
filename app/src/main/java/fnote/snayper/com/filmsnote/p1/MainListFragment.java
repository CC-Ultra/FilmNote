package fnote.snayper.com.filmsnote.p1;

import android.content.Intent;
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
public abstract class MainListFragment extends Fragment implements AdapterInterface
	{
	 private String title;
	 ListView list;
	 FloatingActionButton activeButton;
	 SimpleCursorAdapter adapter;
	 int contentType;
	 int listElementLayout;

	 String dbListFrom[]= {O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_ALL, O.db.FIELD_NAME_WATCHED, O.db.FIELD_NAME_DATE};
	 int dbListTo[]= {R.id.title, R.id.newEpisodes, R.id.watchedEpisodes, R.id.lastDate};

	 class ActiveButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Intent jumper= new Intent(getActivity(), AddActivity.class);
			 jumper.putExtra("Content type",contentType);
			 startActivity(jumper);
			 }
		 }

	 abstract void setListener_listOnClick();
	 abstract void setListener_listOnLongClick();
	 public String getTitle()
		{
		 return title;
		 }
	 public void setTitle(String _title)
		{
		 title= _title;
		 }
	 @Override
	 public void onResume()
		{
		 initAdapter();
		 super.onResume();
		 }

	 @Nullable
	 @Override
	 public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
		{
		 super.onCreateView(inflater,container,savedInstanceState);
		 View view= inflater.inflate(R.layout.main_list_fragment,container, false);

		 Bundle bundleParams= getArguments();
		 MainListFragmentParams params= bundleParams.getParcelable("Params");
		 listElementLayout= params.listElementLayout;
		 contentType= params.contentType;

		 list= (ListView)view.findViewById(R.id.list);
		 initAdapter();
		 activeButton= (FloatingActionButton)view.findViewById(R.id.activeButton);
		 activeButton.setOnClickListener(new ActiveButtonListener() );
		 setListener_listOnClick();
		 setListener_listOnLongClick();
		 return view;
		 }
	 }
