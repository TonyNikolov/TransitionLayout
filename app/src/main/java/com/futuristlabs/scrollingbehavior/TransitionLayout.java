package com.futuristlabs.scrollingbehavior;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

public class TransitionLayout extends FrameLayout {
    public static final int ANCHOR_IGNORE = -1;
    public static final int ANCHOR_HEIGHT = 0;
    public static final int DEFAULT_ANCHOR = ANCHOR_IGNORE;
    private OnLayoutListener listener;
    private int maxHeight;
    private int anchor;

    public TransitionLayout(@NonNull Context context) {
        super(context);

        init(context, null);
    }

    public TransitionLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public TransitionLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TransitionLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        maxHeight = Math.max(maxHeight, getMeasuredHeight());
        setMinimumHeight(Math.min(getMinimumHeight(), getMeasuredHeight()));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        prepare();
        super.onLayout(changed, left, top, right, bottom);

        if (listener != null) {
            listener.onLayout();
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return generateDefaultLayoutParams(); // TODO Change this?
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
//        prepare();
        super.setLayoutParams(params);
    }

    public void addOnLayoutListener(OnLayoutListener listener) {
        this.listener = listener;
    }

    private void init(@NonNull Context c, @Nullable AttributeSet attrs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(ViewOutlineProvider.BOUNDS);
        }


