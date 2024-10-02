package com.obbedcode.xplex.views.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.obbedcode.xplex.R;
import com.obbedcode.xplex.views.fragment.app.HooksFragment;
import com.obbedcode.xplex.views.fragment.app.LogsFragment;
import com.obbedcode.xplex.views.fragment.app.SettingsFragment;
import com.obbedcode.xplex.views.fragment.app.pager.AppPagerFragment;

public class AppHookActivity extends ActivityBase {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setArgs(getIntent().getExtras());
        setContentView(R.layout.app_activity);
        //HooksFragment.class, LogsFragment.class, SettingsFragment.class
        super.setBottomNavFragmentTypes(AppPagerFragment.class);
        super.setBottomNavFragmentIds(R.id.app_bottom_item_1);

        super.onCreate(savedInstanceState);
    }
}


