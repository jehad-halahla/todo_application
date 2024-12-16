package com.example.project1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to parse JSON data into Task objects.
 */
public class TaskJsonParser {

    /**
     * Parses a JSON string and converts it into a list of Task objects.
     *
     * @param json The JSON string representing the tasks.
     * @return A list of Task objects or null if parsing fails.
     */
    public static List<Task> getTasksFromJson(String json) {
        List<Task> tasks = new ArrayList<>();
        try {
            // Parse the root JSON object
            JSONObject root = new JSONObject(json);

            // Extract the "tasks" array from the root object
            JSONArray tasksArray = root.getJSONArray("tasks");

            // Iterate through each task in the array
            for (int i = 0; i < tasksArray.length(); i++) {
                JSONObject jsonObject = tasksArray.getJSONObject(i);

                // Extract fields from the JSON object
                String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");
                String description = jsonObject.getString("description");
                String dueDate = jsonObject.getString("dueDate");
                String priority = jsonObject.getString("priority");
                boolean isCompleted = jsonObject.getBoolean("isCompleted");
                boolean isReminderSet = jsonObject.getBoolean("isReminderSet");

                // Create a new Task object using the extracted data
                Task task = new Task();
                task.setId(Integer.parseInt(id));
                task.setTitle(title);
                task.setDescription(description);
                task.setDueDate(dueDate);
                task.setPriority(priority);
                task.setCompleted(isCompleted);
//                task.setReminderSet(isReminderSet);

                // Add the Task object to the list
                tasks.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
        return tasks;
    }
}
