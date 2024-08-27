package com.obbedcode.shared.utils;

import android.app.ActivityThread;
import android.os.Binder;
import android.os.Process;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructStat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;

public class FileUtils {
    private static final String TAG = "ObbedCode.XP.FileUtils";

    /**
     * Reads the target of a symbolic link. This will attempt to try to use the Canonical Method first read below example of when why to use Canonical method.
     * You can use the readSymbolicLink(String, bool) Overload to specify second parameter for Canonical
     *
     * <p>
     *     Canonical if selected as a param to try will remove/resolve any trailing periods like "../".
     *     If you were to resolve example "/proc/self/fd/10" Non Canonical output would look something like "../SomeFolder/SomeFile.txt".
     *     If you were to resolve example "/proc/self/fd/10" with Canonical output it would look something like "/sdcard/SomeFolder/SomeFile.txt" it resolves the ".."
     * </p>
     *
     * @param pathFile Target File with Path to resolve the Symbolic Link for.
     * @return The target path of the symbolic link, or null if the link cannot be read.
     */
    public static String readSymbolicLink(String pathFile) { return readSymbolicLink(pathFile, true); }

    /**
     * Reads the target of a symbolic link.
     *
     * <p>
     *     Canonical if selected as a param to try will remove/resolve any trailing periods like "../".
     *     If you were to resolve example "/proc/self/fd/10" Non Canonical output would look something like "../SomeFolder/SomeFile.txt".
     *     If you were to resolve example "/proc/self/fd/10" with Canonical output it would look something like "/sdcard/SomeFolder/SomeFile.txt" it resolves the ".."
     * </p>
     *
     * @param pathFile Target File with Path to resolve the Symbolic Link for.
     * @param tryCanonical Try using the Canonical Method first else use Os.readlink if failed and or set to false.
     * @return The target path of the symbolic link, or null if the link cannot be read.
     */
    @NonNull
    public static String readSymbolicLink(String pathFile, boolean tryCanonical)  {
        long oldId = Binder.clearCallingIdentity();
        try {
            if (!existsBypassPermissionsCheck(pathFile)) {
                XLog.e(TAG, "Failed to Read File Symbolic Link, as File Does not exist. File: " + pathFile);
                return Str.EMPTY;
            }
            if(tryCanonical) {
                try {
                    File file = new File(pathFile);
                    String link = file.getCanonicalPath();
                    if(Str.isValid(link))
                        return link;
                }catch (Exception e) {
                    XLog.e(TAG, "Error Reading Canonical Path (Symbolic Link Extended) Using [File.getCanonicalPath]. File: " + pathFile + " Error: " +  e.getMessage() + " Trying Backup [Os.readlink]", true);
                }
            }
            try {
                return Os.readlink(pathFile);
            } catch (Exception e) {
                XLog.e(TAG, "Error Reading Symbolic Link using [Os.readlink].  File: " + pathFile + " Error: " + e.getMessage(), true);
                return Str.EMPTY;
            }
        }finally {
            Binder.restoreCallingIdentity(oldId);
        }
    }

    /**
     * Read target File contents / Data as a UTF8 String.
     *
     * @param pathFile The File including its full Path.
     * @return The contents of the file as a String.
     */
    public static String readFileContentsAsString(String pathFile) { return readFileContentsAsString(pathFile,  StandardCharsets.UTF_8); }

    /**
     * Read target File contents / Data as a String.
     *
     * @param pathFile The File including its full Path.
     * @param charSet Set the Char Set / Encoding of String Data
     * @return The contents of the file as a String.
     */
    @NonNull
    public static String readFileContentsAsString(String pathFile, Charset charSet) {
        File file = new File(pathFile);
        if (!existsBypassPermissionsCheck(pathFile)) {
            XLog.e(TAG, "Failed to read File Contents as String, as File does not Exist. File: " + pathFile);
            return Str.EMPTY;
        }

        try  (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return new String(data, charSet);
        }catch (Exception e) {
            XLog.e(TAG, "Error Reading File Contents as a String. File: " + pathFile + " CharSet: " + charSet.name() + " Error: " + e.getMessage(), true);
            return Str.EMPTY;
        }
    }

    /**
     * Read target File contents / Data as a UTF8 String. This is to Read Virtual Files on the System
     *
     * @param pathFile The File including its full Path.
     * @return The contents of the file as a String.
     */
    public static String readVirtualFileContentsAsString(String pathFile) { return readVirtualFileContentsAsString(pathFile, StandardCharsets.UTF_8); }

    /**
     * Read target File contents / Data as a String. This is to Read Virtual Files on the System
     *
     * @param pathFile The File including its full Path.
     * @param charSet Set the Char Set / Encoding of String Data
     * @return The contents of the file as a String.
     */
    @NonNull
    public static String readVirtualFileContentsAsString(String pathFile, Charset charSet) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathFile), charSet))) {
            int c;
            while ((c = reader.read()) != -1) {
                if (c == 0) {
                    content.append(' ');//or New Line
                } else {
                    content.append((char) c);
                }
            }

            return content.toString().trim();
        } catch (IOException e) {
            XLog.i(TAG, "Failed to Read: " + pathFile +  " Has Read: " + hasRead(pathFile) + " Error: " + e.getMessage());
            return content.toString();
        }
    }

    /**
     * Check if you have Read access to a File
     *
     * @param pathFile The File including its full Path.
     * @return True if can read else false
     */
    public static boolean hasRead(String pathFile) {
        try {
            StructStat stat = Os.stat(pathFile);
            if ((stat.st_mode & 0400) == 0)
                return false;
            return true;
        }catch (Exception ignored) {  }
        return false;
    }

    /**
     * This will Bypass Stupid Fucking Android File.exists() issues
     * Some permissions thing despite it ironically detecting if its a directory or file there for it exists...
     * I tried clearing Caller UID but not help, Despite the Process being (1000) android (system_server)
     * Try new File("/proc/3/cwd").exists() returns False but .isDirectory() returns true....
     *
     * @param fileOrDirectory File or Directory you want to check if it exists
     * @return The contents of the file as a String.
     */
    public static boolean existsBypassPermissionsCheck(String fileOrDirectory) {
        File f = new File(fileOrDirectory);
        try {
            return Os.stat(fileOrDirectory) != null || f.isFile() || f.isDirectory();
        } catch (Exception ignored) {   }
        return f.isFile() || f.isDirectory();
    }
}
