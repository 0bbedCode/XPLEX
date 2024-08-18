package com.obbedcode.shared.usage;

import android.os.Process;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.DynamicField;
import com.obbedcode.shared.reflect.DynamicMethod;
import com.obbedcode.shared.utils.HiddenApiUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ProcessApi {
    private static final String TAG = "ObbedCode.XP.ProcessApi";
    private static final DynamicMethod internalReadProcFile = new DynamicMethod(Process.class, "readProcFile", String.class, int[].class, String[].class, long[].class, float[].class).setAccessible(true);
    private static final DynamicMethod internalGetPIDs = new DynamicMethod(Process.class, "getPids", String.class, int[].class).setAccessible(true);

    //https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/os/Process.java
    //https://android.googlesource.com/platform/frameworks/base/+/android-6.0.0_r1/core/java/android/os/Process.java
    //https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/com/android/internal/os/ProcessCpuTracker.java

    // public static final native int[] getPids(String path, int[] lastArray);

    //public static final int PROC_SPACE_TERM = 32;   // ASCII for space ' '
    //public static int PROC_COMBINE = 256;     // 0x100
    //public static int PROC_OUT_LONG = 8192;   // 0x2000
    public static final int PROC_TERM_MASK = useFieldValueOr("PROC_TERM_MASK", 0xff);
    public static final int PROC_ZERO_TERM = useFieldValueOr("PROC_ZERO_TERM", 0);
    public static final int PROC_SPACE_TERM = (int)' ';
    public static final int PROC_TAB_TERM = (int)'\t';
    public static final int PROC_COMBINE = useFieldValueOr("PROC_COMBINE", 0x100);
    public static final int PROC_PARENS = useFieldValueOr("PROC_PARENS", 0x200);
    public static final int PROC_QUOTES = useFieldValueOr("PROC_QUOTES", 0x400);
    public static final int PROC_OUT_STRING = useFieldValueOr("PROC_OUT_STRING", 0x1000);
    public static final int PROC_OUT_LONG = useFieldValueOr("PROC_OUT_LONG", 0x2000);
    public static final int PROC_OUT_FLOAT = useFieldValueOr("PROC_OUT_FLOAT", 0x4000);

    public static int[] SYSTEM_CPU_FORMAT = new int[] {
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

    /**
     * Simple method to Read from the /proc/stat File to then parse the Data for overall CPU Usage
     *
     * @return Returns -1 if failed else the Total Overall CPU Usage on a 0-100 scale
     */
    public static double getOverallCpuUsage() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream("/proc/stat")))) {

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
        if(internalReadProcFile.isValid()) {
            if(Boolean.TRUE.equals(internalReadProcFile.tryStaticInvoke(filePath, format, outStrings, outLongs, outFloats)));
               return true;
        } return ProcessUtils.readProcFile(filePath, format, outStrings, outLongs, outFloats);
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

    private static int useFieldValueOr(String fieldName, int defaultValue) {
        DynamicField field = new DynamicField(Process.class, fieldName).setAccessible(true);
        if(!field.isValid()) return defaultValue;
        Object val = field.tryGetValueStatic();
        if(val == null) return defaultValue;
        try {
            if(val instanceof Integer)
                return (int)val;
        }catch (ClassCastException ignored) { }
        return defaultValue;
    }
}
