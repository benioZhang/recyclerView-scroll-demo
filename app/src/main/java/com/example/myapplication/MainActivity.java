package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private RadioGroup mScrollerGroup;
    private RadioGroup mPreferenceGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        SimpleAdapter adapter = new SimpleAdapter(500);
        recyclerView.setAdapter(adapter);

        mEditText = findViewById(R.id.edit_text);
        mEditText.setText("100");

        mScrollerGroup = findViewById(R.id.radio_group_scroller);
        mScrollerGroup.check(R.id.radio_button_smooth);

        mPreferenceGroup = findViewById(R.id.radio_group_align);
        mPreferenceGroup.check(R.id.radio_button_any);

        findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onJump();
                testJump();
            }
        });
    }

    private void onJump() {
        boolean useSmoothScroller = true;
        if (mScrollerGroup.getCheckedRadioButtonId() == R.id.radio_button_immediate) {
            useSmoothScroller = false;
        }

        int preference = SnapPreference.SNAP_TO_ANY;
        int preferenceId = mPreferenceGroup.getCheckedRadioButtonId();
        if (preferenceId == R.id.radio_button_start) {
            preference = SnapPreference.SNAP_TO_START;
        } else if (preferenceId == R.id.radio_button_end) {
            preference = SnapPreference.SNAP_TO_END;
        } else if (preferenceId == R.id.radio_button_center) {
            preference = SnapPreference.SNAP_TO_CENTER;
        }

        int position = Integer.parseInt(mEditText.getText().toString());

        IndexScroller indexScroller = useSmoothScroller
                ? new SmoothIndexScroller(mRecyclerView)
                : new ImmediateIndexScroller(mRecyclerView);
        indexScroller.scrollToPosition(position, preference);
    }

    private void testJump() {
        boolean useSmoothScroller = true;
        if (mScrollerGroup.getCheckedRadioButtonId() == R.id.radio_button_immediate) {
            useSmoothScroller = false;
        }

        int preference = SnapPreference.SNAP_TO_ANY;
        int preferenceId = mPreferenceGroup.getCheckedRadioButtonId();
        if (preferenceId == R.id.radio_button_start) {
            preference = SnapPreference.SNAP_TO_START;
        } else if (preferenceId == R.id.radio_button_end) {
            preference = SnapPreference.SNAP_TO_END;
        } else if (preferenceId == R.id.radio_button_center) {
            preference = SnapPreference.SNAP_TO_CENTER;
        }

        int position = Integer.parseInt(mEditText.getText().toString());

        if (useSmoothScroller) {
            testSmoothScroll(position, preference);
        } else {
            testScroll(position, preference);
        }
    }

    private void testScroll(int targetPosition, int preference) {
        RecyclerView recyclerView = mRecyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        recyclerView.scrollToPosition(targetPosition);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                View targetView = layoutManager.findViewByPosition(targetPosition);
                if (targetView != null) {
                    Rangefinder rangefinder = new Rangefinder(layoutManager);
                    final int dx = rangefinder.calculateDxToMakeVisible(targetView, preference);
                    final int dy = rangefinder.calculateDyToMakeVisible(targetView, preference);
                    if (dx != 0 || dy != 0) {
                        recyclerView.scrollBy(-dx, -dy);
                    }
                }
            }
        });
    }

    private void testSmoothScroll(int targetPosition, int preference) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected int getHorizontalSnapPreference() {
                return preference;
            }

            @Override
            protected int getVerticalSnapPreference() {
                return preference;
            }

            @Override
            public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                if (snapPreference == SnapPreference.SNAP_TO_CENTER) {
                    return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
                }
                return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
            }
        };
        smoothScroller.setTargetPosition(targetPosition);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    public static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
        private final int mCount;

        public SimpleAdapter(int count) {
            mCount = count;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.create(parent.getContext());
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.mTextView.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return mCount;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mTextView;

            public ViewHolder(@NonNull TextView itemView) {
                super(itemView);
                mTextView = itemView;
            }

            public static ViewHolder create(Context context) {
                TextView textView = new TextView(context);
                textView.setGravity(Gravity.CENTER);
                int dp40 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                        context.getResources().getDisplayMetrics());
                textView.setLayoutParams(new RecyclerView.LayoutParams(dp40, RecyclerView.LayoutParams.MATCH_PARENT));
                return new ViewHolder(textView);
            }
        }
    }
}