package com.snayper.filmsnote.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

/**
 * Активность пасхалки. Тут только один {@link ImageView} с картинкой и {@code Toast}. При переходе сюда устанавливается
 * коровья тема
 * <p><sub>(10.03.2016)</sub></p>
 * @author CC-Ultra
 */
public class EasterActivity extends AppCompatActivity
	{
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.easter_layout);

		 MainActivity.themeSwitcher= O.prefs.THEME_ID_COW;
		 MainActivity.prefs.edit().putInt(O.mapKeys.prefs.THEME, O.prefs.THEME_ID_COW).apply();
		 Toast.makeText(this,"Что это у тебя за тема такая?",Toast.LENGTH_SHORT).show();
		 }
	 }
