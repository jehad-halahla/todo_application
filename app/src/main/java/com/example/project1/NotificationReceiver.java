package com.example.project1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_SNOOZE = "com.example.project1.ACTION_SNOOZE";
    private static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_SNOOZE.equals(action)) {
            Log.d("NotificationReceiver", "Snooze action received");

            // Make a parser
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            // Remove the current notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);

            // Reschedule the notification after 5 minutes
            long taskID = intent.getLongExtra("task_id", -1);
            String taskTitle = intent.getStringExtra("task_title");
            String taskDescription = intent.getStringExtra("task_description");
            String taskDueDate = intent.getStringExtra("task_due_date");
            String taskDueTime = intent.getStringExtra("task_due_time");
            String taskPriority = intent.getStringExtra("task_priority");
            String taskReminderTime = intent.getStringExtra("task_reminder_time");

            long snoozeDuration = 1 * 60 * 1000; // 5 minutes in milliseconds
            Task task = new Task(); // Ensure Task object is properly initialized
            task.setId(taskID);
            task.setTitle(taskTitle);
            task.setDescription(taskDescription);
            task.setDueDate(taskDueDate);
            task.setDueTime(taskDueTime);
            task.setPriority(taskPriority);

            // Set the new reminder time
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis() + snoozeDuration);
            // Parse to format YYYY-MM-DD HH:MM
            String reminderTime = sdf.format(calendar.getTime());
            task.setReminderTime(reminderTime);

            NotificationScheduler.scheduleTaskReminder(context, task, 1); // Pass snooze duration as 1 minutes
            Log.d("New Notification", "New notification at time " + reminderTime);
        } else {
            // Normal notification handling
            String taskTitle = intent.getStringExtra("task_title");

            // Create a notification manager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // For devices running Android O and above, create a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Task Reminders", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Task Reminder")
                    .setContentText("You have a task: " + taskTitle)
                    .setSmallIcon(R.drawable.baseline_notification_important_24) // Use an appropriate icon here
                    .setAutoCancel(true)
                    .addAction(createSnoozeAction(context, taskTitle)) // Add the snooze action
                    .build();

            // Show the notification
            notificationManager.notify(0, notification);
        }
    }

    // Create Snooze Action
    private NotificationCompat.Action createSnoozeAction(Context context, String taskTitle) {
        Intent snoozeIntent = new Intent(context, NotificationReceiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra("task_title", taskTitle); // Pass task title for rescheduling
        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Action.Builder(
                R.drawable.baseline_snooze_24, // Icon for snooze action
                "Snooze",
                snoozePendingIntent
        ).build();
    }
}
