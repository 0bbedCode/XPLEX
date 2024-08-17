package com.obbedcode.xplex.service;

import android.content.Context;
import android.content.pm.IPackageManager;
import android.os.Bundle;
import android.os.Process;

import com.obbedcode.shared.Constants;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.process.SystemIPC;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.process.UidProcessObserver;
import com.obbedcode.shared.utils.PkgUtils;
import com.obbedcode.shared.utils.ServiceUtils;

import java.util.List;


public class UserService {
    private static final String TAG = "ObbedCode.XP.UserService";

    private static int appUid = 0;

    public static void register(IPackageManager pms) {
        XLog.i(TAG, "Initializing XplexService ", true);
        XplexService.bind(pms);
        //PS We bind here the Package Manager Service so in theory we can cut context from the Interface ??
        appUid = PkgUtils.getPackageUidCompat(pms, Constants.APP_PACKAGE_NAME, 0, 0);
        XLog.i(TAG, "XPL-EX Application Uid: " + appUid, true);
        //PackageInfo pkgInfo = PkgUtils.getPackageInfoCompat(pms, Constants.APP_PACKAGE_NAME, 0, 0);
        //Verify Signature
        //Just return if signature does not match

        ServiceUtils.waitSystemService(Context.ACTIVITY_SERVICE);
        XLog.i(TAG, "Found the Activity Service", true);
        new UidProcessObserver(appUid)
                .useOldMethod(true)
                .setOnNotifyEvent((uid) -> {
                    if(uid != appUid) return;
                    XLog.i(TAG, "UID Observer Found UID: " + uid);
                    try {
                        XLog.i(TAG, "Creating Service Bundle to send to the XPL-EX Client", true);
                        Bundle extras = new Bundle();
                        extras.putBinder("binder", XplexService.instance);
                        XLog.i(TAG, "Created Binder to Send to the Client App", true);
                        Bundle reply = SystemIPC.create()
                                .setUid(Process.SYSTEM_UID)
                                .setCallingPackage(Constants.SYSTEM_SERVER)
                                .setProviderAuthority(Constants.PROVIDER_AUTHORITY)
                                .setExtras(extras)
                                .callToProvider();
                        if(reply == null) {
                            XLog.e(TAG, "Error Calling to Provider: " + Constants.PROVIDER_AUTHORITY + " Failed returned null Reply", true, true);
                            return;
                        }

                        XLog.i(TAG, "Call to Provider Finished without Error: " + Constants .PROVIDER_AUTHORITY, true);
                    }catch (Exception e) {
                        XLog.e(TAG, "UidObserver Error: " + e.getMessage(), true, true);
                    }
                }).startMonitorOnActive(false);
    }
}
