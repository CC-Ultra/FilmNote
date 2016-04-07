package com.snayper.filmsnote.Utils;

import java.util.Date;

/**
 * Created by snayper on 17.02.2016.
 */
public class Record_Film
	{
	 private String title;
	 private Date date;
	 private boolean watched=false;

	 public void setTitle(String _title)
		{
		 title=_title;
		 }
	 public void setDate(Date _date)
		{
		 date=_date;
		 }
	 public void setWatched(boolean _watched)
		{
		 watched=_watched;
		 }
	 public boolean isWatched()
		{
		 return watched;
		 }
	 public String getTitle()
		{
		 return title;
		 }
	 public Date getDate()
		{
		 return date;
		 }
	 }
