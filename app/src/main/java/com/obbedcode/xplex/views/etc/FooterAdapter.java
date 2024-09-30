package com.obbedcode.xplex.views.etc;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FooterAdapter extends RecyclerView.Adapter<FooterAdapter.FooterViewHolder> {
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View view) {
            super(view);
        }
    }

    @NonNull
    @Override
    public FooterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Space view = new Space(parent.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtil.dpToPx(parent.getContext(), 96)));
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FooterViewHolder holder, int position) { }

    @Override
    public int getItemCount() {
        return 1;
    }
}