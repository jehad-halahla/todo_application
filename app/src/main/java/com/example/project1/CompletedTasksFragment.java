package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * CompletedTasksFragment displays completed tasks.
 */
public class CompletedTasksFragment extends BaseTaskFragment {

    public CompletedTasksFragment() {
        // Required empty public constructor
    }

    public static CompletedTasksFragment newInstance() {
        return new CompletedTasksFragment();
    }

    @Override
    protected String getFilterType() {
        return "Completed";
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_completed_tasks; // Ensure this layout exists
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.recycler_view_completed_tasks; // Ensure this ID matches your layout
    }

    @Override
    protected boolean isGrouped() {
        return true;
    }

    /**
     * Optionally, override onEditClick or onDeleteClick if specific behavior is needed.
     */


}
