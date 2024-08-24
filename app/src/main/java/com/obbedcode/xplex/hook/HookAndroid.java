package com.obbedcode.xplex.hook;

import android.annotation.SuppressLint;
import android.content.pm.IPackageManager;

import com.obbedcode.shared.hook.HookManager;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.xplex.service.UserService;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookAndroid {
    private static final String TAG = "ObbedCode.XP.HookAndroid";

    private static boolean hasFoundPackageService = false;
    private static final HookManager manager = new HookManager();



    public static void deployHook(final XC_LoadPackage.LoadPackageParam lpparam) {
        if("android".equals(lpparam.packageName)) {
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

                @SuppressLint("PrivateApi")
                Class<?> clazzSensor = Class.forName("com.android.server.SensorServer", false, lpparam.classLoader);


                XposedBridge.hookAllMethods(clazzSensor, "startSensorServiceNative", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XLog.i(TAG, "SENSOR SERVICE NATIVE [startSensorServiceNative]", true);
                        super.beforeHookedMethod(param);
                    }
                });

                XposedBridge.hookAllMethods(clazzSensor, "registerRuntimeSensorNative", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            int id = (int)param.args[1];
                            String name = (String)param.args[3];
                            String manf = (String)param.args[4];
                            XLog.i(TAG, "SENSOR SERVICE [registerRuntimeSensorNative] ID:" + id + " NAME:" + name + " MANF: " + manf, true);
                        }catch (Exception ignored) { }
                        super.beforeHookedMethod(param);
                    }
                });

                //XposedBridge.hookAllMethods(clazzSensor, )

                //https://android.googlesource.com/platform/frameworks/base.git/+/master/core/java/com/android/internal/os/ZygoteInit.java
                //com.android.server.am.ActivityManagerService [startProcessLocked] [startIsolatedProcess]


                /*Class<?> clazzZY = Class.forName("android.os.ZygoteProcess", false, lpparam.classLoader);
                XposedBridge.hookAllMethods(clazzZY, "startViaZygote", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        try {
                            int uid = (int)param.args[2];
                            XLog.i(TAG, "UID Starting Zygote: " + uid + " Is UID: " + (uid == APP_UID) + " Target: " + APP_UID, true);
                        }catch (Exception e) {
                            XLog.e(TAG, "Failed to After Hook for [startViaZygote]: " + e.getMessage(), true, true);
                        }
                    }
                });*/


                //XLog.i(TAG, "Hooking [startProcessLocked] Method in Android", true);
                //Class<?> clazzAMS = Class.forName("com.android.server.am.ActivityManagerService", false, lpparam.classLoader);
                //XposedBridge.hookAllMethods(clazzAMS, "startProcessLocked", new XC_MethodHook() {
                    //@Override
                    //protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //try {
                            //super.afterHookedMethod(param);
                            //String p = (String)param.args[0];
                            //if(p.equalsIgnoreCase(BuildConfig.APPLICATION_ID)) {
                                //try {
                                    //XLog.i(TAG, "XPL-EX Process Started! Linking the Service: " + BuildConfig.APPLICATION_ID, true);
                                    //IContentProvider provider = ActivityManagerApis.getContentProviderExternal(Constants.PROVIDER_AUTHORITY, 0, null, null);
                                    //if(provider == null) {
                                    //    XLog.e(TAG, "Content Provider for [" + Constants.PROVIDER_AUTHORITY + "] Is Null...", true, true);
                                    //    return;
                                    //}

                                    /*XLog.i(TAG, "Creating Bundle for XPL-EX Service", true);
                                    Bundle extras = new Bundle();
                                    extras.putBinder("binder", XplexService.instance);
                                    XLog.i(TAG, "Instance was written to the Binder XplexService is Null ? " + (XplexService.instance == null), true);

                                    Bundle reply = null;
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        AttributionSource attr = new AttributionSource.Builder(1000).setPackageName(Constants.SYSTEM_SERVER).build();
                                        reply = provider.call(attr, Constants.PROVIDER_AUTHORITY, Str.EMPTY, null, extras);
                                    }
                                    else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R) reply = provider.call(Constants.SYSTEM_SERVER, null, Constants.PROVIDER_AUTHORITY, Str.EMPTY, null, extras);
                                    else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) reply = provider.call(Constants.SYSTEM_SERVER, Constants.PROVIDER_AUTHORITY, Str.EMPTY, null, extras);
                                    else reply = provider.call(Constants.SYSTEM_SERVER, Str.EMPTY, null, extras);
                                    if(reply == null) {
                                        XLog.e(TAG, "Failed to send Binder to XPL-EX, [" + Constants.PROVIDER_AUTHORITY + "]", true, true);
                                        return;
                                    }

                                    XLog.i(TAG, "Sent Binder to XPL-EX,  [" + Constants.PROVIDER_AUTHORITY + "]", true);*/
                                //}catch (Exception e) {
                                //    XLog.e(TAG, "Service to XPL-EX Linking Error: " + e.getMessage(), true, true);
                                //}
                            //}
                        //}catch (Exception e) {
                        //    XLog.e(TAG, "[startProcessLocked] Exception Hook: " + e.getMessage(), true, true);
                        //}
                    //}
                //});

                /*XposedBridge.hookAllMethods(clazzAMS, "startIsolatedProcess", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        try {
                            String aOne = (String)param.args[0];
                            String aThe = (String)param.args[2];
                            XLog.i(TAG, "Process Started Isolated: " + aOne + " P: " + aThe, true);
                        }catch (Exception e) {

                        }
                    }
                });*/

            }catch (Exception eServiceManager) {
                XLog.e(TAG, eServiceManager);
            }
        }
    }
}
