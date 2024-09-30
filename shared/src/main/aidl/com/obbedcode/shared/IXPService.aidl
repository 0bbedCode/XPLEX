// IXPService.aidl
package com.obbedcode.shared;

// Declare any non-default types here with import statements
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.data.XApp;

import com.obbedcode.shared.xplex.data.XSetting;

import com.obbedcode.shared.usage.RunningProcess;
import com.obbedcode.shared.service.ParceledListSlice;

interface IXPService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    String getLog();

    ParceledListSlice<XApp> getInstalledAppsEx();

    double getOverallCpuUsage();
    double getOverallMemoryUsage();

    List<RunningProcess> getRunningProcesses();

    List<XSetting> getAppHookSettings(int userId, String category);

    //How can I add to my interface a more advance type like
    //List<XApp> XApp being (com.obbedcode.shared.data.XApp)
    //List<XApp> getInstalledAppsEx();
}