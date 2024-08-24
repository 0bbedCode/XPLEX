package com.obbedcode.shared.usage;

import android.app.ActivityManager;

import com.obbedcode.shared.logger.XLog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsageUtils {
    private static final String TAG = "ObbedCode.XP.UsageUtils";

    public static final String PROC_STAT_FILE = "/proc/stat";
    public static final Pattern DUMPSYS_CPU_TOAL_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)% TOTAL: (\\d+(\\.\\d+)?)% user \\+ (\\d+(\\.\\d+)?)% kernel");

    /**
     * Calculates the total Memory Usage
     * <p>
     * Uses variables from /proc/meminfo of totalMemory and availMemory to calculate
     * </p>
     *
     * @param memInfo Structure for Memory Info holding Avail memory and Total Memory
     * @return The overall Memory Usage
     */
    public static double calculateMemoryUsage(ActivityManager.MemoryInfo memInfo) { return calculateMemoryUsage(memInfo.totalMem, memInfo.availMem); }

    /**
     * Calculates the total Memory Usage
     * <p>
     * Uses variables from /proc/meminfo of totalMemory and availMemory to calculate
     * </p>
     *
     * @param totalMemory Total Memory      Typically (KB)
     * @param availMemory Available memory  Typically (KB)
     * @return The overall Memory Usage
     */
    public static double calculateMemoryUsage(long totalMemory, long availMemory) {
        if(totalMemory == 0) return 0;
        long usedMemory = totalMemory - availMemory;
        return (usedMemory / (double)totalMemory) * 100.0;
    }

    /**
     * Parse CPU Total from dumpsys cpuinfo
     * <p>
     * This will parse either a line or all the output from the Command dumpsys cpuinfo
     * Parses it to specifically find the Total CPU usage
     * using dumpsys is not recommended to get the total cpu usage as you can cut the middle man out directly get it from the /proc/stat file as that's what dumpsys cpuinfo does
     * </p>
     *
     * @param dumpSysOutput    Can be the full output of the dumpsys cpuinfo command result or a line from that command result
     * @return Returns a double Percentage of the Total Overall CPU Usage 0-100%
     */
    public static double parseDumpsysCpuTotal(String dumpSysOutput) {
        if(dumpSysOutput != null) {
            try {
                Matcher matcher = DUMPSYS_CPU_TOAL_PATTERN.matcher(dumpSysOutput);
                if(matcher.find())
                    return Double.parseDouble(matcher.group(1));    //(1) as Regex on (0) is the whole matching string instead of the match it self
                                                                    //Can test it on:https://regex101.com/
                                                                    //Specific Line "3% TOTAL: 2.4% user + 0.3% kernel + 0% iowait + 0.1% irq + 0% softirq"
                                                                    //Raw Expression:(\d+(\.\d+)?)% TOTAL: (\d+(\.\d+)?)% user \+ (\d+(\.\d+)?)% kernel
                                                                    //Java Expression:(\\d+(\\.\\d+)?)% TOTAL: (\\d+(\\.\\d+)?)% user \\+ (\\d+(\\.\\d+)?)% kernel
                                                                    //Alternative Java Expression:(\\d+(\\.\\d+)?)% TOTAL:.*
            }catch (Exception e) {
                XLog.e(TAG, "Failed to Parse [dumpsys] cpuinfo Command output..." + e.getMessage(), true);
            }
        } return -1;
    }


    /**
     * Parses a CPU Line from the /proc/stat File
     * <p>
     * Parses total 7-10 Columns from the /proc/stat Line
     * user, nice, system, idle, iowait, irq, and softirq, steal, guest, guestnice
     * </p>
     *
     * @param line    The CPU line from the stat file, ensure it starts with 'cpu'
     * @param readAllColumns Read the rest of the Columns not needed for overall usage, rest of the columns being (steal, guest, guestnice)
     * @return The Parsed Numeric Values of the Selected Columns for the stat cpu line
     */
    public static long[] parseProcStatLine(String line, boolean readAllColumns) {
        long[] columns = new long[readAllColumns ? 10 : 7];
        if(line != null && line.startsWith("cpu")) {
            try {
                String[] tokens = line.split("\\s+");

                //Start at Index (1) as (0) is 'cpu' not a number from the column
                columns[0] = Long.parseLong(tokens[1]);         //User
                columns[1] = Long.parseLong(tokens[2]);         //Nice
                columns[2] = Long.parseLong(tokens[3]);         //System
                columns[3] = Long.parseLong(tokens[4]);         //Idle
                columns[4] = Long.parseLong(tokens[5]);         //iowait
                columns[5] = Long.parseLong(tokens[6]);         //irq
                columns[6] = Long.parseLong(tokens[7]);         //softirq
                if(readAllColumns) {
                    columns[7] = Long.parseLong(tokens[8]);     //Steal
                    columns[8] = Long.parseLong(tokens[9]);     //Guest
                    columns[9] = Long.parseLong(tokens[10]);    //GuestNice
                }

            }catch (Exception e) {
                XLog.e(TAG, "Failed to Parse line from (/proc/stat) File");
            }
        }
        return columns;
    }

    /**
     * Calculates the overall CPU usage percentage based on CPU time values.
     * <p>
     * The calculation is based on the time spent in various CPU states, including
     * user, nice, system, idle, iowait, irq, and softirq.
     * </p>
     *
     * @param user    The time the CPU has spent in user mode.
     * @param nice    The time the CPU has spent in user mode with low priority (nice).
     * @param system  The time the CPU has spent in system (kernel) mode.
     * @param idle    The time the CPU has spent idle.
     * @param iowait  The time the CPU has spent waiting for I/O to complete.
     * @param irq     The time the CPU has spent servicing interrupts.
     * @param softirq The time the CPU has spent servicing soft interrupts.
     * @return The CPU usage as a percentage (0-100%).
     */
    public static double calculateCpuUsage(long user, long nice, long system, long idle, long iowait, long irq, long softirq) {
        // Calculate total CPU time
        long totalCpuTime = user + nice + system + idle + iowait + irq + softirq;

        // Calculate total idle time
        long totalIdleTime = idle + iowait;

        // Calculate CPU usage as a percentage
        long totalUsageTime = totalCpuTime - totalIdleTime;
        return (double) totalUsageTime / totalCpuTime * 100.0;
    }
}
