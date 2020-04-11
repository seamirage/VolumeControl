package com.trueapps.volumecontrol.notifications

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationsScheduler(private val context: Context) {

    fun enableNotifications() {
        //TODO: min value
        val periodHours:Long = 2

        val request = PeriodicWorkRequestBuilder<NotificationsWorker>(periodHours, TimeUnit.HOURS)
                .addTag(REMINDER_SERVICE_TAG)
                .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(REMINDER_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, request)
        Log.d(TAG, "Notifications are enabled")
    }

    fun disableNotifications() {
        WorkManager.getInstance(context)
                .cancelAllWorkByTag(REMINDER_SERVICE_TAG)
        Log.d(TAG, "Notifications are disabled")
    }

    companion object {
        private const val REMINDER_SERVICE_TAG = "volume_control_reminder_service"
        private const val REMINDER_WORK_NAME = "check_time_left_before_warning"
        private val TAG = NotificationsScheduler::class.java.simpleName
    }
}