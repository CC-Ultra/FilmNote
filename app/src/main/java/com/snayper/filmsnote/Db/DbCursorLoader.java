package com.snayper.filmsnote.Db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import com.snayper.filmsnote.Utils.O;

/**
 * Created by snayper on 18.04.2016.
 */
public class DbCursorLoader extends CursorLoader
	{
	 private int contentType;
	 private ContentResolver resolver;
	 private Uri uri;

	 public DbCursorLoader(Context context,ContentResolver _resolver,int _contentType)
		{
		 super(context);
		 resolver=_resolver;
		 contentType=_contentType;
		 uri= Uri.parse("content://"+ O.db.AUTHORITY +"/"+ O.db.PROVIDER_PATH[contentType] );
		 }
	 public int getContentType()
		{
		 return contentType;
		 }

	@Override
	 public Cursor loadInBackground()
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
	 }
