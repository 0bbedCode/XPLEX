package com.obbedcode.shared.io;

import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;

import com.obbedcode.shared.BuildConfig;
import com.obbedcode.shared.Str;
import com.obbedcode.shared.io.builders.ChmodModeBuilder;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.StreamUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileApi {
    private static final String TAG = "ObbedCode.XP.FileApi";

    public static ChmodModeBuilder customChmodMode() { return ChmodModeBuilder.create(); }

    //0770 is mode from default XPL-EX (7 + 7 + 0) (RWX[Owner], RWX[Owner], None[Other])
    //Issue is if a ROOT File manager create dir it CAN cause permissions issues given ROOT > SYSTEM
    public static final int MODE_ALL_RWX = customChmodMode()
            .setOwnerPermissions(ModePermission.READ_WRITE_EXECUTE) //7
            .setGroupPermissions(ModePermission.READ_WRITE_EXECUTE) //7
            .setOtherPermissions(ModePermission.READ_WRITE_EXECUTE) //7
            .getMode();                                             //777

    public static final int MODE_ALL_RW = customChmodMode()
            .setOwnerPermissions(ModePermission.READ_WRITE)         //6
            .setGroupPermissions(ModePermission.READ_WRITE)         //6
            .setOtherPermissions(ModePermission.READ_WRITE)         //6
            .getMode();                                             //666

    public static final int MODE_ALL_R = customChmodMode()
            .setOwnerPermissions(ModePermission.READ)               //4
            .setGroupPermissions(ModePermission.READ)               //4
            .setOtherPermissions(ModePermission.READ)               //4
            .getMode();                                             //444

    public static void chmod(String fileOrDirectory, int mode, boolean recursive) {
        executeCommand("chmod " + (recursive ? "-R " : "") + mode + " " + fileOrDirectory);
    }

    public static void chown(String fileOrDirectory, UserId userId, UserId groupId, boolean recursive) {
        chown(fileOrDirectory, userId.getValue(), groupId.getValue(), recursive);
    }

    public static void chown(String fileOrDirectory, int userId, int groupId, boolean recursive) {
        String u = userId == -1 ? "" : String.valueOf(userId);
        String g = groupId == -1 ? "" : String.valueOf(groupId);
        executeCommand("chown " + (recursive ? "-R " : "" ) + u + ":" + g + " " + fileOrDirectory);
    }

    public static void chown(String fileOrDirectory, String user, String group, boolean recursive) {
        String u = user == null ? "" : user;
        String g = group == null ? "" : group;
        executeCommand("chown " + (recursive ? "-R " : "" ) + u + ":" + g + " " + fileOrDirectory);
    }

    public static void rmDirectoryForcefully(String directory) { rmDirectoryForcefully(directory, Process.myUid(), Process.myUid()); }
    public static void rmDirectoryForcefully(String directory, UserId userId, UserId groupId) { rmDirectoryForcefully(directory, userId.getValue(), groupId.getValue()); }
    public static void rmDirectoryForcefully(String directory, int userId, int groupId) {
        chown(directory, userId, groupId, true);
        chmod(directory, MODE_ALL_RWX, true);
        rm(directory, true);
    }

    public static void rmFileForcefully(String file) {  rmFileForcefully(file, Process.myUid(), Process.myUid()); }
    public static void rmFileForcefully(String file, UserId userId, UserId groupId) { rmFileForcefully(file, userId.getValue(), groupId.getValue()); }
    public static void rmFileForcefully(String file, int userId, int groupId) {
        chown(file, userId, groupId, false);
        chmod(file, MODE_ALL_RWX, false);
        rm(file, false);
    }

    public static boolean deleteFileOrDirectoryForcefully(String fileOrDirectory) {
        if(exists(fileOrDirectory)) {
            File f = new File(fileOrDirectory);
            //Should we use "myUid" or Hardcode one ?
            if(isDirectory(fileOrDirectory) || f.isDirectory())
                rmDirectoryForcefully(fileOrDirectory);
            else if(isFile(fileOrDirectory) || f.isFile())
                rmFileForcefully(fileOrDirectory);
            else {
                rmDirectoryForcefully(fileOrDirectory);
                rmDirectoryForcefully(fileOrDirectory);
            } return exists(fileOrDirectory);
        } return true;
    }

    public static boolean exists(String fileOrDirectory) {
        File f = new File(fileOrDirectory);
        try { return Os.stat(fileOrDirectory) != null || f.isFile() || f.isDirectory(); } catch (Exception ignored) {   }
        return f.isFile() || f.isDirectory();
    }

    public static boolean isDirectory(String directory) {
        if(directory == null) return false;
        try {
            StructStat stat = Os.stat(directory);
            return OsConstants.S_ISDIR(stat.st_mode);
        }catch (Exception ignored) { }
        return false;
    }

    public static boolean isFile(String file) {
        if(file == null) return false;
        try {
            StructStat stat = Os.stat(file);
            return OsConstants.S_ISREG(stat.st_mode);
        }catch (Exception ignored) { }
        return false;
    }

    public static void rm(String fileOrDirectory, boolean recursive) {
        //-r : Removes directories and their content recursively.
        //-f : Forces the removal of all files or directories.
        executeCommand("rm " + (recursive ? "-rf " : "-f") + fileOrDirectory);
    }

    public static String getParent(String fileOrDirectory) {
        if(fileOrDirectory == null) return File.separator;
        String delimiter = getPathDelimiter(fileOrDirectory);
        if(delimiter == null) return fileOrDirectory;
        List<String> parts = getParts(fileOrDirectory, delimiter);
        if(parts.isEmpty() || parts.size() == 1) return delimiter;
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter);
        int sz = parts.size() - 2;
        for(int i = 0; i < parts.size() - 1; i++) {
            sb.append(parts.get(i));
            if(i != sz) {
                sb.append(delimiter);
            }
        } return sb.toString();
    }

    public static List<String> getParts(String fileOrDirectory) { return getParts(fileOrDirectory, null); }

    public static List<String> getParts(String fileOrDirectory, String overrideDelimiter) {
        List<String> parts = new ArrayList<>();
        if(fileOrDirectory != null && !fileOrDirectory.isEmpty()) {
            String del = overrideDelimiter == null ? getPathDelimiter(fileOrDirectory) : overrideDelimiter;
            if(del != null) {
                String trimmed = Str.trim(fileOrDirectory, del, true, false);
                if(!trimmed.contains(del)) {
                    parts.add(trimmed);
                    return parts;
                }

                String[] splits = trimmed.split(Pattern.quote(del));
                for(String s : splits)
                    if(!s.isEmpty())
                        parts.add(s);
            }
        } return parts;
    }

    public static String buildPath(List<String> paths, String separator) {
        if(paths != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(separator);
            int lst = paths.size() - 1;
            for(int i = 0; i < paths.size(); i++) {
                sb.append(paths.get(i));
                if(i != lst) sb.append(separator);
            } return sb.toString();
        } return null;
    }

    //MEat Emoji Hex Bytes = (F0 9F A5 A9)

    public static String buildPath(String... paths) {
        if(paths != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(File.separator);
            int lst = paths.length - 1;
            for(int i = 0; i < paths.length; i++) {
                sb.append(paths[i]);
                if(i != lst) sb.append(File.separator);
            } return sb.toString();
        } return null;
    }

    public static FileDescriptor generateFakeFileDescriptor(String contents) {
        File mockFile = generateTempFakeFile(contents);
        if(mockFile == null)
            return null;
        try {
            ParcelFileDescriptor pFileDescriptor = ParcelFileDescriptor.open(mockFile, ParcelFileDescriptor.MODE_READ_ONLY);//0x10000000
            return pFileDescriptor.getFileDescriptor();
        }catch (Exception e) {
            XLog.e(TAG,"Failed to Create Fake File Descriptor!! Error: " + e, true);
            return null;
        }
    }


    public static File generateTempFakeFile(String contents) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            File temp = File.createTempFile("temp", null);
            fos = new FileOutputStream(temp, false);
            osw = new OutputStreamWriter(fos);
            osw.write(contents);
            return temp;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Create Fake File! Error: " + e, true) ;
            return null;
        }finally {
            StreamUtils.close(osw, true);
            StreamUtils.close(fos);
        }
    }

    public static String getPathDelimiter(String path) { return getPathDelimiter(path, false); }
    public static String getPathDelimiter(String path, boolean useDefaultIfNull) { return path.contains(File.separator) ? File.separator : File.separator.equals("/") ? useDefaultIfNull ? File.separator : null : path.contains("/") ? "/" : useDefaultIfNull ? File.separator : null; }

    private static void executeCommand(String command) {
        try {
            Runtime.getRuntime().exec(command).waitFor();
        }catch (Exception e) {
            if(BuildConfig.DEBUG)
                XLog.e(TAG, "[executeCommand] Failed: " + e.getMessage());
        }
    }
}
