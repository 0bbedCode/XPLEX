package com.obbedcode.shared.io.builders;

import android.os.Bundle;

public class BundleBuilder {
    private final Bundle mBundle = new Bundle();
    private boolean mWriteIfNull = false;

    public static BundleBuilder create() { return new BundleBuilder(); }
    public static BundleBuilder create(boolean writeIfNull) { return new BundleBuilder(writeIfNull); }

    public BundleBuilder()  { }
    public BundleBuilder(boolean writeIfNull) {
        mWriteIfNull = writeIfNull;
    }

    public BundleBuilder writeIfNull(boolean writeIfNull) {
        mWriteIfNull = writeIfNull;
        return this;
    }

    public BundleBuilder write(String key, Bundle bundle) {
        if(bundle == null) return this;
        mBundle.putBundle(key, bundle);
        return this;
    }

    public BundleBuilder write(String key, Long number) {
        if(key == null || (number == null && !mWriteIfNull)) return this;
        mBundle.putLong(key, number);
        return this;
    }

    public BundleBuilder write(String key, String data) {
        if(key == null || (data == null && !mWriteIfNull)) return this;
        mBundle.putString(key, data);
        return this;
    }

    public BundleBuilder write(String key, Integer number) {
        if(key == null || (number == null && !mWriteIfNull)) return this;
        mBundle.putInt(key, number);
        return this;
    }

    public Bundle build() { return mBundle; }
}
