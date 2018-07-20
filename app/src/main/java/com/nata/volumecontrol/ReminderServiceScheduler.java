package com.nata.volumecontrol;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

public class ReminderServiceScheduler {
    private static final String REMINDER_SERVICE_TAG = "volume_control_reminder_service";
    private static final String TAG = ReminderServiceScheduler.class.getSimpleName();

    private double allowedInaccuracy;
    private FirebaseJobDispatcher dispatcher;

    public ReminderServiceScheduler(Context context, double allowedTimePercent) {
        this.allowedInaccuracy = allowedTimePercent;
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
    }

    public void scheduleReminderService(int periodHours) {
        int checkValueTimeSecondsEarliest = (int)(periodHours * 60 * 60 * (1 - allowedInaccuracy));
        int checkValueTimeSecondsLatest = (int) (periodHours * 60 * 60 * (1 + allowedInaccuracy));
        Log.d(TAG, "earliest " + checkValueTimeSecondsEarliest);
        Log.d(TAG, "latest " + checkValueTimeSecondsLatest);

        Job reminderJob = dispatcher.newJobBuilder()
                .setService(VolumeControlReminderService.class)
                .setTag(REMINDER_SERVICE_TAG)
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(checkValueTimeSecondsEarliest, checkValueTimeSecondsLatest))
                .setLifetime(Lifetime.FOREVER)
                .build();

        dispatcher.mustSchedule(reminderJob);
    }
}
