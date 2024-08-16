package com.obbedcode.xplex.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManagerHidden;
import android.content.AttributionSource;
import android.content.Context;
import android.content.IContentProvider;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import androidx.annotation.RequiresApi;

import com.obbedcode.shared.Constants;
import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.process.UidProcessObserver;
import com.obbedcode.shared.reflect.ReflectUtil;
import com.obbedcode.shared.utils.PkgUtils;
import com.obbedcode.shared.utils.ServiceUtils;

import rikka.hidden.compat.ActivityManagerApis;
import rikka.hidden.compat.adapter.UidObserverAdapter;


public class UserService {
    private static final String TAG = "ObbedCode.XP.UserService";

    private static int appUid = 0;

    public static void register(IPackageManager pms) {
        XLog.i(TAG, "Initialize XplexService ", true);
        //XplexService serv = new XplexService();
        appUid = PkgUtils.getPackageUidCompat(pms, Constants.APP_PACKAGE_NAME, 0, 0);
        XLog.i(TAG, "XPL-EX Application Uid: " + appUid, true);

        //PackageInfo pkgInfo = PkgUtils.getPackageInfoCompat(pms, Constants.APP_PACKAGE_NAME, 0, 0);
        //Verify Signature
        //Just return if signature does not match
        //ServiceUtils.waitSystemService(Context.ACTIVITY_SERVICE);
        //XLog.i(TAG, "Found the Activity Service", true);
        /*new UidProcessObserver(appUid)
                .useOldMethod(false)
                .useUidObserver(true)
                .setOnNotifyEvent((uid) -> {
                    //ReflectUtil.getClassForName("FuckTest", true).getDeclaredMethod("cool", int.class).invoke(null, "ddd");
                    XLog.i(TAG, "UID EVENT INVOKED: " + uid,true);
                    if(uid != appUid) return;
                    try {
                        XLog.i(TAG, "UID Handler is invoking UID::[" + uid + "]", true);
                        IContentProvider provider = ActivityManagerApis.getContentProviderExternal(Constants.PROVIDER_AUTHORITY, 0, null, null);
                        if(provider == null) {
                            XLog.e(TAG, "Content Provider for [" + Constants.PROVIDER_AUTHORITY + "] Is Null...", true, true);
                            return;
                        }

                        XLog.i(TAG, "Creating Bundle for Service: " + uid, true);
                        Bundle extras = new Bundle();
                        extras.putBinder("binder", XplexService.instance);
                        XLog.i(TAG, "Instance was written to the Binder XplexService", true);

                        Bundle reply = null;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            AttributionSource attr = new AttributionSource.Builder(1000).setPackageName(Constants.SYSTEM_SERVER).build();
                            reply = provider.call(attr, Constants.PROVIDER_AUTHORITY, Str.EMPTY, null, extras);
                        }
                        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R) reply = provider.call(Constants.SYSTEM_SERVER, null, Constants.PROVIDER_AUTHORITY, Str.EMPTY, null, extras);
                        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) reply = provider.call(Constants.SYSTEM_SERVER, Constants.PROVIDER_AUTHORITY, Str.EMPTY, null, extras);
                        else reply = provider.call(Constants.SYSTEM_SERVER, Str.EMPTY, null, extras);
                        if(reply == null) {
                            XLog.e(TAG, "Failed to send Binder to App, [" + Constants.PROVIDER_AUTHORITY + "]", true, true);
                            return;
                        }

                        XLog.i(TAG, "Sent Binder to App,  [" + Constants.PROVIDER_AUTHORITY + "]", true);
                    }catch (Exception e) {
                        XLog.e(TAG, "UidObserver Error: " + e.getMessage(), true, true);
                    }
                }).startMonitorOnActive(false);*/
    }
}
