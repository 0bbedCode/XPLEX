package com.obbedcode.shared.process;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManagerHidden;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.content.Context;
import android.os.Build;
import android.os.FileObserver;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.DynamicMethod;
import com.obbedcode.shared.reflect.ReflectUtil;
import com.obbedcode.shared.utils.CollectionUtils;
import com.obbedcode.shared.utils.HiddenApiUtils;
import com.obbedcode.shared.utils.ThreadUtils;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rikka.hidden.compat.ActivityManagerApis;
import rikka.hidden.compat.adapter.UidObserverAdapter;

public class UidProcessObserver {
    private static final String TAG = "ObbedCode.XP.UidProcessObserver";

    public static final int UID_OBSERVER_OLD_KIND = 0x1;
    public static final int UID_OBSERVER_AM_KIND = 0x2;
    public static final int UID_OBSERVER_PROC_KIND = 0x3;

    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(5);
    private int mUid = 0;

    private boolean mUseOldMethod = false;
    private UidObserverAdapter mUidObserverAdapter = null;


    private INotifyUid mNotifyUidEvent;
    private boolean mKeepMonitoring = false;

    public UidProcessObserver(int uid) { mUid = uid; mUseOldMethod = Build.VERSION.SDK_INT == Build.VERSION_CODES.M; }

    public UidProcessObserver useOldMethod(boolean useOld) { this.mUseOldMethod = useOld; return this; }
    public UidProcessObserver setOnNotifyEvent(INotifyUid notifyEvent) { this.mNotifyUidEvent = notifyEvent; return this; }

    public void stopMonitorOnActive() {
        mKeepMonitoring = false;
        if(mUidObserverAdapter != null) {
            try {
                ActivityManagerApis.unregisterUidObserver(mUidObserverAdapter);
                mUidObserverAdapter = null;
            }catch (Exception ignored) {  }
        }
    }

    public void startMonitorOnActive() { startMonitorOnActive(true); }

    @SuppressLint("NewApi")
    public void startMonitorOnActive(boolean stopWhenFound) {
        if(!mKeepMonitoring) {
            mKeepMonitoring = true;
            XLog.i(TAG, "Starting UID monitor for UID: " + mUid + " Use Old: " + mUseOldMethod, true);
            if(mUseOldMethod) {
                try {
                    Object am = HiddenApiUtils.getIActivityManager();
                    if(am == null) {
                        mKeepMonitoring = false;
                        XLog.e(TAG, "Failed to Get IActivity Manager Class Interface...", true, true);
                        return;
                    }

                    DynamicMethod mth = new DynamicMethod(am.getClass(), "getRunningAppProcesses")
                            .bindInstance(am);

                    boolean foundBefore = false;
                    boolean foundCurrent = false;
                    int lastFailCount = 0;

                    if(mth.isValid()) {
                        while (mKeepMonitoring) {
                            //foundCurrent = false;
                            List<ActivityManager.RunningAppProcessInfo> runningProcesses = mth.tryInstanceInvoke();
                            if(!CollectionUtils.isValid(runningProcesses)) {
                                ThreadUtils.sleep(1000);
                                lastFailCount++;
                                if(lastFailCount > 10) {
                                    mKeepMonitoring = false;
                                    XLog.e(TAG, "UID Observing Failed...", true, true);
                                    return;
                                } continue;
                            }

                            if(lastFailCount > 0) lastFailCount = 0;
                            foundCurrent = false;
                            for(ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                                if(processInfo.uid == mUid) {
                                    foundCurrent = true;
                                    if(!foundBefore) {
                                        foundBefore = true;
                                        XLog.i(TAG, "Found UID From Old method: " + mUid, true);
                                        if(mNotifyUidEvent != null) mNotifyUidEvent.onUidActive(mUid);
                                        if(stopWhenFound) {
                                            XLog.i(TAG, "Monitor for UID: " + mUid + " Will stop now...", true);
                                            mKeepMonitoring = false;
                                            return;
                                        }
                                    } break;
                                }
                            }

                            if(!foundCurrent && foundBefore)
                                foundBefore = false;//reset last time flag

                            ThreadUtils.sleep(800);
                        }
                    } else {
                        mKeepMonitoring = false;
                        XLog.e(TAG, "Failed to Create Dynamic Method for UID Observing [getRunningAppProcesses]", true, true);
                    }
                }catch (Exception e) {
                    mKeepMonitoring = false;
                    XLog.e(TAG, "Failed to Use the Old method to Monitor for new UIDs, Error: " + e.getMessage(), true, true);
                }
            } else {
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    try {
                        XLog.i(TAG, "Starting UidObserver from Activity Manager UID: " + mUid, true);
                        //this keeps throwing some fucking remote exception no matter what not here but else where
                        mUidObserverAdapter = new UidObserverAdapter() {
                            @Override
                            public void onUidActive(int uid) throws RemoteException {
                                //super.onUidActive(uid);
                                if(mUid == uid) {
                                    XLog.i(TAG, "UID Active: " + uid, true);
                                    mNotifyUidEvent.onUidActive(uid);
                                    if(stopWhenFound)
                                        mKeepMonitoring = false;
                                }

                                XLog.i(TAG, "UID FALSE: " + uid, true);
                                if(!mKeepMonitoring)
                                    ActivityManagerApis.unregisterUidObserver(this);
                            }
                        };

                        XLog.i(TAG, "Registering the UID Observer for Activity Manager UID: " + mUid, true);
                        ActivityManagerApis.registerUidObserver(
                                mUidObserverAdapter,
                                ActivityManagerHidden.UID_OBSERVER_ACTIVE,
                                ActivityManagerHidden.PROCESS_STATE_UNKNOWN,
                                null);
                    }catch (Exception e) {
                        mKeepMonitoring = false;
                        XLog.e(TAG, "Failed to use Activity Manager UidObserver, Error: " + e.getMessage(), true, true);
                    }
                }
            }
        }
    }
}
