package com.obbedcode.shared.reflect;

public class ServicesGlobal {
    private static Object activityManagerService = null;

    public static Object getIActivityManager() {
        //Ensure careful with these calls as they wait can cause a Dead Lock on Hook Threads
        if(activityManagerService == null) activityManagerService = HiddenApiUtils.getIActivityManager();
        return activityManagerService;
    }

}
