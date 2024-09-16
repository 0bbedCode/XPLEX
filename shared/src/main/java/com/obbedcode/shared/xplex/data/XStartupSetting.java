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
import com.obbedcode.shared.db.SQLSnake;
import com.obbedcode.shared.db.SnakeAction;
import com.obbedcode.shared.helpers.ContentValueBuilder;
import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.utils.CursorUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class XStartupSetting extends XIdentity implements IDatabaseSerial, Parcelable {
    public String receiverName;
    public Integer state;

    public static final XStartupSetting DEFAULT = new XStartupSetting("null", 0);

    public static XStartupSetting create(Integer user, String category, String receiverName, Integer state) { return new XStartupSetting(user, category, receiverName, state); }

    public XStartupSetting() { }
    public XStartupSetting(XIdentity identity, String receiverName, Integer state) { super(identity); this.receiverName = receiverName; this.state = state; }
    public XStartupSetting(String receiverName, Integer state) { this.receiverName = receiverName; this.state = state; }
    public XStartupSetting(Integer user, String category, String receiverName, Integer state) { this(receiverName, state); bindIdentity(user, category); }
    public XStartupSetting(@NonNull Parcel in) {
        super(in);
        this.receiverName = in.readString();
        this.state = in.readInt();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.identityWriteToParcel(dest, flags);
        dest.writeString(receiverName);
        dest.writeInt(state);
    }

    @Override
    public List<ContentValues> toContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public ContentValues toContentValues() {
        return ContentValueBuilder.create()
                .put(super.toContentValues())
                .put(Table.FIELD_RECEIVER_NAME, receiverName)
                .put(Table.FIELD_STATE, state)
                .build();
    }

    @Override
    public void writeQuery(SQLSnake snake, SnakeAction wantedAction) {
        if(snake != null) {
            switch (wantedAction) {
                case UPDATE:
                case DELETE:
                    super.writeQuery(snake, wantedAction);
                    if(this.receiverName != null) snake.whereColumn(Table.FIELD_RECEIVER_NAME, this.receiverName);
                    break;
            }
        }
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            super.fromCursor(cursor);
            this.receiverName = CursorUtils.readString(cursor, Table.FIELD_RECEIVER_NAME);
            this.state = CursorUtils.readInteger(cursor, Table.FIELD_STATE);
        }
    }

    @Override
    public void fromContentValues(ContentValues contentValues) {
        if(contentValues != null) {
            super.fromContentValues(contentValues);
            this.receiverName = contentValues.getAsString(Table.FIELD_RECEIVER_NAME);
            this.state = contentValues.getAsInteger(Table.FIELD_STATE);
        }
    }

    @Override
    public void copy(ICopyable from) {
        super.copy(from);
        if(from instanceof XStartupSetting) {
            XStartupSetting obj = (XStartupSetting) from;
            this.receiverName = obj.receiverName;
            this.state = obj.state;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof XStartupSetting)) return false;
        XStartupSetting inst = (XStartupSetting) obj;
        return Str.safeEqualsIgnoreCase(this.receiverName, inst.receiverName);
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder
                .create(super.toString(), true)
                .appendFieldLine(Table.FIELD_RECEIVER_NAME, this.receiverName)
                .appendFieldLine(Table.FIELD_STATE, this.state)
                .toString();
    }

    public static final Creator<XStartupSetting> CREATOR = new Creator<XStartupSetting>() {
        @Override
        public XStartupSetting createFromParcel(Parcel in) { return new XStartupSetting(in); }

        @Override
        public XStartupSetting[] newArray(int size) { return new XStartupSetting[size]; }
    };

    public static class Table {
        public static final String NAME = "startup_settings";
        public static final String FIELD_USER = "user";
        public static final String FIELD_CATEGORY = "category";
        public static final String FIELD_RECEIVER_NAME = "receiverName";
        public static final String FIELD_STATE = "state";
        public static final LinkedHashMap<String, String> COLUMNS = new LinkedHashMap<String, String>() {{
            put(FIELD_USER, "INTEGER");
            put(FIELD_CATEGORY, "TEXT");
            put(FIELD_RECEIVER_NAME, "TEXT");
            put(FIELD_STATE, "INTEGER");
            put("PRIMARY", "KEY(" + FIELD_USER + ", " + FIELD_CATEGORY + ", " + FIELD_RECEIVER_NAME + ")");//KEY(user, category, receiverName)
        }};
    }
}

//
//DATABASE [xp]
//
//TABLES:
//  [settings] (for app hooks ?) before => [user, category, name, value] Store most settings ?
//      -> user
//      -> category
//      -> name
//      -> value
//  [receivers]
//      -> user
//      -> category
//      -> name
//      -> blocked
//  [build_props]
//      -> user
//      -> category
//      -> name
//      -> value        [Can be empty, as extra may determine state]
//      -> extra        [setting maybe toggle block, force show or something]
//  [assignments]
//      -> user
//      -> category     [Going to be package name]
//      -> hook         [Hook ID]
//      -> mode         [Mode can be (Java, Lua, ....)] Like the Extra Field
//
//
//DATABASE [logs]
//
//TABLES:
//  [usage] Hmm this one we need to work on
//      -> installed
//      -> used
//      -> restricted
//
//
//
//XPLEX OLD
//
//  [assignments]
//      -> package          [Package Name]
//      -> uid              [User ID]
//      -> hook             [Hook ID]
//      -> installed        [If it was installed ?? maybe they dont use api hook ?] when it was deployed
//      -> used             [Used Time Stamp]
//      -> restricted       [Was it Restricted (1) = True (0) = False] sometimes the log is not usage so thats why it can be 0 ??
//      -> exception        [Last Error]
//      -> old              [Old Setting]
//      -> new              [New Setting (Spoofed Setting)]
//