package com.obbedcode.shared.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import androidx.annotation.NonNull;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.io.FileEx;
import com.obbedcode.shared.io.ModePermission;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.CursorUtils;
import com.obbedcode.shared.utils.XposedUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Should we use XLOG ? we need to make sure it dosnt dead lock with pushing logs to DB
public class SQLDatabase {
    private static final String TAG = "ObbedCode.XP.SQLDatabase";

    protected FileEx file;
    protected FileEx parentDirectory;
    protected final Object lock = new Object();
    protected SQLiteDatabase db;
    public ReentrantReadWriteLock dbLock = new ReentrantReadWriteLock(true);

    public SQLiteDatabase getRawDatabase() { return db; }

    public SQLDatabase(String databasePath) { this(databasePath, false); }
    public SQLDatabase(String databaseNameOrPath, boolean useCustomPath) {
        file = new FileEx(useCustomPath ?
                getDatabaseDirectoryCustom(null) + File.separator + databaseNameOrPath :
                databaseNameOrPath, false, false);
        parentDirectory = file.getDirectory();
        ensureDirectoryExists();
    }

    @SuppressWarnings("unused")
    public boolean isReady(String tableName, LinkedHashMap<String, String> columns) {
        if(!isReady()) return false;
        if(tableName == null) return true;//Assume they did not want to specify table name ???
        if(hasTable(tableName)) return true;
        if(columns == null) {
            XLog.e(TAG, "Table Does not exist and Columns is null... " + tableName + " " + this);
            return false;
        }

        return  createTable(columns, tableName);
    }

    @SuppressWarnings("unused")
    public boolean isReady() { return isReady(this); }

    @SuppressWarnings("unused")
    public FileEx getFile() { return file; }

    @SuppressWarnings("unused")
    public FileEx getParentDirectory() { return parentDirectory; }

    //Use proper as it MAY not exist at ONE point
    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public boolean exists() { return parentDirectory.exists() && file.exists(); }

