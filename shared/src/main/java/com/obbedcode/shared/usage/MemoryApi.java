package com.obbedcode.shared.usage;

import android.app.ActivityManager;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.DynamicMethod;
import com.obbedcode.shared.reflect.ServicesGlobal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryApi {
    private static final String TAG = "ObbedCode.XP.MemoryApi";

    private static final DynamicMethod internalGetMemoryInfo
            = new DynamicMethod(ServicesGlobal.getIActivityManager().getClass(), "getMemoryInfo", ActivityManager.MemoryInfo.class)
            .bindInstance(ServicesGlobal.getIActivityManager());

    /**
     * Retrieves the memory information of the system by reading from the /proc/meminfo file.
     * This method populates the ActivityManager.MemoryInfo structure, which includes details
     * such as total memory, available memory, and swap memory.
     *
     * <p>It reads and parses the memory-related values from the /proc/meminfo file and maps them
     * into the corresponding fields in the MemoryInfo structure. The method uses a switch statement
     * to efficiently assign values to the appropriate fields.
     *
     * @return ActivityManager.MemoryInfo populated with memory statistics from /proc/meminfo.
     *         Returns a MemoryInfo structure with default values in case of an error.
     *
     * @see android.app.ActivityManager.MemoryInfo
     */
    public static ActivityManager.MemoryInfo getMemoryInfoFromProc() {
        //Using this method would be faster in theory than ActivityManager "getMemoryInfo" as that will invoke a lot more code, IPC Call, to then eventually read from /proc/meminfo
        /*
            Original trace from [getMemoryInfo]

            [android.app.ActivityManager][getMemoryInfo]
                :https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/app/ActivityManager.java

            [IPC]

            [com.android.server.am.ActivityManagerService][getMemoryInfo]
                :https://cs.android.com/android/platform/superproject/+/android14-qpr3-release:frameworks/base/services/core/java/com/android/server/am/ActivityManagerService.java

            [com.android.server.am.ProcessList][getMemoryInfo]
                :https://cs.android.com/android/platform/superproject/+/android14-qpr3-release:frameworks/base/services/core/java/com/android/server/am/ProcessList.java

            [android_util_Process.cpp][android_os_Process_getFreeMemory(JNIEnv* env, jobject clazz)]
                :https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/jni/android_util_Process.cpp;

            [sysmeminfo.h][ReadMemInfo]
                :https://cs.android.com/android/platform/superproject/main/+/main:system/memory/libmeminfo/include/meminfo/sysmeminfo.h

            [sysmeninfo.cpp][ReadMemInfo]
                :https://cs.android.com/android/platform/superproject/main/+/main:system/memory/libmeminfo/sysmeminfo.cpp;
         */

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        String line;
        boolean gotTotal = false;
        boolean gotFree = false;
        boolean gotCached = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"))) {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String name = parts[0];
                //* 1024 to Convert to MB
                //NDK Specifies MemFree and Cached Tags only for [android_util_Process.cpp][android_os_Process_getFreeMemory]
                //We can possibly also use "Buffers:" and "SwapFree:"
                //https://cs.android.com/android/platform/superproject/+/android14-qpr3-release:frameworks/base/core/jni/android_util_Process.cpp;l=657?q=getFreeMemory
                switch (name) {
                    case "MemTotal:":
                        memoryInfo.totalMem = Long.parseLong(parts[1]) * 1024;
                        gotTotal = true;
                        break;
                    case "MemFree:":
                        memoryInfo.availMem += Long.parseLong(parts[1]) * 1024;
                        gotFree = true;
                        break;
                    case "Cached:":
                        memoryInfo.availMem += Long.parseLong(parts[1]) * 1024;
                        gotCached = true;
                        break;
                }

                if(gotTotal && gotFree && gotCached)
                    break;
            }

            //This crap is optional
            //memoryInfo.threshold = (long) (memoryInfo.totalMem * 0.15);
            //memoryInfo.lowMemory = memoryInfo.availMem < memoryInfo.threshold;
        } catch (IOException e) { XLog.e(TAG, "Error reading the Memory File [/proc/meminfo]"); }
        return memoryInfo;
    }

    /**
     * Using the Activity Manager Service return a Instance of MemoryInfo
     *
     * <p> Do note while this returns a Structure for MemoryInfo its best to direct read from the File as that's what happens in the end /proc/meminfo
     * Using getMemoryInfoFromProc() will directly read from the meminfo file
     *
     * @return ActivityManager.MemoryInfo populated with memory statistics from ActivityManager.getMemoryInfo
     *         Returns a MemoryInfo structure with default values in case of an error.
     *
     * @see android.app.ActivityManager.MemoryInfo
     */
    public static ActivityManager.MemoryInfo getMemoryInfoFromService() {
        ActivityManager.MemoryInfo avm = new ActivityManager.MemoryInfo();
        internalGetMemoryInfo.tryInstanceInvoke(avm);
        return avm;
    }
}
