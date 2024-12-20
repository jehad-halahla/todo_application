package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.Task;

import java.util.List;


/**
 * Adapter for displaying tasks with optional grouping (e.g., headers for dates).
 */
public class TaskItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<?> items; // Can hold either Task or String+Task for grouped data
    private boolean isGrouped; // Determines if the data is grouped
    private final Context context;
    private final OnItemClickListener listener;

    /**
     * Interface for handling edit and delete actions on Task items.
     */
    public interface OnItemClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
        void onEmailClick(Task task);
        void onCompletedClick(Task task);
        void onAddNotificationClick(Task task);
    }

    /**
     * Constructor for grouped data.
     *
     * @param context    the context in which the adapter is used
     * @param items      the list of items (Tasks and headers for grouped)
     * @param isGrouped  whether the data is grouped
     * @param listener   the listener for handling item clicks
     */
    public TaskItemAdapter(Context context, List<?> items, boolean isGrouped, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.isGrouped = isGrouped;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (isGrouped && items.get(position) instanceof String) {
            return VIEW_TYPE_HEADER; // Header (e.g., date)
        }
        return VIEW_TYPE_ITEM; // Task
    }

    public List<?> getItems() {
        return items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.fragment_task_item, parent, false);
            return new TaskViewHolder(view, listener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else {
            ((TaskViewHolder) holder).bind((Task) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Updates the adapter's list of items and refreshes the RecyclerView.
     *
     * @param newItems   the new list of items
     * @param isGrouped  whether the new data is grouped
     */
    public void updateList(List<?> newItems, boolean isGrouped) {
        this.items = newItems;
        this.isGrouped = isGrouped;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for headers (e.g., date headers).
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.header_title);
        }

        public void bind(String date) {
            headerTitle.setText(date);
        }
    }

    /**
     * ViewHolder for Task items.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitle;
        private final TextView taskDescription;
        private final TextView taskDueDate;
        private final TextView taskPriority;
        private final TextView taskStatus;
        private final ImageView taskEdit;
        private final ImageView taskDelete;
        private final ImageView taskEmail;
        private final ImageView taskReminder;
        private final CheckBox taskCompleted;
        private Task currentTask;

        public TaskViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskDueDate = itemView.findViewById(R.id.task_due_date);
            taskPriority = itemView.findViewById(R.id.task_priority);
            taskStatus = itemView.findViewById(R.id.task_status);
            taskEdit = itemView.findViewById(R.id.task_edit);
            taskDelete = itemView.findViewById(R.id.task_delete);
            taskEmail = itemView.findViewById(R.id.task_email);
            taskReminder = itemView.findViewById(R.id.task_reminder_icon);
            taskCompleted = itemView.findViewById(R.id.task_checkbox);
            setupClickListeners(listener);

        }

        public void bind(Task task) {
            currentTask = task;
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskDueDate.setText("Due: " + task.getDueDate() + " " + task.getDueTime());
            taskPriority.setText("Priority: " + task.getPriority());
            taskStatus.setText(task.isCompleted() ? "Status: Completed" : "Status: Incomplete");
            taskCompleted.setChecked(task.isCompleted());

            // Set priority background color based on priority level
            int priorityColor = 0;
            switch (task.getPriority()) {
                case "High":
                    priorityColor = ContextCompat.getColor(itemView.getContext(), R.color.cute_red); // High priority
                    taskPriority.setBackgroundColor(priorityColor);
                    break;
                case "Medium":
                    priorityColor = ContextCompat.getColor(itemView.getContext(), R.color.yellow); // Medium priority
                    taskPriority.setBackgroundColor(priorityColor);

                    break;
                case "Low":
                    priorityColor = ContextCompat.getColor(itemView.getContext(), R.color.green_500); // Low priority
                    taskPriority.setBackgroundColor(priorityColor);

                    break;
            }

        }

        private void setupClickListeners(final OnItemClickListener listener) {
            taskEdit.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onEditClick(currentTask);
                }
            });

            taskDelete.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onDeleteClick(currentTask);
                }
            });

            taskEmail.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onEmailClick(currentTask);
                }
            });
            taskCompleted.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onCompletedClick(currentTask);
                }
            });
            taskReminder.setOnClickListener(v -> {
                if (listener != null && currentTask != null) {
                    listener.onAddNotificationClick(currentTask);
                }
            });

        }
    }


}

