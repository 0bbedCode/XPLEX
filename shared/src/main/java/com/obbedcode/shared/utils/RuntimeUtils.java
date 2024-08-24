package com.obbedcode.shared.utils;

import android.util.Log;

import com.obbedcode.shared.reflect.DynamicMethod;
import com.obbedcode.shared.reflect.HiddenApiUtils;
import com.obbedcode.shared.reflect.ReflectUtil;

public class RuntimeUtils {
    private static final String TAG = "ObbedCode.XP.RuntimeUtils";

    private static final DynamicMethod nativeFillInStackTrace;
    private static final DynamicMethod nativeGetStackTrace;
    private static final DynamicMethod internalGetStackTrace;
    static {
        HiddenApiUtils.bypassHiddenApiRestrictions();
        nativeFillInStackTrace = new DynamicMethod(Throwable.class, "nativeFillInStackTrace").setAccessible(true);
        nativeGetStackTrace = new DynamicMethod(Throwable.class, "nativeGetStackTrace", Object.class).setAccessible(true);
        internalGetStackTrace = new DynamicMethod(ReflectUtil.tryGetMethodEx(Throwable.class, "getOurStackTrace", "getInternalStackTrace")).setAccessible(true);
    }

    //Try look for IXposedHookLoadPackage ?
    //Or for me com.obbedcode.xplex.hook.XposedEntry
    public static boolean stackTraceContains(String className, String methodName, boolean useOr) {
        StackTraceElement[] elements = getStackTraceSafe();
        if(elements == null)
            return false;

        //Copied pasted to even reduce time by skipping if checks but doing if checks once
        String cComp = className != null ? className.toLowerCase() : null;
        String mComp = methodName != null ? methodName.toLowerCase() : null;
        boolean doClass = cComp != null;
        boolean doMethod = mComp != null;
        if(doClass && doMethod) {
            if(useOr) {
                for(int i = 0; i < elements.length; i++) {
                    StackTraceElement e = elements[i];
                    String c = e.getClassName().toLowerCase();
                    String m = e.getMethodName().toLowerCase();
                    if(c.contains(cComp) || m.contains(mComp))
                        return true;
                }
            }else {
                for(int i = 0; i < elements.length; i++) {
                    StackTraceElement e = elements[i];
                    String c = e.getClassName().toLowerCase();
                    String m = e.getMethodName().toLowerCase();
                    if(c.contains(cComp) && m.contains(mComp))
                        return true;
                }
            }
        } else {
            if(doClass) {
                for(int i = 0; i < elements.length; i++) {
                    StackTraceElement e = elements[i];
                    String c = e.getClassName().toLowerCase();
                    if(c.contains(cComp))
                        return true;
                }
            } else if(doMethod) {
                for(int i = 0; i < elements.length; i++) {
                    StackTraceElement e = elements[i];
                    String m = e.getMethodName().toLowerCase();
                    if(m.contains(mComp))
                        return true;
                }
            }
        } return false;
    }

    public static String getStackTraceSafeString() { return getStackTraceSafeString(null); }
    public static String getStackTraceSafeString(Throwable thr) {
        StackTraceElement[] elements = getStackTraceSafe(thr);
        StringBuilder sb = new StringBuilder();
        if(elements != null) {
            for (int i = 0; i < elements.length; i++) {
                StackTraceElement e = elements[i];
                sb.append(" >> ").append(e.getClassName()).append(":").append(e.getMethodName()).append("(").append(e.getFileName()).append(":").append(e.getLineNumber()).append(")").append("\n");
            }
        } return sb.toString();
    }

    public static StackTraceElement[] getStackTraceSafe() { return getStackTraceSafe(null); }
    public static StackTraceElement[] getStackTraceSafe(Throwable thr) {
        //https://android.googlesource.com/platform/prebuilts/fullsdk/sources/android-29/+/refs/heads/androidx-core-release/java/lang/Throwable.java
        //https://cs.android.com/android/platform/superproject/main/+/main:libcore/ojluni/src/main/java/java/lang/Throwable.java
        //https://github.com/xdtianyu/android-6.0.0_r1/blob/master/libcore/luni/src/main/java/java/lang/Throwable.java
        if(!HiddenApiUtils.bypassHiddenApiRestrictions()) {
            Log.e(TAG, "Failed to bypass Hidden API Restrictions, used for [getStackTraceSafe]. Using fall back generic API.");
            return thr == null ? new Throwable().getStackTrace() : thr.getStackTrace();
        }

        if(thr != null) {
            if(internalGetStackTrace.isValid()) {
                StackTraceElement[] elements = internalGetStackTrace.tryInstanceInvokeEx(thr);
                if(elements != null && elements.length > 0)
                    return elements;
                else {
                    Log.e(TAG, "Failed to Get Stack Trace Custom Instance, it returned 0 or Null...");
                }
            } else {
                Log.e(TAG, "Failed to Get Stack Trace Custom Instance, seems Instance method is Null. Is Valid: " + internalGetStackTrace.isValid());
            }
            return thr.getStackTrace();
        } else {
            if(nativeFillInStackTrace.isValid() && nativeGetStackTrace.isValid()) {
                Object backTrace = nativeFillInStackTrace.tryStaticInvoke();
                if(backTrace != null) {
                    StackTraceElement[] elements = nativeGetStackTrace.tryStaticInvoke(backTrace);
                    if(elements != null && elements.length > 0)
                        return elements;
                    else {
                        Log.e(TAG, "Failed to Get Stack Trace Custom Static, it returned 0 or Null...");
                    }
                }
            } else {
                Log.e(TAG, "Failed to Get Stack Trace Custom Static, one of the Methods is Invalid. [nativeFillInStackTrace] is valid: " + nativeFillInStackTrace.isValid() + " [nativeGetStackTrace] is valid: " + nativeGetStackTrace.isValid());
            }
            return getStackTraceSafe(new Throwable());
        }
    }
}
