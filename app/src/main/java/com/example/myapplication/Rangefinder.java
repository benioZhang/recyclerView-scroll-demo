package com.example.myapplication;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Helper class for LayoutManager to calculate distance
 *
 * @see LinearSmoothScroller
 */
public class Rangefinder {
    private final RecyclerView.LayoutManager mLayoutManager;

    public Rangefinder(RecyclerView.LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }

    @Nullable
    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    /**
     * Calculates the vertical scroll amount necessary to make the given view fully visible
     * inside the RecyclerView.
     *
     * @param view           The view which we want to make fully visible
     * @param snapPreference The edge which the view should snap to when entering the visible
     *                       area. One of {@link SnapPreference#SNAP_TO_START}, {@link SnapPreference#SNAP_TO_END} or
     *                       {@link SnapPreference#SNAP_TO_ANY}.
     * @return The vertical scroll amount necessary to make the view visible with the given
     * snap preference.
     */
    public int calculateDyToMakeVisible(View view, int snapPreference) {
        final RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null || !layoutManager.canScrollVertically()) {
            return 0;
        }
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        final int top = layoutManager.getDecoratedTop(view) - params.topMargin;
        final int bottom = layoutManager.getDecoratedBottom(view) + params.bottomMargin;
        final int start = layoutManager.getPaddingTop();
        final int end = layoutManager.getHeight() - layoutManager.getPaddingBottom();
        return calculateDtToFit(top, bottom, start, end, snapPreference);
    }

    /**
     * Calculates the horizontal scroll amount necessary to make the given view fully visible
     * inside the RecyclerView.
     *
     * @param view           The view which we want to make fully visible
     * @param snapPreference The edge which the view should snap to when entering the visible
     *                       area. One of {@link SnapPreference#SNAP_TO_START}, {@link SnapPreference#SNAP_TO_END} or
     *                       {@link SnapPreference#SNAP_TO_END}
     * @return The vertical scroll amount necessary to make the view visible with the given
     * snap preference.
     */
    public int calculateDxToMakeVisible(View view, int snapPreference) {
        final RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager == null || !layoutManager.canScrollHorizontally()) {
            return 0;
        }
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        final int left = layoutManager.getDecoratedLeft(view) - params.leftMargin;
        final int right = layoutManager.getDecoratedRight(view) + params.rightMargin;
        final int start = layoutManager.getPaddingLeft();
        final int end = layoutManager.getWidth() - layoutManager.getPaddingRight();
        return calculateDtToFit(left, right, start, end, snapPreference);
    }

    /**
     * Helper method for {@link #calculateDxToMakeVisible(android.view.View, int)} and
     * {@link #calculateDyToMakeVisible(android.view.View, int)}
     */
    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd,
                                @SnapPreference int snapPreference) {
        switch (snapPreference) {
            case SnapPreference.SNAP_TO_START:
                return boxStart - viewStart;
            case SnapPreference.SNAP_TO_END:
                return boxEnd - viewEnd;
            case SnapPreference.SNAP_TO_ANY:
                final int dtStart = boxStart - viewStart;
                if (dtStart > 0) {
                    return dtStart;
                }
                final int dtEnd = boxEnd - viewEnd;
                if (dtEnd < 0) {
                    return dtEnd;
                }
                break;
            case SnapPreference.SNAP_TO_CENTER:
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }
        return 0;
    }
}
