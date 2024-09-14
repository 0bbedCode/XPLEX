package com.obbedcode.shared.utils;

import android.database.Cursor;

public class CursorUtils {
    public static void closeCursor(Cursor c) {
        if(c != null) {
            try { c.close(); }catch (Throwable ignored) { }
        }
    }
}
