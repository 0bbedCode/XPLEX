package com.obbedcode.xplex.views.tests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.obbedcode.xplex.R;

import rikka.recyclerview.BaseViewHolder;


public class TestServiceViewHolder extends CoreTestBaseViewHolder {
    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder<Object> createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            View innerView = inflater.inflate(R.layout.core_test_item, parent, false);
            return new TestServiceViewHolder(innerView);
        }
    };


    public TestServiceViewHolder(View itemView) {
        super(itemView);
        init(R.string.text_test_service, false);
    }

    @Override
    public boolean executeTest() {
        try {
            Thread.sleep(1200);
            return true;
        } catch (Exception e) {
            setLastMessage(e.getMessage());
            return false;
        }
    }
}