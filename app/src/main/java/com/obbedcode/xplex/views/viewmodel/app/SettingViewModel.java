package com.obbedcode.xplex.views.viewmodel.app;

import android.app.Application;

import com.obbedcode.shared.repositories.SettingRepository;
import com.obbedcode.shared.xplex.data.XSetting;
import com.obbedcode.xplex.views.viewmodel.app.base.ListBaseViewModel;

public class SettingViewModel extends ListBaseViewModel<XSetting> {
    public SettingViewModel(Application application) {
        super(application, "SettingGroupViewModel");
        super.setRepository(SettingRepository.INSTANCE);
    }
}
