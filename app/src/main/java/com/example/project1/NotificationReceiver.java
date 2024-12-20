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

public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_SNOOZE = "com.example.project1.ACTION_SNOOZE";
    private static final String CHANNEL_ID = "TASK_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get task info from the intent
        Log.d("NotificationReceiver", "onReceive called");
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
                .addAction(createSnoozeAction(context)) // Add the snooze action
                .build();

// Show the notification
        notificationManager.notify(0, notification);
    }


    // Create Snooze Action
    private NotificationCompat.Action createSnoozeAction(Context context) {
        Intent snoozeIntent = new Intent(context, NotificationReceiver.class);
        snoozeIntent.setAction(NotificationReceiver.ACTION_SNOOZE); // Use the ACTION_SNOOZE defined in NotificationReceiver
        snoozeIntent.putExtra("snooze_duration", 60000); // 1 minute snooze duration
        //remove the notification
        snoozeIntent.putExtra("remove_notification", true);
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
