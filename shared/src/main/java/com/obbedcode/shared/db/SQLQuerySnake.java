package com.obbedcode.shared.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.CursorUtils;

import java.util.ArrayList;
import java.util.Collection;

public class SQLQuerySnake extends SQLQueryBuilder {
    private static final String TAG = "ObbedCode.XP.SQLQuerySnake";

    public static SQLQueryBuilder create() { return new SQLQueryBuilder(); }

    private boolean mCanCompile = true;
    private Exception mError = null;

    public boolean canCompile() { return mCanCompile; }
    public Exception error() { return mError; }
    public boolean hasError() { return mError != null; }

    public SQLQuerySnake ensureDatabaseIsReady() {
        mCanCompile = SQLDatabase.isReady(getDatabase());
        return this;
    }

    public Cursor query() {
        Cursor c = null;
        try {
            String[] onlyRets = onlyReturn.isEmpty() ? null : getOnlyReturn();
            SQLiteDatabase sBase = database.getRawDatabase();
            c = sBase.query(
                    getTableName(),
                    onlyRets,
                    getWhereClause(),
                    getWhereArgs(),
                    null,
                    null,
                    getColumnOrder());
        }catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Error Querying Database: " + super.toString() + " Error: " + e.getMessage());
        }

        return c;
    }

    public boolean exists() {
        String table = getTableName();
        if(!mCanCompile || database == null || !TextUtils.isEmpty(table)) return false;
        mCanCompile = false;
        database.readLock();
        Cursor c = query();
        try {
            if(c == null) return false;
            return c.moveToFirst();
        }catch (Exception e) {
            XLog.e(TAG, "Failed to query Cursor for Check if Exists! DB=" + database + " Table Name=" + table + " Error=" + e.getMessage());
            return false;
        } finally {
            database.readUnlock();
            CursorUtils.closeCursor(c);
        }
    }

    public Collection<String> queryAsStringList(String columnReturn, boolean cleanUpAfter) {
        if(!mCanCompile) return null;
        mCanCompile = false;

        prepareReturn(columnReturn);

        database.readLock();
        Cursor c = query();
        Collection<String> values = new ArrayList<>();
        try {
            if(c != null) {
                int ix = c.getColumnIndex(columnReturn);
                if(ix == -1) {
                    XLog.e(TAG, "");
                    return values;
                }

            }
        } catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to query Cursor as String List! DB=" + database + " Error=" + e.getMessage());
        } finally {
            database.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        }
    }

    private void prepareReturn(String fieldReturn) {
        if(!onlyReturn.isEmpty() && !onlyReturn.contains(fieldReturn)) onlyReturn.clear();
        if(!onlyReturn.contains(fieldReturn)) onlyReturn.add(fieldReturn);
    }
}
