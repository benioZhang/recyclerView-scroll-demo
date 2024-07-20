package com.example.myapplication;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class ImmediateIndexScroller implements IndexScroller {
    private final RecyclerView mRecyclerView;

    public ImmediateIndexScroller(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
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
        ImmediateScroller scroller = createScroller(recyclerView, snapPreference);
        scroller.start(position);
    }

    @NonNull
    protected ImmediateScroller createScroller(@NonNull RecyclerView recyclerView, @SnapPreference int snapPreference) {
        return new ImmediateScroller(recyclerView, snapPreference);
    }

    public static class ImmediateScroller {
        public final RecyclerView recyclerView;
        private final int mVerticalSnapPreference;
        private final int mHorizontalSnapPreference;
        private boolean mRunning;
        private int mTargetPosition = RecyclerView.NO_POSITION;
        private Runnable mScrollRunnable;

        public ImmediateScroller(@NonNull RecyclerView recyclerView, @SnapPreference int snapPreference) {
            this(recyclerView, snapPreference, snapPreference);
        }

        public ImmediateScroller(@NonNull RecyclerView recyclerView, @SnapPreference int verticalSnapPreference, @SnapPreference int horizontalSnapPreference) {
            this.recyclerView = recyclerView;
            mVerticalSnapPreference = verticalSnapPreference;
            mHorizontalSnapPreference = horizontalSnapPreference;
        }

        public void start(int position) {
            ImmediateScroller scroller = getScroller();
            if (scroller != null) {// stop previous scroller
                scroller.stop();
            }
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            mRunning = true;
            mTargetPosition = position;
            setScroller(this);
            recyclerView.scrollToPosition(position);
            recyclerView.post(mScrollRunnable = new Runnable() {
                @Override
                public void run() {
                    mScrollRunnable = null;
                    if (mRunning && mTargetPosition != RecyclerView.NO_POSITION) {
                        View targetView = findViewByPosition(mTargetPosition);
                        if (targetView != null) {
                            onTargetFound(targetView);
                        }
                    }
                    stop();
                }
            });
        }

        private int getTagKey() {
            return Objects.hash(recyclerView, ImmediateScroller.class);
        }

        private ImmediateScroller getScroller() {
            Object tag = recyclerView.getTag(getTagKey());
            return tag instanceof ImmediateScroller ? (ImmediateScroller) tag : null;
        }

        private void setScroller(ImmediateScroller scroller) {
            recyclerView.setTag(getTagKey(), scroller);
        }

        public void stop() {
            if (!mRunning) {
                return;
            }
            mRunning = false;
            mTargetPosition = RecyclerView.NO_POSITION;
            setScroller(null);
            if (mScrollRunnable != null) {
                recyclerView.removeCallbacks(mScrollRunnable);
                mScrollRunnable = null;
            }
        }

        protected View findViewByPosition(int position) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            return layoutManager != null ? layoutManager.findViewByPosition(position) : null;
        }

        protected void onTargetFound(@NonNull View targetView) {
            Rangefinder rangefinder = new Rangefinder(recyclerView.getLayoutManager());
            final int dx = rangefinder.calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
            final int dy = rangefinder.calculateDyToMakeVisible(targetView, getVerticalSnapPreference());
            if (dx != 0 || dy != 0) {
                recyclerView.scrollBy(-dx, -dy);
            }
        }

        public int getHorizontalSnapPreference() {
            return mHorizontalSnapPreference;
        }

        public int getVerticalSnapPreference() {
            return mVerticalSnapPreference;
        }
    }
}
