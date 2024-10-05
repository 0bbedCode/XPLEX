package com.obbedcode.shared.repositories.interfaces;

import com.obbedcode.shared.xplex.XAppCache;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public interface IFilterFactory {
    String getCategory();
    boolean isFilter(XHookDef hookDef);
    void deployHooks(final XC_LoadPackage.LoadPackageParam lpparam);

    void bindAppCache(XAppCache appCache);
    void handleDefinition(XHookDef def);
}
