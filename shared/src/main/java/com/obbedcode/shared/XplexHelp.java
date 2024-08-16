package com.obbedcode.shared;

import com.obbedcode.shared.utils.RuntimeUtils;

public class XplexHelp {
    public static boolean isInModule() {
        //Something like this
        StackTraceElement[] elements = RuntimeUtils.getStackTraceSafe();
        if(elements == null) return false;
        for(int i = 0; i < elements.length; i++) {
            StackTraceElement e = elements[i];
            String c = e.getClassName();
            if(c.startsWith("com.obbedcode")) {
                if(c.contains("XposedEntry"))
                    return true;
                else {
                    if(e.getMethodName().contains("HookedMethod"))
                        return true;
                }
            }
        } return false;
    }
}
