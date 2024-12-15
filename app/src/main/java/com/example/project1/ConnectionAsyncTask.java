package com.example.project1;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.List;

public class ConnectionAsyncTask extends AsyncTask<String, String, String> {

    private final TaskListener listener;

    public ConnectionAsyncTask(TaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Notify the listener that the task is about to start
        if (listener != null) {
            listener.onPreExecute();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        // Fetch data from the API using the provided URL
        return HttpManager.getData(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (listener != null) {
            if (s != null && !s.isEmpty()) {
                // Parse the JSON response into Task objects
                List<Task> tasks = TaskJsonParser.getTasksFromJson(s);
                listener.onSuccess(tasks); // Notify listener of the success
            } else {
                listener.onError("Failed to fetch data or empty response");
            }
        }
    }

    public interface TaskListener {
        void onPreExecute();
        void onSuccess(List<Task> tasks);
        void onError(String errorMessage);
    }
}
