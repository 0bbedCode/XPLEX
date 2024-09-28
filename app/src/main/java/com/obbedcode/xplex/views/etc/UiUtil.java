package com.obbedcode.xplex.views.etc;

import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class UiUtil {
    public static void setSpaceFooterView(RecyclerView recyclerView, FooterAdapter footerAdapter) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (!(adapter instanceof ConcatAdapter)) {
            throw new IllegalStateException("RecyclerView adapter must be a ConcatAdapter");
        }

        ConcatAdapter concatAdapter = (ConcatAdapter) adapter;
        boolean hasFooter = concatAdapter.getAdapters().contains(footerAdapter);
        int childCount = recyclerView.getChildCount();
        int itemCount = concatAdapter.getItemCount();

        boolean should;
        if (hasFooter) {
            should = childCount >= itemCount - 1;
        } else {
            should = childCount >= itemCount;
        }

        if (should) {
            if (!hasFooter && childCount != 0) {
                concatAdapter.addAdapter(footerAdapter);
            }
        } else {
            if (hasFooter) {
                concatAdapter.removeAdapter(footerAdapter);
            }
        }
    }
}
