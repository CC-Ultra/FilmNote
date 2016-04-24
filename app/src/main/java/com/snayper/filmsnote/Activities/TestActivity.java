package com.snayper.filmsnote.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.*;
import java.util.*;

/**
 * Тестовая активность, чтобы если что, изолированно от остального остальных протестировать какой-то функционал. Использует
 * test_layout. Для работы нужно прописать {@code intent-filter} в манифесте
 * <p><sub>(17.04.2016)</sub></p>
 * @author CC-Ultra
 */
public class TestActivity extends AppCompatActivity
	{
	 private class OkButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
//			 ;
			 }
		 }
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.test_layout);

		 Button okButton= (Button)findViewById(R.id.okButton);
		 okButton.setOnClickListener(new OkButtonListener() );
		 }
	 }
