package com.obbedcode.shared.xplex;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;

import com.obbedcode.shared.BuildConfig;

import java.lang.reflect.Method;

public class XUtil {
    private static final String TAG = "ObbedCode.XP.XUtil";
    private static final int PER_USER_RANGE = 100000;

    public static void setPermissions(String path, int mode, int uid, int gid) {
        try {
            Class<?> fileUtils = Class.forName("android.os.FileUtils");
            Method setPermissions = fileUtils
                    .getMethod("setPermissions", String.class, int.class, int.class, int.class);
            setPermissions.invoke(null, path, mode, uid, gid);
        } catch (Throwable ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
        }
    }

    public static int getAppId(int uid) {
        try {
            // public static final int getAppId(int uid)
            Method method = UserHandle.class.getDeclaredMethod("getAppId", int.class);
            return (int) method.invoke(null, uid);
        } catch (Throwable ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
            return uid % PER_USER_RANGE;
        }
    }

    public static int getUserId(int uid) {
        try {
            // public static final int getUserId(int uid)
            Method method = UserHandle.class.getDeclaredMethod("getUserId", int.class);
            return (int) method.invoke(null, uid);
        } catch (Throwable ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
            return uid / PER_USER_RANGE;
        }
    }

    public static int getUserUid(int userid, int appid) {
        try {
            // public static int getUid(@UserIdInt int userId, @AppIdInt int appId)
            Method method = UserHandle.class.getDeclaredMethod("getUid", int.class, int.class);
            return (int) method.invoke(null, userid, appid);
        } catch (Throwable ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
            return userid * PER_USER_RANGE + (appid % PER_USER_RANGE);
        }
    }

}
