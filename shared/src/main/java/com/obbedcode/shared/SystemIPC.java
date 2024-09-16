package com.obbedcode.shared;

import android.annotation.SuppressLint;
import android.content.AttributionSource;
import android.content.IContentProvider;
import android.os.Bundle;
import android.os.Process;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.usage.ProcessApi;

import java.util.List;

import rikka.hidden.compat.ActivityManagerApis;
import rikka.hidden.compat.PackageManagerApis;

public class SystemIPC {
    private static final String TAG = "ObbedCode.XP.IPC";

    public static SystemIPC create() { return new SystemIPC(); }

    private int mUid;
    private String mCallingPackage;
    private String mProviderAuthority;

    private String mMethod;
    private String mArg;
    private Bundle mExtras;

    public SystemIPC() {
        mUid = 0;
        mMethod = Str.EMPTY;
        mArg = null;
    }

    public SystemIPC bindCaller() {
        this.mUid = Process.myUid();
        this.mCallingPackage = ProcessApi.getSelfPackageName();
        return this;
    }

    public SystemIPC setUid(int uid) {
        this.mUid = uid;
        return this;
    }

    public SystemIPC setCallingPackage(String callingPackage) {
        this.mCallingPackage = callingPackage;
        return this;
    }

    public SystemIPC setProviderAuthority(String providerAuthority) {
        this.mProviderAuthority = providerAuthority;
        return this;
    }

    public SystemIPC setMethod(String method) {
        this.mMethod = method;
        return this;
    }

    public SystemIPC setArg(String arg) {
        this.mArg = arg;
        return this;
    }

    public SystemIPC setExtras(Bundle extras) {
        this.mExtras = extras;
        return this;
    }

    public Bundle callToProvider() { return makeCall(this.mProviderAuthority, this.mUid, this.mCallingPackage, this.mMethod, this.mArg, this.mExtras); }

    public static Bundle makeCall(
            String providerAuthority,
            Bundle extras) {
        int uid = Process.myUid();
        List<String> packages = PackageManagerApis.getPackagesForUidNoThrow(uid);
        return makeCall(providerAuthority, uid, packages.get(0), Str.EMPTY, null, extras);
    }

    public static Bundle makeCall(
            String providerAuthority,
            String method,
            String arg,
            Bundle extras) {
        int uid = Process.myUid();
        List<String> packages = PackageManagerApis.getPackagesForUidNoThrow(uid);
        return makeCall(providerAuthority, uid, packages.get(0), method, arg, extras);
    }

    public static Bundle makeCall(
            String providerAuthority,
            int uid,
            String callingPackage,
            Bundle extras) {
        return makeCall(providerAuthority, uid, callingPackage, Str.EMPTY, null, extras);
    }

    @SuppressLint("NewApi")
    public static Bundle makeCall(
            String providerAuthority,
            int uid,
            String callingPackage,
            String method,
            String arg,
            Bundle extras) {
        Bundle reply = null;
        try {
            IContentProvider provider = ActivityManagerApis.getContentProviderExternal(providerAuthority, 0, null, null);
            if(provider == null) {
                XLog.e(TAG, "Failed to Get Content Provider: " + providerAuthority);
                return reply;
            }

            if(BuildRuntime.isSnowConeApi31Android12(true)) {
                AttributionSource attr = new AttributionSource.Builder(uid).setPackageName(callingPackage).build();
                reply = provider.call(attr, providerAuthority, method, arg, extras);
            }
            else if(BuildRuntime.isRedVelvetCakeApi30Android11())
                reply = provider.call(callingPackage, null, providerAuthority, method, arg, extras);
            else if(BuildRuntime.isQuinceTartApi29Android10())
                reply = provider.call(callingPackage, providerAuthority, method, arg, extras);
            else
                provider.call(callingPackage, method, arg, extras);
        }catch (Exception e) {
            XLog.e(TAG, "Failed to make a IPC Call: " + e.getMessage(), true, true);
        }
        return reply;
    }
}
