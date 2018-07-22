package com.nata.volumecontrol.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.nata.volumecontrol.MainActivity;
import com.nata.volumecontrol.R;
import com.nata.volumecontrol.VolumeControlService;
import com.nata.volumecontrol.settings.DefaultSettings;
import com.nata.volumecontrol.settings.SettingsStorage;
import com.nata.volumecontrol.settings.SharedPreferencesSettingsStorage;

import java.util.concurrent.TimeUnit;

public class VolumeControlReminderService extends JobService {
    private static final String TAG = VolumeControlReminderService.class.getSimpleName();
    private static final String CHANNEL_ID = "volume_control_channel";
    private static final int NOTIFICATION_ID = 1;

    private VolumeControlService volumeControlService;
    private SettingsStorage settingsStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: DI
        volumeControlService = new VolumeControlService();
        //TODO: DI
        settingsStorage = new SharedPreferencesSettingsStorage(this);
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        try {
            Log.d(TAG, "Started reminder check.");
            int unsafeMilliseconds = volumeControlService.getUnsafeMilliseconds(getContentResolver());
            int remainingMilliseconds = volumeControlService.getMaxUnsafeMusicPlayDuration() - unsafeMilliseconds;
            int millisecondsBeforeWarningSetting = getMillisecondsBeforeWarning();

            if (remainingMilliseconds <= millisecondsBeforeWarningSetting) {
                Log.d(TAG, "Min distance is reached, notification will be sent.");
                showNotification(TimeUnit.MILLISECONDS.toHours(remainingMilliseconds));
            }

        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error at starting job: ", e);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    //TODO: extract interface, e.g. NotificationService

    private void showNotification(long hoursLeft) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_warning)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.flush_reminder_notification_hours_only, hoursLeft))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(makeOpenAppIntent(this));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private PendingIntent makeOpenAppIntent(Context context) {
        Intent startMainActivityIntent = new Intent(context, MainActivity.class);
        startMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(
                context,
                0,
                startMainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "volume_control_channel";
            String description = "volume_control_channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private int getMillisecondsBeforeWarning() {
        int hoursBeforeWarning = settingsStorage.getMinTimeBeforeWarningInHours(DefaultSettings.MIN_TIME_BEFORE_WARNING_HOURS);
        return hoursBeforeWarning * 60 * 60 * 1000;
    }
}
