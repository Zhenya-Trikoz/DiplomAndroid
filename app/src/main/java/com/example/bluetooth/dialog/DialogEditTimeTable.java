package com.example.bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bluetooth.constFields.ConstFields;
import com.example.bluetooth.R;
import com.example.bluetooth.timeTable.ListTimeTable;

public class DialogEditTimeTable extends Dialog {
    private ListTimeTable listTimeTable;

    private Switch switchRepetition;
    private EditText editTextSizePortion;
    private TimePicker timePicker;
    private Button buttonOk;

    private boolean repetition;
    private String sizePortion;
    private String time;

    public DialogEditTimeTable(@NonNull Context context) {
        super(context);
        setContentView(R.layout.layout_dialog_edit_time_table);

        init();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                repetition = switchRepetition.isChecked();

                sizePortion = editTextSizePortion.getText().toString();
                if (sizePortion.length() == 0 || Integer.parseInt(sizePortion) > 99) {
                    Toast.makeText(context, "Невірно введені дані, спробуйте знову", Toast.LENGTH_SHORT).show();
                } else {
                    String hour = getStringLess(timePicker.getHour());
                    String minute = getStringLess(timePicker.getMinute());

                    time = hour + ":" + minute;

                    Intent intent = new Intent(ConstFields.EDIT_LIST_TIME_TABLE);
                    intent.putExtra("id", listTimeTable.getId());
                    intent.putExtra("repetition", repetition);
                    intent.putExtra("sizePortion", sizePortion);
                    intent.putExtra("time", time);
                    context.sendBroadcast(intent);
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
    public String getStringLess(int number) {
        String s;
        if (number <= 9) {
            s = "0" + number;
        } else {
            s = String.valueOf(number);
        }
        return s;
    }
    public void data(ListTimeTable listTimeTable) {
        this.listTimeTable = listTimeTable;

        editTextSizePortion.setText(listTimeTable.getSizePortion());
        switchRepetition.setChecked(listTimeTable.isRepetition());
        timePicker.setHour(Integer.parseInt(listTimeTable.getTime().substring(0, 2)));
        timePicker.setMinute(Integer.parseInt(listTimeTable.getTime().substring(3)));
    }


}
