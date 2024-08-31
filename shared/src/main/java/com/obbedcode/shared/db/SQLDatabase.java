package com.obbedcode.shared.db;

import android.database.sqlite.SQLiteDatabase;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.FileUtils;

import java.io.File;

//Should we use XLOG ? we need to make sure it dosnt dead lock with pushing logs to DB
public class SQLDatabase {
    private static final String TAG = "ObbedCode.XP.SQLDatabase";

    protected String name;
    protected String directory;
    protected File file;
    protected final Object lock = new Object();
    protected SQLiteDatabase db;


    public SQLDatabase(String databasePath) {
        file = new File(databasePath);
        name = file.getName();
        directory = file.getParent();
    }

    public boolean exists() { return file != null && FileUtils.existsBypassPermissionsCheck(file.getAbsolutePath()); }

    public boolean isOpen(boolean openIfNot) { return db != null && db.isOpen() || openIfNot && open(); }
    public boolean open() {
        try {
            if(!exists()) {
                XLog.e(TAG, "Database File Object is null or does not Exist... " + directory + " File: " + name);
                return false;
            }

            if(db == null) {
                synchronized (lock) {//Point of DeadLock (1)
                    db = SQLiteDatabase.openOrCreateDatabase(file, null);
                    XLog.i(TAG, "Database File Opened or Created: " + file.getAbsolutePath() + " IsOpen=" + db.isOpen());
                }
            }

            return db.isOpen();
        }catch (Exception e) {
            XLog.e(TAG, "Error trying to Open or Create Database: " + directory + " File: " + name + " Error: " + e.getMessage());
            return false;
        }
    }

    private void GivePermissions() {

    }
}
