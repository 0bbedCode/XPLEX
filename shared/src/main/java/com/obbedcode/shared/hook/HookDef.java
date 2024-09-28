package com.obbedcode.shared.hook;

import java.lang.reflect.Member;

public class HookDef {
    public String className;
    public String methodName;
    public String[] paramTypes;

    public String scriptContents;

    public boolean beforeHook = false;
    public boolean afterHook = false;

    public boolean hookAll = false;

    public int minSdk = 1;
    public int maxSdk = 999;

    public IHookFace face;

    public String[] settings;

    public boolean isField() { return methodName.startsWith("#"); }
    public boolean isDisabled(String packageName) {
        //check SDK
        //Check if Manually Disabled

        return false;
    }

    public Class<?> resolveParamTypes() {
        return null;
    }

    public Member resolveMember() {
        return null;
    }

    public void params(String... paramTypes) {

    }

    public void setting(String... settings) {

    }

    //Function to build and ensure TRIM (if I accidently leave a whitespace)
}
