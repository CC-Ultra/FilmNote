package com.snayper.filmsnote.Utils;

import android.content.ContentResolver;
import android.content.Context;
import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Activities.WebActivity;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Interfaces.WebTaskComleteListener;
import com.snayper.filmsnote.Parsers.AsyncParser;
import com.snayper.filmsnote.Services.Updater;

import java.util.HashMap;

/**
 * Утильный класс на один метод, основное назначение которого было в том, чтобы избежать дублирования кода при невозможности
 * пользоваться наследованием. Вызывается в реализациях {@link WebTaskComleteListener#useParserResult(Record_Serial)} при
 * работе с {@link AsyncParser}. В каждом случае реализующий класс добавляет что-то от себя до или после выполнения метода.
 * Конечная цель метода - сделать запись в базу, если она отвечает требованиям
 * <p><sub>(18.03.2016)</sub></p>
 * @author CC-Ultra
 * @see WebTaskComleteListener
 * @see AsyncParser
 * @see WebActivity
 * @see EditActivity
 * @see Updater
 */
public class ParserResultConsumer
	{
	/**
	 * С {@code null} работать дальше нет смысла, как и с сериалом без названия. Проверяю можно ли по {@link Record_Serial#imgSrc}
	 * подгрузить картинку, и если нет - обнуляю это поле. Если требовалось просто добавление, то ловлю текущую дату и добавляю
	 * запись. Иначе, извлекаю по {@code dbPosition} запись и начинаю перегонять данные из {@code extractedData} в {@code dbRecord}.
	 * Попутно проверяя не обнулился ли {@code extractedData.all}, не нужно ли убавить {@code dbRecord.watched}, не пустые ли
	 * имя файла и дата. Если какой-то флажок не стоит, о нем умалчивается. Потом запрос на обновление.
	 * @param resolver нужен для того чтобы делать запрос, для класса {@link DbConsumer}
	 * @param extractedData извлеченная парсером запись
	 * @param action определяет что делать с записью
	 * @param contentType инициализирует {@link DbConsumer}
	 * @param dbPosition какую запись обновлять при {@code action == O.interaction.WEB_ACTION_UPDATE}
	 * @return извлеклось ли что-то, или пришла пустая запись
	 */
	 public static boolean useParserResult(Context context,ContentResolver resolver,Record_Serial extractedData,int action,int contentType,int dbPosition)
		{
		 if( (extractedData!=null) && (extractedData.getTitle().length()!=0) )
			{
			 DbConsumer dbConsumer= new DbConsumer(context,resolver,contentType);
			 if( FileManager.getStoredPicURI(context, extractedData.getImgSrc() ).length() == 0)
				 extractedData.setImgSrc("");
			 if(action == O.interaction.WEB_ACTION_ADD)
				{
				 extractedData.setDate(DateUtil.getCurrentDate() );
				 dbConsumer.putRecord(extractedData);
				 }
			 else //if(action == O.interaction.WEB_ACTION_UPDATE)
				{
				 Record_Serial dbRecord= dbConsumer.extractRecord_Serial(dbPosition);
				 HashMap<String,Object> data= new HashMap<>();
				 dbRecord.setTitle( extractedData.getTitle() );
				 data.put(O.db.FIELD_NAME_TITLE,dbRecord.getTitle());
				 if(extractedData.getAll()!=0)
					 dbRecord.setAll( extractedData.getAll() );
				 data.put(O.db.FIELD_NAME_ALL,dbRecord.getAll());
				 if(extractedData.getAll() < dbRecord.getWatched() )
					{
					 dbRecord.setWatched( extractedData.getAll() );
					 data.put(O.db.FIELD_NAME_WATCHED,dbRecord.getWatched() );
					 }
				 dbRecord.setWebSrc( extractedData.getWebSrc() );
				 data.put(O.db.FIELD_NAME_WEB,dbRecord.getWebSrc() );
				 if(extractedData.getImgSrc().length()!=0)
					{
					 dbRecord.setImgSrc( extractedData.getImgSrc() );
					 data.put(O.db.FIELD_NAME_IMG, dbRecord.getImgSrc() );
					 }
				 if(extractedData.getDate() != null)
					{
					 dbRecord.setDate( extractedData.getDate() );
					 data.put(O.db.FIELD_NAME_DATE, dbRecord.getDate().getTime() );
					 }
				 if(extractedData.isConfidentDate() )
					{
					 dbRecord.setConfidentDate(true);
					 data.put(O.db.FIELD_NAME_CONFIDENT_DATE, dbRecord.isConfidentDate() );
					 }
				 if(extractedData.isUpdated() )
					{
					 dbRecord.setUpdated(true);
					 data.put(O.db.FIELD_NAME_UPDATE_MARK, dbRecord.isUpdated() );
					 }
				 if(extractedData.hasUpdateOrder() )
					{
					 dbRecord.setUpdateOrder(true);
					 data.put(O.db.FIELD_NAME_UPDATE_ORDER, dbRecord.hasUpdateOrder() );
					 }
				 dbConsumer.updateRecord(dbPosition,data);
				 }
			 return true;
			 }
		 else
			 return false;
		 }
	}
