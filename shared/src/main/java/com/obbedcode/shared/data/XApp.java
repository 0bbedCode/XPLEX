package com.obbedcode.shared.data;

import android.app.ActivityThread;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.obbedcode.shared.logger.XLog;

public class XApp implements Parcelable {
    private static final String TAG = "ObbedCode.XP.XApp";

    private int mUid;
    private int mIcon;
    private boolean mIsSystem;
    private boolean mIsEnabled;
    private boolean mIsPersistent;
    private String mAppName;
    private String mPackageName;

    //data/user/0 would be default, profiles would be in a different sub directory
    public XApp(Parcel in) {
        mUid = in.readInt();
        mIcon = in.readInt();
        mIsSystem = in.readByte() != 0;
        mIsEnabled = in.readByte() != 0;
        mIsPersistent = in.readByte() != 0;
        mAppName = in.readString();
        mPackageName = in.readString();
    }

    public XApp(ApplicationInfo app, IPackageManager pm) {
        //ActivityThread.currentActivityThread().getApplication().getPackageManager().getApplicationLabel()

        //pm.getPackageInfo("", 0, 0).lab

        this.mUid = app.uid;
        this.mIcon = app.icon;
        //this.mIsSystem = ApplicationApi.isSystemApplication(app);
        this.mIsSystem = false;
        this.mIsEnabled = true;
        this.mIsPersistent = true;
        //this.mIsEnabled = ApplicationApi.isEnabled(app, pm);
        //this.mIsPersistent = ApplicationApi.isPersistent(app);


        //this.mAppName = app.loadLabel(new PackageItemInfo.DisplayNameComparator(pm)).toString();
        //this.mAppName = (String)pm.getApplicationLabel(packageInfo.applicationInfo).toString()
        //this.mAppName = HiddenApiUtils.getApplicationLabel(app.packageName).toString();
        XLog.i(TAG, "APP NAME=" + ActivityThread.currentActivityThread().getApplication().getPackageManager().getApplicationLabel(app));
        this.mPackageName = app.packageName;
    }

    public int getUid() { return mUid; }
    public int getIcon() { return mIcon; }
    public boolean isSystem() { return mIsSystem; }
    public boolean isEnabled() { return mIsEnabled; }
    public boolean isPersistent() { return mIsPersistent; }
    public String getAppName() { return mAppName; }
    public String getPackageName() { return mPackageName; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mUid);
        dest.writeInt(mIcon);
        dest.writeByte((byte) (mIsSystem ? 1 : 0));
        dest.writeByte((byte) (mIsEnabled ? 1 : 0));
        dest.writeByte((byte) (mIsPersistent ? 1 : 0));
        dest.writeString(mAppName);
        dest.writeString(mPackageName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<XApp> CREATOR = new Creator<XApp>() {
        @Override
        public XApp createFromParcel(Parcel in) {
            return new XApp(in);
        }

        @Override
        public XApp[] newArray(int size) {
            return new XApp[size];
        }
    };
}
