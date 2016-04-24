package com.snayper.filmsnote.Interfaces;

import com.snayper.filmsnote.Activities.EditActivity;
import com.snayper.filmsnote.Activities.MainActivity;
import com.snayper.filmsnote.Fragments.ActionDialog;
import com.snayper.filmsnote.Utils.ConfirmDialog;

/**
 * <p>Callback для активностей и фрагментов, запускающих {@link ConfirmDialog}</p>
 * {@link ConfirmDialog} это более легковесная и удобная версия {@link ActionDialog}, реализованная средствами последнего.
 * Чтобы он мог изменять нестатические элементы активностей, нужно передать их в него через этот интерфейс, чтобы {@link ConfirmDialog}
 * мог вызвать действие в зависимоти от решения. Требуется описать 2 случая: нажатие на положительный и отрицательный ответ.
 * Чтобы можно было вызвать диалог в нескольких разных случаях (подразумевая разные действия), добавлены параметры {@code int yesId}
 * и {@code int noId}, обрабатывать которые рекомендуется в {@code switch}. Если по нажатию на кнопку никакого действия не
 * требуется, соответствующий метод оставляется пустым
 * <p><sub>(30.03.2016)</sub></p>
 * @author CC-Ultra
 * @see ConfirmDialog
 * @see ActionDialog
 * @see MainActivity
 * @see EditActivity
 */
public interface DialogDecision
	{
	 void sayNo(int noId);
	 void sayYes(int yesId);
	 }
