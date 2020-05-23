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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.trueapps.volumecontrol.settings.SettingsActivity
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var progressCurrent: ProgressBar
    private lateinit var btnReset: Button
    private lateinit var tvRemainingTimeDisplay: TextView
    private lateinit var tvRemainingTime: TextView
    private lateinit var tvMaxDuration: TextView

    private lateinit var secureSettings: SecureSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        secureSettings = SecureSettingsManager()

        progressCurrent = findViewById(R.id.progress_current)
        progressCurrent.rotation = 180f
        btnReset = findViewById(R.id.btn_flush)
        tvRemainingTimeDisplay = findViewById(R.id.tv_remaining_time_display)
        tvRemainingTime = findViewById(R.id.tv_remaining_time_small)
        tvMaxDuration = findViewById<TextView>(R.id.tv_max_duration).apply {
            text = durationToText(secureSettings.unsafeVolumeMusicActiveMsMax, R.string.duration_pattern)
        }

        btnReset.setOnClickListener {
            resetUnsafeMillis()
            updateProgressBar()
            showRebootReminderDialog()
        }
    }

    private fun resetUnsafeMillis() {
        try {
            secureSettings.updateTotalUnsafeMilliseconds(this@MainActivity.contentResolver, 1)
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception: no permissions to write secure settings", e)
            showPermissionRequiredDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        updateProgressBar()
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
            val remainingTime = secureSettings.unsafeVolumeMusicActiveMsMax - secureSettings.readUnsafeMilliseconds(contentResolver)
            tvRemainingTimeDisplay.text = durationToText(remainingTime, R.string.display_duration_pattern)
            tvRemainingTime.text = durationToText(remainingTime, R.string.duration_pattern)
            progressCurrent.max = secureSettings.unsafeVolumeMusicActiveMsMax
            progressCurrent.progress = remainingTime
        } catch (e: SettingNotFoundException) {
            Log.e(TAG, "Could not load setting", e)
            Toast.makeText(this, "Could not load current value.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRebootReminderDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.attention)
                .setMessage(R.string.remind_to_reboot_message)
                .setPositiveButton(R.string.ok) { dialog, id -> }
                .show()
    }

    private fun showPermissionRequiredDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.no_permission)
                .setMessage(getString(R.string.grant_permission_message))
                .setPositiveButton(R.string.ok) { dialog, id -> }
                .show()
    }

    private fun durationToText(duration: Int, patternResId: Int): String {
        val hours = TimeUnit.MILLISECONDS.toHours(duration.toLong())
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong()) - hours * 60
        return String.format(getString(patternResId), hours, minutes)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}