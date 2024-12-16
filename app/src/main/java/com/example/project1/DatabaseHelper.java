package com.example.project1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TodoApp.db";
    private static final int DATABASE_VERSION = 1;

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
    private static final String COLUMN_PRIORITY = "Priority";
    private static final String COLUMN_IS_COMPLETED = "IsCompleted";
    private static final String COLUMN_USER_EMAIL = "UserEmail"; // Foreign key

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
                + COLUMN_PRIORITY + " TEXT NOT NULL, "
                + COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0, " // 0 = false, 1 = true
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, task.getTitle());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);
//        values.put(COLUMN_USER_EMAIL, task.getUserEmail());

        long result = db.insert(TABLE_TASK, null, values);
        db.close();
        return result;
    }

    public boolean updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, task.getTitle());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_PRIORITY, task.getPriority());
        values.put(COLUMN_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        int rows = db.update(TABLE_TASK, values, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return rows > 0;
    }

    public boolean deleteTask(int taskID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASK, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskID)});
        db.close();
        return rows > 0;
    }

    public Cursor getTasksByUser(String userEmail, boolean showCompleted) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASK + " WHERE " + COLUMN_USER_EMAIL + " = ?";
        if (!showCompleted) {
            query += " AND " + COLUMN_IS_COMPLETED + " = 0";
        }
        return db.rawQuery(query, new String[]{userEmail});
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
}
