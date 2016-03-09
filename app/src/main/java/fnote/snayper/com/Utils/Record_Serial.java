package fnote.snayper.com.Utils;

/**
 * Created by snayper on 17.02.2016.
 */
public class Record_Serial
	{
	 private String title="",date="",imgSrc="";
	 private int all=0;
	 private int watched=0;

	 public void setTitle(String _title)
		{
		 title=_title;
		 }
	 public void setImgSrc(String _imgSrc)
		{
		 imgSrc=_imgSrc;
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
	 public String getTitle()
		{
		 return title;
		 }
	 public String getImgSrc()
		{
		 return imgSrc;
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
	 }
