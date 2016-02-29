package fnote.snayper.com.filmsnote.p1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by snayper on 09.02.2016.
 */
public class DbHelper extends SQLiteOpenHelper implements BaseColumns
	{
	 private static final String DB_CREATE_SCRIPT[]= new String[3];
	 static SQLiteDatabase db;
	 static Cursor cursors[]= new Cursor[3];

	 public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
		{
		 super(context, name, factory, version);
		 for(int i=0; i<3; i++)
			 DB_CREATE_SCRIPT[i]= "create table "+ O.db.TABLE_NAME[i] +" ("+ BaseColumns._ID +" integer primary key autoincrement, "
					+ O.db.FIELD_NAME_TITLE +" text not null, "
					+ O.db.FIELD_NAME_ALL +" text not null, "
					+ O.db.FIELD_NAME_WATCHED +" text not null, "
					+ O.db.FIELD_NAME_DATE +" text not null, "
					+ O.db.FIELD_NAME_FLAG +" text not null);";
		 }
	 void initDb()
		{
		 db= getWritableDatabase();
		 }
	 static void initCursors()
		{
		 String fields[][]=
				{
					 {DbHelper._ID, O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_DATE, O.db.FIELD_NAME_FLAG},
					 {DbHelper._ID, O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_ALL, O.db.FIELD_NAME_WATCHED, O.db.FIELD_NAME_DATE}
				 };
		 cursors[0] = db.query(O.db.TABLE_NAME[0], fields[0], null, null, null, null, null);
		 cursors[1] = db.query(O.db.TABLE_NAME[1], fields[1], null, null, null, null, null);
		 cursors[2] = db.query(O.db.TABLE_NAME[2],fields[1],null,null,null,null,null);
		 }
	 static void putRecord_Serial(Record_Serial rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.title);
		 newRecord.put(O.db.FIELD_NAME_DATE, "");
		 newRecord.put(O.db.FIELD_NAME_ALL, ""+ rec.all);
		 newRecord.put(O.db.FIELD_NAME_WATCHED, ""+ rec.watched);
		 newRecord.put(O.db.FIELD_NAME_FLAG, "");
		 db.insert(O.db.TABLE_NAME[tableNum],null,newRecord);
		 DbHelper.initCursors();
		 }
	 static void putRecord_Films(Record_Film rec,int tableNum)
		{
		 ContentValues newRecord = new ContentValues();
		 newRecord.put(O.db.FIELD_NAME_TITLE, rec.title);
		 newRecord.put(O.db.FIELD_NAME_DATE, Util.getCurentDate() );
		 newRecord.put(O.db.FIELD_NAME_ALL, "");
		 newRecord.put(O.db.FIELD_NAME_WATCHED, "");
		 newRecord.put(O.db.FIELD_NAME_FLAG, ""+ rec.watched);
		 db.insert(O.db.TABLE_NAME[tableNum],null,newRecord);
		 DbHelper.initCursors();
		 }
	 static Record_Film extractRecord_Film(int tableNum,int position)
		{
		 Record_Film result;
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 String title= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 result= new Record_Film(title);
		 String date= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_DATE) );
		 result.date=date;
		 result.watched= cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_FLAG) ).charAt(0);
		 return result;
		 }
	 static Record_Serial extractRecord_Serial(int tableNum,int position)
		{
		 Record_Serial result;
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
		 String title= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 result= new Record_Serial(title);
		 String date= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_DATE) );
		 result.date=date;
		 result.all= Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_ALL) ) );
		 result.watched= Integer.parseInt(cursor.getString(cursor.getColumnIndex(O.db.FIELD_NAME_WATCHED) ) );
		 return result;
		 }
	 static void updateRecord(int tableNum,int position,HashMap<String,Object> updatedData)
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
	 static void deleteRecord(int tableNum,int position)
		{
		 Cursor cursor= DbHelper.cursors[tableNum];
		 cursor.moveToPosition(position);
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
		 db.execSQL("DROP TABLE IF IT EXISTS "+ O.db.TABLE_NAME);
		 onCreate(db);
		 }
	}

