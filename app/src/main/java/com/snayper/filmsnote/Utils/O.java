package com.snayper.filmsnote.Utils;

/**
 * Created by snayper on 24.02.2016.
 */
public class O
	{
	 public static final String TAG="c123";

	 public static class prefs
		{
		 public static final int UPDATE_INTERVAL_DEFAULT=7;
		 public static final int NOTIFICATION_TYPE_ID_DEFAULT=1;
		 public static final int NOTIFICATION_TYPE_ID_TOAST=2;
		 public static final int THEME_ID_MENTOR=0;
		 public static final int THEME_ID_ULTRA=1;
		 public static final int THEME_ID_COW=2;
		 public static final String THEME_STR_MENTOR="Стандартная";
		 public static final String THEME_STR_ULTRA="Ultra";
		 public static final String THEME_STR_COW="Секретная коровья";
		 public static final int SETTINGS_LIST_THEME=0;
		 public static final int SETTINGS_LIST_GSM_ORDER=1;
		 public static final int SETTINGS_LIST_UPDATE_TIME=2;
		 public static final int SETTINGS_LIST_UPDATE_INTERVAL=3;
		 public static final int SETTINGS_LIST_NOTIFICATION_TYPE=4;
		 public static final int SETTINGS_LIST_HELP=5;
		 public static final int SETTINGS_LIST_ABOUT=6;
		 }
	 public static class date
		{
		 public static final long MINUTE_MILLIS= 1000*60;
		 public static final long HOUR_MILLIS= MINUTE_MILLIS*60;
		 public static final long DAY_MILLIS= HOUR_MILLIS*24;
		 }

	 public static class db
		{
		 public static final String AUTHORITY="com.snayper.filmsnote.DB";
		 public static final String PROVIDER_PATH[]= {"Films","Serial","Mult_Serial"};
		 public static final int URI_ID_FILMS=0;
		 public static final int URI_ID_SERIAL=1;
		 public static final int URI_ID_MULT=2;

		 public static final String DB_FILENAME="filmNote.db";
		 public static final int DB_VERSION=9;
		 public static final String TABLE_NAME[]= {"Films","Serial","Mult_Serial"};
		 public static final String FIELD_NAME_ID= "_id";
		 public static final String FIELD_NAME_TITLE="Title";
		 public static final String FIELD_NAME_DATE="Date";
		 public static final String FIELD_NAME_WATCHED="Watched";
		 public static final String FIELD_NAME_ALL="All2";
		 public static final String FIELD_NAME_IMG="Image";
		 public static final String FIELD_NAME_WEB="WebSRC";
		 public static final String FIELD_NAME_FILM_WATCHED="Watched_flag";
		 public static final String FIELD_NAME_CONFIDENT_DATE="Confident_flag";
		 public static final String FIELD_NAME_UPDATE_ORDER="Update_order";
		 public static final String FIELD_NAME_UPDATE_MARK="Update_mark";
		 public static final String TABLE_FIELDS[][]=
				{
					{FIELD_NAME_ID, FIELD_NAME_TITLE, FIELD_NAME_DATE, FIELD_NAME_FILM_WATCHED},
					{FIELD_NAME_ID, FIELD_NAME_TITLE, FIELD_NAME_ALL, FIELD_NAME_WATCHED,
							FIELD_NAME_DATE, FIELD_NAME_IMG, FIELD_NAME_WEB,
							FIELD_NAME_UPDATE_ORDER, FIELD_NAME_UPDATE_MARK, FIELD_NAME_CONFIDENT_DATE}
				 };
		 }

	 public static class mapKeys
		{
		 public static class extra
			{
			 public static final String CONTENT_TYPE="Content type";
			 public static final String ACTION="Action";
			 public static final String POSITION="Position";
			 public static final String PENDING_INTENT_AS_EXTRA="Activity agent intent";
			 }
		 public static class prefs
			{
			 public static final String PREFS_FILENAME="Ultra Prefs";
			 public static final String THEME="Theme";
			 public static final String GSM_ORDER="GSM order";
			 public static final String UPDATE_TIME="Update time";
			 public static final String UPDATE_INTERVAL="Update interval";
			 public static final String NOTIFICATION_TYPE="Notification type";
			 public static final String YPS_MODE="Yps mode";
			 }
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
		 public final static int LISTENER_CONFIRM_YES=8;
		 public final static int LISTENER_CONFIRM_NO=9;
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
