package com.obbedcode.xplex.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;

import com.obbedcode.xplex.views.activity.app.AppBarActivity;
import com.obbedcode.xplex.databinding.HookAppsActivityBinding;
import com.obbedcode.xplex.uiex.dialogs.DialogInterfaceEx;

public class HookAppsActivity extends AppBarActivity implements DialogInterfaceEx.OnPositiveDoneEvent  {
    private static final String TAG = "ObbedCode.XPL.HookAppsActivity";

    private AppBarConfiguration appBarConfiguration;
    private HookAppsActivityBinding mBinding;
    private final HookAppsAdapter adapter = new HookAppsAdapter();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = HookAppsActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        RecyclerView rv = mBinding.list;
        rv.setAdapter(adapter);

        rikka.recyclerview.RecyclerViewKt.fixEdgeEffect(rv, true, true);
        rikka.recyclerview.RecyclerViewKt.addItemSpacing(rv, 0, 4f, 0, 4f, TypedValue.COMPLEX_UNIT_DIP);
        rikka.recyclerview.RecyclerViewKt.addEdgeSpacing(rv, 16f, 4f, 16f, 4f, TypedValue.COMPLEX_UNIT_DIP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(!LocalSettings.hasPreference(getApplicationContext(), "user_name"))
        //    GlobalDialogs.invokeSetUsername(getSupportFragmentManager(), this);

    }

    @Override
    public void onPositiveDone(Bundle data, int dialogId) {
        if(data != null && !data.isEmpty()) {
            switch (dialogId) {
                case GlobalDialogs.DIALOG_USERNAME:
                    adapter.updateView(dialogId);
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //switch (item.getItemId()) {
        //}

        return true;
    }
}
