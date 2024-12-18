package com.example.project1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

/**
 * Abstract Base Fragment that handles common task interactions.
 */
public abstract class BaseTaskFragment extends Fragment implements TaskItemAdapter.OnItemClickListener {

    protected RecyclerView recyclerView;
    protected TaskItemAdapter taskItemAdapter;
    protected List<Task> taskList;

    /**
     * Subclasses must implement this to provide the filter type.
     *
     * @return The filter type ("All", "Today", "Completed").
     */
    protected abstract String getFilterType();

    /**
     * Subclasses must implement this to provide the layout resource ID.
     *
     * @return The layout resource ID.
     */
    protected abstract int getLayoutResource();

    /**
     * Subclasses must implement this to provide the RecyclerView ID.
     *
     * @return The RecyclerView ID.
     */
    protected abstract int getRecyclerViewId();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the respective fragment layout
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(getRecyclerViewId());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            taskList = homeActivity.getFilteredTaskList(getFilterType());
            taskItemAdapter = new TaskItemAdapter(getContext(), taskList, this);
            recyclerView.setAdapter(taskItemAdapter);
        }
    }

    /**
     * Handles the edit action for a task.
     *
     * @param task The Task object to edit.
     */
    public void onEditClick(Task task) {
        // Handle edit action (common implementation)
        Toast.makeText(getContext(), "Edit Task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Implement editing functionality (e.g., open a dialog or navigate to an edit screen)
    }

    /**
     * Handles the delete action for a task.
     *
     * @param task The Task object to delete.
     */
    public void onDeleteClick(Task task) {
        // Handle delete action (common implementation)
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.deleteTask(task);
            Toast.makeText(getContext(), "Deleted Task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the email action for a task.
     *
     * @param task The Task object to email.
     */
    public void onEmailClick(Task task) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, task.getTitle());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Reminder for task: " + task.getDescription());
        startActivity(emailIntent);
    }

    /**
     * Refreshes the task list when notified by HomeActivity.
     */
    public void refreshTasks() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            taskList = homeActivity.getFilteredTaskList(getFilterType());
            taskItemAdapter.updateList(taskList);
        }
    }
}
