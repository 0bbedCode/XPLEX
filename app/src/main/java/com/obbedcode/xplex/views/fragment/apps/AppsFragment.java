package com.obbedcode.xplex.views.fragment.apps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.data.XUser;
import com.obbedcode.shared.xplex.data.hook.XHookApp;
import com.obbedcode.xplex.databinding.AppsFragmentBinding;
import com.obbedcode.xplex.views.activity.AppHookGroupsActivity;
import com.obbedcode.xplex.views.adapter.AppsAdapter;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.fragment.base.BaseFragmentAdapter;
import com.obbedcode.xplex.views.viewmodel.AppsViewModel;

import java.util.List;

import kotlin.Pair;

public class AppsFragment extends BaseFragmentAdapter<AppsFragmentBinding, XApp>
        implements AppsAdapter.OnItemClickListener,
        IOnTabClickListener,
        IOnClearClickListener {
    private static final String TAG = "ObbedCode.XP.AppsFragment";

    private BottomSheetDialog appConfigDialog;
    private BottomSheetDialog appInfoDialog;
    private AppsViewModel mViewModel;

    private XUser mUser;

    public static AppsFragment newInstance(String type, XUser user) {
        AppsFragment frag = new AppsFragment();
        Bundle b = new Bundle();
        b.putString("type", type);
        b.putBundle("user", user.toBundle());
        XLog.i(TAG, "[new instance] TYPE=" + type + " USER=" + user);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getArguments() != null ? getArguments().getString("type", "user") : "user";
        mUser = new XUser();
        mUser.fromBundle(getArguments());

        XLog.i(TAG, "USER=" + mUser + " TYPE==" + type);

        mViewModel = new ViewModelProvider(this).get(AppsViewModel.class);
        mViewModel.setType(type);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.bindAdapter(new AppsAdapter(requireContext(), this))
                        .bindRecyclerView(_binding.recyclerView)
                        .bindSwipeRefresh(_binding.swipeRefresh)
                        .bindProgressBar(_binding.progressBar)
                        .initRecyclerView(true, true, true)
                        .initSwipeRefresh()
                        .bindCustomViewFlipper(_binding.vfContainer)
                        .linkBottomFooterToCustomFlipper()
                        .initObserve();
    }

    private void initSheet() {
        //appConfigDialog = new BottomSheetDialog(requireContext());
    }

    @Override
    protected LiveData<List<XApp>> getLiveData() { return mViewModel.getRawLiveData(); }

    @Override
    protected void onRefreshEvent() {
        mViewModel.refresh();
    }

    @Override
    public void updateSortedList(Pair<String, List<String>> filter, String keyword, boolean isReverse) { mViewModel.updateList(filter, keyword, isReverse); }

    @Override
    public void onDestroy() {
        //Lets start moving this down a layer ??
        super.onDestroy();
        if(appConfigDialog != null) appConfigDialog.dismiss();
        if(appInfoDialog != null) appInfoDialog.dismiss();
        appConfigDialog = null;
        appInfoDialog = null;
    }

    @Override
    public void onItemClick(XApp app) {
        //Start new Intent
        Intent settingIntent = new Intent(getActivity(), AppHookGroupsActivity.class);
        mUser.toIntent(settingIntent);
        XHookApp hookedApp = app.toHookApp();

        XLog.i(TAG, "[onItemClick] USER=" + mUser + "  APP=" + hookedApp);

        hookedApp.toIntent(settingIntent);
        startActivity(settingIntent);
    }

    @Override
    public void onItemLongClick(XApp app) {
        //ignore
        //Long click will just open it ? we can also reverse it
    }

}
