package com.obbedcode.shared.process;

import android.app.ActivityThread;
import android.os.Build;
import android.os.Process;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import rikka.hidden.compat.PackageManagerApis;

public class ProcHelper {
    private static final String TAG = "ObbedCode.XP.ProcHelper";

    public static boolean isSystemUID() { return isSystemUID(Process.myUid()); }
    public static boolean isSystemUID(int uid) { return uid == Process.SYSTEM_UID; }

    public static String getPackageName() {
        try {
            return ActivityThread.currentActivityThread().getApplication().getPackageName();
        }catch (Exception e) {
            XLog.e(TAG, "Failed Getting Package Name, Error: " + e.getMessage(), true);
            List<String> packages = PackageManagerApis.getPackagesForUidNoThrow(Process.myUid());
            return packages.get(0);
        }
    }

    public static String getSelfProcessName() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Process.myProcessName();
        } else {
            BufferedReader fileReader = null;
            try {
                fileReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream("/proc/self/cmdline"), "iso-8859-1"));

                int c;
                StringBuilder processName = new StringBuilder();
                while ((c = fileReader.read()) > 0)
                    processName.append((char) c);

                return processName.toString();
            }catch (Exception e) {
                XLog.e(TAG, "Failed to Read /proc/self/cmdline File for Process Name, Error: " + e.getMessage(), true);
            } finally {
                StreamUtils.dispose(fileReader);
            }
        } return getPackageName();
    }
}
