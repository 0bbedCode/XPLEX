package com.obbedcode.xplex.views.fragment.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.obbedcode.shared.data.XApp;
import com.obbedcode.xplex.databinding.AppFragmentBinding;
import com.obbedcode.xplex.views.adapter.AppsAdapter;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.fragment.base.BaseFragment;
import com.obbedcode.xplex.views.viewmodel.AppsViewModel;

import java.util.List;

import kotlin.Pair;

public class AppFragment  extends BaseFragment<AppFragmentBinding>
        implements AppsAdapter.OnItemClickListener,
        IOnTabClickListener,
        IOnClearClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getArguments() != null ? getArguments().getString("type", "user") : "user";
        //mViewModel = new ViewModelProvider(this).get(AppsViewModelEx.class);
        //mViewModel.setType(type);
    }


    @Override
    public void onItemClick(XApp app) {

    }

    @Override
    public void onItemLongClick(XApp app) {

    }

    @Override
    public void onClearAll() {

    }

    @Override
    public void search(String keyword) {

    }

    @Override
    public void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse) {

    }

    @Override
    public void onReturnTop() {

    }
}
