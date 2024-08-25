package com.obbedcode.shared.usage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RunningProcess implements Parcelable {
    private static final String TAG = "ObbedCode.XP.RunningProcess";

    public int pid;
    public String cmdline;

    public String executablePath;
    public String currentWorkingDirectory;
    public String rootDirectory;

    public String currentContext;
    public String previousContext;

    public int uid = 0;
    public String cmdLineName;
    public String commName;
    public String statusName;

    public boolean isLinuxProcess = true;

    public RunningProcess() { }
    public RunningProcess(Parcel in) {
        this.pid = in.readInt();
        this.cmdline = in.readString();

        this.executablePath = in.readString();
        this.currentWorkingDirectory = in.readString();
        this.rootDirectory = in.readString();

        this.currentContext = in.readString();
        this.previousContext = in.readString();

        this.uid = in.readInt();
        this.cmdLineName = in.readString();
        this.commName = in.readString();
        this.statusName = in.readString();

        this.isLinuxProcess = in.readByte() != 0;
    }

    public RunningProcess(ProcVirtualFileProcess procFs) {
        this.pid = procFs.pid;
        this.cmdline = procFs.getCmdLine();

        this.executablePath = procFs.getExeSymLinkExecutablePath();
        this.currentWorkingDirectory = procFs.getCwdSymLinkCurrentWorkingDirectory();
        this.rootDirectory = procFs.getRootSymLinkRootDirectory();

        this.currentContext = procFs.getCurrentContext();
        this.previousContext = procFs.getPreviousContext();

        this.isLinuxProcess = cmdline != null && cmdline.startsWith(File.separator) && executablePath != null && !executablePath.contains("app_process");
        this.cmdLineName = ProcessUtils.parseCommandLineName(this.cmdline);

        this.commName = procFs.getComm();
        ProcStatus status = procFs.getStatus();
        if(status != null) {
            this.uid = status.getUid();
            this.statusName = status.getName();
        }
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
        dest.writeInt(this.pid);
        dest.writeString(this.cmdline);

        dest.writeString(this.executablePath);
        dest.writeString(this.currentWorkingDirectory);
        dest.writeString(this.rootDirectory);

        dest.writeString(this.currentContext);
        dest.writeString(this.previousContext);

        dest.writeInt(this.uid);
        dest.writeString(this.cmdLineName);
        dest.writeString(this.commName);
        dest.writeString(this.statusName);

        dest.writeByte((byte)(this.isLinuxProcess ? 1 : 0));
    }


    @NonNull
    @Override
    public String toString() {
        return new StringBuilder()
                .append("CMD Line Name:").append(this.cmdLineName).append(Str.NEW_LINE)
                .append("COMM Name:").append(this.commName).append(Str.NEW_LINE)
                .append("Status Name:").append(this.statusName).append(Str.NEW_LINE)
                .append("PID:").append(this.pid).append(Str.NEW_LINE)
                .append("UID:").append(this.uid).append(Str.NEW_LINE)
                .append("CMD Line:").append(this.cmdline).append(Str.NEW_LINE)
                .append("Executable Directory:").append(this.executablePath).append(Str.NEW_LINE)
                .append("Current Working Directory:").append(this.currentWorkingDirectory).append(Str.NEW_LINE)
                .append("Root Directory:").append(this.rootDirectory).append(Str.NEW_LINE)
                .append("Is Linux:").append(this.isLinuxProcess).append(Str.NEW_LINE)
                .toString();
    }

    public static List<RunningProcess> getRunningProcesses(boolean getLinuxProcesses) {
        List<ProcVirtualFileProcess> procProcesses = ProcVirtualFileProcess.getRunningProcesses();
        //XLog.i(TAG, "Total Proc Processes to Parse: " + procProcesses.size());
        List<RunningProcess> runningProcesses = new ArrayList<>();
        if(!CollectionUtils.isValid(procProcesses)) return runningProcesses;
        for(ProcVirtualFileProcess vp : procProcesses) {
            if(vp.existsOrIsRunning()) {
                RunningProcess rp = new RunningProcess(vp);
                if(vp.existsOrIsRunning())
                    runningProcesses.add(rp);
            }
        }

        return runningProcesses;
    }
}