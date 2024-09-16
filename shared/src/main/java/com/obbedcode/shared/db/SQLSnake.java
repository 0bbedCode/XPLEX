package com.obbedcode.shared.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.utils.CursorUtils;
import com.obbedcode.shared.xplex.data.XIdentity;

import java.util.ArrayList;
import java.util.Collection;

public class SQLSnake extends SQLQueryBuilder {
    private static final String TAG = "ObbedCode.XP.SQLSnake";

    public static SQLSnake create() { return new SQLSnake(); }
    public static SQLSnake create(SQLDatabase database) { return new SQLSnake(database); }
    public static SQLSnake create(SQLDatabase database, String tableName) { return new SQLSnake(database, tableName); }
    public static SQLSnake create(SQLDatabase database, String tableName, boolean pushColumnIfNullValue) { return new SQLSnake(database, tableName, pushColumnIfNullValue); }

    private boolean mCanCompile = true;
    private Exception mError = null;
    private SQLDatabase mDatabase;
    private IDatabaseSerial mDatabaseSerialObj;
    private SnakeAction mAction = SnakeAction.RESOLVE;
    private boolean mInternalObjWasConsumed = false;

    public boolean canCompile() { return mCanCompile; }
    public Exception error() { return mError; }
    public boolean hasError() { return mError != null; }
    public SQLDatabase getDatabase() { return mDatabase; }
    public IDatabaseSerial getPinnedObject() { return mDatabaseSerialObj; }
    public SnakeAction getAction() { return mAction; }

    public SQLSnake() {  }
    public SQLSnake(SQLDatabase database) { mDatabase = database; }
    public SQLSnake(SQLDatabase database, String tableName) { super(tableName); mDatabase = database; }
    public SQLSnake(SQLDatabase database, String tableName, boolean pushColumnIfNullValue) { super(tableName, pushColumnIfNullValue); mDatabase = database; }

