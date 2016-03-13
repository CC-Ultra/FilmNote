package fnote.snayper.com.Activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import fnote.snayper.com.Utils.FileManager;
import fnote.snayper.com.filmsnote.R;

/**
 * Created by snayper on 10.03.2016.
 */
public class ImageActivity extends AppCompatActivity
	{
	 String imgSrc="http://kinogo.co/uploads/posts/2016-02/1454483603_zachetnyyprepod2.jpg";
	 ImageView img;

	 class infoClickListener implements ImageView.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 Bitmap b;

			 img.buildDrawingCache();
			 b= img.getDrawingCache();

//			 BitmapDrawable bd= (BitmapDrawable)img.getDrawable();
//			 b= bd.getBitmap();

			 if(b!=null)
				{
				 Log.d("c123","w="+b.getWidth()+"\th="+b.getHeight());
//				 Log.d("c123","Memory: "+ b.getByteCount() );
				 Log.d("c123","Memory: "+ b.getRowBytes()*b.getHeight() );
				 }
			 }
		 }
	 class TestClickListener implements ImageView.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
//			 FileManager.storeWebSrcPic(ImageActivity.this, imgSrc, R.dimen.img_w, R.dimen.img_h);
			 FileManager.storedPicToImageView(ImageActivity.this, img, FileManager.getFilenameFromURL(imgSrc) );
/*/
			 Bitmap b;

//			 img.buildDrawingCache();
//			 b= img.getDrawingCache();

//			 BitmapDrawable bd= (BitmapDrawable)img.getDrawable();
//			 b= bd.getBitmap();

			 RequestCreator picassoControl= picasso.load(imgSrc);
			 picassoControl.resizeDimen(R.dimen.img_w,R.dimen.img_h).centerInside();
			 try
				{
				 b= picassoControl.get();
				 Log.d("c123","w="+b.getWidth()+"\th="+b.getHeight());
				 Log.d("c123","Memory: "+ b.getByteCount() );
				 }
			 catch(IOException e) {}
/*/
			 }
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.image_layout);

		 Button button= (Button)findViewById(R.id.testButton);
		 Button infoButton= (Button)findViewById(R.id.infoButton);
		 img= (ImageView)findViewById(R.id.img);
/*/
		 picasso= Picasso.with(this);
		 RequestCreator picassoControl= picasso.load(imgSrc);
		 picassoControl.resizeDimen(R.dimen.img_w,R.dimen.img_h).centerInside().into(img);
/*/
		 button.setOnClickListener(new TestClickListener() );
		 infoButton.setOnClickListener(new infoClickListener() );
		 }
	 }
