package fnote.snayper.com.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import java.util.HashMap;
import android.support.v4.app.FragmentPagerAdapter;
import fnote.snayper.com.Fragments.Fragment_Films;
import fnote.snayper.com.Fragments.Fragment_Serial;
import fnote.snayper.com.Fragments.MainListFragment;
import fnote.snayper.com.Fragments.MainListFragmentParams;
import fnote.snayper.com.filmsnote.R;
import fnote.snayper.com.Utils.O;

/**
 * Created by snayper on 10.02.2016.
 */
public class TabsFragmentAdapter extends FragmentPagerAdapter
	{
	 private HashMap<Integer,MainListFragment> tabs;

	 public TabsFragmentAdapter(FragmentManager fm)
		{
		 super(fm);
		 initTabsMap();
		 }
	 @Override
	 public CharSequence getPageTitle(int position)
		{
		 return tabs.get(position).getTitle();
		 }
	 @Override
	 public Fragment getItem(int position)
		{
		 return tabs.get(position);
		 }
	 @Override
	 public int getCount()
		{
		 return tabs.size();
		 }
	 private void initTabsMap()
		{
		 tabs = new HashMap<>();
		 String titles[]= {"Films","Serial","Mult"};
		 MainListFragment createdFragments[]= new MainListFragment[3];
		 Bundle paramsBundle[]= {new Bundle(), new Bundle(), new Bundle() };
		 createdFragments[0]= new Fragment_Films();
		 createdFragments[1]= new Fragment_Serial();
		 createdFragments[2]= new Fragment_Serial();
		 paramsBundle[0].putParcelable("Params", new MainListFragmentParams(R.layout.main_list_element_film,O.interaction.CONTENT_FILMS) );
		 paramsBundle[1].putParcelable("Params", new MainListFragmentParams(R.layout.main_list_element_serial,O.interaction.CONTENT_SERIAL) );
		 paramsBundle[2].putParcelable("Params", new MainListFragmentParams(R.layout.main_list_element_serial,O.interaction.CONTENT_MULT) );
		 for(int i=0; i<3; i++)
			{
			 createdFragments[i].setArguments(paramsBundle[i] );
			 createdFragments[i].setTitle(titles[i] );
			 tabs.put(i, createdFragments[i] );
			 }
		 }
	 }
