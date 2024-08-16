package com.obbedcode.xplex.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Process;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.settings.LocalSettings;
import com.obbedcode.xplex.AppBarActivity;
import com.obbedcode.xplex.uiex.dialogs.DialogInterfaceEx;
import com.obbedcode.xplex.utils.AppIconCache;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.MainHomeActivityBinding;
import com.obbedcode.xplex.databinding.AboutDialogBinding;

public class HomeActivity extends AppBarActivity implements DialogInterfaceEx.OnPositiveDoneEvent {
    private static final String TAG = "ObbedCode.XPL.HomeActivity";

    private AppBarConfiguration appBarConfiguration;
    private MainHomeActivityBinding mBinding;
    private final HomeAdapter adapter = new HomeAdapter();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = MainHomeActivityBinding.inflate(getLayoutInflater());
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
        if(!LocalSettings.hasPreference(getApplicationContext(), "user_name"))
            GlobalDialogs.invokeSetUsername(getSupportFragmentManager(), this);

    }

    @Override
    public void onPositiveDone(Bundle data, int dialogId) {
        XLog.i(TAG, "INVOKING ON POSITIVE DONEE = " + dialogId);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {



        switch (item.getItemId()) {
            case R.id.action_about:
                AboutDialogBinding binding = AboutDialogBinding.inflate(LayoutInflater.from(this));
                binding.aboutText.setText(Html.fromHtml(getString(R.string.message_about)));
                binding.icon.setImageBitmap(AppIconCache.INSTANCE.getOrLoadBitmap(
                        this,
                        getApplicationInfo(),
                        Process.myUid() / 100000,
                        getResources().getDimensionPixelOffset(R.dimen.default_app_icon_size)));

                new MaterialAlertDialogBuilder(this)
                        .setView(binding.getRoot()).show();







                /*binding.sourceCode.setMovementMethod(LinkMovementMethod.getInstance());
                binding.sourceCode.setText(getString(
                        R.string.about_view_source_code,
                        "<b><a href=\"https://github.com/RikkaApps/Shizuku\">GitHub</a></b>"
                ).toHtml());
                binding.icon.setImageBitmap(
                        AppIcon.getOrLoadBitmap(
                                this,
                                getApplicationInfo(),
                                Process.myUid() / 100000,
                                getResources().getDimensionPixelOffset(R.dimen.default_app_icon_size)
                        )
                );
                try {
                    binding.versionName.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new MaterialAlertDialogBuilder(this)
                        .setView(binding.getRoot())
                        .show();*/
                return true;
        }

        return true;
    }
}
