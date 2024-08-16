package com.obbedcode.xplex.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.obbedcode.xplex.R;

import rikka.recyclerview.BaseViewHolder;

public class HomeCoreTestViewHolder extends BaseViewHolder<Object> {
    public static final Creator<Object> CREATOR = new Creator<Object>() {
        @Override
        public BaseViewHolder<Object> createViewHolder(LayoutInflater inflater, ViewGroup parent) {
            View outerView = inflater.inflate(R.layout.main_card_container, parent, false);
            View innerView = inflater.inflate(R.layout.main_card_core_test, (ViewGroup) outerView, true);
            return new HomeCoreTestViewHolder(innerView);
        }
    };

    private final CoreTestAdapter adapter = new CoreTestAdapter();

    public HomeCoreTestViewHolder(View itemView) {
        super(itemView);
        //Do nothing for now
        RecyclerView rv = itemView.findViewById(R.id.rv_core_tests);
        rv.setAdapter(adapter);
    }
}
