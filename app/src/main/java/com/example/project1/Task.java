package com.example.project1;

import java.util.Objects;

public class Task implements Comparable<Task> {
    // Data fields
    private long id;
    private String title;
    private String description;
    private String dueDate;
    private String dueTime;
    private String priority = "Medium"; // Default value
    private boolean isCompleted = false; // Default value
    private String userEmail;

    // No task can be made with no user
    public Task() {}

    public Task(String title, String description, String dueDate, String dueTime, String priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        if (priority != null && !priority.isEmpty()) {
            this.priority = priority;
        }
    }

    // Setters and getters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        if (priority != null && !priority.isEmpty()) {
            this.priority = priority;
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", dueTime='" + dueTime + '\'' +
                ", priority='" + priority + '\'' +
                ", isCompleted=" + isCompleted +
                '}';
    }

    @Override
    public int compareTo(Task other) {
        // Define priority ordering
        int thisPriority = getPriorityValue(this.priority);
        int otherPriority = getPriorityValue(other.priority);

        // Lower values indicate higher priority
        return Integer.compare(thisPriority, otherPriority);
    }

    private int getPriorityValue(String priority) {
        switch (priority) {
            case "High":
                return 1; // Highest priority
            case "Medium":
                return 2;
            case "Low":
                return 3; // Lowest priority
            default:
                return Integer.MAX_VALUE; // Undefined priorities come last
        }
    }


}
