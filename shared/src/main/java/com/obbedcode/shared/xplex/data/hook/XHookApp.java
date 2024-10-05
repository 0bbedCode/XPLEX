package com.obbedcode.shared.xplex.data.hook;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.io.builders.BundleBuilder;
import com.obbedcode.shared.io.IBundler;
import com.obbedcode.shared.xplex.data.XIdentity;

public class XHookApp implements IBundler {
    public int uid;
    public String name;
    public String packageName;

    @Override
    public Bundle toBundle() {
        return BundleBuilder.create()
                .write("uid", uid)
                .write("name", name)
                .write("packageName", packageName)
                .build();
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            uid = bundle.getInt("uid", XIdentity.GLOBAL_USER);
            name = bundle.getString("name");
            packageName = bundle.getString("packageName");
        }
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder.create()
                .appendField("uid", uid)
                .appendField("name", name)
                .appendField("packageName", packageName)
                .toString();
    }

    //Maybe we build more into this ??
    //Put Identity or should we keep the two seperate
    //If we include specific things for the current user then lets not bind them
}
