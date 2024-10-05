package com.obbedcode.shared.repositories.filters.bases;

import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.xplex.XAppCache;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FilterBase implements IFilterFactory {
    public String filterCategory;

    public List<XHookDef> beforeHooks = new ArrayList<>();
    public List<XHookDef> afterHooks = new ArrayList<>();

    public List<XHookDef> definitions = new ArrayList<>();

    public XAppCache appCache;

    public void bindAppCache(XAppCache appCache) {
        this.appCache = appCache;
    }

    //Some filters just need one hook ??
    //Example get prop
    //We just need to look up in the get prop Table


    public void setDefinitions(XHookDef... defs) {
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
    public boolean isFilter(XHookDef hookDef) {
        if(hookDef == null || hookDef.method == null) return false;
        return hookDef.method.toLowerCase().contains(filterCategory.toLowerCase());
    }

    @Override
    public void deployHooks(XC_LoadPackage.LoadPackageParam lpparam) {
        boolean doBefore = !beforeHooks.isEmpty();
        boolean doAfter = !afterHooks.isEmpty();
    }

    @Override
    public void handleDefinition(XHookDef def) {
        if(def.afterHook) afterHooks.add(def);
        else if(def.beforeHook) beforeHooks.add(def);
    }
}
