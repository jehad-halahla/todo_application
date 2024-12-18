package com.example.project1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * HomeActivity manages the main screen with a navigation drawer and Fragments to display tasks.
 */
public class HomeActivity extends AppCompatActivity implements ConnectionAsyncTask.TaskListener {

    // Local variables to store the user's name and email
    private String userName;
    private String userEmail;

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private FloatingActionButton floatingActionButton;

    // Data Lists
    private final List<Task> originalTaskList = new ArrayList<>();  // Holds all tasks

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

        // Set default fragment
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_today);
            loadFragment(TodayTasksFragment.newInstance());
            setTitle(navigationView.getMenu().findItem(R.id.nav_today).getTitle());
        }

        // Set FloatingActionButton click listener to add a new task
        floatingActionButton.setOnClickListener(v -> populateTasks());

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

        // Initialize FloatingActionButton
        floatingActionButton = findViewById(R.id.floatingActionButton2);

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
     * Sets up the NavigationView's item selection listener to load appropriate Fragments.
     */
    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                String filterTitle = "";

                switch (menuItem.getItemId()) {
                    case R.id.nav_today:
                        selectedFragment = TodayTasksFragment.newInstance();
                        filterTitle = getString(R.string.nav_today);
                        break;
                    case R.id.nav_new_task:
                        // Implement New Task functionality
                        Toast.makeText(HomeActivity.this, "New Task clicked", Toast.LENGTH_SHORT).show();
                        showAddTaskDialog();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true; // Early return since we're not loading a fragment
                    case R.id.nav_all:
                        selectedFragment = AllTasksFragment.newInstance();
                        filterTitle = getString(R.string.nav_all);
                        break;
                    case R.id.nav_completed:
                        selectedFragment = CompletedTasksFragment.newInstance();
                        filterTitle = getString(R.string.nav_completed);
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
                        logOut();
                        break;
                    default:
                        break;
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    setTitle(filterTitle);
                }

                // Close the drawer after selection
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    /**
     * Loads the specified Fragment into the fragment container.
     *
     * @param fragment The Fragment to load.
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /**
     * Populates the original task list by fetching data from the API or local storage.
     */
    private void populateTasks() {
        // Clear existing tasks to avoid duplication
        originalTaskList.clear();

        // Execute the AsyncTask to fetch tasks from the API
        String apiUrl = "https://mocki.io/v1/b00eafce-248d-4ef2-af82-16063a826fa6"; // Replace with your actual API URL
        new ConnectionAsyncTask(this).execute(apiUrl);
    }

    /**
     * Handles the logout functionality by clearing user data and navigating to LoginActivity.
     */
    private void logOut(){
        // Clear user information from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to LoginActivity and clear the back stack
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPreExecute() {
        // Optionally, show a ProgressBar or loading indicator here
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
            // Notify active Fragments to refresh their data
            notifyFragmentsDataChanged();
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

    /**
     * Adds a new task to the original task list and notifies Fragments to refresh.
     *
     * @param newTask The Task object to add.
     */
    private void addNewTask(Task newTask) {
        originalTaskList.add(newTask);
        notifyFragmentsDataChanged();
        Toast.makeText(this, "New Task Added", Toast.LENGTH_SHORT).show();

        // TODO: Implement persistent storage (e.g., save to database)
    }

    /**
     * Handles the deletion of a task from the original task list and notifies Fragments.
     *
     * @param task The Task object to delete.
     */
    public void deleteTask(Task task) {
        originalTaskList.remove(task);
        notifyFragmentsDataChanged();
        Toast.makeText(this, "Deleted Task: " + task.getTitle(), Toast.LENGTH_SHORT).show();

        // TODO: Implement deletion from persistent storage (e.g., remove from database)
    }

    /**
     * Notifies all active Fragments to refresh their task lists.
     */
    private void notifyFragmentsDataChanged() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();

        for (Fragment fragment : fragments) {
            if (fragment instanceof BaseTaskFragment) {
                ((BaseTaskFragment) fragment).refreshTasks();
            }
        }
    }

    /**
     * Shows a dialog to add a new task.
     */
    private void showAddTaskDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.add_task_layout, null);

        // Initialize UI components within the dialog
        EditText editTextTitle = dialogView.findViewById(R.id.edit_text_task_title);
        EditText editTextDescription = dialogView.findViewById(R.id.edit_text_task_description);
        EditText editTextDueDate = dialogView.findViewById(R.id.edit_text_task_due_date);
        Spinner spinnerPriority = dialogView.findViewById(R.id.spinner_task_priority);
        CheckBox checkBoxCompleted = dialogView.findViewById(R.id.checkbox_is_completed);
        CheckBox checkBoxReminderSet = dialogView.findViewById(R.id.checkbox_is_reminder_set);
        Button buttonAddTask = dialogView.findViewById(R.id.button_add_task);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);

        // Set up the Spinner for task priority
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);

        // Set a DatePickerDialog when due date EditText is clicked
        editTextDueDate.setOnClickListener(v -> showDatePickerDialog(editTextDueDate));

        // Build the AlertDialog without title and default buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle the "Add" button click
        buttonAddTask.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();
            String dueDate = editTextDueDate.getText().toString().trim();
            String priority = spinnerPriority.getSelectedItem().toString();
            boolean isCompleted = checkBoxCompleted.isChecked();
            boolean isReminderSet = checkBoxReminderSet.isChecked();

            // Validate inputs
            if (title.isEmpty()) {
                editTextTitle.setError("Title is required");
                editTextTitle.requestFocus();
                return;
            }

            if (dueDate.isEmpty()) {
                editTextDueDate.setError("Due date is required");
                editTextDueDate.requestFocus();
                return;
            }

            // Validate the due date format
            if (!isValidDate(dueDate)) {
                editTextDueDate.setError("Invalid date format. Use YYYY-MM-DD");
                editTextDueDate.requestFocus();
                return;
            }

            // Create a new Task object
            Task newTask = new Task();
            newTask.setTitle(title);
            newTask.setDescription(description);
            newTask.setDueDate(dueDate);
            newTask.setPriority(priority);
            newTask.setCompleted(isCompleted);
