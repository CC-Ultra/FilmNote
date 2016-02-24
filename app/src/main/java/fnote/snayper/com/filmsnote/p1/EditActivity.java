package fnote.snayper.com.filmsnote.p1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import fnote.snayper.com.filmsnote.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by snayper on 22.02.2016.
 */
public class EditActivity extends AppCompatActivity
	{
	 Button watchButton,cancelButton;
	 ListView episodeList;
	 ImageView img;
	 TextView titleTxtView, dateTxtView;
	 String titleSrc;
	 String dateSrc;
	 int watched,all;
	 int contentType;

	 class WatchButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 ;
			 }
		 }
	 class CancelButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 ;
			 }
		 }

	 String getCurentDate()
		{
		 String result;
		 Calendar date= Calendar.getInstance();
		 int day= date.get(Calendar.DAY_OF_MONTH);
		 String dayStr= (day<10 ? "0" : "") + day;
		 int month= date.get(Calendar.MONTH) +1;
		 String monthStr= (month<10 ? "0" : "") + month;
		 int year= date.get(Calendar.YEAR);
		 result= ""+ dayStr +"."+ monthStr +"."+ year;
		 return result;
		 }

	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.edit_layout);

		 Intent intent=getIntent();
		 contentType=intent.getIntExtra("Content type",-1);
		 titleSrc=intent.getStringExtra("Title");
		 watched= intent.getIntExtra("Watched episodes",-1);
		 all= intent.getIntExtra("All episodes",-1);
		 dateSrc= getCurentDate();

		 watchButton= (Button)findViewById(R.id.watchButton);
		 cancelButton= (Button)findViewById(R.id.cancelButton);
		 episodeList= (ListView)findViewById(R.id.episodeList);
		 titleTxtView= (TextView)findViewById(R.id.title);
		 dateTxtView= (TextView)findViewById(R.id.date);
		 img= (ImageView)findViewById(R.id.img);

		 String from[]= new String[all];
		 int to[]= new int[all];
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
//		 String listSrc[]= new String[all];
		 CustomSimpleAdapter adapter= new CustomSimpleAdapter(EditActivity.this, listData, android.R.layout.simple_list_item_1, from, to);
//		 ArrayAdapter<String> adapter= new ArrayAdapter<>(EditActivity.this,android.R.layout.simple_list_item_1,listSrc);

		 episodeList.setAdapter(adapter);
		 titleTxtView.setText(titleSrc);
		 dateTxtView.setText(dateSrc);
		 watchButton.setOnClickListener(new WatchButtonListener() );
		 cancelButton.setOnClickListener(new CancelButtonListener() );
		 }
	}
