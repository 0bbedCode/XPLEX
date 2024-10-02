package com.obbedcode.xplex.views.viewmodel.app;

import android.app.Application;

import com.obbedcode.shared.repositories.LogRepository;
import com.obbedcode.shared.xplex.data.XLogLog;
import com.obbedcode.xplex.views.viewmodel.app.base.AppBaseViewModel;

public class LogViewModel extends AppBaseViewModel<XLogLog> {
    public LogViewModel(Application application) {
        super(application, "LogGroupViewModel");
        super.setRepository(LogRepository.INSTANCE);
    }
}
