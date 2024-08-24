package com.obbedcode.shared.usage;

import android.app.ActivityManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.obbedcode.shared.utils.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunningProcess implements Parcelable {
    private static final String TAG = "ObbedCode.XP.RunningProcess";

    public static final String PROC_COMM_PROCESS_NAME = "comm";
    public static final String PROC_CMD_LINE_PROCESS_PATH = "cmdline";
    public static final String PROC_EXE_PROCESS_PATH_LINK = "exe";

    public String name;
    public String path;
    public String cmdline;
    public int pid;
    public int uid = 0;
    public boolean isLinuxProcess = true;

    public RunningProcess() { }
    public RunningProcess(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.cmdline = in.readString();
        this.pid = in.readInt();
        this.uid = in.readInt();
        this.isLinuxProcess = in.readByte() != 0;
    }

    public static final Creator<RunningProcess> CREATOR = new Creator<RunningProcess>() {
        @Override
        public RunningProcess createFromParcel(Parcel in) { return new RunningProcess(in); }
        @Override
        public RunningProcess[] newArray(int size) { return new RunningProcess[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.cmdline);
        dest.writeInt(this.pid);
        dest.writeInt(this.uid);
        dest.writeByte((byte)(this.isLinuxProcess ? 1 : 0));
    }

    public static List<RunningProcess> getRunningProcesses(boolean getLinuxProcesses) {
        HashMap<Integer, RunningProcess> processes = new HashMap<>();
        int[] pidArray = ProcessApi.getPids("/proc", null);
        List<ActivityManager.RunningAppProcessInfo> managedProcesses = ProcessApi.getRunningAppProcesses();

        for(int i = 0 ; i < pidArray.length; i++) {
            int p = pidArray[i];
            if(p > 1) {
                RunningProcess process = new RunningProcess();
                process.pid = p;
                process.name = FileUtils.readFileContentsAsString("/proc/" + p + "/" + PROC_COMM_PROCESS_NAME);
                process.path = FileUtils.readSymbolicLink("/proc/" + p + "/" + PROC_EXE_PROCESS_PATH_LINK);
                process.cmdline = FileUtils.readFileContentsAsString("/proc/" + p + "/" + PROC_CMD_LINE_PROCESS_PATH);
                processes.put(p, process);
            }
        }

        for(ActivityManager.RunningAppProcessInfo ai : managedProcesses) {
            //XLog.i(TAG, "MANAGED PROCESS: " + ai.uid + " NAME: " + ai.processName);
            RunningProcess process = processes.get(ai.pid);
            if(process != null) {
                process.name = ai.processName;
                process.uid = ai.uid;
                process.path = "/data/";
                process.isLinuxProcess = false;
            } else {
                process = new RunningProcess();
                process.pid = ai.pid;
                process.uid = ai.uid;
                process.name = ai.processName;
                process.path = "/data/";
                process.isLinuxProcess = false;
                processes.put(ai.pid, process);
            }
        }

        List<RunningProcess> lst = new ArrayList<>(processes.values());
        for (RunningProcess p : lst)
            Log.d(TAG, p.toString());

        return lst;
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder()
                .append("Name:").append(this.name).append("\n")
                .append("Path:").append(this.path).append("\n")
                .append("PID:").append(this.pid).append("\n")
                .append("UID:").append(this.uid).append("\n")
                .append("Is Linux:").append(this.isLinuxProcess).append("\n")
                .toString();
    }
}
