package com.obbedcode.xplex.views;

import android.annotation.SuppressLint;

import com.obbedcode.shared.logger.XLog;

import java.util.ArrayList;

import rikka.recyclerview.IdBasedRecyclerViewAdapter;

public class HomeAdapter extends IdBasedRecyclerViewAdapter  {
    private static final String TAG = "ObbedCode.XP.HomeAdapter";
    public HomeAdapter() {
        super(new ArrayList<>());
        setHasStableIds(true);
        updateData();
    }

    private static final int ID_WELCOME = 18;
    private static final int ID_STATS = 13;
    private static final int ID_CORE_TESTS = 14;
    private static final int ID_FEATURES = 15;

    private final HomeViewEvents mEvents = new HomeViewEvents();

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        clear();
        addItem(HomeStatsViewHolder.CREATOR, null, ID_STATS);
        addItem(HomeWelcomeViewHolder.CREATOR, mEvents, ID_WELCOME);
        addItem(HomeCoreTestViewHolder.CREATOR, null, ID_CORE_TESTS);
        addItem(HomeFeaturesViewHolder.CREATOR, null, ID_FEATURES);
        notifyDataSetChanged();
    }


    public void updateView(int dialogCode) {
        switch (dialogCode) {
            case GlobalDialogs.DIALOG_USERNAME:
               // Object obj = IdRecyclerViewUtils.getObjectFromId(this, ID_WELCOME);
               // if(obj instanceof HomeViewEvents) {
               //     HomeViewEvents hve = (HomeViewEvents) obj;
               //     hve.onWelcomeUpdate.update();
               // } //this works :D but may be slower given it needs enumeration can be slower versus direct access to what we need ?
                mEvents.onWelcomeUpdate.update();
                break;
        }
    }
}
