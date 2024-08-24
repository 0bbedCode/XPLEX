package com.obbedcode.shared.usage;

import android.os.StrictMode;

import androidx.annotation.Nullable;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;

public class ProcessUtils {
    //https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/jni/android_util_Process.cpp
    private static final String TAG = "ObbedCode.XP.ProcUtils";

    // Constants for buffer sizes (adjust as needed)
    private static final int K_PROC_READ_STACK_BUFFER_SIZE = 1024;           //2048;
    private static final int K_PROC_READ_MIN_HEAP_BUFFER_SIZE = 4096;
    private static final int MAX_READABLE_PROCFILE_SIZE = 64 * 1024 * 1024;  // 64 MB

    // Constants for parsing (these should match the PROC_* constants in C++)
    private static final int PROC_PARENS = 0x200;
    private static final int PROC_QUOTES = 0x400;
    private static final int PROC_TERM_MASK = 0xff;
    private static final int PROC_COMBINE = 0x100;
    private static final int PROC_OUT_STRING = 0x1000;
    private static final int PROC_OUT_LONG = 0x2000;
    private static final int PROC_OUT_FLOAT = 0x4000;
    private static final int PROC_CHAR = 0x800;

    /**
     * Reads and parses a proc file given the file path, format, and output arrays.
     *
     * @param filePath   The path of the proc file to read.
     * @param format     The format array defining how the file should be parsed.
     * @param outStrings Array to hold output strings (optional).
     * @param outLongs   Array to hold output long values (optional).
     * @param outFloats  Array to hold output float values (optional).
     * @return true if the file was read and parsed successfully, false otherwise.
     */
    public static boolean readProcFile(String filePath, int[] format, String[] outStrings,
                                       long[] outLongs, float[] outFloats) {
        if (filePath == null || format == null) {
            throw new NullPointerException("File path and format must not be null.");
        }

        try (FileInputStream fis = new FileInputStream(filePath);
             FileChannel channel = fis.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(K_PROC_READ_STACK_BUFFER_SIZE);
            long offset = 0;
            int bytesRead;

            while ((bytesRead = channel.read(buffer)) > 0) {
                offset += bytesRead;
                if (!buffer.hasRemaining()) {
                    if (buffer.capacity() >= MAX_READABLE_PROCFILE_SIZE) {
                        XLog.e(TAG, "Proc file too big: " + filePath, true);
                        return false;
                    }
                    ByteBuffer newBuffer = ByteBuffer.allocate(Math.max(buffer.capacity() * 2, K_PROC_READ_MIN_HEAP_BUFFER_SIZE));
                    buffer.flip();
                    newBuffer.put(buffer);
                    buffer = newBuffer;
                }
            }

            if (bytesRead < 0 && offset == 0) {
                XLog.e(TAG, "Unable to read process file: " + filePath, true);
                return false;
            }

            buffer.flip();
            return parseProcLineArray(buffer, 0, buffer.limit(), format, outStrings, outLongs, outFloats);

        } catch (IOException e) {
            XLog.e(TAG, "Error reading proc file: " + filePath + " Error: " + e, true);
            return false;
        }
    }

    /**
     * Parses the given buffer based on the format array and populates the output arrays.
     *
     * @param buffer     The buffer containing the proc file content.
     * @param startIndex The starting index in the buffer.
     * @param endIndex   The ending index in the buffer.
     * @param format     The format array defining how to parse the buffer.
     * @param outStrings Array to hold output strings.
     * @param outLongs   Array to hold output long values.
     * @param outFloats  Array to hold output float values.
     * @return true if parsing was successful, false otherwise.
     */
    private static boolean parseProcLineArray(ByteBuffer buffer, int startIndex, int endIndex,
                                              int[] format, String[] outStrings,
                                              long[] outLongs, float[] outFloats) {
        int i = startIndex;
        int di = 0;

        for (int fi = 0; fi < format.length; fi++) {
            int mode = format[fi];

            if ((mode & PROC_PARENS) != 0) {
                i++;
            } else if ((mode & PROC_QUOTES) != 0) {
                if (buffer.get(i) == '"') {
                    i++;
                } else {
                    mode &= ~PROC_QUOTES;
                }
            }

            char term = (char) (mode & PROC_TERM_MASK);
            int start = i;

            if (i >= endIndex) {
                XLog.e(TAG, "Ran off end of data @" + i, true);
                return false;
            }

            int end = -1;
            if ((mode & PROC_PARENS) != 0) {
                while (i < endIndex && buffer.get(i) != ')') {
                    i++;
                }
                end = i;
                i++;
            } else if ((mode & PROC_QUOTES) != 0) {
                while (i < endIndex && buffer.get(i) != '"') {
                    i++;
                }
                end = i;
                i++;
            }
            while (i < endIndex && buffer.get(i) != term) {
                i++;
            }
            if (end < 0) {
                end = i;
            }

            if (i < endIndex) {
                i++;
                if ((mode & PROC_COMBINE) != 0) {
                    while (i < endIndex && buffer.get(i) == term) {
                        i++;
                    }
                }
            }

            if ((mode & (PROC_OUT_FLOAT | PROC_OUT_LONG | PROC_OUT_STRING)) != 0) {
                String value = extractString(buffer, start, end);

                if ((mode & PROC_OUT_FLOAT) != 0 && di < outFloats.length) {
                    try {
                        outFloats[di] = Float.parseFloat(value);
                    } catch (NumberFormatException e) {
                        XLog.e(TAG, "Error parsing float: " + value + " Error: " + e, true);
                        return false;
                    }
                }

                if ((mode & PROC_OUT_LONG) != 0 && di < outLongs.length) {
                    if ((mode & PROC_CHAR) != 0) {
                        outLongs[di] = buffer.get(start);
                    } else {
                        try {
                            outLongs[di] = Long.parseLong(value);
                        } catch (NumberFormatException e) {
                            XLog.e(TAG, "Error parsing long: " + value + " Error: " + e, true);
                            return false;
                        }
                    }
                }

                if ((mode & PROC_OUT_STRING) != 0 && di < outStrings.length) {
                    outStrings[di] = value;
                }

                di++;
            }
        }

        return true;
    }

