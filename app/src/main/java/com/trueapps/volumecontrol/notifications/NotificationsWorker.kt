package com.trueapps.volumecontrol.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.trueapps.volumecontrol.MainActivity
import com.trueapps.volumecontrol.R
import com.trueapps.volumecontrol.VolumeControlApplication
import com.trueapps.volumecontrol.SecureSettingsManager
import com.trueapps.volumecontrol.settings.SettingsStorage
import java.util.concurrent.TimeUnit

class NotificationsWorker(private val appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    private val secureSettings: SecureSettingsManager = VolumeControlApplication.Instance.dependenciesProvider.secureSettingsManager
    private val settingsStorage: SettingsStorage = VolumeControlApplication.Instance.dependenciesProvider.preferencesStorage
    private val notificationChannelId = appContext.getString(R.string.notifications_channel_main)

    override fun doWork(): Result {
        try {
            Log.d(TAG, "Started reminder check.")
            val unsafeMilliseconds = secureSettings.readUnsafeMilliseconds(appContext.contentResolver)
            val remainingMilliseconds = secureSettings.unsafeVolumeMusicActiveMsMax - unsafeMilliseconds
            val millisecondsBeforeWarningSetting = millisecondsBeforeWarning()
            // When unsafeMilliseconds == 0 it means that safe listening is in "unconfirmed" state
            if (remainingMilliseconds <= millisecondsBeforeWarningSetting || unsafeMilliseconds == 0) {
                Log.d(TAG, "Min distance is reached, notification will be sent.")
                showNotification(TimeUnit.MILLISECONDS.toHours(remainingMilliseconds.toLong()) + 1)
            }
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Error at starting job: ", e)
            return Result.failure(Data.Builder().putString("exception", e.toString()).build())
        }
        return Result.success()
    }

    private fun showNotification(hoursLeft: Long) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notification = buildNotification(hoursLeft)

        notificationManager.notify(VOLUME_CONTROL_NOTIFICATION_ID, notification)
    }

    private fun buildNotification(hoursLeft: Long): Notification? {
        return NotificationCompat.Builder(appContext, notificationChannelId)
                .setSmallIcon(R.drawable.icon_warning)
                .setContentTitle(appContext.getString(R.string.app_name))
                .setContentText(appContext.getString(R.string.notification_message, hoursLeft))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(makeOpenAppIntent(appContext))
                .setChannelId(notificationChannelId)
                .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val notificationChannel = NotificationChannel(
                notificationChannelId,
                appContext.getString(R.string.main_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun makeOpenAppIntent(context: Context): PendingIntent {
        val startMainActivityIntent = Intent(context, MainActivity::class.java)
        startMainActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
                context,
                VOLUME_CONTROL_PENDING_INTENT_ID,
                startMainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun millisecondsBeforeWarning(): Long {
            val hoursBeforeWarning = settingsStorage.minTimeBeforeWarningInHours
            return TimeUnit.HOURS.toMillis(hoursBeforeWarning.toLong())
        }

    companion object {
        private val TAG = NotificationsWorker::class.java.simpleName
        private const val VOLUME_CONTROL_NOTIFICATION_ID = 1
        private const val VOLUME_CONTROL_PENDING_INTENT_ID = 1
    }
}