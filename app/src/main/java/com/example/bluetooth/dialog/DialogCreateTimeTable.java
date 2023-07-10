package com.example.bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bluetooth.constFields.ConstFields;
import com.example.bluetooth.R;
//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;
//import com.velli20.materialunixgraph.Line;
//import com.velli20.materialunixgraph.LineGraph;
//import com.velli20.materialunixgraph.LinePoint;

import java.util.ArrayList;

public class DialogCreateTimeTable extends Dialog {

    private CheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday, checkBoxSunday;
    private Switch switchRepetition;
    private EditText editTextSizePortion;
    private TimePicker timePicker;
    private Button buttonOk;

    //
    private ArrayList<String> listCheckBox;
    private boolean repetition;
    private String sizePortion;
    private String time;

    public DialogCreateTimeTable(Context context) {
        super(context);
        setContentView(R.layout.layout_dialog_create_time_table);

        init();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBoxMonday.isChecked()) {
                    listCheckBox.add(checkBoxMonday.getText().toString());
                }
                if (checkBoxTuesday.isChecked()) {
                    listCheckBox.add(checkBoxTuesday.getText().toString());
                }
                if (checkBoxWednesday.isChecked()) {
                    listCheckBox.add(checkBoxWednesday.getText().toString());
                }
                if (checkBoxThursday.isChecked()) {
                    listCheckBox.add(checkBoxThursday.getText().toString());
                }
                if (checkBoxFriday.isChecked()) {
                    listCheckBox.add(checkBoxFriday.getText().toString());
                }
                if (checkBoxSaturday.isChecked()) {
                    listCheckBox.add(checkBoxSaturday.getText().toString());
                }
                if (checkBoxSunday.isChecked()) {
                    listCheckBox.add(checkBoxSunday.getText().toString());
                }

                if (switchRepetition.isChecked()) {
                    repetition = true;
                }
                // Отримуємо текст з поля введення
                sizePortion = editTextSizePortion.getText().toString();
                // Передаємо дані до активності за допомогою інтерфейсу або іншого способу
                // Закриваємо діалог

                if (listCheckBox.size() == 0 || sizePortion.length() == 0 || Integer.parseInt(sizePortion) > 99) {
                    Toast.makeText(context, "Невірно введені дані, спробуйте знову", Toast.LENGTH_SHORT).show();
                    listCheckBox.clear();
                } else {

                    String hour = getStringLess(timePicker.getHour());
                    String minute = getStringLess(timePicker.getMinute());

                    time = hour + ":" + minute;

//                    dataTransfer.createListTimeTable(listCheckBox, repetition, sizePortion, time);

                    Intent intent = new Intent(ConstFields.CREATE_LIST_TIME_TABLE);
                    intent.putStringArrayListExtra("listCheckBox", listCheckBox);
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
        checkBoxMonday = findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = findViewById(R.id.checkBoxTuesday);
        checkBoxWednesday = findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = findViewById(R.id.checkBoxThursday);
        checkBoxFriday = findViewById(R.id.checkBoxFriday);
        checkBoxSaturday = findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = findViewById(R.id.checkBoxSunday);

        switchRepetition = findViewById(R.id.switchRepetition);

        editTextSizePortion = findViewById(R.id.editTextSizePortion);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        buttonOk = findViewById(R.id.buttonOk);

        listCheckBox = new ArrayList<>();
        repetition = false;
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
}
