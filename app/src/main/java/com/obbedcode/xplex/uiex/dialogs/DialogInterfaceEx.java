package com.obbedcode.xplex.uiex.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;

public interface DialogInterfaceEx {
    public static final int DIALOG_NEGATIVE = 0;
    public static final int DIALOG_POSITIVE = 1;
    public static final int DIALOG_CANCELED = 2;

    public static final int DIALOG_CODE_GOOD = 1;
    public static final int DIALOG_CODE_UNKNOWN = 0;
    public static final int DIALOG_CODE_ERROR = -1;
    //public static final String KEY_RESULT_CODE = "result_code";

    interface OnClickListener {
        void onClick(DialogInterface dialog, int which, BaseDialog baseDialog);
    }

    interface OnDismissEvent {
        void onDialogDismiss(Bundle data, int resultCode, int dialogId);
        //void onDialogPositiveFinish(Bundle result, int )
    }

    interface OnExceptionEvent {
        void onException(Bundle data, Exception e, int dialogId);
    }

    interface OnPositiveDoneEvent{
        void onPositiveDone(Bundle data, int dialogId);
    }
}
