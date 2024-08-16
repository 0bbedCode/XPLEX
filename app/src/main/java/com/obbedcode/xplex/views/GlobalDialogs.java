package com.obbedcode.xplex.views;

import androidx.fragment.app.FragmentManager;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.settings.LocalSettings;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.uiex.dialogs.BaseDialog;
import com.obbedcode.xplex.uiex.dialogs.DialogInterfaceEx;

public class GlobalDialogs {
    private static final String TAG = "ObbedCode.XPL.GlobalDialogs";

    public static final int DIALOG_USERNAME = 0;

    public static void invokeSetUsername(FragmentManager fragmentManager, DialogInterfaceEx.OnPositiveDoneEvent onPositiveDone) {
        new BaseDialog(R.layout.dialog_username, R.string.text_username, DIALOG_USERNAME)
                .setPositiveButton(R.string.text_ok, (dialog, which, baseDialog) -> {
                    String uName = baseDialog.getStringFromEditText(R.id.etUsername);
                    if(Str.isValid(uName)) {
                        baseDialog.setResultCodeGood();
                        LocalSettings.setString(baseDialog.getMyContext(), "user_name", uName);
                        baseDialog.data.putString("user_name", uName);
                    }
                })
                .setOnPositiveDone(onPositiveDone)
                .showEx(fragmentManager);
    }

}
