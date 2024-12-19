package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * AllTasksFragment displays all tasks.
 */
public class AllTasksFragment extends BaseTaskFragment {


    public AllTasksFragment() {
        // Required empty public constructor
    }

    public static AllTasksFragment newInstance() {
        return new AllTasksFragment();
    }


    @Override
    protected String getFilterType() {
        return "All";
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_all_tasks; // Ensure this layout exists
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.recycler_view_all_tasks; // Ensure this ID matches your layout
    }

    @Override
    protected boolean isGrouped() {
        return true;
    }

    /**
     * Optionally, override onEditClick or onDeleteClick if specific behavior is needed.
     */


}