//            newTask.setReminderSet(isReminderSet);

            // Add the new task to the list
            addNewTask(newTask);

            // Dismiss the dialog
            dialog.dismiss();
        });

        // Handle the "Cancel" button click
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Shows a DatePickerDialog and sets the selected date to the provided EditText.
     *
     * @param editTextDueDate The EditText to set the selected date.
     */
    private void showDatePickerDialog(EditText editTextDueDate) {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date as YYYY-MM-DD
                    String formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                            selectedYear, selectedMonth + 1, selectedDay);
                    editTextDueDate.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Validates the date format to ensure it matches YYYY-MM-DD.
     *
     * @param date The date string to validate.
     * @return True if valid, false otherwise.
     */
    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date parsedDate = sdf.parse(date);
            return parsedDate != null;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Public method to retrieve the original task list.
     *
     * @return The list of all tasks.
     */
    public List<Task> getOriginalTaskList() {
        return originalTaskList;
    }

    /**
     * Retrieves a filtered list of tasks based on the provided filter criteria.
     *
     * @param filter The filter criteria ("Today", "All", "Completed")
     * @return A list of tasks matching the filter.
     */
    public List<Task> getFilteredTaskList(String filter) {
        List<Task> filteredList = new ArrayList<>();

        switch (filter) {
            case "Today":
                // Get today's date dynamically
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String todayDate = sdf.format(new Date());
                for (Task task : originalTaskList) {
                    if (task.getDueDate().startsWith(todayDate)) {
                        filteredList.add(task);
                    }
                }
                break;
            case "Completed":
                for (Task task : originalTaskList) {
                    if (task.isCompleted()) {
                        filteredList.add(task);
                    }
                }
                break;
            case "All":
            default:
                filteredList.addAll(originalTaskList);
                break;
        }

        return filteredList;
    }

}
