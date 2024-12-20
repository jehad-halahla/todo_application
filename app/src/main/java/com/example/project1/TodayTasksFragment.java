package com.example.project1;

import android.widget.Toast;

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

    @Override
    public void onCompletedClick(Task task) {
        super.onCompletedClick(task);
        //if all today tasks are completed, then a toast message should be displayed
        if (taskList.isEmpty()) {
//            Toast.makeText(getContext(), "All today tasks are completed", Toast.LENGTH_SHORT).show();
            showDoneDialog();
        }

    }


    private void showDoneDialog() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        if (homeActivity != null) {
            homeActivity.showDoneDialog();
        }
    }

}
