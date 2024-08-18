package com.obbedcode.shared.process;

import android.app.ActivityManager;
import android.os.RemoteException;

import java.util.List;

import rikka.hidden.compat.ActivityManagerApis;

public class TaskManager {



    public static void test() throws RemoteException {
        List<ActivityManager.RunningTaskInfo> tasks = ActivityManagerApis.getTasks(100);
        for (ActivityManager.RunningTaskInfo t : tasks) {
        }
    }

}
