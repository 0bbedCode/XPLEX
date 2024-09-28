package com.obbedcode.xplex.views.fragment.apps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.color.MaterialColors;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.xplex.BuildConfig;
import com.obbedcode.xplex.MainActivity;
import com.obbedcode.xplex.databinding.AppsFragmentBinding;
import com.obbedcode.xplex.views.adapter.AppsAdapter;
import com.obbedcode.xplex.views.etc.CustomViewFlipper;
import com.obbedcode.xplex.views.etc.FooterAdapter;
import com.obbedcode.xplex.views.etc.INavContainer;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.etc.LinearItemDecoration;
import com.obbedcode.xplex.views.etc.OnClearContainer;
import com.obbedcode.xplex.views.etc.OnTabClickContainer;
import com.obbedcode.xplex.views.etc.UiUtil;
import com.obbedcode.xplex.views.fragment.base.BaseFragment;
import com.obbedcode.xplex.views.viewmodel.apps.AppViewModel;

import java.util.List;

import kotlin.Pair;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class AppsFragment extends BaseFragment<AppsFragmentBinding>
        implements AppsAdapter.OnItemClickListener,
        IOnTabClickListener,
        IOnClearClickListener {

    private static final String TAG = "ObbedCode.XP.AppsFragment";

    private BottomSheetDialog appConfigDialog;
    private BottomSheetDialog appInfoDialog;

    private AppViewModel mViewModel;
    private AppsAdapter mAdapter;
    private final FooterAdapter mFooterAdapter = new FooterAdapter();


    public static AppsFragment newInstance(String type) {
        AppsFragment frag = new AppsFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        frag.setArguments(b);

        if(BuildConfig.DEBUG)
            XLog.i(TAG, "newInstance=" + type);

        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getArguments() != null ? getArguments().getString("type", "user") : "user";
        //XLog.i(TAG, "[onCreate] TYPE=" + type);
        mViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        mViewModel.setType(type);

        //XLog.i(TAG, "[onCreate] Finished....");
        //if ("configured".equals(type)) {
        //    Fragment parentFragment = getParentFragment();
        //    if (parentFragment instanceof IOnFabClickContainer) {
        //        ((IOnFabClickContainer) parentFragment).setFabController(this);
        //    }
        //}

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        XLog.i(TAG, "[onViewCreated] ...");
        initView();
        initRefresh();
        initSheet();
        initObserve();
    }

    private void initSheet() {
        //appConfigDialog = new BottomSheetDialog(requireContext());

    }

    private void initView() {
        mAdapter = new AppsAdapter(requireContext(), this);
        binding.recyclerView.setAdapter(new ConcatAdapter(mAdapter));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (getActivity() instanceof INavContainer) {
                    INavContainer navContainer = (INavContainer) getActivity();
                    if (dy > 0) {
                        navContainer.hideNavigation();
                    } else if (dy < 0) {
                        navContainer.showNavigation();
                    }
                }
            }
        });

        binding.recyclerView.addItemDecoration(new LinearItemDecoration(4));
        new FastScrollerBuilder(binding.recyclerView).useMd2Style().build();

        binding.vfContainer.setOnDisplayedChildChangedListener(new CustomViewFlipper.OnDisplayedChildChangedListener() {
            @Override
            public void onChanged(int whichChild) {
                //binding.recyclerView
                UiUtil.setSpaceFooterView(binding.recyclerView, mFooterAdapter);
            }
        });
    }

    private void initRefresh() {
        XLog.i(TAG, "[initRefresh]");
        binding.swipeRefresh.setColorSchemeColors(MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary, -1));
        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refreshApps();
            }
        });
    }

    private void initObserve() {
        XLog.i(TAG, "[initObserve]");
        mViewModel.getAppsLiveData().observe(getViewLifecycleOwner(), appInfos -> {
            XLog.i(TAG, "[initObserve] => App Info Size: " + appInfos.size());
            mAdapter.submitList(appInfos);
            binding.swipeRefresh.setRefreshing(false);
            binding.progressBar.setVisibility(View.INVISIBLE);
            updateSearchHint(appInfos.size());
            ConcatAdapter adapter = (ConcatAdapter) binding.recyclerView.getAdapter();
            if(adapter != null && appInfos.isEmpty() && adapter.getAdapters().contains(mFooterAdapter))
                adapter.removeAdapter(mFooterAdapter);

            if(binding.vfContainer.getDisplayedChild() != appInfos.size())
                binding.vfContainer.setDisplayedChild(appInfos.size());
        });
    }

    private void updateSearchHint(int size) {
        if(this.isResumed()) {
            if(getParentFragment() instanceof AppsPagerFragment) {
                AppsPagerFragment frag = (AppsPagerFragment) getParentFragment();
                frag.setHint(size);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment parent = getParentFragment();
        if(parent instanceof OnTabClickContainer) {
            OnTabClickContainer tabClick = (OnTabClickContainer) parent;
            tabClick.setTabController(this);
        }

        if(parent instanceof OnClearContainer) {
            OnClearContainer conClick = (OnClearContainer)parent;
            conClick.setOnClearClickListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Fragment parent = getParentFragment();
        if(parent instanceof OnTabClickContainer) ((OnTabClickContainer)parent).setTabController(null);
        if(parent instanceof OnClearContainer) ((OnClearContainer)parent).setOnClearClickListener(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(appConfigDialog != null)
            appConfigDialog.dismiss();
        if(appInfoDialog != null)
            appInfoDialog.dismiss();
        appConfigDialog = null;
        appInfoDialog = null;
    }

    @Override
    public void onItemClick(XApp app) {
        //ignore
    }

    @Override
    public void onItemLongClick(XApp app) {
        //ignore
    }

    @Override
    public void onReturnTop() {
        binding.recyclerView.scrollToPosition(0);
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            //Show "showNavigation();
        }
    }

    @Override
    public void onClearAll() {

    }

    @Override
    public void search(String keyword) {

    }

    @Override
    public void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse) {
        XLog.i(TAG, "[updateSortedList] List Updating => " + keyword + " IsReversed: " + isReverse);
        mViewModel.updateList(filter, keyword, isReverse);
    }
}
