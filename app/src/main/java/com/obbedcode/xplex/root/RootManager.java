package com.obbedcode.xplex.root;

import com.obbedcode.shared.logger.XLog;
import com.topjohnwu.superuser.Shell;

public class RootManager {
    private static final String TAG = "ObbedCode.XP.RootManager";
    public static RootManager instance;

    public boolean hasRootAccess = false;

    public RootManager() {
        instance = this;
        Shell.enableVerboseLogging = true;
        Shell.setDefaultBuilder(
                Shell.Builder.create()
                        .setFlags(Shell.FLAG_REDIRECT_STDERR)
                        .setTimeout(10));
    }

    public void requestRoot() {
        //This is now crashing ?
        XLog.i(TAG, "Requesting Root...", true);
        Shell.getShell(shell -> {
            XLog.i(TAG, "Root Permission State! " + Shell.isAppGrantedRoot());
            hasRootAccess = Boolean.TRUE.equals(Shell.isAppGrantedRoot());
        });
    }
}
