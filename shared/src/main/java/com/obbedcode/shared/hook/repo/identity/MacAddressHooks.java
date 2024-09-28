package com.obbedcode.shared.hook.repo.identity;

import com.obbedcode.shared.hook.HookDef;
import com.obbedcode.shared.hook.IHookFace;
import com.obbedcode.shared.hook.PackageGum;
import com.obbedcode.shared.hook.repo.RepoGlobals;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class MacAddressHooks {
    public static List<HookDef> getDefinitions() {
        return null;
    }
    //Make a properties version to "ensure" this complex system will work ?

    public class WifiInfoDefinition extends HookDef {
        public WifiInfoDefinition() {
            //Init settings options
            //Deployer init the settings ?
            //Hmm when it inits hook for package it will get "settings" values
            //enable Properties Hooker
            //

            //query
            //call
            //properties
            //file
            //shell
            //settings

            //Goal is to not deploy multiple hooks on the same damn function
            //But one

            //Hmm yes
            //So this will return the HookDef for Properties
            //Properties Hook when Invoked will just read from Settings
            //becareful of global though as if they set global setting can be an issue ?
            //Maybe in the inner context of global we can set a list just for properties ??

            this.methodName = "getMacAddress";
            this.className = RepoGlobals.CLASS_WIFI_INFO;
            this.afterHook = true;
            this.setting("id.mac.address");
            this.face = new IHookFace() {
                @Override
                public void afterHook(XC_MethodHook.MethodHookParam param, PackageGum gum) {
                    //param.th
                }

                @Override
                public void beforeHook(XC_MethodHook.MethodHookParam param, PackageGum gum) {

                }
            };
        }
    }
}
