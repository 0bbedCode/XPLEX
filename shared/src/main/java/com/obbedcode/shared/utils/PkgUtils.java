package com.obbedcode.shared.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;

import androidx.annotation.RequiresApi;

import com.obbedcode.shared.logger.XLog;

public class PkgUtils {
    private static final String TAG = "ObbedCode.XP.PkgUtils";

    public static int getPackageUidCompat(IPackageManager pms, String packageName, long flags, int userId) {
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return pms.getPackageUid(packageName, flags, userId);
            else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.N) return pms.getPackageUid(packageName, (int)flags, userId);
            else return pms.getPackageUid(packageName, userId);
        }catch (RemoteException re) {
            XLog.e(TAG, re);
            return 0;
        }
    }

    public static int getPackageUid(Context context, String packageName) { return getPackageUid(context, packageName, 0); }
    public static int getPackageUid(Context context, String packageName, int flags) {
        try {
            PackageManager pkgMgr = context.getPackageManager();
            ApplicationInfo appInfo = pkgMgr.getApplicationInfo(packageName, flags);
            return appInfo.uid;
        }catch (Exception e) {
            XLog.e(TAG, "[getPackageUid] Error: " + e);
            return -1;
        }
    }

    public static PackageInfo getPackageInfoCompat(IPackageManager pms, String packageName, long flags, int userId) {
        try {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                    pms.getPackageInfo(packageName, flags, userId) :
                    pms.getPackageInfo(packageName, (int)flags, userId);
        }catch (RemoteException re) {
            XLog.e(TAG, re);
            return null;
        }
    }

}
