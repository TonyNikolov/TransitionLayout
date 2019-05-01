package com.futuristlabs.scrollingbehavior;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    TransitionLayout flHeader;
    FrameLayout flRoot;
    ScrollingRecyclerView rv;
    boolean firstHeaderLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        flHeader = findViewById(R.id.fl_activity_main_header);
        flRoot = findViewById(R.id.fl_activity_main_root);

        rv = findViewById(R.id.rv_activity_main);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MainActivityAdapter());
        rv.setOnScrollListener(new ScrollingRecyclerView.onScrollListener() {
            @Override
            public void onScrolled() {
                int verticalOffset = rv.computeVerticalScrollOffset();
                int headerMinHeight = flHeader.getMinimumHeight();
                int headerMaxHeight = flHeader.getMaxHeight();
                int currentHeaderHeight = Math.max((headerMaxHeight - verticalOffset), headerMinHeight);
                ViewGroup.LayoutParams headerParams = flHeader.getLayoutParams();
                headerParams.height = currentHeaderHeight;
                flHeader.setLayoutParams(headerParams);

                ViewCompat.setElevation(flHeader, currentHeaderHeight == headerMinHeight ? 8 : 0);

                Log.d("onScrolled", String.format("verticalScrollOffset: %d", verticalOffset));
                Log.d("onScrolled", String.format("percentageScrolled %.2f", flHeader.getTransitionProgress()));
                Log.d("onScrolled", String.format("headerCurrentHeight %d, headerMinHeight: %d, headerMaxHeight %d", flHeader.getHeight(), headerMinHeight, headerMaxHeight));

            }
        });

        flRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                flRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                Log.d("onGlobalLayout", String.format("header height: %d", flHeader.getHeight()));

                if (!firstHeaderLayout) {
                    firstHeaderLayout = true;
                    int flHeaderHeight = flHeader.getHeight();
                    rv.addItemDecoration(new TopPaddingItemDecorator(flHeaderHeight), 0);
                }

                View searchBar = findViewById(R.id.view_activity_search_bar);
                View iconRight = findViewById(R.id.view_activity_right_icon);

                TransitionLayout.LayoutParams searchBarParams = (TransitionLayout.LayoutParams) searchBar.getLayoutParams();
                TransitionLayout.LayoutParams iconRightParams = (TransitionLayout.LayoutParams) iconRight.getLayoutParams();

                int parentWidth = ((View) searchBar.getParent()).getWidth();
                searchBarParams.setWidthB(parentWidth - iconRightParams.getWidthB());
                flHeader.requestLayout();
            }
        });
    }
}
