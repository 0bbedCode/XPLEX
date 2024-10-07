package com.obbedcode.shared.xplex;

import android.content.Context;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.TypeResolver;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XHooker {
    private static final String TAG = "ObbedCode.XP.XHooker";

    public static void hookPackage(final XC_LoadPackage.LoadPackageParam lpparam, int uid, final Context context) {
        try {
            XAppCache appCache = XAppCache.create(uid, lpparam.packageName, context)
                    .initAssignments()
                    .initSettings();

            for(XAssignment ass : appCache.assignments) {
                XHookDefinition def = ass.definition;
                TypeResolver.ResolvedHook resolved = def.resolvedHook(context);
                if(resolved.isField) {

                } else {
                    //Check for WildCard all methods hook
                    final Member member = resolved.resolveMember();
                    if(member != null) {
                        try {
                            XposedBridge.hookMethod(member, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    if(def.beforeHook) def.handleHooked(appCache.createParam(param, def, true));
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    if(def.afterHook) def.handleHooked(appCache.createParam(param, def, false));
                                }
                            });
                        }catch (Exception innerE) {
                            XLog.e(TAG, "Failed to Deploy Hook: " + def + " Package: " + lpparam.packageName + " Error: " + innerE);
                        }
                    }
                }
            }
        }catch (Exception e) {
            XLog.e(TAG, "Error Hooking Package: " + lpparam.packageName + " Error: " + e);
        }
    }
}
