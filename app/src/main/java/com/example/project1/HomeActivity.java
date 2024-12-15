package com.example.project1;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
public class HomeActivity extends AppCompatActivity implements TaskItemAdapter.OnItemClickListener {

    // UI Components
    private RecyclerView recyclerView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    // Data Lists
    private List<Task> originalTaskList = new ArrayList<>();  // Holds all tasks
    private List<Task> filteredTaskList = new ArrayList<>();  // Holds filtered tasks

    // Adapter
    private TaskItemAdapter taskItemAdapter;

    // Current filter
    private String currentFilter = "Today"; // Default filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI components
        initializeUIComponents();

        // Set up the RecyclerView
        setupRecyclerView();

        // Populate tasks with dummy data
        populateDummyTasks();

        // Initialize the adapter with the filtered task list
        taskItemAdapter = new TaskItemAdapter(this, filteredTaskList, this);
        recyclerView.setAdapter(taskItemAdapter);

        // Set a default selection and apply the filter
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_today);
            filterTasks(currentFilter);
        }
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

        // Initialize NavigationView and set its listener
        navigationView = findViewById(R.id.navigation_view);
        setupNavigationView();
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager.
     */
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.task_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                        addNewTask();
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
     * Populates the original task list with dummy data.
     */
    private void populateDummyTasks() {
        // Clear existing tasks to avoid duplication
        originalTaskList.clear();
        //we will read the database and the api using a helper class called dataRepository
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
            case "All":
                filteredTaskList.addAll(originalTaskList);
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
    private void addNewTask() {
        // Example: Add a new task
        Task newTask = new Task();
        newTask.setTitle("New Task");
        newTask.setDescription("Description of new task.");
        newTask.setDueDate("2024-12-17 12:00 PM");
        newTask.setPriority("Low");
        newTask.setCompleted(false);
        // newTask.setReminderSet(false); // Uncomment if needed
        originalTaskList.add(newTask);

        // Reapply the current filter to include the new task
        filterTasks(currentFilter);

        Toast.makeText(this, "New Task Added", Toast.LENGTH_SHORT).show();
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
}
