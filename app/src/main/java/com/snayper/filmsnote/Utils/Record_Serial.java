package com.snayper.filmsnote.Utils;

import java.util.Date;

/**
 * <p>Просто структура данных для хранения записи сериала или мультсериала</p>
 * <p>В наличии такие поля:</p>
 * <p>{@code title} - заголовок сериала</p>
 * <p>{@code date} - дата последнего обновления</p>
 * <p>{@code all} - всего серий в наличии</p>
 * <p>{@code watched} - сколько из них просмотрено</p>
 * <p>{@code imgsrc} - имя файла картинки</p>
 * <p>{@code websrc} - ссылка на источник, откуда была извлечена информация</p>
 * <p>{@code updateOrder} - искать ли сервису обновления для этого сериала</p>
 * <p>{@code confidentDate} - известна ли дата по которой этот сериал выходит, чтобы можно было уверенно обновлять с каким-то
 * интервалом</p>
 * <p>{@code updated} - метка, что информация о сериале обновилась, но пользователь еще не снял ее, демонстрируя, что заметил это</p>
 * <p><sub>(17.02.2016)</sub></p>
 * @author CC-Ultra
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
