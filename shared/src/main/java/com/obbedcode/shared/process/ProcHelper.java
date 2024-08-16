package com.obbedcode.shared.process;

import android.os.FileObserver;

import androidx.annotation.Nullable;

import com.obbedcode.shared.BuildRuntime;
import com.obbedcode.shared.logger.XLog;

import java.io.File;

public class ProcHelper {
    private static final String TAG = "ObbedCode.XP.ProcHelper";

    public static void startActivityManagerUidObserver(INotifyUid onNotify) {
        try {
            if(BuildRuntime.isNougatApi24and25Android7(true)) {

            }
        }catch (Exception e) {
            XLog.e(TAG, "Failed to use Activity Manager UID observer [registerUidObserver]...", true, true);
        }
    }

    /*public static FileObserver startUIDFileMonitor(INotifyUid onNotify) {
        try {

        }catch (Exception e) {
            XLog.e(TAG, "[startUIDFileMonitor] Failed, error: " + e.getMessage(), true, true);
            return null;
        }
    }*/
}
