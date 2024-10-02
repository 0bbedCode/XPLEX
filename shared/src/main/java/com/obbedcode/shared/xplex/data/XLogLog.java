package com.obbedcode.shared.xplex.data;

import android.os.Bundle;

import com.obbedcode.shared.io.BundleBuilder;
import com.obbedcode.shared.io.IBundler;

public class XLogLog implements IBundler {
    //App info ??
    public String title;
    public Code code;
    public long time;
    public String message;

    @Override
    public Bundle toBundle() {
        return BundleBuilder.create()
                .write("title", title)
                .write("code", code.value)
                .write("time", time)
                .write("message", message)
                .build();
    }

    @Override
    public void fromBundle(Bundle bundle) {
        title = bundle.getString("title");
        code = Code.parse(bundle.getInt("code"));
        time = bundle.getLong("time");
        message = bundle.getString("message");
    }

    public enum Code {
        UNKNOWN(0),
        USAGE(1),
        ERROR(2),
        INFO(3),
        DEBUG(4),
        WARN(5);

        public static Code parse(int code) {
            if(code >= Code.values().length || code < -1) return Code.UNKNOWN;
            return Code.values()[code];
        }

        private final int value;
        Code(int value) { this.value = value; }
        public int getValue() { return value; }
    }
}
