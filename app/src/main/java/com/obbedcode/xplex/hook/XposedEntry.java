package com.obbedcode.xplex.hook;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import com.obbedcode.shared.BuildConfig;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.XHooker;
import com.obbedcode.shared.xplex.XUtil;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedEntry  implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private static final String TAG = "ObbedCode.XposedEntry";

    public void initZygote(final IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        XLog.hasXposed = true;
        XLog.i(TAG, "Zygote Init, system=" + startupParam.startsSystemServer + " debug=" + BuildConfig.DEBUG, true);
    }

    public static boolean hasSystemHooked = false;

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XLog.hasXposed = true;
        XLog.i(TAG, "Loaded package=" + lpparam.packageName + ":" + Process.myUid(), true);

        if("android".equalsIgnoreCase(lpparam.packageName)) {
            HookAndroid.deployHook(lpparam);
            /*@SuppressLint("PrivateApi") Class<?> clazzAM = Class.forName("com.android.server.am.ActivityManagerService", false, lpparam.classLoader);
            XposedBridge.hookAllMethods(clazzAM, "systemReady",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XLog.i(TAG, "System Is Ready", true);
                            super.beforeHookedMethod(param);
                        }
                    });*/
        } else if(!lpparam.packageName.startsWith("com.obbedcode") && !lpparam.packageName.contains("setting")) {
            XLog.i(TAG, "[Hook] Hooking Package => " + lpparam.packageName);
            hookApplication(lpparam);
        }
    }

    public void hookApplication(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final int uid = android.os.Process.myUid();
        final boolean tiramisuOrHigher = (Build.VERSION.SDK_INT >= 33);
        // https://android.googlesource.com/platform/frameworks/base/+/169aeafb2d97b810ae123ad036d0c58336961c55%5E%21/#F1
        Class<?> at = Class.forName(tiramisuOrHigher ? "android.app.Instrumentation" : "android.app.LoadedApk", false, lpparam.classLoader);

        XposedBridge.hookAllMethods(at,
                tiramisuOrHigher ? "newApplication" : "makeApplication", new XC_MethodHook() {
                    private boolean made = false;

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            if(!made) {
                                made = true;
                                Context context = (Application) param.getResult();

                                //Check for isolate process
                                int userid = XUtil.getUserId(uid);
                                int start = XUtil.getUserUid(userid, 99000);
                                int end = XUtil.getUserUid(userid, 99999);
                                boolean isolated = (uid >= start && uid <= end);
                                if (isolated) {
                                    Log.i(TAG, "Skipping isolated " + lpparam.packageName + ":" + uid);
                                    return;
                                }

                                //hookPackage(lpparam, uid, context);
                                XHooker.hookPackage(lpparam, uid, context);
                            }
                        }catch (Throwable ex) {
                            Log.e(TAG, Log.getStackTraceString(ex));
                            XposedBridge.log(ex);
                        }
                    }
                });
    }
}
