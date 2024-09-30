package com.obbedcode.xplex.views.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.obbedcode.shared.PrefManager;
import com.obbedcode.shared.Str;
import com.obbedcode.shared.UiGlobals;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.pm.AppRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.Pair;
import kotlin.Triple;

/*public class AppsViewModelEx extends AndroidViewModel {
    private String type = UiGlobals.TAB_USER;
    private final MutableLiveData<Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long>> mUpdateParams;
    private final LiveData<List<XApp>> mAppsLiveData;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final Executor mBackgroundExecutor = Executors.newSingleThreadExecutor();

    public AppsViewModelEx(Application application) {
        super(application);
        this.mUpdateParams = new MutableLiveData<>(new Triple<>(new Pair<>(Str.EMPTY, new ArrayList<>()), new Pair<>(Str.EMPTY, false), 0L));
        this.mAppsLiveData = setupAppsLiveData(application);
        refreshApps();
    }

    public void setType(String type) { this.type = type; }
    public LiveData<List<XApp>> getAppsLiveData() { return mAppsLiveData; }

    public void refreshApps() {
        mUpdateParams.setValue(new Triple<>(
                new Pair<>(PrefManager.order(), getFilterList()),
                new Pair<>(Str.EMPTY, PrefManager.isReverse()),
                System.currentTimeMillis()
        ));
    }

    public void updateList(Pair<String, List<String>> filter, String keyword, boolean isReversed) {
        mUpdateParams.setValue(new Triple<>(
                filter,
                new Pair<>(keyword, isReversed),
                System.currentTimeMillis()
        ));
    }

    private List<String> getFilterList() {
        List<String> filterList = new ArrayList<>();
        if (PrefManager.isConfigured()) filterList.add(UiGlobals.FILTER_CONFIGURED);
        if (PrefManager.isUpdated()) filterList.add(UiGlobals.FILTER_LAST_UPDATE);
        if (PrefManager.isDisabled()) filterList.add(UiGlobals.FILTER_DISABLED);
        return filterList;
    }

    private LiveData<List<XApp>> setupAppsLiveData(final Application application) {
        return Transformations.switchMap(mUpdateParams, input -> Transformations.map(
                Transformations.distinctUntilChanged(
                        Transformations.switchMap(mUpdateParams, params -> {
                            MutableLiveData<List<XApp>> result = new MutableLiveData<>();
                            mBackgroundExecutor.execute(() -> {

                                Pair<String, List<String>> filter = params.getFirst();
                                Pair<String, Boolean> searchParams = params.getSecond();

                                //Future filter here
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

                                List<XApp> filteredSortedApps = AppRepository.getFilteredAndSortedApps(
                                        apps,
                                        filter,
                                        searchParams.getFirst(),
                                        searchParams.getSecond()
                                );

                                mMainHandler.post(() -> result.setValue(filteredSortedApps));
                            });
                            return result;
                        })
                ),
                appInfos -> appInfos // You can add any additional transformations here if needed
        ));
    }
}*/
