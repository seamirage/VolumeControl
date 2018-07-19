package com.nata.volumecontrol;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.concurrent.TimeUnit;

public class VolumeControlReminderService extends JobService {
    private static final String TAG = VolumeControlReminderService.class.getSimpleName();
    private static final String CHANNEL_ID = "volume_control_channel";
    private static final int NOTIFICATION_ID = 346;

    private VolumeControlService volumeControlService;


    @Override
    public void onCreate() {
        super.onCreate();
        volumeControlService = new VolumeControlService();
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        try {
            Log.d(TAG, "Started reminder check.");
            int unsafeMilliseconds = volumeControlService.getUnsafeMilliseconds(getContentResolver());
            int remainingMilliseconds = volumeControlService.getMaxUnsafeMusicPlayDuration() - unsafeMilliseconds;
            //Read from preferences
            int hours = 10;
            int minDistance = hours *60 *60 *1000; // 10 hours

            if (remainingMilliseconds <= minDistance) {
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
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
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
}
