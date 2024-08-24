package com.obbedcode.xplex;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.obbedcode.xplex.utils.ThemeHelper;

import rikka.material.app.MaterialActivity;

public class AppActivity extends MaterialActivity {

    @Nullable
    @Override
    public String computeUserThemeKey() { return ThemeHelper.getTheme(this) + ThemeHelper.isUsingSystemColor(); }

    @Override
    public void onApplyUserThemeResource(@NonNull Resources.Theme theme, boolean isDecorView) {
        if(ThemeHelper.isUsingSystemColor()) {
            //if resources configuration isNight
            //
            //boolean isNight = true;
            if (rikka.core.res.ConfigurationKt.isNight(getResources().getConfiguration()))
                theme.applyStyle(R.style.ThemeOverlay_DynamicColors_Dark, true);
            else
                theme.applyStyle(R.style.ThemeOverlay_DynamicColors_Light, true);
        }

        theme.applyStyle(ThemeHelper.getThemeStyleRes(this), true);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    public void onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars();
        final Resources.Theme theme = getTheme();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().getDecorView().post(() -> {
               if(getWindow().getDecorView().getRootWindowInsets().getSystemWindowInsetBottom() >= Resources.getSystem().getDisplayMetrics().density * 40) {
                   getWindow().setNavigationBarColor(rikka.core.res.ResourcesKt.resolveColor(theme, android.R.attr.navigationBarColor) & 0x00ffffff | 0x20000000);
                   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                       getWindow().setNavigationBarContrastEnforced(false);
                   }
               }else {
                   getWindow().setNavigationBarColor(Color.TRANSPARENT);
                   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                       getWindow().setNavigationBarContrastEnforced(true);
                   }
               }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(!super.onSupportNavigateUp()) {
            finish();
        }

        return true;
    }
}