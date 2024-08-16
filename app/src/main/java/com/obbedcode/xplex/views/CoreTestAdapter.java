package com.obbedcode.xplex.views;

import android.annotation.SuppressLint;
import android.util.Log;

import com.obbedcode.xplex.views.tests.CoreTestBaseViewHolder;
import com.obbedcode.xplex.views.tests.TestRootViewHolder;
import com.obbedcode.xplex.views.tests.TestServiceViewHolder;
import com.obbedcode.xplex.views.tests.TestXluaFolderViewHolder;
import com.obbedcode.xplex.views.tests.TestXplexFolderViewHolder;
import com.obbedcode.xplex.views.tests.TestXposedViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rikka.recyclerview.BaseViewHolder;
import rikka.recyclerview.IdBasedRecyclerViewAdapter;

public class CoreTestAdapter extends IdBasedRecyclerViewAdapter {
    public CoreTestAdapter() {
        super(new ArrayList<>());
        updateData();
        setHasStableIds(true);
    }

    private static final int ID_XPOSED = 6;

    private static final int ID_XPLEX_SERVICE = 7;
    private static final int ID_FOLDER_XLUA = 8;
    private static final int ID_FOLDER_XPLEX = 9;

    private static final int ID_DATABASE_XLUA = 10;
    private static final int ID_DATABASE_MOCK = 11;
    private static final int ID_OLD_COLLECTIONS = 12;
    private static final int ID_INTERNAL_HOOK_MODIFICATIONS = 13;

    private static final int ID_ROOT_ACCESS = 1337;


    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        clear();
        addItem(TestRootViewHolder.CREATOR, null, ID_ROOT_ACCESS);
        addItem(TestXposedViewHolder.CREATOR, null, ID_XPOSED);
        addItem(TestServiceViewHolder.CREATOR, null, ID_XPLEX_SERVICE);
        addItem(TestXluaFolderViewHolder.CREATOR, null, ID_FOLDER_XLUA);
        addItem(TestXplexFolderViewHolder.CREATOR, null, ID_FOLDER_XPLEX);
        notifyDataSetChanged();
    }
}
