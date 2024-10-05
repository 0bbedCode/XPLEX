package com.obbedcode.shared.xplex;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XHooker {
    private static final String TAG = "ObbedCode.XP.XHooker";

    public void hookPackage(final XC_LoadPackage.LoadPackageParam lpparam, int uid, final Context context) {
        try {
            XAppCache appCache = XAppCache.create(uid, lpparam.packageName, context)
                    .initAssignments()
                    .initSettings();

            for(XAssignment ass : appCache.assignments) {
                XHookDef def = ass.definition;
                if(def == null) continue;
                if(def.isField()) {

                } else {
                    //Check for WildCard all methods hook
                    final Member member = def.resolveMember();
                    if(member != null) {
                        try {
                            XposedBridge.hookMethod(member, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    //if(def.beforeHook)
                                        //def.handleHooked(appCache.createParam(param, def));
                                    super.beforeHookedMethod(param);
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    //if(def.afterHook)
                                        //def.handleHooked(appCache.createParam(param, def));
                                    super.afterHookedMethod(param);
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
