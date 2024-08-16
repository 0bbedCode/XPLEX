package com.obbedcode.shared.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Process;
import android.util.Log;

import com.obbedcode.shared.logger.XLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UsageUtils {
    private static final String TAG = "ObbedCode.XP.UsageUtils";


    //Service API IXplexService

    //getCpuUsage() //One like this can use multiple methods but similar to Dep Injection inject what can be used to be needed
    //IsRoot then use Root Method, has XPrivacyLua then do so
    //getRamUsage()

    public static double getMemoryUsage(Context context) {
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            double availableMegs = mi.availMem / 0x100000L;
            //Percentage can be calculated for API 16+
            double percentAvail = mi.availMem / (double)mi.totalMem * 100.0;
            return percentAvail;
        }catch (Exception e) {
            XLog.e(TAG, "[getMemoryUsage] Error: " + e);
            return 0;
        }
    }

    public static long previousCpuTime = 0;
    public static long previousAppCpuTime = 0;


    public static double getOverallCpuUsage() {
        int numCores = Runtime.getRuntime().availableProcessors();
        double totalUsage = 0;
        int coreCount = 0;

        for (int i = 0; i < numCores; i++) {
            try {
                double currentFreq = getCoreFrequency(i);
                double maxFreq = getCoreMaxFrequency(i);
                if (maxFreq != 0) {
                    double usage = (currentFreq / maxFreq) * 100;
                    totalUsage += usage;
                    coreCount++;
                    String finalUsage = String.format("CPU Core %d Usage: %.2f%%", i, usage);
                    Log.d(TAG, finalUsage);
                } else {
                    Log.d(TAG, "Unable to read max frequency for core " + i);
                }
            } catch (IOException ex) {
                Log.d(TAG, "Core " + i + " is idle or stopped.", ex);
            }
        }

        if (coreCount > 0) {
            return totalUsage / coreCount;
        } else {
            return 0;
        }
    }

    private static double getCoreFrequency(int coreIndex) throws IOException {
        RandomAccessFile readerCurFreq = new RandomAccessFile("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/scaling_cur_freq", "r");
        String curFreq = readerCurFreq.readLine();
        double currentFreq = Double.parseDouble(curFreq) / 1000; // Convert from kHz to MHz
        readerCurFreq.close();
        return currentFreq;
    }

    private static double getCoreMaxFrequency(int coreIndex) throws IOException {
        RandomAccessFile readerMaxFreq = new RandomAccessFile("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/cpuinfo_max_freq", "r");
        String maxFreq = readerMaxFreq.readLine();
        double maximumFreq = Double.parseDouble(maxFreq) / 1000; // Convert from kHz to MHz
        readerMaxFreq.close();
        return maximumFreq;
    }


    public static float getCpuUsage() throws IOException {
        long cpuTime = getTotalCpuTime();
        long appTime = getAppCpuTime();

        float cpuUsage = 0;
        if (previousCpuTime > 0 && previousAppCpuTime > 0) {
            long cpuTimeDiff = cpuTime - previousCpuTime;
            long appTimeDiff = appTime - previousAppCpuTime;
            cpuUsage = 100f * appTimeDiff / cpuTimeDiff;
        }

        previousCpuTime = cpuTime;
        previousAppCpuTime = appTime;

        return cpuUsage;
    }

    private static long getTotalCpuTime() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] cpuData = line.split("\\s+");
                long totalCpuTime = 0;
                for (int i = 1; i < cpuData.length; i++) {
                    totalCpuTime += Long.parseLong(cpuData[i]);
                }
                return totalCpuTime;
            }
        }
        return 0;
    }

    private static long getAppCpuTime() throws IOException {
        int pid = Process.myPid();
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/" + pid + "/stat"))) {
            String line = reader.readLine();
            if (line != null) {
                String[] procData = line.split("\\s+");
                return Long.parseLong(procData[13]) + Long.parseLong(procData[14]);
            }
        }
        return 0;
    }
}
