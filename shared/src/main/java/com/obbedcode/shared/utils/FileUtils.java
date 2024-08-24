package com.obbedcode.shared.utils;

import android.system.Os;

import androidx.annotation.NonNull;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

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
        File file = new File(pathFile);
        if (!file.exists()) {
            XLog.e(TAG, "Failed to Read File Symbolic Link, as File Does not exist. File: " + pathFile);
            return Str.EMPTY;
        }
        if(tryCanonical) {
            try {
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
        //Allow specification for CHAR SET
        File file = new File(pathFile);
        if (!file.exists()) {
            XLog.e(TAG, "Failed to read File Contents as String, as File does not Exist. File: " + pathFile);
            return Str.EMPTY;
        }

        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                return new String(data, charSet);
            }
        }catch (Exception e) {
            XLog.e(TAG, "Error Reading File Contents as a String. File: " + pathFile + " CharSet: " + charSet.name() + " Error: " + e.getMessage(), true);
            return "";
        }
    }
}
