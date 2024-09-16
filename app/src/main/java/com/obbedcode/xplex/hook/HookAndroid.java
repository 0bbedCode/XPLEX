package com.obbedcode.xplex.hook;

import android.annotation.SuppressLint;
import android.app.IUidObserver;
import android.content.pm.IPackageManager;
import android.os.RemoteCallbackList;

import com.obbedcode.shared.hook.HookManager;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.RuntimeUtils;
import com.obbedcode.xplex.service.UserService;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookAndroid {
    private static final String TAG = "ObbedCode.XP.HookAndroid";

    private static boolean hasFoundPackageService = false;
    private static final HookManager manager = new HookManager();

    public static void deployHook(final XC_LoadPackage.LoadPackageParam lpparam) {
        if("android".equalsIgnoreCase(lpparam.packageName)) {
            XLog.i(TAG, "Found (SYSTEM_SERVER) " + lpparam.packageName, true);
            try {
                @SuppressLint("PrivateApi")
                Class<?> clazzSM = Class.forName("android.os.ServiceManager", false, lpparam.classLoader);
                manager.hookAllMethods(clazzSM, "addService", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            String service = (String)param.args[0];
                            XLog.i(TAG, "Service: " + service, true);
                            if("package".equals(service)) {
                                if(hasFoundPackageService)
                                    return;

                                IPackageManager pms = (IPackageManager)param.args[1];
                                if(pms == null) {
                                    XLog.e(TAG, "Casting Param[1] to IPackageManager Interface failed", true, true);
                                    return;
                                }

                                hasFoundPackageService = true;
                                manager.unHook("addService");
                                XLog.i(TAG, "Found [package] pms Service now creating our Service ?", true);
                                XLog.i(TAG, "All hooks are now unhooked from [addService]", true);
                                new Thread(() -> {
                                    try {
                                        XLog.i(TAG, "Starting XPL-EX Thread for Service...", true);
                                        UserService.register(pms);
                                        XLog.i(TAG, "Starting XPL-EX Service from PMS", true);
                                    }catch (Exception e) {
                                        XLog.e(TAG, "Failed to Start XPL-EX Service: " + e.getMessage(), true, true);
                                    }
                                }).start();
                            }
                        }catch (Exception innerServiceManager) {
                            XLog.e(TAG, innerServiceManager, true);
                        } finally {
                            super.beforeHookedMethod(param);
                        }
                    }
                });
            }catch (Exception eServiceManager) {
                XLog.e(TAG, eServiceManager);
            }
        }
    }
}
