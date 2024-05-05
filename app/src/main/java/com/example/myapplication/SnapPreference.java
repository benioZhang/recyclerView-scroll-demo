package com.example.myapplication;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.LinearSmoothScroller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef(value = {
        SnapPreference.SNAP_TO_START,
        SnapPreference.SNAP_TO_ANY,
        SnapPreference.SNAP_TO_END,
        SnapPreference.SNAP_TO_CENTER
})
public @interface SnapPreference {
    /**
     * Align child view's left or top with parent view's left or top
     */
    int SNAP_TO_START = LinearSmoothScroller.SNAP_TO_START;

    /**
     * <p>Decides if the child should be snapped from start or end, depending on where it
     * currently is in relation to its parent.</p>
     * <p>For instance, if the view is virtually on the left of RecyclerView, using
     * {@code SNAP_TO_ANY} is the same as using {@code SNAP_TO_START}</p>
     */
    int SNAP_TO_ANY = LinearSmoothScroller.SNAP_TO_ANY;

    /**
     * Align child view's right or bottom with parent view's right or bottom
     */
    int SNAP_TO_END = LinearSmoothScroller.SNAP_TO_END;

    /**
     * Align child view's center with parent view's center
     */
    int SNAP_TO_CENTER = 2;
}
