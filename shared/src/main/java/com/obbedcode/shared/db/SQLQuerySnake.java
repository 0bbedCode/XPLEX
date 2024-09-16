package com.obbedcode.shared.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.CursorUtils;

import java.util.ArrayList;
import java.util.Collection;

public class SQLQuerySnake extends SQLQueryBuilder {
    private static final String TAG = "ObbedCode.XP.SQLQuerySnake";

    public static SQLQuerySnake create() { return new SQLQuerySnake(); }
    public static SQLQuerySnake create(SQLDatabase database) { return new SQLQuerySnake(database); }
    public static SQLQuerySnake create(SQLDatabase database, String tableName) { return new SQLQuerySnake(database, tableName); }
    public static SQLQuerySnake create(SQLDatabase database, String tableName, boolean pushColumnIfNullValue) { return new SQLQuerySnake(database, tableName, pushColumnIfNullValue); }


    private boolean mCanCompile = true;
    private Exception mError = null;
    private SQLDatabase mDatabase;

    public boolean canCompile() { return mCanCompile; }
    public Exception error() { return mError; }
    public boolean hasError() { return mError != null; }
    public SQLDatabase getDatabase() { return mDatabase; }

    public SQLQuerySnake() {  }
    public SQLQuerySnake(SQLDatabase database) { mDatabase = database; }
    public SQLQuerySnake(SQLDatabase database, String tableName) { super(tableName); mDatabase = database; }
    public SQLQuerySnake(SQLDatabase database, String tableName, boolean pushColumnIfNullValue) { super(tableName, pushColumnIfNullValue); mDatabase = database; }

    public SQLQuerySnake database(SQLDatabase database) {
        this.mDatabase = database;
        return this;
    }

    private Cursor internalQuery() {
        Cursor c = null;
        try {
            String[] onlyRets = onlyReturn.isEmpty() ? null : getOnlyReturn();
            SQLiteDatabase sBase = mDatabase.getRawDatabase();
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

    @SuppressWarnings("unused")
    public SQLQuerySnake ensureDatabaseIsReady() {
        mCanCompile = SQLDatabase.isReady(getDatabase());
        return this;
    }

    @SuppressWarnings("unused")
    public boolean exists() {
        String table = getTableName();
        if(!mCanCompile || mDatabase == null || !TextUtils.isEmpty(table)) return false;
        mCanCompile = false;
        mDatabase.readLock();
        Cursor c = internalQuery();
        try {
            if(c == null) return false;
            return c.moveToFirst();
        }catch (Exception e) {
            XLog.e(TAG, "Failed to query Cursor for Check if Exists! DB=" + mDatabase + " Table Name=" + table + " Error=" + e.getMessage());
            return false;
        } finally {
            mDatabase.readUnlock();
            CursorUtils.closeCursor(c);
        }
    }

    @Nullable
    @SuppressWarnings("unused")
    public String queryGetFirstString(String columnReturn, boolean cleanUpAfter) { return queryGetFirstString(columnReturn, null, cleanUpAfter); }

    @Nullable
    public String queryGetFirstString(String columnReturn, String defaultValue, boolean cleanUpAfter) {
        if(!mCanCompile) return null;
        mCanCompile = false;

        prepareReturn(columnReturn);
        mDatabase.readLock();;
        Cursor c = internalQuery();
        try {
            if(c != null && c.moveToFirst()) return c.getString(0);
        } catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to Query Get First String. Error: " + e.getMessage() + " " + this);
        } finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        } return defaultValue;
    }

