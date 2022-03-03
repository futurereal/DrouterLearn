package com.viomi.waterpurifier.edison.ui.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @description:
 * @data:2021/11/22
 */
public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public RecyclerViewDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
     /*   outRect.right = space;
        outRect.bottom = space;*/

        // Add top margin only for the first item to avoid double space between items
        /*if (parent.getChildPosition(view) == 0)
            outRect.top = space;*/
    }
}