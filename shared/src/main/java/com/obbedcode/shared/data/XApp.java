
package com.obbedcode.shared.data;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerHidden;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import com.obbedcode.shared.api.ApplicationApi;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.PkgUtils;

import java.sql.Time;
import java.time.Clock;

import rikka.hidden.compat.PackageManagerApis;

public class XApp implements Parcelable {
    //private static final String TAG = "ObbedCode.XP.XApp";
    //public static PackageManager PM = ActivityThread.currentActivityThread().getApplication().getPackageManager();

    public ApplicationInfo info;
    public int uid;
    public int icon;
    //public Drawable icon;
    public boolean isSystem;
    public boolean isEnabled;
    public boolean isPersistent;
    public String appName;
    public String packageName;

    public long lastUpdateTime;
    public long firstInstallTime;
    public long size;
    public int targetSdk;

    //data/user/0 would be default, profiles would be in a different sub directory
    public XApp(Parcel in) {
        info = (ApplicationInfo) in.readValue(ApplicationInfo.class.getClassLoader());
        uid = in.readInt();
        icon = in.readInt();
        //icon = (Drawable) in.readValue(Drawable.class.getClassLoader());
        isSystem = in.readByte() != 0;
        isEnabled = in.readByte() != 0;
        isPersistent = in.readByte() != 0;
        appName = in.readString();
        packageName = in.readString();

        lastUpdateTime = in.readLong();
        firstInstallTime = in.readLong();
        size = in.readLong();
        targetSdk = in.readInt();
    }

    public XApp(ApplicationInfo app, IPackageManager pm) {
        PackageInfo pkgInfo = PkgUtils.getPackageInfoCompat(pm, app.packageName, 0, 0);
        if(pkgInfo != null) {
            this.lastUpdateTime = pkgInfo.lastUpdateTime;
            this.firstInstallTime = pkgInfo.firstInstallTime;
        } else {
            this.lastUpdateTime = SystemClock.currentThreadTimeMillis();
            this.firstInstallTime = SystemClock.currentThreadTimeMillis();
        }

        this.info = app;
        this.uid = app.uid;
        this.icon = app.icon;
        this.isSystem = false;
        this.isEnabled = false;
        this.isPersistent = false;
        this.appName = app.packageName;         //We resolve it on the other end as doing so will ensure locale is being used

        this.targetSdk = app.targetSdkVersion;
        this.packageName = app.packageName;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(info);
        dest.writeInt(uid);
        dest.writeInt(icon);
        //dest.writeValue(icon);
        dest.writeByte((byte) (isSystem ? 1 : 0));
        dest.writeByte((byte) (isEnabled ? 1 : 0));
        dest.writeByte((byte) (isPersistent ? 1 : 0));
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeLong(lastUpdateTime);
        dest.writeLong(firstInstallTime);
        dest.writeLong(size);
        dest.writeInt(targetSdk);
    }

    @Override
    public int describeContents() { return 0; }

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
