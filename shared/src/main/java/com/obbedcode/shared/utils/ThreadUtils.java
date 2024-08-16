package com.obbedcode.shared.utils;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;

public class ThreadUtils {
    private static final String TAG = "ObbedCode.XP.ThreadUtils";

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        }catch (Exception e) {
            XLog.e(TAG, "Sleep Exception: " + e.getMessage());
        }
    }
}
