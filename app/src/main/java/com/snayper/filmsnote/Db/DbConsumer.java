package com.snayper.filmsnote.Db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.util.Log;
import com.snayper.filmsnote.Utils.*;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by snayper on 18.04.2016.
 */
public class DbConsumer
	{
	 private ContentResolver resolver;
	 private Context context;
	 private Loader loader;
	 private int contentType;
	 private Uri uri;

	 public DbConsumer(Context _context,ContentResolver _resolver,int _contentType)
		{
		 context=_context;
		 resolver=_resolver;
		 contentType=_contentType;
		 uri= Uri.parse("content://"+ O.db.AUTHORITY +"/"+ O.db.PROVIDER_PATH[contentType] );
		 }
	 public DbConsumer(Context _context,ContentResolver _resolver,Loader _loader,int _contentType)
		{
		 this(_context,_resolver,_contentType);
		 loader=_loader;
		 }
	 public int getCount()
		{
		 int result;
		 Cursor cursor= getCursor();
		 result=cursor.getCount();
		 cursor.close();
		 return result;
		 }
	 private Cursor getCursor()
		{
		 Cursor result;
		 String fields[];
		 if(contentType==O.interaction.CONTENT_FILMS)
			 fields= O.db.TABLE_FIELDS[0];
		 else
			 fields= O.db.TABLE_FIELDS[1];
		 result= resolver.query(uri,fields,null,null,null);
		 return result;
		 }
	 public void putRecord(Record_Film rec)
		{
		 ContentValues newRecord= new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.getTitle() );
		 newRecord.put(O.db.FIELD_NAME_DATE, DateUtil.getCurrentDate().getTime() );
		 newRecord.put(O.db.FIELD_NAME_FILM_WATCHED,rec.isWatched());
		 resolver.insert(uri,newRecord);
		 if(loader!=null)
			 loader.forceLoad();
		 }
	 public void putRecord(Record_Serial rec,int tableNum)
		{
		 ContentValues newRecord= new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.getTitle() );
		 newRecord.put(O.db.FIELD_NAME_DATE, (rec.getDate()==null ? 0L : rec.getDate().getTime() ) );
		 newRecord.put(O.db.FIELD_NAME_ALL, rec.getAll() );
		 newRecord.put(O.db.FIELD_NAME_WATCHED, rec.getWatched() );
		 newRecord.put(O.db.FIELD_NAME_IMG, rec.getImgSrc() );
		 newRecord.put(O.db.FIELD_NAME_WEB, rec.getWebSrc() );
		 newRecord.put(O.db.FIELD_NAME_UPDATE_ORDER, rec.hasUpdateOrder() );
		 newRecord.put(O.db.FIELD_NAME_UPDATE_MARK, rec.isUpdated() );
		 newRecord.put(O.db.FIELD_NAME_CONFIDENT_DATE,rec.isConfidentDate() );
		 resolver.insert(uri,newRecord);
		 if(loader!=null)
			 loader.forceLoad();
		 }
	 public Record_Film extractRecord_Film(int position)
		{
		 Record_Film result;
		 Cursor cursor= getCursor();
		 cursor.moveToPosition(position);
		 result= new Record_Film();
		 result.setTitle(cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) ) );
		 Date date= new Date(cursor.getLong(cursor.getColumnIndex(O.db.FIELD_NAME_DATE) ) );
		 if(date.getTime()==0)
			 date=null;
		 result.setDate(date);
		 result.setWatched(cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_FILM_WATCHED)) == 1);
		 cursor.close();
		 return result;
		 }
	 public Record_Serial extractRecord_Serial(int position)
		{
		 Record_Serial result;
		 Cursor cursor= getCursor();
		 cursor.moveToPosition(position);
		 result= new Record_Serial();
		 result.setTitle(cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) ) );
		 Date date= new Date(cursor.getLong(cursor.getColumnIndex(O.db.FIELD_NAME_DATE) ) );
		 if(date.getTime()==0)
			 date=null;
		 result.setDate(date);
		 result.setImgSrc(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_IMG)));
		 result.setWebSrc(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_WEB)));
		 result.setAll(Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_ALL))));
		 result.setWatched(Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_WATCHED))));
		 result.setUpdateOrder((cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_UPDATE_ORDER)) == 1));
		 result.setUpdated((cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_UPDATE_MARK)) == 1));
		 result.setConfidentDate((cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_CONFIDENT_DATE)) == 1));
		 cursor.close();
		 return result;
		 }
	 public void updateRecord(int tableNum,int position,HashMap<String,Object> updatedData)
		{
		 Cursor cursor= getCursor();
		 cursor.moveToPosition(position);
		 ContentValues record= new ContentValues();
		 DatabaseUtils.cursorRowToContentValues(cursor,record);
		 for(HashMap.Entry<String,Object> x : updatedData.entrySet() )
			 switch(x.getKey() )
				{
				 case O.db.FIELD_NAME_TITLE:
					 record.put(x.getKey(), ""+ x.getValue() );
					 break;
				 case O.db.FIELD_NAME_DATE:
					 record.put(x.getKey(), (Long)x.getValue() );
					 break;
				 case O.db.FIELD_NAME_FILM_WATCHED:
					 record.put(x.getKey(), (Boolean)x.getValue() );
					 break;
				 case O.db.FIELD_NAME_UPDATE_ORDER:
					 record.put(x.getKey(), (Boolean)x.getValue() );
					 break;
				 case O.db.FIELD_NAME_UPDATE_MARK:
					 record.put(x.getKey(), (Boolean)x.getValue() );
					 break;
				 case O.db.FIELD_NAME_CONFIDENT_DATE:
					 record.put(x.getKey(), (Boolean)x.getValue() );
					 break;
				 case O.db.FIELD_NAME_ALL:
					 record.put(x.getKey(), (Integer)x.getValue() );
					 break;
				 case O.db.FIELD_NAME_WATCHED:
					 record.put(x.getKey(), (Integer)x.getValue() );
					 break;
				 case O.db.FIELD_NAME_WEB:
					 record.put(x.getKey(), ""+ x.getValue() );
					 break;
				 case O.db.FIELD_NAME_IMG:
					 record.put(x.getKey(), ""+ x.getValue() );
					 break;
				 }
		 String strID= cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_ID) );
		 resolver.update(uri,record, O.db.FIELD_NAME_ID +" = "+ strID, null);
		 cursor.close();
		 if(loader!=null)
			 loader.forceLoad();
		 }
	 public void deleteRecord(int position)
		{
		 Cursor cursor= getCursor();
		 if(contentType!=O.interaction.CONTENT_FILMS)
			{
			 Record_Serial record= extractRecord_Serial(position);
			 String pic= record.getImgSrc();
			 if(pic.length()!=0)
				 FileManager.deleteFile(context,pic);
			 }
		cursor.moveToPosition(position);
		 String strID= cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_ID));
		resolver.delete(uri,O.db.FIELD_NAME_ID +" = "+ strID, null);
		 cursor.close();
		 if(loader!=null)
			 loader.forceLoad();
		 }
	 public void clear()
		{
		 Cursor cursor= getCursor();
		 if(contentType!=O.interaction.CONTENT_FILMS)
			{
			 cursor.moveToFirst();
			 do
				{
				 String pic= cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_IMG) );
				 if(pic.length()!=0)
					 FileManager.deleteFile(context,pic);
				 }
				 while(cursor.moveToNext() );
			 }
		 resolver.delete(uri,null,null);
		 cursor.close();
		 if(loader!=null)
			 loader.forceLoad();
		 }
	 }
