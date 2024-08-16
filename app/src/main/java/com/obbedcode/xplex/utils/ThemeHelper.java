package com.obbedcode.xplex.utils;


import android.content.Context;
import android.os.Build;

import androidx.annotation.StyleRes;

import com.obbedcode.xplex.R;

public class ThemeHelper {

    private static final String THEME_DEFAULT = "DEFAULT";
    private static final String THEME_BLACK = "BLACK";

    public static final String KEY_LIGHT_THEME = "light_theme";
    public static final String KEY_BLACK_NIGHT_THEME = "black_night_theme";
    public static final String KEY_USE_SYSTEM_COLOR = "use_system_color";

    public static boolean isBlackNightTheme(Context context) {
        return true;
        //return ShizukuSettings.getPreferences().getBoolean(KEY_BLACK_NIGHT_THEME, EnvironmentUtils.isWatch(context));
    }

    public static boolean isUsingSystemColor() {
        //return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        //        && ShizukuSettings.getPreferences().getBoolean(KEY_USE_SYSTEM_COLOR, true);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && true;
    }

    public static String getTheme(Context context) {
        /*if (isBlackNightTheme(context)
                && ResourceUtils.isNightMode(context.getResources().getConfiguration()))
            return THEME_BLACK;


        return ShizukuSettings.getPreferences().getString(KEY_LIGHT_THEME, THEME_DEFAULT);*/
        return THEME_DEFAULT;
    }

    @StyleRes
    public static int getThemeStyleRes(Context context) {
        switch (getTheme(context)) {
            case THEME_BLACK:
                return R.style.ThemeOverlay_Black;
            case THEME_DEFAULT:
            default:
                return R.style.ThemeOverlay;
        }
    }
}
