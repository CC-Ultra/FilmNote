package com.snayper.filmsnote;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by snayper on 16.04.2016.
 */
public class DbProvider extends ContentProvider
	{
	 @Nullable
	 @Override
	 public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder)
		{
		 return null;
		 }
	 @Nullable
	 @Override
	 public Uri insert(Uri uri,ContentValues values)
		{
		 return null;
		 }
	 @Override
	 public int delete(Uri uri,String selection,String[] selectionArgs)
		{
		 return 0;
		 }
	 @Override
	 public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs)
		{
		 return 0;
		 }
	 @Override
	 public boolean onCreate()
		{
		 return false;
		 }
	 @Nullable
	 @Override
	 public String getType(Uri uri)
		{
		 return null;
		 }
	 }
