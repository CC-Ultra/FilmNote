package fnote.snayper.com.Fragments;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by snayper on 28.02.2016.
 */
public class ActionDialogParams implements Parcelable
	{
	 public AdapterInterface parent;
	 public int contentType;
	 public int position;
	 public String message="";
	 public int buttonsNum=2;
	 public String leftText,rightText, centralText="";
	 public int leftListener,rightListener, centralListener=0;

	 public static final Parcelable.Creator CREATOR = new Creator();

	 private static class Creator implements Parcelable.Creator
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

	 public ActionDialogParams(Parcel in)
		{
		 parent= (AdapterInterface)in.readValue(AdapterInterface.class.getClassLoader() );
		 contentType= in.readInt();
		 position= in.readInt();
		 message=in.readString();
		 buttonsNum= in.readInt();
		 leftListener= in.readInt();
		 rightListener= in.readInt();
		 centralListener= in.readInt();
		 leftText= in.readString();
		 rightText= in.readString();
		 centralText= in.readString();
		 }
	 public ActionDialogParams(AdapterInterface _parent,int _contentType,int _position,String _leftText,String _rightText,int _leftListener,int _rightListener)
		{
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 leftListener=_leftListener;
		 rightListener=_rightListener;
		 leftText=_leftText;
		 rightText=_rightText;
		 }
	 public ActionDialogParams(AdapterInterface _parent,int _contentType,int _position,String _leftText,String _rightText,String _centralText,int _leftListener,int _rightListener,int _centralListener)
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
	 public ActionDialogParams(AdapterInterface _parent,int _contentType,int _position,String _message,String _leftText,String _rightText,int _leftListener,int _rightListener)
		{
		 parent=_parent;
		 contentType=_contentType;
		 position=_position;
		 message=_message;
		 leftListener=_leftListener;
		 rightListener=_rightListener;
		 leftText=_leftText;
		 rightText=_rightText;
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
		 dest.writeString(message);
		 dest.writeInt(buttonsNum);
		 dest.writeInt(leftListener);
		 dest.writeInt(rightListener);
		 dest.writeInt(centralListener);
		 dest.writeString(leftText);
		 dest.writeString(rightText);
		 dest.writeString(centralText);
		 }
	 }
