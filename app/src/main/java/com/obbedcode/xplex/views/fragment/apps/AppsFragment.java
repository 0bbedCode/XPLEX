package com.obbedcode.xplex.views.fragment.apps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.color.MaterialColors;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
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
import com.obbedcode.xplex.views.viewmodel.AppsViewModel;

import java.util.List;

import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public class AppsFragment extends BaseFragment<AppsFragmentBinding>
        implements AppsAdapter.OnItemClickListener,
        IOnTabClickListener,
        IOnClearClickListener {

    private BottomSheetDialog appConfigDialog;
    private BottomSheetDialog appInfoDialog;

    private AppsViewModel mViewModel;
    private AppsAdapter mAdapter;
    private final FooterAdapter mFooterAdapter = new FooterAdapter();

    public static AppsFragment newInstance(String type) {
        AppsFragment frag = new AppsFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getArguments() != null ? getArguments().getString("type", "user") : "user";
        mViewModel = new ViewModelProvider(this).get(AppsViewModel.class);
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
        _binding.recyclerView.setAdapter(new ConcatAdapter(mAdapter));
        _binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        _binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Activity ac = getActivity();
                if(ac != null) {
                    XLog.i("ObbedCode.XP.AppsFragment", " AC=" + ac.getClass().getName());
                }

                if (ac instanceof INavContainer) {
                    INavContainer navContainer = (INavContainer) ac;
                    if (dy > 0) {
                        navContainer.hideNavigation();
                    } else if (dy < 0) {
                        navContainer.showNavigation();
                    }
                }
            }
        });

        _binding.recyclerView.addItemDecoration(new LinearItemDecoration(UiUtil.dpToPx(requireContext(),4)));
        new FastScrollerBuilder(_binding.recyclerView).useMd2Style().build();

        //_binding.vfContainer.setOnDisplayedChildChangedListener(new CustomViewFlipperEx.OnDisplayedChildChangedListener() {
        //    @Override
        //    public void onChanged(int whichChild) {
        //        UiUtil.setSpaceFooterView(_binding.recyclerView, mFooterAdapter);
        //    }
        //});

        _binding.vfContainer.setOnDisplayedChildChangedListener(new Function1<CustomViewFlipper.OnDisplayedChildChangedListener, Unit>() {
            @Override
            public Unit invoke(CustomViewFlipper.OnDisplayedChildChangedListener onDisplayedChildChangedListener) {
                UiUtil.setSpaceFooterView(_binding.recyclerView, mFooterAdapter);
                return Unit.INSTANCE;
            }
        });

        //_binding.vfContainer.setOnDisplayedChildChangedListener(new CustomViewFlipperEx().OnDisplayedChildChangedListener() {
        //    @Override
        //    public void onChanged(int whichChild) {
        //        UiUtil.setSpaceFooterView(_binding.recyclerView, mFooterAdapter);
        //        //_binding.recyclerView.setSpaceFooterView(footerAdapter);
        //    }
        //});

        //_binding.vfContainer.setOnDisplayedChildChangedListener(new Function1<CustomViewFlipper.OnDisplayedChildChangedListener, Unit>() {
        //    @Override
        //    public Unit invoke(CustomViewFlipper.OnDisplayedChildChangedListener onDisplayedChildChangedListener) {
        //        UiUtil.setSpaceFooterView(_binding.recyclerView, mFooterAdapter);
        //        return Unit.INSTANCE;
        //    }
        //});

        //                UiUtil.setSpaceFooterView(_binding.recyclerView, mFooterAdapter);
        //_binding.vfContainer.setOnDisplayedChildChangedListenerEx(new CustomViewFlipper.OnDisplayedChildChangedListener() {
        //    @Override
        //    public void onChanged(int whichChild) {
        //        UiUtil.setSpaceFooterView(_binding.recyclerView, mFooterAdapter);
        //    }
        //});
    }

    private void initRefresh() {
        _binding.swipeRefresh.setColorSchemeColors(MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary, -1));
        _binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.refresh();
            }
        });
    }

    private void initObserve() {
        mViewModel.getAppsLiveData().observe(getViewLifecycleOwner(), new Observer<List<XApp>>() {
            @Override
            public void onChanged(List<XApp> xApps) {
                //this is for WHEN data changes ?
                //This is for WHEN your MODIFY data it determines if it should add the Foot Header then and only then
                mAdapter.submitList(xApps);
                _binding.swipeRefresh.setRefreshing(false);
                _binding.progressBar.setVisibility(View.INVISIBLE);
                updateSearchHint(xApps.size());
                ConcatAdapter adapter = (ConcatAdapter) _binding.recyclerView.getAdapter();
                if(adapter != null && xApps.isEmpty() && adapter.getAdapters().contains(mFooterAdapter))
                    adapter.removeAdapter(mFooterAdapter);

                if(_binding.vfContainer.getDisplayedChild() != xApps.size())
                    _binding.vfContainer.setDisplayedChild(xApps.size());
            }
        });

        /*mViewModel.getAppsLiveData().observe(getViewLifecycleOwner(), appInfos -> {
            mAdapter.submitList(appInfos);
            binding.swipeRefresh.setRefreshing(false);
            binding.progressBar.setVisibility(View.INVISIBLE);
            updateSearchHint(appInfos.size());
            ConcatAdapter adapter = (ConcatAdapter) binding.recyclerView.getAdapter();
            if(adapter != null && appInfos.isEmpty() && adapter.getAdapters().contains(mFooterAdapter))
                adapter.removeAdapter(mFooterAdapter);

            if(binding.vfContainer.getDisplayedChild() != appInfos.size())
                binding.vfContainer.setDisplayedChild(appInfos.size());
        });*/
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

        List<XApp> apps = mViewModel.getAppsLiveData().getValue();
        int sz = apps == null ? 0 : apps.size();
        updateSearchHint(sz);
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
        if(appConfigDialog != null) appConfigDialog.dismiss();
        if(appInfoDialog != null) appInfoDialog.dismiss();
        appConfigDialog = null;
        appInfoDialog = null;
    }

    @Override
    public void onItemClick(XApp app) {
        //
        //ignore
        //



    }

    @Override
    public void onItemLongClick(XApp app) {
        //ignore
        //Long click will just open it ? we can also reverse it
    }

    @Override
    public void onReturnTop() {
        _binding.recyclerView.scrollToPosition(0);
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            //Show "showNavigation();
            //MainActivity ac = (MainActivity) activity;
            //ac
        }
    }

    @Override
    public void onClearAll() {

    }

    @Override
    public void search(String keyword) {

    }

    @Override
    public void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse) { mViewModel.updateList(filter, keyword, isReverse); }
}
