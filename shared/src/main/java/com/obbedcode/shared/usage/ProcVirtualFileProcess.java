package com.obbedcode.shared.usage;

import androidx.annotation.Nullable;

import com.obbedcode.shared.utils.FileUtils;

import java.io.File;

public class ProcVirtualFileProcess {
    public int pid;
    public String rootPath;
    public File rootDirectory;

    public static final String PROC_STATUS = "status";
    public static final String PROC_STAT = "stat";
    public static final String PROC_STAT_MEM = "statm";
    public static final String PROC_CMD_LINE = "cmdline";
    public static final String PROC_COMM = "comm";
    public static final String PROC_ENVIRON = "environ";

    public static final String PROC_EXE = "exe";
    public static final String PROC_CWD = "cwd";
    public static final String PROC_ROOT = "root";

    public static final String PROC_ATTR = "attr";
    public static final String PROC_ATTR_CURRENT = "current";
    public static final String PROC_ATTR_PREVIOUS = "previous";

    public ProcVirtualFileProcess(int pid) {
        this.pid = pid;
        this.rootPath = "/proc/" + pid;
        this.rootDirectory = new File(this.rootPath);
    }

    @SuppressWarnings("unused")
    public boolean existsOrIsRunning() { return this.rootDirectory.exists(); }

    @Nullable
    @SuppressWarnings("unused")
    public ProcStatus getStatus() {
        String data = readFileData(PROC_STATUS);
        if(data == null) return null;
        return ProcStatus.parse(data);
    }

    @Nullable
    @SuppressWarnings("unused")
    public ProcStat getStat() {
        String data = readFileData(PROC_STAT);
        if(data == null) return null;
        return ProcStat.parse(data.toCharArray());
    }

    @Nullable
    @SuppressWarnings("unused")
    public ProcMemStat getMemStat() {
        String data = readFileData(PROC_STAT_MEM);
        if(data == null) return null;
        return ProcMemStat.parse(data);
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
    public String getCurrentContext() { return readFileData(PROC_ATTR + File.separator + PROC_ATTR_CURRENT, true); }

    @Nullable
    @SuppressWarnings("unused")
    public String getPreviousContext() { return readFileData(PROC_ATTR + File.separator + PROC_ATTR_PREVIOUS, true); }

    public RunningProcess toRunningProcess() {
        return null;
    }

    @Nullable
    @SuppressWarnings("unused")
    private String readFileData(String subPath) { return readFileData(subPath, false); }

    @Nullable
    @SuppressWarnings("unused")
    private String readFileData(String subPath, boolean trim) {
        File file = new File(rootPath + "/" + subPath);
        if(file.exists() && file.isFile()) {
            String data = FileUtils.readFileContentsAsString(file.getAbsolutePath());
            if(!data.isEmpty())
                return trim ? data.trim() : data;
        } return null;
    }
}
