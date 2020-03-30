package com.trueapps.volumecontrol.notifications

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NotificationsScheduler(private val context: Context) {

    fun enableNotifications() {
        //TODO: min value
        val periodHours:Long = 2

        val constraints = Constraints.Builder()
                .setRequiresCharging(true)
                .build()

        //TODO: !!!
        //val request = PeriodicWorkRequestBuilder<NotificationsWorker>(periodHours, TimeUnit.HOURS)
        val request = PeriodicWorkRequestBuilder<NotificationsWorker>(15, TimeUnit.MINUTES)
                .addTag(REMINDER_SERVICE_TAG)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context).enqueue(request)
        Log.d(TAG, "Notifications are enabled")
    }

    fun disableNotifications() {
        WorkManager.getInstance(context)
                .cancelAllWorkByTag(REMINDER_SERVICE_TAG)
        Log.d(TAG, "Notifications are disabled")
    }

    companion object {
        private const val REMINDER_SERVICE_TAG = "volume_control_reminder_service"
        private val TAG = NotificationsScheduler::class.java.simpleName
    }
}