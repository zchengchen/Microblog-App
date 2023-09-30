package com.example.thweibo;

import android.os.Parcel;
import android.os.Parcelable;

public class MyDM  implements Parcelable {
    private String mFromUsername;
    private String mTitle;
    private String mContent;
    private int mId;

    public MyDM() { }

    public String getFromUsername() {
        return mFromUsername;
    }

    public void setFromUsername(String fromUsername) {
        mFromUsername = fromUsername;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mFromUsername);
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mContent);
        parcel.writeInt(this.mId);
    }

    protected MyDM(Parcel parcel) {
        this.mFromUsername = parcel.readString();
        this.mTitle = parcel.readString();
        this.mContent = parcel.readString();
        this.mId = parcel.readInt();
    }

    public static final Parcelable.Creator<MyDM> CREATOR = new Parcelable.Creator<MyDM>() {

        @Override
        public MyDM createFromParcel(Parcel parcel) {
            return new MyDM(parcel);
        }

        @Override
        public MyDM[] newArray(int size) {
            return new MyDM[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}