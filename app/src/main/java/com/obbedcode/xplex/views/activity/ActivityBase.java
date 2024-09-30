package com.obbedcode.xplex.views.activity;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.views.activity.app.AppActivity;
import com.obbedcode.xplex.views.etc.INavContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityBase extends AppActivity implements INavContainer {
    private static final String TAG_BASE = "ObbedCode.XP.ActivityBase";

    protected ViewPager2 viewPager2;
    protected BottomNavigationView bottomNavigationView;
    protected HideBottomViewOnScrollBehavior<BottomNavigationView> hideBottomViewOnScrollBehavior;

    protected List<Fragment> bottomNavFragments;
    protected List<Integer> bottomNavIds;
    protected boolean showBottomNavBar = true;
    protected int defaultIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Now lets build more
        setContentView(R.layout.base_activity_two);
        init();
    }

    public void setBottomNavFragmentIds(int... ids) {
        this.bottomNavIds = new ArrayList<>(ids.length);
        for (int i : ids) {
            this.bottomNavIds.add(i);
        }
    }

    public void setBottomNavFragmentTypes(Class<?>... fragments) {
        bottomNavFragments = new ArrayList<>();
        for(Class<?> t : fragments) {
            try {
                Fragment f = (Fragment)t.newInstance();
                bottomNavFragments.add(f);
            }catch (Throwable e) {
                XLog.e(TAG_BASE, "Failed to Create Instance of Fragment: " + e);
            }
        }
    }

    private void init() {
        viewPager2 = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        hideBottomViewOnScrollBehavior = new HideBottomViewOnScrollBehavior<BottomNavigationView>();
        if(bottomNavFragments != null && !bottomNavFragments.isEmpty()) {
            BottomFragmentStateAdapter adapter = new BottomFragmentStateAdapter(this, bottomNavFragments);
            viewPager2.setAdapter(adapter);
            viewPager2.setUserInputEnabled(false);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                if(bottomNavIds != null) {
                    int index = bottomNavIds.indexOf(item.getItemId());
                    if(index != -1) {
                        viewPager2.setCurrentItem(index);
                    }
                }
                return true;
            });

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
            layoutParams.setBehavior(hideBottomViewOnScrollBehavior);

            viewPager2.setOffscreenPageLimit(bottomNavFragments.size());
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                    updateCurrentFragmentController(position);
                }
            });
            viewPager2.setCurrentItem(defaultIndex, false);
        }
    }

    private void updateCurrentFragmentController(int position) {
        //Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);
        //if (fragment instanceof OnBackPressListener) {
        //    currentFragmentController = (OnBackPressListener) fragment;
        //} else {
        //    currentFragmentController = null;
        //}
    }

    @Override
    protected void onApplyThemeResource(@NonNull Resources.Theme theme, int resourceId, boolean first) {
        super.onApplyThemeResource(theme, resourceId, first);
        theme.applyStyle(rikka.material.preference.R.style.ThemeOverlay_Rikka_Material3_Preference, true);
    }

    @Override
    public void showNavigation() {
        if (hideBottomViewOnScrollBehavior.isScrolledDown())
            hideBottomViewOnScrollBehavior.slideUp(bottomNavigationView);
    }

    @Override
    public void hideNavigation() {
        if (hideBottomViewOnScrollBehavior.isScrolledUp())
            hideBottomViewOnScrollBehavior.slideDown(bottomNavigationView);
    }

    static class BottomFragmentStateAdapter extends FragmentStateAdapter {

        private final List<Fragment> fragmentList;

        public BottomFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
            super(fragmentActivity);
            this.fragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }
}
