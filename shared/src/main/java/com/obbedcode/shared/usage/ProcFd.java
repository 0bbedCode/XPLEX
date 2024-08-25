package com.obbedcode.shared.usage;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.CollectionUtils;
import com.obbedcode.shared.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProcFd implements Parcelable {
    private static final String TAG = "ObbedCode.XP.ProcFd";

    public int pid;
    public String rootFdDirectory;
    public String rootFdInfoDirectory;

    public int descriptorNumber;
    public String symbolicLink;

    /*/proc/pid/fdinfo/ Parts*/
    public long position = 0;           //Current Position the target Process is in within the File (literal) So if I read or write 8 bytes of the file Position would be offset (8)
    public int flags = 0;               //What flags were used to access the File
    public int mountId = 0;             //Number Identifier of the Mount, usually when system mounts partitions what not it classifies them via ID Number

    public ProcFd(Parcel in) {
        this.pid = in.readInt();
        this.descriptorNumber = in.readInt();
        this.symbolicLink = in.readString();
        this.position = in.readLong();
        this.flags = in.readInt();
        this.mountId = in.readInt();
    }

    @SuppressWarnings("unused")
    public ProcFd(int pid, int fileDescriptorNumber) { this(getRootFdDirectory(pid), getRootFdInfoDirectory(pid), pid, fileDescriptorNumber); }
    public ProcFd(String rootFdDirectory, String rootFdInfoDirectory, int pid, int fileDescriptorNumber) {
        this.pid = pid;
        this.rootFdDirectory = rootFdDirectory;
        this.rootFdInfoDirectory = rootFdInfoDirectory;
        this.descriptorNumber = fileDescriptorNumber;
        this.symbolicLink = FileUtils.readSymbolicLink(this.rootFdDirectory + File.separator + fileDescriptorNumber);
    }

    private void resolveExtraInfo() {
        if(!exists()) return;
        String data = FileUtils.readFileContentsAsString(this.rootFdInfoDirectory + File.separator + this.descriptorNumber);
        if(data.isEmpty()) return;
        String[] parts = data.split("\\n");
        for(String line : parts) {
            if(Str.isValid(line) && line.contains(":")) {
                // Use regex to split by any amount of whitespace around the colon
                String[] subParts = line.split("\\s*:\\s*");
                if(subParts.length > 1) {
                    String name = subParts[0].trim();
                    String value = subParts[1].trim();
                    try {
                        switch (name.toLowerCase()) {
                            case "pos":
                                this.position = Long.decode(value);
                                break;
                            case "flags":
                                this.flags = Integer.decode(value);
                                break;
                            case "mnt_id":
                                this.mountId = Integer.decode(value);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        XLog.e(TAG, "Failed to parse Line from /proc/pid/fdinfo/descriptor File. Line Failure: " + line + " Error: " + e.getMessage());
                    }
                }
            }
        }
    }

    public boolean exists() { return FileUtils.existsBypassPermissionsCheck(this.rootFdDirectory + File.separator + this.descriptorNumber); }
    public boolean isSocket() { return Str.isValid(this.symbolicLink) && this.symbolicLink.startsWith("socket:"); }
    public boolean isPipe() { return Str.isValid(this.symbolicLink) && this.symbolicLink.startsWith("pipe:"); }
    public boolean isFile() { return Str.isValid(this.symbolicLink) && (this.symbolicLink.startsWith("..") || this.symbolicLink.startsWith(File.separator)); }

    public static String getRootFdDirectory(int pid) { return ProcVirtualFileProcess.PROC_DIRECTORY + File.separator + pid + File.separator + ProcVirtualFileProcess.PROC_FD; }
    public static String getRootFdInfoDirectory(int pid) { return ProcVirtualFileProcess.PROC_DIRECTORY + File.separator + pid + File.separator + ProcVirtualFileProcess.PROD_FD_INFO; }

    public static final Creator<ProcFd> CREATOR = new Creator<ProcFd>() {
        @Override
        public ProcFd createFromParcel(Parcel in) { return new ProcFd(in); }
        @Override
        public ProcFd[] newArray(int size) { return new ProcFd[size]; }
    };

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.pid);
        dest.writeInt(this.descriptorNumber);
        dest.writeString(this.symbolicLink);
        dest.writeLong(this.position);
        dest.writeInt(this.flags);
        dest.writeInt(this.mountId);
    }

    @NonNull
    public static List<ProcFd> getProcessFileDescriptors(int pid, boolean resolveExtraInfo) {
        String rootFd = getRootFdDirectory(pid);
        String rootFdInfo = getRootFdInfoDirectory(pid);
        File[] files = new File(rootFd).listFiles((dir, file) -> TextUtils.isDigitsOnly(file));
        List<ProcFd> procFiles = new ArrayList<>();
        if(!CollectionUtils.isValidArray(files)) return procFiles;
        for(File f : files) {
            if(FileUtils.existsBypassPermissionsCheck(f.getAbsolutePath())) {
                ProcFd pfd = new ProcFd(rootFd, rootFdInfo, pid, Integer.decode(f.getName()));
                if(resolveExtraInfo) {
                    pfd.resolveExtraInfo();
                }
            }
        }

        return procFiles;
    }
}