package com.obbedcode.shared.usage;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.os.Build;
import android.os.Process;

import com.obbedcode.shared.GhostCallerUid;
import com.obbedcode.shared.io.FileApi;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.DynamicMethod;
import com.obbedcode.shared.reflect.ReflectUtil;
import com.obbedcode.shared.reflect.HiddenApiUtils;
import com.obbedcode.shared.reflect.ServicesGlobal;
import com.obbedcode.shared.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import rikka.hidden.compat.PackageManagerApis;

public class ProcessApi {
    private static final String TAG = "ObbedCode.XP.ProcessApi";

    /*Directory to Store CPU information including Cores*/
    public static final String DIRECTORY_CPU = FileApi.buildPath("sys", "devices", "system", "cpu");

    //https://android.googlesource.com/kernel/msm/+/android-msm-bullhead-3.10-marshmallow-dr/Documentation/ABI/testing/sysfs-devices-system-cpu

    /*What cores exist on the Hardware offline or online does not matter*/
    public static final String FILE_CPU_CORE_PRESENT = FileApi.buildPath("sys", "devices", "system", "cpu", "present");

    /*Possible Limit for the Hardware to Handle*/
    public static final String FILE_CPU_CORE_POSSIBLE = FileApi.buildPath("sys", "devices", "system", "cpu", "possible");

    /*Cores offline*/
    public static final String FILE_CPU_CORE_OFFLINE = FileApi.buildPath("sys", "devices", "system", "cpu", "offline");

    /*Cores Online*/
    public static final String FILE_CPU_CORE_ONLINE = FileApi.buildPath("sys", "devices", "system", "cpu", "online");

    /*Max amount of Cores the Kernel Can Handle this one format is not in 0-X like the rest but more so X*/
    public static final String FILE_CPU_KERNEL_MAX = FileApi.buildPath("sys", "devices", "system", "cpu", "kernel_max");

    public static final String FILE_SELF_CMDLINE = FileApi.buildPath("proc", "self", "cmdline");

    public static final String FILE_CPU_STAT = FileApi.buildPath("proc", "stat");

    private static final DynamicMethod internalReadProcFile = new DynamicMethod(Process.class, "readProcFile", String.class, int[].class, String[].class, long[].class, float[].class).setAccessible(true);
    private static final DynamicMethod internalGetPIDs = new DynamicMethod(Process.class, "getPids", String.class, int[].class).setAccessible(true);

    private static final DynamicMethod internalGetRunningAppProcesses
            = new DynamicMethod(ServicesGlobal.getIActivityManager().getClass(), "getRunningAppProcesses").bindInstance(ServicesGlobal.getIActivityManager());

    //https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/os/Process.java
    //https://android.googlesource.com/platform/frameworks/base/+/android-6.0.0_r1/core/java/android/os/Process.java
    //https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/com/android/internal/os/ProcessCpuTracker.java

    public static final int PROC_TERM_MASK = ReflectUtil.useFieldValueOrDefaultInt(Process.class,"PROC_TERM_MASK", 0xff);
    public static final int PROC_ZERO_TERM =  ReflectUtil.useFieldValueOrDefaultInt(Process.class,"PROC_ZERO_TERM", 0);
    public static final int PROC_SPACE_TERM = (int)' '; //32
    public static final int PROC_TAB_TERM = (int)'\t';
    public static final int PROC_COMBINE =  ReflectUtil.useFieldValueOrDefaultInt(Process.class,"PROC_COMBINE", 0x100);
    public static final int PROC_PARENS =  ReflectUtil.useFieldValueOrDefaultInt(Process.class,"PROC_PARENS", 0x200);
    public static final int PROC_QUOTES =  ReflectUtil.useFieldValueOrDefaultInt(Process.class,"PROC_QUOTES", 0x400);
    public static final int PROC_OUT_STRING =  ReflectUtil.useFieldValueOrDefaultInt(Process.class, "PROC_OUT_STRING", 0x1000);
    public static final int PROC_OUT_LONG =  ReflectUtil.useFieldValueOrDefaultInt(Process.class,"PROC_OUT_LONG", 0x2000);
    public static final int PROC_OUT_FLOAT =  ReflectUtil.useFieldValueOrDefaultInt(Process.class,"PROC_OUT_FLOAT", 0x4000);

