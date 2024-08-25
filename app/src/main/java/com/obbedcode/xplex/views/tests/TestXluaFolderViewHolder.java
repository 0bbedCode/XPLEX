package com.obbedcode.xplex.views.tests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.obbedcode.shared.DirectoryUtils;
import com.obbedcode.xplex.R;

import java.io.File;

import rikka.recyclerview.BaseViewHolder;

public class TestXluaFolderViewHolder extends CoreTestBaseViewHolder {
    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder<Object> createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            View innerView = inflater.inflate(R.layout.core_test_item, parent, false);
            return new TestXluaFolderViewHolder(innerView);
        }
    };

    public TestXluaFolderViewHolder(View itemView) {
        super(itemView);
        init(R.string.text_test_folder_xlua, true);
        //executeTest();
    }

    @Override
    public boolean executeTest() {
        try {
            return new File(DirectoryUtils.getOldDirectory()).isDirectory();
        } catch (Exception e) {
            setLastMessage(e.getMessage());
            return false;
        }
    }
}
