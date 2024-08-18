package com.obbedcode.shared.usage;

import com.obbedcode.shared.IXPService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsageMonitorUser {
    public interface IUsageUpdate {
        void onCpuPercentageOverallUsageUpdate(float update);
        void onMemoryPercentageOverallUsageUpdate(float update);
    }

    private boolean mRunningCpuMonitor = false;
    private boolean mRunningMemoryMonitor = false;
    private IXPService mService = null;

    public ExecutorService runningThreads = Executors.newFixedThreadPool(5);

    public UsageMonitorUser(IXPService service) {

    }

    public void startMemoryMonitor() {

    }
}
