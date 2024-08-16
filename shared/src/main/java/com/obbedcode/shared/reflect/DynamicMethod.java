package com.obbedcode.shared.reflect;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DynamicMethod {
    private static final String TAG = "ObbedCode.XP.DynamicMethod";

    private final Method mMethod;
    private Object mInstance;

    public boolean isValid() { return mMethod != null; }

    public DynamicMethod(String className, String methodName, Class<?> paramTypes) { this(ReflectUtil.tryGetClassForName(className), methodName, paramTypes); }
    public DynamicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) { this(ReflectUtil.tryGetMethod(clazz, methodName, paramTypes)); }
    public DynamicMethod(Method method) {
        this.mMethod = method;
    }

    public DynamicMethod bindInstance(Object instance) { this.mInstance = instance; return this; }

    public DynamicMethod setAccessible(boolean accessible) {
        try {
            this.mMethod.setAccessible(accessible);
            return this;
        }catch (Exception e) {
            Log.e(TAG, "[setAccessible] Error Setting Accessibility: set:" + accessible);
            return this;
        }
    }

    public <T> T staticInvoke(Object ... args) throws InvocationTargetException, IllegalAccessException { return (T)mMethod.invoke(null, args); }
    public <T> T tryStaticInvoke(Object... args) {
        try {
            return (T)mMethod.invoke(null, args);
        }catch (Exception e) {
            Log.e(TAG, "[tryStaticInvoke] Error Invoking Method static: " + e.getMessage());
            return null;
        }
    }

    public <T> T instanceInvokeEx(Object instance, Object... args) throws InvocationTargetException, IllegalAccessException { return (T)mMethod.invoke(instance, args); }
    public <T> T tryInstanceInvokeEx(Object instance, Object... args) {
        try {
            return (T)mMethod.invoke(instance, args);
        }catch (Exception e) {
            Log.e(TAG, "[tryInstanceInvokeEx] Error Invoking Method Instance: " + e.getMessage());
            return null;
        }
    }

    public <T> T instanceInvoke(Object... args) throws InvocationTargetException, IllegalAccessException { return (T)mMethod.invoke(mInstance, args); }
    public <T> T tryInstanceInvoke(Object... args) {
        try {
            return (T)mMethod.invoke(mInstance, args);
        }catch (Exception e) {
            Log.e(TAG, "[tryInstanceInvoke] Error Invoking Method Instance: " + e.getMessage());
            return null;
        }
    }
}
