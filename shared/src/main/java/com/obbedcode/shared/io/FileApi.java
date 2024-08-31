package com.obbedcode.shared.io;

import com.obbedcode.shared.logger.XLog;

public class FileApi {
    private static final String TAG = "ObbedCode.XP.FileApi";

    public static final String OWNER_ROOT = "root";
    public static final int OWNER_ROOT_ID = 0;

    public static final String OWNER_SYSTEM = "system";
    public static final int OWNER_SYSTEM_ID = 1000;

    public static final String OWNER_SHELL = "shell";
    public static final int OWNER_SHELL_ID = 2000;

    public static final String OWNER_NOBODY = "nobody";
    public static final int OWNER_NOBODY_UID = 9999;

    public static final String OWNER_MEDIA_RW = "media_rw";
    public static final int OWNER_MEDIA_RW_ID = 1023;

    public static final String OWNER_BLUETOOTH = "bluetooth";
    public static final int OWNER_BLUETOOTH_ID = 1002;

    public static final String OWNER_WIFI = "wifi";
    public static final int OWNER_WIFI_ID = 1010;

    public static final String OWNER_RADIO = "radio";
    public static final int OWNER_RADIO_ID = 1001;

    public static final String OWNER_INPUT = "input";
    public static final int OWNER_INPUT_ID = 1004;

    public static final String OWNER_GRAPHICS = "graphics";
    public static final int OWNER_GRAPHICS_ID = 1003;

    public static final String OWNER_LOG = "log";
    public static final int OWNER_LOG_ID = 1007;

    public static final String OWNER_SECURITY = "security";
    public static final int OWNER_SECURITY_ID = 1009;

    public static final String OWNER_ADB = "adb";
    public static final int OWNER_ADB_ID = 1041;

    public static final String OWNER_U0_AN = "u0_aN";
    //ids can be from 10000

    public static final String OWNER_APP_ZYGOTE = "app_zygote";
    public static final int OWNER_APP_ZYGOTE_ID = 1015;

    public static final int MODE_RWX_EVERYONE = 777;

    public void chmod(String file, int mode, boolean recursive) {
        executeCommand("chmod " + (recursive ? "-R " : "") + mode + " " + file);
    }

    public void chown(String file, String user, String group, boolean recursive) {
        String u = user == null ? "" : user;
        String g = group == null ? "" : group;
        executeCommand("chown " + (recursive ? "-R " : "" ) + u + ":" + g + " " + file);
    }

    private void executeCommand(String command) {
        try {
            Runtime.getRuntime().exec(command).waitFor();
        }catch (Exception e) {
            XLog.e(TAG, "[executeCommand] Failed: " + e.getMessage());
        }
    }
}
