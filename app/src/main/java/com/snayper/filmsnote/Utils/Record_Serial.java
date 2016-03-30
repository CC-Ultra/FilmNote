package com.snayper.filmsnote.Utils;

import java.util.Date;

/**
 * Created by snayper on 17.02.2016.
 */
public class Record_Serial
	{
	 private String title="",date="",imgSrc="",webSrc="";
	 private int all=0;
	 private int watched=0;
	 private Date trueDate;

	 public void setTitle(String _title)
		{
		 title=_title;
		 }
	 public void setImgSrc(String _imgSrc)
		{
		 imgSrc=_imgSrc;
		 }
	 public void setWebSrc(String _webSrc)
		{
		 webSrc=_webSrc;
		 }
	 public void setDate(String _date)
		{
		 date=_date;
		 }
	 public void setAll(int _all)
		{
		 all=_all;
		 }
	 public void setWatched(int _watched)
		{
		 watched=_watched;
		 }
	 public void setTrueDate(Date _trueDate)
		{
		 trueDate=_trueDate;
		 }
	 public String getTitle()
		{
		 return title;
		 }
	 public String getImgSrc()
		{
		 return imgSrc;
		 }
	 public String getWebSrc()
		{
		 return webSrc;
		 }
	 public String getDate()
		{
		 return date;
		 }
	 public int getAll()
		{
		 return all;
		 }
	 public int getWatched()
		{
		 return watched;
		 }
	 public Date getTrueDate()
		{
		 return trueDate;
		 }
	 }
