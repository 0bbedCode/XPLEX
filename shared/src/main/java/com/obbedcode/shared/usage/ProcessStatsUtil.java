package com.obbedcode.shared.usage;

import android.os.StrictMode;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class ProcessStatsUtil {
    private static final String TAG = "ObbedCode.XP.ProcStatsUtil";

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
}
