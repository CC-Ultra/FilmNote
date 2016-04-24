package com.snayper.filmsnote.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.snayper.filmsnote.Activities.AddActivity;
import com.snayper.filmsnote.Activities.GlobalMenuOptions;
import com.snayper.filmsnote.Activities.MainActivity;
import com.snayper.filmsnote.Adapters.CustomCursorAdapter_Films;
import com.snayper.filmsnote.Adapters.CustomCursorAdapter_Serial;
import com.snayper.filmsnote.Adapters.TabsFragmentAdapter;
import com.snayper.filmsnote.Db.DbConsumer;
import com.snayper.filmsnote.Db.DbCursorLoader;
import com.snayper.filmsnote.Interfaces.AdapterInterface;
import com.snayper.filmsnote.Parsers.AsyncParser;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.O;

/**
 * <p>Абстрактный класс фрагмента для {@link ViewPager} в {@link MainActivity}</p>
 * Основная логика фрагментов здесь. В потомках только специфические Listener-ы на обычное и длинное нажатие.
 * <p>Фрагменты при создании как правило инициализируются и заносятся в {@link TabsFragmentAdapter#tabs} и выдаются методом
 * {@link TabsFragmentAdapter#getItem} по требованию {@link ViewPager}, но иногда, когда они нужны, он не вызывает этот
 * метод, а производит пустые неинициализированные фрагменты, и причина этому мне совершенно не понятна. Чтобы защититься
 * от последствий работы с неинициализированными фрагментами, было внедрено несколько решений, среди которых Loader-ы.
 * Фрагмент состоит из {@link FloatingActionButton} и {@link ListView}, для которого нужен {@link CustomCursorAdapter_Films}
 * или {@link CustomCursorAdapter_Serial}, ориентированные на разные {@link Cursor} и {@link #listElementLayout}, зависящие
 * от {@link #contentType}. Но т.к. последний может быть не установлен, старт производится по {@code null} курсору и адаптер
 * ничего не делает. Курсор поставляется адаптеру средствами {@link DbCursorLoader}, в реализующих {@link LoaderManager.LoaderCallbacks}
 * методах. Но для его корректной работы требуется все тот же {@code contentType}. Для безопасной работы приложения методы
 * {@link LoaderManager.LoaderCallbacks} и {@link #initAdapter()} были защищены полем {@link #saveExitSumm},
 * которое ненулевое, если хотя-бы один фрагмент инициализирован (а значит, {@link TabsFragmentAdapter#getItem(int)}
 * продолжает работать)</p>
 * <p>Фрагмент реализует интерфейс {@link AdapterInterface}, а это значит, передается в {@link AsyncParser}, из которого
 * производится вызов {@link #initAdapter()}. Также он производится в методе {@link #onResume()}. Итого, список обновляется
 * тремя способами: через callback {@link AdapterInterface}, по возвращению во фрагмент и через {@link #loader}, если изменения
 * в базе произошли без выхода из фрагмента</p>
 * <p>Кроме того, фрагмент окрашивается соответствующими теме цветами</p>
 * <p>Имеется {@link DbConsumer}</p>
 * <p>Массивы {@link #dbListFrom} и {@link #dbListFrom} нужны для работы адаптеров</p>
 * <p><sub>(16.02.2016)</sub></p>
 * @author CC-Ultra
 * @see Fragment_Films
 * @see Fragment_Serial
 * @see DbCursorLoader
 * @see DbConsumer
 * @see ViewPager
 * @see TabsFragmentAdapter
 * @see AsyncParser
 * @see CustomCursorAdapter_Films
 * @see CustomCursorAdapter_Serial
 */
