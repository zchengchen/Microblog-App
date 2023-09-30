package com.example.thweibo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class MyBlog implements Parcelable {

    // Id of microBlog for every
    private static final String TAG = "MyBlog";
    private int mBlogID;
    private String mUsername;
    private String mDatetime;
    private String mContent;
    private String mImgPath;

    public String getImgPath() {
        return mImgPath;
    }

    public String toString() {
        String s = "";
        s += "BlogId: " + String.valueOf(mUsername);
        s += "\nName: " + mBlogID;
        s += "\nImg: " + String.valueOf(mImgPath.length());
        return s + "\n";
    }

    public void setImgPath(String imgPath) {
        if(imgPath == null) {
            this.mImgPath = "";
            return ;
        }
        this.mImgPath = imgPath;
    }

    public MyBlog() { }

    public int getWeiboId() {
        return mBlogID;
    }

    public void setWeiboId(int weiboId) {
        this.mBlogID = weiboId;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String MblogID) {
        this.mUsername = MblogID;
    }

    public String getDatetime() {
        return mDatetime;
    }

    public void setDatetime(String datetime) {
        this.mDatetime = datetime;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mBlogID);
        parcel.writeString(this.mUsername);
        parcel.writeString(this.mDatetime);
        parcel.writeString(this.mContent);
        parcel.writeString(this.mImgPath);
    }

    protected MyBlog(Parcel parcel) {
        this.mBlogID = parcel.readInt();
        this.mUsername = parcel.readString();
        this.mDatetime = parcel.readString();
        this.mContent = parcel.readString();
        this.mImgPath = parcel.readString();
    }

    public static final Parcelable.Creator<MyBlog> CREATOR = new Parcelable.Creator<MyBlog>() {

        @Override
        public MyBlog createFromParcel(Parcel parcel) {
            return new MyBlog(parcel);
        }

        @Override
        public MyBlog[] newArray(int size) {
            return new MyBlog[size];
        }
    };
}
