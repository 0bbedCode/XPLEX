package com.obbedcode.shared.process;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class XTask implements Parcelable {
    private boolean mIsService;
    private String mName;
    private String mSource;         //Usually being path
    private int mUid;
    private int mPid;
    private List<String> mPackages = new ArrayList<>();

    public XTask() { }

    public XTask(Parcel in) {
        this.mIsService = in.readByte() != 0;
        this.mName = in.readString();
        this.mSource = in.readString();
        this.mUid = in.readInt();
        //this.mPackages = in.readArr
    }

    public static final Creator<XTask> CREATOR = new Creator<XTask>() {
        @Override
        public XTask createFromParcel(Parcel in) { return new XTask(in); }
        @Override
        public XTask[] newArray(int size) { return new XTask[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (mIsService ? 1 : 0));
        dest.writeString(mName);
        dest.writeString(mSource);
        dest.writeInt(mUid);
        dest.writeStringList(mPackages);
    }
}
