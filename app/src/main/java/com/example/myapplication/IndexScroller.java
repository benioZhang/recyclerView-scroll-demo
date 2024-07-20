package com.example.myapplication;

public interface IndexScroller {
    void scrollToPosition(int position);

    void scrollToPosition(int position, @SnapPreference int snapPreference);
}