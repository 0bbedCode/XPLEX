package com.obbedcode.shared.helpers;

import android.content.ContentValues;

public class ContentValueBuilder {
    public static ContentValueBuilder create() { return new ContentValueBuilder(); }

    private ContentValues mContentValues = new ContentValues();
    private boolean mIgnoreNull = true;
    private String mDefString = "<nll>";

    public ContentValueBuilder ignoreNull(boolean ignoreNull) {
        this.mIgnoreNull = ignoreNull;
        return this;
    }

    public ContentValueBuilder put(ContentValues cv) {
        if(cv != null) mContentValues.putAll(cv);
        return this;
    }

    public ContentValueBuilder put(String key, String data) { return put(key, data, null); }
    public ContentValueBuilder put(String key, String data, String defaultData) {
        String d = data == null ? defaultData : data;
        if(d != null || !mIgnoreNull) mContentValues.put(key, d);
        return this;
    }

    public ContentValueBuilder put(String key, Integer data) { return put(key, data, null); }
    public ContentValueBuilder put(String key, Integer data, Integer defaultData) {
        Integer d = data == null ? defaultData : data;
        if(d != null || !mIgnoreNull) mContentValues.put(key, d);
        return this;
    }

    public ContentValueBuilder put(String key, Boolean data) { return put(key, data, null); }
    public ContentValueBuilder put(String key, Boolean data, Boolean defaultData) {
        Boolean d = data == null ? defaultData : data;
        if(d != null || !mIgnoreNull) mContentValues.put(key, d);   //Parcel you cant write booleans but need to represent them as a Int
        return this;
    }

    public ContentValues build() { return mContentValues; }
}
