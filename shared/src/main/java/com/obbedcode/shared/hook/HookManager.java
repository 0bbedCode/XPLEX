package com.obbedcode.shared.hook;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class HookManager {
    public static HookManager create() { return new HookManager(); }

    private Map<String, Collection<XC_MethodHook.Unhook>> mHookz = new HashMap<>();

    public HookManager hookAllMethods(Class<?> hookClass, String methodName, XC_MethodHook callback) {
        Set<XC_MethodHook.Unhook> hookz = XposedBridge.hookAllMethods(hookClass, methodName, callback);
        Collection<XC_MethodHook.Unhook> hookzCopy = mHookz.get(methodName);
        if(hookzCopy == null) {
            hookzCopy = new ArrayList<>();
            hookzCopy.addAll(hookz);
            mHookz.put(methodName, hookzCopy);
        } else {
            hookzCopy.addAll(hookz);
            mHookz.put(methodName, hookzCopy);
        } return this;
    }

    public void unHook() { unHook("*"); }
    public void unHook(String methodName) {
        if(methodName.equalsIgnoreCase("*")) {
            //Map.Entry<String, String> r : settings.entrySet()
            for(Map.Entry<String, Collection<XC_MethodHook.Unhook>> p : mHookz.entrySet()) {
                for(XC_MethodHook.Unhook u : p.getValue())
                    u.unhook();
            }

            mHookz.clear();
        }else {
            Collection<XC_MethodHook.Unhook> hookzCopy = mHookz.get(methodName);
            for(XC_MethodHook.Unhook u : hookzCopy)
                u.unhook();

            mHookz.remove(methodName);
        }
    }
}
