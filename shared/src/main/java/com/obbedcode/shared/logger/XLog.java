package com.obbedcode.shared.logger;

import android.util.Log;

import com.obbedcode.shared.XplexHelp;
import com.obbedcode.shared.utils.RuntimeUtils;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class XLog {
    //hmm also check if logd server is avail then create a log que to send to it ?
    //also log que can be used to write to file or DB
    private static boolean mDoLog = true;

    public static boolean hasXposed = false;

    public static void i(String tag, String message) { i(tag, message, false); }
    public static void i(String tag, String message, boolean xposedBridge) {
        if(mDoLog) {
            Log.i(tag, message);
            if(xposedBridge) {
                //Need to fix this piece of shit
                //Its causing issues when "xposed" somehow isnt working
                //Reflection does not work ...
                //Worst part is the FUCKING IDE does not push Updates unless <strings.xml> gets modified ??
                //Who invented this shit fucking IDE ?????
                XposedBridge.log("[" + tag + "] " + message);
            }
        }
    }

    public static void e(String tag, Exception exception) { e(tag, exception, true, false); }
    public static void e(String tag, Exception exception, boolean printStack) { e(tag, exception, printStack, false); }
    public static void e(String tag, Exception exception, boolean printStack, boolean xposedBridge) { e(tag, exception.getMessage() + (printStack ? "\n" + Log.getStackTraceString(exception) : ""), false, xposedBridge); }

    public static void e(String tag, String message) { e(tag, message, false, false); }
    public static void e(String tag, String message, boolean printStack) { e(tag, message, printStack, false); }
    public static void e(String tag, String message, boolean printStack, boolean xposedBridge) {
        if(mDoLog) {
            String msg = message + (printStack ? "\n" + Log.getStackTraceString(new Exception()) : "");
            Log.e(tag, msg);
            if(xposedBridge) {
                XposedBridge.log("[" + tag + "] " + msg);
            }
        }
    }



    /*public static boolean isXposedAvailable() {
        boolean isXposedAvailable = false;
        try {
            Class.forName("de.robv.android.xposed.XposedBridge");
            isXposedAvailable = true;
        } catch (ClassNotFoundException e) {
            isXposedAvailable = false;
        }
        return isXposedAvailable;
    }

    public static boolean isInModuleContext() {
        try {
            ClassLoader classLoader = XLog.class.getClassLoader();
            return classLoader.loadClass("de.robv.android.xposed.XposedBridge") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }*/

    //Tbh not really a way besides some stupid pre hook shit....
    /*private static boolean hasXposedLoaded() {
        try {
            //Class<?> clzz = Class.forName("de.robv.android.xposed.XposedBridge");
            Class<?> clzz = XposedBridge.class;
            Log.d("ObbedCode.XLog", "Class Exists for Xposed");
            Method mth = clzz.getMethod("log", String.class);
            Log.d("ObbedCode.XLog", "Method Exists for Xposed");
            return true;
        }catch (Exception ignore) { Log.d("ObbedCode.XLog", "Xposed does not exist :( " + ignore.getMessage()); }
        return false;
    }*/
}
