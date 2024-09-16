package com.obbedcode.shared.xplex.database;

import android.content.Context;

import com.obbedcode.shared.db.IDatabaseManage;
import com.obbedcode.shared.db.SQLDatabase;
import com.obbedcode.shared.db.SQLQuerySnake;
import com.obbedcode.shared.xplex.data.XSetting;
import com.obbedcode.shared.xplex.data.XStartupSetting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public class XDatabaseManager implements IDatabaseManage {
    private final SQLDatabase mDatabase = new SQLDatabase("xp", true);
    private final Object mLockMain = new Object();

    @SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
    public boolean canQueryTable(String tableName, LinkedHashMap<String, String> columns) { return mDatabase.isReady(tableName, columns); }


    //Making puts will be a bit harder lets start the port

    public XSetting getSetting(Integer userId, String packageName, String settingName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XSetting.Table.NAME, XSetting.Table.COLUMNS)) return XSetting.DEFAULT;
            return SQLQuerySnake
                    .create(mDatabase, XSetting.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .whereColumn(XSetting.Table.FIELD_NAME, settingName)
                    .asSnake()
                    .queryGetFirstAs(XSetting.class, true, XSetting.DEFAULT);
        }
    }

    public Collection<XSetting> getSettings(Integer userId, String packageName, String settingName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XSetting.Table.NAME, XSetting.Table.COLUMNS)) return new ArrayList<>();
            return SQLQuerySnake
                    .create(mDatabase, XSetting.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .whereColumn(XSetting.Table.FIELD_NAME, settingName)
                    .asSnake()
                    .queryAs(XSetting.class, true);
        }
    }

    public Collection<XStartupSetting> getStartupSettings(Integer userId, String packageName) {
        synchronized (mLockMain) {
            if(!canQueryTable(XStartupSetting.Table.NAME, XStartupSetting.Table.COLUMNS)) return new ArrayList<>();
            return SQLQuerySnake
                    .create(mDatabase, XStartupSetting.Table.NAME)
                    .pushColumnValueIfNull(false)
                    .whereIdentity(userId, packageName)
                    .asSnake()
                    .queryAs(XStartupSetting.class, true);
        }
    }


    @Override
    public boolean ensureReady(Context context) {
        //Check if tables exist if not create them
        //If table stores defaults ensure defaults are stored and saved
        return true;
    }

    //We can store the get Functions here example getAssignments etc
    //Cache can be stored here...
    //We should not interface over tho ??

    //We can also do something with cache on the Service Interface ??
    //We will have to expose the "get" functions on the Interface so do note that...
    //Lets try this, lets work as we "go"


    //Do cache ing here too ?????



}
