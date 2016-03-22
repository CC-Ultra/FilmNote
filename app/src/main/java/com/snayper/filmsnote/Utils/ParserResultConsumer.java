package com.snayper.filmsnote.Utils;

import android.content.Context;
import com.snayper.filmsnote.Activities.WebTaskComleteListener;

import java.util.HashMap;

/**
 * Created by snayper on 18.03.2016.
 */
public class ParserResultConsumer
	{
	 public static void useParserResult(Context context,Record_Serial extractedData,int action,int contentType,int dbPosition)
		{
		 if( (extractedData!=null) && (extractedData.getTitle().length()!=0) )
			{
			 extractedData.setImgSrc(FileManager.getFilenameFromURL(extractedData.getImgSrc()));
			 if( FileManager.getStoredPicURI(context, extractedData.getImgSrc() ).length() == 0)
				 extractedData.setImgSrc("");
			 if(action == O.interaction.WEB_ACTION_ADD)
				{
				 extractedData.setDate(Util.getCurentDate());
				 DbHelper.putRecord_Serial(extractedData,contentType);
				 }
			 if(action == O.interaction.WEB_ACTION_UPDATE)
				{
				 Record_Serial dbRecord= DbHelper.extractRecord_Serial(contentType,dbPosition);
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
				 data.put(O.db.FIELD_NAME_WEB,dbRecord.getWebSrc());
				 if(extractedData.getImgSrc().length()!=0)
					{
					 dbRecord.setImgSrc( extractedData.getImgSrc() );
					 data.put(O.db.FIELD_NAME_IMG, dbRecord.getImgSrc() );
					 }
				 if(extractedData.getDate().length()!=0)
					{
					 dbRecord.setDate( extractedData.getDate() );
					 data.put(O.db.FIELD_NAME_DATE, dbRecord.getDate() );
					 }
				 DbHelper.updateRecord(contentType,dbPosition,data);
				 }
			 }
		 }
	}
