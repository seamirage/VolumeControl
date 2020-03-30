package com.trueapps.volumecontrol

import android.app.Application
import android.content.Context
import com.trueapps.volumecontrol.notifications.NotificationsScheduler
import com.trueapps.volumecontrol.settings.SharedPreferencesSettingsStorage

class VolumeControlApplication : Application() {
    lateinit var dependenciesProvider: DependenciesProvider private set

    override fun onCreate() {
        super.onCreate()
        Instance = this
        dependenciesProvider = DependenciesProvider(applicationContext)

        if (dependenciesProvider.preferencesStorage.showNotifications) {
            dependenciesProvider.notificationsScheduler.enableNotifications()
        } else {
            dependenciesProvider.notificationsScheduler.disableNotifications()
        }
    }

    companion object {
        lateinit var Instance: VolumeControlApplication
    }
}

class DependenciesProvider(appContext: Context) {
    val notificationsScheduler = NotificationsScheduler(appContext)
    val preferencesStorage = SharedPreferencesSettingsStorage(appContext)
}