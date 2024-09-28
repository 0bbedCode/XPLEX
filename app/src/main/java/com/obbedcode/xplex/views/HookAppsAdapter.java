package com.obbedcode.xplex.views;

import android.annotation.SuppressLint;

import com.obbedcode.shared.IXPService;
import com.obbedcode.shared.data.XApp;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.service.ServiceClient;

import java.util.ArrayList;
import java.util.List;

import rikka.recyclerview.IdBasedRecyclerViewAdapter;

public class HookAppsAdapter  extends IdBasedRecyclerViewAdapter {
    private static final String TAG = "ObbedCode.XP.HookAppsAdapter";

    private IXPService service = ServiceClient.getService();

    public HookAppsAdapter() {
        super(new ArrayList<>());
        setHasStableIds(true);
        updateData();
    }

    //private static final int ID_IDENTITY_AND_TRACKING = 18;

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        clear();
        try {
            //List<XApp> apps = service.getInstalledAppsEx();



        }catch (Exception e) {

        }

        //addItem(HomeStatsViewHolder.CREATOR, null, ID_STATS);
        //addItem(HomeWelcomeViewHolder.CREATOR, mEvents, ID_WELCOME);
        //addItem(HomeCoreTestViewHolder.CREATOR, null, ID_CORE_TESTS);
        //addItem(HomeFeaturesViewHolder.CREATOR, null, ID_FEATURES);
        notifyDataSetChanged();
    }


    public void updateView(int dialogCode) {
        /*switch (dialogCode) {
            case GlobalDialogs.DIALOG_USERNAME:
                // Object obj = IdRecyclerViewUtils.getObjectFromId(this, ID_WELCOME);
                // if(obj instanceof HomeViewEvents) {
                //     HomeViewEvents hve = (HomeViewEvents) obj;
                //     hve.onWelcomeUpdate.update();
                // } //this works :D but may be slower given it needs enumeration can be slower versus direct access to what we need ?
                mEvents.onWelcomeUpdate.update();
                break;
        }*/
    }
}
