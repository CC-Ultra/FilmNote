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
 * <p>Класс-обертка над DB, чтобы можно было ее раздавать между несколькими процессами (и даже приложениями)</p>
 * На борту имеется {@link DbHelper}, но его обязанности сократились до базовых подключения к базе, создания, и действий над
 * устаревшей. Также тут есть свой {@link UriMatcher}, который призван определять к какой таблице обращаются с запросами.
 * Сам же функционал класса достаточно скудный: четыре основных метода (и пустой {@link #getType(Uri)}, которым я все равно
 * бы не пользовался), и пара служебных, вутренних. Все потому, что общение с классом идет через {@link ContentResolver}
 * через ограниченный интерфейс, и расширить его нельзя (вроде). Так что более высоким уровнем взаимодействия с базой (вроде
 * извлечения или обновления записей) занимается {@link DbConsumer}
 * <p><sub>(16.04.2016)</sub></p>
 * @author CC-Ultra
 * @see DbConsumer
 * @see #query(Uri, String[], String, String[], String)
 * @see #insert(Uri, ContentValues)
 * @see #update(Uri, ContentValues, String, String[])
 * @see #delete(Uri, String, String[])
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

	/**
	 * Кастомный {@link SQLiteDatabase}, отвечающий за подключение к базе, выполнение {@link #DB_CREATE_SCRIPT} при создании
	 * и сброс устаревшей базы
	 */
	 private class DbHelper extends SQLiteOpenHelper
		{
		 private final String DB_CREATE_SCRIPT[]= new String[3];

		/**
		 * Инициализация {@link #DB_CREATE_SCRIPT} и выполнение базового конструктора
		 * @param context единственное что требуется получить извне. Остальное просто константы
		 */
		 public DbHelper(Context context)
			{
			 super(context, O.db.DB_FILENAME, null, O.db.DB_VERSION);
			 DB_CREATE_SCRIPT[0]= "create table "+ O.db.TABLE_NAME[0]
					 +" ("+ O.db.FIELD_NAME_ID+" integer primary key autoincrement"
					 +", "+ O.db.FIELD_NAME_TITLE +" TEXT"
					 +", "+ O.db.FIELD_NAME_DATE +" INT"
					 +", "+ O.db.FIELD_NAME_FILM_WATCHED +" INT"
					 +");";
			 for(int i=1; i<3; i++)
				 DB_CREATE_SCRIPT[i]= "create table "+ O.db.TABLE_NAME[i]
						 +" ("+ O.db.FIELD_NAME_ID+" integer primary key autoincrement"
						 +", "+ O.db.FIELD_NAME_TITLE +" TEXT"
						 +", "+ O.db.FIELD_NAME_ALL +" INT"
						 +", "+ O.db.FIELD_NAME_WATCHED +" INT"
						 +", "+ O.db.FIELD_NAME_DATE +" INT"
						 +", "+ O.db.FIELD_NAME_WEB +" TEXT"
						 +", "+ O.db.FIELD_NAME_IMG +" TEXT"
						 +", "+ O.db.FIELD_NAME_UPDATE_ORDER +" INT"
						 +", "+ O.db.FIELD_NAME_UPDATE_MARK+" INT"
						 +", "+ O.db.FIELD_NAME_CONFIDENT_DATE +" INT"
						 +");";
			 }

		/**
		 * Пускаю в ход {@link #DB_CREATE_SCRIPT}-ы
		 */
		 @Override
		 public void onCreate(SQLiteDatabase db)
			{
			 db.execSQL(DB_CREATE_SCRIPT[0] );
			 db.execSQL(DB_CREATE_SCRIPT[1] );
			 db.execSQL(DB_CREATE_SCRIPT[2] );
			 }

		/**
		 * Просто сброс базы если устарела. Версия ставится в {@link O.db#DB_VERSION}. Делать что-то более сложное мне не
		 * позволяет мой уровень знания Sql.
		 */
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

	/**
	 * Каждый из четырех основных методов должен делать запрос к базе по своей таблице, но навигация сделана через {@link Uri}
	 * в которой эта информация имеется. Ее расшифровкой занимается {@link #uriMatcher}
	 * @param uri адрес базы и таблицы в ней, которую и надо расшифровать
	 * @throws IllegalArgumentException если битая {@code uri}
	 */
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

	/**
	 * Практически то же самое, что и {@link #getTableNum(Uri)}, только возвращает не индекс, а массив полей базы для запроса
	 * @see #getTableNum(Uri)
	 */
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

	/**
	 * Запрос к базе на получение курсора. Нужен для чтения информации из базы. По сути, делегирование через {@link #dbHelper}.
	 * Тонкостей работы с {@link ContentResolver} я не понимаю, а последние строчки просто скопировал из примера.
	 * @param uri указывает на таблицу
	 * @param projection поля в полученном курсоре
	 * @param selection строка условий выборки, фильтры
	 * @param selectionArgs аргументы для {@code selection}
	 * @param sortOrder сортировка. Устанавливается в обратном порядке, чтобы выводить в списках сначала последние добавленные
	 * @return новый {@link Cursor}. Почти что новая таблица, в которой будет столько полей и записей, сколько указано в
	 * вышеописанных аргументах
	 */
	 @Nullable
	 @Override
	 synchronized public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortOrder)
		{
		 sortOrder= O.db.FIELD_NAME_ID +" DESC";
		 int tableNum= getTableNum(uri);
		 String fields[]= getFields(uri);
		 SQLiteDatabase db= dbHelper.getWritableDatabase();
		 Cursor cursor= db.query(O.db.PROVIDER_PATH[tableNum],fields,selection,selectionArgs,null,null,sortOrder);
		 cursor.setNotificationUri(getContext().getContentResolver(), uri);
		 return cursor;
		 }

	/**
	 * Занесение новой записи. По сути, делегирование через {@link #dbHelper}. В конце что-то, наверно, очень важное с
	 * {@link ContentResolver}
	 * @param uri указывает на таблицу
	 * @param values содержит информацию для формирования записи
	 * @return {@link Uri} для доступа к свежесозданной записи. Мной всегда игнорируется
	 */
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

	/**
	 * Удаление указанных записей. По сути, делегирование через {@link #dbHelper}. В конце что-то, наверно, очень важное
	 * с {@link ContentResolver}
	 * @param uri указывает на таблицу
	 * @param selection условие выборки, какие записи удалять
	 * @param selectionArgs параметры для прошлого аргумента
	 * @return количество удаленных записей. Мной всегда игнорировалось
	 */
	 @Override
	 synchronized public int delete(Uri uri,String selection,String[] selectionArgs)
		{
		 int tableNum= getTableNum(uri);
		 SQLiteDatabase db= dbHelper.getWritableDatabase();
		 int count= db.delete(O.db.PROVIDER_PATH[tableNum],selection,selectionArgs);
		 getContext().getContentResolver().notifyChange(uri,null);
		 return count;
		 }

	/**
	 * Обновление указанных записей. По сути, делегирование через {@link #dbHelper}. В конце что-то, наверно, очень важное
	 * с {@link ContentResolver}
	 * @param uri указывает на таблицу
	 * @param values содержит информацию для формирования записи
	 * @param selection условие выборки, какие записи подменять
	 * @param selectionArgs параметры для прошлого аргумента
	 * @return количество обновленных записей. Мной всегда игнорировалось
	 */
	 @Override
	 synchronized public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs)
		{
		 int tableNum= getTableNum(uri);
		 SQLiteDatabase db= dbHelper.getWritableDatabase();
		 int count=db.update(O.db.PROVIDER_PATH[tableNum],values,selection,selectionArgs);
		 getContext().getContentResolver().notifyChange(uri, null);
		 return count;
		 }

	/**
	 * Просто подключение к базе
	 */
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
