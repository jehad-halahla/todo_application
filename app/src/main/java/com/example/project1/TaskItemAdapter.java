package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;
import com.example.project1.Task;

import java.util.List;

/**
 * Adapter for displaying a list of Task items in a RecyclerView.
 */
public class TaskItemAdapter extends RecyclerView.Adapter<TaskItemAdapter.TaskViewHolder> {

    // Member Variables
    private final Context context;
    private List<Task> taskList;
    private final OnItemClickListener listener;

    /**
     * Interface for handling edit and delete actions on Task items.
     */
    public interface OnItemClickListener {
        void onEditClick(Task task);
        void onDeleteClick(Task task);
        void onEmailClick(Task task);
    }

    /**
     * Constructor for TaskItemAdapter.
     *
     * @param context  the context in which the adapter is used
     * @param taskList the list of tasks to display
     * @param listener the listener for handling item clicks
     */
    public TaskItemAdapter(Context context, List<Task> taskList, OnItemClickListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    /**
     * Inflates the item layout and creates the ViewHolder.
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the task item layout
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_task_item, parent, false);
        return new TaskViewHolder(view, listener);
    }

    /**
     * Binds data to the ViewHolder's views.
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    /**
     * Returns the total number of items in the list.
     */
    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * Updates the task list and notifies the adapter.
     *
     * @param newList the new list of tasks
     */
    public void updateList(List<Task> newList) {
        this.taskList = newList;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for Task items.
     */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        // UI Components
        private final TextView taskTitle;
        private final TextView taskDescription;
        private final TextView taskDueDate;
        private final TextView taskPriority;
        private final TextView taskStatus;
        private final ImageView taskReminderIcon;
        private final ImageView taskEdit;
        private final ImageView taskDelete;
        private final ImageView taskEmail;

        private Task currentTask;

        /**
         * Constructor for TaskViewHolder.
         *
         * @param itemView the item view
         * @param listener the listener for handling item clicks
         */
        public TaskViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            // Initialize UI components
            taskTitle = itemView.findViewById(R.id.task_title);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskDueDate = itemView.findViewById(R.id.task_due_date);
            taskPriority = itemView.findViewById(R.id.task_priority);
            taskStatus = itemView.findViewById(R.id.task_status);
            taskReminderIcon = itemView.findViewById(R.id.task_reminder_icon);
            taskEdit = itemView.findViewById(R.id.task_edit);
            taskDelete = itemView.findViewById(R.id.task_delete);
            taskEmail = itemView.findViewById(R.id.task_email);

            // Set up click listeners for edit and delete actions
            setupClickListeners(listener);
        }

        /**
         * Binds a Task object to the UI components.
         *
         * @param task the Task to bind
         */
        public void bind(Task task) {
            currentTask = task;
            taskTitle.setText(task.getTitle());
            taskDescription.setText(task.getDescription());
            taskDueDate.setText("Due: " + task.getDueDate());
            taskPriority.setText("Priority: " + task.getPriority());
            taskStatus.setText(task.isCompleted() ? "Status: Completed" : "Status: Incomplete");
//            taskReminderIcon.setVisibility(task.isReminderSet() ? View.VISIBLE : View.GONE);
        }

        /**
         * Sets up click listeners for the edit and delete icons.
         *
         * @param listener the listener to handle clicks
         */
        private void setupClickListeners(final OnItemClickListener listener) {
            taskEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && currentTask != null) {
                        listener.onEditClick(currentTask);
                    }
                }
            });

            taskDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && currentTask != null) {
                        listener.onDeleteClick(currentTask);
                    }
                }
            });
            taskEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && currentTask != null) {
                        listener.onEmailClick(currentTask);
                    }
                }
            });

            }
        }
    }

