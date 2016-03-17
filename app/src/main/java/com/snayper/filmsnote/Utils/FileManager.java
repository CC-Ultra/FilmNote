package com.snayper.filmsnote.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.*;

/**
 * Created by snayper on 11.03.2016.
 */
public class FileManager
	{
	 public static String getFilenameFromURL(String urlStr)
		{
		 if(urlStr.length()==0)
			{
			 Log.d(O.TAG,"getFilenameFromURL: передано пустое имя файла для извлечения");
			 return "";
			 }
		 String result;
		 int startIndex= urlStr.lastIndexOf('/')+1;
		 result= urlStr.substring(startIndex);
		 return result;
		 }
	 public static String getFileExtension(String filename)
		{
		 if(filename.length()==0)
			{
			 Log.d(O.TAG,"getFileExtension: передано пустое имя файла для извлечения");
			 return "";
			 }
		 String result;
		 int startIndex= filename.lastIndexOf('.')+1;
		 result= filename.substring(startIndex).toLowerCase();
		 return result;
		 }
	 public static String getStoredPicURI(Context context,String filename)
		{
		 if(filename.length()==0)
			{
			 Log.d(O.TAG,"getStoredPicURI: передано пустое имя файла для извлечения");
			 return "";
			 }
		 String result;
		 File dir;
		 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
			 dir= context.getExternalCacheDir();
		 else
			 dir= context.getCacheDir();
		 if(dir==null)
			{
			 Log.d(O.TAG,"getStoredPicURI: путь к папке кэша не найден");
			 return "";
			 }
		 result= dir.getAbsolutePath() +"/"+ filename;
		 if( !(new File(result) ).exists() )
			{
			 Log.d(O.TAG,"Файл "+ filename +" не найден");
			 result="";
			 }
		 return result;
		 }
	 public static void storeWebSrcPic(Context context, String picSrc, int widthResDimen, int heightResDimen)
		{
		 if(picSrc.length()==0)
			{
			 Log.d(O.TAG,"Нечего сохранять, пустая ссылка");
			 return;
			 }
		 Picasso pablo= Picasso.with(context);
		 RequestCreator picControl= pablo.load(picSrc);
		 picControl.resizeDimen(widthResDimen,heightResDimen);
		 picControl.centerInside();
		 Bitmap bitmap;
		 try
			{
			 bitmap= picControl.get();
			 }
		 catch(IOException pabloIO)
			{
			 Log.d(O.TAG,"storeWebSrcPic: Picasso.get exception");
			 return;
			 }
		 File dir;
		 String filepath="";
		 String filename= getFilenameFromURL(picSrc);
		 OutputStream fileOut;
		 try
			{
			 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
				 dir= context.getExternalCacheDir();
			 else
				 dir= context.getCacheDir();
			 if(dir==null)
				{
				 Log.d(O.TAG,"storeWebSrcPic: путь к папке кэша не найден");
				 return;
				 }
			 filepath= dir.getAbsolutePath() +"/"+ filename;
			 fileOut= new FileOutputStream(filepath);
			 }
		 catch(FileNotFoundException fileErr)
			{
			 Log.d(O.TAG,"FileNotFoundException для "+ filepath);
			 return;
			 }
		 catch(Exception unknown)
			{
			 Log.d(O.TAG,"onBitmapLoaded: неведомая ошибка\n");
			 unknown.printStackTrace();
			 return;
			 }
		 switch(getFileExtension(filename) )
			{
			 case "png":
				 bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
				 break;
			 case "jpg":
				 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);
				 break;
			 case "webp":
				 bitmap.compress(Bitmap.CompressFormat.WEBP, 100, fileOut);
			 }
		 try
			{
			 fileOut.close();
			 }
		 catch(IOException closeErr)
			{
			 Log.d(O.TAG,"Не могу закрыть файл "+ filename +" после записи. Файл не сохранен");
			 }
		 }
	 public static void deleteFile(Context context, String filename)
		{
		 if(filename.length()==0)
			{
			 Log.d(O.TAG,"Нечего удалять, пустой адрес");
			 return;
			 }
		 File dir;
		 String filepath;
		 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
			 dir= context.getExternalCacheDir();
		 else
			 dir= context.getCacheDir();
		 if(dir==null)
			{
			 Log.d(O.TAG,"deleteFile: путь к папке кэша не найден");
			 return;
			 }
		 filepath= dir.getAbsolutePath() +"/"+ filename;
		 File fileToDelete=new File(filepath);
		 if(fileToDelete.exists() )
			 fileToDelete.delete();
		 }
	 }