    @SuppressWarnings("unused")
    public void ensureDirectoryExists() {
        try {
            if(!XposedUtils.isVirtualXposed()) {
                if(parentDirectory.mkdirs()) {
                    parentDirectory.takeOwnership();
                    parentDirectory.setPermissions(ModePermission.READ_WRITE_EXECUTE, ModePermission.READ_WRITE_EXECUTE, ModePermission.NONE);
                }
            }
        } catch (Exception e) {
            XLog.e(TAG, "Failed to Ensure Database Exists! " + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public boolean isOpen() { return isOpen(false); }

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public boolean isOpen(boolean openIfNot) {
        if(db != null && db.isOpen()) return true;
        if(openIfNot) return open();
        return false;
    }

    @SuppressWarnings("unused")
    public void togglePermissions() {
        if(file.exists()) {
            file.takeOwnership();
            file.setPermissions(ModePermission.READ_WRITE_EXECUTE, ModePermission.READ_WRITE_EXECUTE, ModePermission.NONE);
        }
    }

    @SuppressWarnings("unused")
    public boolean open() {
        try {
            if(!parentDirectory.exists()) {
                XLog.e(TAG, "Parent Directory does not Exist... " + file.getPath() + " File: " + file.getName());
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
            XLog.e(TAG, "Error trying to Open or Create Database: " + file.getPath() + " File: " + file.getName() + " Error: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean delete(String tableName) { return delete(tableName, null, null); }

    @SuppressWarnings("unused")
    public boolean delete(String tableName, String whereClause, String[] whereArgs) {
        try {
            long rows = db.delete(tableName, whereClause, whereArgs);
            if(rows < 0) {
                XLog.e(TAG, "Failed to Delete Row Possibly ? Table: " + tableName + " Return: " + rows + " Where Clause: " + whereClause + " Where Args: " + Str.joinArray(whereArgs));
                return false;
            }

            return true;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Delete Item from Table: " + tableName + " Selection Args: " + whereClause + " Compare Values: " + Str.joinArray(whereArgs) + " Error: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        try {
            long rows = db.updateWithOnConflict(tableName, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_REPLACE);
            if(rows != 1) {
                XLog.e(TAG, "Failed to update Row into Table: " + tableName + " Where Clause: " + whereClause + " Where Args: " + Str.joinArray(whereArgs));
                return false;
            }

            return true;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Update Row into Table: " + tableName + " Where Clause: " + whereClause + " Where Args: " + Str.joinArray(whereArgs) + " Error: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean insert(String tableName, ContentValues values) {
        try {
            long rows = db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if(rows < 0) {
                XLog.e(TAG, "Failed to insert Data into Table: " + tableName + " Returned: " + rows);
                return false;
            }

            return true;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Insert Row into Table: " + tableName + " Error: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public void writeLock() { dbLock.writeLock().lock(); }

    @SuppressWarnings("unused")
    public void writeUnlock() { dbLock.writeLock().unlock(); }

    @SuppressWarnings("unused")
    public void readLock() { dbLock.readLock().lock(); }

    @SuppressWarnings("unused")
    public void readUnlock() { dbLock.readLock().unlock(); }

    @SuppressWarnings("unused")
    public boolean beginTransaction() { return beginTransaction(false); }

    @SuppressWarnings("unused")
    public boolean beginTransaction(boolean writeLock) {
        try {
            if(!isOpen(true)) return false;
            if(writeLock) writeLock();
            db.beginTransaction();
            return true;
        }catch (Exception e) {
            if(writeLock) writeUnlock();
            XLog.e(TAG, "Failed to Begin Database Transaction: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public void  setTransactionSuccessful() {
        try { db.setTransactionSuccessful();
        }catch (Exception e) {
            XLog.e(TAG, "Failed to set the Database Transaction as Successful: " + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    public boolean endTransaction() { return endTransaction(false, false); }

    @SuppressWarnings("unused")
    public boolean endTransaction(boolean writeUnlock, boolean wasSuccessfulTransaction) {
        try {
            if(!isOpen(false)) return false;
            if(wasSuccessfulTransaction) setTransactionSuccessful();
            db.endTransaction();
            if(writeUnlock) writeUnlock();
            return true;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to End Database Transaction: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean dropTable(String tableName) {
        if(!isOpen(true)) return false;
        try{
            String query = "DROP TABLE IF EXISTS " + tableName;
            db.execSQL(query);
            return true;
        }catch (Exception e){
            XLog.e(TAG, "Failed to drop Table: " + tableName);
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean createTable(Map<String, String> columns, String name) {
        if(!isOpen(true)) return false;
        if(!Str.isValidNotWhitespaces(name)){
            XLog.e(TAG, "[createTable] Not a valid Table Name");
            return false;
        }

        String qry = dynamicCreateQuery(columns, name);
        try {
            db.execSQL(qry);
            return true;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to create Table: props >> " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean tableIsEmpty(String tableName) { return tableEntries(tableName) <= 0; }

    @SuppressWarnings("unused")
    public int tableEntries(String tableName) {
        if(!isOpen(true) || !hasTable(tableName)) return -1;
        int count = 0;
        Cursor cursor = null;
        try {
            String query = "SELECT COUNT(*) FROM " + tableName;
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) count = cursor.getInt(0);
            return count;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to get Table Item Count.. " + e.getMessage());
            return count;
        }finally {
            CursorUtils.closeCursor(cursor);
        }
    }

    @SuppressWarnings("unused")
    public boolean hasTable(String tableName) {
        if(!isOpen(true)) return false;
        Cursor cursor = null;
        try {
            String qry = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name=?";
            cursor = db.rawQuery(qry, new String[] { tableName });
            if (!cursor.moveToFirst()) return false;
            int count = cursor.getInt(0);
            return count > 0;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to check for DB Table: " + e.getMessage());
            return false;
        } finally {
            CursorUtils.closeCursor(cursor);
        }
    }

    @SuppressWarnings("unused")
    public boolean close() {
        try {
            if(!isOpen(false)) return false;
            db.close();
            return true;
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Close DB: " + e.getMessage());
            return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder.create()
                .appendFieldLine("Name", this.file.getName())
                .appendFieldLine("Path", this.parentDirectory.getAbsolutePath())
                .appendFieldLine("Open", this.isOpen())
                .toString();
    }

    @SuppressWarnings("unused")
    public static String dynamicCreateQuery(Map<String, String> columns, String tableName) {
        String top = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
        StringBuilder mid = new StringBuilder();

        String pValue = columns.remove("PRIMARY");
        int i = 1;
        int sz = columns.size();
        for(Map.Entry<String, String> r : columns.entrySet()) {
            String l = r.getKey() + " " + r.getValue();
            mid.append(l);
            //if(i == 1) mid.append(" PRIMARY KEY");
            //if(sz != i)  mid.append(",");
            if(i < sz) mid.append(", ");
            i++;
        }

        if(pValue != null) {
            mid.append(", PRIMARY ").append(pValue);
        }

        return top + mid + ");";
    }

    @SuppressWarnings("unused")
    public static boolean isReady(SQLDatabase db) {
        if(db == null) {
            XLog.e(TAG, "[isReady] Database Object is null...");
            return false;
        }

        if(!db.exists()) {
            XLog.e(TAG, "[isReady] Database Does not Exist: " + db.file.getAbsolutePath());
            return false;
        }

        if(!db.isOpen(true)) {
            XLog.e(TAG, "[isOpen] Database is not Open: " + db.file.getAbsoluteFile());
            return false;
        }

        return true;
    }

    @SuppressWarnings("unused")
    public static String getDatabaseDirectoryCustom(Context context) {
        try {
            //Stop using Context it can get messy
            //See workarounds
            if(context != null && XposedUtils.isVirtualXposed())
                return context.getFilesDir().getPath();
            else {
                String base = Environment.getDataDirectory() + File.separator + "system" + File.separator;
                File[] fls = new File(base).listFiles();
                if(fls != null) {
                    for (File f : fls) {
                        if(f.isDirectory() && f.getName().startsWith("XPX-")) {
                            return f.getAbsolutePath();
                        }
                    }
                }

                String name = "XPX-" + UUID.randomUUID().toString();
                String full = base + name;
                File baseDir = new File(base + name);
                if(!baseDir.mkdirs() && !baseDir.isDirectory()) return null;
                return full;
            }
        }catch (Exception e) {
            XLog.e(TAG, "Failed to Get the Custom Database Directory :( " + e.getMessage(), true);
            return null;
        }
    }
}
