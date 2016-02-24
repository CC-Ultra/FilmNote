package fnote.snayper.com.filmsnote.p1;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;
import android.support.v4.app.FragmentPagerAdapter;
import fnote.snayper.com.filmsnote.R;

/**
 * Created by snayper on 10.02.2016.
 */
public class TabsFragmentAdapter extends FragmentPagerAdapter
	{
	 private Map<Integer, MainListFragment> tabs;
	 private Context context;

	 public TabsFragmentAdapter(Context context, FragmentManager fm)
		{
		 super(fm);
		 this.context = context;
		 initTabsMap(context);
		 }
	 @Override
	 public CharSequence getPageTitle(int position)
		{
		 return tabs.get(position).getTitle();
		 }
	 @Override
	 public Fragment getItem(int position)
		{
//		 return new Fragment_Films(R.layout.main_list_element_film);
		 return tabs.get(position);
		 }
	 @Override
	 public int getCount()
		{
		 return tabs.size();
		 }
	 private void initTabsMap(Context context)
		{
		 tabs = new HashMap<>();
		 tabs.put(0, new Fragment_Films(context, R.layout.main_list_element_film, "Films", O.CONTENT_FILMS) );
		 tabs.put(1, new Fragment_Serial(context, R.layout.main_list_element_serial, "Serial", O.CONTENT_SERIAL) );
		 tabs.put(2, new Fragment_Serial(context, R.layout.main_list_element_serial, "Mult", O.CONTENT_MULT) );
		 }
	 }
