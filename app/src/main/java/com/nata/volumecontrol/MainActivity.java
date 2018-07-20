package com.nata.volumecontrol;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar progressCurrent;
    private Button btnRefresh;
    private Button btnFlush;
    private TextView tvTotalPlayed;
    private TextView tvMaxDuration;

    private VolumeControlService volumeControlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volumeControlService = new VolumeControlService();

        progressCurrent = findViewById(R.id.progress_current);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnFlush = findViewById(R.id.btn_flush);
        tvTotalPlayed = findViewById(R.id.tv_total_played);
        tvMaxDuration = findViewById(R.id.tv_max_duration);
        tvMaxDuration.setText(durationToText(volumeControlService.getMaxUnsafeMusicPlayDuration()));

        updateProgressBar();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProgressBar();
            }
        });

        btnFlush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volumeControlService.putTotalUnsafeMilliseconds(MainActivity.this.getContentResolver(), 1);
                updateProgressBar();
            }
        });

        ReminderServiceScheduler reminderScheduler = new ReminderServiceScheduler(getApplicationContext(), 0.1);
        //TODO: load hours from preferences
        reminderScheduler.scheduleReminderService(8);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent startSettingsActivityIntent = SettingsActivity.makeStartIntent(this);
                startActivity(startSettingsActivityIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateProgressBar() {
        try {
            int currentState = volumeControlService.getUnsafeMilliseconds(getContentResolver());
            tvTotalPlayed.setText(durationToText(currentState));
            progressCurrent.setMax(volumeControlService.getMaxUnsafeMusicPlayDuration());
            progressCurrent.setProgress(currentState);

        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Could not load setting", e);
            Toast.makeText(this,"Could not load current value.", Toast.LENGTH_SHORT).show();
        }
    }

    private String durationToText(int duration) {
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - hours * 60;

        return String.format("%02d:%02d", hours, minutes);
    }
}
