package com.obbedcode.xplex.uiex;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class LeftAlignedLayoutManager extends FlexboxLayoutManager {
    private int itemsPerRow = -1;

    public LeftAlignedLayoutManager(Context context) {
        super(context);
        setFlexWrap(FlexWrap.WRAP);
        setJustifyContent(JustifyContent.FLEX_START);
        setAlignItems(AlignItems.FLEX_START);
    }

    @Override
    public void layoutDecoratedWithMargins(View view, int left, int top, int right, int bottom) {
        super.layoutDecoratedWithMargins(view, left, top, right, bottom);

        // Get the position of the item
        int position = getPosition(view);

        // Check if it's in the last row
        if (isInLastRow(position)) {
            // Align to the left
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            view.layout(lp.leftMargin, view.getTop(), right, bottom);
        }
    }

    private boolean isInLastRow(int position) {
        if (itemsPerRow == -1) {
            itemsPerRow = calculateItemsPerRow();
        }
        int itemCount = getItemCount();
        int lastRowStartPosition = itemCount - (itemCount % itemsPerRow);
        return position >= lastRowStartPosition;
    }

    private int calculateItemsPerRow() {
        if (getChildCount() == 0) return 1;

        View firstChild = getChildAt(0);
        int childWidth = firstChild.getWidth();
        int totalWidth = getWidth() - getPaddingLeft() - getPaddingRight();

        return Math.max(1, totalWidth / childWidth);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        itemsPerRow = -1; // Reset itemsPerRow
        super.onLayoutChildren(recycler, state);
    }
}