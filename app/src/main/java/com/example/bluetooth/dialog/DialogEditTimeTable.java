package com.example.bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bluetooth.timeTable.DataTransfer;
import com.example.bluetooth.R;
import com.example.bluetooth.timeTable.ListTimeTable;

public class DialogEditTimeTable extends Dialog {
    private DataTransfer dataTransfer;
    private ListTimeTable listTimeTable;

    private Switch switchRepetition;
    private EditText editTextSizePortion;
    private TimePicker timePicker;
    private Button buttonOk;

    boolean repetition;
    String sizePortion;
    String time;

    public DialogEditTimeTable(@NonNull Context context) {
        super(context);
        dataTransfer = (DataTransfer) context;
        setContentView(R.layout.layout_dialog_edit_time_table);

        init();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                repetition = switchRepetition.isChecked();

                sizePortion = editTextSizePortion.getText().toString();
                if (sizePortion.length() == 0) {
                    Toast.makeText(context, "Невірно введені дані, спробуйте знову", Toast.LENGTH_SHORT).show();
                } else {
                    String hour, minute;
                    if (timePicker.getHour() <= 9) {
                        hour = "0" + timePicker.getHour();
                    } else {
                        hour = String.valueOf(timePicker.getHour());
                    }

                    if (timePicker.getMinute() <= 9) {
                        minute = "0" + timePicker.getMinute();
                    } else {
                        minute = String.valueOf(timePicker.getMinute());
                    }

                    time = hour + ":" + minute;


                    dataTransfer.editListTimeTable(listTimeTable.getId(), repetition, sizePortion, time);
                    dismiss();
                }

            }
        });
    }

    public void init() {

        switchRepetition = findViewById(R.id.switchRepetition);

        editTextSizePortion = findViewById(R.id.editTextSizePortion);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        buttonOk = findViewById(R.id.buttonOk);

    }

    public void data(ListTimeTable listTimeTable) {
        this.listTimeTable = listTimeTable;

        editTextSizePortion.setText(listTimeTable.getSizePortion());
        switchRepetition.setChecked(listTimeTable.isRepetition());
        timePicker.setHour(Integer.parseInt(listTimeTable.getTime().substring(0, 2)));
        timePicker.setMinute(Integer.parseInt(listTimeTable.getTime().substring(3)));
    }


}
