package com.obbedcode.shared.usage;

import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.CollectionUtils;
import com.obbedcode.shared.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*
 * Credits to https://github.com/MuntashirAkon/AppManager For the Concepts what to look for and Parsing for the STAT, STATUS, STATM files etc...
 * Best App Manager for Android, Open Source, Great Concepts, Help to make my own /proc/pid Parser Implementations since he already did a lot of the research
 */
public class ProcVirtualFileProcess {
    private static final String TAG = "ObbedCode.XP.ProcVirtualFileProcess";

    public static final String PROC_DIRECTORY =  File.separator + "proc";
    public static final File PROC_FILE = new File(PROC_DIRECTORY);

    public final int pid;
    public final String rootPath;
    public final File rootDirectory;

    public static final String PROC_STATUS = "status";
    public static final String PROC_STAT = "stat";
    public static final String PROC_STAT_MEM = "statm";
    public static final String PROC_CMD_LINE = "cmdline";
    public static final String PROC_COMM = "comm";
    public static final String PROC_ENVIRON = "environ";
    public static final String PROC_WCHAN = "wchan";

    /*Symbolic Link Resolvers used on this. exe for executable path, cwd for current working directory and root for root directory*/
    public static final String PROC_EXE = "exe";
    public static final String PROC_CWD = "cwd";
    public static final String PROC_ROOT = "root";

    /*Usually SELinux Context / Policy String*/
    public static final String PROC_ATTR = "attr";
    public static final String PROC_ATTR_CURRENT = "current";
    public static final String PROC_ATTR_PREVIOUS = "previous";

    public static final String PROC_FD = "fd";
    public static final String PROD_FD_INFO = "fdinfo";

    public ProcVirtualFileProcess(int pid) {
        this.pid = pid;
        this.rootPath = PROC_DIRECTORY + File.separator + pid;
        this.rootDirectory = new File(this.rootPath);
    }

    @SuppressWarnings("unused")
    public boolean existsOrIsRunning() { return this.rootDirectory.isDirectory(); }

    @Nullable
    @SuppressWarnings("unused")
    public ProcStatus getStatus() {
        String data = readFileData(PROC_STATUS);
        return data == null ? null : ProcStatus.parse(data);
    }

    @Nullable
    @SuppressWarnings("unused")
    public ProcStat getStat() {
        String data = readFileData(PROC_STAT);
        return data == null ? null : ProcStat.parse(data.toCharArray());
    }

    @Nullable
    @SuppressWarnings("unused")
    public ProcMemStat getMemStat() {
        String data = readFileData(PROC_STAT_MEM);
        return data == null ? null : ProcMemStat.parse(data);
    }

    @Nullable
    @SuppressWarnings("unused")
    public String getCmdLine() { return readFileData(PROC_CMD_LINE); }

    @Nullable
    @SuppressWarnings("unused")
    public String getComm() { return readFileData(PROC_COMM); }

    @Nullable
    @SuppressWarnings("unused")
    public String getExeSymLinkExecutablePath() { return FileUtils.readSymbolicLink(rootPath + File.separator + PROC_EXE); }

    @Nullable
    @SuppressWarnings("unused")
    public String getCwdSymLinkCurrentWorkingDirectory() { return FileUtils.readSymbolicLink(rootPath + File.separator + PROC_CWD); }

    @Nullable
    @SuppressWarnings("unused")
    public String getRootSymLinkRootDirectory() { return FileUtils.readSymbolicLink(rootPath + File.separator + PROC_ROOT); }

    @Nullable
    @SuppressWarnings("unused")
    public String[] getEnvironmentVariables() {
        String data = readFileData(PROC_ENVIRON);
        return data != null ? data.split("\0") : null;
    }

    @Nullable
    @SuppressWarnings("unused")
    public String getWChan() { return readFileData(PROC_WCHAN); }

    @Nullable
    @SuppressWarnings("unused")
    public String getCurrentContext() { return readFileData(PROC_ATTR + File.separator + PROC_ATTR_CURRENT, true); }

    @Nullable
    @SuppressWarnings("unused")
    public String getPreviousContext() { return readFileData(PROC_ATTR + File.separator + PROC_ATTR_PREVIOUS, true); }

    @Nullable
    @SuppressWarnings("unused")
    public List<ProcFd> readFileDescriptors(boolean resolveExtraInfo) { return ProcFd.getProcessFileDescriptors(this.pid, resolveExtraInfo); }

    @Nullable
    @SuppressWarnings("unused")
    private String readFileData(String subPath) { return readFileData(subPath, false); }

    @Nullable
    @SuppressWarnings("unused")
    private String readFileData(String subPath, boolean trim) {
        File file = new File(rootPath + File.separator + subPath);
        String data = FileUtils.readVirtualFileContentsAsString(file.getAbsolutePath());
        return !Str.isValid(data) ? null : trim ? data.trim() : data;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Integer) return (int)obj == this.pid;
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder()
                .append("PID:").append(this.pid).append(Str.NEW_LINE)
                .append("Proc Path:").append(this.rootPath).append(Str.NEW_LINE)
                .append("Executable Path:").append(this.getExeSymLinkExecutablePath()).append(Str.NEW_LINE)
                .append("Current Working Directory:").append(this.getCwdSymLinkCurrentWorkingDirectory()).append(Str.NEW_LINE)
                .append("Root Directory:").append(this.getRootSymLinkRootDirectory()).append(Str.NEW_LINE)
                .append("Command Line:").append(this.getCmdLine()).append(Str.NEW_LINE)
                .toString();
    }

    @NonNull
    public static int[] getRunningProcessIds() {
        int[] processIds = ProcessApi.getPids(PROC_DIRECTORY, null);
        if(CollectionUtils.isValidArray(processIds))
            return processIds;

        File[] files = PROC_FILE.listFiles((dir, name) -> TextUtils.isDigitsOnly(name));
        if(!CollectionUtils.isValidArray(files))
            return new int[]{Process.myPid()};

        processIds = new int[files.length];
        for(int i = 0; i < files.length; i++)
            processIds[i] = Integer.decode(files[i].getName());

        return processIds;
    }

    @NonNull
    @SuppressWarnings("unused")
    public static List<ProcVirtualFileProcess> getRunningProcesses() {
        int[] processIds = getRunningProcessIds();
        List<ProcVirtualFileProcess> processes = new ArrayList<>();
        if(!CollectionUtils.isValidArray(processIds)) return processes;
        for(int i = 0; i < processIds.length; i++) {
            ProcVirtualFileProcess proc = new ProcVirtualFileProcess(processIds[i]);
            if(proc.existsOrIsRunning())
                processes.add(proc);
        }

        return processes;
    }
}
