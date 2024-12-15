package com.example.project1;

public class Task {
    //data fields
    int id;
    String title;
    String description;
    String dueDate;
    String priority;
    boolean isCompleted;
    String userEmail = ""; // the foriegn key

    //no task can be made with no user

    public Task(){}
    public Task(String description, String dueDate, int id, boolean isCompleted, String priority, String title, String userEmail) {
        this.description = description;
        this.dueDate = dueDate;
        this.id = id;
        this.isCompleted = isCompleted;
        this.priority = priority;
        this.title = title;
        this.userEmail = userEmail;
    }

    //setters and getters

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", priority='" + priority + '\'' +
                ", isCompleted=" + isCompleted +
//                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}
