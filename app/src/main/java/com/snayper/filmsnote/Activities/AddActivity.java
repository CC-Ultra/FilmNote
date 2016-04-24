package com.snayper.filmsnote.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Utils.O;
import com.snayper.filmsnote.Utils.Record_Film;
import com.snayper.filmsnote.Utils.Record_Serial;
import com.snayper.filmsnote.R;

/**
 * <p>Активность для offline добавления</p>
 * Здесь кнопка и поле ввода. Пользователь вводит текст, жмет кнопку и фильм/сериал добавляется в базу. Пользователь может
 * передумать и нажать через меню на переход в online режим
 * <p><sub>(18.02.2016)</sub></p>
 * @author CC-Ultra
 */
public class AddActivity extends GlobalMenuOptions
	{
	 private EditText titleInput;
	 private Toolbar toolbar;
	 private DbConsumer dbConsumer;
	 private int contentType;
	 private int toolbarTextColor,toolbarBackgroundColor;

	/**
	 * Listener для кнопки
	 * @see #acceptPress()
	 */
	 private class OkButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 acceptPress();
			 }
		 }

	/**
	 * Listener {@link #titleInput}, обрабатывающий нажатие {@code Enter} во время ввода
	 * @see #acceptPress()
	 */
	 private class SubmitListener implements TextView.OnEditorActionListener
		{
		 @Override
		 public boolean onEditorAction(TextView v,int actionId,KeyEvent event)
			{
			 acceptPress();
			 return false;
			 }
		 }

	/**
	 * Если пользователь решил добавить сериал, все-таки online. Завершает эту активность и прыгает в {@link WebActivity}
	 * добавляя в {@link Intent} {@code O.interaction.WEB_ACTION_ADD}
	 * @see #onOptionsItemSelected
	 */
	 private void toOnline()
		{
		 finish();
		 Intent jumper= new Intent(this,WebActivity.class);
		 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
		 jumper.putExtra(O.mapKeys.extra.ACTION, O.interaction.WEB_ACTION_ADD);
		 startActivity(jumper);
		 }

	/**
	 * Если был введено секретное ключевое слово, дает доступ к пасхалке. Не зависит от регистра и пробелов по бокам
	 */
	 private boolean easterCheck(String secretKey)
		{
		 return secretKey.toLowerCase().trim().equals("955653 ограбить корован!");
		 }

	/**
	 * Привязывает к {@link #toolbar} меню
	 */
	 private void initToolbar()
		{
		 toolbar=(Toolbar) findViewById(R.id.toolbar);
		 setSupportActionBar(toolbar);
		 }

	/**
	 * Действие при нажатии кнопки или {@code Enter} при вводе. Если позитивная проверка на пасхалку, то переход в {@link EasterActivity},
	 * иначе, если текст не пустой, добавляю запись, в зависимости от ее типа, и выхожу из активности
	 * @see OkButtonListener
	 * @see SubmitListener
	 */
	 private void acceptPress()
		{
		 String title= titleInput.getText().toString();
		 if(easterCheck(title) )
			{
			 Intent jumper= new Intent(AddActivity.this,EasterActivity.class);
			 startActivity(jumper);
			 return;
			 }
		 if(title.length()==0)
			 return;
		 if(contentType==O.interaction.CONTENT_FILMS)
			{
			 Record_Film record= new Record_Film();
			 record.setTitle(title);
			 dbConsumer.putRecord(record);
			 }
		 else
			{
			 Record_Serial record= new Record_Serial();
			 record.setTitle(title);
			 dbConsumer.putRecord(record);
			 }
		 finish();
		 }

	/**
	 * Установка содержимого меню
	 * @see GlobalMenuOptions#onCreateOptionsMenu
	 */
	 @Override
	 protected void setMenuLayout(Menu menu)
		{
		 getMenuInflater().inflate(R.menu.add_menu,menu);
		 }

	/**
	 * При перезагрузке активности нужно упаковать в {@link Intent} те же данные, с которыми она была запущена
	 * @param reset перезапускающий активность {@link Intent}
	 * @see #resetActivity()
	 */
	 @Override
	 protected void putIntentExtra(Intent reset)
		{
		 reset.putExtra(O.mapKeys.extra.CONTENT_TYPE,contentType);
		 }

	/**
	 * Инициализация цветов {@link #toolbar}
	 */
	 @Override
	 protected void initLayoutThemeCustoms()
		{
		 super.initLayoutThemeCustoms();
		 toolbarTextColor=lightTextColor;
		 toolbarBackgroundColor=panelColor;
		 }

	/**
	 * Установка цветов {@link #toolbar}
	 */
	 @Override
	 protected void setLayoutThemeCustoms()
		{
		 super.setLayoutThemeCustoms();
		 toolbar.setTitleTextColor(toolbarTextColor);
		 toolbar.setBackgroundColor(toolbarBackgroundColor);
	 	 }

	/**
	 * Получение элементов страницы, выдача Listener-ов, создание {@link #dbConsumer}, инициализация и покраска {@link #toolbar},
	 */
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
		{
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.add_layout);

		 contentType= getIntent().getIntExtra(O.mapKeys.extra.CONTENT_TYPE,-1);

		 Button okButton= (Button)findViewById(R.id.okButton);
		 titleInput= (EditText)findViewById(R.id.titleInput);

		 dbConsumer= new DbConsumer(this,getContentResolver(),contentType);
		 okButton.setOnClickListener(new OkButtonListener() );
		 titleInput.setOnEditorActionListener(new SubmitListener() );
		 initToolbar();
		 setLayoutThemeCustoms();
		 }

	/**
	 * Если добавление не сериала, то конвертировать в online нельзя. Тогда скрываю пункт меню
	 */
	 @Override
	 public boolean onPrepareOptionsMenu(Menu menu)
		{
		 menu.findItem(R.id.menu_convert).setVisible(contentType!=0);
		 return super.onPrepareOptionsMenu(menu);
		 }

	/**
	 * Срабатывает по нажатию на пункт меню
	 * @param item по нему определяется кто был нажат и что теперь делать
	 */
	 @Override
	 public boolean onOptionsItemSelected(MenuItem item)
		{
		 switch(item.getItemId() )
			{
			 case R.id.menu_convert:
				 toOnline();
				 return true;
			 default:
				 return super.onOptionsItemSelected(item);
			 }
		 }
	 }
