package com.obbedcode.shared.db;

import com.obbedcode.shared.logger.XLog;

public class DatabaseUtils {
    private static final String TAG = "ObbedCode.XP.DatabaseUtils";

    public static boolean isReady(SQLDatabase db) {
        if(db == null) {
            XLog.e(TAG, "[isReady] Database Object is null...");
            return false;
        }

        if(!db.exists()) {
            XLog.e(TAG, "[isReady] Database Does not Exist: " + db.file.getAbsolutePath());
            return false;
        }
    }
}
