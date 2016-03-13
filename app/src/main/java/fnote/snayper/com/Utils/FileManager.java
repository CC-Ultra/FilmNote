package fnote.snayper.com.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.*;

/**
 * Created by snayper on 11.03.2016.
 */
public class FileManager
	{
	 private static class FileSaver implements Target
		{
		 String filename;
		 Context context;

		 FileSaver(Context _context,String _filename)
			{
			 context=_context;
			 filename=_filename;
			 }
		 @Override
		 public void onBitmapLoaded(Bitmap bitmap,Picasso.LoadedFrom loadedFrom)
			{
			 File dir;
			 String filepath="";
			 OutputStream fileOut;
			 try
				{
				 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
					 dir= context.getExternalCacheDir();
				 else
					 dir= context.getCacheDir();
				 filepath= dir.getAbsolutePath() +"/"+ filename;
				 fileOut= new FileOutputStream(filepath);
				 }
			 catch(FileNotFoundException fileErr)
				{
				 Log.d("c123","FileNotFoundException для "+ filepath);
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
				 Log.d("c123","Не могу закрыть файл "+ filename +" после записи. Файл не сохранен");
				 }
			 }
		 @Override
		 public void onBitmapFailed(Drawable drawable)
			{
			 ;
			 }
		 @Override
		 public void onPrepareLoad(Drawable drawable)
			{
			 ;
			 }
		 }

	 public static String getFilenameFromURL(String urlStr)
		{
		 String result;
		 int startIndex= urlStr.lastIndexOf('/')+1;
		 result= urlStr.substring(startIndex);
		 return result;
		 }
	 public static String getFileExtension(String filename)
		{
		 String result;
		 int startIndex= filename.lastIndexOf('.')+1;
		 result= filename.substring(startIndex).toLowerCase();
		 return result;
		 }
	 public static void storedPicToImageView(Context context,ImageView img,String filename)
		{
		 String picSrc;
		 File dir;
		 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) )
			 dir= context.getExternalCacheDir();
		 else
			 dir= context.getCacheDir();
		 picSrc= dir.getAbsolutePath() +"/"+ filename;
		 if( (new File(picSrc) ).exists() )
			 img.setImageURI(Uri.parse(picSrc));
		 else
			 Log.d("c123","Файл "+ filename +" не найден");
		 }
	 public static void storeWebSrcPic(Context context,String picSrc,int widthResDimen,int heightResDimen)
		{
		 Picasso pablo= Picasso.with(context);
		 RequestCreator picControl= pablo.load(picSrc);
		 picControl.resizeDimen(widthResDimen,heightResDimen);
		 picControl.centerInside();
		 picControl.into(new FileSaver(context, getFilenameFromURL(picSrc) ) );
		 }
	 }