    public SQLSnake database(SQLDatabase database) {
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
    public SQLSnake action(SnakeAction action) {
        this.mAction = action;
        return this;
    }

    @SuppressWarnings("unused")
    public SQLSnake setActionToDeleteElseInsert(boolean delete) {
        this.mAction = delete ? SnakeAction.DELETE : SnakeAction.INSERT;
        return this;
    }

    @SuppressWarnings("unused")
    public SQLSnake pinObject(IDatabaseSerial serialObj) { return pinObject(serialObj, false); }
    public SQLSnake pinObject(IDatabaseSerial serialObj, boolean writeIdentityToClause) { return pinObject(serialObj, writeIdentityToClause, true); }
    public SQLSnake pinObject(IDatabaseSerial serialObj, boolean writeIdentityToClause, boolean consumeObject) {
        this.mDatabaseSerialObj = serialObj;
        //this.mInternalObjWasConsumed = false;
        if(consumeObject) {
            //will include the id
            consumePinnedObject();
        } else {
            if(writeIdentityToClause && serialObj instanceof XIdentity) {
                XIdentity id = (XIdentity) serialObj;
                if(id.user != null && id.category != null && !hasConsumedId()) {
                    whereIdentity(id.user, id.category);
                }
            }
        }

        return this;
    }

    @SuppressWarnings("unused")
    public SQLSnake unbindObject() {
        this.mDatabaseSerialObj = null;
        return this;
    }

    @SuppressWarnings("unused")
    public SQLSnake ensureDatabaseIsReady() {
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

    @SuppressWarnings("unused")
    public SQLSnake consumePinnedObject() { return consumePinnedObject(mAction); }
    public SQLSnake consumePinnedObject(SnakeAction wantedAction) {
        if(mDatabaseSerialObj != null) {
            mInternalObjWasConsumed = true;
            mDatabaseSerialObj.writeQuery(this, wantedAction);
        }
        return this;
    }


    public SQLSnake consumeObject(IDatabaseSerial serialObject) { return consumeObject(serialObject, mAction); } //bind it to the flag ??
    public SQLSnake consumeObject(IDatabaseSerial serialObject, SnakeAction wantedAction) {
        if(serialObject != null)
            serialObject.writeQuery(this, wantedAction);
        return this;
    }

    public boolean executeAction() { return executeAction(null, mAction); }
    public boolean executeAction(SnakeAction actionOverride) { return executeAction(null, actionOverride); }
    public boolean executeAction(IDatabaseSerial obj) { return executeAction(obj, mAction); }
    public boolean executeAction(IDatabaseSerial obj, SnakeAction actionOverride) {
        SnakeAction act = isActionEmpty(actionOverride) ?
                mAction :
                actionOverride == SnakeAction.RESOLVE ?
                        !isActionEmpty(mAction) ? mAction : actionOverride : actionOverride;
        switch (act) {
            case UPDATE:
                return updateItem(obj);
            case INSERT:
                return insertItem(obj);
            case DELETE:
                return deleteItem(obj);
            case RESOLVE:
                SnakeAction na = resolveUnresolvedAction(mDatabaseSerialObj == null ? obj == null ? null : obj.toContentValues() : mDatabaseSerialObj.toContentValues());
                if(isActionEmpty(na, true)) {
                    XLog.e(TAG, "Failed to Resolve Snake action how about set it dip shit stop making my life more difficult you piece of shit fuck");
                    return false;
                }

                return executeAction(obj, na);
            case NONE:
            case ERROR:
                XLog.e(TAG, "Action Ended in an ERROR....");
                return false;
            default:
                XLog.e(TAG, "Cannot complete action: " + act.name());
                return false;
        }
    }

    @SuppressWarnings("unused")
    public boolean insertItem() { return insertItem(null); }
    public boolean insertItem(IDatabaseSerial obj) {
        if(mDatabase == null || TextUtils.isEmpty(getTableName())) return false;
        IDatabaseSerial pObj = obj == null ? mDatabaseSerialObj : obj;
        if(pObj == null) {
            XLog.e(TAG, "Failed to Insert Database Item [insertItem] OBJ is null....");
            return false;
        }

        if(!mDatabase.beginTransaction(true)) {
            XLog.e(TAG, "Failed to Begin Database Transaction! [insertItem] " + this);
            return false;
        }

        try {
            if(!mDatabase.insert(getTableName(), pObj.toContentValues())) {
                XLog.e(TAG, "Failed to Insert Database Item [insertItem] error: " + pObj);
                return false;
            }

            mDatabase.setTransactionSuccessful();
            return true;
        } finally {
            mDatabase.endTransaction(true, false);
        }
    }

    @SuppressWarnings("unused")
    public boolean updateItem() { return updateItem(null); }
    public boolean updateItem(IDatabaseSerial obj) {
        if(mDatabase == null || TextUtils.isEmpty(getTableName())) return false;
        ContentValues cvUpdated = prepareContentValuesAndClause(obj, SnakeAction.UPDATE);
        if(!hasConsumedMoreThanId() || cvUpdated == null) {
            XLog.e(TAG, "Failed to Update Item, ContentValues is null or Where Clause is not created...");
            return false;
        }

        if(!mDatabase.beginTransaction(true)) {
            XLog.e(TAG, "Failed to Begin Database Transaction! [updateItem] " + this);
            return false;
        }

        try {
            if(!mDatabase.update(getTableName(), cvUpdated, getWhereClause(), getWhereArgs())) {
                XLog.e(TAG, "Failed to Update Database Item! [updateItem] " + Str.concat(obj, mDatabaseSerialObj) + " " + this);
                return false;
            }

            mDatabase.setTransactionSuccessful();
            return true;
        }finally {
            mDatabase.endTransaction(true, false);
        }
    }

    @SuppressWarnings("unused")
    public boolean deleteItem() { return deleteItem(null); }
    public boolean deleteItem(IDatabaseSerial obj) {
        if(mDatabase == null || TextUtils.isEmpty(getTableName())) return false;
        ContentValues cvUpdated = prepareContentValuesAndClause(obj, SnakeAction.DELETE);
        if(!hasConsumedMoreThanId() || cvUpdated == null) {
            XLog.e(TAG, "Failed to Delete Database Item, ContentValues is null or Where Clause is not created...");
            return false;
        }

        if(!mDatabase.beginTransaction(true)) {
            XLog.e(TAG, "Failed to Begin Database Transaction! [deleteItem] " + this);
            return false;
        }

        try {
            if(!mDatabase.delete(getTableName(), getWhereClause(), getWhereArgs())) {
                XLog.e(TAG, "Failed to Delete Database Item! [deleteItem] " + Str.concat(obj, mDatabaseSerialObj) + " " + this);
                return false;
            }

            mDatabase.setTransactionSuccessful();
            return true;
        }finally {
            mDatabase.endTransaction(true, false);
        }
    }

    @Nullable
    @SuppressWarnings("unused")
    public String queryGetFirstString(String columnReturn, boolean cleanUpAfter) { return queryGetFirstString(columnReturn, null, cleanUpAfter); }

    @Nullable
    public String queryGetFirstString(String columnReturn, String defaultValue, boolean cleanUpAfter) {
        if(!isQueryReady()) return defaultValue;

        prepareReturn(columnReturn);
        mDatabase.readLock();;
        Cursor c = internalQuery();
        try {
            if(c != null && c.moveToFirst()) return c.getString(0);//ensure not null or use default
        } catch (Exception e) {
            mError = e;
            XLog.e(TAG, "Failed to Query Get First String. Error: " + e.getMessage() + " " + this);
        } finally {
            mDatabase.readUnlock();
            if(cleanUpAfter) CursorUtils.closeCursor(c);
        } return defaultValue;
    }

    @SuppressWarnings("unused")
    public Collection<String> queryAsStringList(String columnReturn, boolean cleanUpAfter) {
        if(!isQueryReady()) return new ArrayList<>();
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

    private ContentValues prepareContentValuesAndClause(IDatabaseSerial obj, SnakeAction action) {
        if(!hasConsumedMoreThanId()) {
            if (mDatabaseSerialObj != null) {
                //We take priority for internal one
                mDatabaseSerialObj.writeQuery(this, action);
                return obj == null ? mDatabaseSerialObj.toContentValues() : obj.toContentValues();
            } else {
                if (obj != null) {
                    obj.writeQuery(this, action);
                    return obj.toContentValues();
                }
            }
        } else {
            return obj == null ? mDatabaseSerialObj == null ? null : mDatabaseSerialObj.toContentValues() : obj.toContentValues();
        } return null;
    }

    private SnakeAction resolveUnresolvedAction(ContentValues extraArgs) {
        //Ugh fuck this function but for my ADHD autism w.e this will make me feel better if SNAKE is used in different ways...
        //But i mean if you pin object and consume its ID for Safety then it will always be flagged as UPDATE or DELETE ....
        //Ye... oi fucking hell this is stupid
        //For now we leave this shit show make person set ACTION
        boolean flagWhereClause = hasWhereClause();
        boolean flagExtra = extraArgs != null;
        if(flagWhereClause && hasConsumedMoreThanId()) {
            if(mDatabaseSerialObj != null) {
                if(mInternalObjWasConsumed) {
                    return flagExtra ? SnakeAction.UPDATE : SnakeAction.DELETE;
                } else {
                    return SnakeAction.UPDATE;
                }
            } else {
                return flagExtra ? SnakeAction.UPDATE : SnakeAction.DELETE;
            }
        } else {
            if(extraArgs == null) {
                if(mDatabaseSerialObj != null)
                    return SnakeAction.INSERT;  //Insert the Pinned Object
                else
                    return SnakeAction.ERROR;   //So No pinned object, no WHERE clause, No Extra args so IMPOSSIBLE
            }

            if(mDatabaseSerialObj != null && !hasConsumedMoreThanId()) {
                consumePinnedObject();    //Consume since [flagWhereClause] = False needs to be Consumed
                return SnakeAction.UPDATE;
            } else {
                return SnakeAction.INSERT;
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder
                .create(super.toString(), true)
                .appendFieldLine("Can Compile", this.mCanCompile)
                .appendFieldLine("Error", this.mError)
                .appendFieldLine("Database", this.mDatabase)
                .toString();
    }

    public static boolean isActionEmpty(SnakeAction act) { return isActionEmpty(act, false); }
    public static boolean isActionEmpty(SnakeAction act, boolean includeResolve) {
        return act == SnakeAction.ERROR || act == SnakeAction.NONE || (includeResolve && act == SnakeAction.RESOLVE);
    }
}
