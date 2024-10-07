package com.obbedcode.shared.repositories;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.repositories.filters.PropertiesFilterFactory;
import com.obbedcode.shared.repositories.filters.ShellFilterFactory;
import com.obbedcode.shared.repositories.hooks.privacy.tracking.MacAddressHooks;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.repositories.interfaces.IRepository;
import com.obbedcode.shared.service.ServiceClient;
import com.obbedcode.shared.xplex.XAppCache;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;
import com.obbedcode.shared.xplex.data.hook.XHookContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.Pair;

//Make these all UI ?
public class HookRepository implements IRepository<XHookContainer> {
    public static final HookRepository INSTANCE = new HookRepository();

    private static final String TAG = "ObbedCode.XP.HookRepository";

    private static final Object mLock = new Object();
    private static Map<String, XHookDefinition> mHooksCache = new HashMap<>();


    public static IXPService getService() {
        return ServiceClient.getService();
    }


    @Override
    public List<XHookContainer> get() {
        return Collections.emptyList();
    }

    @Override
    public List<XHookContainer> get(int userId, String packageName, String type) {
        List<XAssignment> assignments = getAssignments(userId, packageName, false);
        Map<String, XHookDefinition> definitions = getHookDefinitions();

        Map<String, XHookContainer> containers = new HashMap<>();

        for(XHookDefinition definition : definitions.values()) {
            if(definition.group.equalsIgnoreCase(type)) {
                XHookContainer container = containers.get(definition.container);
                if(container == null) {
                    container = new XHookContainer();
                    container.name = definition.container;
                    container.description = "Some Description";
                    container.hookIds.add(definition.hookId);
                    //Init settings shit
                    //For now if ONE is enabled its considered enabled
                    for(XAssignment ass : assignments) {
                        if(ass.hook.equalsIgnoreCase(definition.hookId)) {
                            container.isEnabled = true;
                            //Get Settings
                            break;
                        }
                    }
                    containers.put(definition.container, container);
                } else {
                    container.hookIds.add(definition.hookId);
                }
            }
        }


        return new ArrayList<>(containers.values());
    }

    @Override
    public List<XHookContainer> getFilteredAndSorted(List<XHookContainer> items, Pair<String, List<String>> filter, String keyword, boolean isReverse) {
        return items;
    }

    public static Map<String, XHookDefinition> parseList(List<XHookDefinition> definitions) {
        Map<String, XHookDefinition> defMap = new HashMap<>();
        for(XHookDefinition d : definitions)
            defMap.put(d.getHookId(), d);

        return defMap;
    }

    public static Map<String, XHookDefinition> getHookDefinitions() {
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
                Map<String, XHookDefinition> definitions = getHookDefinitions();
                for(XAssignment ass : assignments) {
                    XHookDefinition def = definitions.get(ass.hook);
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
