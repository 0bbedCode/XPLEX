package com.obbedcode.xplex.views.tests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.XplexApplication;
import com.topjohnwu.superuser.Shell;

import rikka.recyclerview.BaseViewHolder;


public class TestRootViewHolder extends CoreTestBaseViewHolder {
    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder<Object> createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            View innerView = inflater.inflate(R.layout.core_test_item, parent, false);
            return new TestRootViewHolder(innerView);
        }
    };

    public TestRootViewHolder(View itemView) {
        super(itemView);
        init(R.string.text_test_root, false);
    }

    @Override
    public boolean executeTest() {
        try {
            if(!XplexApplication.Manager.hasRootAccess)
                XplexApplication.Manager.requestRoot();
            return Shell.getShell().isRoot();
        } catch (Exception e) {
            setLastMessage(e.getMessage());
            return false;
        }
    }
}