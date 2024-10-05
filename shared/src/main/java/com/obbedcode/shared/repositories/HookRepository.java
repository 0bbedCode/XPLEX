package com.obbedcode.shared.repositories;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.repositories.filters.PropertiesFilterFactory;
import com.obbedcode.shared.repositories.filters.ShellFilterFactory;
import com.obbedcode.shared.repositories.hooks.privacy.tracking.MacAddressHooks;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.service.ServiceClient;
import com.obbedcode.shared.service.XplexService;
import com.obbedcode.shared.xplex.XAppCache;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookRepository {
    private static final String TAG = "ObbedCode.XP.HookRepository";

    private static final Object mLock = new Object();
    private static Map<String, XHookDef> mHooksCache = new HashMap<>();


    public static IXPService getService() {
        return ServiceClient.getService();
    }

    public static Map<String, XHookDef> parseList(List<XHookDef> defs) {
        Map<String, XHookDef> defMap = new HashMap<>();
        for(XHookDef d : defs) {
            defMap.put(d.getHookId(), d);
        }

        return defMap;
    }

    public static Map<String, XHookDef> getHookDefinitions() {
        synchronized (mLock) {
            if(mHooksCache.isEmpty()) {
                mHooksCache.putAll(parseList(MacAddressHooks.MAC_HOOKS));
            }

            return mHooksCache;
        }
    }

    public static List<IFilterFactory> getFactoryInstances(XAppCache appCache) {
        List<IFilterFactory> factories = new ArrayList<>();
        factories.add(PropertiesFilterFactory.createFactoryInstance());
        factories.add(ShellFilterFactory.createFactoryInstance());
        for(IFilterFactory fac : factories)
            fac.bindAppCache(appCache);

        return factories;
    }


    public static List<XAssignment> getAssignments(int userId, String packageName, boolean resolveHook) {
        List<XAssignment> assignments = new ArrayList<>();
        IXPService serv = getService();
        if(serv == null) return assignments;
        try {
            assignments = serv.getAppAssignments(userId, packageName).getList();
            if(resolveHook) {
                Map<String, XHookDef> definitions = getHookDefinitions();
                for(XAssignment ass : assignments) {
                    XHookDef def = definitions.get(ass.hook);
                    if(def != null) {
                        ass.definition = def;
                    }
                }
            }
        }catch (Throwable e) {
            XLog.e(TAG, "Failed to get Hooks for Package: " + packageName + " Error: " + e);
        }
        return assignments;
    }
}
