package com.snayper.filmsnote.Interfaces;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Fragments.MainListFragment;

/**
 * <p>Callback для активностей и фрагментов, запускающих {@link ActionDialog}</p>
 * {@link ActionDialog} хоть и является отдельной сущностью, которая не может пользоваться нестатичными методами активностей
 * все же не способен породить вызов {@link AppCompatActivity#onResume()}, через который производится обновление информации
 * на странице, потому что отрабатывает он прямо на ней же, никуда не переходя. Так что ему нужно передать callback, через
 * который он сможет вызывать эти методы
 * <p><sub>(26.02.2016)</sub></p>
 * @see ActionDialog
 * @see MainListFragment
 * @see EditActivity
 * @author CC-Ultra
 */
public interface AdapterInterface
	{
	 Context getContext();
	 void initAdapter();
	 }
