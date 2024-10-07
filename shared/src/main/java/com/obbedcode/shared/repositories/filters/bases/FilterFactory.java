package com.obbedcode.shared.repositories.filters.bases;

import android.content.Context;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.xplex.XAppCache;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FilterFactory implements IFilterFactory {
    private static final String TAG = "ObbedCode.XP.FilterFactory";

    public String filterCategory;

    public List<XHookDefinition> beforeHooks = new ArrayList<>();
    public List<XHookDefinition> afterHooks = new ArrayList<>();

    public List<XHookDefinition> definitions = new ArrayList<>();

    public XAppCache appCache;

    public void bindAppCache(XAppCache appCache) {
        this.appCache = appCache;
    }

    //Some filters just need one hook ??
    //Example get prop
    //We just need to look up in the get prop Table


    public void setDefinitions(XHookDefinition... defs) {
        if(defs != null) {
            definitions.addAll(Arrays.asList(defs));
        }
    }

    public void setFilterCategory(String filterCategory) {
        if(filterCategory != null) {
            this.filterCategory = filterCategory.trim();
        }
    }

    @Override
    public String getCategory() { return filterCategory; }

    @Override
    public boolean isFilter(XHookDefinition hookDef) {
        if(hookDef == null || hookDef.method == null) return false;
        XLog.i(TAG, "[IsFilterer factoryy] => " + hookDef.method + " => " + filterCategory);
        return hookDef.method.toLowerCase().contains(filterCategory.toLowerCase());
    }

    @Override
    public void deployHooks(XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        boolean doBefore = !beforeHooks.isEmpty();
        boolean doAfter = !afterHooks.isEmpty();
    }

    @Override
    public void handleDefinition(XHookDefinition def) {
        if(def.afterHook) afterHooks.add(def);
        else if(def.beforeHook) beforeHooks.add(def);
    }
}
