package com.obbedcode.xplex.views.tests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.obbedcode.xplex.R;

import rikka.recyclerview.BaseViewHolder;

public class TestXposedViewHolder extends CoreTestBaseViewHolder {
    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder<Object> createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            //TextView tvStat = parent.getRootView().findViewById(R.id.tv_pb_current_test);
            //tvStat.setText(R.string.text_test_xposed);
            View innerView = inflater.inflate(R.layout.core_test_item, parent, false);
            return new TestXposedViewHolder(innerView);
        }
    };

    public TestXposedViewHolder(View itemView) {
        super(itemView);

        //ViewGroup vg = (ViewGroup)itemView.getParent();
        //TextView tvStat = vg.getRootView().findViewById(R.id.tv_pb_current_test);
        //tvStat.setText(R.string.text_test_xposed);

        init(R.string.text_test_xposed, false);
    }

    @Override
    public boolean executeTest() {
        try {
            Thread.sleep(3000);
            //setThumbsUp();
            return true;
        } catch (Exception e) {
            setLastMessage(e.getMessage());
            //setThumbsDown();
            return false;
        }
    }
}
