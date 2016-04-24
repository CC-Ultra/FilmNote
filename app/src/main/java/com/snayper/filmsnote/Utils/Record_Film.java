package com.snayper.filmsnote.Utils;

import java.util.Date;

/**
 * <p></p>
 * <p>Просто структура данных для хранения записи фильма</p>
 * <p>В наличии такие поля:</p>
 * <p>{@code title} - заголовок сериала</p>
 * <p>{@code date} - дата добавления или последнего обновления статуса</p>
 * <p>{@code watched} - статус просмотра</p>
 * <p><sub>(17.02.2016)</sub></p>
 * @author CC-Ultra
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
