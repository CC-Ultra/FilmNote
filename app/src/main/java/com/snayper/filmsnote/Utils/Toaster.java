package com.snayper.filmsnote.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * <p>Класс, способный слать {@code Toast} откуда угодно прямо на Home screen</p>
 * Для этого используется пока непостижимое для меня хитрое колдовство с классами {@link Looper} и {@link Handler}. Еще нужен
 * какой-то контекст... Метод в классе один единственный. Меня научил этому гугл.
 * <p><s>не описание а сплошное масло масляное</s></p>
 * <p><sub>(02.04.2016)</sub></p>
 * @author CC-Ultra
 * @see Handler
 * @see Looper
 */
public class Toaster
	{
	/**
	 * Собственно, действующее лицо. Он запускает {@link HomeToast} когда попросят
	 */
	 private static Handler toaster= new Handler(Looper.getMainLooper() );

	/**
	 * {@link Runnable}, который единственное что делает - это инициализируется и шлет {@code Toast}
	 */
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

	/**
	 * {@link #toaster} запускает {@link HomeToast}
	 * @param context откуда запускается {@code Toast}
	 */
	 public static void makeHomeToast(Context context,String message)
		{
		 toaster.post(new HomeToast(context,message) );
		 }
	 }
