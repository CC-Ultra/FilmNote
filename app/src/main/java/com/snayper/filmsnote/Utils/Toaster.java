package com.snayper.filmsnote.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by snayper on 02.04.2016.
 */
public class Toaster
	{
	 private static Handler toaster= new Handler(Looper.getMainLooper() );

	 private static class HomeToast implements Runnable
		{
		 String msg;
		 Context context;

		 HomeToast(Context _context,String _msg)
			{
			 context=_context;
			 msg=_msg;
			 }
		 @Override
		 public void run()
			{
			 Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
			 }
		 }

	 public static void makeHomeToast(Context context,String message)
		{
		 toaster.post(new HomeToast(context,message) );
		 }
	 }
