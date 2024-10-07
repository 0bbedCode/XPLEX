package com.obbedcode.xplex.views.viewmodel.app;

import android.app.Application;

import com.obbedcode.shared.repositories.HookRepository;
import com.obbedcode.shared.xplex.data.hook.XHookContainer;
import com.obbedcode.xplex.views.viewmodel.app.base.ListBaseViewModel;

public class HookViewModel extends ListBaseViewModel<XHookContainer> {
    public HookViewModel(Application application) {
        super(application, "Hook");
        super.setRepository(HookRepository.INSTANCE);
    }
}
