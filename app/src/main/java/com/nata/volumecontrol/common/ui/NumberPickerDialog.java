package com.nata.volumecontrol.common.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

public class NumberPickerDialog {
    private int minValue;
    private int maxValue;
    private Context context;
    private OnNumberSelectedListener onNumberSelectedListener;
    private AlertDialog dialog;

    public interface OnNumberSelectedListener {
        void onNumberSelected(int selectedNumber);
    }

    public NumberPickerDialog(Context context, int minValue, int maxValue, int currentValue, final OnNumberSelectedListener onNumberSelectedListener) {
        this.context = context;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.onNumberSelectedListener = onNumberSelectedListener;

        final NumberPicker picker = new NumberPicker(context);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(currentValue);

        final FrameLayout layout = new FrameLayout(context);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));

        dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onNumberSelectedListener.onNumberSelected(picker.getValue());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null).create();
    }

    public void show() {
        dialog.show();
    }
}
