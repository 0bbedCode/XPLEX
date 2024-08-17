package com.obbedcode.shared.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;

import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rikka.hidden.compat.ActivityManagerApis;
import rikka.hidden.compat.PackageManagerApis;

public class ApplicationApi {
    private static final String TAG = "ObbedCode.XP.ApplicationApi";

    public static boolean isPersistent(ApplicationInfo ai) { return isPersistent(ai.flags, ai.packageName); }
    public static boolean isPersistent(int flags, String packageName) {
        return ((flags & ApplicationInfo.FLAG_PERSISTENT) != 0 ||
                "android".equals(packageName));
    }

    public static int getEnabledSetting(PackageManager pm, ApplicationInfo ai) { return getEnabledSetting(pm, ai.packageName); }
    public static int getEnabledSetting(PackageManager pm, String packageName) { return pm.getApplicationEnabledSetting(packageName); }

    public static boolean isEnabled(ApplicationInfo ai, PackageManager pm) { return isEnabled(ai, getEnabledSetting(pm, ai)); }
    public static boolean isEnabled(ApplicationInfo ai, int enabledSetting) {
        return (ai.enabled &&
                (enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT ||
                        enabledSetting == PackageManager.COMPONENT_ENABLED_STATE_ENABLED));
    }

    public static boolean isSystemApplication(ApplicationInfo appInfo) { return isSystemApplication(appInfo.flags); }
    public static boolean isSystemApplication(int flags) { return (flags & (ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0; }




    /*public static List<XApp> getApplicationsEx(Context context) {
        List<ApplicationInfo> apps = getApplications(context);
        List<XApp> xApps = new ArrayList<>(apps.size());
        try {
             PackageManager pm = context.getPackageManager();
             for(ApplicationInfo ai : apps) {
                 xApps.add(new XApp(ai, pm));
             }
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Convert Applications into XApps, Error: " + e.getMessage(), true);
        } return xApps;
    }*/

    //public static List<ApplicationInfo> getApplications(Context context) {
    //    if(context == null) {
    //        PackageManagerApis.getInstalledPackages(0, )
    //    }
    //}


    /*public static List<ApplicationInfo> getApplications(Context context) {
        XLog.i(TAG, "zer0def for TaiChi Support");
        List<String> expApps = XposedApi.getExpApps(context);
        List<ApplicationInfo> apps = new ArrayList<>();
        try {
            PackageManager pkgManager = context.getPackageManager();
            if(expApps.isEmpty())
                return pkgManager.getInstalledApplications(0);
            else {
                for(String expApp : expApps) {
                    try {
                        apps.add(pkgManager.getApplicationInfo(expApp, 0));
                    }catch (Exception innerE) {
                        XLog.e(TAG, "Failed to get Virtual Xposed App: " + expApp + " Error: " + innerE.getMessage(), true);
                    }
                }
            }
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Get a list of Installed Application, Error: " + e.getMessage(), true);
        } return apps;
    }*/
}
