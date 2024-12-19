package com.example.project1;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * AsyncTask to handle network operations for fetching tasks.
 */
public class ConnectionAsyncTask extends AsyncTask<String, Void, List<Task>> {

    // WeakReference to prevent memory leaks
    private final WeakReference<TaskListener> listenerRef;
    private String errorMessage = null;

    /**
     * Constructor accepting a TaskListener.
     *
     * @param listener The listener to handle callbacks.
     */
    public ConnectionAsyncTask(TaskListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        TaskListener listener = listenerRef.get();
        if (listener != null) {
            listener.onPreExecute();
        }
    }

    @Override
    protected List<Task> doInBackground(String... strings) {
        if (strings.length == 0) {
            errorMessage = "No URL provided";
            return null;
        }

        String url = strings[0];
        // Fetch data from the API
        String jsonData = HttpManager.getData(url);
        if (jsonData == null || jsonData.isEmpty()) {
            errorMessage = "Failed to fetch data or empty response";
            return null;
        }

        // Parse JSON data into Task objects
        List<Task> tasks = TaskJsonParser.getTasksFromJson(jsonData);
        if (tasks == null) {
            errorMessage = "Error parsing data";
            return null;
        }

        return tasks;
    }

    @Override
    protected void onPostExecute(List<Task> tasks) {
        super.onPostExecute(tasks);
        TaskListener listener = listenerRef.get();
        if (listener != null) {
            if (tasks != null) {
                listener.onSuccess(tasks);
            } else {
                listener.onError(errorMessage != null ? errorMessage : "Unknown error occurred");
            }
        }
    }

    /**
     * Interface to handle callbacks from the AsyncTask.
     */
    public interface TaskListener {
        void onPreExecute();
        void onSuccess(List<Task> tasks);
        void onPostExecute(List<Task> tasks);
        void onError(String errorMessage);
    }
}
