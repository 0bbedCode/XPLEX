package com.obbedcode.xplex.root;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;

import com.obbedcode.shared.logger.XLog;

public class RootService extends com.topjohnwu.superuser.ipc.RootService implements Handler.Callback {
    private static final String TAG = "ObbedCode.RootService";

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        XLog.i(TAG, "RootService: onBind");
        Handler handler = new Handler(Looper.getMainLooper(), this);
        return new Messenger(handler).getBinder();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        XLog.i(TAG, "RootService: " + msg.toString() + " what: " + msg.what);
        return false;
    }
}
