package fnote.snayper.com.filmsnote.p1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snayper on 29.02.2016.
 */
public class MainListFragmentParams implements Parcelable
	{
	 int listElementLayout;
	 int contentType;

	 public static final Parcelable.Creator CREATOR = new Creator();

	 static class Creator implements Parcelable.Creator
		{
		 public MainListFragmentParams createFromParcel(Parcel in)
			{
			 return new MainListFragmentParams(in);
			 }
		 @Override
		 public MainListFragmentParams[] newArray(int size)
			{
			 return new MainListFragmentParams[0];
			 }
		 }

	 MainListFragmentParams(Parcel in)
		{
		 listElementLayout= in.readInt();
		 contentType= in.readInt();
		 }
	 MainListFragmentParams(int _listElementLayout,int _contentType)
		{
		 listElementLayout=_listElementLayout;
		 contentType=_contentType;
		 }
	 @Override
	 public int describeContents()
		{
		 return 0;
		 }
	 @Override
	 public void writeToParcel(Parcel dest,int flags)
		{
		 dest.writeInt(listElementLayout);
		 dest.writeInt(contentType);
		 }
	 }
