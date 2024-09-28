package com.obbedcode.xplex.views.activity.app;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.obbedcode.xplex.R;

public abstract class AppBarActivity extends AppActivity {
    //Do we use app bar activity for everything ?
    //private final ViewGroup rootView;
    //private final AppBarLayout toolbarContainer;
    //private final Toolbar toolbar;

    public AppBarActivity() {
        //rootView = (ViewGroup) findViewById(R.id.root);
        //toolbarContainer = (AppBarLayout) findViewById(R.id.toolbar_container);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(getLayoutId());
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    @LayoutRes
    protected int getLayoutId() { return R.layout.main_app_bar_activity; }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup rootView = findViewById(R.id.root);
        getLayoutInflater().inflate(layoutResID, rootView , true);
        rootView.bringChildToFront(findViewById(R.id.toolbar_container));
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        ViewGroup rootView = findViewById(R.id.root);
        rootView.addView(view, 0, params);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    public void onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars();
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
}


