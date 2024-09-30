package com.obbedcode.shared.pm;

import android.content.Context;
import android.content.pm.PackageManager;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.UiGlobals;
import com.obbedcode.shared.api.ApplicationApi;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.service.ParceledListSlice;
import com.obbedcode.shared.service.ServiceClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kotlin.Pair;

public class AppRepository {
    private static final String TAG = "ObbedCode.XP.AppRepository";

    private static final IXPService XP_SERVICE = ServiceClient.waitForService();

    public static List<XApp> getInstalledApps(Context context, boolean isSystem) {
        List<XApp> apps = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        try {
            ParceledListSlice<XApp> parceledApps = XP_SERVICE.getInstalledAppsEx();
            apps = parceledApps.getList();
            List<XApp> filtered = new ArrayList<>();
            for(XApp app : apps) {
                app.appName = pm.getApplicationLabel(app.info).toString();
                app.isEnabled = ApplicationApi.isEnabled(app.info, pm);
                app.isSystem = ApplicationApi.isSystemApplication(app.info);
                app.isPersistent = ApplicationApi.isPersistent(app.info);
                if(isSystem && app.isSystem)
                    filtered.add(app);
                else if(!isSystem && !app.isSystem)
                    filtered.add(app);
            }

            return filtered;
        }catch (Exception e) {
            XLog.e(TAG, "Error Getting Apps over IPC: " + e);
        }
        return apps;
    }

    public static List<XApp> getFilteredAndSortedApps(
            List<XApp> apps,
            Pair<String, List<String>> filter,
            String keyword,
            boolean isReverse) {
        if(apps.isEmpty()) return apps;
        Comparator<XApp> comparator = getComparator(filter.getFirst(), isReverse);
        List<XApp> filteredApps = new ArrayList<>();
        for (XApp app : apps) {
            if (isAppMatchingCriteria(app, keyword, filter.getSecond())) {
                filteredApps.add(app);
            }
        }

        Collections.sort(filteredApps, comparator);
        return filteredApps;
    }

    private static boolean isAppMatchingCriteria(XApp app, String keyword, List<String> filterCriteria) {
        boolean keywordMatch = keyword.isEmpty() ||
                app.appName.toLowerCase().contains(keyword.toLowerCase()) ||
                app.packageName.toLowerCase().contains(keyword.toLowerCase());

        if (!keywordMatch)
            return false;

        for (String criteria : filterCriteria) {
            switch (criteria) {
                case UiGlobals.FILTER_CONFIGURED: //已配置
                    return false;//For now
                    //if (!app.isEnabled) return false;   //This is wrong ??, yes
                    //break;
                case UiGlobals.FILTER_LAST_UPDATE:    //最近更新
                    if (System.currentTimeMillis() - app.lastUpdateTime >= 3 * 24 * 3600 * 1000L) return false;
                    break;
                case UiGlobals.FILTER_DISABLED:     //已禁用
                    if (app.isEnabled) return false;
                    break;
            }
        }

        return true;
    }

    public static Comparator<XApp> getComparator(String sortBy, boolean isReverse) {
        Comparator<XApp> comparator;
        switch (sortBy) {
            case UiGlobals.SORT_APP_SIZE:            //应用大小
                comparator = new Comparator<XApp>() {
                    @Override
                    public int compare(XApp a1, XApp a2) { return Long.compare(a1.size, a2.size); }
                };                break;
            case UiGlobals.FILTER_LAST_UPDATE:        //最近更新时间
                comparator = new Comparator<XApp>() {
                    @Override
                    public int compare(XApp a1, XApp a2) {
                        return Long.compare(a1.lastUpdateTime, a2.lastUpdateTime);
                    }
                };
                break;
            case UiGlobals.SORT_INSTALL_DATE:   //安装日期
                comparator = new Comparator<XApp>() {
                    @Override
                    public int compare(XApp a1, XApp a2) { return Long.compare(a1.firstInstallTime, a2.firstInstallTime); }
                };
                break;
            case UiGlobals.SORT_TARGET_SDK:          //Target 版本
                comparator = new Comparator<XApp>() {
                    @Override
                    public int compare(XApp a1, XApp a2) { return Integer.compare(a1.targetSdk, a2.targetSdk); }
                };
                break;
            default:
                comparator = new Comparator<XApp>() {
                    @Override
                    public int compare(XApp a1, XApp a2) { return String.CASE_INSENSITIVE_ORDER.compare(a1.appName, a2.appName); }
                };
                break;
        }

        if (isReverse) {
            final Comparator<XApp> finalComparator = comparator;
            comparator = new Comparator<XApp>() {
                @Override
                public int compare(XApp a1, XApp a2) { return finalComparator.compare(a2, a1); }
            };
        }

        return comparator;
    }
}
