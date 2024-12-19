package com.example.project1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Abstract Base Fragment that handles common task interactions.
 */
public abstract class BaseTaskFragment extends Fragment implements TaskItemAdapter.OnItemClickListener {

    protected RecyclerView recyclerView;
    protected TaskItemAdapter taskItemAdapter;
    protected List<Task> taskList;

    private DatabaseHelper dbHelper;

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

    /**
     * Subclasses must implement this to indicate if the data should be grouped.
     *
     * @return true if the data is grouped, false otherwise.
     */
    protected abstract boolean isGrouped();

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

            if (isGrouped()) {
                Log.d("Preparing grouped tasks", "Preparing grouped tasks");
                List<Object> groupedTasks = prepareGroupedTasks(taskList);
                taskItemAdapter = new TaskItemAdapter(getContext(), groupedTasks, true, this);
            } else {
                taskItemAdapter = new TaskItemAdapter(getContext(), taskList, false, this);
            }

            recyclerView.setAdapter(taskItemAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
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
    @Override
    public void onDeleteClick(Task task) {
        if (task == null) {
            Log.e("BaseTaskFragment", "onDeleteClick: Task is null.");
            return;
        }
        Log.d("BaseTaskFragment", "onDeleteClick: Task received: " + task.getTitle());
        dbHelper = DatabaseHelper.getInstance(getContext());
//        dbHelper.deleteTask(task);
        if (isGrouped()) {
            // Handle deletion in a grouped RecyclerView
            handleGroupedTaskDeletion(task);
        } else {
            // Handle deletion in a non-grouped RecyclerView
            handleUngroupedTaskDeletion(task);
        }
    }

    /**
     * Handles deletion of a task in a grouped RecyclerView.
     */
    private void handleGroupedTaskDeletion(Task task) {
        List<Object> groupedItems = (List<Object>) taskItemAdapter.getItems(); // Get current grouped items
        int taskIndex = -1;
        String headerKey = task.getDueDate(); // Assuming dueDate is used as the grouping key
        boolean headerRemoved = false;

        // Find and remove the task in the grouped list
        for (int i = 0; i < groupedItems.size(); i++) {
            if (groupedItems.get(i) instanceof Task && groupedItems.get(i).equals(task)) {
                taskIndex = i;
                groupedItems.remove(i);
                break;
            }
        }

        // Check if the header has no remaining tasks
        int remainingTasksUnderHeader = 0;
        for (Object item : groupedItems) {
            if (item instanceof Task && ((Task) item).getDueDate().equals(headerKey)) {
                remainingTasksUnderHeader++;
            }
        }

        // If no tasks remain under the header, remove the header
        if (remainingTasksUnderHeader == 0) {
            for (int i = 0; i < groupedItems.size(); i++) {
                if (groupedItems.get(i) instanceof String && groupedItems.get(i).equals(headerKey)) {
                    groupedItems.remove(i);
                    taskItemAdapter.notifyItemRemoved(i);
                    headerRemoved = true;
                    break;
                }
            }
        }

        // Notify the adapter about task removal
        if (taskIndex >= 0) {
            taskItemAdapter.notifyItemRemoved(taskIndex);
            if (!headerRemoved) {
                taskItemAdapter.notifyItemRangeChanged(taskIndex, groupedItems.size());
            }
        }

        // Remove the task from the global task list
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.deleteTask(task);
        }

        Log.d("BaseTaskFragment", "Grouped task deleted: " + task.getTitle());
    }

    /**
     * Handles deletion of a task in an ungrouped RecyclerView.
     */
    private void handleUngroupedTaskDeletion(Task task) {
        int taskIndex = taskList.indexOf(task);

        if (taskIndex >= 0) {
            // Remove the task from the taskList
            taskList.remove(taskIndex);

            // Notify the adapter about the item removal
            taskItemAdapter.notifyItemRemoved(taskIndex);
            taskItemAdapter.notifyItemRangeChanged(taskIndex, taskList.size());

            // Remove the task from the global task list
            if (getActivity() instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.deleteTask(task);
            }

            Log.d("BaseTaskFragment", "Ungrouped task deleted: " + task.getTitle());
        } else {
            Log.e("BaseTaskFragment", "Task not found in taskList for ungrouped deletion.");
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

    public void onCompletedClick(Task task) {
        //check if the box is clicked, if so then mark the task as completed

    }

    /**
     * Refreshes the task list when notified by HomeActivity.
     */
    public void refreshTasks() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            taskList = homeActivity.getFilteredTaskList(getFilterType()); // Refresh task list

            if (isGrouped()) {
                List<Object> groupedTasks = prepareGroupedTasks(taskList); // Regroup tasks
                taskItemAdapter.updateList(groupedTasks, true); // Update adapter with grouped tasks
            } else {
                taskItemAdapter.updateList(taskList, false); // Update adapter with ungrouped tasks
            }
        }
    }

    /**
     * Refreshes the task list when notified by HomeActivity.
     */
    public void refreshTasks(int i) {
        if (i >= 0 && i < taskList.size()) {
            // Remove the task from the taskList
            taskList.remove(i);

            // Notify the adapter about the item removal
            taskItemAdapter.notifyItemRemoved(i);
            taskItemAdapter.notifyItemRangeChanged(i, taskList.size());

            // Debug log for confirmation
            Log.d("BaseTaskFragment", "Task at index " + i + " removed from taskList and adapter notified.");
        } else {
            Log.e("BaseTaskFragment", "Invalid index " + i + " for task removal.");
        }
    }



    private List<Object> prepareGroupedTasks(List<Task> tasks) {
        Map<String, List<Task>> groupedTasks = new TreeMap<>(); // TreeMap ensures chronological order

        for (Task task : tasks) {
            String date = task.getDueDate();
            groupedTasks.putIfAbsent(date, new ArrayList<>());
            groupedTasks.get(date).add(task);
        }

        // Prepare a flat list with section headers
        List<Object> groupedList = new ArrayList<>();
        for (Map.Entry<String, List<Task>> entry : groupedTasks.entrySet()) {
            groupedList.add(entry.getKey()); // Add the date as a header
            groupedList.addAll(entry.getValue()); // Add tasks for that date
        }

        return groupedList;
    }

    public void notifyTaskListChanged() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            List<Task> updatedTasks = homeActivity.getFilteredTaskList(getFilterType());

            if (isGrouped()) {
                List<Object> groupedTasks = prepareGroupedTasks(updatedTasks);
                taskItemAdapter.updateList(groupedTasks, true); // Notify grouped changes
            } else {
                DiffUtil.Callback diffCallback = new TaskDiffCallback(taskList, updatedTasks);
                DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

                taskList.clear();
                taskList.addAll(updatedTasks);

                diffResult.dispatchUpdatesTo(taskItemAdapter); // Notify granular changes
            }
        }
    }

}
