package com.snayper.filmsnote.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by snayper on 09.02.2016.
 */
public class DbHelper extends SQLiteOpenHelper implements BaseColumns
	{
	 private static final String DB_CREATE_SCRIPT[]= new String[3];
	 private static SQLiteDatabase db;
	 public static Cursor cursors[]= new Cursor[3];

	 public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
		{
		 super(context, name, factory, version);
		 DB_CREATE_SCRIPT[0]= "create table "+ O.db.TABLE_NAME[0] +" ("+ BaseColumns._ID +" integer primary key autoincrement, "
				+ O.db.FIELD_NAME_TITLE +" edit_text_selector not null, "
				+ O.db.FIELD_NAME_DATE +" edit_text_selector not null, "
				+ O.db.FIELD_NAME_FLAG +" edit_text_selector not null);";
		 for(int i=1; i<3; i++)
			 DB_CREATE_SCRIPT[i]= "create table "+ O.db.TABLE_NAME[i] +" ("+ BaseColumns._ID +" integer primary key autoincrement, "
					+ O.db.FIELD_NAME_TITLE +" edit_text_selector not null, "
					+ O.db.FIELD_NAME_ALL +" edit_text_selector not null, "
					+ O.db.FIELD_NAME_WATCHED +" edit_text_selector not null, "
					+ O.db.FIELD_NAME_DATE +" edit_text_selector not null, "
					+ O.db.FIELD_NAME_WEB +" edit_text_selector not null, "
					+ O.db.FIELD_NAME_IMG +" edit_text_selector not null);";
		 }
	 public void initDb()
		{
		 db= getWritableDatabase();
		 }
	 public static void initCursors()
		{
		 String fields[][]=
				{
					 {DbHelper._ID, O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_DATE, O.db.FIELD_NAME_FLAG},
					 {DbHelper._ID, O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_ALL, O.db.FIELD_NAME_WATCHED, O.db.FIELD_NAME_DATE, O.db.FIELD_NAME_IMG, O.db.FIELD_NAME_WEB}
				 };
		 cursors[0] = db.query(O.db.TABLE_NAME[0], fields[0], null, null, null, null, DbHelper._ID +" DESC");
		 cursors[1] = db.query(O.db.TABLE_NAME[1], fields[1], null, null, null, null, DbHelper._ID +" DESC");
		 cursors[2] = db.query(O.db.TABLE_NAME[2], fields[1], null, null, null, null, DbHelper._ID +" DESC");
		 }
	 public static void putRecord_Serial(Record_Serial rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.getTitle() );
		 newRecord.put(O.db.FIELD_NAME_DATE, ""+ rec.getDate() );
		 newRecord.put(O.db.FIELD_NAME_ALL, ""+ rec.getAll() );
		 newRecord.put(O.db.FIELD_NAME_WATCHED, ""+ rec.getWatched() );
		 newRecord.put(O.db.FIELD_NAME_IMG, ""+ rec.getImgSrc() );
		 newRecord.put(O.db.FIELD_NAME_WEB, ""+ rec.getWebSrc() );
		 db.insert(O.db.TABLE_NAME[tableNum],null,newRecord);
		 DbHelper.initCursors();
		 }
	 public static void putRecord_Films(Record_Film rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.getTitle() );
		 newRecord.put(O.db.FIELD_NAME_DATE, Util.getCurentDate() );
		 newRecord.put(O.db.FIELD_NAME_FLAG, ""+ rec.getWatched() );
		 db.insert(O.db.TABLE_NAME[tableNum],null,newRecord);
		 DbHelper.initCursors();
		 }
	 public static Record_Film extractRecord_Film(int tableNum,int position)
		{
		 Record_Film result;
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 String title= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 result= new Record_Film();
		 result.setTitle(title);
		 String date= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_DATE) );
		 result.setDate(date);
		 result.setWatched(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_FLAG) ).charAt(0));
		 return result;
		 }
	 public static Record_Serial extractRecord_Serial(int tableNum,int position)
		{
		 Record_Serial result;
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 String title= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 result= new Record_Serial();
		 result.setTitle(title);
		 String date= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_DATE) );
		 result.setDate(date);
		 String imgSrc= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_IMG) );
		 result.setImgSrc(imgSrc);
		 String webSrc= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_WEB) );
		 result.setWebSrc(webSrc);
		 result.setAll(Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_ALL) ) ) );
		 result.setWatched(Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_WATCHED) ) ) );
		 return result;
		 }
	 public static void updateRecord(int tableNum,int position,HashMap<String,Object> updatedData)
		{
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 ContentValues record = new ContentValues();
		 DatabaseUtils.cursorRowToContentValues(cursor,record);
		 for(HashMap.Entry x : updatedData.entrySet() )
			 record.put(""+ x.getKey(), ""+ x.getValue() );
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
		 }
	 @Override
	 public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion)
		{
		 Log.w("SQLite","Обновляемся с версии "+oldVersion+" на версию "+newVersion);
		 db.execSQL("DROP TABLE IF IT EXISTS "+ O.db.TABLE_NAME); //тут будет ошибка, но х.з как правильно
		 onCreate(db);
		 }
	}

