package com.snayper.filmsnote.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.*;

/**
 * <p>Класс созданный для кеширования. Сохраняет картинки по ссылке, читает, удаляет, работает с именами</p>
 * Сохранение производится средствами класса {@link Bitmap} и библиотеки {@code picasso} ({@link Picasso}, {@link RequestCreator}).
 * Папка для кеширования стандартная системная, предпочтение карте памяти.
 * <p><sub>(11.03.2016)</sub></p>
 * @author CC-Ultra
 */
public class FileManager
	{
	/**
	 * Отсекает всю строку до последнего {@code '/'} включительно, оставляя только имя
	 * @return пустая строка, если на вход пришла пустая или {@code null}, или вырезанное из url имя файла
	 */
	 public static String getFilenameFromURL(String urlStr)
		{
		 if(urlStr==null || urlStr.length()==0)
			{
			 Log.d(O.TAG,"getFilenameFromURL: передано пустое имя файла для извлечения");
			 return "";
			 }
		 String result;
		 int startIndex= urlStr.lastIndexOf('/')+1;
		 result= urlStr.substring(startIndex);
		 return result;
		 }

	/**
	 * Отсекает всю строку до последней {@code '.'} включительно, оставляя только расширение
	 * @return пустая строка, если на вход пришла пустая или {@code null}, или вырезанное из имени файла расширение
	 */
	 private static String getFileExtension(String filename)
		{
		 if(filename==null || filename.length()==0)
			{
			 Log.d(O.TAG,"getFileExtension: передано пустое имя файла для извлечения");
			 return "";
			 }
		 String result;
		 int startIndex= filename.lastIndexOf('.')+1;
		 result= filename.substring(startIndex).toLowerCase();
		 return result;
		 }

	/**
	 * получаю папку, делаю из нее полный путь к файлу
	 * @param context нужен для получения адреса системной папки cache
	 * @return строка, которую можно использовать для {@link ImageView#setImageURI}
	 */
	 public static String getStoredPicURI(Context context,String filename)
		{
		 if(filename==null || filename.length()==0)
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

	/**
	 * Создаю объект {@link Picasso}, говорю ему грузить картинку (и грузится оно, очевидно, асинхронно), устанавливаю размеры
	 * и центрирование, и в конечном итоге делаю {@link RequestCreator#get()}. Тут можно подождать, пусть грузится, все равно
	 * работа ведется асинхронно. Потом получаю имя файла, расположение папки, полный путь и создаю {@link OutputStream}.
	 * В зависимости от расширения с помощью этого потока делаю {@link Bitmap#compress} и закрываю поток, сохраняя файл.
	 * @param context нужен для получения адреса системной папки cache
	 * @param picSrc ссылка на картинку
	 * @param widthResDimen ширина сохраняемой
	 * @param heightResDimen высота сохраняемой
	 */
	 public static void storeWebSrcPic(Context context, String picSrc, int widthResDimen, int heightResDimen)
		{
		 if(picSrc==null || picSrc.length()==0)
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

	/**
	 * получение папки, создание пути, удаление
	 * @param context нужен для получения адреса системной папки cache
	 */
	 public static void deleteFile(Context context, String filename)
		{
		 if(filename==null || filename.length()==0)
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
		 File fileToDelete= new File(filepath);
		 if(fileToDelete.exists() )
			 fileToDelete.delete();
		 }
	 }
