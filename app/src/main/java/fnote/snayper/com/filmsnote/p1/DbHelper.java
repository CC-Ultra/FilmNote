package fnote.snayper.com.filmsnote.p1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by snayper on 09.02.2016.
 */
public class DbHelper extends SQLiteOpenHelper implements BaseColumns
	{
	 private static final String DB_CREATE_SCRIPT[]= new String[3];

	 public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
		{
		 super(context, O.DB_FILENAME, factory, O.DB_VERSION);
		 for(int i=0; i<3; i++)
			 DB_CREATE_SCRIPT[i]= "create table "+ O.TABLE_NAME[i] +" ("+ BaseColumns._ID +" integer primary key autoincrement, "
					+ O.FIELD_NAME_TITLE +" text not null, "
					+ O.FIELD_NAME_ALL +" text not null, "
					+ O.FIELD_NAME_WATCHED +" text not null, "
					+ O.FIELD_NAME_DATE +" text not null, "
					+ O.FIELD_NAME_FLAG +" text not null);";
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
// Запишем в журнал
		 Log.w("SQLite","Обновляемся с версии "+oldVersion+" на версию "+newVersion);
// Удаляем старую таблицу и создаём новую
		 db.execSQL("DROP TABLE IF IT EXISTS "+ O.TABLE_NAME);
// Создаём новую таблицу
		 onCreate(db);
		 }

//	 fnote.snayper.com.filmsnote.p1.DbHelper(Context context)
//		{
//		 super(context, DB_FILENAME, null, DB_VERSION);
//		 }
//	 public fnote.snayper.com.filmsnote.p1.DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler)
//		{
//		 super(context, name, factory, version, errorHandler);
//		 }
	}

