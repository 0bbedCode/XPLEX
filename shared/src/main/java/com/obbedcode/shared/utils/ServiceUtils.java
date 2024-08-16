package com.obbedcode.shared.utils;

import android.annotation.SuppressLint;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Log;

import java.lang.reflect.Method;

public class ServiceUtils {
    public static void waitSystemService(String name) {
        while (ServiceManager.getService(name) == null) {
            //try {
            //    Thread.sleep(1000);
            //} catch (InterruptedException e) {
            //    Thread.currentThread().interrupt();
            //    throw new RuntimeException(e);
            //}
            ThreadUtils.sleep(1000);
        }
    }

    public static IBinder getService(String serviceName) {
        try {
            IBinder binder = null;
            @SuppressLint("PrivateApi") Class<?> servManagerClass = Class.forName("android.os.ServiceManager");
            Method getServ = servManagerClass.getMethod("getService", String.class);
            binder = (IBinder)getServ.invoke(null, serviceName);
            return binder;
        }catch (Exception e) {
            return null;
        }
    }
}
