package com.obbedcode.shared.api;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.process.ProcHelper;
import com.obbedcode.shared.SystemIPC;

import java.util.ArrayList;
import java.util.List;

public class XposedApi {
    private static final String TAG = "ObbedCode.XP.XposedApi";
    private static final String WEISHU_URI = "content://me.weishu.exposed.CP/";

    private static Boolean isExp;
    public static boolean isExpModuleActive() {
        if (isExp != null) {
            return isExp;
        }
        try {
            @SuppressLint("PrivateApi") Context context = (Context) Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
            if (context == null)
                return isExp = false;

            try {
                Bundle call = context.getContentResolver().call(Uri.parse("content://me.weishu.exposed.CP/"), "active", null, null);
                if (call == null)
                    return isExp = false;

                isExp = call.getBoolean("active", false);
                return isExp;
            } catch (Throwable th) {
                return isExp = false;
            }
        } catch (Throwable th2) {
            return isExp = false;
        }
    }

    public static boolean isVirtualXposed() {
        return  !TextUtils.isEmpty(System.getProperty("vxp"))
                || !TextUtils.isEmpty(System.getProperty("exp"))
                || isExpModuleActive();
    }

    public static List<String> getExpApps() { return getExpApps(null); }
    public static List<String> getExpApps(Context context) {
        List<String> expApps = new ArrayList<>();
        if(context == null) {
            try {
                Bundle call = SystemIPC.create()
                        .setUid(Process.myUid())
                        .setCallingPackage(ProcHelper.getPackageName())
                        .setMethod("apps")
                        .callToProvider();
                expApps.addAll(call.getStringArrayList("apps"));
            }catch (Exception e) {
                XLog.e(TAG, "Failed to Create a List of Virtual Xposed Apps TaiChi, Error: " + e.getMessage(), true, true);
            }
        }else {
            try {
                Bundle call = context.getContentResolver().call(Uri.parse(WEISHU_URI), "apps", null, null);
                if(call == null || !call.containsKey("apps"))
                    return expApps;

                expApps.addAll(call.getStringArrayList("apps"));
            }catch (Exception e) {
                XLog.e(TAG, "Failed to Create a List of Virtual Xposed Apps TaiChi, Error: " + e.getMessage(), true, true);
            }
        }
        return expApps;
    }
}
