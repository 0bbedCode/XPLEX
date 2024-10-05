package com.obbedcode.xplex.views.fragment.app;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.obbedcode.shared.xplex.data.hook.XHookApp;
import com.obbedcode.shared.xplex.data.hook.XHookGroup;
import com.obbedcode.shared.xplex.data.XUser;
import com.obbedcode.xplex.databinding.AppHookFragmentBinding;
import com.obbedcode.xplex.views.adapter.app.HookGroupAdapter;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.fragment.base.BaseFragmentAdapter;
import com.obbedcode.xplex.views.viewmodel.app.HookGroupViewModel;

import java.util.List;

import kotlin.Pair;

public class HooksFragment
        extends
        BaseFragmentAdapter<AppHookFragmentBinding, XHookGroup>
        implements
        HookGroupAdapter.OnItemClickListener,
        IOnTabClickListener,
        IOnClearClickListener {


    private static final String TAG = "ObbedCode.XP.HooksFragment";

    private HookGroupViewModel mViewModel;

    public static HooksFragment newInstance(XHookApp app) {
        HooksFragment frag = new HooksFragment();
        frag.setArguments(app.toBundle());
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HookGroupViewModel.class);
        XHookApp app = new XHookApp();
        app.fromBundle(getArguments());
        mViewModel.setHookApplication(app);
        mViewModel.setTargetUser(XUser.DEFAULT);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.bindAdapter(new HookGroupAdapter(requireContext(), this))
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
    protected LiveData<List<XHookGroup>> getLiveData() { return mViewModel.getRawLiveData(); }

    @Override
    protected void onRefreshEvent() { mViewModel.refresh(); }


    @Override
    public void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse) { mViewModel.updateList(filter, keyword, isReverse); }

    @Override
    public void onItemClick(XHookGroup group) {
        //todo
    }

    @Override
    public void onItemLongClick(XHookGroup group) {
        //todo
    }
}