    public static final int[] SYSTEM_CPU_FORMAT = new int[] {
            PROC_SPACE_TERM | PROC_COMBINE,          // Combine multiple spaces
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 1: user time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 2: nice time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 3: system time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 4: idle time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 5: iowait time
            PROC_SPACE_TERM | PROC_OUT_LONG,         // 6: irq time
            PROC_SPACE_TERM | PROC_OUT_LONG          // 7: softirq time
    };

    static { HiddenApiUtils.bypassHiddenApiRestrictions(); }

    public static boolean isSystemUID() { return isSystemUID(Process.myUid()); }
    public static boolean isSystemUID(int uid) { return uid == Process.SYSTEM_UID; }

    /**
     * Gets the Current Process Package Name
     *
     * @return return Package Name of the Current Process
     */
    public static String getSelfPackageName() {
        try {
            return ActivityThread.currentActivityThread().getApplication().getPackageName();
        }catch (Exception e) {
            XLog.e(TAG, "Failed Getting Package Name, Error: " + e.getMessage(), true);
            List<String> packages = PackageManagerApis.getPackagesForUidNoThrow(Process.myUid());
            return packages.get(0);
        }
    }

    /**
     * Gets the Current Process Name
     *
     * @return return Process Name of Current
     */
    public static String getSelfProcessName() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Process.myProcessName();
        } else {
            BufferedReader fileReader = null;
            try {
                fileReader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(FILE_SELF_CMDLINE), "iso-8859-1"));

                int c;
                StringBuilder processName = new StringBuilder();
                while ((c = fileReader.read()) > 0)
                    processName.append((char) c);

                return processName.toString();
            }catch (Exception e) {
                XLog.e(TAG, "Failed to Read /proc/self/cmdline File for Process Name, Error: " + e.getMessage(), true);
            } finally {
                StreamUtils.close(fileReader);
            }
        } return getSelfPackageName();
    }

    /**
     * Retrieves the number of physically present CPU cores in the system.
     * This reflects the CPUs that are currently present, regardless of whether they are online or offline.
     *
     * @return The number of present CPU cores, or -1 if an error occurs.
     */
    public static int getPresentCores() { return ProcessUtils.parseCpuFile(FILE_CPU_CORE_PRESENT); }

    /**
     * Retrieves the number of possible CPU cores in the system.
     * This indicates the maximum number of CPUs that could potentially be managed by the system, even if some are not currently available.
     *
     * @return The number of possible CPU cores, or -1 if an error occurs.
     */
    public static int getPossibleCores() { return ProcessUtils.parseCpuFile(FILE_CPU_CORE_POSSIBLE); }

    /**
     * Retrieves the maximum CPU index that the kernel can manage.
     * This provides the upper limit of CPU cores that the kernel is capable of handling.
     *
     * @return The maximum CPU index (0-based), or -1 if an error occurs.
     */
    public static int getKernelMaxCores() { return ProcessUtils.parseCpuFile(FILE_CPU_KERNEL_MAX); }

    /**
     * Retrieves the number of offline CPU cores in the system.
     * Offline cores are those that are present but not currently active or available for use.
     *
     * @return The number of offline CPU cores, or -1 if an error occurs.
     */
    public static int getOfflineCores() { return ProcessUtils.parseCpuFile(FILE_CPU_CORE_OFFLINE); }

    /**
     * Retrieves the number of online CPU cores in the system.
     * Online cores are those that are currently active and available for use.
     *
     * @return The number of online CPU cores, or -1 if an error occurs.
     */
    public static int getOnlineCores() { return ProcessUtils.parseCpuFile(FILE_CPU_CORE_ONLINE); }

    /**
     * Calculates the total number of CPU cores by counting the directories
     * in /sys/devices/system/cpu/ that match the pattern "cpu[0-9]+".
     *
     * @return The total number of CPU cores, or the number of available processors as a fallback.
     */
    public static int getCoreCount() {
        try {
            File dir = new File(DIRECTORY_CPU);
            File[] files = dir.listFiles((file, name) -> name.matches("cpu[0-9]+"));
            if(files != null) return files.length;
        } catch (Exception ignored) { }
        int pres = getPresentCores();
        return pres == -1 ? Runtime.getRuntime().availableProcessors() : pres;
        //availableProcessors uses "(int) Libcore.os.sysconf(_SC_NPROCESSORS_CONF);"
    }

    /**
     * Simple method to Read from the /proc/stat File to then parse the Data for overall CPU Usage
     *
     * @return Returns -1 if failed else the Total Overall CPU Usage on a 0-100 scale
     */
    public static double getOverallCpuUsage() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(FILE_CPU_STAT)))) {
            String line = reader.readLine(); // Read the first line, which represents overall CPU stats
            if (line != null && line.startsWith("cpu")) {
                String[] tokens = line.split("\\s+");
                // Extract the values from the line (user, nice, system, idle, iowait, irq, softirq)
                long user = Long.parseLong(tokens[1]);
                long nice = Long.parseLong(tokens[2]);
                long system = Long.parseLong(tokens[3]);
                long idle = Long.parseLong(tokens[4]);
                long iowait = Long.parseLong(tokens[5]);
                long irq = Long.parseLong(tokens[6]);
                long softirq = Long.parseLong(tokens[7]);
                // Calculate CPU usage using the helper function
                return UsageUtils.calculateCpuUsage(user, nice, system, idle, iowait, irq, softirq);
            }
        } catch (Exception ex) {
            XLog.e(TAG, "Failed to Read and Parse /proc/stat File, Error: " + ex.getMessage());
        } return -1; // Return -1 in case of an error
    }

    /**
     * Reads and parses a proc file given the file path, format, and output arrays.
     * The difference from this and the one provided in ProcUtils is that this one Calls to the (android.os.Process) one First, if Fails then Calls to the Custom Java Implementation
     *
     * @param filePath   The path of the proc file to read.
     * @param format     The format array defining how the file should be parsed.
     * @param outStrings Array to hold output strings (optional).
     * @param outLongs   Array to hold output long values (optional).
     * @param outFloats  Array to hold output float values (optional).
     * @return true if the file was read and parsed successfully, false otherwise.
     */
    public static boolean readProcFile(String filePath, int[] format, String[] outStrings, long[] outLongs, float[] outFloats) {
        return internalReadProcFile.isValid() && Boolean.TRUE.equals(internalReadProcFile.tryStaticInvoke(filePath, format, outStrings, outLongs, outFloats))
                || ProcessUtils.readProcFile(filePath, format, outStrings, outLongs, outFloats);
    }

    /**
     * Get the list of PIDs from the specified path.
     * The difference from this and the one provided in ProcUtils is that this one Calls to the (android.os.Process) one First, if Fails then Calls to the Custom Java Implementation
     *
     * @param path      The path to the directory containing PIDs (e.g., "/proc").
     * @param lastArray The previous array of PIDs (optional).
     * @return The updated array of PIDs.
     */
    public static int[] getPids(String path, int[] lastArray) {
        if(internalGetPIDs.isValid()) {
            Object ret = internalGetPIDs.tryInstanceInvoke(path, lastArray);
            if(ret != null)
                return (int[])ret;
        } return ProcessUtils.getPids(path, lastArray);
    }

    /**
     * Return a list using the ActivityManager Api of Running Applications
     *
     * @return List of ActivityManager.RunningAppProcessInfo
     */
    public static List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses() {
        return GhostCallerUid.invokeCallable(() ->
                internalGetRunningAppProcesses.tryInstanceInvoke());
    }
}
