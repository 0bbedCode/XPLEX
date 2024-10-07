package com.obbedcode.shared.repositories.interfaces;

import android.content.Context;

import com.obbedcode.shared.xplex.XAppCache;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface IFilterFactory {
    String getCategory();
    boolean isFilter(XHookDefinition hookDef);
    void deployHooks(final XC_LoadPackage.LoadPackageParam lpparam, Context context);

    void bindAppCache(XAppCache appCache);
    void handleDefinition(XHookDefinition def);
}
