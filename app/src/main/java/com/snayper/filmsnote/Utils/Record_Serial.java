package com.snayper.filmsnote.Utils;

import java.util.Date;

/**
 * Created by snayper on 17.02.2016.
 */
public class Record_Serial
	{
	 private String title="",imgSrc="",webSrc="";
	 private int all=0;
	 private int watched=0;
	 private Date date;
	 private boolean updateOrder=false,confidentDate=false,updated=false;

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
	 public void setDate(Date _date)
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
	 public void setUpdateOrder(boolean _updateOrder)
		{
		 updateOrder=_updateOrder;
		 }
	 public void setConfidentDate(boolean _confidentDate)
		{
		 confidentDate=_confidentDate;
		 }
	 public void setUpdated(boolean _updated)
		{
		 updated=_updated;
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
	 public Date getDate()
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
	 public boolean hasUpdateOrder()
		{
		 return updateOrder;
		 }
	 public boolean isConfidentDate()
		{
		 return confidentDate;
		 }
	 public boolean isUpdated()
		{
		 return updated;
		 }
	 }
