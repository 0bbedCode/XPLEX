package com.obbedcode.shared.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

public class XposedUtils {
    private static Boolean isExp;
    public static boolean isExpModuleActive() {
        if (isExp != null) return isExp;
        try {
            //ye we can use ActivityThread...
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
}
