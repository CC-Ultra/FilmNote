package fnote.snayper.com.filmsnote.p1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snayper on 28.02.2016.
 */
public class ActionDialogParams implements Parcelable
	{
	 AdapterInterface parent;
	 int contentType;
	 int position;
	 int buttonsNum;
	 String leftText,rightText, centralText="";
	 int leftListener,rightListener, centralListener=0;

	 public static final Parcelable.Creator CREATOR = new Creator();

	 static class Creator implements Parcelable.Creator
		{
		 public ActionDialogParams createFromParcel(Parcel in)
			{
			 return new ActionDialogParams(in);
			 }
		 @Override
		 public ActionDialogParams[] newArray(int size)
			{
			 return new ActionDialogParams[0];
			 }
		 }

	 ActionDialogParams(Parcel in)
		{
		 parent= (AdapterInterface)in.readValue(AdapterInterface.class.getClassLoader() );
		 contentType= in.readInt();
		 position= in.readInt();
		 buttonsNum= in.readInt();
		 leftListener= in.readInt();
		 rightListener= in.readInt();
		 centralListener= in.readInt();
		 leftText= in.readString();
		 rightText= in.readString();
		 centralText= in.readString();
		 }
	 ActionDialogParams(AdapterInterface _parent,int _contentType,int _position,String _leftText,String _rightText,int _leftListener,int _rightListener)
		{
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 buttonsNum=2;
		 leftListener=_leftListener;
		 rightListener=_rightListener;
		 leftText=_leftText;
		 rightText=_rightText;
		 }
	 ActionDialogParams(AdapterInterface _parent,int _contentType,int _position,String _leftText,String _rightText,String _centralText,int _leftListener,int _rightListener,int _centralListener)
		{
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 buttonsNum=3;
		 leftListener=_leftListener;
		 rightListener=_rightListener;
		 centralListener=_centralListener;
		 leftText=_leftText;
		 rightText=_rightText;
		 centralText=_centralText;
		 }
	 @Override
	 public int describeContents()
		 {
		 return 0;
		 }
	 @Override
	 public void writeToParcel(Parcel dest,int flags)
		{
		 dest.writeValue(parent);
		 dest.writeInt(contentType);
		 dest.writeInt(position);
		 dest.writeInt(buttonsNum);
		 dest.writeInt(leftListener);
		 dest.writeInt(rightListener);
		 dest.writeInt(centralListener);
		 dest.writeString(leftText);
		 dest.writeString(rightText);
		 dest.writeString(centralText);
		 }
	 }
