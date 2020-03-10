package com.trueapps.volumecontrol.notifications;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.trueapps.volumecontrol.settings.SettingsStorage;
import com.trueapps.volumecontrol.settings.SharedPreferencesSettingsStorage;

public class NotificationsScheduler {
    private static final String REMINDER_SERVICE_TAG = "volume_control_reminder_service";
    private static final String TAG = NotificationsScheduler.class.getSimpleName();

    private double allowedInaccuracy;
    private FirebaseJobDispatcher dispatcher;
    private SettingsStorage settingsStorage;

    public NotificationsScheduler(Context context, double allowedTimePercent) {
        this.allowedInaccuracy = allowedTimePercent;
        //TODO: DI
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        //TODO: DI
        settingsStorage = new SharedPreferencesSettingsStorage(context);
    }

    public void scheduleReminderService() {
        //TODO:
        int periodHours = 8;
        int checkValueTimeSecondsEarliest = (int)(periodHours * 60 * 60 * (1 - allowedInaccuracy));
        int checkValueTimeSecondsLatest = (int) (periodHours * 60 * 60 * (1 + allowedInaccuracy));
        Log.d(TAG, "earliest " + checkValueTimeSecondsEarliest);
        Log.d(TAG, "latest " + checkValueTimeSecondsLatest);

        Job reminderJob = dispatcher.newJobBuilder()
                .setService(VolumeControlNotificationsService.class)
                .setTag(REMINDER_SERVICE_TAG)
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(checkValueTimeSecondsEarliest, checkValueTimeSecondsLatest))
                .setLifetime(Lifetime.FOREVER)
                .build();

        dispatcher.mustSchedule(reminderJob);
    }

    public void cancelReminding() {
        dispatcher.cancel(REMINDER_SERVICE_TAG);
    }
}
