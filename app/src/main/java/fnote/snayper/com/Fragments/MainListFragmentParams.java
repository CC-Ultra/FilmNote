package fnote.snayper.com.Fragments;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snayper on 29.02.2016.
 */
public class MainListFragmentParams implements Parcelable
	{
	 public int listElementLayout;
	 public int contentType;

	 public static final Parcelable.Creator CREATOR = new Creator();

	 private static class Creator implements Parcelable.Creator
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

	 public MainListFragmentParams(Parcel in)
		{
		 listElementLayout= in.readInt();
		 contentType= in.readInt();
		 }
	 public MainListFragmentParams(int _listElementLayout,int _contentType)
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
