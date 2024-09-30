package com.obbedcode.xplex.views.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.obbedcode.xplex.MainActivity;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.views.activity.app.AppBarActivity;
import com.obbedcode.xplex.views.etc.INavContainer;
import com.obbedcode.xplex.views.fragment.apps.AppsPagerFragment;

public class GroupHooksActivity extends ActivityBase {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.setBottomNavFragmentTypes(AppsPagerFragment.class);
        super.setBottomNavFragmentIds(R.id.main_bottom_item_1);
        super.onCreate(savedInstanceState);
    }

}


