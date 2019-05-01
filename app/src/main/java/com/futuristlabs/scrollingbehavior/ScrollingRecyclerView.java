package com.futuristlabs.scrollingbehavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

public class ScrollingRecyclerView extends RecyclerView {
    private onScrollListener mListener;

    public ScrollingRecyclerView(@NonNull Context context) {
        super(context);

        init();
    }

    public ScrollingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ScrollingRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (mListener != null) {
            mListener.onScrolled();
        }
    }

    public void setOnScrollListener(onScrollListener listener) {
        this.mListener = listener;
    }

    interface onScrollListener {
        void onScrolled();
    }
}
