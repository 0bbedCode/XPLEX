package com.obbedcode.shared.hook.gum;

import android.os.Build;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.ReflectUtil;
import com.obbedcode.shared.utils.RuntimeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ReflectGum {
    private static final String REFLECT_TAG = "ObbedCode.XP.ReflectGum";
    //Create a Cache

    public int runningSdk() { return Build.VERSION.SDK_INT; }

    public Method methodFor(Class<?> clazz, String methodName) { return ReflectUtil.tryGetMethod(clazz, methodName); }
    public Method methodFor(String className, String methodName) { return ReflectUtil.tryGetMethod(classFor(className), methodName); }
    public Class<?> classFor(String className) { return ReflectUtil.tryGetClassForName(className); }

    public boolean hasField(Class<?> clazz, String fieldName) {
        try {
            for(Field f : clazz.getDeclaredFields())
                if(f.getName().equalsIgnoreCase(fieldName))
                    return true;
            for(Field f : clazz.getFields())
                if(f.getName().equalsIgnoreCase(fieldName))
                    return true;
        }catch (Exception ignored) { }
        return false;
    }

    public boolean hasMethod(Class<?> clazz, String methodName) {
        try {
            for(Method m : clazz.getDeclaredMethods())
                if(m.getName().equalsIgnoreCase(methodName))
                    return true;
            for(Method m : clazz.getMethods())
                if(m.getName().equalsIgnoreCase(methodName))
                    return true;

        }catch (Exception ignored) { }
        return false;
    }

    public String getStackTraceString() { return RuntimeUtils.getStackTraceSafeString(); }
    public StackTraceElement[] getStackTrace() { return RuntimeUtils.getStackTraceSafe(); }

    public Object invoke(String className, String methodName, Object instance, Object... args) {
        try {
            //Set accessibility ?
            Class<?>[] types = new Class[args.length];
            for(int i = 0; i < args.length; i++) types[i] = args[i].getClass();
            Method mth = ReflectUtil.tryGetMethod(ReflectUtil.tryGetClassForName(className), methodName, types);
            return mth.invoke(instance, args);
        }catch (Exception e) {
            XLog.e(REFLECT_TAG, "Failed to InstanceInvoke Method: " + methodName + " Class: " + className + " Error: " + e.getMessage());
            return null;
        }
    }

    public Object invokeStatic(String className, String methodName, Object... args) {
        try {
            Class<?>[] types = new Class[args.length];
            for(int i = 0; i < args.length; i++) types[i] = args[i].getClass();
            Method mth = ReflectUtil.tryGetMethod(ReflectUtil.tryGetClassForName(className), methodName, types);
            return mth.invoke(null, args);
        }catch (Exception e) {
            XLog.e(REFLECT_TAG, "Failed to Static Invoke Method: " + methodName + " Class: " + className + " Error: " + e.getMessage());
            return null;
        }
    }
}
