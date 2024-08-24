package com.obbedcode.xplex;

import android.app.Application;
import android.util.Log;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.xplex.root.RootManager;

public class XplexApplication extends Application {
    private static final String TAG = "ObbedCode.XplexApplication";
    public static RootManager Manager = new RootManager();

    @Override
    public void onCreate() {
        super.onCreate();
        //Having a Xposed.log here on any activity seems to break it, its for module part of apk (aka non activity/ui ? )
        //https://stackoverflow.com/questions/24528719/classnotfoundexception-with-xposedbridge
        //Hmm to be clear that would be the "XposedEntry" class everything it branches out too
        Log.w(TAG, "Creating....");
        //if(!Manager.hasRootAccess) {
        //    Manager.requestRoot();
        //}

        XLog.i(TAG, "Root Status: " + Manager.hasRootAccess);
    }
}
