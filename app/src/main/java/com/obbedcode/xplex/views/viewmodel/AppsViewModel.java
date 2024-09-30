package com.obbedcode.xplex.views.viewmodel;

import android.app.Application;

import com.obbedcode.shared.PrefManager;
import com.obbedcode.shared.UiGlobals;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.pm.AppRepository;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;
import kotlin.Triple;

public class AppsViewModel extends BaseViewModel<XApp> {

    public AppsViewModel(Application application) {
        super(application, UiGlobals.TAB_USER);
    }

    @Override
    public List<String> getFilterList() {
        List<String> filterList = new ArrayList<>();
        if (PrefManager.isConfigured()) filterList.add(UiGlobals.FILTER_CONFIGURED);
        if (PrefManager.isUpdated()) filterList.add(UiGlobals.FILTER_LAST_UPDATE);
        if (PrefManager.isDisabled()) filterList.add(UiGlobals.FILTER_DISABLED);
        return filterList;
    }

    @Override
    protected List<XApp> filterData(Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long> params, Application application) {
        Pair<String, List<String>> filter = params.getFirst();
        Pair<String, Boolean> searchParams = params.getSecond();
        boolean isSystem = type.equalsIgnoreCase(UiGlobals.TAB_SYSTEM);
        //make sure because system apps can be "configured"
        List<XApp> apps = AppRepository.getInstalledApps(application, isSystem);
        if (UiGlobals.FILTER_CONFIGURED.equalsIgnoreCase(type)) {
            List<XApp> configuredApps = new ArrayList<>();
            for (XApp app : apps) {
                //if (app.isEnabled == 1) {
                //    configuredApps.add(app);
                //}
            }
            apps = configuredApps;
        }

        return AppRepository.getFilteredAndSortedApps(
                apps,
                filter,
                searchParams.getFirst(),
                searchParams.getSecond()
        );
    }
}
