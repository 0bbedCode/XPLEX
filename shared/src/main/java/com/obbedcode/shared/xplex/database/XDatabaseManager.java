package com.obbedcode.shared.xplex.database;

import android.content.Context;

import com.obbedcode.shared.db.IDatabaseManage;
import com.obbedcode.shared.db.SQLDatabase;
import com.obbedcode.shared.db.SQLSnake;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.data.XAssignment;
import com.obbedcode.shared.xplex.data.XSetting;
import com.obbedcode.shared.xplex.data.XStartupSetting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public class XDatabaseManager implements IDatabaseManage {
    private final SQLDatabase mDatabase = new SQLDatabase("xp", true);
    private final Object mLockMain = new Object();

    public static XDatabaseManager instance = new XDatabaseManager();

    public SQLDatabase getDatabase() { return mDatabase; }

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public boolean isDatabaseReady() { return mDatabase.isReady(); }

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public boolean canQueryTable(String tableName, LinkedHashMap<String, String> columns) { return mDatabase.isReady(tableName, columns); }

    public boolean putStartupSetting(Integer user, String category, String receiverName, Integer state, boolean deleteSetting) { return putStartupSetting(XStartupSetting.create(user, category, receiverName, state), deleteSetting); }
    public boolean putStartupSetting(XStartupSetting startupSetting, boolean deleteSetting) {
        synchronized (mLockMain) {
            if(!canQueryTable(XStartupSetting.Table.NAME, XStartupSetting.Table.COLUMNS)) return false;
            return SQLSnake
                    .create(mDatabase, XStartupSetting.Table.NAME)
                    .setActionToDeleteElseInsert(deleteSetting)
                    .pinObject(startupSetting, deleteSetting, deleteSetting)
                    .executeAction();
        }
    }

    public boolean putSetting(Integer userId, String packageName, String settingName, String value, boolean deleteSetting) { return putSetting(XSetting.create(userId, packageName, settingName, value), deleteSetting); }
    public boolean putSetting(XSetting setting, boolean deleteSetting) {
        synchronized (mLockMain) {
            if(!canQueryTable(XSetting.Table.NAME, XSetting.Table.COLUMNS)) return false;
            return SQLSnake
                    .create(mDatabase, XSetting.Table.NAME)
                    .setActionToDeleteElseInsert(deleteSetting)
                    .pinObject(setting, deleteSetting, deleteSetting)
                    .executeAction();
        }
    }

    public XSetting getSetting(Integer userId, String packageName, String settingName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XSetting.Table.NAME, XSetting.Table.COLUMNS)) return XSetting.DEFAULT;
            return SQLSnake
                    .create(mDatabase, XSetting.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .whereColumn(XSetting.Table.FIELD_NAME, settingName)
                    .asSnake()
                    .queryGetFirstAs(XSetting.class, true, XSetting.DEFAULT);
        }
    }

    //We can pass NULL args for setting Name, then it will only get from userId and PackageName
    public Collection<XSetting> getSettings(Integer userId, String packageName, String settingName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XSetting.Table.NAME, XSetting.Table.COLUMNS)) return new ArrayList<>();
            return SQLSnake
                    .create(mDatabase, XSetting.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .whereColumn(XSetting.Table.FIELD_NAME, settingName)
                    .asSnake()
                    .queryAs(XSetting.class, true);
        }
    }

    public XStartupSetting getStartupSetting(Integer userId, String packageName, String receiverName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XStartupSetting.Table.NAME, XStartupSetting.Table.COLUMNS)) return XStartupSetting.DEFAULT;
            return SQLSnake
                    .create(mDatabase, XStartupSetting.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .whereColumn(XStartupSetting.Table.FIELD_RECEIVER_NAME, receiverName)
                    .asSnake()
                    .queryGetFirstAs(XStartupSetting.class, true, XStartupSetting.DEFAULT);
        }
    }

    public Collection<XStartupSetting> getStartupSettings(Integer userId, String packageName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XStartupSetting.Table.NAME, XStartupSetting.Table.COLUMNS)) return new ArrayList<>();
            return SQLSnake
                    .create(mDatabase, XStartupSetting.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .asSnake()
                    .queryAs(XStartupSetting.class, true);
        }
    }

    public Collection<XAssignment> getAssignments(Integer userId, String packageName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XAssignment.Table.NAME, XAssignment.Table.COLUMNS)) return new ArrayList<>();
            return SQLSnake
                    .create(mDatabase, XAssignment.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .asSnake()
                    .queryAs(XAssignment.class, true);
        }
    }

    public boolean putAssignment(Integer userId, String packageName, String hook, XAssignment.Kind extra, boolean deleteAssignment) { return putAssignment(XAssignment.create(userId, packageName, hook, extra), deleteAssignment);  }
    public boolean putAssignment(XAssignment assignment, boolean deleteAssignment) {
        synchronized (mLockMain) {
            if(!canQueryTable(XAssignment.Table.NAME, XAssignment.Table.COLUMNS)) return false;
            return SQLSnake
                    .create(mDatabase, XAssignment.Table.NAME)
                    .setActionToDeleteElseInsert(deleteAssignment)
                    .pinObject(assignment, deleteAssignment, deleteAssignment)
                    .executeAction();
        }
    }


    @Override
    public boolean ensureReady(Context context) {
        return true;
    }
}
