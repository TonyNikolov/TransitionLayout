package com.futuristlabs.scrollingbehavior;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TopPaddingItemDecorator extends RecyclerView.ItemDecoration {
    private int topPadding = 0;

    private TopPaddingItemDecorator() {

    }

    public TopPaddingItemDecorator(int topPadding) {
        this.topPadding = topPadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top += topPadding;
        }
    }

    public int getTopPadding() {
        return this.topPadding;
    }
}
