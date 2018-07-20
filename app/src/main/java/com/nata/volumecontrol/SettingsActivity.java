package com.nata.volumecontrol;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nata.volumecontrol.common.ui.NumberPickerDialog;

public class SettingsActivity extends AppCompatActivity {

    private Switch reminderSwitch;
    private LinearLayout settingsLayout;
    private TextView timeBeforeWarn;
    private TextView howOftenSetting;

    public static Intent makeStartIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ViewStub settingsStub = findViewById(R.id.view_stub_reminder_settings);
        settingsStub.inflate();

        settingsLayout = findViewById(R.id.layout_reminder_settings);
        settingsLayout.setVisibility(View.INVISIBLE);

        reminderSwitch = findViewById(R.id.switch_reminder);
        reminderSwitch.setOnCheckedChangeListener(listener);

        timeBeforeWarn = findViewById(R.id.tv_settings_min_time_before_warn);
        timeBeforeWarn.setText("3");
        timeBeforeWarn.setOnClickListener(new ChooseNumberClickListener(1, 19, new NumberPickerDialog.OnNumberSelectedListener() {
            @Override
            public void onNumberSelected(int selectedNumber) {
                Toast.makeText(SettingsActivity.this, "Selected: " + selectedNumber, Toast.LENGTH_SHORT).show();
            }
        }));
        howOftenSetting = findViewById(R.id.tv_settings_how_often_to_check);
        howOftenSetting.setText("8");
        howOftenSetting.setOnClickListener(new ChooseNumberClickListener(1, 24, new NumberPickerDialog.OnNumberSelectedListener() {
            @Override
            public void onNumberSelected(int selectedNumber) {
                Toast.makeText(SettingsActivity.this, "Selected: " + selectedNumber, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            settingsLayout.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        }
    };

    private class ChooseNumberClickListener implements View.OnClickListener {
        private int minValue;
        private int maxValue;
        private NumberPickerDialog.OnNumberSelectedListener onNumberSelectedListener;

        private ChooseNumberClickListener(int minValue, int maxValue, NumberPickerDialog.OnNumberSelectedListener onNumberSelectedListener) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.onNumberSelectedListener = onNumberSelectedListener;
        }

        @Override
        public void onClick(View v) {
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(SettingsActivity.this, minValue, maxValue, onNumberSelectedListener);
            numberPickerDialog.show();
        }
    }
}
