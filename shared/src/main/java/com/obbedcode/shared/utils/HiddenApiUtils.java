package com.obbedcode.shared.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Log;

import com.obbedcode.shared.BuildRuntime;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.ReflectUtil;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HiddenApiUtils {
    private static final String TAG = "ObbedCode.XP.HiddenApiUtils";

    private static boolean hiddenApiBypassed = false;

    @SuppressLint("NewApi")
    public static boolean bypassHiddenApiRestrictions() {
        if(hiddenApiBypassed) return true;
        if(BuildRuntime.isPieApi28Android9(true)) {
            try {
                Log.i(TAG, "Bypassing Hidden API Restrictions using Method (1)");
                Method forName = Class.class.getDeclaredMethod("forName", String.class);
                Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

                Class vmRuntimeClass = (Class) forName.invoke(null, "dalvik.system.VMRuntime");
                Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
                Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[] {String[].class});

                Object vmRuntime = getRuntime.invoke(null);
                setHiddenApiExemptions.invoke(vmRuntime, new String[][]{new String[]{"L"}});
                hiddenApiBypassed = true;
                Log.i(TAG, "Hidden API Restrictions Bypassed using Method (1)");
            }catch (Exception e) {
                Log.e(TAG, "Hidden API Restrictions bypass (1) Failed: " + e.getMessage());
                try {
                    Log.i(TAG, "Bypassing Hidden API Restrictions using Method (2)");
                    hiddenApiBypassed = HiddenApiBypass.setHiddenApiExemptions("L");
                    Log.i(TAG, "Hidden API Restrictions Bypassed using Method (2) ? " + hiddenApiBypassed);
                }catch (Exception ee) {
                    Log.e(TAG, "Hidden API Restrictions bypass (2) Failed: " + ee.getMessage());
                }
            }
        } else {
            hiddenApiBypassed = true;
            Log.i(TAG, "Android Version Appears to be Lower than Android (9) SDK API Level (28) Code Name Pie, not bypassing as anything lower than that does not implement Hidden Api Restrictions.");
        } return hiddenApiBypassed;
    }


    public static Method getRunningProcessesMethod(Object am) { return ReflectUtil.tryGetMethod(am.getClass(), "getRunningAppProcesses"); }
    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(Object am, Method mth) {
        List<ActivityManager.RunningAppProcessInfo> procs = new ArrayList<>();
        try {
            if(am == null || mth == null) {
                am = getIActivityManager();
                mth = getRunningProcessesMethod(am);
                if(mth == null)
                    return procs;

            } return (List<ActivityManager.RunningAppProcessInfo>) mth.invoke(am);
        }catch (Exception e) {
            XLog.e(TAG, "Error Invoking getRunningAppProcesses... " + e.getMessage());
            return procs;
        }
    }

    public static Object getIActivityManager() {
        try {
            IBinder binder = ServiceManager.getService(Context.ACTIVITY_SERVICE);
            Method asInterface = ReflectUtil.tryGetMethod("android.app.ActivityManagerNative", "asInterface", IBinder.class);
            return asInterface.invoke(null, binder);
        }catch (Exception e) {
            XLog.e(TAG, "Error getting Activity Manager Interface reflection: " + e.getMessage());
            return null;
        }
    }



    /*public static List<ActivityManager.RunningAppProcessInfo> getRunningProcesses()  {
        ActivityManagerNative.asInterface()


        IBinder amBinder = ServiceManager.getService("activity");
        //if (amBinder == null) {
        //    throw new IllegalStateException("ActivityManager service not found");
        //}
        Class<?> amClass = Class.forName("android.app.ActivityManagerNative");
        Method getDefaultMethod = amClass.getMethod("asInterface", IBinder.class);
        Object am = getDefaultMethod.invoke(null, amBinder);

        Method getRunningAppProcessesMethod = am.getClass().getMethod("getRunningAppProcesses");
        return (List<ActivityManager.RunningAppProcessInfo>) getRunningAppProcessesMethod.invoke(am);
    }*/
}
