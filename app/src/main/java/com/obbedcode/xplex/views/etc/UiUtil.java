package com.obbedcode.xplex.views.etc;

import android.content.Context;

import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.obbedcode.shared.logger.XLog;

public class UiUtil {
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static void setSpaceFooterView(RecyclerView recyclerView, FooterAdapter footerAdapter) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        //if (!(adapter instanceof ConcatAdapter)) {
        //    throw new IllegalStateException("RecyclerView adapter must be a ConcatAdapter");
        //}

        ConcatAdapter concatAdapter = (ConcatAdapter) adapter;
        boolean hasFooter = concatAdapter.getAdapters().contains(footerAdapter);
        int childCount = recyclerView.getChildCount();
        int itemCount = concatAdapter.getItemCount();

        //int lastVisibleItemPosition = 0;
        //if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
        //    lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        //}

        boolean should = hasFooter ? childCount >= itemCount - 1 : childCount >= itemCount;
        //XLog.i("ObbedCode.XP.UiUtil", "[uiui] Has Footer =" + hasFooter + " Child Count =" + childCount + " Item Count=" + itemCount + " Should=" + should + " Lst: " + lastVisibleItemPosition);

        //Spending 2+ hours on this we are force moving on now thank you
        //boolean should = !(hasFooter ? childCount >= itemCount - 1 : childCount >= itemCount);
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
