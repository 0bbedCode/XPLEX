package com.obbedcode.shared.repositories.filters;

import android.content.Context;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.TypeResolver;
import com.obbedcode.shared.repositories.filters.bases.FilterFactory;
import com.obbedcode.shared.repositories.filters.bases.FilterPropertiesDefinition;
import com.obbedcode.shared.repositories.filters.shell.CommandData;
import com.obbedcode.shared.repositories.interfaces.ICommandHook;
import com.obbedcode.shared.repositories.interfaces.ICommandInterceptor;
import com.obbedcode.shared.repositories.interfaces.IFilterFactory;
import com.obbedcode.shared.xplex.XParam;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

//Extend XHookDef ?? its a hook def technically
public class PropertiesFilterFactory extends FilterFactory {
    private static final String TAG = "ObbedCode.XP.PropertiesFilterFactory";

    //We dont need to get "properties" right away more so lets use this "filter"
    //It will handle everything we need to handle creating us a Properties List
    //Tnen Deploying Hooks...

    public static final String AUTHOR = "ObbedCode";
    public static final String COLLECTION = "Intercept";
    public static final String GROUP = "Filter";
    public static final String CONTAINER = "Device Properties";
    public static final String DESCRIPTION = "Intercept and Filter Properties from get prop / build.prop";

    public static void setHeader(XHookDefinition def) { def.setHeader(AUTHOR, COLLECTION, GROUP, CONTAINER, DESCRIPTION); }

    public static IFilterFactory createFactoryInstance() { return new PropertiesFilterFactory(); }
    public static final List<XHookDefinition> DEFINITIONS = Arrays.asList(
            new PropertiesHookOne(),
            new PropertiesHookTwo(),
            new PropertiesHookThree(),
            new PropertiesHookFour());

    public PropertiesFilterFactory() {
        this.definitions = DEFINITIONS;
        super.setFilterCategory("properties");
    }

    @Override
    public void handleDefinition(XHookDefinition def) {
        if(isFilter(def) && def instanceof FilterPropertiesDefinition) {
            super.handleDefinition(def);    //Add this to a list of before / after
                                            //Not rlly needed other than a way to tell if before or after hooks
        }
    }

    @Override
    public void deployHooks(XC_LoadPackage.LoadPackageParam lpparam, Context context) {
        if(!afterHooks.isEmpty() || !beforeHooks.isEmpty()) {
            try {
                for (XHookDefinition propHook : DEFINITIONS) {
                    try {
                        TypeResolver.ResolvedHook resolved = propHook.resolvedHook(context);
                        final Member member = resolved.resolveMember();
                        XposedBridge.hookMethod(member, new XC_MethodHook() {
                            final boolean hasBefore = !beforeHooks.isEmpty();
                            final boolean hasAfter = !afterHooks.isEmpty();

                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                //XParam xParam = appCache.createParam(param, propHook, true);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                try {
                                    XParam xParam = appCache.createParam(param, propHook, false);
                                    String prop = (String)xParam.getArgument(0);
                                    if(prop == null || prop.equalsIgnoreCase("vxp"))
                                        return;

                                    XLog.i(TAG, "[shell factoryy] =>  Prop Fake: " + prop);
                                    String propSetting = xParam.getPropertySetting(prop);
                                    if(propSetting == null) return;
                                    String settingValue = xParam.getSetting(propSetting);
                                    XLog.i(TAG, "[shell factoryy] => Setting Prop Fake: " + settingValue);
                                    xParam.setResult(settingValue == null ? "" : settingValue);
                                }catch (Exception e) {
                                    XLog.e(TAG, "Error Property Interceptor Handler...");
                                }
                            }
                        });
                    }catch (Exception eInner) {
                        XLog.e(TAG, "Failed to Hook method: " + eInner + " => " + propHook);
                    }
                }
            }catch (Exception e) {
                XLog.e(TAG, "Error Deploying Hooks... " + e);
            }
        }
    }


    public static class PropertiesHookOne extends XHookDefinition {
        public PropertiesHookOne() {
            PropertiesFilterFactory.setHeader(this);
            super.setHookId("Filter/System.getProperty(prop)");
            super.setMethod("getProperty");
            super.setClass("java.lang.System");
            super.setParams("java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }

    public static class PropertiesHookTwo extends XHookDefinition {
        public PropertiesHookTwo() {
            PropertiesFilterFactory.setHeader(this);
            super.setHookId("Filter/System.getProperty(prop, e)");
            super.setMethod("getProperty");
            super.setClass("java.lang.System");
            super.setParams("java.lang.String", "java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }

    public static class  PropertiesHookThree extends XHookDefinition {
        public PropertiesHookThree() {
            PropertiesFilterFactory.setHeader(this);
            super.setHookId("Filter/SystemProperties.get(prop)");
            super.setMethod("get");
            super.setClass("android.os.SystemProperties");
            super.setParams("java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }

    public static class  PropertiesHookFour extends XHookDefinition {
        public PropertiesHookFour() {
            PropertiesFilterFactory.setHeader(this);
            super.setHookId("Filter/SystemProperties.get(prop, e)");
            super.setMethod("get");
            super.setClass("android.os.SystemProperties");
            super.setParams("java.lang.String", "java.lang.String");
            super.setReturnType("java.lang.String");
        }
    }
}
