package com.example.myapplication;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class SmoothIndexScroller implements IndexScroller {
    private final RecyclerView mRecyclerView;

    public SmoothIndexScroller(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @NonNull
    protected RecyclerView.SmoothScroller createScroller(@NonNull RecyclerView recyclerView, @SnapPreference int snapPreference) {
        return new SmoothScroller(recyclerView.getContext(), snapPreference);
    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPosition(position, SnapPreference.SNAP_TO_ANY);
    }

    @Override
    public void scrollToPosition(int position, @SnapPreference int snapPreference) {
        RecyclerView recyclerView = mRecyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null || position < 0) {
            return;
        }
        RecyclerView.SmoothScroller scroller = createScroller(recyclerView, snapPreference);
        scroller.setTargetPosition(position);
        layoutManager.startSmoothScroll(scroller);
    }

    public static class SmoothScroller extends LinearSmoothScroller {
        private final int mVerticalSnapPreference;
        private final int mHorizontalSnapPreference;

        public SmoothScroller(Context context, @SnapPreference int snapPreference) {
            this(context, snapPreference, snapPreference);
        }

        public SmoothScroller(Context context, @SnapPreference int verticalSnapPreference, @SnapPreference int horizontalSnapPreference) {
            super(context);
            mVerticalSnapPreference = verticalSnapPreference;
            mHorizontalSnapPreference = horizontalSnapPreference;
        }

        @Override
        protected int getVerticalSnapPreference() {
            return mVerticalSnapPreference;
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return mHorizontalSnapPreference;
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            if (snapPreference == SnapPreference.SNAP_TO_CENTER) {
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
            }
            return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
        }
    }
}
