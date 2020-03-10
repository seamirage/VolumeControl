package com.trueapps.volumecontrol.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trueapps.volumecontrol.R


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    companion object {
        fun makeStartIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}