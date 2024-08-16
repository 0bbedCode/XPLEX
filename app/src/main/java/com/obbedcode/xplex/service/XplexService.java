package com.obbedcode.xplex.service;

import android.os.RemoteException;

import com.obbedcode.shared.IXplexService;


public class XplexService extends IXplexService.Stub {
    public static XplexService instance = null;

    public XplexService() { instance = this; }

    //@Override
    //public String getLogs() {
    //    return "";
    //}

    //@Override
    //public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException { }
}
