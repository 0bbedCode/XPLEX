package com.obbedcode.xplex.views.fragment.apps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.obbedcode.shared.PrefManager;
import com.obbedcode.shared.UiGlobals;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.BaseTablayoutViewpagerBinding;
import com.obbedcode.xplex.databinding.BottomDialogSearchFilterBinding;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.fragment.base.BasePagerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Pair;

public class AppsPagerFragment extends BasePagerFragment {
    private final List<Integer> mTabList = Arrays.asList(R.string.tab_user_apps, R.string.tab_configured_apps, R.string.tab_system_apps);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        _binding = BaseTablayoutViewpagerBinding.inflate(inflater, container, false);
        //setupFilterButton();
        createFilterButton();
        //Then FAB ?
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.refManager = PrefManager.create("AppsPagerFrag", PrefManager.ORDER_APPLICATION_NAME);

        super.sortByTitles = Arrays.asList(UiGlobals.SORT_APP_SIZE, UiGlobals.FILTER_LAST_UPDATE, UiGlobals.SORT_INSTALL_DATE, UiGlobals.SORT_TARGET_SDK);
        super.filterTitles = Arrays.asList(UiGlobals.FILTER_CONFIGURED, UiGlobals.FILTER_LAST_UPDATE, UiGlobals.FILTER_DISABLED);

        super.initEditText();
        super.initSearchButtons();
        //initFilterSheet();
        super.initFilterSheets();
    }

    @Override
    protected void handleChipClick(Chip chip, String title, boolean isSortBy) {
        if(!isSortBy && UiGlobals.FILTER_CONFIGURED.equalsIgnoreCase(title)) {
            Snackbar.make(filterBinding.getRoot(), "Error", Snackbar.LENGTH_SHORT).show();
            chip.setChecked(false);
            return;
        }
        super.handleChipClick(chip, title, isSortBy);
    }

    @Override
    protected void search(String text) {
        updateSortAndFilters();
    }

    public void setHint(int totalApps) {
        //搜索 = search
        //个应用 = applications
        _binding.editText.setHint(totalApps != 0 ? "Search " + totalApps + " Applications" : "Search");
    }

    @Override
    protected Fragment getFragment(int position) {
        switch (position) {
            case 0: return AppsFragment.newInstance(UiGlobals.TAB_USER);
            case 1: return AppsFragment.newInstance(UiGlobals.TAB_CONFIGURED);
            case 2: return AppsFragment.newInstance(UiGlobals.TAB_SYSTEM);
            default: throw new IllegalArgumentException();
        }
    }

    @Override
    protected List<Integer> getTabList() { return mTabList; }
}
