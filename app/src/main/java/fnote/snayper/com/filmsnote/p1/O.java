package fnote.snayper.com.filmsnote.p1;

/**
 * Created by snayper on 24.02.2016.
 */
public class O
	{
	 static class db
		{
		 public static final String DB_FILENAME= "filmNote.db";
		 public static final int DB_VERSION= 6;
		 public static final String TABLE_NAME[]= {"Films","Serial","Mult_Serial"};
		 public static final String FIELD_NAME_TITLE= "Title";
		 public static final String FIELD_NAME_DATE= "Date";
		 public static final String FIELD_NAME_WATCHED= "Watched";
		 public static final String FIELD_NAME_ALL= "All2";
		 public static final String FIELD_NAME_FLAG= "Flag";
		 }

	 static class content
		{
		 final static int CONTENT_FILMS=0;
		 final static int CONTENT_SERIAL=1;
		 final static int CONTENT_MULT=2;
		 }

	 static class dialog
		{
		 final static int LISTENER_FILM_CANCEL=0;
		 final static int LISTENER_FILM_WATCH=1;
		 final static int LISTENER_SERIAL_CANCEL=2;
		 final static int LISTENER_SERIAL_DEL=3;
		 final static int LISTENER_MAIN_LIST_DEL=4;
		 final static int LISTENER_MAIN_LIST_UPDATE=5;
		 }
	 }
