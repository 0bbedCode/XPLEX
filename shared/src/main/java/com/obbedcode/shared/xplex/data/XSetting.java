package com.obbedcode.shared.xplex.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obbedcode.shared.ICopyable;
import com.obbedcode.shared.Str;
import com.obbedcode.shared.db.IDatabaseSerial;
import com.obbedcode.shared.helpers.ContentValueBuilder;
import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.utils.CursorUtils;

import java.util.LinkedHashMap;
import java.util.List;

public class XSetting extends XIdentity implements IDatabaseSerial, Parcelable {
    public String name;
    public String value;

    public static final XSetting DEFAULT = new XSetting(GLOBAL_IDENTITY, "null", "null");

    public XSetting() { }
    public XSetting(XIdentity identity, String name, String value) { super(identity); this.name = name; this.value = value; }
    public XSetting(String name, String value) { this.name = name; this.value = value; }
    public XSetting(Integer user, String category, String name, String value) { this(name, value); bindIdentity(user, category); }
    public XSetting(@NonNull Parcel in) {
        super(in);
        name = in.readString();
        value = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.identityWriteToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(value);
    }

    @Override
    public List<ContentValues> toContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public ContentValues toContentValues() {
        //Be careful for null values, its one thing to "update" a db entry without a column and another when you push it with a NULL value column!!!!
        return ContentValueBuilder.create()
                .put(super.toContentValues())
                .put(Table.FIELD_NAME, this.name)
                .put(Table.FIELD_VALUE, this.value)
                .build();
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            super.fromCursor(cursor);
            this.name = CursorUtils.readString(cursor, Table.FIELD_NAME);
            this.value = CursorUtils.readString(cursor, Table.FIELD_VALUE);
        }
    }

    @Override
    public void fromContentValues(ContentValues contentValues) {
        if(contentValues != null) {
            super.fromContentValues(contentValues);
            this.name = contentValues.getAsString(Table.FIELD_NAME);
            this.value = contentValues.getAsString(Table.FIELD_VALUE);
        }
    }

    @Override
    public void copy(ICopyable from) {
        super.copy(from);
        if(from instanceof XSetting) {
            XSetting obj = (XSetting) from;
            this.name = obj.name;
            this.value = obj.value;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        //return super.equals(obj); Should we check ID ??
        if(!(obj instanceof XSetting)) return false;
        XSetting inst = (XSetting) obj;
        return Str.safeEqualsIgnoreCase(this.name, inst.name);
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder
                .create(super.toString(), true)
                .appendFieldLine(Table.FIELD_NAME, this.name)
                .appendFieldLine(Table.FIELD_VALUE, this.value)
                .toString();
    }

    public static final Creator<XSetting> CREATOR = new Creator<XSetting>() {
        @Override
        public XSetting createFromParcel(Parcel in) { return new XSetting(in); }

        @Override
        public XSetting[] newArray(int size) { return new XSetting[size]; }
    };

    public static class Table {
        public static final String NAME = "settings";
        public static final String FIELD_USER = "user";
        public static final String FIELD_CATEGORY = "category";
        public static final String FIELD_NAME = "name";
        public static final String FIELD_VALUE = "value";
        public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {{
            put(FIELD_USER, "INTEGER");
            put(FIELD_CATEGORY, "TEXT PRIMARY KEY");
            put(FIELD_NAME, "TEXT");
            put(FIELD_VALUE, "TEXT");
        }};
    }
}
