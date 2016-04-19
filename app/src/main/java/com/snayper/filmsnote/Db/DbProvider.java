package com.snayper.filmsnote.Db;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import com.snayper.filmsnote.Utils.O;

/**
 * Created by snayper on 16.04.2016.
 */
public class DbProvider extends ContentProvider
	{
	 private DbHelper dbHelper;

	 private static final UriMatcher uriMatcher;
	 static
		{
		 uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		 uriMatcher.addURI(O.db.AUTHORITY, O.db.PROVIDER_PATH[0], O.db.URI_ID_FILMS);
		 uriMatcher.addURI(O.db.AUTHORITY, O.db.PROVIDER_PATH[1], O.db.URI_ID_SERIAL);
		 uriMatcher.addURI(O.db.AUTHORITY, O.db.PROVIDER_PATH[2], O.db.URI_ID_MULT);
		 }

	 private class DbHelper extends SQLiteOpenHelper
		{
		 private final String DB_CREATE_SCRIPT[]= new String[3];

		 public DbHelper(Context context)
			{
			 super(context, O.db.DB_FILENAME, null, O.db.DB_VERSION);
			 DB_CREATE_SCRIPT[0]= "create table "+ O.db.TABLE_NAME[0]
					 +" ("+ O.db.FIELD_NAME_ID+" integer primary key autoincrement"
					 +", "+ O.db.FIELD_NAME_TITLE +" edit_text_selector not null"
					 +", "+ O.db.FIELD_NAME_DATE +" INT"
					 +", "+ O.db.FIELD_NAME_FILM_WATCHED +" INT"
					 +");";
			 for(int i=1; i<3; i++)
				 DB_CREATE_SCRIPT[i]= "create table "+ O.db.TABLE_NAME[i]
						 +" ("+ O.db.FIELD_NAME_ID+" integer primary key autoincrement"
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
			 db.execSQL("DROP TABLE IF EXISTS "+O.db.TABLE_NAME[0]);
			 db.execSQL("DROP TABLE IF EXISTS "+O.db.TABLE_NAME[1]);
			 db.execSQL("DROP TABLE IF EXISTS "+ O.db.TABLE_NAME[2] );
			 onCreate(db);
			 }
		 }
	 private int getTableNum(Uri uri)
		{
		 switch(uriMatcher.match(uri) )
			{
			 case O.db.URI_ID_FILMS:
				 return O.interaction.CONTENT_FILMS;
			 case O.db.URI_ID_SERIAL:
				 return O.interaction.CONTENT_SERIAL;
			 case O.db.URI_ID_MULT:
				 return O.interaction.CONTENT_MULT;
			 default:
				 Log.d(O.TAG,"getTableNum: Плохой URI");
				 throw new IllegalArgumentException("Плохой URI");
			 }
		 }
	 private String[] getFields(Uri uri)
		{
		 switch(uriMatcher.match(uri) )
			{
			 case O.db.URI_ID_FILMS:
				 return O.db.TABLE_FIELDS[0];
			 case O.db.URI_ID_SERIAL:
				 return O.db.TABLE_FIELDS[1];
			 case O.db.URI_ID_MULT:
				 return O.db.TABLE_FIELDS[1];
			 default:
				 Log.d(O.TAG,"getFields: Плохой URI");
				 throw new IllegalArgumentException("Плохой URI");
			 }
		 }

	 @Nullable
	 @Override
	 synchronized public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder)
		{
		 sortOrder= O.db.FIELD_NAME_ID +" DESC";
		 int tableNum= getTableNum(uri);
		 String fields[]= getFields(uri);
		 SQLiteDatabase db= dbHelper.getWritableDatabase();
		 Cursor cursor= db.query(O.db.PROVIDER_PATH[tableNum], fields, selection, selectionArgs, null, null, sortOrder);
		 cursor.setNotificationUri(getContext().getContentResolver(), uri);
		 return cursor;
		 }
	 @Nullable
	 @Override
	 synchronized public Uri insert(Uri uri,ContentValues values)
		{
		 int tableNum= getTableNum(uri);
		 SQLiteDatabase db= dbHelper.getWritableDatabase();
		 long rowID= db.insert(O.db.PROVIDER_PATH[tableNum], null, values);
		 Uri resultUri= ContentUris.withAppendedId(uri,rowID);
		 getContext().getContentResolver().notifyChange(resultUri, null);
		 return resultUri;		 
		 }
	 @Override
	 synchronized public int delete(Uri uri,String selection,String[] selectionArgs)
		{
		 int tableNum= getTableNum(uri);
		 SQLiteDatabase db= dbHelper.getWritableDatabase();
		 int count= db.delete(O.db.PROVIDER_PATH[tableNum],selection,selectionArgs);
		 getContext().getContentResolver().notifyChange(uri,null);
		 return count;
		 }
	 @Override
	 synchronized public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs)
		{
		 int tableNum= getTableNum(uri);
		 SQLiteDatabase db= dbHelper.getWritableDatabase();
		 int count=db.update(O.db.PROVIDER_PATH[tableNum],values,selection,selectionArgs);
		 getContext().getContentResolver().notifyChange(uri, null);
		 return count;
		 }

	 @Override
	 public boolean onCreate()
		{
		 dbHelper= new DbHelper(getContext() );
		 return true;
		 }
	 @Nullable
	 @Override
	 public String getType(Uri uri)
		{
		 return null;
		 }
	 }
