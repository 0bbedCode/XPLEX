package com.obbedcode.xplex.views.fragment.app;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.obbedcode.shared.xplex.data.XHookApp;
import com.obbedcode.shared.xplex.data.XLogLog;
import com.obbedcode.xplex.databinding.AppHookFragmentBinding;
import com.obbedcode.xplex.views.adapter.app.LogAdapter;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.fragment.base.BaseFragmentAdapter;
import com.obbedcode.xplex.views.viewmodel.app.LogViewModel;

import java.util.List;

import kotlin.Pair;

public class LogsFragment
        extends
        BaseFragmentAdapter<AppHookFragmentBinding, XLogLog>
        implements
        LogAdapter.OnItemClickListener,
        IOnTabClickListener,
        IOnClearClickListener {

    private LogViewModel mViewModel;

    public static LogsFragment newInstance(XHookApp app) {
        LogsFragment frag = new LogsFragment();
        frag.setArguments(app.toBundle());
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LogViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.bindAdapter(new LogAdapter(requireContext(), this))
                .bindRecyclerView(_binding.recyclerView)
                .bindSwipeRefresh(_binding.swipeRefresh)
                .bindProgressBar(_binding.progressBar)
                .initRecyclerView(true, true, true)
                .initSwipeRefresh()
                .bindCustomViewFlipper(_binding.vfContainer)
                .linkBottomFooterToCustomFlipper()
                .initObserve();
    }


    @Override
    protected LiveData<List<XLogLog>> getLiveData() { return mViewModel.getRawLiveData(); }

    @Override
    protected void onRefreshEvent() { mViewModel.refresh(); }


    @Override
    public void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse) { mViewModel.updateList(filter, keyword, isReverse); }

    @Override
    public void onItemClick(XLogLog log) {
        //todo
    }

    @Override
    public void onItemLongClick(XLogLog log) {
        //todo
    }
}
