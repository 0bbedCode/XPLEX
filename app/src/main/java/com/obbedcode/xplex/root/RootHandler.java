package com.obbedcode.xplex.root;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.obbedcode.shared.logger.XLog;

public class RootHandler implements Handler.Callback {
    private static final String TAG = "ObbedCode.RootHandler";

    public static RootHandler instance;

    public RootHandler() { instance = this; }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        XLog.i(TAG, "handleMessage: " + msg.toString() + " what: " + msg.what);
        return false;
    }
}
