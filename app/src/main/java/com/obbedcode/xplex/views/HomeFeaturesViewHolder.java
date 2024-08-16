package com.obbedcode.xplex.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.flexbox.FlexboxLayout;
import com.obbedcode.xplex.R;
import com.obbedcode.xplex.uiex.FeatureItemView;
//import com.wefika.flowlayout.FlowLayout;

import rikka.recyclerview.BaseViewHolder;

public class HomeFeaturesViewHolder  extends BaseViewHolder<Object> {
    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            View outerView = inflater.inflate(R.layout.main_card_container, parent, false);
            View innerView = inflater.inflate(R.layout.main_card_features, (ViewGroup) outerView, true); //set the view on a Card
            return new HomeFeaturesViewHolder(innerView);
        }
    };

    public HomeFeaturesViewHolder(View itemView) {
        super(itemView);

        FlexboxLayout flexboxLayout = itemView.findViewById(R.id.flexboxLayout);

        for (int i = 0; i < 10; i++) {
            FeatureItemView featureItemView = new FeatureItemView(itemView.getContext());
            featureItemView.setIcon(R.drawable.ic_remix_thumb_up_line);
            featureItemView.setText("Item " + (i + 1));

            featureItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle click
                }
            });

            featureItemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Handle long click
                    return true;
                }
            });


            flexboxLayout.addView(featureItemView);
        }
    }
}
