package com.obbedcode.shared.xplex;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.repositories.HookRepository;
import com.obbedcode.shared.repositories.SettingRepository;
import com.obbedcode.shared.repositories.filters.bases.FilterPropertiesDef;
import com.obbedcode.shared.repositories.interfaces.ICommandInterceptor;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.repositories.interfaces.IFilterableDefinition;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.XIdentity;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;

public class XAppCache {
    private static final String TAG = "ObbedCode.XP.XAppCache";

    public int userId;
    public String packageName;
    public Context context;
    public int versionCode;

    public Map<String, String> settings = new HashMap<>();
    public Map<String, String> properties = new HashMap<>();

    public List<XAssignment> assignments = new ArrayList<>();
    public List<IFilterFactory> filters = HookRepository.getFactoryInstances(this);

    public List<ICommandInterceptor> interceptors = new ArrayList<>();

    public boolean hasCommandInterceptors() { return interceptors != null && !interceptors.isEmpty(); }
    public List<ICommandInterceptor> getCommandInterceptors() { return interceptors; }
    public void addCommandInterceptor(ICommandInterceptor com) {
        if(!interceptors.contains(com))
            interceptors.add(com);
    }

    public static XAppCache create() { return new XAppCache(); }
    public static XAppCache create(int userId, String packageName) { return new XAppCache(userId, packageName, null); }
    public static XAppCache create(int userId, String packageName, Context context) { return new XAppCache(userId, packageName, context); }

    public XAppCache() {  }
    public XAppCache(int userId, String packageName, Context context) {
        this.userId = userId;
        this.packageName = packageName;
        bindContext(context);
    }

    public void putProperty(String propertyName, String settingName) {
        properties.put(propertyName, settingName);
    }

    public void bindContext(Context context) {
        this.context = context;
        if(this.context != null) {
            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                this.versionCode = pInfo.versionCode;
            }catch (Exception e) {
                XLog.e(TAG, "Failed to Resolve App Version Code: " + packageName + " Error: " + e);
            }
        }
    }

    public XParam createParam(XC_MethodHook.MethodHookParam param, XHookDef hook, boolean isBefore) {
        return new XParam(
                settings,
                properties,
                hook,
                param,
                packageName,
                isBefore);
    }

    public XAppCache initAssignments() {
        List<XAssignment> assignmentsCopy = HookRepository.getAssignments(userId, packageName, true);
        for(XAssignment ass : assignmentsCopy) {
            XHookDef def = ass.definition;
            if(def instanceof FilterPropertiesDef) {
                //Init Defs that have "properties" linked to them now
                FilterPropertiesDef propFilter = (FilterPropertiesDef) def;
                String setting = propFilter.settings[0];
                for(String s : propFilter.properties)
                    properties.put(s, setting);
            }

            if(def instanceof IFilterableDefinition) {
                IFilterableDefinition defFilter = (IFilterableDefinition) def;
                for (IFilterFactory fac : filters) {
                    if(defFilter.isFactory(fac)) {
                        fac.handleDefinition(def);
                    }
                }
            } else {
                assignments.add(ass);
            }
        }

        return this;
    }

    public XAppCache initSettings() {
        settings.putAll(SettingRepository.getSettings(XIdentity.GLOBAL_USER, XIdentity.GLOBAL_NAMESPACE));
        settings.putAll(SettingRepository.getSettings(userId, packageName));
        return this;
    }
}
