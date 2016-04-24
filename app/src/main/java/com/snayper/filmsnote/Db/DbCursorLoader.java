package com.snayper.filmsnote.Db;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.CursorLoader;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.Utils.O;

/**
 * Класс на базе {@link AsyncTask}, который фоново делает запрос к базе на чтение и выдает в результате {@link Cursor} тем,
 * у кого прописан {@link LoaderManager.LoaderCallbacks}. Для обращения к базе нужны {@link ContentResolver} и
 * {@link Uri}, указывающий на конкретную таблицу. Это значит, что на каждую таблицу нужен свой Loader.
 * <p><sub>(18.04.2016)</sub></p>
 * @author CC-Ultra
 * @see MainListFragment
 */
public class DbCursorLoader extends CursorLoader
	{
	 private int contentType;
	 private ContentResolver resolver;
	 private Uri uri;

	/**
	 * @param context для конструктора суперкласса
	 * @param _resolver чтобы делать запросы
	 * @param _contentType чтобы инициализировать {@code uri}, нужный для этих запросов
	 */
	 public DbCursorLoader(Context context,ContentResolver _resolver,int _contentType)
		{
		 super(context);
		 resolver=_resolver;
		 contentType=_contentType;
		 uri= Uri.parse("content://"+ O.db.AUTHORITY +"/"+ O.db.PROVIDER_PATH[contentType] );
		 }

	/**
	 * Здесь основной экшн. Но очень простой. Для просто получения курсора по всем записям не требуется второй и третий
	 * аргументы в {@link ContentResolver#query}, а третий (сортировка) все равно игнорируется, и устанавливается в
	 * {@link DbProvider#query} самостоятельно
	 * @see DbProvider#query(Uri, String[], String, String[], String)
	 */
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
