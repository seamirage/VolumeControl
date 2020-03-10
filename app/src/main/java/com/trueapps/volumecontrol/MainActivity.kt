package com.trueapps.volumecontrol

import android.os.Bundle
import android.provider.Settings.SettingNotFoundException
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.trueapps.volumecontrol.settings.SettingsActivity
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var progressCurrent: ProgressBar
    private lateinit var btnRefresh: Button
    private lateinit var btnFlush: Button
    private lateinit var tvTotalPlayed: TextView
    private lateinit var tvMaxDuration: TextView

    private lateinit var volumeControlService: VolumeControlSettingsService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        volumeControlService = VolumeControlSettingsService()

        progressCurrent = findViewById(R.id.progress_current)
        btnRefresh = findViewById(R.id.btn_refresh)
        btnFlush = findViewById(R.id.btn_flush)
        tvTotalPlayed = findViewById(R.id.tv_total_played)
        tvMaxDuration = findViewById(R.id.tv_max_duration)
        tvMaxDuration.text = durationToText(volumeControlService.unsafeVolumeMusicActiveMsMax)

        updateProgressBar()
        btnRefresh.setOnClickListener { updateProgressBar() }
        btnFlush.setOnClickListener {
            volumeControlService.putTotalUnsafeMilliseconds(this@MainActivity.contentResolver, 1)
            updateProgressBar()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val startSettingsActivityIntent = SettingsActivity.makeStartIntent(this)
                startActivity(startSettingsActivityIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateProgressBar() {
        try {
            val currentState = volumeControlService.getUnsafeMilliseconds(contentResolver)
            tvTotalPlayed.text = durationToText(currentState)
            progressCurrent.max = volumeControlService.unsafeVolumeMusicActiveMsMax
            progressCurrent.progress = currentState
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Could not load setting", e)
            Toast.makeText(this, "Could not load current value.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun durationToText(duration: Int): String {
        val hours = TimeUnit.MILLISECONDS.toHours(duration.toLong())
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong()) - hours * 60
        return String.format("%02d:%02d", hours, minutes)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}