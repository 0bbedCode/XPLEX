package com.obbedcode.xplex.views.viewmodel.apps;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.PrefManager;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.pm.AppRepository;
import com.obbedcode.shared.service.ServiceClient;
import com.obbedcode.shared.service.UserService;
import com.obbedcode.shared.service.XplexService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import kotlin.Pair;
import kotlin.Triple;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;

public class AppViewModel extends AndroidViewModel {
    private static final String TAG = "ObbedCode.XP.AppViewModel";


    private String type = "user";
    //private final AtomicReference<Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long>> updateParams;
    private MutableLiveData<Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long>> updateParams;
    private LiveData<List<XApp>> appsLiveData;
    //private final MutableLiveData<List<XApp>> appsLiveData;
    //private final ScheduledExecutorService executor;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Executor backgroundExecutor = Executors.newSingleThreadExecutor();


    public AppViewModel(Application application) {
        super(application);
        XLog.i(TAG, "[init] begin");
        //this.updateParams = new AtomicReference<>(new Triple<>(new Pair<>("", new ArrayList<>()), new Pair<>("", false), 0L));
        this.updateParams = new MutableLiveData<>(new Triple<>(new Pair<>("", new ArrayList<>()), new Pair<>("", false), 0L));

        //this.appsLiveData = new MutableLiveData<>();
        //this.executor = Executors.newSingleThreadScheduledExecutor();

        XLog.i(TAG, "[init]....");
        //setupAppsLiveData();
        this.appsLiveData = setupAppsLiveData(application.getBaseContext());

        XLog.i(TAG, "[init] Setup Apps Live Data finished: Live Data Size: " + this.appsLiveData.isInitialized());
        refreshApps();
    }

    public void setType(String type) {
        this.type = type;
    }

    public LiveData<List<XApp>> getAppsLiveData() {
        return appsLiveData;
    }

    public void refreshApps() {
        updateParams.setValue(new Triple<>(
                new Pair<>(PrefManager.order(), getFilterList()),
                new Pair<>("", PrefManager.isReverse()),
                System.currentTimeMillis()
        ));
        //updateParams.set(new Triple<>(
        //        new Pair<>(PrefManager.order(), getFilterList()),
        //        new Pair<>("", PrefManager.isReverse()),
        //        System.currentTimeMillis()
        //));
    }

    public void updateList(Pair<String, List<String>> filter, String keyword, boolean isReversed) {
        //updateParams.set(new Triple<>(
        //        filter,
        //        new Pair<>(keyword, isReversed),
        //        System.currentTimeMillis()
        //));

        updateParams.setValue(new Triple<>(
                filter,
                new Pair<>(keyword, isReversed),
                System.currentTimeMillis()
        ));
    }

    private List<String> getFilterList() {
        List<String> filterList = new ArrayList<>();
        if (PrefManager.isConfigured())
            filterList.add("Configured");
        if (PrefManager.isUpdated())
            filterList.add("Last Update");
        if (PrefManager.isDisabled())
            filterList.add("Disabled");
        return filterList;
    }

    private LiveData<List<XApp>> setupAppsLiveData(final Context context) {
        return Transformations.switchMap(updateParams, input -> {
            MutableLiveData<List<XApp>> result = new MutableLiveData<>();
            backgroundExecutor.execute(() -> {
                Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long> params = input;
                Pair<String, List<String>> filter = params.getFirst();
                Pair<String, Boolean> searchParams = params.getSecond();

                XLog.i(TAG, "[setupAppsLiveData] Filter => " + filter.getFirst() + " Search Params: " + searchParams.getFirst());

                boolean isSystem = type.equalsIgnoreCase("system");
                List<XApp> apps = AppRepository.getInstalledApps(context, isSystem);

                XLog.i(TAG, "[setupAppsLiveData] Is System => " + isSystem + " App Size: " + apps.size() + " Type: " + type);

                if("configured".equalsIgnoreCase(type)) {
                    List<XApp> configuredApps = new ArrayList<>();
                    for (XApp app : apps) {
                        if (app.isEnabled)
                            configuredApps.add(app);
                    }
                    apps = configuredApps;
                }

                XLog.i(TAG, "[setupAppsLiveData] Filtering Apps... Apps Size: " + apps.size() + " Filter => " + filter.getFirst() + " Search First: " + searchParams.getFirst() + " Search Second: " + searchParams.getSecond());

                List<XApp> filteredSortedApps = AppRepository.getFilteredAndSortedApps(
                        apps,
                        filter,
                        searchParams.getFirst(),
                        searchParams.getSecond()
                );

                XLog.i(TAG, "[setupAppsLiveData] Posting New Sorted: " + filteredSortedApps.size());
                mainHandler.post(() -> result.setValue(filteredSortedApps));
            });
            return result;
        });
    }

    /*private void setupAppsLiveDatas() {
        XLog.i(TAG, "[setupAppsLiveData]...");
        executor.scheduleWithFixedDelay(() -> {
            XLog.i(TAG, "[setupAppsLiveData] in the action...");
            Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long> params = updateParams.get();
            Pair<String, List<String>> filter = params.getFirst();
            Pair<String, Boolean> searchParams = params.getSecond();

            XLog.i(TAG, "[setupAppsLiveData] Filter => " + filter.getFirst() + " Search Params: " + searchParams.getFirst());

            boolean isSystem = type.equalsIgnoreCase("system");
            List<XApp> apps = AppRepository.getInstalledApps(isSystem);

            XLog.i(TAG, "[setupAppsLiveData] Is System => " + isSystem + " App Size: " + apps.size() + " Type: " + type);

            if("configured".equalsIgnoreCase(type)) {
                List<XApp> configuredApps = new ArrayList<>();
                for (XApp app : apps) {
                    if (app.isEnabled())
                        configuredApps.add(app);
                }
                apps = configuredApps;
            }

            XLog.i(TAG, "[setupAppsLiveData] Filtering Apps... Apps Size: " + apps.size() + " Filter => " + filter.getFirst() + " Search First: " + searchParams.getFirst() + " Search Second: " + searchParams.getSecond());

            List<XApp> filteredSortedApps = AppRepository.getFilteredAndSortedApps(
                    apps,
                    filter,
                    searchParams.getFirst(),
                    searchParams.getSecond()
            );

            XLog.i(TAG, "[setupAppsLiveData] Filtered Apps: " + filteredSortedApps.size());
            appsLiveData.postValue(filteredSortedApps);
            XLog.i(TAG, "[setupAppsLiveData] Posted Values!");
        }, 0, 300, TimeUnit.MILLISECONDS);
    }*/
}
