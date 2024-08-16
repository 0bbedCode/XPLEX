package com.obbedcode.xplex.views.tests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.obbedcode.xplex.R;

import rikka.recyclerview.BaseViewHolder;


public class TestXplexFolderViewHolder extends CoreTestBaseViewHolder {
    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder<Object> createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            View innerView = inflater.inflate(R.layout.core_test_item, parent, false);
            return new TestXplexFolderViewHolder(innerView);
        }
    };

    public TestXplexFolderViewHolder(View itemView) {
        super(itemView);
        init(R.string.text_test_folder_xplex, true);
        //executeTest();
    }

    @Override
    public boolean executeTest() {
        try {
            Thread.sleep(1200);
            //setEyeClosed();
            return true;
        } catch (Exception e) {
            setLastMessage(e.getMessage());
            //setThumbsDown();
            return false;
        }
    }
}