    @Nullable
    @SuppressWarnings("unused")
    public Collection<String> queryAsStringList(String columnReturn, boolean cleanUpAfter) {
        if(!isQueryReady()) return null;
        prepareReturn(columnReturn);
        mDatabase.readLock();
        Cursor c = internalQuery();
        Collection<String> values = new ArrayList<>();
        try {
            if(c != null) {
                int ix = c.getColumnIndex(columnReturn);
                if(ix == -1) {
                    XLog.e(TAG, "Failed to Query as String List, [ix] Returned (-1) " + this);
                    return values;
                }

                int typeCode = c.getType(ix);
                if(typeCode == Cursor.FIELD_TYPE_NULL) {
                    XLog.e(TAG, "Field type is Null (failed to find field) returning List of Items: " + this);
                    return values;
                }

                if(!c.moveToFirst()) {
                    XLog.e(TAG, "Failed to Move to first element in the Cursor: " + this);
                    return values;
                }

                switch (typeCode) {
                    case Cursor.FIELD_TYPE_STRING:
                        do {
                            values.add(c.getString(ix));
                        } while(c.moveToNext());
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        do {
                            values.add(Integer.toString(c.getInt(ix)));
                        } while(c.moveToNext());
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        do {
                            values.add(Float.toString(c.getFloat(ix)));
                        } while(c.moveToNext());
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        //
                        break;
                }
            }

            return values;
        } catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to query Cursor as String List! DB=" + mDatabase + " Error=" + e.getMessage());
            return values;
        } finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        }
    }

    @SuppressWarnings("unused")
    public long queryGetFirstLong(String columnReturn, boolean cleanUpAfter) { return queryGetFirstLong(columnReturn, 0, cleanUpAfter); }
    public long queryGetFirstLong(String columnReturn,long defaultValue, boolean cleanUpAfter) {
        if(!isQueryReady()) return defaultValue;
        prepareReturn(columnReturn);
        mDatabase.readLock();
        Cursor c = internalQuery();
        try {
            if(c != null && c.moveToFirst()) return c.getLong(0);
        } catch (Exception e) {
            XLog.e(TAG, "Failed to query as First Long! Error: " + e.getMessage() + " " + this);
        } finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        }

        return defaultValue;
    }

    @Nullable
    @SuppressWarnings("unused")
    public <T extends IDatabaseSerial> T queryGetFirstAs(Class<T> typeClass, boolean cleanUpAfter) { return queryGetFirstAs(typeClass, cleanUpAfter, null); }

    @Nullable
    @SuppressWarnings("unused")
    public <T extends IDatabaseSerial> T queryGetFirstAs(Class<T> typeClass, boolean cleanUpAfter, T defaultValue) {
        if(!isQueryReady()) return defaultValue;
        T item = null;
        Cursor c = null;
        try {
            mDatabase.readLock();
            c = internalQuery();
            if(c != null && c.moveToFirst()) {
                item = typeClass.newInstance();
                item.fromCursor(c);
                return item;
            }
        } catch (InstantiationException e) {
            mError = e;
            Log.e(TAG, "Your object is messed up via constructor not my fault... " + e.getMessage());
        } catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to Query Cursor first As. Error" + e.getMessage() + " " + this);
        }
        finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        }

        return item == null ? defaultValue : item;
    }

    @SuppressWarnings("unused")
    public <T extends IDatabaseSerial> Collection<T> queryAs(Class<T> typeClass) { return queryAs(typeClass, false); }
    public <T extends IDatabaseSerial> Collection<T> queryAs(Class<T> typeClass, boolean cleanUpAfter) {
        if(!isQueryReady()) return new ArrayList<>();
        mDatabase.readLock();
        Cursor c = internalQuery();
        Collection<T> items = new ArrayList<>();
        try {
            if(c != null && c.moveToFirst()) {
                do {
                    T item = typeClass.newInstance();
                    item.fromCursor(c);
                    items.add(item);
                } while (c.moveToNext());
            }

            return items;
        }catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to Query as Item List. Error: " + e.getMessage() + " " + this);
        } finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        }

        return items;
    }

    @SuppressWarnings("unused")
    public <T extends IDatabaseSerial> Collection<T> queryAll(Class<T> typeClass) { return queryAll(typeClass, false); }
    public <T extends IDatabaseSerial> Collection<T> queryAll(Class<T> typeClass, boolean cleanUpAfter) {
        if(!isQueryReady()) return new ArrayList<>();

        Collection<T> items = new ArrayList<>();
        Cursor c = null;
        try {
            String[] columns = onlyReturn.isEmpty() ? null : getOnlyReturn();
            mDatabase.readLock();
            c = mDatabase.getRawDatabase().query(
                    getTableName(),
                    columns,
                    null,
                    null,
                    null,
                    null,
                    getColumnOrder());

            c.moveToFirst();
            do {
                T item = typeClass.newInstance();
                item.fromCursor(c);
                items.add(item);
            } while(c.moveToNext());

            return items;
        }catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to Execute Query and list all object. Error: " + e.getMessage() + " " + this);
            return items;
        } finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        }
    }

    @SuppressWarnings("unused")
    public <T extends IDatabaseSerial> Collection<T> queryAllRaw(Class<T> typeClass) { return queryAllRaw(typeClass, false); }
    public <T extends IDatabaseSerial> Collection<T> queryAllRaw(Class<T> typeClass, boolean cleanUpAfter) {
        if(!isQueryReady()) return new ArrayList<>();

        Collection<T> items = new ArrayList<>();
        Cursor c = null;
        try {
            mDatabase.readLock();
            c = mDatabase.getRawDatabase().rawQuery("SELECT * FROM " + getTableName(), null);
            c.moveToFirst();
            do {
                T item = typeClass.newInstance();
                item.fromCursor(c);
                items.add(item);
            } while(c.moveToNext());

            return items;
        } catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to Query all the Items as Raw Query: Error: " + e.getMessage() + " " + this);
            return items;
        } finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isQueryReady() {
        if(mDatabase == null || !mDatabase.isOpen(true) || !mCanCompile || TextUtils.isEmpty(getTableName())) return false;
        mCanCompile = false;
        return true;
    }

    private void prepareReturn(String fieldReturn) {
        if(!onlyReturn.isEmpty() && !onlyReturn.contains(fieldReturn)) onlyReturn.clear();
        if(!onlyReturn.contains(fieldReturn)) onlyReturn.add(fieldReturn);
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder
                .create(super.toString(), true)
                .appendFieldLine("Can Compile", this.mCanCompile)
                .appendFieldLine("Error", this.mError.getMessage())
                .appendFieldLine("Database", this.mDatabase)
                .toString();
    }
}
