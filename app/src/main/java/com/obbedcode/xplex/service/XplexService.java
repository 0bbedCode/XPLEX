package com.obbedcode.xplex.service;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.os.RemoteException;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.IXplexService;
import com.obbedcode.shared.api.XposedApi;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.usage.MemoryApi;
import com.obbedcode.shared.usage.ProcessApi;
import com.obbedcode.shared.usage.RunningProcess;
import com.obbedcode.shared.usage.UsageUtils;
import com.obbedcode.shared.utils.PkgUtils;
import com.obbedcode.shared.utils.RuntimeUtils;
import com.obbedcode.xplex.hook.TestSite;

import java.util.ArrayList;
import java.util.List;

//Now this interface when given is safe no need to caller UID checks
public class XplexService extends IXPService.Stub {
    private static final String TAG = "ObbedCode.XP.XplexService";

    public static XplexService instance = null;
    public static void bind(IPackageManager pms) {
        if(instance == null && pms != null) {
            new XplexService(pms);
        }
    }

    public IPackageManager packageManager;
    public XplexService(IPackageManager pms) {
        packageManager = pms;
        instance = this;
    }

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException { }

    @Override
    public String getLog() throws RemoteException {
        //TestSite.TestCPUStatOne();
        return null;
    }

    @Override
    public double getOverallCpuUsage() throws RemoteException {
        long[] vals = new long[7];
        if(ProcessApi.readProcFile(UsageUtils.PROC_STAT_FILE, ProcessApi.SYSTEM_CPU_FORMAT, null, vals, null)) {
            return UsageUtils.calculateCpuUsage(
                    vals[0],
                    vals[1],
                    vals[2],
                    vals[3],
                    vals[4],
                    vals[5],
                    vals[6]);
        }
        return 0;
    }

    @Override
    public double getOverallMemoryUsage() throws RemoteException { return UsageUtils.calculateMemoryUsage(MemoryApi.getMemoryInfoFromService()); }

    @Override
    public List<XApp> getInstalledAppsEx() throws RemoteException {
        XLog.i(TAG, "zer0def for TaiChi Support");
        List<XApp> apps = new ArrayList<>();
        try {
            List<ApplicationInfo> pkgInfos = PkgUtils.getInstalledApplicationsCompat(packageManager,0, 0);
            for(ApplicationInfo ai : pkgInfos) {
                XApp app = new XApp(ai, packageManager);
                apps.add(app);
            }
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Get a List of Installed Apps on the Device, Service Error: " + e.getMessage(), true);
        } return apps;
    }

    @Override
    public List<RunningProcess> getRunningProcesses() throws RemoteException {
        return RunningProcess.getRunningProcesses(true);
    }
}
