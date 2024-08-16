package com.obbedcode.xplex.service;


import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;


//import com.obbedcode.shared.IXplexService;
import com.obbedcode.shared.logger.XLog;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//
//This is for the Client, will contact actual XplexService (ServiceClient.getLogs() => HMAService.getLogs())
//
/*public class ServiceClient extends IXplexService.Stub implements IBinder.DeathRecipient {
    private static final String TAG = "ObbedCode.XP.ServiceClient";
    private static IXplexService service;

    //Proxy Class for Service
    private static class ServiceProxy implements InvocationHandler {
        private final IXplexService obj;
        ServiceProxy(IXplexService obj) { this.obj = obj; }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(obj, args);
            if (result == null)
                Log.i(TAG, "Call service method " + method.getName());
             else
                Log.i(TAG, "Call service method " + method.getName() + " with result " + result.toString().substring(0, Math.min(20, result.toString().length())));

            return result;
        }
    }

    public static void linkService(IBinder binder) {
        service = (IXplexService) Proxy.newProxyInstance(
                ServiceClient.class.getClassLoader(),
                new Class[]{IXplexService.class},
                new ServiceProxy(IXplexService.Stub.asInterface(binder)));
        try {
            binder.linkToDeath(new ServiceClient(), 0);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to link death recipient", e);
        }
    }

    @Override
    public void binderDied() { service = null; }

    @Override
    public IBinder asBinder() { return service == null ? null : service.asBinder(); }



    @Override
    public int getServiceVersion() throws RemoteException {

    }

    private IXplexService getServiceLegacy() {
        if(service != null) return service;

        IBinder pm = ServiceManager.getService("package");
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();

        IXplexService remote = null;
        try {
            data.writeInterfaceToken("com.something");
        }catch (RemoteException e) {
            XLog.e(TAG, "Remote Exception Service: " + e);
        } finally {
            data.recycle();
            reply.recycle();
        }
    }
}*/