package com.snayper.filmsnote.Services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.snayper.filmsnote.Activities.MainActivity;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by snayper on 28.03.2016.
 */
public class Updater extends Service
	{
	 private SharedPreferences prefs;
	 private final Handler handler = new Handler();
	 private Timer lateTimer= new Timer();
	 private Timer regularTimer= new Timer();
	 private HashMap<RecordInfo,Record_Serial> records;
	 private NotificationManager notificationManager;
	 private PendingIntent activityAgent;
	 private Date nextUpdate;
	 private boolean gsmOrder;
	 private int updateInerval;
	 private long updateTime;
	 private int notificationType;
	 private int notificationId=0;
	 private int id= android.os.Process.myPid();
	 private DummyListener dummyListener= new DummyListener();

	 private class DummyListener implements WebTaskComleteListener
		{
		 @Override
		 public void useParserResult(Record_Serial extractedData) {}
		 }
	 private class RecordInfo
		{
		 int contentType,dbPosition;
		 public RecordInfo(int _contentType,int _dbPosition)
			{
			 contentType=_contentType;
			 dbPosition=_dbPosition;
			 }
		 }
	 private class WrapperTask extends TimerTask
		{
		 @Override
		 public void run()
			{
			 handler.post(new UpdateTask() );
			 }
		 }
	 private class UpdateTask extends TimerTask
		{
		 @Override
		 public void run()
			{
//нормальное обновление
			 initPrefs();
			 DbHelper.initCursors();
			 Toaster.makeHomeToast(Updater.this,"Task.run() начало");
			 Log.d(O.TAG,"run: начало");
			 ArrayList<String> updatedTitles= new ArrayList<>();
			 records= filterListForActual(getListforUpdate() );
			 Toaster.makeHomeToast(Updater.this,"checkForWifi() "+ checkForWifi() );
			 Toaster.makeHomeToast(Updater.this,"gsmOrder "+ gsmOrder);
			 if(checkForWifi() || gsmOrder)
				{
				 AsyncParser parser;
				 for(HashMap.Entry<RecordInfo,Record_Serial> x : records.entrySet() )
					{
					 Record_Serial dbRecord= x.getValue();
					 int allBefore= dbRecord.getAll();
					 parser= getParser(dbRecord.getWebSrc() );
					 if(parser==null)
						{
						 Log.d(O.TAG,"run: парсер для "+ dbRecord.getTitle() +" не был проинициализирован и запущен");
						 continue;
						 }
					 parser.execute();
					 try
						{
						 Record_Serial webExtracted;
						 Log.d(O.TAG,"Ищу обновление для: "+ dbRecord.getTitle()+" confident="+ dbRecord.isConfidentDate());
						 Toaster.makeHomeToast(Updater.this,"Ищу обновление для: "+ dbRecord.getTitle());
						 webExtracted= parser.get();
						 if(allBefore < webExtracted.getAll() )
							{
							 updatedTitles.add(webExtracted.getTitle());
							 webExtracted.setUpdated(true);
							 Date currentDate= DateUtil.getCurrentDate();
							 if(!dbRecord.isConfidentDate() )
								 webExtracted.setDate(currentDate);
							 else
								{
								 long difftime= currentDate.getTime() - dbRecord.getDate().getTime();
								 long longInterval= O.date.MINUTE_MILLIS*2;
//								 long longInterval= O.date.DAY_MILLIS*updateInerval;
								 long addingDifftime= (difftime/longInterval)*longInterval;
								 webExtracted.setDate(new Date(dbRecord.getDate().getTime() + addingDifftime) );
 								 }
							 webExtracted.setConfidentDate(true);
							 RecordInfo info= x.getKey();
							 ParserResultConsumer.useParserResult(Updater.this,webExtracted,O.interaction.WEB_ACTION_UPDATE,info.contentType,info.dbPosition);
							 Log.d(O.TAG,"Запись обновлена: "+webExtracted.getTitle()+" confident="+webExtracted.isConfidentDate());
							 Toaster.makeHomeToast(Updater.this,"Запись обновлена: "+webExtracted.getTitle());
							 }
						 }
					 catch(InterruptedException e)
						{ Log.d(O.TAG,"run: Interrupted"); }
					 catch(ExecutionException e)
						{ Log.d(O.TAG,"run: ExecutionException"); }
					 }
				 }
			 if(updatedTitles.size()!=0)
				 sendNotification(updatedTitles);
			 Toaster.makeHomeToast(Updater.this,"Task.run() конец");
			 Log.d(O.TAG,"run: конец");
//
/*/ранний тест
			 int progress=0;
			 while(progress<100)
				 try
					{
					 TimeUnit.SECONDS.sleep(5);
					 progress+=20;
					 Toaster.makeHomeToast(Updater.this,progress +"%");
					 }
				 catch(InterruptedException e)
					{
					 Log.d(O.TAG,"run: Interrupted");
					 }
/*/
			 }
		 }

	 private AsyncParser getParser(String webSrc)
		{
		 AsyncParser result=null;
		 if(webSrc.contains(O.web.filmix.HOST) )
			 result= new Parser_Filmix(this, dummyListener, webSrc, false);
		 else if(webSrc.contains(O.web.seasonvar.HOST) )
			 result= new Parser_Seasonvar(this, dummyListener, webSrc, false);
		 else if(webSrc.contains(O.web.kinogo.HOST) )
			 result= new Parser_Kinogo(this, dummyListener, webSrc, false);
		 else if(webSrc.contains(O.web.onlineLife.HOST) )
			 result= new Parser_OnlineLife(this, dummyListener, webSrc, false);
		 return result;
		 }
	 private HashMap<RecordInfo,Record_Serial> filterListForActual(HashMap<RecordInfo,Record_Serial> from)
		{
		 HashMap<RecordInfo,Record_Serial> result= new HashMap<>();
		 for(HashMap.Entry<RecordInfo,Record_Serial> x : from.entrySet() )
			{
			 Record_Serial record= x.getValue();
			 if(!record.isConfidentDate() )
				{
				 result.put(x.getKey(),record);
				 continue;
				 }
//			 long difftime= DateUtil.dropTime(DateUtil.getCurrentDate() ).getTime() - DateUtil.dropTime(record.getDate() ).getTime();
//			 if( difftime/O.date.DAY_MILLIS >= updateInerval)
			 long difftime= DateUtil.getCurrentDate().getTime() - record.getDate().getTime();
			 if( difftime >= O.date.MINUTE_MILLIS*2)
				 result.put(x.getKey(),record);
			 }

		 return result;
		 }
	 private HashMap<RecordInfo,Record_Serial> getListforUpdate()
		{
		 HashMap<RecordInfo,Record_Serial> result= new HashMap<>();
		 for(int i=1; i<3; i++)
			{
			 Cursor cursor= DbHelper.cursors[i];
			 for(int j=0; j<cursor.getCount(); j++)
				{
				 Record_Serial record= DbHelper.extractRecord_Serial(i,j);
				 RecordInfo info= new RecordInfo(i,j);
				 if(record.hasUpdateOrder() )
					 result.put(info,record);
				 }
			 }
		 return result;
		 }
	 @SuppressWarnings("deprecation")
	 private boolean checkForWifi()
		{
		 ConnectivityManager connectivityManager= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		 return info.isConnected();
		 }
	 private void initPrefs()
		{
		 prefs= getSharedPreferences(O.mapKeys.prefs.PREFS_FILENAME, MODE_PRIVATE);
		 gsmOrder= prefs.getBoolean(O.mapKeys.prefs.GSM_ORDER,false);
		 updateInerval= prefs.getInt(O.mapKeys.prefs.UPDATE_INTERVAL,7);
		 notificationType= prefs.getInt(O.mapKeys.prefs.NOTIFICATION_TYPE, O.prefs.NOTIFICATION_TYPE_ID_DEFAULT);
		 updateTime= prefs.getLong(O.mapKeys.prefs.UPDATE_TIME, DateUtil.buildTime(20,0).getTime() );
		 }
	 private String buildNotificationMessage(ArrayList<String> titles)
		{
		 String result;
		 if(titles.size()<3)
			{
			 StringBuilder sb= new StringBuilder();
			 sb.append("Новые серии:\t");
			 for(String title : titles)
				 sb.append("["+ title +"]\t");
			 result= sb.toString();
			 }
		 else
			 result= "Вышли обновления для "+ titles.size() +" сериалов";
		 return result;
		 }
	 private void sendNotification(ArrayList<String> titles)
		{
		 String message= buildNotificationMessage(titles);
		 if(notificationType==O.prefs.NOTIFICATION_TYPE_ID_TOAST)
			 Toaster.makeHomeToast(this,message);
		 else if(notificationType==O.prefs.NOTIFICATION_TYPE_ID_DEFAULT)
			{
			 Intent intent= new Intent(this, MainActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			 PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, intent, 0);
			 NotificationCompat.Builder builder= new NotificationCompat.Builder(this);
			 builder.setSmallIcon(R.mipmap.app_icon);
			 builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.notification_new_episodes));
			 builder.setTicker("Новые серии!");
			 builder.setWhen(System.currentTimeMillis());
			 builder.setContentTitle("Новые серии");
			 builder.setContentText(message);
			 builder.setNumber(titles.size());
			 builder.setContentIntent(pendingIntent);
			 Notification notification= builder.build();
			 notification.ledARGB=0x0000F0F0;
			 notification.ledOffMS=5000;
			 notification.ledOnMS=300;
			 notification.flags|= Notification.FLAG_AUTO_CANCEL;
			 notification.flags|= Notification.FLAG_SHOW_LIGHTS;
			 notificationManager.notify(notificationId++, notification);
			 }
		 }

	 @Override
	 public int onStartCommand(Intent intent,int flags,int startId)
		{
		 activityAgent= intent.getParcelableExtra(O.mapKeys.extra.PENDING_INTENT_AS_EXTRA);
		 Toaster.makeHomeToast(this,"Сервис "+ id + " запущен");
		 Log.d(O.TAG,"onStartCommand: ");
/*/ежедненая работа сервиса
		 Date currentDate= DateUtil.getCurrentDate();
		 Date regularDate= DateUtil.combineDateAndTime(currentDate,new Date(updateTime));
		 if(currentDate.after(regularDate) )
			{
			 TimerTask lateTask= new UpdateTask();
			 lateTimer.schedule(lateTask,0);
			 regularDate.setTime(regularDate.getTime() + O.date.DAY_MILLIS);
			 }

		 TimerTask regularTask= new UpdateTask();
		 regularTimer.schedule(regularTask,regularDate, O.date.DAY_MILLIS);
/*/
//тестовые установки
		 Date currentDate= DateUtil.getCurrentDate();
		 Date lateDate= new Date(currentDate.getTime() - O.date.MINUTE_MILLIS);
		 Date regularDate= new Date(currentDate.getTime() + O.date.MINUTE_MILLIS/6);
		 if(currentDate.after(lateDate) )
			{
			 WrapperTask lateTask= new WrapperTask();
//			 TimerTask lateTask= new UpdateTask();
			 lateTimer.schedule(lateTask,0);
			 regularDate.setTime(regularDate.getTime() + O.date.MINUTE_MILLIS);
			 }

		 WrapperTask regularTask= new WrapperTask();
//		 TimerTask regularTask= new UpdateTask();
		 regularTimer.schedule(regularTask,regularDate, O.date.MINUTE_MILLIS);
//
//		 stopSelf();
		 return START_REDELIVER_INTENT;
		 }
	 @Override
	 public void onCreate()
		{
		 super.onCreate();
		 DbHelper dbHelper= new DbHelper(this);
		 dbHelper.initDb();
		 DbHelper.initCursors();
		 Log.d("c123","onCreate: ");
		 notificationManager= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		 initPrefs();
		 }
	 @Nullable
	 @Override
	 public IBinder onBind(Intent intent)
		{
		 return null;
		 }
	 @Override
	 public void onDestroy()
		{
		 regularTimer.cancel();
		 lateTimer.cancel();
		 Log.d(O.TAG,"onDestroy: ");
		 Toaster.makeHomeToast(this,"Сервис "+ id + " закрыт");
		 super.onDestroy();
		 }
	 }
