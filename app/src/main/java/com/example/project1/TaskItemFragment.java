package com.example.project1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.example.project1.R;

public class TaskItemFragment extends Fragment {

    private TextView taskTitle, taskDescription, taskDueDate, taskPriority, taskStatus;
    private ImageView taskReminderIcon, taskEdit, taskDelete;

    public TaskItemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_item, container, false);

        // Initialize views
        taskTitle = view.findViewById(R.id.task_title);
        taskDescription = view.findViewById(R.id.task_description);
        taskDueDate = view.findViewById(R.id.task_due_date);
        taskPriority = view.findViewById(R.id.task_priority);
        taskStatus = view.findViewById(R.id.task_status);
        taskReminderIcon = view.findViewById(R.id.task_reminder_icon);
        taskEdit = view.findViewById(R.id.task_edit);
        taskDelete = view.findViewById(R.id.task_delete);

        // Set up the task data (this data should ideally come from a task model)
        taskTitle.setText("Task Title");
        taskDescription.setText("Task Description");
        taskDueDate.setText("Due: 2024-12-31 10:00 AM");
        taskPriority.setText("Priority: High");
        taskStatus.setText("Status: Incomplete");
        // Set reminder icon if needed (for example, when a reminder is set)
        taskReminderIcon.setVisibility(View.VISIBLE); // Show reminder icon

        // Set click listeners for edit and delete
        taskEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle edit task action
                // Open an activity or fragment for editing the task
            }
        });

        taskDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete task action
                // Perform deletion from database and update UI
            }
        });

        return view;
    }
}