public abstract class MainListFragment extends Fragment implements AdapterInterface,LoaderManager.LoaderCallbacks<Cursor>
	{
	 protected DbConsumer dbConsumer;
	 protected Loader loader;
	 private String title;
	 protected ListView list;
	 protected FloatingActionButton actionButton;
	 protected SimpleCursorAdapter adapter;
	 protected int contentType;
	 protected static int saveExitSumm=0;
	 private int exitDeposit=0;
	 public int fakeContentType;
	 protected int listElementLayout;
	 private int themeResource,actionButtonBackgroundColor,actionButtonImageRes,dividerColor;

	 protected String dbListFrom[]= {O.db.FIELD_NAME_TITLE, O.db.FIELD_NAME_ALL, O.db.FIELD_NAME_WATCHED, O.db.FIELD_NAME_DATE};
	 protected int dbListTo[]= {R.id.title, R.id.newEpisodes, R.id.watchedEpisodes, R.id.lastDate};

	/**
	 * <p>Плавающая кнопка для добавления фильма или сериала</p>
	 * Работа кнопки зависит от {@code contentType}, потому что в случае фильма только один вариант страницы добавления,
	 * а в случая сериала - два. Для выбора на какую переходить порождается {@link ActionDialog}, которому передаются
	 * {@code contentType}, нулевая позиция (которая будет проигнорирована) и указываются требования:
	 * <p>Две кнопки с текстом {@code "Online"} и {@code "Offline"}, на которые будут привязаны {@link ActionDialog.AddOnlineListener}
	 * и {@link ActionDialog.AddOfflineListener}. Все это под заголовком {@code "Выбери способ добавления"}</p>
	 * @see ActionDialog
	 */
	 protected class ActiveButtonListener implements View.OnClickListener
		{
		 @Override
		 public void onClick(View v)
			{
			 if(contentType == O.interaction.CONTENT_FILMS)
				{
				 Intent jumper= new Intent(getActivity(),AddActivity.class);
				 jumper.putExtra(O.mapKeys.extra.CONTENT_TYPE, contentType);
				 startActivity(jumper);
				 }
			 else
				{
				 String txtLeft="Online";
				 String txtRight="Offline";
				 String message="Выбери способ добавления";
				 int position=0;
				 int listenerLeft=O.dialog.LISTENER_ADD_ONLINE;
				 int listenerRight=O.dialog.LISTENER_ADD_OFFLINE;
				 ActionDialog dialog=new ActionDialog();
				 dialog.viceConstructor(MainListFragment.this,contentType,position,message,txtLeft,txtRight,listenerLeft,listenerRight);
				 dialog.show(getActivity().getSupportFragmentManager(),"");
				 }
			 }
		 }

	 protected abstract void setListener_listOnClick();
	 protected abstract void setListener_listOnLongClick();

	/**
	 * Этот метод расширяет привычное получение главного {@code view} фрагмента установкой соответствующей темы. {@code themeResource}
	 * инициализируется в {@link #initLayoutThemeCustoms()}
	 * @param inflater параметр из {@link #onCreateView}
	 * @param container параметр из {@link #onCreateView}
	 * @return главное {@link View} фрагмента
	 * @see #initLayoutThemeCustoms()
	 */
	 protected View initContentView(LayoutInflater inflater,ViewGroup container)
		{
		 initLayoutThemeCustoms();
		 final Context contextThemeWrapper= new ContextThemeWrapper(getActivity(),themeResource);
		 LayoutInflater localInflater= inflater.cloneInContext(contextThemeWrapper);
		 return localInflater.inflate(R.layout.main_list_fragment,container,false);
		 }

	/**
	 * {@link #exitDeposit} - это то, за счет чего {@link #saveExitSumm} остается ненулевой при хотя-бы единственном живом
	 * инициализированном фрагменте. В {@link MainListFragment#onCreateView} каждый фрагмент делает вклад в {@code exitDeposit},
	 * а в {@link MainListFragment#onDestroyView()} забирает обратно. Если фрагмент неинициализирован, то {@code exitDeposit}
	 * нулевой и вклад не влияет на {@code saveExitSumm}, а доступ к методам {@link LoaderManager.LoaderCallbacks} и {@link #initAdapter()}
	 * остается закрыт
	 * <p><s>{@link #fakeContentType} - чисто тестовая переменная</s></p>
	 * @see #onCreateView
	 * @see #onCreateLoader
	 * @see #onLoadFinished
	 * @see #onResume()
	 */
	 public void initFragment(String _title,int _contentType,int _listElementLayout)
		{
		 title=_title;
		 contentType=_contentType;
		 fakeContentType= contentType+4;
		 listElementLayout=_listElementLayout;
		 exitDeposit=1;
//		 Log.d(O.TAG,"initFragment: "+ fakeContentType);
		 }
	 public String getTitle()
		{
		 return title;
		 }

	/**
	 * Команда {@link #loader}-у загрузить обновленную информацию
	 * @see #onLoadFinished
	 */
	 @Override
	 public void initAdapter()
		{
		 loader.forceLoad();
		 }

	/**
	 * Инициализация темы, изображения на {@link #actionButton} и цветов для списка. {@code @SuppressWarnings("deprecation")}
	 * нужен, чтобы пользоваться {@link Resources#getColor(int)}
	 * @see #setLayoutThemeCustoms(View)
	 */
	 @SuppressWarnings("deprecation")
	 protected void initLayoutThemeCustoms()
		{
		 Resources resources= getResources();
		 switch(GlobalMenuOptions.themeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 themeResource= R.style.Theme_Mentor;
				 actionButtonBackgroundColor= resources.getColor(R.color.actionButtonBackground_mentor);
				 actionButtonImageRes= R.mipmap.add_button;
				 dividerColor= resources.getColor(R.color.list_divider_mentor);
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 themeResource= R.style.Theme_Ultra;
				 actionButtonBackgroundColor= resources.getColor(R.color.actionButtonBackground_ultra);
				 actionButtonImageRes= R.mipmap.add_button_ultra;
				 dividerColor= resources.getColor(R.color.list_divider_ultra);
				 break;
			 case O.prefs.THEME_ID_COW:
				 themeResource= R.style.Theme_Cow;
				 actionButtonBackgroundColor= resources.getColor(R.color.actionButtonBackground_cow);
				 actionButtonImageRes= R.mipmap.add_button;
				 dividerColor= resources.getColor(R.color.list_divider_cow);
				 break;
			 default:
				 themeResource= R.style.Theme_Mentor;
				 actionButtonBackgroundColor= resources.getColor(R.color.actionButtonBackground_mentor);
				 actionButtonImageRes= R.mipmap.add_button;
				 dividerColor= resources.getColor(R.color.list_divider_mentor);
			 }
		 }

	/**
	 * Применения цветов списка и изображения для {@link #actionButton}
	 * @param view главная {@link View} фрагмента
	 * @see #initLayoutThemeCustoms()
	 */
	 protected void setLayoutThemeCustoms(View view)
		{
		 FloatingActionButton actionButton= (FloatingActionButton)view.findViewById(R.id.activeButton);
		 actionButton.setBackgroundTintList(ColorStateList.valueOf(actionButtonBackgroundColor));
		 actionButton.setImageResource(actionButtonImageRes);
		 ColorDrawable divcolor= new ColorDrawable(dividerColor);
		 list.setDivider(divcolor);
		 list.setDividerHeight(2);
		 }

	/**
	 * В {@link #initContentView} устанавливается тема после вызова {@link #initLayoutThemeCustoms()}. Потом в {@link #saveExitSumm}
	 * дописывается {@link #exitDeposit}, и если фрагмент инициализированный, нижесоздаваемый {@link #loader}, зарегистрированный
	 * по {@code id=contentType}, будет работать. Создается {@link #dbConsumer} по {@code contentType} и {@code loader},
	 * адаптер с пустым пока курсором, и в зависимости от реализующего класса ({@link Fragment_Films} или {@link Fragment_Serial})
	 * на список назначаются Listener-ы. Инициализация {@code loader} приводит к вызову {@link #onCreateLoader}
	 * @see #initContentView
	 * @see #initFragment
	 * @see #onCreateLoader
	 */
	 @Nullable
	 @Override
	 public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
		{
		 super.onCreateView(inflater,container,savedInstanceState);
//		 Log.d(O.TAG,"onCreateView: "+ fakeContentType);
		 View view= initContentView(inflater,container);

		 list= (ListView)view.findViewById(R.id.list);
		 actionButton= (FloatingActionButton)view.findViewById(R.id.activeButton);

		 saveExitSumm+= exitDeposit;
		 loader= getActivity().getSupportLoaderManager().initLoader(contentType,null,this);
		 dbConsumer= new DbConsumer(getActivity(), getActivity().getContentResolver(),loader,contentType);
		 actionButton.setOnClickListener(new ActiveButtonListener());
		 if(contentType==O.interaction.CONTENT_FILMS)
			 adapter= new CustomCursorAdapter_Films(getActivity(), listElementLayout, null, dbListFrom, dbListTo);
		 else
			 adapter= new CustomCursorAdapter_Serial(getActivity(), listElementLayout, null, dbListFrom, dbListTo);
		 list.setAdapter(adapter);
		 setListener_listOnClick();
		 setListener_listOnLongClick();
		 setLayoutThemeCustoms(view);
		 return view;
		 }

	/**
	 * Новый курсор загружается по команде {@link DbCursorLoader#forceLoad()}, которую можно вызвать, обладая объектом этого
	 * класса, а значит, не уходя из активности, потому что их жизненные циклы связаны. Чтобы загрузить курсор вернувшись из
	 * другой активности, {@link #initAdapter()} вызывается здесь. Доступ к вызову есть при условии, что все фрагменты инициализированы
	 */
	 @Override
	 public void onResume()
		{
		 if(saveExitSumm>0)
			 initAdapter();
		 super.onResume();
		 }
	 @Override
	 public void onDestroyView()
		{
		 saveExitSumm-= exitDeposit;
		 super.onDestroyView();
		 }

	/**
	 *
	 * @param id соответствует {@code contentType}
	 * @return если все фрагменты инициализированы, {@link DbCursorLoader} создается, а иначе возвращается {@code null}
	 */
	@Override
	 public Loader<Cursor> onCreateLoader(int id,Bundle args)
		{
//		 Log.d(O.TAG,"onCreateLoader: "+ fakeContentType);
		 if(saveExitSumm>0)
			 return new DbCursorLoader(getActivity(), getActivity().getContentResolver(), contentType);
		 return null;
		 }

	/**
	 * Доступ к вызову есть при условии, что все фрагменты инициализированы. Подменяет курсор в адаптере на новый, что приводит
	 * к обновлению списка
	 * @param cursor свежезагруженный {@link Cursor}
	 */
	 @Override
	 public void onLoadFinished(Loader<Cursor> loader,Cursor cursor)
		{
//		 Log.d(O.TAG,"onLoadFinished: loader "+ ( (DbCursorLoader)loader).getContentType() +"\tcontentType "+ fakeContentType);
		 if(saveExitSumm>0)
			 adapter.swapCursor(cursor);
		 }
	 @Override
	 public void onLoaderReset(Loader<Cursor> loader) {}
	 }
