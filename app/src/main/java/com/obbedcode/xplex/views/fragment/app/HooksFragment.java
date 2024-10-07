package com.obbedcode.xplex.views.fragment.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.obbedcode.shared.io.builders.BundleBuilder;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.data.hook.XHookApp;
import com.obbedcode.shared.xplex.data.hook.XHookGroup;
import com.obbedcode.shared.xplex.data.XUser;
import com.obbedcode.xplex.databinding.AppHookFragmentBinding;
import com.obbedcode.xplex.views.activity.AppHookGroupsActivity;
import com.obbedcode.xplex.views.activity.AppHooksActivity;
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
    private XHookApp mApp;
    private XUser mUser;

    public static HooksFragment newInstance(XHookApp app, XUser user) {
        HooksFragment frag = new HooksFragment();
        frag.setArguments(BundleBuilder.combine(app.toBundle(), user.toBundle()));
        XLog.i(TAG, "[new instance] APP=" + app + "  USER=" + user);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(HookGroupViewModel.class);
        mApp = new XHookApp();
        mApp.fromBundle(getArguments());

        mUser = new XUser();
        mUser.fromBundle(getArguments());

        XLog.i(TAG, "APP=" + mApp + "  USER=" + mUser);

        mViewModel.setHookApplication(mApp);
        mViewModel.setTargetUser(mUser);
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
        Intent settingIntent = new Intent(getActivity(), AppHooksActivity.class);

        XLog.i(TAG, "APP=" + mApp + "  USER=" + mUser + "  GROUP=" + group);

        mApp.toIntent(settingIntent);
        mUser.toIntent(settingIntent);
        group.toIntent(settingIntent);
        startActivity(settingIntent);
    }

    @Override
    public void onItemLongClick(XHookGroup group) {
        //todo
    }
}
