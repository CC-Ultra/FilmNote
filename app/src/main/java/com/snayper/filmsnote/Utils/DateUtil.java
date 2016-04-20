package com.snayper.filmsnote.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by snayper on 26.02.2016.
 */
public class DateUtil
	{
	 public static Date getCurrentDate()
		{
		 return new Date();
		 }
	 public static Date dropTime(Date date)
		{
		 Calendar cleanDate= Calendar.getInstance();
		 Calendar oldDate= Calendar.getInstance();
		 cleanDate.setTime(new Date(0) );
		 oldDate.setTime(date);
		 cleanDate.set(Calendar.DAY_OF_MONTH, oldDate.get(Calendar.DAY_OF_MONTH) );
		 cleanDate.set(Calendar.MONTH, oldDate.get(Calendar.MONTH) );
		 cleanDate.set(Calendar.YEAR, oldDate.get(Calendar.YEAR) );
		 Date result= cleanDate.getTime();
		 return result;
		 }
	 public static Date buildTime(int hours,int minutes)
		{
		 long resTime= minutes*O.date.MINUTE_MILLIS + hours*O.date.HOUR_MILLIS;
		 Date date= new Date(resTime);
		 return date;
		 }
	 public static int getHours(Date date)
		{
		 Calendar x= Calendar.getInstance();
		 x.setTime(date);
		 return x.get(Calendar.HOUR_OF_DAY);
		 }
	 public static int getMinutes(Date date)
		{
		 Calendar x= Calendar.getInstance();
		 x.setTime(date);
		 return x.get(Calendar.MINUTE);
		 }
	 public static Date buildDate(int day,int month,int year)
		{
		 Calendar result= Calendar.getInstance();
		 result.setTime(new Date(0));
		 result.set(Calendar.YEAR,year);
		 result.set(Calendar.MONTH,month-1);
		 result.set(Calendar.DAY_OF_MONTH,day);
		 return dropTime(result.getTime());
		 }
	 public static Date combineDateAndTime(Date date,Date time)
		{
		 Date result= new Date();
		 date= dropTime(date);
		 result.setTime(date.getTime() + time.getTime() );
		 return result;
		 }
	 public static String timeToString(Date from, boolean showSeconds)
		{
		 if(from==null)
			 return "";
		 String result;
		 Calendar date= Calendar.getInstance();
		 date.setTime(from);
		 int hours= date.get(Calendar.HOUR_OF_DAY);
		 String hoursStr= (hours<10 ? "0" : "") + hours;
		 int minutes= date.get(Calendar.MINUTE);
		 String minutesStr= (minutes<10 ? "0" : "") + minutes;
		 int seconds= date.get(Calendar.SECOND);
		 String secondsStr= (seconds<10 ? "0" : "") + seconds;
		 if(showSeconds)
			 result= hoursStr +":"+ minutesStr +":"+ secondsStr;
		 else
			 result= hoursStr +":"+ minutesStr;
		 return result;
		 }
	 public static String dateToString(Date from)
		{
		 if(from==null)
			 return "";
		 String result;
		 Calendar date= Calendar.getInstance();
		 date.setTime(from);
		 int day= date.get(Calendar.DAY_OF_MONTH);
		 String dayStr= (day<10 ? "0" : "") + day;
		 int month= date.get(Calendar.MONTH) +1;
		 String monthStr= (month<10 ? "0" : "") + month;
		 int year= date.get(Calendar.YEAR);
		 result= ""+ dayStr +"."+ monthStr +"."+ year;
		 return result;
		 }
	 }
