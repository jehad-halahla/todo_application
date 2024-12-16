package com.example.project1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * HomeActivity manages the main screen with a navigation drawer and a RecyclerView to display tasks.
 */
public class HomeActivity extends AppCompatActivity implements TaskItemAdapter.OnItemClickListener, ConnectionAsyncTask.TaskListener {

    // Local variables to store the user's name and email
    private String userName;
    private String userEmail;

    // UI Components
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    // Data Lists
    private final List<Task> originalTaskList = new ArrayList<>();  // Holds all tasks
    private final List<Task> filteredTaskList = new ArrayList<>();  // Holds filtered tasks

    // Adapter
    private TaskItemAdapter taskItemAdapter;

    // Current filter
    private String currentFilter = "Today"; // Default filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "User Name"); // Default value if not found
        userEmail = sharedPreferences.getString("userEmail", "user@example.com"); // Default value if not found

        // Initialize UI components
        initializeUIComponents();

        // Set up the RecyclerView
        setupRecyclerView();

        // Initialize the adapter with the filtered task list
        taskItemAdapter = new TaskItemAdapter(this, filteredTaskList, this);
        recyclerView.setAdapter(taskItemAdapter);

        // Populate tasks with data from the API
        populateDummyTasks();

        // Set a default selection and apply the filter
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_today);
            filterTasks(currentFilter);
        }

        // Set the title of the activity
        setTitle("Home");
    }

    /**
     * Initializes UI components like Toolbar, DrawerLayout, and NavigationView.
     */
    private void initializeUIComponents() {
        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Set up the ActionBarDrawerToggle to handle drawer opening and closing
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize NavigationView
        navigationView = findViewById(R.id.navigation_view);
        setupNavigationView();

        // Access the header view of the NavigationView
        View headerView = navigationView.getHeaderView(0);

        // Find and set the user name and email in the navigation header
        TextView navUserName = headerView.findViewById(R.id.nav_header_name);
        TextView navUserEmail = headerView.findViewById(R.id.nav_header_email);
        navUserName.setText(userName);
        navUserEmail.setText(userEmail);
    }

    /**
     * Sets up the NavigationView's item selection listener.
     */
    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle navigation view item clicks here.
                switch (menuItem.getItemId()) {
                    case R.id.nav_today:
                        currentFilter = "Today";
                        filterTasks(currentFilter);
                        break;
                    case R.id.nav_new_task:
                        // Implement New Task functionality
                        Toast.makeText(HomeActivity.this, "New Task clicked", Toast.LENGTH_SHORT).show();
                        showAddTaskDialog();
                        break;
                    case R.id.nav_all:
                        currentFilter = "All";
                        filterTasks(currentFilter);
                        break;
                    case R.id.nav_completed:
                        currentFilter = "Completed";
                        filterTasks(currentFilter);
                        break;
                    case R.id.nav_search:
                        // Implement Search functionality
                        Toast.makeText(HomeActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
                        // TODO: Open Search Activity or Dialog
                        break;
                    case R.id.nav_profile:
                        // Implement Profile functionality
                        Toast.makeText(HomeActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
                        // TODO: Open Profile Activity
                        break;
                    case R.id.nav_logout:
                        // Implement Logout functionality
                        Toast.makeText(HomeActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                        // TODO: Perform logout and redirect to Sign-In Activity
                        break;
                    default:
                        break;
                }

                // Close the drawer after selection
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager.
     */
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Populates the original task list by fetching data from the API.
     */
    private void populateDummyTasks() {
        // Clear existing tasks to avoid duplication
        originalTaskList.clear();

        // Execute the AsyncTask to fetch tasks from the API
        String apiUrl = "https://mocki.io/v1/b00eafce-248d-4ef2-af82-16063a826fa6"; // Replace with your actual API URL
        new ConnectionAsyncTask(this).execute(apiUrl);
    }

    /**
     * Filters the tasks based on the selected option and updates the RecyclerView.
     *
     * @param filter The filter criteria ("Today", "All", "Completed")
     */
    private void filterTasks(String filter) {
        filteredTaskList.clear();

        switch (filter) {
            case "Today":
                // Get today's date dynamically
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String todayDate = sdf.format(new Date());
                for (Task task : originalTaskList) {
                    if (task.getDueDate().startsWith(todayDate)) {
                        filteredTaskList.add(task);
                    }
                }
                break;
            case "Completed":
                for (Task task : originalTaskList) {
                    if (task.isCompleted()) {
                        filteredTaskList.add(task);
                    }
                }
                break;
            default:
                filteredTaskList.addAll(originalTaskList);
                break;
        }

        // Notify the adapter to refresh the RecyclerView
        taskItemAdapter.updateList(filteredTaskList);
    }

    /**
     * Adds a new task to the original task list and refreshes the RecyclerView based on the current filter.
     */
    private void addNewTask(Task newTask) {


        originalTaskList.add(newTask);
        filterTasks(currentFilter);

        Toast.makeText(this, "New Task Added", Toast.LENGTH_SHORT).show();

        // TODO: Implement persistent storage (e.g., save to database or API)
    }

    /**
     * Handles the edit action for a task.
     *
     * @param position The position of the task in the filtered list.
     */
    @Override
    public void onEditClick(int position) {
        // Retrieve the task from the filtered list
        Task task = filteredTaskList.get(position);
        Toast.makeText(this, "Edit Task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Implement editing functionality (e.g., open a dialog or new activity)
    }

    /**
     * Handles the delete action for a task.
     *
     * @param position The position of the task in the filtered list.
     */
    @Override
    public void onDeleteClick(int position) {
        // Retrieve the task from the filtered list
        Task task = filteredTaskList.get(position);

        // Remove the task from the original list
        originalTaskList.remove(task);

        // Reapply the current filter to update the RecyclerView
        filterTasks(currentFilter);

        Toast.makeText(this, "Deleted Task: " + task.getTitle(), Toast.LENGTH_SHORT).show();

        // TODO: Implement deletion from database or persistent storage
    }

    /**
     * Callback before the AsyncTask starts.
     * Removed ProgressBar related code.
     */
    @Override
    public void onPreExecute() {
        // No ProgressBar functionality implemented
    }

    /**
     * Callback when the AsyncTask successfully fetches and parses tasks.
     *
     * @param tasks The list of fetched tasks.
     */
    @Override
    public void onSuccess(List<Task> tasks) {
        if (tasks != null) {
            originalTaskList.addAll(tasks);
            filterTasks(currentFilter);
        } else {
            Toast.makeText(this, "Error fetching tasks", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback when the AsyncTask encounters an error.
     *
     * @param errorMessage The error message to display.
     */
    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void showAddTaskDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.add_task_layout, null);

        // Initialize UI components within the dialog
        EditText editTextTitle = dialogView.findViewById(R.id.edit_text_task_title);
        EditText editTextDescription = dialogView.findViewById(R.id.edit_text_task_description);
        EditText editTextDueDate = dialogView.findViewById(R.id.edit_text_task_due_date);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinner_task_priority);
        CheckBox checkBoxCompleted = dialogView.findViewById(R.id.checkbox_is_completed);
        CheckBox checkBoxReminderSet = dialogView.findViewById(R.id.checkbox_is_reminder_set);

        // Set up the Spinner for task priority
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Add New Task")
                .setPositiveButton("Add", null) // Set to null to override the onClick later
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Override the Positive Button to prevent automatic dismissal on validation failure
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            String dueDate = editTextDueDate.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            boolean isCompleted = checkBoxCompleted.isChecked();
            boolean isReminderSet = checkBoxReminderSet.isChecked();

            // Validate inputs
            if (title.isEmpty()) {
                editTextTitle.setError("Title is required");
                return;
            }

            if (dueDate.isEmpty()) {
                editTextDueDate.setError("Due date is required");
                return;
            }

            // Optionally, validate the due date format here

//            // Create a new Task object
            Task newTask = new Task(
            );
            newTask.setId(0);
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setDueDate(dueDate);
            newTask.setPriority(priority);
            newTask.setCompleted(isCompleted);

            // Add the new task to the list
            addNewTask(newTask);

            // Dismiss the dialog
            dialog.dismiss();
        });
    }
}
