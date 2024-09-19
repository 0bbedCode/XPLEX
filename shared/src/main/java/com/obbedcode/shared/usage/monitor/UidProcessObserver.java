package com.obbedcode.shared.usage.monitor;

import android.app.ActivityManager;
import android.os.Build;
import android.os.RemoteException;

import com.obbedcode.shared.GhostCallerUid;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.reflect.DynamicMethod;
import com.obbedcode.shared.reflect.ReflectUtil;
import com.obbedcode.shared.utils.CollectionUtils;
import com.obbedcode.shared.reflect.HiddenApiUtils;
import com.obbedcode.shared.utils.ThreadUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rikka.hidden.compat.ActivityManagerApis;
import rikka.hidden.compat.adapter.UidObserverAdapter;

public class UidProcessObserver {
    private static final String TAG = "ObbedCode.XP.UidProcessObserver";

    /*
    *   Use this method to Init Fields as using rikka.hidden.compat ActivityManagerHidden Does not return Proper Field Values
    *   ActivityManagerHidden.UID_OBSERVER_ACTIVE or ActivityManagerHidden.PROCESS_STATE_UNKNOWN both always Return (0)
    *   Despite SDK being checked for 24+ and Running on 31+ Still returns (0) So instead we try to grab field Value Dynamically else use a Hard Coded Value from Android Source
    *   Found this out by hooking ActivityManagerServer.registerUidObserverForUids() / UidObserverController.register() Seeing (HMA) Passes Which (8) and Cutpoint (-1) but for me its (0) and (0)
    */
    public static final int UID_OBSERVER_ACTIVE = ReflectUtil.useFieldValueOrDefaultInt(ActivityManager.class,"UID_OBSERVER_ACTIVE", 1<<3);
    public static final int PROCESS_STATE_UNKNOWN = ReflectUtil.useFieldValueOrDefaultInt(ActivityManager.class, "PROCESS_STATE_UNKNOWN", -1);

    private int mUid = 0;
    private boolean mUseOldMethod = false;
    private UidObserverAdapter mUidObserverAdapter = null;
    private INotifyUid mNotifyUidEvent;
    private boolean mIsMonitoring = false;

    private ExecutorService mRunningService = Executors.newSingleThreadExecutor();

    public UidProcessObserver(int uid) { mUid = uid; mUseOldMethod = Build.VERSION.SDK_INT == Build.VERSION_CODES.M; }

    public UidProcessObserver useOldMethod(boolean useOld) { this.mUseOldMethod = useOld; return this; }
    public UidProcessObserver setOnNotifyEvent(INotifyUid notifyEvent) { this.mNotifyUidEvent = notifyEvent; return this; }

    @SuppressWarnings("unused")
    public void stop() {
        mIsMonitoring = false;
        if(mUidObserverAdapter != null) {
            try {
                ActivityManagerApis.unregisterUidObserver(mUidObserverAdapter);
                mUidObserverAdapter = null;
            }catch (Exception ignored) {  }
        }

        try {
            mRunningService.shutdownNow();
            mRunningService.shutdown();
            mRunningService = Executors.newSingleThreadExecutor();
        }catch (Exception ignored) { }
    }

    @SuppressWarnings("unused")
    public void startAsync(boolean stopWhenFound) {
        if(!mIsMonitoring) {
            mRunningService.submit(() -> { start(stopWhenFound); });
        }
    }

    @SuppressWarnings("unused")
    public void start() { start(true); }
    public void start(final boolean stopWhenFound) {
        if(mNotifyUidEvent == null) {
            XLog.e(TAG, "Notify Event is Null please set the Event so it can be Invoked when UID is found...", true, true);
            return;
        }

        if(mUid <= 0 || mUid > 100000) {
            XLog.e(TAG, "UID is invalid please set a Valid UID between 1 and 10000, UID: " + mUid, true, true);
            return;
        }

        if(mIsMonitoring) {
            XLog.e(TAG, "Cant Monitor for UID when its already monitoring else...", true, true);
            return;
        }

        mIsMonitoring = true;
        XLog.i(TAG, "Starting UID monitor for UID [" + mUid + "] use old method ? " + String.valueOf(mUseOldMethod), true);
        try {
            if(mUseOldMethod) {
                Object am = HiddenApiUtils.getIActivityManager();
                if(am == null) {
                    XLog.e(TAG, "Failed to get IActivity Manager Class Interface...", true, true);
                    return;
                }

                DynamicMethod mth = new DynamicMethod(am.getClass(), "getRunningAppProcesses")
                        .bindInstance(am);

                if(mth.isValid()) {
                    new GhostCallerUid().startAction(() -> {
                        int fails = 0;
                        boolean wasFound = false;
                        while (mIsMonitoring && fails < 50) {
                            try {
                                boolean hasFound = false;
                                List<ActivityManager.RunningAppProcessInfo> processes = mth.tryInstanceInvoke();
                                if(!CollectionUtils.isValid(processes)) {
                                    XLog.e(TAG, "Collection Returned from [getRunningAppProcesses] returned null or empty...", true, true);
                                    fails++;
                                    continue;
                                }

                                for(ActivityManager.RunningAppProcessInfo process : processes) {
                                    if(process.uid == mUid) {
                                        hasFound = true;
                                        if(!wasFound) {
                                            wasFound = true;
                                            if(stopWhenFound) mIsMonitoring = false;
                                            if(mNotifyUidEvent != null) mNotifyUidEvent.onUidActive(mUid);
                                        } break;
                                    }
                                }

                                if(!hasFound && wasFound) {
                                    //Meaning it HAS not found the UID BUT it WAS found Before now lets reset the flag
                                    wasFound = false;
                                }

                                ThreadUtils.sleep(800);
                            }catch (Exception innerE) {
                                XLog.e(TAG, "Error for Monitoring [getRunningAppProcesses] Error: " + innerE.getMessage() + " UID: " + mUid, true, true);
                                fails++;
                            }
                        }
                    });
                } else {
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        try {
                            mUidObserverAdapter = new UidObserverAdapter() {
                                @Override
                                public void onUidActive(int uid) throws RemoteException {
                                    if(mUid == uid) {
                                        mNotifyUidEvent.onUidActive(uid);
                                        if(stopWhenFound) mIsMonitoring = false;
                                        if(!mIsMonitoring) stop();  // ActivityManagerApis.unregisterUidObserver(this);
                                    }
                                }
                            };

                            XLog.i(TAG, "Starting UID Observer [registerUidObserver] UID: " + mUid + " [UID_OBSERVER_ACTIVE][" + UID_OBSERVER_ACTIVE + "] [PROCESS_STATE_UNKNOWN][" + PROCESS_STATE_UNKNOWN + "]", true);
                            ActivityManagerApis.registerUidObserver(
                                    mUidObserverAdapter,
                                    UID_OBSERVER_ACTIVE,
                                    PROCESS_STATE_UNKNOWN,
                                    null);
                        }catch (Exception innerE) {
                            XLog.e(TAG, "Error for Monitoring [registerUidObserver] Error: " + innerE.getMessage() + " UID: " + mUid, true, true);
                            mIsMonitoring = false;
                        }
                    } else {
                        XLog.e(TAG, "Failed to Monitor UID as Android is less than [M] API Level [23], yet Old method has not been selected for Monitoring.", true, true);
                        mIsMonitoring = false;
                    }
                }
            }
        }
        catch (Exception e) {
            XLog.e(TAG, "Failed to Monitor UID, internal Exception. Error: " + e.getMessage() + " UID: " + mUid, true, true);
            mIsMonitoring = false;
        }
    }
}
