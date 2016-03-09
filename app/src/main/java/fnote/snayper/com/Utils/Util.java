package fnote.snayper.com.Utils;

import java.util.Calendar;

/**
 * Created by snayper on 26.02.2016.
 */
public class Util
	{
	 public static String getCurentDate()
		{
		 String result;
		 Calendar date= Calendar.getInstance();
		 int day= date.get(Calendar.DAY_OF_MONTH);
		 String dayStr= (day<10 ? "0" : "") + day;
		 int month= date.get(Calendar.MONTH) +1;
		 String monthStr= (month<10 ? "0" : "") + month;
		 int year= date.get(Calendar.YEAR);
		 result= ""+ dayStr +"."+ monthStr +"."+ year;
		 return result;
		 }
	 }
