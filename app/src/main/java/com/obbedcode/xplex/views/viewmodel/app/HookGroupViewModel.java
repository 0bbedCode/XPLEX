package com.obbedcode.xplex.views.viewmodel.app;

import android.app.Application;

import com.obbedcode.shared.repositories.HookGroupRepository;
import com.obbedcode.shared.xplex.data.hook.XHookGroup;
import com.obbedcode.xplex.views.viewmodel.app.base.ListBaseViewModel;

public class HookGroupViewModel extends ListBaseViewModel<XHookGroup> {
    public HookGroupViewModel(Application application) {
        super(application, "HookApplicationViewModel");
        super.setRepository(HookGroupRepository.INSTANCE);
    }
}
