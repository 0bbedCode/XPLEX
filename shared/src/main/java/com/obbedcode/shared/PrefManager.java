package com.obbedcode.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.obbedcode.shared.logger.XLog;

public class PrefManager {
    private static SharedPreferences pref = null;

    public static void ensureOpen(Context context) {
        if(pref == null && context != null) {

            pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            //pref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static void bindPreferences(SharedPreferences prefs) {
        if(prefs != null) {
            pref = prefs;
        }
    }

    public static final String ORDER_APPLICATION_NAME = "Application Name";

    public String id;
    public String defaultOrder;

    public static PrefManager create(String id, String defaultOrder) { return new PrefManager(id, defaultOrder); }
    public PrefManager(String id, String defaultOrder) {
        this.id = id;
        this.defaultOrder = defaultOrder;
    }

    public void isEnabled(String key, boolean enabled) { pref.edit().putBoolean(id + "_is_" + key, enabled).apply(); }
    public boolean isEnabled(String key) { return pref.getBoolean(id + "_is_"  + key, false); }

    public void orderEx(String order) { pref.edit().putString(id + "_order", order).apply(); }
    public String orderEx() {
        XLog.i("ObbedCode.XP.PrefManager", "PREF IS NULL: " + (pref == null) + " ID=" + id + "  DEF=" + defaultOrder);
        return pref.getString(id + "_order", defaultOrder);
    }

    public void isReverseEx(boolean isDisabled) { pref.edit().putBoolean(id + "_isReverse", isDisabled).apply(); }
    public boolean isReverseEx() { return pref.getBoolean(id + "_isReverse", false); }


    //public static String getDefaultPage() { return pref }

    public static boolean isConfigured() { return pref.getBoolean("isConfigured", false); }
    public static void isConfigured(boolean isConfigured) { pref.edit().putBoolean("isConfigured", isConfigured).apply(); }

    public static void isUpdated(boolean updated) { pref.edit().putBoolean("updated", updated).apply(); }
    public static boolean isUpdated() { return pref.getBoolean("updated", false); }

    public static void isDisabled(boolean isDisabled) { pref.edit().putBoolean("disabled", isDisabled).apply(); }
    public static boolean isDisabled() { return pref.getBoolean("disabled", false); }

    public static void order(String order) { pref.edit().putString("order", ORDER_APPLICATION_NAME).apply(); }
    public static String order() {
        return pref.getString("order", "Application Name");
    }

    public static void isReverse(boolean isDisabled) { pref.edit().putBoolean("isReverse", isDisabled).apply(); }
    public static boolean isReverse() { return pref.getBoolean("isReverse", false); }
}
