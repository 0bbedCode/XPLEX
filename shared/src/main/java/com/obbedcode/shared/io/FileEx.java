package com.obbedcode.shared.io;

import android.os.Process;
import android.system.Os;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class FileEx extends File {
    private static final String TAG = "ObbedCode.XP.FileEx";

    public static final String SELF_FD_PATH = FileApi.buildPath("proc", "self", "fd") + separator;
    public static final String SDCARD_PATH = FileApi.buildPath("sdcard") + separator;

    public FileEx(int descriptorNumber) { super(FileUtils.readSymbolicLink( SELF_FD_PATH + descriptorNumber)); }
    public FileEx(String file) { super(ensureFormat(file, false, true)); }
    public FileEx(String file, boolean readAsSymbolic, boolean parseIfFileDescriptor) { super(ensureFormat(file, readAsSymbolic, parseIfFileDescriptor));  }
    public FileEx(File file) { super(file.getAbsolutePath()); }

    @Override
    public boolean delete() { return FileApi.deleteFileOrDirectoryForcefully(getAbsolutePath()); }

    @NonNull
    @Override
    public String getCanonicalPath() throws IOException { return FileUtils.readSymbolicLink(getAbsolutePath(), true); }

    @NonNull
    @Override
    public File getCanonicalFile() throws IOException { return new File(getCanonicalPath()); }

    @Override
    public boolean exists() { return FileApi.exists(getAbsolutePath()); }

    @NonNull
    @SuppressWarnings("unused")
    public String readVirtualFileContents(Charset chars) { return exists() ? FileUtils.readVirtualFileContentsAsString(getAbsolutePath(), chars) : ""; }

    @NonNull
    @SuppressWarnings("unused")
    public String readFileContents(Charset chars) { return exists() ? FileUtils.readFileContentsAsString(getAbsolutePath(), chars) : ""; }

    @Override
    public boolean isFile() { return FileApi.isFile(getAbsolutePath()) || super.isFile(); }

    @Override
    public boolean isDirectory() { return FileApi.isDirectory(getAbsolutePath()) || super.isDirectory(); }

    @Override
    public boolean mkdirs() { return exists() || super.mkdirs(); }

    public FileEx getParentEx() { return getParentEx(false); }
    public FileEx getParentEx(boolean isFileOverride) {
        if(isFileOverride || isFile()) return new FileEx(FileApi.getParent(FileApi.getParent(getAbsolutePath())));
        return new FileEx(FileApi.getParent(getAbsolutePath()));
    }

    public void takeOwnership(UserId userId, UserId groupId) { FileApi.chown(getAbsolutePath(), userId, groupId, isDirectory()); }

    public void takeOwnership() { FileApi.chown(getAbsolutePath(), Process.myUid(), Process.myUid(), isDirectory()); }

    public void setPermissions(ModePermission ownerPermissions, ModePermission groupPermissions, ModePermission otherPermissions) {
        FileApi.chmod(getAbsolutePath(), ChmodModeBuilder.create()
                .setOwnerPermissions(otherPermissions)
                .setGroupPermissions(groupPermissions)
                .setOtherPermissions(otherPermissions)
                .getMode(), isDirectory());
    }

    public static String ensureFormat(String fileOrDirectory, boolean readAsSymbolic, boolean parseIfFileDescriptor) {
        if(fileOrDirectory == null || fileOrDirectory.isEmpty()) return separator;
        fileOrDirectory = fileOrDirectory.trim();
        if(fileOrDirectory.isEmpty()) return separator;
        String del = FileApi.getPathDelimiter(fileOrDirectory, true);
        String cleaned = Str.trim(fileOrDirectory, del, true, false);
        if(cleaned == null || cleaned.isEmpty()) return fileOrDirectory; //Maybe just separator

        //something/something/something
        //or
        //something
        String withStart = separator + cleaned;
        if(readAsSymbolic) {
            return FileUtils.readSymbolicLink(withStart, true);
        } else {
            if(parseIfFileDescriptor) {
                if(cleaned.length() > 4) {
                    String low = cleaned.toLowerCase();
                    if(low.startsWith("proc") && low.contains(separator + "fd" + separator)) {
                        List<String> pts = FileApi.getParts(low);
                        if(pts.size() == 4) {
                            // =>  /proc/somePid/fd/fileNumber
                            if(pts.get(pts.size() - 2).equalsIgnoreCase("fd")) {
                                String link = FileUtils.readSymbolicLink(withStart, true);
                                return link.isEmpty() ? withStart : link;
                            }
                        }
                    }
                }

                if(!cleaned.contains(separator)) {
                    char[] chrs = cleaned.toCharArray();
                    boolean isAllNum = true;
                    for(int i = 0; i < chrs.length; i++) {
                        if(!Character.isDigit(chrs[i])) {
                            isAllNum = false;
                            break;
                        }
                    }

                    if(isAllNum) {
                        String fd = SELF_FD_PATH + cleaned;
                        if(FileApi.exists(fd)) {
                            String link = FileUtils.readSymbolicLink(fd, true);
                            return link.isEmpty() ? withStart : link;
                        }
                    }
                }
            }
        }

        return withStart;
    }
}
