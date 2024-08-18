package com.obbedcode.shared.usage;

import android.app.ActivityManager;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.DynamicMethod;
import com.obbedcode.shared.utils.HiddenApiUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MemoryApi {
    private static final String TAG = "ObbedCode.XP.MemoryApi";

    private static Object amService = null;
    private static DynamicMethod amGetMemoryInfo = null;

    private static Object getAmService() {
        if(amService == null) {
            amService = HiddenApiUtils.getIActivityManager();
            if(amService != null) {
                amGetMemoryInfo = new DynamicMethod(amService.getClass(), "")
            }
        }

        return amService;
    }

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
        boolean foundTotal = false;
        boolean foundFree = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"))) {
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String name = parts[0];
                long size = Long.parseLong(parts[1]) * 1024;  // Convert from kB to bytes
                switch (name) {
                    case "MemTotal:":
                        memoryInfo.totalMem = size;
                        foundTotal = true;
                        break;
                    case "MemFree:":
                    case "Buffers:":
                    case "Cached:":
                        memoryInfo.availMem += size;
                        foundFree = true;
                        break;
                    //case "SwapTotal:":
                    //    memoryInfo.totalSwap = size;
                    //    break;
                    //case "SwapFree:":
                    //    memoryInfo.freeSwap = size;
                    //    break;
                }

                if(foundTotal && foundFree)
                    break;
            }

            // Optionally set other fields like threshold or lowMemory
            //memoryInfo.threshold = memoryInfo.totalMem / 10;  // Set an example threshold
            //memoryInfo.lowMemory = memoryInfo.availMem < memoryInfo.threshold;
            //Finish rest if wanted

        } catch (IOException e) { XLog.e(TAG, "Error reading the Memory File [/proc/meminfo]"); }
        return memoryInfo;
    }

    public static ActivityManager.MemoryInfo getMemoryInfoFromService() {
        Object amService = getAmService();

    }
}
