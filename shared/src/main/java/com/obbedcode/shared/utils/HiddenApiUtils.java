package com.obbedcode.shared.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
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

    public static CharSequence getApplicationLabel(String packageName) {
        try {
            XLog.i(TAG, "Getting Label for Package Name: " + packageName);
            // Get IPackageManager instance
            IPackageManager packageManager = (IPackageManager) getIPackageManager();

            XLog.i(TAG, "Got Package Manager: " + packageName);
            // Reflectively get the getApplicationLabel method
            Method getApplicationLabelMethod = packageManager.getClass()
                    .getMethod("getApplicationLabel", ApplicationInfo.class);

            // Obtain ApplicationInfo for the package
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0, 0);

            // Invoke the getApplicationLabel method with ApplicationInfo
            CharSequence label = (CharSequence) getApplicationLabelMethod.invoke(packageManager, appInfo);
            return label;
        } catch (Exception e) {
            // Handle exceptions
            XLog.e(TAG, "Error invoking getApplicationLabel: " + e.getMessage());
            return null;
        }
    }

    public static Object getIPackageManager() {
        try {
            // Get the binder for the package service
            IBinder binder = (IBinder) Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class)
                    .invoke(null, "package");

            // Get the IPackageManager interface using reflection
            Class<?> stubClass = Class.forName("android.content.pm.IPackageManager$Stub");
            Method asInterface = stubClass.getMethod("asInterface", IBinder.class);
            // Invoke the asInterface method to get the IPackageManager instance
            return asInterface.invoke(null, binder);
        } catch (Exception e) {
            // Handle exceptions
            XLog.e(TAG, "Error getting Package Manager Interface via reflection: " + e.getMessage());
            return null;
        }
    }

    public static Object getIActivityManager() {
        try {
            ServiceUtils.waitSystemService(Context.ACTIVITY_SERVICE);
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
