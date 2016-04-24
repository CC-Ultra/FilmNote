package com.snayper.filmsnote.Utils;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Interfaces.DialogDecision;

/**
 * <p>Простой диалог {@code y/n}, который вызывается в одну строчку, как какой-нибудь {@code Toast}</p>
 * Реализован на базе {@link ActionDialog} и требует для работы {@link AppCompatActivity}, чтобы вызывать {@link FragmentActivity#getSupportFragmentManager()}
 * для запуска {@link ActionDialog}, и объекта, реализующего интерфейс callback-а {@link DialogDecision}, чтобы по нажатию
 * на какую-то кнопку сделать нужное действие. Все. Остальное он берет на себя
 * <p><sub>(30.03.2016)</sub></p>
 * @author CC-Ultra
 * @see ActionDialog
 * @see DialogDecision
 */
public class ConfirmDialog
	{
	 AppCompatActivity activity;
	 DialogDecision parent;
	 int yesId,noId=-1;

	/**
	 * {@link #yesId} связан с реализацией метода {@link DialogDecision#sayYes(int)}, указывает какому именно действию было
	 * сказано {@code Да}
	 */
	 class YesListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 parent.sayYes(yesId);
			 }
		 }

	/**
	 * {@link #noId} связан с реализацией метода {@link DialogDecision#sayNo(int)}, указывает какому именно действию было
	 * сказано {@code Нет}
	 */
	 class NoListener implements DialogInterface.OnClickListener
		{
		 @Override
		 public void onClick(DialogInterface dialog,int which)
			{
			 parent.sayNo(noId);
			 }
		 }

	/**
	 * После инициализаций сразу вызывается {@link #action()}. Создание объекта приводит сразу к запуску диалога
	 */
	 public ConfirmDialog(AppCompatActivity _activity,DialogDecision _parent,int _yesId)
		{
		 activity=_activity;
		 parent=_parent;
		 yesId=_yesId;
		 action();
		 }

	/**
	 * Суть та же что и в {@link #ConfirmDialog(AppCompatActivity, DialogDecision, int)}
	 * @param _noId единственное отличие
	 */
	 public ConfirmDialog(AppCompatActivity _activity,DialogDecision _parent,int _yesId,int _noId)
		{
		 this(_activity,_parent,_yesId);
		 noId=_noId;
		 action();
		 }

	/**
	 * Конструирование и запуск нового {@link ActionDialog}
	 */
	 void action()
		{
		 ActionDialog dialog= new ActionDialog();
		 dialog.viceConstructor("Ты точно уверен?","Конечно!","Не надо",new YesListener(),new NoListener() );
		 dialog.show(activity.getSupportFragmentManager(),"");
		 }
	 }