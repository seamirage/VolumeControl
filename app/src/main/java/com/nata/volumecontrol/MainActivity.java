package com.nata.volumecontrol;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String AUDIO_SAFE_VOLUME_STATE = "audio_safe_volume_state";
    private static final String UNSAFE_VOLUME_MUSIC_ACTIVE_MS = "unsafe_volume_music_active_ms";

    private Button btnGetCurrent;
    private TextView tvCurrentState;
    private Button btnUpdateCurrent;
    private EditText etNewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetCurrent = findViewById(R.id.btnGetCurrentValue);
        btnUpdateCurrent = findViewById(R.id.btnUpdateCurrentValue);
        tvCurrentState = findViewById(R.id.tvCurrentState);
        etNewState = findViewById(R.id.etNewState);

        try {
            int currentState = getCurrentValue();
            tvCurrentState.setText(Integer.toString(currentState));
        } catch (Settings.SettingNotFoundException e) {
            Toast.makeText(this, "Could not load setting: " + e.getMessage(), Toast.LENGTH_LONG);
        }

        btnGetCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int currentState = getCurrentValue();
                    tvCurrentState.setText(Integer.toString(currentState));
                } catch (Settings.SettingNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Could not load setting: " + e.getMessage(), Toast.LENGTH_LONG);
                }
            }
        });

        btnUpdateCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putValue(Integer.parseInt(MainActivity.this.etNewState.getText().toString()));
            }
        });
    }

    private void putValue(int value) {
        //putAudioSafe(value);
        putTotalUnsafeMilliseconds(value);
    }

    private int getCurrentValue() throws Settings.SettingNotFoundException {
        //return getAudioSafe();
        return getUnsafeMIlliseconds();
    }

    private int getAudioSafe() throws Settings.SettingNotFoundException {
        return Settings.Global.getInt(getContentResolver(), AUDIO_SAFE_VOLUME_STATE);
    }

    private void putAudioSafe(int value) {
        Settings.Global.putInt(getContentResolver(),
                AUDIO_SAFE_VOLUME_STATE,
                value);
    }

    private void putTotalUnsafeMilliseconds(int value) {
        Settings.Secure.putInt(getContentResolver(), UNSAFE_VOLUME_MUSIC_ACTIVE_MS, value);
    }

    private int getUnsafeMIlliseconds() throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(getContentResolver(), UNSAFE_VOLUME_MUSIC_ACTIVE_MS);
    }
}
