package com.obbedcode.xplex.views.viewmodel.app;

import android.app.Application;

import com.obbedcode.shared.repositories.HookGroupRepository;
import com.obbedcode.shared.xplex.data.hook.XHookGroup;
import com.obbedcode.xplex.views.viewmodel.app.base.AppBaseViewModel;

public class HookGroupViewModel extends AppBaseViewModel<XHookGroup> {
    public HookGroupViewModel(Application application) {
        super(application, "HookApplicationViewModel");
        super.setRepository(HookGroupRepository.INSTANCE);
    }
}
