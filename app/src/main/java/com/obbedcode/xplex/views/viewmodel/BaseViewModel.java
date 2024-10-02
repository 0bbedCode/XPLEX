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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.NotImplementedError;
import kotlin.Pair;
import kotlin.Triple;

public abstract class BaseViewModel<T>  extends AndroidViewModel {
    protected String type = "";
    protected final MutableLiveData<Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long>> updateParams;
    protected final LiveData<List<T>> liveData;
    protected final Handler mainHandler = new Handler(Looper.getMainLooper());
    protected final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    public void setType(String type) { this.type = type; }
    public LiveData<List<T>> getRawLiveData() { return this.liveData; }

    public BaseViewModel(Application application, String type) {
        super(application);
        this.updateParams = new MutableLiveData<>(new Triple<>(new Pair<>(Str.EMPTY, new ArrayList<>()), new Pair<>(Str.EMPTY, false), 0L));
        this.liveData = setupLiveData(application);
        this.type = type;
        refresh();
    }

    public void refresh() {
        updateParams.setValue(new Triple<>(
                new Pair<>(PrefManager.order(), getFilterList()),
                new Pair<>(Str.EMPTY, PrefManager.isReverse()),
                System.currentTimeMillis()
        ));
    }

    public void updateList(Pair<String, List<String>> filter, String keyword, boolean isReversed) {
        updateParams.setValue(new Triple<>(
                filter,
                new Pair<>(keyword, isReversed),
                System.currentTimeMillis()
        ));
    }

    protected List<T> filterData(Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long> params, Application application) {
        throw new NotImplementedError();
    }

    protected List<String> getFilterList() {
        return new ArrayList<>();
    }

    private LiveData<List<T>> setupLiveData(final Application application) {
        return Transformations.switchMap(updateParams, input -> Transformations.map(
                Transformations.distinctUntilChanged(
                        Transformations.switchMap(updateParams, params -> {
                            MutableLiveData<List<T>> result = new MutableLiveData<>();
                            backgroundExecutor.execute(() -> {
                                List<T> filteredData = filterData(params, application);
                                mainHandler.post(() -> result.setValue(filteredData));
                            });
                            return result;
                        })
                ),
                rawLiveData -> rawLiveData // You can add any additional transformations here if needed
        ));
    }
}
