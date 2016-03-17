package com.snayper.filmsnote.Utils;

/**
 * Created by snayper on 17.02.2016.
 */
public class Record_Film
	{
	 private String title,date="";
	 private char watched='f';

	 public void setTitle(String _title)
		{
		 title=_title;
		 }
	 public void setDate(String _date)
		{
		 date=_date;
		 }
	 public void setWatched(char _watched)
		{
		 watched=_watched;
		 }
	 public char getWatched()
		{
		 return watched;
		 }
	 public String getTitle()
		{
		 return title;
		 }
	 public String getDate()
		{
		 return date;
		 }
	 }
