package com.example.project1;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationScheduler {

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleTaskReminder(Context context, Task task, int snoozeDuration) {
        try {
            Log.d("NotificationScheduler", "Scheduling notification for task: " + task.toString());

            // Convert the reminder time string to Calendar
            String reminderTime = task.getReminderTime();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            calendar.setTime(sdf.parse(reminderTime));
            Log.d("NotificationScheduler", "Parsed reminder time: " + sdf.format(calendar.getTime()));

            // Calculate the current time
            Calendar now = Calendar.getInstance();

            // If snooze duration is provided, adjust the calendar time
            if (snoozeDuration > 0) {
                calendar.add(Calendar.MINUTE, snoozeDuration); // Add snooze duration to calendar
            }

            // If the new notification time is before the current time, adjust to the next day
            if (calendar.before(now)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1); // Move to the next day if snooze time has passed
            }

            // Create a pending intent to send a broadcast when the alarm goes off
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("task_title", task.getTitle());  // Send task info
            intent.putExtra("task_id", task.getId());
            intent.putExtra("task_description", task.getDescription());
            intent.putExtra("task_due_date", task.getDueDate());
            intent.putExtra("task_due_time", task.getDueTime());
            intent.putExtra("task_priority", task.getPriority());
            intent.putExtra("task_reminder_time", task.getReminderTime());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) task.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the AlarmManager system service
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Set the alarm to trigger at the chosen time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Log.d("NotificationScheduler", "Notification set for: " + task.getTitle() + " at " + sdf.format(calendar.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
