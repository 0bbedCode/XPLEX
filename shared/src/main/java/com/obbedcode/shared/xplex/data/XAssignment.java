package com.obbedcode.shared.xplex.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.obbedcode.shared.ICopyable;
import com.obbedcode.shared.db.IDatabaseSerial;
import com.obbedcode.shared.db.SQLSnake;
import com.obbedcode.shared.db.SnakeAction;
import com.obbedcode.shared.hook.repo.Int;
import com.obbedcode.shared.io.builders.ContentValuesEx;
import com.obbedcode.shared.utils.CursorUtils;
import com.obbedcode.shared.xplex.data.hook.XHookDef;

import java.util.LinkedHashMap;

public class XAssignment extends XIdentity implements IDatabaseSerial, Parcelable {
    public String hook;
    public Kind extra;

    public XHookDef definition;

    public static XAssignment create(Integer user, String category, String hook, Integer extra) { return new XAssignment(user, category, hook, extra); }
    public static XAssignment create(Integer user, String category, String hook, Kind extra) { return new XAssignment(user, category, hook, extra); }

    public XAssignment() { }
    public XAssignment(Integer user, String category) { super(user, category); }

    public XAssignment(XAssignment identity, String hook, Integer extra) { this(identity, hook, Kind.parse(extra)); }
    public XAssignment(XAssignment identity, String hook, Kind extra) {
        super(identity);
        this.hook = hook;
        this.extra = extra;
    }

    public XAssignment(Integer user, String category, String hook, Integer extra) { this(user, category, hook, Kind.parse(extra)); }
    public XAssignment(Integer user, String category, String hook, Kind extra) {
        super(user, category);
        this.hook = hook;
        this.extra = extra;
    }

    public XAssignment(Parcel in) {
        super(in);
        hook = in.readString();
        extra = Kind.parse(in.readInt());
    }

    @Override
    public void writeQuery(SQLSnake snake, SnakeAction wantedAction) {
        if(snake != null) {
            switch (wantedAction) {
                case UPDATE:
                case DELETE:
                    super.writeQuery(snake, wantedAction);
                    if(this.hook != null) snake.whereColumn(Table.FIELD_HOOK_ID, this.hook);
                    break;
            }
        }
    }

    @Override
    public ContentValues toContentValues() {
        return ContentValuesEx
                .create(super.toContentValues())
                .write(Table.FIELD_HOOK_ID, hook)
                .write(Table.FIELD_EXTRA, extra.value)
                .build();
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            super.fromCursor(cursor);
            this.hook = CursorUtils.readString(cursor, Table.FIELD_HOOK_ID);
            this.extra = Kind.parse(CursorUtils.readInteger(cursor, Table.FIELD_EXTRA));
        }
    }

    @Override
    public void fromContentValues(ContentValues contentValues) {
        if(contentValues != null) {
            super.fromContentValues(contentValues);
            this.hook = contentValues.getAsString(Table.FIELD_HOOK_ID);
            this.extra = Kind.parse(contentValues.getAsInteger(Table.FIELD_EXTRA));
        }
    }

    @Override
    public void copy(ICopyable from) {
        super.copy(from);
        if(from instanceof XAssignment) {
            XAssignment obj = (XAssignment) from;
            this.hook = obj.hook;
            this.extra = obj.extra;
        }
    }

    @Override
    public int describeContents() { return 0;  }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.identityWriteToParcel(dest, flags);
        dest.writeString(hook);
        dest.writeInt(extra.value);
    }

    public static final Creator<XAssignment> CREATOR = new Creator<XAssignment>() {
        @Override
        public XAssignment createFromParcel(Parcel in) { return new XAssignment(in); }

        @Override
        public XAssignment[] newArray(int size) { return new XAssignment[size]; }
    };


    public enum Kind {
        UNKNOWN(0),
        INLINE(1),
        LUA(2),
        JAVASCRIPT(3),
        PYTHON(4),
        BEANSHELL(5);

        public static Kind parse(Integer code) {
            if(code == null) return UNKNOWN;
            if(code >= Kind.values().length || code < -1) return Kind.UNKNOWN;
            return Kind.values()[code];
        }

        private final int value;
        Kind(int value) { this.value = value; }
        public int getValue() { return value; }
    }


    public static class Table {
        public static final String NAME = "assignments";

        public static final String FIELD_USER = "user";
        public static final String FIELD_CATEGORY = "category";
        public static final String FIELD_HOOK_ID = "hook";
        public static final String FIELD_EXTRA = "extra";

        public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {{
            put(FIELD_USER, "INTEGER");
            put(FIELD_CATEGORY, "TEXT");
            put(FIELD_HOOK_ID, "TEXT");
            put(FIELD_EXTRA, "INTEGER");

            put("PRIMARY", "KEY(" + FIELD_USER + ", " + FIELD_CATEGORY + ", " + FIELD_HOOK_ID + ")");
        }};
    }
}

