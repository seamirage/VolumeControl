package com.nata.volumecontrol;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = (20 * 3600 * 1000); // 20 hours
    private static final String UNSAFE_VOLUME_MUSIC_ACTIVE_MS = "unsafe_volume_music_active_ms";

    private ProgressBar progressCurrent;
    private Button btnFlush;
    private TextView tvTotalPlayed;
    private TextView tvMaxDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressCurrent = findViewById(R.id.progress_current);
        btnFlush = findViewById(R.id.btn_flush);
        tvTotalPlayed = findViewById(R.id.tv_total_played);
        tvMaxDuration = findViewById(R.id.tv_max_duration);
        tvMaxDuration.setText(durationToText(UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX));

        updateProgressBar();

        btnFlush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putTotalUnsafeMilliseconds(1);
                updateProgressBar();
            }
        });
    }

    private void updateProgressBar() {
        try {
            int currentState = getUnsafeMilliseconds();
            tvTotalPlayed.setText(durationToText(currentState));
            progressCurrent.setMax(UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX);
            progressCurrent.setProgress(currentState);

        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Could not load setting", e);
            Toast.makeText(this,"Could not load current value.", Toast.LENGTH_SHORT).show();
        }
    }

    private int getUnsafeMilliseconds() throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(getContentResolver(), UNSAFE_VOLUME_MUSIC_ACTIVE_MS);
    }

    private void putTotalUnsafeMilliseconds(int value) {
        Settings.Secure.putInt(getContentResolver(), UNSAFE_VOLUME_MUSIC_ACTIVE_MS, value);
    }

    private String durationToText(int duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - hours * 60;

        return String.format("%02d:%02d", hours, minutes);
    }
}
