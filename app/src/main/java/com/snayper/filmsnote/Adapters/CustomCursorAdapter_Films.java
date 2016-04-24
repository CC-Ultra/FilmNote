package com.snayper.filmsnote.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.snayper.filmsnote.Activities.GlobalMenuOptions;
import com.snayper.filmsnote.Fragments.MainListFragment;
import com.snayper.filmsnote.R;
import com.snayper.filmsnote.Utils.DateUtil;
import com.snayper.filmsnote.Utils.O;
import java.util.Date;

/**
 * <p>Адаптер для {@link MainListFragment}</p>
 * Кроме заполнения даными текстовых полей, адаптер инициализируется цветами согласно теме, красит все текстовые поля и
 * в зависимости от значения {@code watched} у записи, ставит одну из картинов на статус фильма.
 * <p><sub>(19.02.2016)</sub></p>
 * @author CC-Ultra
 */
public class CustomCursorAdapter_Films extends SimpleCursorAdapter
	{
	 private int drawableRes,textColor;
	 private Context context;

	/**
	 * Кромe {@code super()} здесь еще идет инициализация цветов и ресурсов согласно текущей теме.
	 * {@code @SuppressWarnings("deprecation")} нужен, чтобы пользоваться {@link Resources#getColor(int)}
	 */
	 @SuppressWarnings("deprecation")
	 public CustomCursorAdapter_Films(Context _context,int layout,Cursor c,String[] from,int[] to)
		{
		 super(_context,layout,c,from,to,0);
		 context=_context;
		 Resources resources= context.getResources();
		 switch(GlobalMenuOptions.themeSwitcher)
			{
			 case O.prefs.THEME_ID_MENTOR:
				 textColor= resources.getColor(R.color.list_text_mentor);
				 drawableRes= R.drawable.list_selector_mentor;
				 break;
			 case O.prefs.THEME_ID_ULTRA:
				 textColor= resources.getColor(R.color.list_text_ultra);
				 drawableRes= R.drawable.list_selector_ultra;
				 break;
			 case O.prefs.THEME_ID_COW:
				 textColor= resources.getColor(R.color.list_text_cow);
				 drawableRes= R.drawable.list_selector_cow;
				 break;
			 default:
				 textColor= resources.getColor(R.color.list_text_mentor);
				 drawableRes= R.drawable.list_selector_mentor;
			 }
		 }

	/**
	 * Метод, который возвращает сложные элементы списка. Работа идет на базе {@code convertView}, который может оказаться
	 * и пустым, и тогда придется его делать самостоятельно через {@link LayoutInflater#inflate}. Когда View для заполнения
	 * получена, ставлю на фон соответствующий селектор, получаю все TextView и всем ставлю цвет. Получаю курсор, вытаскиваю
	 * из него всю информацию по записи, проставляю везде этот текст и в зависимости от флага {@code watched} ставится
	 * картинка статуса фильма
	 */
	 @Override
	 public View getView(int position,View convertView,ViewGroup parent)
		{
		 if(convertView==null)
			{
			 LayoutInflater inflater;
			 inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView= inflater.inflate(R.layout.main_list_element_film, parent, false);
			 }
		 convertView.setBackgroundResource(drawableRes);
		 TextView titleTxt= (TextView)convertView.findViewById(R.id.title);
		 TextView dateTxt= (TextView)convertView.findViewById(R.id.lastDate);
		 ImageView img= (ImageView)convertView.findViewById(R.id.watchedStatusImage);

		 Cursor cursor= getCursor();
		 cursor.moveToPosition(position);
		 String title= cursor.getString( cursor.getColumnIndex(O.db.FIELD_NAME_TITLE) );
		 Date date= new Date(cursor.getLong(cursor.getColumnIndex(O.db.FIELD_NAME_DATE) ) );
		 if(date.getTime()==0)
			 date=null;
		 boolean watched= cursor.getInt(cursor.getColumnIndex(O.db.FIELD_NAME_FILM_WATCHED) ) == 1;

		 titleTxt.setTextColor(textColor);
		 dateTxt.setTextColor(textColor);
		 titleTxt.setText(title);
		 dateTxt.setText(DateUtil.dateToString(date) );
		 if(watched)
			 img.setImageResource(R.drawable.list_watched);
		 else
			 img.setImageResource(R.drawable.list_not_watched);
		 return convertView;
		 }
	 }
