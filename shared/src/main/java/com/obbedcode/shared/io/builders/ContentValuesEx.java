package com.obbedcode.shared.io.builders;

import android.content.ContentValues;

import com.obbedcode.shared.utils.DataTypeUtils;

public class ContentValuesEx {
    public static ContentValuesEx create() { return new ContentValuesEx(); }
    public static ContentValuesEx create(ContentValues cv) { return new ContentValuesEx(cv); }

    private boolean mWriteNull = false;
    private ContentValues mVc = new ContentValues();

    public ContentValuesEx writeNull(boolean writeNull) { this.mWriteNull = writeNull; return this; }

    public ContentValuesEx() { }
    public ContentValuesEx(ContentValues cv) { this.mVc = cv; }

    public ContentValuesEx write(String key, String value) {
        if(value == null && !mWriteNull) return this;
        mVc.put(key, value);
        return this;
    }

    public ContentValuesEx write(String key, String value, String defaultValue) {
        if(value == null) value = defaultValue;
        mVc.put(key, value);
        return this;
    }

    public ContentValuesEx write(String key, Boolean value) {
        if(value == null && !mWriteNull) return this;
        mVc.put(key, DataTypeUtils.boolToInt(value));
        return this;
    }

    public ContentValuesEx write(String key, Boolean value, boolean defaultValue) {
        if(value == null) value = defaultValue;
        mVc.put(key, DataTypeUtils.boolToInt(value));
        return this;
    }

    public ContentValuesEx write(String key, Integer value) {
        if(value == null && !mWriteNull) return this;
        mVc.put(key, value);
        return this;
    }

    public ContentValuesEx write(String key, Integer value, int defaultValue) {
        if(value == null) value = defaultValue;
        mVc.put(key, value);
        return this;
    }

    public ContentValuesEx write(String key, Long value) {
        if(value == null && !mWriteNull) return this;
        mVc.put(key, value);
        return this;
    }

    public ContentValuesEx write(String key, Long value, long defaultValue) {
        if(value == null) value = defaultValue;
        mVc.put(key, value);
        return this;
    }

    public ContentValues build() { return mVc; }
}
