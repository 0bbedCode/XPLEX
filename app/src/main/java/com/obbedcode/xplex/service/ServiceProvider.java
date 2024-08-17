package com.obbedcode.xplex.service;

import android.app.ActivityThread;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.obbedcode.shared.logger.XLog;

//Provider, will get the "call" command
//We UID observe when our UID is present we link our Process to the Service
public class ServiceProvider extends ContentProvider {
    private static final String TAG = "ObbedCode.XP.ServiceProvider";

    @Override
    public boolean onCreate() { return false; }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null;}

    @Override
    public String getType(Uri uri) { return null; }

    @Override
    public Uri insert(Uri uri, ContentValues values) { return null; }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (!"android".equals(getCallingPackage()) || extras == null) return null;
        XLog.i(TAG, "Got the Service Call from the System Service on the Client Side! Caller is (android)");
        IBinder binder = extras.getBinder("binder");
        if (binder == null) return null;
        XLog.i(TAG, "Binder Read from the System Service Call on the Client Side! Now linking the Service");
        ServiceClient.linkService(binder);
        return new Bundle();
    }
}
