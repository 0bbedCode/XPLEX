package com.obbedcode.shared.xplex.data.hook;

import android.content.Intent;

import com.obbedcode.shared.io.interfaces.IIntent;

import java.util.ArrayList;
import java.util.List;

public class XHookContainer implements IIntent {
    public String name;
    public String description;

    public boolean isEnabled = false;
    public List<String> settingNames = new ArrayList<>();
    public List<String> settingValues = new ArrayList<>();
    public List<String> hookIds = new ArrayList<>();

    public XHookContainer() { }

    @Override
    public void fromIntent(Intent intent) {
        if(intent != null) {
            name = intent.getStringExtra("hookName");
            description = intent.getStringExtra("hookDescription");
            isEnabled = intent.getBooleanExtra("hookEnabled", false);
            settingNames = intent.getStringArrayListExtra("settingNames");
            settingValues = intent.getStringArrayListExtra("settingValues");
        }
    }

    @Override
    public void toIntent(Intent intent) {
        if(intent != null) {
            intent.putExtra("hookName", name);
            intent.putExtra("hookDescription", description);
            intent.putExtra("hookEnabled", isEnabled);
            intent.putExtra("settingNames", new ArrayList<>(settingNames));
            intent.putExtra("settingValues", new ArrayList<>(settingValues));
        }
    }
}
