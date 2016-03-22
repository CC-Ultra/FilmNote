package com.snayper.filmsnote.Utils;

/**
 * Created by snayper on 24.02.2016.
 */
public class O
	{
	 public static final String TAG="c123";

	 public static class db
		{
		 public static final String DB_FILENAME= "filmNote.db";
		 public static final int DB_VERSION= 6;
		 public static final String TABLE_NAME[]= {"Films","Serial","Mult_Serial"};
		 public static final String FIELD_NAME_TITLE= "Title";
		 public static final String FIELD_NAME_DATE= "Date";
		 public static final String FIELD_NAME_WATCHED= "Watched";
		 public static final String FIELD_NAME_ALL= "All2";
		 public static final String FIELD_NAME_FLAG= "Flag";
		 public static final String FIELD_NAME_IMG= "Image";
		 public static final String FIELD_NAME_WEB= "WebSRC";
		 }

	 public static class interaction
		{
		 public final static int CONTENT_FILMS=0;
		 public final static int CONTENT_SERIAL=1;
		 public final static int CONTENT_MULT=2;
		 public final static int WEB_ACTION_ADD=3;
		 public final static int WEB_ACTION_UPDATE=4;
		 }

	 public static class dialog
		{
		 public final static int LISTENER_FILM_CANCEL=0;
		 public final static int LISTENER_FILM_WATCH=1;
		 public final static int LISTENER_SERIAL_CANCEL=2;
		 public final static int LISTENER_SERIAL_DEL=3;
		 public final static int LISTENER_MAIN_LIST_DEL=4;
		 public final static int LISTENER_MAIN_LIST_UPDATE=5;
		 public final static int LISTENER_ADD_ONLINE=6;
		 public final static int LISTENER_ADD_OFFLINE=7;
		 }

	 public static class web
		{
		 public static class filmix
			{
			 public final static int ID=0;
			 public final static String HOST_FULL="http://filmix.net";
			 public final static String HOST="filmix.net";
			 }
		 public static class seasonvar
			{
			 public final static int ID=1;
			 public final static String HOST_FULL="http://seasonvar.ru";
			 public final static String HOST="seasonvar.ru";
			 }
		 public static class kinogo
			{
			 public final static int ID=2;
			 public final static String HOST_FULL="http://kinogo.co";
			 public final static String HOST="kinogo.co";
			 }
		 public static class onlineLife
			{
			 public final static int ID=3;
			 public final static String HOST_FULL="http://www.online-life.cc";
			 public final static String HOST="www.online-life.cc";
			 }
		 }
	 }
