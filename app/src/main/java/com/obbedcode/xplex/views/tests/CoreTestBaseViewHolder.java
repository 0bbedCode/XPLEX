package com.obbedcode.xplex.views.tests;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.obbedcode.xplex.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rikka.recyclerview.BaseViewHolder;

public abstract  class CoreTestBaseViewHolder extends BaseViewHolder<Object> {
    public static ExecutorService executor = Executors.newFixedThreadPool(100);

    private final TextView mTvTestName;
    private final ImageView mIvTestIcon;
    private final View mView;
    private final ProgressBar mPbTest;

    private String mTestName;
    //private int mTestCode;
    private String mLastMessage;

    private boolean mLastResult;

    public CoreTestBaseViewHolder(View view) {
        super(view);
        mView = view;
        mTvTestName = view.findViewById(R.id.tv_core_item_name);
        mIvTestIcon = view.findViewById(R.id.iv_core_item_icon);
        mPbTest = view.findViewById(R.id.pb_test_progress);
        mIvTestIcon.setVisibility(View.INVISIBLE);
    }

    public void setIconVisible() { mIvTestIcon.setVisibility(View.VISIBLE); }
    public void setProgressBarInvisible() { mPbTest.setVisibility(View.INVISIBLE); }

    public String getLastMessage() { return mLastMessage; }
    public void setLastMessage(String message) { mLastMessage = message; }

    public boolean getLastResult() { return mLastResult; }
    private void setLastResult(boolean lastResult) { mLastResult = lastResult; }

    public void setTestName(String testName) { mTestName = testName; }
    public String getTestName() { return mTestName; }

    public void init(int resId, boolean useEye) { init(mView.getResources().getString(resId), useEye); }
    public void init(String testName, boolean useEye) {
        setTestName(testName);
        mTvTestName.setText(testName);
        executor.submit(() -> {
            final boolean res = executeTest();
            new Handler(Looper.getMainLooper()).post(() -> {
                if(!res) {
                    if(useEye) setEyeOpen();
                    else setThumbsDown();
                }else {
                    if(useEye) setEyeClosed();
                    else setThumbsUp();
                }
                setProgressBarInvisible();
                setIconVisible();
            });
        });
    }

    public TextView getTextView() { return mTvTestName; }
    public ImageView getImageView() { return mIvTestIcon; }

    public void setThumbsUp() {
        mIvTestIcon.setImageResource(R.drawable.ic_remix_thumb_up_line);
        mIvTestIcon.setRotation(0);
    }

    public void setThumbsDown() {
        mIvTestIcon.setImageResource(R.drawable.ic_remix_thumb_up_line);
        mIvTestIcon.setRotation(180);
    }

    public void setEyeOpen() { mIvTestIcon.setImageResource(R.drawable.ic_remix_eye_line); }
    public void setEyeClosed() { mIvTestIcon.setImageResource(R.drawable.ic_remix_eye_off_line); }

    public boolean executeTest() {
        return false;
    }
}
