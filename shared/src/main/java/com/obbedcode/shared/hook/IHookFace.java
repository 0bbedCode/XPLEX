package com.obbedcode.shared.hook;

import de.robv.android.xposed.XC_MethodHook;

public interface IHookFace {
    void afterHook(XC_MethodHook. MethodHookParam param, PackageGum gum);
    void beforeHook(XC_MethodHook. MethodHookParam param, PackageGum gum);
}
