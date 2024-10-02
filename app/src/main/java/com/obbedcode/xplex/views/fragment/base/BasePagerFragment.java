package com.obbedcode.xplex.views.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.obbedcode.shared.PrefManager;
import com.obbedcode.shared.Str;
import com.obbedcode.shared.UiGlobals;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.BaseTablayoutViewpagerBinding;
import com.obbedcode.xplex.databinding.BottomDialogSearchFilterBinding;
import com.obbedcode.xplex.views.etc.IOnBackClickListener;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.etc.OnBackPressContainer;
import com.obbedcode.xplex.views.etc.OnClearContainer;
import com.obbedcode.xplex.views.etc.OnTabClickContainer;
import com.obbedcode.xplex.views.etc.UiUtil;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

public abstract class BasePagerFragment extends BaseFragment<BaseTablayoutViewpagerBinding> implements
        IOnBackClickListener,
        OnTabClickContainer,
        OnClearContainer {

    protected IOnTabClickListener tabController;
    protected IOnClearClickListener controller;

    protected ImageButton filterButton;
    protected BottomSheetDialog filterBottomSheet;
    protected BottomDialogSearchFilterBinding filterBinding;

    protected List<String> sortByTitles;
    protected List<String> filterTitles;

    private InputMethodManager mInputManagerMethod;
    private String mLastQuery = "";

    protected abstract void search(String text);
    protected abstract Fragment getFragment(int position);
    protected abstract List<Integer> getTabList();
    public abstract void setHint(int count);

    protected PrefManager refManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mInputManagerMethod = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        //initEditText();
        //initButton();
    }

    protected void initFilterSheets() {
        //Sheets Init
        filterBottomSheet = new BottomSheetDialog(requireContext());
        filterBinding = BottomDialogSearchFilterBinding.inflate(getLayoutInflater(), null, false);
        if(filterBottomSheet != null)
            filterBottomSheet.setContentView(filterBinding.getRoot());


        //Tool Bar Init
        filterBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(filterBottomSheet != null) filterBottomSheet.dismiss();
            }
        });

        filterBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_reset) resetFilters();
                return false;
            }
        });

        //Switches and Chips
        filterBinding.reverseSwitch.setChecked(refManager.isReverseEx());
        filterBinding.reverseSwitch.setOnCheckedChangeListener((bv, isChecked) -> {
            refManager.isReverseEx(isChecked);
            updateSortAndFilters();
        });

        if(sortByTitles != null && !sortByTitles.isEmpty())
            setupChipGroup(filterBinding.sortBy, sortByTitles, true);

        if(filterTitles != null && !filterTitles.isEmpty())
            setupChipGroup(filterBinding.filter, filterTitles, false);

    }

    protected void handleChipClick(Chip chip, String title, boolean isSortBy) {
        String cleanTitle = title.replaceAll(" ", "_");
        if(isSortBy) {
            refManager.orderEx(cleanTitle);
        } else {
            refManager.isEnabled(cleanTitle, chip.isChecked());
        }

        String message = isSortBy ?
                getString(R.string.filter_sort_by_default) + ": " + title :
                title + " Updated";
        Snackbar.make(filterBinding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
        updateSortAndFilters();
    }

    protected void updateSortAndFilters() {
        List<String> filters = new ArrayList<>();
        for (String filter : filterTitles) {
            String set = "is_" + filter.replaceAll(" ", "_");
            if(refManager.isEnabled(set))
                filters.add(filter);
        }

        if(controller != null)
            controller.updateSortedList(new Pair<>(refManager.orderEx(), filters), _binding.editText.getText().toString(), refManager.isReverseEx());
    }

    protected void resetFilters() {
        if(filterBinding != null) {
            filterBinding.sortBy.check(filterBinding.sortBy.getChildAt(0).getId());
            filterBinding.filter.clearCheck();
            filterBinding.reverseSwitch.setChecked(false);

            refManager.isReverseEx(false);

            refManager.orderEx(refManager.defaultOrder);
            if(filterTitles != null) {
                for (String filterTitle : filterTitles) {
                    refManager.isEnabled(filterTitle.replaceAll(" ", "_"), false);
                }
            }

            updateSortAndFilters();
        }
    }

    private void setupChipGroup(ChipGroup chipGroup, List<String> titles, boolean isSortBy) {
        chipGroup.setSingleSelection(isSortBy);
        for (String title : titles) {
            Chip chip = new Chip(requireContext());
            chip.setText(title);
            chip.setCheckable(true);
            chip.setClickable(true);

            String titleClean = title.replaceAll(" ", "_");
            if (isSortBy) {
                String r = refManager.id + "_" + titleClean;
                chip.setChecked(r.equals(refManager.orderEx()));
            } else {
                chip.setChecked(refManager.isEnabled(titleClean));
            }
            chip.setOnClickListener(v -> handleChipClick(chip, title, isSortBy));
            chipGroup.addView(chip);
        }
    }

    protected void initEditText() {
        _binding.searchContainer.setVisibility(View.VISIBLE);
        _binding.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { setIconAndFocus(hasFocus ? R.drawable.ic_magnifier_to_back : R.drawable.ic_back_to_magnifier, hasFocus); }
        });
        _binding.editText.addTextChangedListener(textWatcher);
    }

    protected void initSearchButtons() {
        if(filterButton != null) {
            filterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(filterBottomSheet != null) {
                        filterBottomSheet.show();
                    }
                }
            });
        }

        _binding.searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_binding.editText.isFocused()) {
                    _binding.editText.setText(Str.EMPTY);
                    setIconAndFocus(R.drawable.ic_back_to_magnifier, false);
                } else {
                    setIconAndFocus(R.drawable.ic_magnifier_to_back, true);
                }
            }
        });

        _binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { _binding.editText.setText(Str.EMPTY); }
        });
    }

    protected void createFilterButton() {
        _binding.searchContainer.setVisibility(View.VISIBLE);

        filterButton = new ImageButton(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        filterButton.setLayoutParams(params);

        int paddingHorizontal = UiUtil.dpToPx(requireContext(), 16);
        filterButton.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        filterButton.setImageResource(R.drawable.ic_filter);

        TypedValue outValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);

        filterButton.setBackgroundResource(outValue.resourceId);
        _binding.searchContainer.addView(filterButton);
    }

    /*private void initEditText() {
        _binding.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { setIconAndFocus(hasFocus ? R.drawable.ic_magnifier_to_back : R.drawable.ic_back_to_magnifier, hasFocus); }
        });
        _binding.editText.addTextChangedListener(textWatcher);
    }*/

    public void setIconAndFocus(int drawableId, boolean focus) {
        _binding.searchIcon.setImageDrawable(requireContext().getDrawable(drawableId));
        if(_binding.searchIcon.getDrawable() instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable amv = (AnimatedVectorDrawable) _binding.searchIcon.getDrawable();
            amv.start();
        }

        if(focus) {
            _binding.editText.requestFocus();
            if(mInputManagerMethod != null) mInputManagerMethod.showSoftInput(_binding.editText, 0);
        } else {
            _binding.editText.clearFocus();
            if(mInputManagerMethod != null) mInputManagerMethod.hideSoftInputFromWindow(_binding.editText.getWindowToken(), 0);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void afterTextChanged(Editable s) { _binding.clear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE); }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newQuery = s.toString().toLowerCase();
            if(!newQuery.equals(mLastQuery)) {
                search(newQuery);
                mLastQuery = newQuery;
            }
        }
    };

    private void initView() {
        _binding.viewPager.setOffscreenPageLimit(getTabList().size());
        _binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return getFragment(position);
            }
            @Override
            public int getItemCount() {  return getTabList().size(); }
        });

        /*binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Fragment fragment = getFragment(position);

                XLog.i("ObbedCode.XP.BasePagerFragment", "[onPageChange]");

                if (fragment instanceof AppsFragment) {
                    XLog.i("ObbedCode.XP.BasePagerFragment", "[onPageChange] Refreshing..");
                    ((AppsFragment) fragment).refreshApps();
                }
            }
        });*/

        new TabLayoutMediator(_binding.tabLayout, _binding.viewPager, (tab, position) -> { tab.setText(getString(getTabList().get(position))); }).attach();
        _binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {  }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {  }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { if(tabController != null) tabController.onReturnTop(); }
        });
    }

    @Override
    public void onDestroyView() {
        _binding.editText.removeTextChangedListener(textWatcher);
        super.onDestroyView();
        if(filterBottomSheet != null) {
            filterBottomSheet.dismiss();
            filterBottomSheet = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Activity ac = getActivity();
        if(ac instanceof OnBackPressContainer) {
            OnBackPressContainer con = (OnBackPressContainer) ac;
            con.setBackController(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity ac = getActivity();
        if(ac instanceof OnBackPressContainer) {
            OnBackPressContainer con = (OnBackPressContainer) ac;
            con.setBackController(this);
        }
    }

    @Override
    public boolean onBackPressed() {
        if(!_binding.editText.isFocused()) return false;
        _binding.editText.setText(Str.EMPTY);
        setIconAndFocus(R.drawable.ic_back_to_magnifier, false);
        return true;
    }

    @Override
    public void setOnClearClickListener(IOnClearClickListener listener) { this.controller = listener; }

    @Override
    public IOnClearClickListener getOnClearClickListener() { return this.controller; }

    @Override
    public void setTabController(IOnTabClickListener tabController) { this.tabController = tabController; }

    @Override
    public IOnTabClickListener getTabController() { return this.tabController; }
}
