package com.obbedcode.xplex.views.activity.apps;

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
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.views.activity.app.AppActivity;
import com.obbedcode.xplex.views.etc.INavContainer;
import com.obbedcode.xplex.views.fragment.apps.AppsPagerFragment;

import java.util.ArrayList;
import java.util.List;

public class AppsActivityy extends AppActivity implements INavContainer {


    //private OnBackPressListener currentFragmentController;
    private ViewPager2 viewPager2;
    private BottomNavigationView bottomNavigationView;
    private HideBottomViewOnScrollBehavior<BottomNavigationView> hideBottomViewOnScrollBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apps_activity);
        setupViewPagerAndBottomNavigation();
    }

    /*@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AppsPagerFragment())
                    .commit();
        }
    }*/

    private void setupViewPagerAndBottomNavigation() {
        viewPager2 = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        hideBottomViewOnScrollBehavior = new HideBottomViewOnScrollBehavior<BottomNavigationView>();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new AppsPagerFragment());
        //fragments.add(new RequestFragment());
        //fragments.add(new HomeFragment());
        //fragments.add(new BlockListFragment());
        //fragments.add(new SettingsFragment());

        BottomFragmentStateAdapter adapter = new BottomFragmentStateAdapter(this, fragments);
        viewPager2.setAdapter(adapter);
        viewPager2.setUserInputEnabled(false);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.main_bottom_item_1)
                viewPager2.setCurrentItem(0);
            return true;
        });
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(hideBottomViewOnScrollBehavior);

        viewPager2.setOffscreenPageLimit(fragments.size());
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                updateCurrentFragmentController(position);
            }
        });
        viewPager2.setCurrentItem(0, false);

        //viewPager2.setCurrentItem(PrefManager.INSTANCE.getDefaultPage(), false);
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
