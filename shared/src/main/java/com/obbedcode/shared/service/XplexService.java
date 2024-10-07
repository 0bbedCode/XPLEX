package com.obbedcode.shared.service;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.os.RemoteException;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.usage.MemoryApi;
import com.obbedcode.shared.usage.ProcessApi;
import com.obbedcode.shared.usage.RunningProcess;
import com.obbedcode.shared.usage.UsageUtils;
import com.obbedcode.shared.utils.PkgUtils;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.XSetting;
import com.obbedcode.shared.xplex.data.XStartupSetting;
import com.obbedcode.shared.xplex.database.XDatabaseManager;
import com.obbedcode.shared.xplex.database.XPrivacyControl;

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
    //public XDatabaseManager xpDatabase;

    public XplexService(IPackageManager pms) {
        packageManager = pms;
        //try {
            //xpDatabase = XDatabaseManager.instance;
        //}catch (Exception e) {
        //    XLog.e(TAG, "Eror ror internal", true, true);
        //}
        instance = this;
    }

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException { }

    @Override
    public String getLog() throws RemoteException {
        try {
            XLog.i(TAG, "Getting DB Man...", true);
            XDatabaseManager xpDatabase = XDatabaseManager.instance;
            XLog.i(TAG, " OPENED DB: " + xpDatabase.getDatabase().isOpen(true));

            XLog.i(TAG, " STARTING DB TESTS ");
            XLog.i(TAG, "[1] IS READY: " + xpDatabase.isDatabaseReady());
            XLog.i(TAG, "[2] DB INFO: " + xpDatabase.getDatabase().toString());
            XLog.i(TAG, "[3] HAS TABLE: " +  xpDatabase.getDatabase().hasTable(XStartupSetting.Table.NAME));
            XLog.i(TAG, "[4] PUT ONE: " + xpDatabase.putStartupSetting(0, "global", "test", 0, false));
            XLog.i(TAG, "[5] BACK ONE: " +  xpDatabase.getStartupSetting(0, "global", "test").toString());
            XLog.i(TAG, "[6] PUT TWO: " + xpDatabase.putStartupSetting(0, "global", "test2", 0, false));
            XLog.i(TAG, "[7] BACK TWO: " + xpDatabase.getStartupSetting(0, "global", "test2").toString());
            XLog.i(TAG, "[8] PUT THREE: " +  xpDatabase.putStartupSetting(0, "mod", "test2", 0, false));
            XLog.i(TAG, "[9] SIZE ONE: " +  xpDatabase.getStartupSettings(0, "global").size());
            XLog.i(TAG, "[10] SIZE TWO: " + xpDatabase.getStartupSettings(0, "mod").size());
            XLog.i(TAG, "[11] DELETE ONE: " + xpDatabase.putStartupSetting(0, "global", "test", null, true));
            XLog.i(TAG, "[12] SIZE THREE: " +  xpDatabase.getStartupSettings(0, "global").size());

            XLog.i(TAG, "[13] PUT FOUR: " + xpDatabase.putStartupSetting(0, "global", "test4", 0, false));
            XLog.i(TAG, "[14] PUT FIVE: " + xpDatabase.putStartupSetting(0, "global", "test5", 0, false));
            XLog.i(TAG, "[15] PUT SIZ: " + xpDatabase.putStartupSetting(0, "global", "test8", 0, false));


            XLog.i(TAG, "[16] SIZE FOUR: " +  xpDatabase.getStartupSettings(0, "global").size());


        }catch (Exception e) {
            XLog.e(TAG, "Error get log: " + e.getMessage(), true, true);
        }
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
    public double getOverallMemoryUsage() throws RemoteException { return UsageUtils.calculateMemoryUsage(MemoryApi.getMemoryInfoFromProc()); }

    @Override
    public ParceledListSlice<XApp> getInstalledAppsEx() throws RemoteException {
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
        }

        return new ParceledListSlice<>(apps);

    }

    @Override
    public ParceledListSlice<XAssignment> getAppAssignments(int userId, String category) {
        return new ParceledListSlice<>(XPrivacyControl.getAssignments(userId, category));
    }

    @Override
    public List<RunningProcess> getRunningProcesses() throws RemoteException {
        return RunningProcess.getRunningProcesses(true);
    }

    @Override
    public ParceledListSlice<XSetting> getAppSettings(int userId, String category) {
        return null;
    }
}
