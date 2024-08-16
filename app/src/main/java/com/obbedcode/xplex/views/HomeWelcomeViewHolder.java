package com.obbedcode.xplex.views;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.obbedcode.shared.Str;
import com.obbedcode.shared.logger.XLog;
import com.obbedcode.shared.settings.LocalSettings;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.uiex.IUpdateViewHolder;
import com.obbedcode.xplex.uiex.UpdateViewHolder;

import rikka.recyclerview.BaseViewHolder;



public class HomeWelcomeViewHolder extends BaseViewHolder<HomeViewEvents> implements IUpdateViewHolder {
    private final TextView mWelcomeName;
    private final View mView;


    public static final Creator<HomeViewEvents> CREATOR = new Creator<HomeViewEvents>() {
        @Override
        public BaseViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            //Inflate "main_card_container.xml" as its the Card View BackGround
            //Inflate "main_card_welcome.xml" as its the actual view that will be placed or inflated onto the Card
            // Inflate "main_card_container.xml" as its the Card View BackGround
            View outerView = inflater.inflate(R.layout.main_card_container, parent, false);
            // Inflate "main_card_welcome.xml" as its the actual view that will be placed or inflated onto the Card
            View innerView = inflater.inflate(R.layout.main_card_welcome, (ViewGroup) outerView, true);
            // Return the new ViewHolder instance with the inflated layout
            return new HomeWelcomeViewHolder(innerView);
        }
    };

    public HomeWelcomeViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mWelcomeName = itemView.findViewById(R.id.tvWelcomeTitleName);
        update();
    }

    @Override
    public void onBind() {
        HomeViewEvents ev = getData();
        ev.onWelcomeUpdate = this;
        super.onBind();
    }

    @Override
    public void update() {
        StringBuilder sbText = new StringBuilder();
        sbText.append(getContext().getResources().getString(R.string.card_welcome_summary));
        sbText.append(Str.SPACE_CHAR);
        sbText.append(LocalSettings.getString(getContext(), "user_name"));
        mWelcomeName.setText(sbText.toString());
    }

    @Override
    public void update(Bundle data) { }
}
