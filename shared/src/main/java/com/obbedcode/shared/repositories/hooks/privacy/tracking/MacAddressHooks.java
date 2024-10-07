package com.obbedcode.shared.repositories.hooks.privacy.tracking;

import android.net.wifi.WifiInfo;

import com.obbedcode.shared.random.RandomNetworkData;
import com.obbedcode.shared.repositories.filters.bases.FilterPropertiesDefinition;
import com.obbedcode.shared.xplex.XParam;
import com.obbedcode.shared.xplex.data.hook.XHookDefinition;

import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.List;

public class MacAddressHooks  {
    public static final List<XHookDefinition> MAC_HOOKS = Arrays.asList(
            new MacHookOne(),
            new MacHookTwo(),
            new MacHookThree());

    //if set but not value was set the notify then with a notification !
    public static final String AUTHOR = "ObbedCode";
    public static final String COLLECTION = "Privacy";
    public static final String GROUP = "Identity and Tracking";
    public static final String CONTAINER = "MacAddress";
    public static final String DESCRIPTION = "Your Mac Address is Hardware Address that is Linked to your Network Device on your Phone.";

    public static final String SETTING = "unique.mac.address";

    public static void setHeader(XHookDefinition def) { def.setHeader(AUTHOR, COLLECTION, GROUP, CONTAINER, DESCRIPTION); }

    public static class MacHookOne extends XHookDefinition {
        public MacHookOne () {
            MacAddressHooks.setHeader(this);
            super.setHookId("Id/WifiInfo.getMacAddress");
            super.setMethod("getMacAddress");
            super.setClass(WifiInfo.class.getName());
            super.setReturnType(String.class.getName());
            super.setSettings(SETTING);
            super.setIsAfterHook(true);
        }

        @Override
        public boolean handleHooked(XParam param) {
            if (afterHook && !param.isNullResult()) {
                String result = param.getResultAsString();
                String fake = getSetting(param);
                if(fake != null) {
                    param.setResult(fake);
                    param.finalizeUsage(result, fake);
                    //Notify
                    //Usage
                    //Notification !!
                    return true;
                }
            }

            return false;
        }
    }

    public static class MacHookTwo extends XHookDefinition {
        public MacHookTwo() {
            MacAddressHooks.setHeader(this);
            super.setHookId("Id/NetworkInterface.getHardwareAddress");
            super.setMethod("getHardwareAddress");
            super.setClass(NetworkInterface.class.getName());
            super.setReturnType(byte[].class.getName());        //[B
            super.setSettings(SETTING);
            super.setIsAfterHook(true);
        }

        @Override
        public boolean handleHooked(XParam param) {
            if(afterHook && !param.isNullResult()) {
                try {
                    String result = RandomNetworkData.macBytesToString((byte[])param.getResult());
                    String fake = getSetting(param);
                    if(fake != null) {
                        byte[] fakeBytes = RandomNetworkData.macStringToBytes(fake);
                        param.setResult(fakeBytes);
                        param.finalizeUsage(result, fake);
                        return true;
                    }
                }catch (Exception e) {
                    //Log
                }
            }

            return false;
        }
    }

    public static class MacHookThree extends FilterPropertiesDefinition {
        public MacHookThree() {
            MacAddressHooks.setHeader(this);
            super.setHookId("Id/Properties(mac)");
            super.setMethod("*properties*shell*");
            super.setProperties("ro.ril.oem.wifimac", "wlan.driver.macaddr", "ro.ril.oem.btmac", "persist.odm.ril.oem.btmac", "persist.odm.ril.oem.wifimac");
            super.setSettings(SETTING);
            super.setIsAfterHook(true);
        }
    }
}
