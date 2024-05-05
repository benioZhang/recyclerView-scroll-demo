package com.example.myapplication;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImmediateIndexScroller implements IndexScroller {
    private final RecyclerView mRecyclerView;
    private final int mSnapPreference;
    private int mTargetPosition = RecyclerView.NO_POSITION;
    private ScrollRunnable mScrollRunnable;

    public ImmediateIndexScroller(@NonNull RecyclerView recyclerView) {
        this(recyclerView, SnapPreference.SNAP_TO_ANY);
    }

    public ImmediateIndexScroller(@NonNull RecyclerView recyclerView, @SnapPreference int snapPreference) {
        mRecyclerView = recyclerView;
        mSnapPreference = snapPreference;
    }

    @Override
    public void scrollToPosition(int position) {
        RecyclerView recyclerView = mRecyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null || position < 0) {
            return;
        }
        mTargetPosition = position;
        recyclerView.scrollToPosition(position);
        if (mScrollRunnable == null) {
            mScrollRunnable = new ScrollRunnable();
        }
        mScrollRunnable.start();
    }

    @NonNull
    protected Rangefinder createRangefinder(RecyclerView.LayoutManager layoutManager) {
        return new Rangefinder(layoutManager);
    }

    protected View findViewByPosition(int position) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        return layoutManager != null ? layoutManager.findViewByPosition(position) : null;
    }

    protected void onTargetFound(@NonNull View targetView) {
        Rangefinder rangefinder = createRangefinder(mRecyclerView.getLayoutManager());
        final int dx = rangefinder.calculateDxToMakeVisible(targetView, mSnapPreference);
        final int dy = rangefinder.calculateDyToMakeVisible(targetView, mSnapPreference);
        if (dx != 0 || dy != 0) {
            mRecyclerView.scrollBy(-dx, -dy);
        }
        mTargetPosition = RecyclerView.NO_POSITION;
    }

    private class ScrollRunnable implements Runnable {
        private boolean mStarted;

        @Override
        public void run() {
            if (mStarted) {
                mStarted = false;
                View targetView = findViewByPosition(mTargetPosition);
                if (targetView != null) {
                    onTargetFound(targetView);
                }
            }
        }

        void start() {
            stop();
            mStarted = true;
            mRecyclerView.post(this);
        }

        void stop() {
            if (mStarted) {
                mRecyclerView.removeCallbacks(this);
                mStarted = false;
            }
        }
    }
}
