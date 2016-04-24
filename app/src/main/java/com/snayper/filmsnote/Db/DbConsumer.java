package com.snayper.filmsnote.Db;

import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.content.Loader;
import com.snayper.filmsnote.Activities.AddActivity;
import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Activities.MainActivity;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.Services.Updater;
import com.snayper.filmsnote.Utils.*;

import java.util.Date;
import java.util.HashMap;

/**
 * <p>Класс для более прикладной работы с DB, как обертка над {@link ContentResolver}</p>
 * Для запросов нужен {@link #resolver} и {@link #uri}, которые привязаны к таблицам. Это значит, что на каждую таблицу
 * нужен свой {@code DbConsumer}. После внесения изменений в базу следует скомандовать {@link CursorLoader}-ам где только
 * возможно, чтоб нужно обновить информацию в местах их использования. Но Loader-ы вне активностей не живут, что осложняет
 * задачу. Но если действие происходит в рамках активности, можно передать текущий Loader в сам {@code DbConsumer} и инициировать
 * обновления отсюда.
 * <p>Ниже перечислены все пользователи класса</p>
 * <p><sub>(18.04.2016)</sub></p>
 * @author CC-Ultra
 * @see MainActivity
 * @see AddActivity
 * @see EditActivity
 * @see MainListFragment
 * @see ActionDialog
 * @see ParserResultConsumer
 * @see Updater
 */
