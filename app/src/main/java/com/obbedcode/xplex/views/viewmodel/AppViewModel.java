package com.obbedcode.xplex.views.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.obbedcode.shared.xplex.data.XHookGroup;

import java.util.List;

public class AppViewModel extends BaseViewModel<XHookGroup>  {

    public AppViewModel(Application application, String type) {
        super(application, type);
    }

}
