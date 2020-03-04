package com.trueapps.volumecontrol.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.trueapps.volumecontrol.MainActivity;
import com.trueapps.volumecontrol.R;
import com.trueapps.volumecontrol.VolumeControlService;
import com.trueapps.volumecontrol.settings.DefaultSettings;
import com.trueapps.volumecontrol.settings.SettingsStorage;
import com.trueapps.volumecontrol.settings.SharedPreferencesSettingsStorage;

import java.util.concurrent.TimeUnit;

public class VolumeControlReminderService extends JobService {
    private static final String TAG = VolumeControlReminderService.class.getSimpleName();
    private static final int VOLUME_CONTROL_NOTIFICATION_ID = 4687;
    private static final int VOLUME_CONTROL_PENDING_INTENT_ID = 3574;
    private static final String VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID = "volume_control_reminder_channel";

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
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_warning)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.flush_reminder_notification_hours_only, hoursLeft))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(makeOpenAppIntent(this))
                .setChannelId(VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID);

        notificationManager.notify(VOLUME_CONTROL_NOTIFICATION_ID, mBuilder.build());
    }

    private PendingIntent makeOpenAppIntent(Context context) {
        Intent startMainActivityIntent = new Intent(context, MainActivity.class);
        startMainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(
                context,
                VOLUME_CONTROL_PENDING_INTENT_ID,
                startMainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private int getMillisecondsBeforeWarning() {
        int hoursBeforeWarning = settingsStorage.getMinTimeBeforeWarningInHours(DefaultSettings.MIN_TIME_BEFORE_WARNING_HOURS);
        return hoursBeforeWarning * 60 * 60 * 1000;
    }
}