public class DbConsumer
	{
	 private ContentResolver resolver;
	 private Context context;
	 private Loader loader;
	 private int contentType;
	 private Uri uri;

	/**
	 * Более бедный конструктор
	 * @param _context нужен для {@link #deleteRecord(int)}, который использует {@link FileManager#deleteFile(Context, String)}
	 * @param _resolver обращается к базе, делает к ней запросы
	 * @param _contentType определяет {@link #uri}, номер таблицы и прочее
	 */
	 public DbConsumer(Context _context,ContentResolver _resolver,int _contentType)
		{
		 context=_context;
		 resolver=_resolver;
		 contentType=_contentType;
		 uri= Uri.parse("content://"+ O.db.AUTHORITY +"/"+ O.db.PROVIDER_PATH[contentType] );
		 }

	/**
	 * Расширенный конструктор с получением {@link #loader}
	 * @param _loader позволяет обновлять курсоры, не уходя из активности
	 * @see #DbConsumer(Context, ContentResolver, int)
	 */
	 public DbConsumer(Context _context,ContentResolver _resolver,Loader _loader,int _contentType)
		{
		 this(_context,_resolver,_contentType);
		 loader=_loader;
		 }

	/**
	 * @return количество записей в соответствующей таблице
	 */
	 public int getCount()
		{
		 int result;
		 Cursor cursor= getCursor();
		 result=cursor.getCount();
		 cursor.close();
		 return result;
		 }

	/**
	 * Для просто получения курсора по всем записям не требуется второй и третий аргументы в {@link ContentResolver#query},
	 * а третий (сортировка) все равно игнорируется, и устанавливается в {@link DbProvider#query} самостоятельно
	 * @return новый {@link Cursor}, через который есть доступ к конкретной таблице
	 */
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

	/**
	 * то же что и в {@link #putRecord(Record_Serial)}, только даже проще
	 */
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

	/**
	 * В отлчие от {@link #putRecord(Record_Film)}, дата может быть пустой ({@code null} ), но в базе хранится long, так что
	 * в таком случае вносится {@code 0L}. Если есть {@code loader}, то ему дается сигнал обновить информацию
	 */
	 public void putRecord(Record_Serial rec)
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

	/**
	 * Получая курсор, двигаюсь на позицию и по одному полю извлекаю, все просто. Нюанс с {@code date}, еще, что она могла
	 * быть {@code null}. В конце закрываю курсор
	 * @param position Позиции в списках {@link MainListFragment} и в базе совпадают, поэтому можно использовать позицию
	 * в списке для доступа к записи в базе
	 */
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

	/**
	 * Получая курсор, двигаюсь на позицию и по одному полю извлекаю, все просто. Нюанс с {@code date} еще, что она могла
	 * быть {@code null}. В конце закрываю курсор
	 * @param position Позиции в списках {@link MainListFragment} и в базе совпадают, поэтому можно использовать позицию
	 * в списке для доступа к записи в базе
	 */
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

	/**
	 * Обновлене записи часто происходит частично, а не полным перекрытием сразу всех полей. Для того чтобы это сделать надо
	 * сначала прошлую запись считать. Так что, получаю курсор, двигаюсь к позиции, а потом метод {@link DatabaseUtils#cursorRowToContentValues}
	 * заполняет свежесозданный объект {@link ContentValues}, позволяя обойтись без посредников {@link Record_Serial} или
	 * {@link Record_Film}. Просто в этот {@code ContentValues record} дописываются нужные данные, а потом запись отправится
	 * в запросе. Есть еще проблема в том, что в {@code updatedData} пишутся любые данные, и узнать что там можно только
	 * через {@code x.getKey()}, так что тут используется {@code switch}. После того как все данные дополнили прошлое состояние
	 * записи, нужно получить индекс в базе, по которому запись будет опять записана. Индекс не равен позиции, но по позиции
	 * его можно извлечь. Это поле {@link O.db#FIELD_NAME_ID}. Теперь можно наконец сделать запрос и обновить базу. Закрываю
	 * курсор. Если есть {@code loader}, то ему дается сигнал обновить информацию
	 * @param position Позиции в списках {@link MainListFragment} и в базе совпадают, поэтому можно использовать позицию
	 * в списке для доступа к записи в базе
	 * @param updatedData {@code HashMap<имя_поля_в_базе,данные>}. Такая структура позволяет записать столько полей, сколько
	 * потребуется, и каких нужно типов. Это и хорошо и плохо
	 */
	 public void updateRecord(int position,HashMap<String,Object> updatedData)
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

	/**
	 * Если это не фильм, значит возможно, в памяти сохранена картинка, которую надо удалить вместе с записью. Признак наличия
	 * картинки - не пустое поле {@link Record_Serial#imgSrc}. Для проверки извлекается запись. Потом получаю курсор, двигаюсь
	 * к позиции, извлекаю по этой позиции поле {@link O.db#FIELD_NAME_ID}, эквивалентное индексу в базе, но не равное позиции.
	 * Потом по этому индексу запрос на удаление и закрываю курсор. Если есть {@code loader}, то ему дается сигнал обновить
	 * информацию
	 * @param position Позиции в списках {@link MainListFragment} и в базе совпадают, поэтому можно использовать позицию
	 * в списке для доступа к записи в базе
	 */
	 public void deleteRecord(int position)
		{
		 if(contentType!=O.interaction.CONTENT_FILMS)
			{
			 Record_Serial record= extractRecord_Serial(position);
			 String pic= record.getImgSrc();
			 if(pic.length()!=0)
				 FileManager.deleteFile(context,pic);
			 }
		 Cursor cursor= getCursor();
		 cursor.moveToPosition(position);
		 String strID= cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_ID));
		 resolver.delete(uri,O.db.FIELD_NAME_ID +" = "+ strID, null);
		 cursor.close();
		 if(loader!=null)
			 loader.forceLoad();
		 }

	/**
	 * Если это не фильм, значит возможно, в памяти сохранена картинка, которую надо удалить вместе с записью. Признак наличия
	 * картинки - не пустое поле {@link Record_Serial#imgSrc}. Получаю курсор, извлекаю поле с именем файла картинки. Не
	 * пустое - удаляю. И так по всем. Потом единственный запрос, чтобы удалить сразу таблицу. Для этого в {@link ContentResolver#delete}
	 * надо передать только таблицу без уточнений. Закрываю курсор. Если есть {@code loader}, то ему дается сигнал обновить
	 * информацию
	 */
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
