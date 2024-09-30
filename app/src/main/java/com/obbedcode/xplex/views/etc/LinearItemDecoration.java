package com.obbedcode.xplex.views.etc;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.obbedcode.shared.logger.XLog;

public class LinearItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public LinearItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.top = space;
    }
}