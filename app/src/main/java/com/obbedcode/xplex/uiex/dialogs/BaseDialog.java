package com.obbedcode.xplex.uiex.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;

import com.obbedcode.shared.logger.XLog;

public class BaseDialog extends AppCompatDialogFragment {
    private static final String TAG = "ObbedCode.XP.BaseDialog";
    //Make sure we dont need to specify fragments ???

    protected Context context;

    protected Integer titleId;
    protected String title;

    protected Integer negativeTextId;
    protected String negativeText;

    protected DialogInterfaceEx.OnClickListener onNegative;
    protected DialogInterfaceEx.OnClickListener onPositive;

    protected Integer positiveTextId;
    protected String positiveText;

    protected Integer layoutId;

    public Context getMyContext() { return this.context; }
    public View getView() { return this.view; }

    public BaseDialog setTitle(String title) { this.title = title; return this; }
    public BaseDialog setTitle(int resId) { this.titleId = resId; return this; }
    public BaseDialog setLayout(int resId) { this.layoutId = resId; return this; }

    protected int dialogId = 0;
    public int getDialogId() { return this.dialogId; }

    protected int resultCode = 0;
    public Bundle data = new Bundle();

    protected DialogInterfaceEx.OnDismissEvent onDismissCallback;
    protected DialogInterfaceEx.OnPositiveDoneEvent onPositiveDoneCallback;

    protected View view;

    public BaseDialog() { }

    public BaseDialog(int viewResourceId, String title) { this(viewResourceId, title, 0); }
    public BaseDialog(int viewResourceId, String title, int dialogId) {
        this.title = title;
        this.layoutId = viewResourceId;
        this.dialogId = dialogId;
    }

    public BaseDialog(int viewResourceId, int titleResourceId) { this(viewResourceId, titleResourceId, 0); }
    public BaseDialog(int viewResourceId, int titleResourceId, int dialogId) {
        this.titleId = titleResourceId;
        this.layoutId = viewResourceId;
        this.dialogId = dialogId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final BaseDialog thisInstance = this;
        if(layoutId != null) {
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            view = inflater.inflate(layoutId, null);
            if(view != null) {
                builder.setView(view);
                if(title != null) {
                    builder.setTitle(title);
                }else if(titleId != null) {
                    builder.setTitle(titleId);
                }

                if(negativeText != null) {
                    builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(onNegative != null)
                                onNegative.onClick(dialog, which, thisInstance);
                        }
                    });
                } else if(negativeTextId != null) {
                    builder.setNegativeButton(negativeTextId, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(onNegative != null)
                                onNegative.onClick(dialog, which, thisInstance);
                        }
                    });
                }

                if(positiveText != null) {
                    builder.setNegativeButton(positiveText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(onPositive != null) {
                                onPositive.onClick(dialog, which, thisInstance);
                                if(onPositiveDoneCallback != null)
                                    onPositiveDoneCallback.onPositiveDone(data, dialogId);
                            }
                        }
                    });
                } else if(positiveTextId != null) {
                    builder.setNegativeButton(positiveTextId, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(onPositive != null) {
                                onPositive.onClick(dialog, which, thisInstance);
                                if(onPositiveDoneCallback != null)
                                    onPositiveDoneCallback.onPositiveDone(data, dialogId);
                            }
                        }
                    });
                }

            }
        } return builder.create();
    }

    //
    //Builder Functions
    //


    public BaseDialog setNegativeButton(String negativeText, DialogInterfaceEx.OnClickListener onClick) {
        this.onNegative = onClick;
        this.negativeText = negativeText;
        return this;
    }

    public BaseDialog setNegativeButton(int negativeText, DialogInterfaceEx.OnClickListener onClick) {
        this.onNegative = onClick;
        this.negativeTextId = negativeText;
        return this;
    }

    public BaseDialog setPositiveButton(String positiveText, DialogInterfaceEx.OnClickListener onClick) {
        this.onPositive = onClick;
        this.positiveText = positiveText;
        return this;
    }

    public BaseDialog setPositiveButton(int positiveText, DialogInterfaceEx.OnClickListener onClick) {
        this.onPositive = onClick;
        this.positiveTextId = positiveText;
        return this;
    }

    public BaseDialog setOnPositiveDone(DialogInterfaceEx.OnPositiveDoneEvent onPositiveDone) {
        this.onPositiveDoneCallback = onPositiveDone;
        return this;
    }

    public BaseDialog setOnDismiss(DialogInterfaceEx.OnDismissEvent onFinish) {
        this.onDismissCallback = onFinish;
        return this;
    }

    public BaseDialog setDialogId(int id) {
        this.dialogId = id;
        return this;
    }

    //
    //End of Builder Functions
    //

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(this.onDismissCallback != null) this.onDismissCallback.onDialogDismiss(data, resultCode, dialogId);
        super.onDismiss(dialog);
    }

    public void showEx(@NonNull FragmentManager manager) {
        this.show(manager, null);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void setResultCodeGood() { this.resultCode = DialogInterfaceEx.DIALOG_CODE_GOOD; }
    public void setResultUnknown() { this.resultCode = DialogInterfaceEx.DIALOG_CODE_UNKNOWN; }
    public void setResultError() { this.resultCode = DialogInterfaceEx.DIALOG_CODE_ERROR; }
    public void setResultCode(int code) { this.resultCode = code; }

    public String getStringFromEditText(int resId) { return getStringFromEditText(resId, null); }
    public String getStringFromEditText(int resId, String defaultString) {
        try {
            EditText edt = view.findViewById(resId);
            return edt.getText().toString();
        }catch (Exception e) {
            XLog.e(TAG, "Failed to get String from Edit Test Res ID: " + resId + "\n" + e);
        } return defaultString;
    }
}
