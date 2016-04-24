package com.snayper.filmsnote.Services;

import android.app.*;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.snayper.filmsnote.Activities.MainActivity;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Db.DbProvider;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * <p>Сервис обновления сериалов</p>
 * Всю работу сервиса можно описать так: на старте проверяет время и инициализирует таймеры. Дальше работает {@link UpdateTask}.
 * По закрытию сервиса, таймеры останавливаются. Если находит обновления, шлет уведомление или {@code Toast} и правит базу.
 * Использует общие с приложением {@link SharedPreferences} и базу (через {@link DbProvider} и {@link DbConsumer}).
 * Работает в отдельном процессе и должен воскресать после закрытия по нехватке памяти. Закрывается из приложения методом
 * {@link MainActivity#exit()}
 * <p><sub>(28.03.2016)</sub></p>
 * @author CC-Ultra
 * @see DbConsumer
 * @see DbProvider
 * @see Context#MODE_MULTI_PROCESS
 * @see NotificationManager
 */
public class Updater extends Service
	{
	 private SharedPreferences prefs;
	 private DbConsumer dbConsumer_serial,dbConsumer_mult;
	 private final Handler handler = new Handler();
	 private Timer lateTimer= new Timer();
	 private Timer regularTimer= new Timer();
	 private NotificationManager notificationManager;
	 private PendingIntent activityAgent;
	 private boolean gsmOrder;
	 private int updateInterval;
	 private long updateTime;
	 private int notificationType;
	 private int notificationId=0;
	 private int id= android.os.Process.myPid();
	 private DummyListener dummyListener= new DummyListener();

	/**
	 * {@link AsyncParser} и любой из его потомков требуют callback для совершения завершающих действий после извлечения
	 * каких-то данных. В приложении передавались сами активности, но здесь для этого используется объект этого класса.
	 * @see WebTaskComleteListener
	 * @see ParserResultConsumer#useParserResult(Context, ContentResolver, Record_Serial, int, int, int)
	 */
	 private class DummyListener implements WebTaskComleteListener
		{
		 @Override
		 public void useParserResult(Record_Serial extractedData) {}
		 }

	/**
	 * Нужен для того, чтобы не путать записи из таблицы сериалов с записями из таблицы мультов. Можно назвать объект этого
	 * класса составным индексом. Используется в картах в качестве ключа для записи
	 */
	 private class RecordInfo
		{
		 int contentType,dbPosition;
		 public RecordInfo(int _contentType,int _dbPosition)
			{
			 contentType=_contentType;
			 dbPosition=_dbPosition;
			 }
		 }

	/**
	 * Есть заморочки с классами {@link Handler}, и кажется, {@link Looper}, которые не позволяют запускать {@link AsyncTask}
	 * в {@link TimerTask}. А мне это нужно, потому что на этой базе написан {@link AsyncParser}. Но если сделать все через
	 * еще одного посредника таким вот образом, все заработает. Понятия не имею почему, я это нагуглил. Этот же класс, по
	 * сути своей, только обертка над {@link UpdateTask}
	 * @see UpdateTask
	 * @see Handler
	 */
	 private class WrapperTask extends TimerTask
		{
		 @Override
		 public void run()
			{
			 handler.post(new UpdateTask() );
			 }
		 }

	/**
	 * Здесь производится вся работа сервиса.
	 */
	 private class UpdateTask extends TimerTask
		{
		/**
		 * Сначала, конечно, идет проверка настроек, потому что со времени последнего запуска задачи они могли 100 раз
		 * измениться. Потом получаю список сериалов для обновления в два этапа. Через методы {@link #getListforUpdate()}
		 * и {@link #filterListForActual(HashMap)}. Идет проверка на подключение к wi-fi и разрешение работать без него.
		 * Если да, то начинается вся работа. Идет перебор по списку сериалов для проверки, получаю их парсер, и делаю
		 * {@link AsyncTask#execute(Object[])}, {@link AsyncTask#get()}. То что последний замораживает поток до своего
		 * выполнения меня не волнует, он все равно фоновый и невидимый. Результатом {@link AsyncTask#get()} есть некая
		 * {@code Record_Serial webExtracted}, которая может быть запросто и {@code null}. Но если нет и серий в ней больше
		 * чем было до обновления, значит эта запись действительно содержит более свежие данные, осталось только провести
		 * манипуляции с базой. Если {@code Record_Serial.confidentDate} был {@code false} (было неизвестно по каким дням
		 * выходит сериал), то уставливается текущая дата, а иначе прошлая дата, сдвинутая на кратное интервалу обновления
		 * время. {@code Record_Serial.confidentDate} устанавливается в {@code true} и запись передается в
		 * {@link ParserResultConsumer#useParserResult} для окончательной обработки и добавления в базу. Сама запись добавится
		 * в {@code ArrayList<String> updatedTitles}, по которому (если по окончанию перебора всех записей к обновлению он
		 * окажется не пустым), будет сформировано и отправлено уведомление, согласно выбранному пользователем типу.
		 * @see ParserResultConsumer#useParserResult
		 * @see #sendNotification(ArrayList)
		 * @see AsyncTask#get()
		 */
		 @Override
		 public void run()
			{
			 initPrefs();
			 Log.d(O.TAG,"run: начало");
			 ArrayList<String> updatedTitles= new ArrayList<>();
			 HashMap<RecordInfo,Record_Serial> records= filterListForActual(getListforUpdate() );
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
						 Log.d(O.TAG,"Ищу обновление для: "+dbRecord.getTitle()+" confident="+dbRecord.isConfidentDate() );
						 webExtracted= parser.get();
						 if(webExtracted==null)
							 Toaster.makeHomeToast(Updater.this,"Ошибка соединения, инет сдох");
						 else if(allBefore < webExtracted.getAll() )
							{
							 updatedTitles.add(webExtracted.getTitle() );
							 webExtracted.setUpdated(true);
							 Date currentDate= DateUtil.getCurrentDate();
							 if(!dbRecord.isConfidentDate() )
								 webExtracted.setDate(currentDate);
							 else
								{
								 long difftime= currentDate.getTime() - dbRecord.getDate().getTime();
//								 long longInterval= O.date.MINUTE_MILLIS*2; //тестовое
								 long longInterval= O.date.DAY_MILLIS * updateInterval;
								 long addingDifftime= (difftime/longInterval)*longInterval;
								 webExtracted.setDate(new Date(dbRecord.getDate().getTime() + addingDifftime) );
 								 }
							 webExtracted.setConfidentDate(true);
							 RecordInfo info= x.getKey();
							 ParserResultConsumer.useParserResult(Updater.this, getContentResolver(), webExtracted,
									 O.interaction.WEB_ACTION_UPDATE, info.contentType, info.dbPosition);
							 Log.d(O.TAG,"Запись обновлена: "+webExtracted.getTitle()+" confident="+webExtracted.isConfidentDate() );
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
			 Log.d(O.TAG,"run: конец");
			 }
		 }

	/**
	 * Из входящей строки извлекается host, по которому подбирается нужный парсер
	 * @param webSrc строка с адресом, откуда был извлечен сериал
	 * @return парсер, семейства {@link AsyncParser}
	 * @see Parser_Filmix
	 * @see Parser_Seasonvar
	 * @see Parser_Kinogo
	 * @see Parser_OnlineLife
	 */
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

	/**
	 * Перебирает все сериалы из двух таблиц в поисках {@code Record_Serial.updateOrder}, чтобы сформировать список таких.
	 * @return {@link HashMap} из составного индекса в качестве ключа и записи в качестве значения
	 * @see RecordInfo
	 * @see DbConsumer
	 */
	 private HashMap<RecordInfo,Record_Serial> getListforUpdate()
		{
		 HashMap<RecordInfo,Record_Serial> result= new HashMap<>();
		 for(int i=0; i<dbConsumer_serial.getCount(); i++)
			{
			 Record_Serial record= dbConsumer_serial.extractRecord_Serial(i);
			 RecordInfo info= new RecordInfo(O.interaction.CONTENT_SERIAL,i);
			 if(record.hasUpdateOrder() )
				 result.put(info,record);
			 }
		 for(int i=0; i<dbConsumer_mult.getCount(); i++)
			{
			 Record_Serial record= dbConsumer_mult.extractRecord_Serial(i);
			 RecordInfo info= new RecordInfo(O.interaction.CONTENT_MULT,i);
			 if(record.hasUpdateOrder() )
				 result.put(info,record);
			 }
		 return result;
		 }

	/**
	 * Выбирает из всех сериалов, которые разрешено обновлять, тех кого не просто можно, а именно нужно обновлять прямо сейчас.
	 * Под это попадают те, у кого {@code Record_Serial.isConfidentDate()==false}, и те, у кого подошли сроки, согласно их дате
	 * и общему интервалу обновлений
	 * @param from список сериалов для обновления, собранный в {@link #getListforUpdate()}
	 * @return новый список, подмножество входящего списка
	 * @see #getListforUpdate()
	 */
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
//			 long difftime= DateUtil.getCurrentDate().getTime() - record.getDate().getTime(); //тестовое
//			 if(difftime >= O.date.MINUTE_MILLIS*2) //тестовое
			 long difftime= DateUtil.dropTime(DateUtil.getCurrentDate() ).getTime() - DateUtil.dropTime(record.getDate() ).getTime();
			 if(difftime/O.date.DAY_MILLIS >= updateInterval)
				 result.put(x.getKey(),record);
			 }
		 return result;
		 }

	/**
	 * {@code @SuppressWarnings("deprecation")} нужен, чтобы пользоваться {@link ConnectivityManager#getNetworkInfo(int)}.
	 * Как это все работает, опять же, не знаю. Вопрос больше к гуглу, который мне об этом рассказал
	 */
	 @SuppressWarnings("deprecation")
	 private boolean checkForWifi()
		{
		 ConnectivityManager connectivityManager= (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		 return info.isConnected();
		 }

	/**
	 * Инициализация полей и режимов из {@link SharedPreferences}. {@code @SuppressWarnings("deprecation")} нужен, чтобы
	 * пользоваться {@link Context#MODE_MULTI_PROCESS}.
	 */
	 @SuppressWarnings("deprecation")
	 private void initPrefs()
		{
		 prefs= getSharedPreferences(O.mapKeys.prefs.PREFS_FILENAME, MODE_MULTI_PROCESS);
		 gsmOrder= prefs.getBoolean(O.mapKeys.prefs.GSM_ORDER,false);
		 updateInterval= prefs.getInt(O.mapKeys.prefs.UPDATE_INTERVAL,7);
		 notificationType= prefs.getInt(O.mapKeys.prefs.NOTIFICATION_TYPE, O.prefs.NOTIFICATION_TYPE_ID_DEFAULT);
		 updateTime= prefs.getLong(O.mapKeys.prefs.UPDATE_TIME, DateUtil.buildTime(20,0).getTime() );
		 }

	/**
	 * Строит строку для {@link #sendNotification(ArrayList)}, которая всплывет в каком-то из уведомлений.
	 * @param titles список наименований обновленных сериалов
	 * @return если {@code titles==null}, то пустая строка, чисто чтобы избежать {@link NullPointerException}, а такого быть
	 * не должно. Если наименований больше двух, то сообщение строится на их количестве, а не на наименованиях
	 */
	 private String buildNotificationMessage(ArrayList<String> titles)
		{
		 String result;
		 if(titles==null)
			 return "";
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

	/**
	 * <p>Отправить уведомление, выбранного пользователем типа.</p>
	 * <p>Если {@code Toast}, то все просто и быстро</p>
	 * Иначе делается {@link NotificationCompat.Builder}, в который кроме всего прочего упаковываются сообщение из
	 * {@link #buildNotificationMessage(ArrayList)} и {@link PendingIntent} для перехода в главную активность по нажатию
	 * на уведомление. Мигание индикатором в наличии
	 * @param titles нужны для {@link #buildNotificationMessage(ArrayList)}
	 */
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
			 builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.notification_new_episodes) );
			 builder.setTicker("Новые серии!");
			 builder.setWhen(System.currentTimeMillis() );
			 builder.setContentTitle("Новые серии");
			 builder.setContentText(message);
			 builder.setNumber(titles.size());
			 builder.setContentIntent(pendingIntent);
			 Notification notification= builder.build();
			 notification.ledARGB=0x0000FF00;
			 notification.ledOffMS=5000;
			 notification.ledOnMS=300;
			 notification.flags|= Notification.FLAG_AUTO_CANCEL;
			 notification.flags|= Notification.FLAG_SHOW_LIGHTS;
			 notificationManager.notify(notificationId++, notification);
			 }
		 }

	/**
	 * Здесь идет проверка когда был запущен сервис. Есть 2 варианта развития событий. {@code regularDate} устанавливается
	 * на текущую дату, указанное в настройках время. Если время запуска сервиса после  установленного в настройках, то
	 * запустится {@code lateTimer}, а {@code regularDate} сместится на следующий день. {@code regularTimer} реализует
	 * нормальную ежедневную работу севиса.
	 * @param intent отсюда я получаю {@code PendingIntent activityAgent} для обратной связи с запустившей сервис активностью
	 *               но использовать его не довелось
	 * @param flags не используется
	 * @param startId не используется
	 * @return флаг {@link Service#START_REDELIVER_INTENT}, призванный возрождать сервис, если он был закрыт системой
	 */
	 @Override
	 public int onStartCommand(Intent intent,int flags,int startId)
		{
		 activityAgent= intent.getParcelableExtra(O.mapKeys.extra.PENDING_INTENT_AS_EXTRA);
		 Log.d(O.TAG,"onStartCommand: Сервис "+ id + " запущен");
		 Date currentDate= DateUtil.getCurrentDate();
		 Date regularDate= DateUtil.combineDateAndTime(currentDate,new Date(updateTime) );
		 if(currentDate.after(regularDate) )
			{
			 TimerTask lateTask= new WrapperTask();
			 lateTimer.schedule(lateTask,0);
			 regularDate.setTime(regularDate.getTime() + O.date.DAY_MILLIS);
			 }

		 TimerTask regularTask= new WrapperTask();
		 regularTimer.schedule(regularTask,regularDate,O.date.DAY_MILLIS);
		 return START_REDELIVER_INTENT;
		 }

	/**
	 * инициализации двух {@link DbConsumer} и {@link NotificationManager}. Их 2 потому что они привязаны каждый к своей
	 * таблице. Это на уровне uri
	 * @see DbConsumer
	 */
	 @Override
	 public void onCreate()
		{
		 super.onCreate();
		 dbConsumer_serial= new DbConsumer(this,getContentResolver(),O.interaction.CONTENT_SERIAL);
		 dbConsumer_mult= new DbConsumer(this,getContentResolver(),O.interaction.CONTENT_MULT);
		 Log.d("c123","onCreate: ");
		 notificationManager= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		 initPrefs();
		 }

	/**
	 * остановка таймеров
	 */
	 @Override
	 public void onDestroy()
		{
		 regularTimer.cancel();
		 lateTimer.cancel();
		 Log.d(O.TAG,"onDestroy: Сервис "+ id + " закрыт");
		 super.onDestroy();
		 }
	 @Nullable
	 @Override
	 public IBinder onBind(Intent intent)
		{
		 return null;
		 }
	 }
