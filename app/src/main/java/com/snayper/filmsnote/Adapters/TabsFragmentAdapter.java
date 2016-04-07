package com.snayper.filmsnote.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import java.util.HashMap;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import com.snayper.filmsnote.Fragments.Fragment_Films;
import com.snayper.filmsnote.Fragments.Fragment_Serial;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

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
//		 Log.d(O.TAG,"getItem: "+ tabs.get(position).contentType);
		 return tabs.get(position);
		 }
	 @Override
	 public int getCount()
		{
		 return tabs.size();
		 }
	 private void initTabsMap()
		{
//		 Log.d(O.TAG,"initTabsMap: TabsFragmentAdapter");
		 tabs = new HashMap<>();
		 MainListFragment fragmentFilms= new Fragment_Films();
		 MainListFragment fragmentSerial= new Fragment_Serial();
		 MainListFragment fragmentMult= new Fragment_Serial();
		 fragmentFilms.initFragment("Фильмы",O.interaction.CONTENT_FILMS, R.layout.main_list_element_film);
		 fragmentSerial.initFragment("Сериалы",O.interaction.CONTENT_SERIAL, R.layout.main_list_element_serial);
		 fragmentMult.initFragment("Мульт-сериалы",O.interaction.CONTENT_MULT, R.layout.main_list_element_serial);
		 tabs.put(O.interaction.CONTENT_FILMS, fragmentFilms);
		 tabs.put(O.interaction.CONTENT_SERIAL, fragmentSerial);
		 tabs.put(O.interaction.CONTENT_MULT, fragmentMult);
		 }
	 }
