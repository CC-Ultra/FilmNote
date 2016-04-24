package com.snayper.filmsnote.Interfaces;

import android.os.AsyncTask;
import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Activities.WebActivity;
import com.snayper.filmsnote.Parsers.AsyncParser;
import com.snayper.filmsnote.Services.Updater;
import com.snayper.filmsnote.Utils.Record_Serial;

/**
 * <p>Callback для активностей и сервисов, которые вызывают {@link AsyncParser}</p>
 * Семейство парсеров на основе {@link AsyncParser} принимают в конструкторе этот callback, чтобы по окончанию работы парсера
 * пустить в ход извлеченную запись без использования {@link AsyncTask#get()}, который замораживает UI
 * <p><sub>(05.03.2016)</sub></p>
 * @author CC-Ultra
 * @see AsyncParser
 * @see WebActivity
 * @see EditActivity
 * @see Updater.DummyListener
 */
public interface WebTaskComleteListener
	{
	 void useParserResult(Record_Serial extractedData);
	 }
