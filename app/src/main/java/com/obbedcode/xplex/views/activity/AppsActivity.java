package com.obbedcode.xplex.views.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.obbedcode.xplex.R;
import com.obbedcode.xplex.views.etc.INavContainer;
import com.obbedcode.xplex.views.fragment.apps.AppsPagerFragment;

public class AppsActivity extends ActivityBase {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.base_activity_two);
        super.setBottomNavFragmentTypes(AppsPagerFragment.class);
        super.setBottomNavFragmentIds(R.id.main_bottom_item_1);
        super.onCreate(savedInstanceState);
    }
}
