package com.obbedcode.shared;

import android.os.Binder;

import com.obbedcode.shared.logger.XLog;

import java.util.concurrent.Callable;

public class GhostCallerUid {
    public static <T> T invokeCallable(Callable<T> callable) {
        long oldIdentity = Binder.clearCallingIdentity();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Error executing callable with cleared UID", e);
            //XLog.e();
        } finally {
            Binder.restoreCallingIdentity(oldIdentity);
        }
    }

    public interface Action {
        void run() throws Exception;
    }

    public void startAction(Action action) {
        long oldIden = Binder.clearCallingIdentity();
        try {
            action.run();
        } catch (Exception e) {
            throw new RuntimeException("Error executing action with cleared UID", e);
        } finally {
            Binder.restoreCallingIdentity(oldIden);
        }
    }

    public <T> T startCallable(Callable<T> callable) {
        long oldIden = Binder.clearCallingIdentity();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Error executing callable with cleared UID", e);
            //XLog.e();
        } finally {
            Binder.restoreCallingIdentity(oldIden);
        }
    }
}
