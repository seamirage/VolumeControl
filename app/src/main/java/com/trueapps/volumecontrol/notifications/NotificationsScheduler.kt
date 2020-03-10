package com.trueapps.volumecontrol.notifications

import android.content.Context
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger

class NotificationsScheduler(context: Context, private val flextimePercent: Double) {
    //TODO: DI
    private val dispatcher: FirebaseJobDispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))

    fun enableNotifications() {
        //TODO:
        val periodHours = 8
        val checkValueTimeSecondsEarliest = (periodHours * 60 * 60 * (1 - flextimePercent)).toInt()
        val checkValueTimeSecondsLatest = (periodHours * 60 * 60 * (1 + flextimePercent)).toInt()
        Log.d(TAG, "earliest $checkValueTimeSecondsEarliest")
        Log.d(TAG, "latest $checkValueTimeSecondsLatest")
        val reminderJob = dispatcher.newJobBuilder()
                .setService(NotificationsService::class.java)
                .setTag(REMINDER_SERVICE_TAG)
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(checkValueTimeSecondsEarliest, checkValueTimeSecondsLatest))
                .setLifetime(Lifetime.FOREVER)
                .build()
        dispatcher.mustSchedule(reminderJob)
    }

    fun disableNotifications() {
        dispatcher.cancel(REMINDER_SERVICE_TAG)
    }

    companion object {
        private const val REMINDER_SERVICE_TAG = "volume_control_reminder_service"
        private val TAG = NotificationsScheduler::class.java.simpleName
    }
}