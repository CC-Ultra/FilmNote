package com.snayper.filmsnote.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.snayper.filmsnote.Adapters.CustomSimpleAdapter_EditList;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Interfaces.AdapterInterface;
import com.snayper.filmsnote.Interfaces.DialogDecision;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Parsers.*;
import com.snayper.filmsnote.Utils.*;
import com.snayper.filmsnote.R;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Активность для управляния записью сериала</p>
 * <p>Записи бывают двух условных типов: offline и onlne добавленными. У вторых можно автоматически или вручную обновлять
 * информацию, что отражается на содержимом страницы: добавляется {@code checkbox} и меняется текст и Listener-ы у кнопок.
 * Признак второго типа записи - не пустая ссылка в поле {@link #webSrc}</p>
 * <p>Класс реализует интерфейсы. {@link AdapterInterface} дает возожность использовать {@link ActionDialog} и обновлять
 * информацию после его работы. {@link WebTaskComleteListener} позволяет обновлять информацию после вызова {@link AsyncParser}.
 * {@link DialogDecision} позволяет вызывать {@link ConfirmDialog}</p>
 * <p><sub>(22.02.2016)</sub></p>
 * @author CC-Ultra
 * @see ConfirmDialog
 * @see AsyncParser
 * @see ActionDialog
 */
public class EditActivity extends GlobalMenuOptions implements AdapterInterface,WebTaskComleteListener,DialogDecision
	{
	 private DbConsumer dbConsumer;
	 private ListView episodeList;
	 private ImageView img;
	 private TextView titleTxtView, dateTxtView;
	 private CheckBox updateBox;
	 private int watched,all;
	 private boolean updateOrder;
	 private int contentType,dbPosition;
	 private String webSrc;
	 private int backgroundRes,checkboxColor,dividerColor;

	/**
	 * Listener для {@link #updateBox}. Меняет флаг {@link O.db#FIELD_NAME_UPDATE_ORDER}, который разрешает сервису обновлять
	 * запись
	 */
	 private class CheckBoxListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 updateOrder=!updateOrder;
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_UPDATE_ORDER,updateOrder);
			 dbConsumer.updateRecord(dbPosition,data);
			 }
		 }

	/**
	 * Listener для {@code allButton}, к которой в зависимости от типа записи привязывается или этот, или {@link UpdateButtonListener}.
	 * Здесь все просто: инкремент {@link #all} и обновление базы
	 */
	 private class AddButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 all++;
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_ALL,all);
			 dbConsumer.updateRecord(dbPosition,data);
			 updateDate();
			 initAdapter();
			 }
		 }

	/**
	 * Listener для {@code allButton}, к которой в зависимости от типа записи привязывается или этот, или {@link AddButtonListener}.
	 * Здесь сбрасывается флаг {@link Record_Serial#confidentDate} (потому что иначе бы сбивался режим обновлений), и в
	 * зависимости от {@link #webSrc} инициализируется и запускается один из типов {@link AsyncParser}.
	 * @see AsyncParser
	 */
	 private class UpdateButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_CONFIDENT_DATE,false);
			 dbConsumer.updateRecord(dbPosition,data);
			 AsyncParser parser;
			 if(webSrc.contains(O.web.filmix.HOST) )
				 parser= new Parser_Filmix(EditActivity.this, EditActivity.this, webSrc, true);
			 else if(webSrc.contains(O.web.seasonvar.HOST) )
				 parser= new Parser_Seasonvar(EditActivity.this, EditActivity.this, webSrc, true);
			 else if(webSrc.contains(O.web.kinogo.HOST) )
				 parser= new Parser_Kinogo(EditActivity.this, EditActivity.this, webSrc, true);
			 else if(webSrc.contains(O.web.onlineLife.HOST) )
				 parser= new Parser_OnlineLife(EditActivity.this, EditActivity.this, webSrc, true);
			 else //if(parser==null)
				{
				 Log.d(O.TAG,"onClick: парсер не был инициализирован, а значит и запущен");
				 return;
				 }
			 parser.execute();
			 }
		 }

	/**
	 * Listener для {@code watchButton}, к которой в зависимости от типа записи привязывается или этот, или {@link WatchButtonListener}.
	 * Тут из {@link #webSrc} делается {@link Uri} и посылается в {@link Intent}, который запустит стандартное средство
	 * обработки веб-ссылок. Как правило, браузер
	 */
	 private class WatchOnlineButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Uri address= Uri.parse(webSrc);
			 Intent openlinkIntent= new Intent(Intent.ACTION_VIEW, address);
			 startActivity(openlinkIntent);
			 }
		 }

	/**
	 * Listener для {@code watchButton}, к которой в зависимости от типа записи привязывается или этот, или {@link WatchOnlineButtonListener}.
	 * Просто вызов {@link #watchEpisode()}
	 * @see #watchEpisode()
	 */
	 private class WatchButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 watchEpisode();
			 }
		 }

	/**
	 * Listener для короткого нажатия на элемент {@link #episodeList}. Просто вызов {@link #watchEpisode()}
	 * @see #watchEpisode()
	 */
	 private class ListItemClickListener_Watch implements AdapterView.OnItemClickListener
		{
		 @Override
		 public void onItemClick(AdapterView<?> parent,View view,int position,long id)
			{
			 watchEpisode();
			 }
		 }

	/**
	 * Listener для длинного нажатия на элемент {@link #episodeList}. Здесь запускается {@link ActionDialog} с такими
	 * требованиями:
	 * <p>2 кнопки с текстами {@code "Отменить последнюю"} и {@code "Отменить последнюю"}, и Listener-ами {@link ActionDialog.SerialCancelListener} и
	 * {@link ActionDialog.SerialDelListener}</p>
	 * @see ActionDialog
	 */
	 private class ListItemLongClickListener implements AdapterView.OnItemLongClickListener
		{
		 @Override
		 public boolean onItemLongClick(AdapterView<?> parent,View view,int position,long id)
			 {
			  String txtLeft="Отменить последнюю";
			  String txtRight="Удалить последнюю";
			  int listenerLeft=O.dialog.LISTENER_SERIAL_CANCEL;
			  int listenerRight=O.dialog.LISTENER_SERIAL_DEL;
			  ActionDialog dialog= new ActionDialog();
			  dialog.viceConstructor(EditActivity.this,contentType,dbPosition,txtLeft,txtRight,listenerLeft,listenerRight);
			  dialog.show(getSupportFragmentManager(),"");
			  return true;
			  }
		 }

	/**
	 * Если пользователь решил, что хочет расширить тип записи до online-добавленной. Для этого получить {@link Record_Serial#webSrc},
	 * и для этого запускается {@link Intent} в {@link WebActivity} с флагом {@link O.interaction#WEB_ACTION_UPDATE}
	 */
	 private void convert()
		{
		 finish();
		 Intent jumper= new Intent(this,WebActivity.class);
		 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
		 jumper.putExtra(O.mapKeys.extra.ACTION, O.interaction.WEB_ACTION_UPDATE);
		 jumper.putExtra(O.mapKeys.extra.POSITION, dbPosition);
		 startActivity(jumper);
		 }

	/**
	 * Отметить как просмотренные все серии. Просто приравниваю {@link #watched} к {@link #all}, обновляю базу и адаптер
	 */
	 private void watchAll()
		{
		 watched=all;
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_WATCHED,watched);
		 dbConsumer.updateRecord(dbPosition,data);
		 initAdapter();
		 }
	/**
	 * Отметить все серии как непросмотренные. Просто обнуляю {@link #watched}, обновляю базу и адаптер
	 */
	 private void unwatchAll()
		{
		 watched=0;
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_WATCHED,watched);
		 dbConsumer.updateRecord(dbPosition,data);
		 initAdapter();
		 }

	/**
	 * Удалить все серии. Обнуляю {@link #watched}, {@link #all} и дату в {@link #dateTxtView} и базе. Обновляю базу и адаптер.
	 */
	 private void clear()
		{
		 watched=0;
		 all=0;
		 String dateSrc="";
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_WATCHED, watched);
		 data.put(O.db.FIELD_NAME_ALL,all);
		 data.put(O.db.FIELD_NAME_DATE,0L);
		 dbConsumer.updateRecord(dbPosition,data);
		 initAdapter();
		 dateTxtView.setText(dateSrc);
		 }

	/**
	 * Если {@code watched<all}, значит есть куда добавлять просмотренную серию. Она добавляетя, заносятся изменения в базу
	 * и прыгает курсор в списке, потому что она может быть далеко внизу. Еще вызывается {@link #initAdapter()}, чтобы обновить
	 * {@link #episodeList}
	 * @see ListItemClickListener_Watch
	 * @see WatchButtonListener
	 */
	 private void watchEpisode()
		{
		 if(watched<all)
			{
			 watched++;
			 HashMap<String,Object> data= new HashMap<>();
			 data.put(O.db.FIELD_NAME_WATCHED,watched);
			 dbConsumer.updateRecord(dbPosition,data);
			 initAdapter();
			 if(watched+3 < all)
				 episodeList.setSelection(all- (watched+3) );
			 }
		 }

	/**
	 * Обновить дату в {@link #dateTxtView} и базе на текущую
	 */
	 private void updateDate()
		{
		 Date currentDate= DateUtil.getCurrentDate();
		 String dateStr= DateUtil.dateToString(currentDate);
		 HashMap<String,Object> data= new HashMap<>();
		 data.put(O.db.FIELD_NAME_DATE,currentDate.getTime());
		 dbConsumer.updateRecord(dbPosition,data);
		 dateTxtView.setText(dateStr);
		 }

	/**
	 * По позиции получаю запись, извлекаю из нее информацию для полей, состояния {@link #updateBox} и имя файла картинки,
	 * а потом устанавливаю их в соответствующие поля, {@link #img} и обновляю адаптер.
	 * @see #initAdapter()
	 */
	 private void initPageByRecord()
		{
		 Record_Serial record= dbConsumer.extractRecord_Serial(dbPosition);
		 String titleSrc= record.getTitle();
		 String dateStr= DateUtil.dateToString(record.getDate() );
		 String imgSrc= FileManager.getStoredPicURI(this,record.getImgSrc() );
		 webSrc= record.getWebSrc();
		 updateOrder= record.hasUpdateOrder();
		 initAdapter();
		 titleTxtView.setText(titleSrc);
		 dateTxtView.setText(dateStr);
		 updateBox.setChecked(updateOrder);
		 if(imgSrc.length() != 0)
			 img.setImageURI(Uri.parse(imgSrc) );
		 }

	/**
	 * По позиции извлекаю запись. Формирую структуру, какую требует {@link CustomSimpleAdapter_EditList}, заполняю ее
	 * согласно полям {@link #all} и {@link #watched}, делаю новый адаптер, передаю его списку. Потом добавляю цвет и толщину
	 * раделителя.
	 */
	 public void initAdapter()
		{
		 Record_Serial record= dbConsumer.extractRecord_Serial(dbPosition);
		 all= record.getAll();
		 watched= record.getWatched();
		 String from[]= {"Episode", "Pic"};
		 int to[]= {R.id.episode, R.id.img};
		 ArrayList< HashMap<String,Object> > listData= new ArrayList<>();
		 HashMap<String,Object> listElementMap;
		 for(int i=0; i<all; i++)
			{
			 listElementMap= new HashMap<>();
			 listElementMap.put("Episode", (i+1) +" Серия");
			 if(i>=watched)
				 listElementMap.put("Pic", false);
			 else
				 listElementMap.put("Pic", true);
			 listData.add(listElementMap);
			 }
		 CustomSimpleAdapter_EditList adapter= new CustomSimpleAdapter_EditList(EditActivity.this,listData,R.layout.edit_list_element,from,to);
		 episodeList.setAdapter(adapter);
		 ColorDrawable divcolor= new ColorDrawable(dividerColor);
		 episodeList.setDivider(divcolor);
		 episodeList.setDividerHeight(2);
		 }
	 @Override
	 public Context getContext()
		{
		 return this;
		 }

	/**
	 * Реализация интерфейса {@link WebTaskComleteListener}. Метод, который срабатывает после вызова {@link AsyncParser}.
	 * Если у извлеченной записи другое количество серий, то в ней обновляется дата на текущую, а потом отдается на окончательное
	 * добавление в {@link ParserResultConsumer}. После чего обновляется страница в {@link #initPageByRecord()}
	 * @param extractedData извлеченная запись
	 * @see #initPageByRecord()
	 * @see ParserResultConsumer
	 */
	 @Override
	 public void useParserResult(Record_Serial extractedData)
		{
		 if(extractedData.getAll()!=all)
			 extractedData.setDate(DateUtil.getCurrentDate() );
		 ParserResultConsumer.useParserResult(this,getContentResolver(),extractedData,O.interaction.WEB_ACTION_UPDATE,contentType,dbPosition);
		 initPageByRecord();
		 }

	/**
	 * Установка содержимого меню
	 * @see GlobalMenuOptions#onCreateOptionsMenu
	 */
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.edit_menu,menu);
		 }

	/**
	 * При перезагрузке активности нужно упаковать в {@link Intent} те же данные, с которыми она была запущена
	 * @param reset перезапускающий активность {@link Intent}
	 * @see #resetActivity()
	 */
	 @Override
	 protected void putIntentExtra(Intent reset)
		{
		 reset.putExtra(O.mapKeys.extra.CONTENT_TYPE,contentType);
		 reset.putExtra(O.mapKeys.extra.POSITION,dbPosition);
		 }

	/**
	 * Инициализация цветов. {@code @SuppressWarnings("deprecation")} нужен, чтобы пользоваться {@link Resources#getColor(int)}
	 * @see #setLayoutThemeCustoms()
	 */
	 @SuppressWarnings("deprecation")
	 @Override
	 protected void initLayoutThemeCustoms()
		{
		 super.initLayoutThemeCustoms();
		 Resources resources= getResources();
		 switch(localThemeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 checkboxColor=darkTextColor;
				 dividerColor= resources.getColor(R.color.list_divider_mentor);
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 checkboxColor=thirdTextColor;
				 dividerColor= resources.getColor(R.color.list_divider_ultra);
				 break;
			 case O.prefs.THEME_ID_COW:
				 checkboxColor=lightTextColor;
				 dividerColor= resources.getColor(R.color.list_background_cow);
				 break;
			 default:
				 checkboxColor=darkTextColor;
				 dividerColor= resources.getColor(R.color.list_divider_mentor);
			 }
		 }

	/**
	 * Установка цвета {@link #updateBox}
	 * @see #initLayoutThemeCustoms()
	 */
	 @Override
	 protected void setLayoutThemeCustoms()
		{
		 super.setLayoutThemeCustoms();
		 updateBox.setTextColor(checkboxColor);
		 }

	/**
	 * Реализация интерфейса {@link DialogDecision}
	 */
	 @Override
	 public void sayNo(int noId) {}
	/**
	 * Реализация интерфейса {@link DialogDecision}
	 * @param yesId указывает на какое именно действие было сказано {@code "Да"}
	 */
	 @Override
	 public void sayYes(int yesId)
		{
		 switch(yesId)
			{
			 case 0:
				 clear();
				 break;
			 }
		 }

	/**
	 * Получаю из {@link Intent} сведения о месте рассматриваемой в активности записи в базе. Потом инициализация элементов
	 * страницы. Создаю {@link #dbConsumer}, теперь можно работать с базой. Инициализирую содержимое страницы в {@link #initPageByRecord()}.
	 * В зависимости от типа записи устаналиваю разный текст и Listener-ы на кнопки {@code watchButton} и {@code watchButton},
	 * а также решаю вопрос видимости {@code updateBox}. Раскрашиваю его в {@link #setLayoutThemeCustoms()}
	 * @see #initPageByRecord()
	 * @see #setLayoutThemeCustoms()
	 */
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.edit_layout);

		 Intent intent=getIntent();
		 contentType=intent.getIntExtra(O.mapKeys.extra.CONTENT_TYPE, -1);
		 dbPosition=intent.getIntExtra(O.mapKeys.extra.POSITION, -1);

		 Button watchButton= (Button)findViewById(R.id.watchButton);
		 Button allButton= (Button)findViewById(R.id.allButton);
		 updateBox= (CheckBox)findViewById(R.id.updateOrder);
		 episodeList= (ListView)findViewById(R.id.episodeList);
		 titleTxtView= (TextView)findViewById(R.id.title);
		 dateTxtView= (TextView)findViewById(R.id.date);
		 img= (ImageView)findViewById(R.id.img);

		 dbConsumer= new DbConsumer(this,getContentResolver(),contentType);
		 initPageByRecord();
		 episodeList.setOnItemClickListener(new ListItemClickListener_Watch());
		 episodeList.setOnItemLongClickListener(new ListItemLongClickListener());
		 updateBox.setOnClickListener(new CheckBoxListener() );
		 if(webSrc.length()!=0)
			{
			 allButton.setText("Обновить информацию");
			 allButton.setOnClickListener(new UpdateButtonListener());
			 watchButton.setText("Просмотреть серию");
			 watchButton.setOnClickListener(new WatchOnlineButtonListener());
			 updateBox.setVisibility(View.VISIBLE);
			 }
		 else
			{
			 allButton.setText("Добавить серию");
			 allButton.setOnClickListener(new AddButtonListener());
			 watchButton.setText("Отметить серию");
			 watchButton.setOnClickListener(new WatchButtonListener());
			 updateBox.setVisibility(View.GONE);
			 }
		 setLayoutThemeCustoms();
		 }

	/**
	 * В зависимости от состояния полей {@link #all} и {@link #watched} скрываются некоторые пункты меню
	 */
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
		{
		 menu.findItem(R.id.menu_convert).setVisible( (webSrc.length()==0) );
		 menu.findItem(R.id.menu_watchAll).setVisible( (watched!=all) );
		 menu.findItem(R.id.menu_unwatchAll).setVisible((watched != 0));
		 menu.findItem(R.id.menu_deleteAll).setVisible((all != 0));
		 return super.onPrepareOptionsMenu(menu);
		 }

	/**
	 * Срабатывает по нажатию на пункт меню
	 * @param item по нему определяется кто был нажат и что теперь делать
	 */
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 switch(item.getItemId() )
			{
			 case R.id.menu_convert:
				 convert();
				 return true;
			 case R.id.menu_watchAll:
				 watchAll();
				 return true;
			 case R.id.menu_unwatchAll:
				 unwatchAll();
				 return true;
			 case R.id.menu_deleteAll:
				 new ConfirmDialog(this,this,0);
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
