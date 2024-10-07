package com.obbedcode.shared.xplex.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.obbedcode.shared.ICopyable;
import com.obbedcode.shared.Str;
import com.obbedcode.shared.db.IDatabaseSerial;
import com.obbedcode.shared.db.SQLSnake;
import com.obbedcode.shared.db.SnakeAction;
import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.io.builders.BundleBuilder;
import com.obbedcode.shared.io.interfaces.IBundler;
import com.obbedcode.shared.utils.CursorUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class XIdentity implements IDatabaseSerial, IBundler {
    public static final int GLOBAL_USER = 0;
    public static final String GLOBAL_NAMESPACE = "global";

    public static final String FIELD_USER = "user";
    public static final String FIELD_CATEGORY = "category";

    public static final String BUNDLE_KEY = "identity";

    public static final XIdentity GLOBAL_IDENTITY = new XIdentity(GLOBAL_USER, GLOBAL_NAMESPACE);

    public Integer user;
    public String category;

    //Should this constructor set it to global ?
    public XIdentity() {  }
    public XIdentity(XIdentity from) { copy(from); }
    public XIdentity(Integer user, String category) { this.user = user; this.category = category; }
    public XIdentity(@NonNull Parcel in) { identityReadFromParcel(in); }

    public boolean isGlobal() { return (category == null || user == null) || (category.equalsIgnoreCase(GLOBAL_NAMESPACE) || user == GLOBAL_USER); }
    public void bindIdentity(Integer user, String category) { this.user = user; this.category = category; }
    public void ensureIdentityIsReady() {
        //Or we can make a default ID that implies its null ??
        if(user == null) user = GLOBAL_USER;
        if(category == null) category = GLOBAL_NAMESPACE;
    }

    public void identityReadFromParcel(@NonNull Parcel in) {
        this.user = in.readInt();
        this.category = in.readString();
    }

    public void identityWriteToParcel(@NonNull Parcel dest, int flags) {
        ensureIdentityIsReady();
        dest.writeInt(this.user);
        dest.writeString(this.category);
    }

    @Override
    public List<ContentValues> toContentValuesList() { return Collections.emptyList(); }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public ContentValues toContentValues() {
        ensureIdentityIsReady();
        ContentValues cv = new ContentValues();
        cv.put("user", user);
        cv.put("category", category);
        return cv;
    }

    @Override
    public void writeQuery(SQLSnake snake, SnakeAction wantedAction) {
        //Should we ensure identity I mean if they or (I) fail to identify then fuck off ye ?
        //BUT WHAT IF you forgot to set, o well
        if(snake != null && this.user != null && this.category != null) {
            if(!snake.hasConsumedId()) {
                snake.whereIdentity(this.user, this.category);
            }
        }
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            this.user = CursorUtils.readInteger(cursor, "user", GLOBAL_USER);
            this.category = CursorUtils.readString(cursor, "category", GLOBAL_NAMESPACE);
        }
    }

    @Override
    public void fromContentValues(ContentValues contentValues) {
        if(contentValues != null) {
            this.user = contentValues.getAsInteger("user");
            this.category = contentValues.getAsString("category");
            ensureIdentityIsReady();
        }
    }

    @Override
    public void copy(ICopyable from) {
        if(from instanceof XIdentity) {
            XIdentity obj = (XIdentity) from;
            this.user = obj.user;
            this.category = obj.category;
        }
    }

    public boolean equalsIdentity(@Nullable Object obj) { return equals(obj); }
    public boolean equalsIdentity(Integer user, String category) { return equals(new XIdentity(user, category)); }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof XIdentity)) return false;
        XIdentity inst = (XIdentity) obj;
        return Str.safeEqualsIgnoreCase(this.category, inst.category) && Objects.equals(this.user, inst.user);
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder.create()
                .appendFieldLine(FIELD_USER, this.user)
                .appendFieldLine(FIELD_CATEGORY, this.category)
                .toString();
    }

    @Override
    public Bundle toBundle() {
        ///Make sure not null ?????
        return BundleBuilder.create()
                .write(FIELD_USER, user)
                .write(FIELD_CATEGORY, category)
                .build();
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle.containsKey(BUNDLE_KEY)) {
            Bundle b = bundle.getBundle(BUNDLE_KEY);
            if(b == null) return;
            user = b.getInt(FIELD_USER);
            category = b.getString(FIELD_CATEGORY);
        } else {
            user = bundle.getInt(FIELD_USER);
            category = bundle.getString(FIELD_CATEGORY);
        }
    }
}