        final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.TransitionLayout);
        anchor = a.getInteger(R.styleable.TransitionLayout_anchor, DEFAULT_ANCHOR);

        a.recycle();

    }

    private void prepare() {
        if (getLayoutParams() == null) {
            return;
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }

            TransitionLayout.LayoutParams params = (LayoutParams) child.getLayoutParams();
            int heightA = params.getHeightA();
            int heightB = params.getHeightB();
            int widthA = params.getWidthA();
            int widthB = params.getWidthB();
            int marginBottomA = params.getMarginBottomA();
            int marginBottomB = params.getMarginBottomB();
            float alphaA = params.getAlphaA();
            float alphaB = params.getAlphaB();

            float baseProgress = getTransitionProgress();

            if (heightA != LayoutParams.NO_TRANSITION
                    && heightB != LayoutParams.NO_TRANSITION) {
                if (heightA == LayoutParams.MATCH_PARENT) {
                    heightA = getHeight();
                }

                params.height = calculateProgressForValues(heightA,heightB,baseProgress, params.getHeightTransitionFactor());

            }

            if (widthA != LayoutParams.NO_TRANSITION
                    && widthB != LayoutParams.NO_TRANSITION) {
                if (widthA == LayoutParams.MATCH_PARENT) {
                    widthA = getWidth();
                }

                params.width = calculateProgressForValues(widthA, widthB,baseProgress, params.getWidthTransitionFactor());
            }

            if (marginBottomA != LayoutParams.NO_TRANSITION
                    && marginBottomB != LayoutParams.NO_TRANSITION) {

                params.bottomMargin = calculateProgressForValues(marginBottomA,marginBottomB, baseProgress, 1F);
            }

            if (alphaA != LayoutParams.NO_TRANSITION
                    && alphaB != LayoutParams.NO_TRANSITION) {
                float currentAlpha = calculateProgressForvalues(alphaA, alphaB, baseProgress, params.getAlphaTransitionFactor());
                child.setAlpha(currentAlpha);
            }

//            if (child instanceof TransitionLayout) {
//                ((TransitionLayout) child).prepare();
//            }
        }
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public float getTransitionProgress() {
        switch (anchor) {
            case ANCHOR_HEIGHT: return getHeightTransitionProgress();
            default: return 1F;
        }
    }


    public float getHeightTransitionProgress() {
        ViewGroup.LayoutParams params = getLayoutParams();

        if (params instanceof TransitionLayout.LayoutParams) {
            int heightA = ((LayoutParams) params).getHeightA();
            int heightB = ((LayoutParams) params).getHeightB();
            int currentHeight = params.height;

            if (heightA > heightB) {
                float maxDifference =  Math.abs(heightA - heightB);
                float currentProgress = heightA -currentHeight;
                return currentProgress / maxDifference;
            } else {
                // increasing
                float maxDifference =  Math.abs(heightA - heightB);
                float currentProgress = currentHeight - heightA;
                return currentProgress / maxDifference;
            }
        } else {
            float minHeight = getMinimumHeight();
            float maxHeight = getMaxHeight();
            float currentHeight = params.height;

            return 1 - ((currentHeight - minHeight) / (maxHeight - minHeight));
        }
    }

    private int calculateProgressForValues(int valueA, int valueB, float baseProgress, float transitionFactor) {
        int currentValue;
        float factor = baseProgress * transitionFactor;

        if (valueA > valueB) {
            // decreasing
            currentValue = Math.max(valueA - (int) ((valueA - valueB) * factor), valueB);
        } else {
            // increasing
            currentValue = Math.min(valueA + (int) ((valueB - valueA) * factor), valueB);
        }

        return currentValue;
    }

    private float calculateProgressForvalues(float valueA, float valueB, float baseProgress, float transitionFactor) {
        float currentValue;
        float factor = baseProgress * transitionFactor;

        if (valueA > valueB) {
            // decreasing
            currentValue = Math.max(valueA - ((valueA - valueB) * factor), valueB);
        } else {
            // increasing
            currentValue = Math.min(valueA + ((valueB - valueA) * factor), valueB);
        }

        return currentValue;
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public static final int NO_TRANSITION = -3;
        int widthA;
        int widthB;
        int heightA;
        int heightB;
        int marginBottomA;
        int marginBottomB;
        float widthTransitionFactor;
        float heightTransitionFactor;
        float alphaTransitionFactor;
        float alphaA;
        float alphaB;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.TransitionLayout);
            widthA = a.getLayoutDimension(R.styleable.TransitionLayout_widthA, NO_TRANSITION);
            widthB = a.getDimensionPixelSize(R.styleable.TransitionLayout_widthB, NO_TRANSITION);
            heightA = a.getDimensionPixelSize(R.styleable.TransitionLayout_heightA, NO_TRANSITION);
            heightB = a.getDimensionPixelSize(R.styleable.TransitionLayout_heightB, NO_TRANSITION);
            marginBottomA = a.getDimensionPixelSize(R.styleable.TransitionLayout_marginBottomA, NO_TRANSITION);
            marginBottomB = a.getDimensionPixelSize(R.styleable.TransitionLayout_marginBottomB, NO_TRANSITION);
            widthTransitionFactor = a.getFloat(R.styleable.TransitionLayout_widthTransitionFactor, 1F);
            alphaTransitionFactor = a.getFloat(R.styleable.TransitionLayout_alphaTransitionFactor, 1F);
            heightTransitionFactor = a.getFloat(R.styleable.TransitionLayout_heightTransitionFactor, 1F);
            alphaA = a.getFloat(R.styleable.TransitionLayout_alphaA, NO_TRANSITION);
            alphaB = a.getFloat(R.styleable.TransitionLayout_alphaB, NO_TRANSITION);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams() {
            super(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        }

        public int getWidthA() {
            return widthA;
        }

        public void setWidthA(int widthA) {
            this.widthA = widthA;
        }

        public int getWidthB() {
            return widthB;
        }

        public void setWidthB(int widthB) {
            this.widthB = widthB;
        }

        public int getHeightA() {
            return heightA;
        }

        public void setHeightA(int heightA) {
            this.heightA = heightA;
        }

        public int getHeightB() {
            return heightB;
        }

        public void setHeightB(int heightB) {
            this.heightB = heightB;
        }

        public int getMarginBottomA() {
            return marginBottomA;
        }

        public void setMarginBottomA(int marginBottomA) {
            this.marginBottomA = marginBottomA;
        }

        public int getMarginBottomB() {
            return marginBottomB;
        }

        public void setMarginBottomB(int marginBottomB) {
            this.marginBottomB = marginBottomB;
        }

        public float getWidthTransitionFactor() {
            return widthTransitionFactor;
        }

        public void setWidthTransitionFactor(float widthTransitionFactor) {
            this.widthTransitionFactor = widthTransitionFactor;
        }

        public float getAlphaTransitionFactor() {
            return alphaTransitionFactor;
        }

        public void setAlphaTransitionFactor(float alphaTransitionFactor) {
            this.alphaTransitionFactor = alphaTransitionFactor;
        }

        public float getAlphaA() {
            return alphaA;
        }

        public void setAlphaA(float alphaA) {
            this.alphaA = alphaA;
        }

        public float getAlphaB() {
            return alphaB;
        }

        public void setAlphaB(float alphaB) {
            this.alphaB = alphaB;
        }

        public float getHeightTransitionFactor() {
            return heightTransitionFactor;
        }

        public void setHeightTransitionFactor(float heightTransitionFactor) {
            this.heightTransitionFactor = heightTransitionFactor;
        }
    }

    interface OnLayoutListener {
        void onLayout();
    }
}
