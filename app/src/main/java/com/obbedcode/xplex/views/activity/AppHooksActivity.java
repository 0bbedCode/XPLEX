package com.obbedcode.xplex.views.activity;

import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.data.XUser;
import com.obbedcode.shared.xplex.data.hook.XHookApp;
import com.obbedcode.shared.xplex.data.hook.XHookContainer;
import com.obbedcode.shared.xplex.data.hook.XHookGroup;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.AppHooksActivityBinding;
import com.obbedcode.xplex.views.HookAppsAdapter;
import com.obbedcode.xplex.views.activity.app.AppBarActivity;
import com.obbedcode.xplex.views.adapter.HookAdapter;
import com.obbedcode.xplex.views.fragment.app.pager.AppPagerFragment;
import com.obbedcode.xplex.views.viewmodel.app.HookViewModel;

import rikka.recyclerview.RecyclerViewKt;

public class AppHooksActivity extends AppBarActivity implements HookAdapter.OnItemClickListener {
    private static final String TAG = "ObbedCode.XP.AppHooksActivity";

    private AppHooksActivityBinding mBinding;
    private HookViewModel mViewModel;
    private HookAdapter mAdapater;

    private XHookApp mApp;
    private XUser mUser;
    private XHookGroup mGroup;
    //private XHookContainer mContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mBinding = AppHooksActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewModel = new ViewModelProvider(this).get(HookViewModel.class);
        //String packageName = getIntent().getStringExtra("packageName");

        mApp = new XHookApp();
        mApp.fromIntent(getIntent());

        mGroup = new XHookGroup();
        mGroup.fromIntent(getIntent());

        mUser = new XUser();
        mUser.fromIntent(getIntent());

        XLog.i(TAG, "APP=" + mApp + "  GROUP=" + mGroup + "  USER=" + mUser);

        mViewModel.setHookApplication(mApp);
        mViewModel.setTargetUser(mUser);
        mViewModel.setType(mGroup.id);

        //mApp.fromBundle();
        //mApp = new XHookApp();
        //mApp.fromBundle(getArguments());
        //mViewModel.setHookApplication(mApp);
        //mViewModel.setTargetUser(XUser.DEFAULT);
        //super.onCreate(savedInstanceState);

        //I dont think we need to invoke a "update" ?

        //mAdapater = new HookAdapter(mBinding.getRoot().getContext(), this);

        RecyclerView recyclerView = mBinding.list;
        //recyclerView.setAdapter(mAdapater);
        //RecyclerViewKt.fixEdgeEffect(recyclerView);
        RecyclerViewKt.addEdgeSpacing(recyclerView, 0, 8, 0, 8, TypedValue.COMPLEX_UNIT_DIP);

        super.onCreate(savedInstanceState);

        //mAdapater.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
        //    @Override
        //    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        //        mViewModel.loadCount();
        //    }
        //});

    }

    @Override
    public void onItemClick(XHookContainer definition) {

    }

    @Override
    public void onItemLongClick(XHookContainer definition) {

    }
}


