package com.obbedcode.shared.utils;

import android.database.Cursor;

public class CursorUtils {

    public static Boolean readBoolean(Cursor c, String key) { return readBoolean(c, key, null); }
    public static Boolean readBoolean(Cursor c, String key, Boolean defaultValue) {
        Integer def = null;
        if(defaultValue != null) def = defaultValue ? 1 : 0;
        Integer v = readInteger(c, key, def);
        if(v == null) return defaultValue;
        if(v == 1) return true;
        if(v == 0) return false;
        return defaultValue;
    }

    public static Integer readInteger(Cursor c, String key) { return readInteger(c, key, null); }
    public static Integer readInteger(Cursor c, String key, Integer defaultValue) {
        if(c == null || key == null) return defaultValue;
        int cIx = c.getColumnIndex(key);
        if(cIx == -1) return defaultValue;
        return c.getInt(cIx);
    }

    public static Long readLong(Cursor c, String key) { return readLong(c, key, null); }
    public static Long readLong(Cursor c, String key, Long defaultValue) {
        if(c == null || key == null) return defaultValue;
        int cIx = c.getColumnIndex(key);
        if(cIx == -1) return defaultValue;
        return c.getLong(cIx);
    }

    public static String readString(Cursor c, String key) { return readString(c, key, null); }
    public static String readString(Cursor c, String key, String defaultValue) {
        if(c == null || key == null) return  defaultValue;
        int cIx = c.getColumnIndex(key);
        if(cIx == -1) return defaultValue;
        String v = c.getString(cIx);
        return v == null ? defaultValue : v;
    }

    public static void closeCursor(Cursor c) {
        if(c != null) {
            try { c.close(); }catch (Throwable ignored) { }
        }
    }
}
