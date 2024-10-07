package com.obbedcode.shared.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;

public class LocalSettings {
    private static final String TAG = "ObbedCode.LocalSettings";



    public static boolean hasPreference(Context context, String prefName) {
        if(Str.isValid(prefName)) {
            try {
                return PreferenceManager.getDefaultSharedPreferences(context).contains(prefName);
            }catch (Exception e) {
                XLog.e(TAG, "Preference Error: " + e);
            }
        } return false;
    }

    public static void setString(Context context, String prefName, String value) {
        if(Str.isValid(prefName)) {
            try {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                prefs.edit().putString(prefName, value).apply();
            }catch (Exception e) {
                XLog.e(TAG, "Preference Error: " + e);
            }
        }
    }

    public static String getString(Context context, String prefName) {
        if(Str.isValid(prefName)) {
            try {
                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if(prefs.contains(prefName)) {
                    return prefs.getString(prefName, null);
                }
            }catch (Exception e) {
                XLog.e(TAG, "Preference Error: " + e);
            }
        } return null;
    }

}
