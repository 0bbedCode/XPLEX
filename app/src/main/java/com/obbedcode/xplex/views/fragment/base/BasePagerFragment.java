package com.obbedcode.xplex.views.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.obbedcode.shared.Str;
import com.obbedcode.shared.hook.repo.Int;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.databinding.BaseTablayoutViewpagerBinding;
import com.obbedcode.xplex.views.etc.IOnBackClickListener;
import com.obbedcode.xplex.views.etc.IOnClearClickListener;
import com.obbedcode.xplex.views.etc.IOnTabClickListener;
import com.obbedcode.xplex.views.etc.OnBackPressContainer;
import com.obbedcode.xplex.views.etc.OnClearContainer;
import com.obbedcode.xplex.views.etc.OnTabClickContainer;

import java.util.List;

public abstract class BasePagerFragment extends BaseFragment<BaseTablayoutViewpagerBinding> implements
        IOnBackClickListener,
        OnTabClickContainer,
        OnClearContainer {

    //protected BaseTablayoutViewpagerBinding binding;
    protected IOnTabClickListener tabController;
    protected IOnClearClickListener controller;

    private InputMethodManager inputManagerMethod;
    private String lastQuery = "";

    //@Nullable
    //@Override
    //public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    //    super.onViewCreated(void, savedInstanceState);
        //this.mView = inflater.inflate(R.layout.base_tablayout_viewpager, container, false);
        //this.binding = BaseTablayoutViewpagerBinding.inflate(inflater, container, false);
        //return binding.getRoot();
    //}

    protected abstract void search(String text);
    protected abstract Fragment getFragment(int position);
    protected abstract List<Integer> getTabList();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.inputManagerMethod = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        initView();
        initEditText();
        initButton();
    }

    private void initView() {
        binding.viewPager.setOffscreenPageLimit(getTabList().size());
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) { return getFragment(position); }

            @Override
            public int getItemCount() {  return getTabList().size(); }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> { tab.setText(getString(getTabList().get(position))); }).attach();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tabController != null) tabController.onReturnTop();
            }
        });
    }

    private void initEditText() {
        binding.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { setIconAndFocus(hasFocus ? R.drawable.ic_magnifier_to_back : R.drawable.ic_back_to_magnifier, hasFocus); }
        });
        binding.editText.addTextChangedListener(textWatcher);
    }

    private void initButton() {
        binding.searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editText.isFocused()) {
                    binding.editText.setText(Str.EMPTY);
                    setIconAndFocus(R.drawable.ic_back_to_magnifier, false);
                } else {
                    setIconAndFocus(R.drawable.ic_magnifier_to_back, true);
                }
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { binding.editText.setText(Str.EMPTY); }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String newQuery = s.toString().toLowerCase();
            if(!newQuery.equals(lastQuery)) {
                search(newQuery);
                lastQuery = newQuery;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            binding.clear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
        }
    };

    public void setIconAndFocus(int drawableId, boolean focus) {
        binding.searchIcon.setImageDrawable(requireContext().getDrawable(drawableId));
        if(binding.searchIcon.getDrawable() instanceof AnimatedVectorDrawable) {
            AnimatedVectorDrawable amv = (AnimatedVectorDrawable) binding.searchIcon.getDrawable();
            amv.start();
        }

        if(focus) {
            binding.editText.requestFocus();
            if(inputManagerMethod != null)
                inputManagerMethod.showSoftInput(binding.editText, 0);
        } else {
            binding.editText.clearFocus();
            if(inputManagerMethod != null)
                inputManagerMethod.hideSoftInputFromWindow(binding.editText.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        binding.editText.removeTextChangedListener(textWatcher);
        super.onDestroyView();
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
        if(binding.editText.isFocused()) {
            binding.editText.setText(Str.EMPTY);
            setIconAndFocus(R.drawable.ic_back_to_magnifier, false);
            return true;
        }

        return false;
    }
}
