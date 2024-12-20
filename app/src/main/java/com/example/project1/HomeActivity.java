package com.example.project1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * HomeActivity manages the main screen with a navigation drawer and Fragments to display tasks.
 */
public class HomeActivity extends AppCompatActivity implements ConnectionAsyncTask.TaskListener {

    // Local variables to store the user's name and email
    private String userName;
    private String userEmail;

    private DatabaseHelper dbHelper;

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    private FloatingActionButton floatingActionButton;

    private BaseTaskFragment currentFragment;

    private FragmentManager fragmentManager;

    AllTasksFragment allTasksFragment ;
    CompletedTasksFragment completedTasksFragment ;
    TodayTasksFragment todayTasksFragment ;


    // Data Lists
    private final List<Task> originalTaskList = new ArrayList<>();  // Holds all tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = DatabaseHelper.getInstance(this);

        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "User Name"); // Default value if not found
        userEmail = sharedPreferences.getString("userEmail", "user@example.com"); // Default value if not found
        // Initialize UI components

        initializeUIComponents();
        loadDatabase();
        initializeFragments();

        // Set default fragment
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_today);
            loadFragment(todayTasksFragment);
            setTitle(navigationView.getMenu().findItem(R.id.nav_today).getTitle());
            notifyFragmentsDataChanged();

        }
        else{
            loadFragment(currentFragment);
            setTitle(currentFragment.getFilterType());
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


    private void initializeFragments() {
        fragmentManager = getSupportFragmentManager();

        todayTasksFragment = TodayTasksFragment.newInstance();
        allTasksFragment = AllTasksFragment.newInstance();
        completedTasksFragment = CompletedTasksFragment.newInstance();

        // Add the fragments to the fragment manager but hide all except the current one
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, allTasksFragment, "AllTasks")
                .hide(allTasksFragment)
                .add(R.id.fragment_container, completedTasksFragment, "CompletedTasks")
                .hide(completedTasksFragment)
                .add(R.id.fragment_container, todayTasksFragment, "TodayTasks")
                .commit();

        // Set the initial fragment to TodayTasksFragment
        currentFragment = todayTasksFragment;
    }

    /**
     * Sets up the NavigationView's item selection listener to load appropriate Fragments.
     */
    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                String filterTitle = "";

                switch (menuItem.getItemId()) {
                    case R.id.nav_today:
                        currentFragment = todayTasksFragment;

                        filterTitle = getString(R.string.nav_today);
                        break;
                    case R.id.nav_new_task:
                        // Implement New Task functionality
                        Toast.makeText(HomeActivity.this, "New Task clicked", Toast.LENGTH_SHORT).show();
                        showAddTaskDialog();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        return true; // Early return since we're not loading a fragment
                    case R.id.nav_all:
                        currentFragment = allTasksFragment;
                        filterTitle = getString(R.string.nav_all);
                        break;
                    case R.id.nav_completed:
                        currentFragment = completedTasksFragment;
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

                if (currentFragment != null) {
                    loadFragment(currentFragment);
                    notifyFragmentsDataChanged();
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

        //hide all fragments
        for (Fragment f : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction()
                    .hide(f)
                    .commit();
        }
        fragmentManager.beginTransaction()
                .show(fragment)
                .commit();
    }

    /**
     * Populates the original task list by fetching data from the API or local storage.
     */
    private void populateTasks() {
        Log.d("HomeActivity", "populateTasks called");
        // Execute the AsyncTask to fetch tasks from the API
        String apiUrl = "https://mocki.io/v1/12cf4ca9-ef06-47ce-b2ef-68671e5bf9db"; // Replace with your actual API URL
        new ConnectionAsyncTask(this).execute(apiUrl);
        //add the tasks to the database

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
            for (Task task : tasks) {
                task.setUserEmail(getUserEmail());
                //write to db
                dbHelper.addTask(task);
            }
            notifyFragmentsDataChanged();
        } else {
            Toast.makeText(this, "Error fetching tasks", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPostExecute(List<Task> tasks) {

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
        newTask.setUserEmail(getUserEmail());
        newTask.setDueTime("12:00");
        dbHelper.addTask(newTask);
        notifyFragmentsDataChanged();
        Toast.makeText(this, "New Task Added", Toast.LENGTH_SHORT).show();

        // TODO: Implement persistent storage (e.g., save to database)
        //save to database

    }

    /**
     * Notifies all active Fragments to refresh their task lists when fragment loaded.
     */
    private void notifyFragmentsDataChanged() {
        List<Fragment> fragments = fragmentManager.getFragments();

        for (Fragment fragment : fragments) {
            if (fragment instanceof BaseTaskFragment && fragment != null) {
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

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar_menu, menu); // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Search for a keyword..");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                searchForKeyword(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text changes for live suggestions or filtering
                searchForKeyword(newText);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                // Show toast message when search icon is clicked
                Toast.makeText(this, "Search icon clicked!", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.action_sort:
                Toast.makeText(this, "Sort icon clicked!", Toast.LENGTH_SHORT).show();
                sortTasks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void loadDatabase() {
        List<Task> tasks;
        dbHelper = DatabaseHelper.getInstance(this);
        tasks = dbHelper.getTasksByUser(userEmail);
        Log.d("HomeActivity", "loadDatabase: " + tasks.size());

    }

    private void sortTasks() {
        //call a sort function in the current fragment
        if (currentFragment != null) {
            currentFragment.sortTasks();
        }
    }

    private void searchForKeyword(String keyword) {
        if (currentFragment != null) {
            currentFragment.searchByKeyWord(keyword);
        }
    }


    private void filterUsingDateRange(Date startDate, Date endDate){
        //call a filter function in the current fragment
        if (currentFragment != null) {
            currentFragment.filterUsingDateRange(startDate, endDate);
        }
    }

    public String getUserEmail() {
        return userEmail;
    }


}
