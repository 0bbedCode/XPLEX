package com.obbedcode.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefManager {
    private static SharedPreferences pref = null;

    public static void ensureOpen(Context context) {
        if(pref == null && context != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void bindPreferences(SharedPreferences prefs) {
        if(prefs != null) {
            pref = prefs;
        }
    }

    public static final String ORDER_APPLICATION_NAME = "Application Name";

    //public static String getDefaultPage() { return pref }

    public static boolean isConfigured() { return pref.getBoolean("isConfigured", false); }
    public static void isConfigured(boolean isConfigured) { pref.edit().putBoolean("isConfigured", isConfigured).apply(); }

    public static void isUpdated(boolean updated) { pref.edit().putBoolean("updated", updated).apply(); }
    public static boolean isUpdated() { return pref.getBoolean("updated", false); }

    public static void isDisabled(boolean isDisabled) { pref.edit().putBoolean("disabled", isDisabled).apply(); }
    public static boolean isDisabled() { return pref.getBoolean("disabled", false); }

    public static void order(String order) { pref.edit().putString("order", ORDER_APPLICATION_NAME).apply(); }
    public static String order() { return pref.getString("order", "Application Name"); }

    public static void isReverse(boolean isDisabled) { pref.edit().putBoolean("isReverse", isDisabled).apply(); }
    public static boolean isReverse() { return pref.getBoolean("isReverse", false); }
}