    /**
     * Helper method to extract a string from the ByteBuffer.
     *
     * @param buffer The buffer containing the string data.
     * @param start  The starting index of the string in the buffer.
     * @return The extracted string.
     */
    private static String extractString(ByteBuffer buffer, int start, int end) {
        byte[] bytes = new byte[end - start];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get(start + i);
        }
        return new String(bytes);
    }


    /**
     * Get the list of PIDs from the specified path.
     *
     * @param path      The path to the directory containing PIDs (e.g., "/proc").
     * @param lastArray The previous array of PIDs (optional).
     * @return The updated array of PIDs.
     */
    public static int[] getPids(String path, int[] lastArray) {
        if (path == null) {
            throw new NullPointerException("Path must not be null");
        }

        File dir = new File(path);
        if (!dir.isDirectory()) {
            return null;
        }

        ArrayList<Integer> pidList = new ArrayList<>();
        if (lastArray != null) {
            for (int pid : lastArray) {
                pidList.add(pid);
            }
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (isNumeric(name)) {
                    try {
                        int pid = Integer.parseInt(name);
                        pidList.add(pid);
                    } catch (NumberFormatException e) {
                        // Ignore this entry if it can't be parsed as a PID
                    }
                }
            }
        }

        // Sort the PID list
        Collections.sort(pidList);

        // Convert the list to an array
        int[] pids = new int[pidList.size()];
        for (int i = 0; i < pidList.size(); i++) {
            pids[i] = pidList.get(i);
        }

        return pids;
    }

    /**
     * Check if a string is numeric (i.e., contains only digits).
     *
     * @param str The string to check.
     * @return True if the string is numeric, false otherwise.
     */
    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * How much to read into a buffer when reading a proc file
     */
    private static final int READ_SIZE = 1024;

    /**
     * Read a {@code proc} file that terminates with a specific byte
     *
     * @param path path of the file to read
     * @param terminator byte that terminates the file. We stop reading once this character is
     * seen, or at the end of the file
     */
    @Nullable
    public static String readTerminatedProcFile(String path, byte terminator) {
        // Permit disk reads here, as /proc isn't really "on disk" and should be fast.
        // TODO: make BlockGuard ignore /proc/ and /sys/ files perhaps?
        //final int savedPolicy = StrictMode.allowThreadDiskReadsMask();
        StrictMode.ThreadPolicy savedPolicy = StrictMode.allowThreadDiskReads();
        try {
            return readTerminatedProcFileInternal(path, terminator);
        } finally {
            //StrictMode.setThreadPolicyMask(savedPolicy);
            StrictMode.setThreadPolicy(savedPolicy);
        }
    }

    private static String readTerminatedProcFileInternal(String path, byte terminator) {
        try (FileInputStream is = new FileInputStream(path)) {
            ByteArrayOutputStream byteStream = null;
            final byte[] buffer = new byte[READ_SIZE];
            while (true) {
                // Read file into buffer
                final int len = is.read(buffer);
                if (len <= 0) {
                    // If we've read nothing, we're done
                    break;
                }

                // Find the terminating character
                int terminatingIndex = -1;
                for (int i = 0; i < len; i++) {
                    if (buffer[i] == terminator) {
                        terminatingIndex = i;
                        break;
                    }
                }
                final boolean foundTerminator = terminatingIndex != -1;

                // If we have found it and the byte stream isn't initialized, we don't need to
                // initialize it and can return the string here
                if (foundTerminator && byteStream == null) {
                    return new String(buffer, 0, terminatingIndex);
                }

                // Initialize the byte stream
                if (byteStream == null) {
                    byteStream = new ByteArrayOutputStream(READ_SIZE);
                }

                // Write the whole buffer if terminator not found, or up to the terminator if found
                byteStream.write(buffer, 0, foundTerminator ? terminatingIndex : len);

                // If we've found the terminator, we can finish
                if (foundTerminator) {
                    break;
                }
            }

            // If the byte stream is null at the end, this means that we have read an empty file
            if (byteStream == null) {
                return "";
            }
            return byteStream.toString();
        } catch (IOException e) {
            //if (DEBUG) {
            //    Slog.d(TAG, "Failed to open proc file", e);
            //}
            return null;
        }
    }

    public static int parseCpuFile(String file) {
        File present = new File(file);
        if(present.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(present));
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        if(line.contains("-")) {
                            String[] parts = line.split("-");
                            return Integer.parseInt(parts[1]) + 1;
                        }else if(!line.isEmpty()) {
                            return Integer.parseInt(line) + 1;
                        }
                    }catch (Exception innerEx) { XLog.e(TAG, "Failed to Parse Line in File: " + file + " Line: " + line + " Error: " + innerEx.getMessage()); }
                }
            }catch (Exception ex) { XLog.e(TAG, "Failed to read CPU File: " + file + " Error: " + ex.getMessage()); }
            finally { StreamUtils.dispose(reader); }
        } return -1;
    }
}
