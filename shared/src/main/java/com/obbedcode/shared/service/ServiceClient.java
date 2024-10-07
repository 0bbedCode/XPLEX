package com.obbedcode.shared.service;


import android.os.IBinder;
import android.os.RemoteException;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.usage.RunningProcess;
import com.obbedcode.shared.utils.ThreadUtils;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.XSetting;
import com.obbedcode.shared.xplex.database.XPrivacyControl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

//
//This is for the Client, will contact actual XplexService (ServiceClient.getLogs() => HMAService.getLogs())
//
public class
ServiceClient extends IXPService.Stub implements IBinder.DeathRecipient {
    private static final String TAG = "ObbedCode.XP.ServiceClient";
    private static IXPService service;

    public static IXPService waitForService() {
        while (service == null)
            ThreadUtils.sleep(1200);
        return service;
    }

    public static IXPService getService() {
        return service;
    }

    private static class ServiceProxy implements InvocationHandler {
        private final IXPService obj;
        ServiceProxy(IXPService obj) { this.obj = obj; }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(obj, args);
            if (result == null)
                XLog.i(TAG, "Call service method " + method.getName());
             else
                XLog.i(TAG, "Call service method " + method.getName() + " with result " + result.toString().substring(0, Math.min(20, result.toString().length())));

            return result;
        }
    }

    public static void linkService(IBinder binder) {
        XLog.i(TAG, "Linking the System Service Binder to a Proxy Service Class...");
        service = (IXPService) Proxy.newProxyInstance(
                ServiceClient.class.getClassLoader(),
                new Class[]{IXPService.class},
                new ServiceProxy(Stub.asInterface(binder)));
        XLog.i(TAG, "System Service Binder has been linked to the Proxy Service Class ");
        try {
            binder.linkToDeath(new ServiceClient(), 0);
        } catch (RemoteException e) {
            XLog.e(TAG, "Failed to link death recipient: " + e.getMessage());
        }
    }

    @Override
    public void binderDied() { service = null; }

    @Override
    public IBinder asBinder() { return service == null ? null : service.asBinder(); }

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public List<RunningProcess> getRunningProcesses()  throws RemoteException { return service.getRunningProcesses(); }

    @Override
    public String getLog() throws RemoteException {
        return "";
    }

    @Override
    public double getOverallCpuUsage() throws RemoteException {
        return service.getOverallCpuUsage();
    }

    @Override
    public double getOverallMemoryUsage() throws RemoteException {
        return service.getOverallMemoryUsage();
    }

    @Override
    public ParceledListSlice<XApp> getInstalledAppsEx() throws RemoteException { return service.getInstalledAppsEx(); }

    @Override
    public ParceledListSlice<XAssignment> getAppAssignments(int userId, String category) throws RemoteException {
        return service.getAppAssignments(userId, category);
    }


    @Override
    public ParceledListSlice<XSetting> getAppSettings(int userId, String category) throws RemoteException { return service.getAppSettings(userId, category); }
}