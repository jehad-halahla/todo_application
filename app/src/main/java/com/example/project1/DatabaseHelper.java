package com.example.project1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TodoApp2.db";
    private static final int DATABASE_VERSION = 1;


    private static final String TAG = "DatabaseHelper";
    // Singleton instance
    private static DatabaseHelper instance;

    // User table
    private static final String TABLE_USER = "User";
    private static final String COLUMN_EMAIL = "Email";
    private static final String COLUMN_FIRST_NAME = "FirstName";
    private static final String COLUMN_LAST_NAME = "LastName";
    private static final String COLUMN_PASSWORD = "Password";

    // Task table
    private static final String TABLE_TASK = "Task";
    private static final String COLUMN_TASK_ID = "TaskID";
    private static final String COLUMN_TASK_TITLE = "Title";
    private static final String COLUMN_TASK_DESCRIPTION = "Description";
    private static final String COLUMN_DUE_DATE = "DueDate";
    private static final String COLUMN_DUE_TIME = "DueTime";
    private static final String COLUMN_PRIORITY = "Priority";
    private static final String COLUMN_IS_COMPLETED = "IsCompleted";
    private static final String COLUMN_USER_EMAIL = "UserEmail"; // Foreign key

    // Private constructor to prevent direct instantiation
    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Returns the singleton instance of DatabaseHelper.
     *
     * @param context The context used to create or retrieve the instance.
     * @return The singleton instance of DatabaseHelper.
     */
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User table
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY, "
                + COLUMN_FIRST_NAME + " TEXT NOT NULL, "
                + COLUMN_LAST_NAME + " TEXT NOT NULL, "
                + COLUMN_PASSWORD + " TEXT NOT NULL)";
        db.execSQL(CREATE_USER_TABLE);

        // Create Task table
        String CREATE_TASK_TABLE = "CREATE TABLE " + TABLE_TASK + " ("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TASK_TITLE + " TEXT NOT NULL, "
                + COLUMN_TASK_DESCRIPTION + " TEXT, "
                + COLUMN_DUE_DATE + " TEXT NOT NULL, "
                + COLUMN_DUE_TIME + " TEXT NOT NULL, "
                + COLUMN_PRIORITY + " TEXT NOT NULL, "
                + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0, "
                + COLUMN_USER_EMAIL + " TEXT NOT NULL, "
                + "FOREIGN KEY(" + COLUMN_USER_EMAIL + ") REFERENCES " + TABLE_USER + "(" + COLUMN_EMAIL + "))";
        db.execSQL(CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }

    // User Management Methods
    public boolean addUser(User user) {
        if (checkEmailExists(user.getEmail())) {
            return false; // Email already exists
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_PASSWORD, user.getPassword());

        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE "
                + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{email, password})) {
            return (cursor.getCount() > 0);
        }
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{email})) {
            return (cursor.getCount() > 0);
        }
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USER, COLUMN_EMAIL + " = ?", new String[]{email});
        db.close();
        return result > 0;
    }

    // Task Management Methods
    public long addTask(Task task) {
        if (taskExists(task.getTitle(), task.getUserEmail())) {
            Log.d(TAG, "Task not added: A task with the title \"" + task.getTitle() + "\" already exists for user " + task.getUserEmail());
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, task.getTitle());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_DUE_TIME, task.getDueTime());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_USER_EMAIL, task.getUserEmail());

        long result = db.insert(TABLE_TASK, null, values);
        db.close();
        Log.d("DatabaseHelper", "Task added with ID: " + result);
        return result;
    }

    /**
     * Checks if a task with the specified title already exists for the given user email.
     *
     * @param title      The title of the task to check.
     * @param userEmail  The email of the user.
     * @return           True if the task exists, false otherwise.
     */
    public boolean taskExists(String title, String userEmail) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean exists = false;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT 1 FROM " + TABLE_TASK +
                    " WHERE " + COLUMN_TASK_TITLE + " = ? AND " + COLUMN_USER_EMAIL + " = ? LIMIT 1";
            cursor = db.rawQuery(query, new String[]{title, userEmail});
            exists = cursor.moveToFirst();

            if (exists) {
                Log.d(TAG, "Task with title \"" + title + "\" already exists for user " + userEmail);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking if task exists", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return exists;
    }

    public boolean deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
       //condition for delete is that the task title and user email must match
        Log.d("DatabaseHelper", "Deleting task with title: " + task.getTitle() + " and user email: " + task.getUserEmail());
        int rows = db.delete(TABLE_TASK, COLUMN_TASK_TITLE + " = ? AND " + COLUMN_USER_EMAIL + " = ?", new String[]{task.getTitle(), task.getUserEmail()});
        db.close();
        Log.d("DatabaseHelper", "Deleted " + rows + " tasks with title: " + task.getTitle());
        return rows > 0;
    }

    public boolean deleteTasksByUser(String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASK, COLUMN_USER_EMAIL + " = ?", new String[]{userEmail});
        Log.d("DatabaseHelper", "Deleted " + rows + " tasks for user: " + userEmail);

        db.close();


        return rows > 0;
    }

    public List<Task> getTasksByUser(String userEmail) {

        List<Task> ret = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASK + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DESCRIPTION)));
                task.setDueDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE)));
                task.setDueTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_TIME)));
                task.setPriority(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_COMPLETED)) == 1);
                task.setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)));
                ret.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ret;
    }

    public Cursor searchTasks(String userEmail, String keyword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASK + " WHERE " + COLUMN_USER_EMAIL + " = ? AND ("
                + COLUMN_TASK_TITLE + " LIKE ? OR " + COLUMN_TASK_DESCRIPTION + " LIKE ?)";
        String searchPattern = "%" + keyword + "%";
        return db.rawQuery(query, new String[]{userEmail, searchPattern, searchPattern});
    }

    public Cursor getTasksByDateRange(String userEmail, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASK + " WHERE " + COLUMN_USER_EMAIL + " = ? AND "
                + COLUMN_DUE_DATE + " BETWEEN ? AND ?";
        return db.rawQuery(query, new String[]{userEmail, startDate, endDate});
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_FIRST_NAME + ", " + COLUMN_LAST_NAME + " FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String firstName = "";
        String lastName = "";

        if (cursor.moveToFirst()) {
            firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
            lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));
        }
        cursor.close();
        db.close();
        return firstName + " " + lastName;
    }

    public void printDatabaseContents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);

            if (cursor.moveToFirst()) {
                do {
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME));

                    Log.d("DatabaseHelper", String.format("User: Email=%s, FirstName=%s, LastName=%s", email, firstName, lastName));
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "No users found in the database.");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error reading database: " + e.getMessage(), e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public boolean updateEmailSafely(String newEmail, String oldEmail, String currentPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, check if the old email and password are correct
        if (!checkUser(oldEmail, currentPassword)) {
            return false; // Authentication failed
        }

        // Check if the new email already exists
        if (checkEmailExists(newEmail)) {
            return false; // New email already exists
        }

        // Start transaction
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_EMAIL, newEmail);

            // Update email
            int updateCount = db.update(TABLE_USER, contentValues, COLUMN_EMAIL + " = ?", new String[]{oldEmail});
            if (updateCount == 1) {
                // Update foreign keys in related tables if necessary
                // For example, updating the UserEmail column in the Task table
                ContentValues taskValues = new ContentValues();
                taskValues.put(COLUMN_USER_EMAIL, newEmail);
                db.update(TABLE_TASK, taskValues, COLUMN_USER_EMAIL + " = ?", new String[]{oldEmail});

                db.setTransactionSuccessful();
                return true;
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating email: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
            db.close();
        }

        return false;
    }


    public boolean updatePassword(String newPassword, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);

        // Update the password where the email matches
        int rowsAffected = db.update(TABLE_USER, values, COLUMN_EMAIL + " = ?", new String[]{email});

        return rowsAffected > 0;
    }
}
