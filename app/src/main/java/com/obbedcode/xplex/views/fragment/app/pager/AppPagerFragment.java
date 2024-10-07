package com.obbedcode.xplex.views.fragment.app.pager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.obbedcode.shared.PrefManager;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.xplex.data.XUser;
import com.obbedcode.shared.xplex.data.hook.XHookApp;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.BaseTablayoutViewpagerBinding;
import com.obbedcode.xplex.views.fragment.app.HooksFragment;
import com.obbedcode.xplex.views.fragment.app.LogsFragment;
import com.obbedcode.xplex.views.fragment.app.SettingsFragment;
import com.obbedcode.xplex.views.fragment.base.BasePagerFragment;

import java.util.Arrays;
import java.util.List;

public class AppPagerFragment extends BasePagerFragment {
    private static final String TAG = "ObbedCode.XP.AppPagerFragment";

    private final List<Integer> mTabList = Arrays.asList(R.string.tab_app_hook, R.string.tab_app_log, R.string.tab_app_settings);
    private XHookApp targetApplication;
    private XUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = BaseTablayoutViewpagerBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.targetApplication = new XHookApp();
        this.targetApplication.fromBundle(getArguments());
        this.user = new XUser();
        this.user.fromBundle(getArguments());

        XLog.i(TAG, "APP=" + targetApplication + "   USER=" + user);

        super.refManager = PrefManager.create("AppPagerFrag", "Name");
        super.initEditText();
        super.initSearchButtons();
    }

    @Override
    protected void search(String text) {
        updateSortAndFilters();
    }

    @Override
    protected List<Integer> getTabList() { return mTabList; }

    public void setHint(int totalElements) {
        //Fix this up to dynamic
        //Also why is this not being used ?
        _binding.editText.setHint(totalElements != 0 ? "Search " + totalElements + " Hooks" : "Search");
    }

    @Override
    protected Fragment getFragment(int position) {
        switch (position) {
            case 0: return HooksFragment.newInstance(targetApplication, user);
            case 1: return LogsFragment.newInstance(targetApplication);
            case 2: return SettingsFragment.newInstance(targetApplication);
            default: throw new IllegalArgumentException();
        }
    }
}
