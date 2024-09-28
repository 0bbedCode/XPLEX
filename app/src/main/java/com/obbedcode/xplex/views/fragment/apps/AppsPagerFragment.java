package com.obbedcode.xplex.views.fragment.apps;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.obbedcode.shared.PrefManager;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.BaseTablayoutViewpagerBinding;
import com.obbedcode.xplex.databinding.BottomDialogSearchFilterBinding;
import com.obbedcode.xplex.views.etc.IOnBackClickListener;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.fragment.base.BasePagerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.Pair;

public class AppsPagerFragment extends BasePagerFragment {

    private final List<Integer> tabList = Arrays.asList(R.string.tab_user_apps, R.string.tab_configured_apps, R.string.tab_system_apps);


    private ImageButton filterButton;
    private BottomSheetDialog bottomSheet;
    private BottomDialogSearchFilterBinding filterBinding;
    //private IOnFab
    //private IOnClearClickListener onClearClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        binding = BaseTablayoutViewpagerBinding.inflate(inflater, container, false);
        setupFilterButton();
        //Then FAB ?
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFilterSheet();
    }

    private void setupFilterButton() {
        this.filterButton = new ImageButton(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        filterButton.setLayoutParams(params);

        int paddingHorizontal = dpToPx(16);
        filterButton.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        filterButton.setImageResource(R.drawable.ic_filter);

        TypedValue outValue = new TypedValue();
        //requireContext().getTheme().resolveAttribute()
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);

        filterButton.setBackgroundResource(outValue.resourceId);
        binding.searchContainer.addView(filterButton);
        //finish
    }

    private void initFilterSheet() {
        this.bottomSheet = new BottomSheetDialog(requireContext());
        this.filterBinding = BottomDialogSearchFilterBinding.inflate(getLayoutInflater(), null, false);
        if(bottomSheet != null) bottomSheet.setContentView(filterBinding.getRoot());
        setupToolBar();
        setupSwitchesAndChips();
        //setupSwitchesAndChips
    }

    private void setupToolBar() {
        filterBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheet != null) bottomSheet.dismiss();
            }
        });

        filterBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_reset) resetFilters();
                return false;
            }
        });
    }

    public void setupSwitchesAndChips() {
        filterBinding.reverseSwitch.setChecked(PrefManager.isReverse());
        filterBinding.reverseSwitch.setOnCheckedChangeListener((bv, isChecked) -> {
            PrefManager.isReverse(isChecked);
            updateSortAndFilters();
        });

        List<String> sortByTitles = Arrays.asList("App Size", "Last updated", "Installation Date", "Target SDK");
        List<String> filterTiles = Arrays.asList("Configured", "Last Update", "Disabled");

        setupChipGroup(filterBinding.sortBy, sortByTitles, true);
        setupChipGroup(filterBinding.filter, filterTiles, false);
    }


    private void setupChipGroup(ChipGroup chipGroup, List<String> titles, boolean isSortBy) {
        chipGroup.setSingleSelection(isSortBy);
        for (String title : titles) {
            Chip chip = new Chip(requireContext());
            chip.setText(title);
            chip.setCheckable(true);
            chip.setClickable(true);
            if (isSortBy) {
                chip.setChecked(title.equals(PrefManager.order()));
            } else {
                switch (title) {
                    case "Configured":
                        chip.setChecked(PrefManager.isConfigured());
                        break;
                    case "Last Update":
                        chip.setChecked(PrefManager.isUpdated());
                        break;
                    case "Disabled":
                        chip.setChecked(PrefManager.isDisabled());
                        break;
                }
            }
            chip.setOnClickListener(v -> handleChipClick(chip, title, isSortBy));
            chipGroup.addView(chip);
        }
    }


    private void handleChipClick(Chip chip, String title, boolean isSortBy) {

        if(!isSortBy && "Configured".equalsIgnoreCase(title)) {
            Snackbar.make(filterBinding.getRoot(), "Error", Snackbar.LENGTH_SHORT).show();
            chip.setChecked(false);
            return;
        }
        //if (!isSortBy && "已配置".equals(title) && !MainActivity.isModuleActivated()) {
        //    Snackbar.make(filerBinding.getRoot(), "模块尚未被激活", Snackbar.LENGTH_SHORT).show();
        //    chip.setChecked(false);
        //    return;
        //}

        if (isSortBy) {
            PrefManager.order(title);
        } else {
            switch (title) {
                case "Configured":
                    PrefManager.isConfigured(chip.isChecked());
                    break;
                case "Last Update":
                    PrefManager.isUpdated(chip.isChecked());
                    break;
                case "Disabled":
                    PrefManager.isDisabled(chip.isChecked());
                    break;
            }
        }

        String message = isSortBy ?
                getString(R.string.filter_sort_by_default) + ": " + title :
                title + " 已更新";
        Snackbar.make(filterBinding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        updateSortAndFilters();
    }


    private void updateSortAndFilters() {
        List<String> filters = new ArrayList<>();
        if (PrefManager.isConfigured())
            filters.add("Configured");  //已配置
        if(PrefManager.isUpdated())
            filters.add("Last Update"); //最近更新
        if(PrefManager.isDisabled())
            filters.add("Disabled");    //已禁用
        if(controller != null)
            controller.updateSortedList(new Pair<>(PrefManager.order(), filters), binding.editText.getText().toString(), PrefManager.isReverse());
    }

    private void resetFilters() {
        filterBinding.sortBy.check(filterBinding.sortBy.getChildAt(0).getId());
        filterBinding.filter.clearCheck();
        filterBinding.reverseSwitch.setChecked(false);
        PrefManager.isReverse(false);
        PrefManager.order(PrefManager.ORDER_APPLICATION_NAME);  //应用名称
        PrefManager.isConfigured(false);
        PrefManager.isUpdated(false);
        PrefManager.isDisabled(false);
        updateSortAndFilters();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    //@Override
    //public void setBackController(IOnBackClickListener backController) {
    //
    //}

    //@Override
    //public IOnBackClickListener getBackController() {
    //    return null;
    //}

    @Override
    protected void search(String text) {
        updateSortAndFilters();
    }

    public void setHint(int totalApps) {
        //搜索 = search
        //个应用 = applications
        binding.editText.setHint(totalApps != 0 ? "Search " + totalApps + " Applications" : "Search");
    }

    @Override
    protected Fragment getFragment(int position) {
        switch (position) {
            case 0: return AppsFragment.newInstance("user");
            case 1: return AppsFragment.newInstance("configured");
            case 2: return AppsFragment.newInstance("system");
            default: throw new IllegalArgumentException();
        }
    }

    @Override
    protected List<Integer> getTabList() {
        return tabList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(bottomSheet != null)
            bottomSheet.dismiss();
        bottomSheet = null;
        //fabController = null;
    }

    @Override
    public void setOnClearClickListener(IOnClearClickListener listener) {

    }

    @Override
    public IOnClearClickListener getOnClearClickListener() {
        return null;
    }

    @Override
    public void setTabController(IOnTabClickListener tabController) {
        this.tabController = tabController;
    }

    @Override
    public IOnTabClickListener getTabController() {
        return this.tabController;
    }
}
