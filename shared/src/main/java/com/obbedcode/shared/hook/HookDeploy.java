package com.obbedcode.shared.hook;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.random.RandomData;
import com.obbedcode.shared.service.ServiceClient;
import com.obbedcode.shared.xplex.data.XIdentity;
import com.obbedcode.shared.xplex.data.XSetting;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookDeploy {
    private static final String TAG = "ObbedCode.XP.HookGum";

    public static IXPService service = ServiceClient.getService();

    //Maybe this part not gum but manage for Hooks ?
    //In UI Show the Settings BEING used so it can be spoofed or looked at the values

    public static String randomString(int length) { return RandomData.generateAlphaString(length); }
    public static String randomNumberString(int length) { return RandomData.generateNumberString(length); }

    //some deploy function
    public final String category;
    public final String collection;
    public final String description;
    public final List<HookDef> definitions;
    //we can use resources to find any scripts that fall under this category ?
    public HookDeploy(String category, String collection, String description, List<HookDef> definitions) {
        this.category = category;
        this.collection = collection;
        this.description = description;
        this.definitions = definitions;
    }

    public boolean isEnabled(String packageName) {
        return  true;
    }

    public void hookPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
    }
}
