package com.obbedcode.shared.xplex.data.hook;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.obbedcode.shared.helpers.StrBuilder;
import com.obbedcode.shared.io.interfaces.IBundler;
import com.obbedcode.shared.io.builders.BundleBuilder;
import com.obbedcode.shared.io.interfaces.IIntent;

public class XHookGroup implements IBundler, IIntent {
    //Icon....
    public String id;
    public String name;
    public String description;

    @Override
    public Bundle toBundle() {
        return BundleBuilder.create()
                .write("groupId", id)
                .write("groupName", name)
                .write("groupDescription", description)
                .build();
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            id = bundle.getString("groupId");
            name = bundle.getString("groupName");
            description = bundle.getString("groupDescription");
        }
    }

    @Override
    public void fromIntent(Intent intent) {
        if(intent != null) {
            id = intent.getStringExtra("groupId");
            name = intent.getStringExtra("groupName");
            description = intent.getStringExtra("groupDescription");
        }
    }

    @Override
    public void toIntent(Intent intent) {
        if(intent != null) {
            intent.putExtra("groupId", id);
            intent.putExtra("groupName", name);
            intent.putExtra("groupDescription", description);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return StrBuilder.create()
                .appendFieldLine("groupId", id)
                .appendFieldLine("groupName", name)
                .appendFieldLine("groupDescription", description)
                .toString();
    }

    //
    //useCount
    //.....
    //
}
