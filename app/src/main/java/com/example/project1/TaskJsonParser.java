package com.example.project1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskJsonParser {

    /**
     * Parses a JSON string and converts it into a list of Task objects.
     * @param json The JSON string representing the tasks.
     * @return A list of Task objects or null if parsing fails.
     */
    public static List<Task> getTasksFromJson(String json) {
        List<Task> tasks = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Task task = new Task();

                // Populate the Task object from JSON
                task.setId(jsonObject.getInt("id"));
                task.setTitle(jsonObject.getString("title"));
                task.setDescription(jsonObject.getString("description"));
                task.setDueDate(jsonObject.getString("dueDate"));
                task.setPriority(jsonObject.getString("priority"));
                task.setCompleted(jsonObject.getBoolean("isCompleted"));

                tasks.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
        return tasks;
    }
}
