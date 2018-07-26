package com.nata.volumecontrol.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.nata.volumecontrol.R;
import com.nata.volumecontrol.common.ui.NumberPickerDialog;
import com.nata.volumecontrol.reminder.ReminderServiceScheduler;

public class SettingsActivity extends AppCompatActivity {

    private Switch reminderSwitch;
    private LinearLayout settingsLayout;
    private TextView timeBeforeWarn;
    private TextView howOftenSetting;
    private int howOftenToCheckInHours;
    private int minTimeBeforeWarningInHours;

    private SettingsStorage settingsStorage;
    private ReminderServiceScheduler reminderScheduler;

    public static Intent makeStartIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //TODO: DI, Singleton (?)
        reminderScheduler = new ReminderServiceScheduler(this, 0.1);

        //TODO: DI
        settingsStorage = new SharedPreferencesSettingsStorage(this);
        final ViewStub settingsStub = findViewById(R.id.view_stub_reminder_settings);
        settingsStub.inflate();

        settingsLayout = findViewById(R.id.layout_reminder_settings);
        settingsLayout.setVisibility(View.INVISIBLE);

        reminderSwitch = findViewById(R.id.switch_reminder);
        reminderSwitch.setOnCheckedChangeListener(listener);
        reminderSwitch.setChecked(settingsStorage.isRemindingEnabled());

        timeBeforeWarn = findViewById(R.id.tv_settings_min_time_before_warn);
        minTimeBeforeWarningInHours = settingsStorage.getMinTimeBeforeWarningInHours(DefaultSettings.MIN_TIME_BEFORE_WARNING_HOURS);
        timeBeforeWarn.setText(String.valueOf(minTimeBeforeWarningInHours));
        timeBeforeWarn.setOnClickListener(new ChooseNumberClickListener(
                1,
                19,
                new CurrentValueProvider() {
                    @Override
                    public int getCurrent() {
                        return settingsStorage.getMinTimeBeforeWarningInHours(DefaultSettings.MIN_TIME_BEFORE_WARNING_HOURS);
                    }
            }, new NumberPickerDialog.OnNumberSelectedListener() {
                @Override
                public void onNumberSelected(int selectedNumber) {
                    settingsStorage.putMinTimeBeforeWarningInHours(selectedNumber);
                    timeBeforeWarn.setText(String.valueOf(selectedNumber));
                }
            }));

        howOftenSetting = findViewById(R.id.tv_settings_how_often_to_check);
        howOftenToCheckInHours = settingsStorage.getHowOftenToCheckInHours(DefaultSettings.HOW_OFTEN_TO_CHECK_HOURS);
        howOftenSetting.setText(String.valueOf(howOftenToCheckInHours));
        howOftenSetting.setOnClickListener(new ChooseNumberClickListener(
                1,
                24,
                new CurrentValueProvider() {
                    @Override
                    public int getCurrent() {
                        return settingsStorage.getHowOftenToCheckInHours(DefaultSettings.HOW_OFTEN_TO_CHECK_HOURS);
                    }
            }, new NumberPickerDialog.OnNumberSelectedListener() {
                @Override
                public void onNumberSelected(int selectedNumber) {
                    settingsStorage.putHowOftenToCheckInHours(selectedNumber);
                    howOftenSetting.setText(String.valueOf(selectedNumber));
                    reminderScheduler.scheduleReminderService();
                }
            }));
    }

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked)  {
                reminderScheduler.scheduleReminderService();
                settingsStorage.putIsRemindingEnabled(true);
                settingsLayout.setVisibility(View.VISIBLE);
            } else {
                reminderScheduler.cancelReminding();
                settingsStorage.putIsRemindingEnabled(false);
                settingsLayout.setVisibility(View.INVISIBLE);
            }
        }
    };

    private class ChooseNumberClickListener implements View.OnClickListener {
        private int minValue;
        private int maxValue;
        private CurrentValueProvider currentValueProvider;
        private NumberPickerDialog.OnNumberSelectedListener onNumberSelectedListener;

        private ChooseNumberClickListener(int minValue, int maxValue, CurrentValueProvider currentValueProvider, NumberPickerDialog.OnNumberSelectedListener onNumberSelectedListener) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.currentValueProvider = currentValueProvider;
            this.onNumberSelectedListener = onNumberSelectedListener;
        }

        @Override
        public void onClick(View v) {
            NumberPickerDialog numberPickerDialog = new NumberPickerDialog(SettingsActivity.this, minValue, maxValue, currentValueProvider.getCurrent(), onNumberSelectedListener);
            numberPickerDialog.show();
        }
    }

    interface CurrentValueProvider {
        int getCurrent();
    }
}
