package com.obbedcode.xplex.hook;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import com.obbedcode.shared.BuildConfig;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.HiddenApiUtils;
import com.obbedcode.xplex.XplexApplication;
import com.obbedcode.xplex.root.RootManager;

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
        }
    }
}
