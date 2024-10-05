package com.obbedcode.xplex.views.viewmodel;

import android.app.Application;

import com.obbedcode.shared.xplex.data.hook.XHookGroup;

public class AppViewModel extends BaseViewModel<XHookGroup>  {

    public AppViewModel(Application application, String type) {
        super(application, type);
    }

}
