package com.snayper.filmsnote.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.snayper.filmsnote.Utils.Record_Serial;
import com.snayper.filmsnote.Utils.Util;

import java.util.*;

/**
 * Created by snayper on 28.03.2016.
 */
public class Updater extends Service
	{
	 ArrayList<Record_Serial> records= new ArrayList();
//	 Date

	 private class DateComparator implements Comparator<Date>
		{
		 @Override
		 public int compare(Date date1,Date date2)
			{
			 if(date1.before(date2) )
				 return -1;
			 else
				 if(date1.after(date2) )
					 return 1;
				 else
					 return 0;
			 }
		 }

//		Util util=null;
//		 synchronized(util)
//			{
//			 util.equals(new Object() );
//			 }

	 private void init()
		{
		 Calendar x= Calendar.getInstance();
		 x.add(Calendar.HOUR,6);
		 Date a= x.getTime();
		 }

	 @Override
	 public int onStartCommand(Intent intent,int flags,int startId)
		{
//		 records= intent.getE
		 return super.onStartCommand(intent,flags,startId);
		 }
	 @Override
	 public void onCreate()
		{
		 super.onCreate();
		 }
	 @Nullable
	 @Override
	 public IBinder onBind(Intent intent)
		{
		 return null;
		 }
	 @Override
	 public void onDestroy()
		{
		 super.onDestroy();
		 }
	 }
