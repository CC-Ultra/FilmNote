package com.snayper.filmsnote.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by snayper on 09.02.2016.
 */
public class DbHelper extends SQLiteOpenHelper implements BaseColumns
	{
	 private static final String DB_CREATE_SCRIPT[]= new String[3];
	 private static SQLiteDatabase db;
	 public static Cursor cursors[]= new Cursor[3];

	 public DbHelper(Context context)
		{
		 super(context, O.db.DB_FILENAME, null, O.db.DB_VERSION);

		 DB_CREATE_SCRIPT[0]= "create table "+ O.db.TABLE_NAME[0]
				 +" ("+ BaseColumns._ID +" integer primary key autoincrement"
				 +", "+ O.db.FIELD_NAME_TITLE +" edit_text_selector not null"
				 +", "+ O.db.FIELD_NAME_DATE +" INT"
				 +", "+ O.db.FIELD_NAME_FILM_WATCHED +" INT"
				 +");";
		 for(int i=1; i<3; i++)
			 DB_CREATE_SCRIPT[i]= "create table "+ O.db.TABLE_NAME[i]
					 +" ("+ BaseColumns._ID +" integer primary key autoincrement"
					 +", "+ O.db.FIELD_NAME_TITLE +" edit_text_selector not null"
					 +", "+ O.db.FIELD_NAME_ALL +" INT"
					 +", "+ O.db.FIELD_NAME_WATCHED +" INT"
					 +", "+ O.db.FIELD_NAME_DATE +" INT"
					 +", "+ O.db.FIELD_NAME_WEB +" edit_text_selector not null"
					 +", "+ O.db.FIELD_NAME_IMG +" edit_text_selector not null"
					 +", "+ O.db.FIELD_NAME_UPDATE_ORDER +" INT"
					 +", "+ O.db.FIELD_NAME_UPDATE_MARK+" INT"
					 +", "+ O.db.FIELD_NAME_CONFIDENT_DATE +" INT"
					 +");";
		 }
	 public void initDb()
		{
		 db= getWritableDatabase();
		 }
	 public static void initCursors()
		{
		 String fields[][]=
				{
					 {DbHelper._ID, O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_DATE, O.db.FIELD_NAME_FILM_WATCHED},
					 {DbHelper._ID, O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_ALL, O.db.FIELD_NAME_WATCHED,
							 O.db.FIELD_NAME_DATE, O.db.FIELD_NAME_IMG, O.db.FIELD_NAME_WEB,
							 O.db.FIELD_NAME_UPDATE_ORDER, O.db.FIELD_NAME_UPDATE_MARK, O.db.FIELD_NAME_CONFIDENT_DATE}
				 };
		 cursors[0] = db.query(O.db.TABLE_NAME[0], fields[0], null, null, null, null, DbHelper._ID +" DESC");
		 cursors[1] = db.query(O.db.TABLE_NAME[1], fields[1], null, null, null, null, DbHelper._ID +" DESC");
		 cursors[2] = db.query(O.db.TABLE_NAME[2], fields[1], null, null, null, null, DbHelper._ID +" DESC");
		 }
	 public static void putRecord_Serial(Record_Serial rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.getTitle() );
		 newRecord.put(O.db.FIELD_NAME_DATE, (rec.getDate()==null ? 0L : rec.getDate().getTime() ) );
		 newRecord.put(O.db.FIELD_NAME_ALL, rec.getAll() );
		 newRecord.put(O.db.FIELD_NAME_WATCHED, rec.getWatched() );
		 newRecord.put(O.db.FIELD_NAME_IMG, rec.getImgSrc() );
		 newRecord.put(O.db.FIELD_NAME_WEB, rec.getWebSrc() );
		 newRecord.put(O.db.FIELD_NAME_UPDATE_ORDER, rec.hasUpdateOrder() );
		 newRecord.put(O.db.FIELD_NAME_UPDATE_MARK, rec.isUpdated() );
		 newRecord.put(O.db.FIELD_NAME_CONFIDENT_DATE,rec.isConfidentDate());
		 db.insert(O.db.TABLE_NAME[tableNum],null,newRecord);
		 DbHelper.initCursors();
		 }
	 public static void putRecord_Films(Record_Film rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.getTitle() );
		 newRecord.put(O.db.FIELD_NAME_DATE, DateUtil.getCurrentDate().getTime() );
		 newRecord.put(O.db.FIELD_NAME_FILM_WATCHED, rec.isWatched() );
		 db.insert(O.db.TABLE_NAME[tableNum],null,newRecord);
		 DbHelper.initCursors();
		 }
	 public static Record_Film extractRecord_Film(int tableNum,int position)
		{
		 Record_Film result;
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 result= new Record_Film();
		 String title= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 result.setTitle(title);
		 Date date= new Date(cursor.getLong(cursor.getColumnIndex(O.db.FIELD_NAME_DATE) ) );
		 if(date.getTime()==0)
			 date=null;
		 result.setDate(date);
		 result.setWatched(cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_FILM_WATCHED) )==1);
		 return result;
		 }
	 public static Record_Serial extractRecord_Serial(int tableNum,int position)
		{
		 Record_Serial result;
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 result= new Record_Serial();
		 String title= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 result.setTitle(title);
		 Date date= new Date(cursor.getLong(cursor.getColumnIndex(O.db.FIELD_NAME_DATE) ) );
		 if(date.getTime()==0)
			 date=null;
		 result.setDate(date);
		 String imgSrc= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_IMG) );
		 result.setImgSrc(imgSrc);
		 String webSrc= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_WEB) );
		 result.setWebSrc(webSrc);
		 result.setAll(Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_ALL) ) ) );
		 result.setWatched(Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_WATCHED) ) ) );
		 result.setUpdateOrder( (cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_UPDATE_ORDER) )==1) );
		 result.setUpdated( (cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_UPDATE_MARK) )==1) );
		 result.setConfidentDate( (cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_CONFIDENT_DATE) ) == 1) );
		 return result;
		 }
	 public static void updateRecord(int tableNum,int position,HashMap<String,Object> updatedData)
		{
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 ContentValues record = new ContentValues();
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
		 String strID= cursor.getString(cursor.getColumnIndex(DbHelper._ID) );
		 int updCount = DbHelper.db.update(O.db.TABLE_NAME[tableNum],record,"_id = ?",new String[]{strID} );
		 DbHelper.initCursors();
		 }
	 public static void deleteRecord(Context context,int tableNum,int position)
		{
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 if(tableNum != O.interaction.CONTENT_FILMS)
			{
			 Record_Serial record= extractRecord_Serial(tableNum,position);
			 String pic= record.getImgSrc();
			 if(pic.length()!=0)
				 FileManager.deleteFile(context,pic);
			 }
		 String strID= cursor.getString(cursor.getColumnIndex(DbHelper._ID) );
		 int delCount = db.delete(O.db.TABLE_NAME[tableNum], "_id = "+ strID, null);
		 initCursors();
		 }

	 @Override
	 public void onCreate(SQLiteDatabase db)
		{
		 db.execSQL(DB_CREATE_SCRIPT[0] );
		 db.execSQL(DB_CREATE_SCRIPT[1] );
		 db.execSQL(DB_CREATE_SCRIPT[2] );
		 } // gg
	 @Override
	 public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
		{
		 Log.w("SQLite","Обновляемся с версии "+oldVersion+" на версию "+newVersion);
		 db.execSQL("DROP TABLE IF EXISTS "+O.db.TABLE_NAME[0]);
		 db.execSQL("DROP TABLE IF EXISTS "+O.db.TABLE_NAME[1]);
		 db.execSQL("DROP TABLE IF EXISTS "+ O.db.TABLE_NAME[2] );
		 onCreate(db);
		 }
	}

