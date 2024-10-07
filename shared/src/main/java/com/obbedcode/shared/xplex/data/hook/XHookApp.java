package com.obbedcode.shared.xplex.data.hook;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.io.builders.BundleBuilder;
import com.obbedcode.shared.io.interfaces.IBundler;
import com.obbedcode.shared.io.interfaces.IIntent;
import com.obbedcode.shared.xplex.data.XIdentity;

public class XHookApp implements IBundler, IIntent {
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
            if(bundle.containsKey("app")) {
                Bundle b = bundle.getBundle("app");
                fromBundle(b);
            } else {
                uid = bundle.getInt("uid", XIdentity.GLOBAL_USER);
                name = bundle.getString("name");
                packageName = bundle.getString("packageName");
            }
        }
    }

    @Override
    public void fromIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("app")) {
                Bundle b = intent.getBundleExtra("app");
                fromBundle(b);
            } else {
                uid = intent.getIntExtra("uid", XIdentity.GLOBAL_USER);
                name = intent.getStringExtra("name");
                packageName = intent.getStringExtra("packageName");
            }
        }
    }

    @Override
    public void toIntent(Intent intent) {
        if(intent != null) {
            intent.putExtra("uid", uid);
            intent.putExtra("name", name);
            intent.putExtra("packageName", packageName);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder.create()
                .appendFieldLine("uid", uid)
                .appendFieldLine("name", name)
                .appendFieldLine("packageName", packageName)
                .toString();
    }

    //Maybe we build more into this ??
    //Put Identity or should we keep the two seperate
    //If we include specific things for the current user then lets not bind them
}
