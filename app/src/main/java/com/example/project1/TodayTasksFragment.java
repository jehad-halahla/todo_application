package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * TodayTasksFragment displays tasks due today.
 */
public class TodayTasksFragment extends BaseTaskFragment {

    public TodayTasksFragment() {
        // Required empty public constructor
    }

    public static TodayTasksFragment newInstance() {
        return new TodayTasksFragment();
    }

    @Override
    protected String getFilterType() {
        return "Today";
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_today_tasks; // Ensure this layout exists
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.recycler_view_today_tasks; // Ensure this ID matches your layout
    }

    @Override
    protected boolean isGrouped() {
        return false;
    }

    /**
     * Optionally, override onEditClick or onDeleteClick if specific behavior is needed.
     */

}
