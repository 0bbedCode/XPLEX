package com.obbedcode.xplex.views.viewmodel.app.base;

import android.app.Application;

import com.obbedcode.shared.repositories.interfaces.IRepository;
import com.obbedcode.shared.repositories.interfaces.IRepositoryContainer;
import com.obbedcode.shared.xplex.data.hook.XHookApp;
import com.obbedcode.shared.xplex.data.XUser;
import com.obbedcode.shared.xplex.interfaces.IXHookAppContainer;
import com.obbedcode.shared.xplex.interfaces.IXUserContainer;
import com.obbedcode.xplex.views.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;
import kotlin.Triple;

public class ListBaseViewModel<T> extends BaseViewModel<T>
        implements
        IXHookAppContainer,
        IXUserContainer,
        IRepositoryContainer<T> {

    private static final String TAG = "ObbedCode.XP.AppBaseViewModel";

    protected XHookApp targetApplication;
    protected XUser targetUser;
    protected IRepository<T> repository;

    public ListBaseViewModel(Application application, String id) { super(application, id); }

    @Override
    protected List<T> filterData(Triple<Pair<String, List<String>>, Pair<String, Boolean>, Long> params, Application application) {
        if(targetApplication == null || targetUser == null || repository == null) return new ArrayList<>();
        Pair<String, List<String>> filter = params.getFirst();
        Pair<String, Boolean> searchParams = params.getSecond();
        return repository.getFilteredAndSorted(
                repository.get(targetUser.id, targetApplication.packageName, type),
                filter,
                searchParams.getFirst(),
                searchParams.getSecond());
    }

    @Override
    public XHookApp getHookApplication() { return targetApplication; }

    @Override
    public void setHookApplication(XHookApp application) { targetApplication = application; }

    @Override
    public XUser getTargetUser() { return targetUser; }

    @Override
    public void setTargetUser(XUser user) { targetUser = user; }

    @Override
    public IRepository<T> getRepository() { return repository; }

    @Override
    public void setRepository(IRepository<T> repository) { this.repository = repository; }
}
