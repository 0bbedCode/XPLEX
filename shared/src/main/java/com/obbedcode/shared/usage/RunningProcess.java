package com.obbedcode.shared.usage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.helpers.StrBuilder;
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
        return StrBuilder.create()
                .appendFieldLine("Command Line Name", this.cmdLineName)
                .appendFieldLine("Comm Name", this.commName)
                .appendFieldLine("Status Name", this.statusName)
                .appendFieldLine("PID", this.pid)
                .appendFieldLine("UID", this.uid)
                .appendFieldLine("Command Line", this.cmdline)
                .appendFieldLine("Executable Working Directory", this.executablePath)
                .appendFieldLine("Current Working Directory", this.currentWorkingDirectory)
                .appendFieldLine("Root Directory", this.rootDirectory)
                .appendFieldLine("Is Linux", this.isLinuxProcess)
                .toString();
    }

    public static List<RunningProcess> getRunningProcesses(boolean getLinuxProcesses) {
        //Do something with "getLinuxProcesses"
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