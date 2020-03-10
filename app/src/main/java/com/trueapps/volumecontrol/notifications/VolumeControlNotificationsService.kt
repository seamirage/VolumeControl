package com.trueapps.volumecontrol.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import androidx.core.app.NotificationCompat
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.trueapps.volumecontrol.MainActivity
import com.trueapps.volumecontrol.R
import com.trueapps.volumecontrol.VolumeControlSettingsService
import com.trueapps.volumecontrol.settings.SettingsStorage
import com.trueapps.volumecontrol.settings.SharedPreferencesSettingsStorage
import java.util.concurrent.TimeUnit

class VolumeControlNotificationsService : JobService() {
    private var volumeControlService: VolumeControlSettingsService? = null
    private var settingsStorage: SettingsStorage? = null
    override fun onCreate() {
        super.onCreate()
        //TODO: DI
        volumeControlService = VolumeControlSettingsService()
        //TODO: DI
        settingsStorage = SharedPreferencesSettingsStorage(this)
    }

    override fun onStartJob(job: JobParameters): Boolean {
        try {
            Log.d(TAG, "Started reminder check.")
            val unsafeMilliseconds = volumeControlService!!.getUnsafeMilliseconds(contentResolver)
            val remainingMilliseconds = volumeControlService!!.unsafeVolumeMusicActiveMsMax - unsafeMilliseconds
            val millisecondsBeforeWarningSetting = millisecondsBeforeWarning
            if (remainingMilliseconds <= millisecondsBeforeWarningSetting) {
                Log.d(TAG, "Min distance is reached, notification will be sent.")
                showNotification(TimeUnit.MILLISECONDS.toHours(remainingMilliseconds.toLong()))
            }
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Error at starting job: ", e)
        }
        return false
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false
    }

    //TODO: extract interface, e.g. NotificationService
    private fun showNotification(hoursLeft: Long) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val mBuilder = NotificationCompat.Builder(this, VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_warning)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.flush_reminder_notification_hours_only, hoursLeft))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(makeOpenAppIntent(this))
                .setChannelId(VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID)
        notificationManager.notify(VOLUME_CONTROL_NOTIFICATION_ID, mBuilder.build())
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

    private val millisecondsBeforeWarning: Int
        private get() {
            //TODO: 3 = DefaultSettings.MIN_TIME_BEFORE_WARNING_HOURS
            val hoursBeforeWarning = settingsStorage!!.getMinTimeBeforeWarningInHours(3)
            return hoursBeforeWarning * 60 * 60 * 1000
        }

    companion object {
        private val TAG = VolumeControlNotificationsService::class.java.simpleName
        private const val VOLUME_CONTROL_NOTIFICATION_ID = 4687
        private const val VOLUME_CONTROL_PENDING_INTENT_ID = 3574
        private const val VOLUME_CONTROL_NOTIFICATION_CHANNEL_ID = "volume_control_reminder_channel"
    }
}