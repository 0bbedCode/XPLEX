package com.obbedcode.shared.xplex.data;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.io.builders.BundleBuilder;
import com.obbedcode.shared.io.interfaces.IBundler;
import com.obbedcode.shared.io.interfaces.IIntent;

public class XUser implements IBundler, IIntent {
    public static final XUser DEFAULT = new XUser(XIdentity.GLOBAL_USER, XIdentity.GLOBAL_NAMESPACE);

    public int id = 0;
    public String name;
    //Lets also note we have "identity" base

    public XUser() { }
    public XUser(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void fromIntent(Intent intent) {
        if(intent != null) {
            if(intent.hasExtra("user")) {
                Bundle b = intent.getBundleExtra("app");
                fromBundle(b);
            } else {
                id = intent.getIntExtra("userId", XIdentity.GLOBAL_USER);
                name = intent.getStringExtra("userName");
            }
        }
    }

    @Override
    public void toIntent(Intent intent) {
        if(intent != null) {
            intent.putExtra("userId", id);
            intent.putExtra("userName", name);
        }
    }

    @Override
    public Bundle toBundle() {
        return BundleBuilder.create()
                .write("userId", id)
                .write("userName", name)
                .build();
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            if(bundle.containsKey("user")) {
                Bundle b = bundle.getBundle("user");
                fromBundle(b);
            } else {
                id = bundle.getInt("userId");
                name = bundle.getString("userName");
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder.create()
                .appendFieldLine("UserId", id)
                .appendFieldLine("userName", name)
                .toString();
    }
}
