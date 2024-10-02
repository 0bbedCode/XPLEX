package com.obbedcode.xplex.views.fragment.base;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.color.MaterialColors;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.xplex.MainActivity;
import com.obbedcode.xplex.views.etc.CustomViewFlipper;
import com.obbedcode.xplex.views.etc.FooterAdapter;
import com.obbedcode.xplex.views.etc.INavContainer;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.etc.LinearItemDecoration;
import com.obbedcode.xplex.views.etc.OnClearContainer;
import com.obbedcode.xplex.views.etc.OnTabClickContainer;
import com.obbedcode.xplex.views.etc.UiUtil;
import com.obbedcode.xplex.views.fragment.apps.AppsPagerFragment;

import java.util.List;

import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public abstract class BaseFragmentAdapter<T extends ViewBinding, L> extends BaseFragment<T> implements
        IOnTabClickListener,
        IOnClearClickListener {

    protected String id;    //Set this fucker
    protected ListAdapter<L, ?> adapter;

    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefresh;
    protected ProgressBar progressBar;
    protected CustomViewFlipper customViewFlipper;

    protected final FooterAdapter footerAdapter = new FooterAdapter();

    protected abstract LiveData<List<L>> getLiveData();
    protected abstract void onRefreshEvent();
    protected void updateSearchHint(int size) {
        if(this.isResumed()) {
            if(getParentFragment() instanceof AppsPagerFragment) {
                BasePagerFragment frag = (BasePagerFragment) getParentFragment();
                frag.setHint(size);
            }
        }
    }

    public BaseFragmentAdapter<T, L> bindId(String id) {
        this.id = id;
        return this;
    }

    public BaseFragmentAdapter<T, L> bindCustomViewFlipper(CustomViewFlipper flipper) {
        this.customViewFlipper = flipper;
        return this;
    }

    public BaseFragmentAdapter<T, L> bindAdapter(ListAdapter<L, ?> adapter) {
        this.adapter = adapter;
        return this;
    }

    public BaseFragmentAdapter<T, L> bindRecyclerView(RecyclerView rc) {
        this.recyclerView = rc;
        return this;
    }

    public BaseFragmentAdapter<T, L> bindSwipeRefresh(SwipeRefreshLayout swipeLayout) {
        this.swipeRefresh = swipeLayout;
        return this;
    }

    public BaseFragmentAdapter<T, L> bindProgressBar(ProgressBar pb) {
        this.progressBar = pb;
        return this;
    }

    public BaseFragmentAdapter<T, L> initSwipeRefresh() {
        swipeRefresh.setColorSchemeColors(MaterialColors.getColor(requireContext(), com.google.android.material.R.attr.colorPrimary, -1));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshEvent();
            }
        });

        return this;
    }

    public BaseFragmentAdapter<T, L> initObserve() {
        getLiveData().observe(getViewLifecycleOwner(), new Observer<List<L>>() {
            @Override
            public void onChanged(List<L> ls) {
                adapter.submitList(ls);
                if(swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if(progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                if(adapter != null && ls != null) {
                    updateSearchHint(ls.size());
                    if(recyclerView != null) {
                        ConcatAdapter conAdapter = (ConcatAdapter)recyclerView.getAdapter();
                        if(conAdapter != null) {
                            if(ls.isEmpty() && conAdapter.getAdapters().contains(footerAdapter))
                                conAdapter.removeAdapter(footerAdapter);
                        }

                        if(customViewFlipper != null && customViewFlipper.getDisplayedChild() != ls.size())
                            customViewFlipper.setDisplayedChild(ls.size());
                    }
                }
            }
        });
        return this;
    }

    public BaseFragmentAdapter<T, L> initRecyclerView(boolean hideNavigation, boolean addLinearDecoration, boolean addFastScrollbar) {
        //adapter = new AppsAdapter(requireContext(), this);
        //_binding.recyclerView.setAdapter(new ConcatAdapter(mAdapter));
        if(recyclerView != null) {
            if(adapter != null)
                recyclerView.setAdapter(new ConcatAdapter(adapter));

            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            if(hideNavigation) {
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        Activity ac = getActivity();
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
            }

            if(addLinearDecoration)
                recyclerView.addItemDecoration(new LinearItemDecoration(UiUtil.dpToPx(requireContext(),4)));

            if(addFastScrollbar)
                new FastScrollerBuilder(recyclerView).useMd2Style().build();
        }

        return this;
    }

    public BaseFragmentAdapter<T, L> linkBottomFooterToCustomFlipper() {
        if(customViewFlipper != null) {
            customViewFlipper.setOnDisplayedChildChangedListener(new Function1<CustomViewFlipper.OnDisplayedChildChangedListener, Unit>() {
                @Override
                public Unit invoke(CustomViewFlipper.OnDisplayedChildChangedListener onDisplayedChildChangedListener) {
                    UiUtil.setSpaceFooterView(recyclerView, footerAdapter);
                    return Unit.INSTANCE;
                }
            });
        }
        return this;
    }

    @Override
    public void onPause() {
        super.onPause();
        Fragment parent = getParentFragment();
        if(parent instanceof OnTabClickContainer) ((OnTabClickContainer)parent).setTabController(null);
        if(parent instanceof OnClearContainer) ((OnClearContainer)parent).setOnClearClickListener(null);
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

        List<L> data = getLiveData().getValue();
        updateSearchHint(data == null ? 0 : data.size());
    }

    @Override
    public void onClearAll() { }

    @Override
    public void search(String keyword) { }

    @Override
    public void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse) { }

    @Override
    public void onReturnTop() {
        recyclerView.scrollToPosition(0);
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            //Show "showNavigation();
            //MainActivity ac = (MainActivity) activity;
            //ac
        }
    }
}
