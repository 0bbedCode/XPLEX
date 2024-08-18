package com.obbedcode.xplex.hook;

import android.os.Process;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.ReflectUtil;
import com.obbedcode.shared.usage.ProcessUtils;
import com.obbedcode.shared.usage.ProcessApi;
import com.obbedcode.shared.utils.HiddenApiUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TestSite {
    private static final String TAG = "ObbedCode.XP.TestSite";

    private static final int PROC_SPACE_TERM = 32;   // ASCII for space ' '
    private static final int PROC_COMBINE = 256;     // 0x100
    private static final int PROC_OUT_LONG = 8192;   // 0x2000
    private static final int[] SYSTEM_CPU_FORMAT = new int[] {
            PROC_SPACE_TERM | PROC_COMBINE,          // Combine multiple spaces
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 1: user time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 2: nice time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 3: system time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 4: idle time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 5: iowait time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 6: irq time
            PROC_SPACE_TERM | PROC_OUT_LONG          // 7: softirq time
    };

    /*dumpsys cpuinfo
            https://cs.android.com/android/platform/superproject/main/+/main:frameworks/native/cmds/dumpsys/dumpsys.cpp
            dumpsys.cpp -> [startDumpThread()]
                -> serviceManager.checkService(cpuinfo)             // the "cpuinfo" service / command should Resolve to the "activity" service
                    -> service.dump()                               //Start IPC to the Service to Dump the Info

                Register Service (cpuinfo) com.android.server.am.ActivityManagerService.java
                    -> public static void setSystemProcess()
                        -> ServiceManager.addService("cpuinfo", new CpuBinder(m));

                https://android.googlesource.com/platform/frameworks/base/+/refs/heads/froyo-release/services/java/com/android/server/am/ActivityManagerService.java
                https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java;l=531;drc=50be190e0dac036d9f3e4950b180f38ccc69fa34?q=ActivityManagerService&ss=android%2Fplatform%2Fsuperproject%2Fmain

                Command output you can use following Regex:
                    Java:(\\d+(\\.\\d+)?)% TOTAL: (\\d+(\\.\\d+)?)% user \\+ (\\d+(\\.\\d+)?)% kernel
                     Raw:(\d+(\.\d+)?)% TOTAL: (\d+(\.\d+)?)% user \+ (\d+(\.\d+)?)% kernel

                So (dumpsys cpuinfo) is great but no Point if we can just cut the Middle Man

              (top) Command seems to be loud, bit more resource intense, and has pretty low rep as far as reporting correct Overall CPU Usage
    */

    public static void TestCPUStatOne() {
        try {
            HiddenApiUtils.bypassHiddenApiRestrictions();

            Method mth = ReflectUtil.tryGetMethod(Process.class, "readProcFile", String.class, int[].class, String[].class, long[].class, float[].class);
            if(mth == null) {
                XLog.e(TAG, "Error Finding [readProcFile] Functions... (1)", false, true);
                mth = ReflectUtil.tryGetMethod(Process.class, "readProcFile");
                if(mth == null) {
                    XLog.e(TAG, "Error Finding [readProcFile] Functions... (2)", false, true);
                    return;
                } else {
                    XLog.i(TAG, "Found [readProcFile] (2) Function", true);
                }
            } else {
                XLog.i(TAG, "Found [readProcFile] (1) Function", true);
            }

            Field ff = ReflectUtil.tryGetField(Process.class, "PROC_OUT_LONG", true);

            XLog.i(TAG, "Did I get field [PROC_OUT_LONG] ? : " + (ff != null), true);

            long[] sysCpu = new long[7];
            //  if (Process.readProcFile("/proc/stat", SYSTEM_CPU_FORMAT,
            //                null, sysCpu, null))

            XLog.i(TAG, "Calling to the [readProcFile] Function....", true);
            Object ret = mth.invoke(null, "/proc/stat", SYSTEM_CPU_FORMAT, null, sysCpu, null);
            boolean result = (boolean)ret;
            XLog.i(TAG, "Called to [readProcFile] Result: " + result + " Now printing Values", true);
            StringBuilder sb = new StringBuilder();
            for(long i : sysCpu) {
                sb.append(i).append("\n");
            }

            XLog.i(TAG, "VALUES from [readProcFile]\n" + sb.toString(), true);

            XLog.i(TAG, "Now Invoking our Custom [readProcFile] Function...", true);
            long[] syss = new long[7];
            boolean resss = ProcessUtils.readProcFile("/proc/stat", ProcessApi.SYSTEM_CPU_FORMAT, null, syss, null);
            XLog.i(TAG, "Invoked our Custom [readProcFile] Result: " + resss + " now printing shit", true);
            StringBuilder sbb = new StringBuilder();
            for(long i : syss) {
                sbb.append(i).append("\n");
            }

            XLog.i(TAG, "Result from out Custom [readProcFile]: " + sbb.toString(), true);

        }catch (Exception e) {
            XLog.e(TAG, "Error TestCPUStatOne", true, true);
        }
    }
}
