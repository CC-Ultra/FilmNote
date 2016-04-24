package com.snayper.filmsnote.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Утильный класс для более комфортной работы с классом {@link Date}. Часто через {@link Calendar}.
 * <p><sub>(26.02.2016)</sub></p>
 * @author CC-Ultra
 * @see DateUtil#getCurrentDate()
 * @see DateUtil#dropTime(Date)
 * @see DateUtil#buildTime(int, int)
 * @see DateUtil#getHours(Date)
 * @see DateUtil#getMinutes(Date)
 * @see DateUtil#combineDateAndTime(Date, Date)
 * @see DateUtil#timeToString(Date, boolean)
 * @see DateUtil#dateToString(Date)
 */
public class DateUtil
	{
	/**
	 * По сути своей, просто делегирование {@link Date#Date()} или {@link Calendar#getInstance()}, но оставил этот метод,
	 * потому что показалось, что его название более говорящее
	 */
	 public static Date getCurrentDate()
		{
		 Calendar result= Calendar.getInstance();
		 return result.getTime();
		 }

	/**
	 * Обнуляет время в {@code date}, оставляя только дату
	 */
	 public static Date dropTime(Date date)
		{
		 Calendar calendar= Calendar.getInstance();
		 calendar.setTime(date);
		 calendar.set(Calendar.MINUTE,0);
		 calendar.set(Calendar.HOUR_OF_DAY,0);
		 calendar.set(Calendar.SECOND,0);
		 return calendar.getTime();
		 }

	/**
	 * @param hours 0..23
	 * @param minutes 0..59
	 * @return новый {@link Date} с обнуленной датой, но установленным временем
	 */
	 public static Date buildTime(int hours,int minutes)
		{
		 Calendar calendar= Calendar.getInstance();
		 calendar.clear(Calendar.YEAR);
		 calendar.clear(Calendar.MONTH);
		 calendar.clear(Calendar.DAY_OF_MONTH);
		 calendar.set(Calendar.HOUR_OF_DAY,hours);
		 calendar.set(Calendar.MINUTE,minutes);
		 return calendar.getTime();
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

	/**
	 * @param date из этого параметра возьмется только дата
	 * @param time в этом дата должна быть обнулена
	 */
	 public static Date combineDateAndTime(Date date,Date time)
		{
		 Date result;
		 date= dropTime(date);
		 Calendar calendar= Calendar.getInstance();
		 calendar.setTime(date);
		 calendar.set(Calendar.HOUR, getHours(time) );
		 calendar.set(Calendar.MINUTE, getMinutes(time) );
		 result= calendar.getTime();
		 return result;
		 }

	/**
	 * {@code 12:34} или {@code 12:34:56}
	 */
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

	/**
	 * {@code 07.12.2007}, например
	 */
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
