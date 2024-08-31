package com.obbedcode.shared.io;

import android.system.Os;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.FileUtils;

import java.io.File;

public class FileExt {
    private static final String TAG = "ObbedCode.XP.FileExt";
    private File mFile;

    public FileExt(int descriptorNumber) {
        mFile = new File(FileUtils.readSymbolicLink("/proc/self/fd/" + descriptorNumber));
    }

    public FileExt(String file) {
        mFile = new File(file);
    }

    public File getFile() { return mFile; }

    public void chmod(int mode) {
        try {
            Os.chmod(mFile.getAbsolutePath(), mode);
        }catch (Exception e) {
            XLog.e(TAG, "[CHMOD] Error: " + e.getMessage() + " File: " + mFile.getAbsolutePath());
        }
    }

    public void chown(int uid, int guid) {
        try {
            Os.chown(mFile.getAbsolutePath(), uid, guid);
        }catch (Exception e) {
            XLog.e(TAG, "[CHOWN] Error: " + e.getMessage() + " File: " + mFile.getAbsolutePath());
        }
    }
}
