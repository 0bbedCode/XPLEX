package com.obbedcode.shared.utils;

import android.text.TextUtils;

public class DataTypeUtils {
    public static boolean stringToBool(String s) {
        if(TextUtils.isEmpty(s)) return false;
        String sLow = s.toLowerCase().trim();
        return isTrueString(sLow);
    }

    public static boolean isTrueString(String s) {
        if(TextUtils.isEmpty(s)) return false;
        return "yes".equalsIgnoreCase(s) ||
                "true".equalsIgnoreCase(s) ||
                "1".equalsIgnoreCase(s) ||
                "checked".equalsIgnoreCase(s) ||
                "enabled".equalsIgnoreCase(s) ||
                "enable".equalsIgnoreCase(s);
    }

    public static boolean isFalseString(String s) {
        if(TextUtils.isEmpty(s)) return false;
        return "no".equalsIgnoreCase(s) ||
                "false".equalsIgnoreCase(s) ||
                "0".equalsIgnoreCase(s) ||
                "unchecked".equalsIgnoreCase(s) ||
                "disabled".equalsIgnoreCase(s) ||
                "disable".equalsIgnoreCase(s);
    }

    public static boolean intToBool(int value) {
        return value == 1;
    }

    public static Integer boolToInt(Boolean value) {
        if(value == null) return null;
        return value ? 1 : 0;
    }
}
