package com.example.bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bluetooth.timeTable.DataTransfer;
import com.example.bluetooth.R;
//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;
//import com.velli20.materialunixgraph.Line;
//import com.velli20.materialunixgraph.LineGraph;
//import com.velli20.materialunixgraph.LinePoint;

import java.util.ArrayList;
import java.util.List;

public class DialogCreateTimeTable extends Dialog {
    private DataTransfer dataTransfer;


    private CheckBox checkBoxMonday, checkBoxTuesday, checkBoxWednesday, checkBoxThursday, checkBoxFriday, checkBoxSaturday, checkBoxSunday;
    private Switch switchRepetition;
    private EditText editTextSizePortion;
    private TimePicker timePicker;
    private Button buttonOk;

    //
    List<String> listCheckBox;
    boolean repetition;
    String sizePortion;
    String time;

    public DialogCreateTimeTable(Context context) {
        super(context);
        dataTransfer = (DataTransfer) context;
        setContentView(R.layout.layout_dialog_create_time_table);

        init();

//        GraphView graph = (GraphView) findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);

        ///

//        LineGraph graph = (LineGraph) findViewById(R.id.graph1);
//
//        long unixTimeNow = System.currentTimeMillis();
//        long oneDayInMillis = 1000 * 60 * 60 * 24;
//        boolean showLinePoints = true;
//
//        Line line = getDummyLine(unixTimeNow, unixTimeNow + oneDayInMillis, showLinePoints);
//        line.setLineColor(Color.parseColor("#FFFFFF"));
//        line.setFillLine(true);
//        line.setFillAlpha(60); /* Set alpha of the fill color 0-255 */
//        line.setLineStrokeWidth(2f);
//
//        graph.setMaxVerticalAxisValue(GRAPH_MAX_VERTICAL_VALUE);
//        graph.addLine(line);


//        //////////
//        List<PointValue> values = new ArrayList<>();
//        values.add(new PointValue(0, 10));
//        values.add(new PointValue(1, 4));
//        values.add(new PointValue(2, 3));
//        values.add(new PointValue(3, 4));
//
//        //In most cased you can call data model methods in builder-pattern-like manner.
//        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
//        List<Line> lines = new ArrayList<Line>();
//        lines.add(line);
//
//        LineChartData data = new LineChartData();
//        data.setLines(lines);
//
//        LineChartView chart = findViewById(R.id.chart);
//        chart.setLineChartData(data);
        // Встановлюємо слухача для кнопки OK
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

                if (listCheckBox.size() == 0 || sizePortion.length() == 0) {
                    Toast.makeText(context, "Невірно введені дані, спробуйте знову", Toast.LENGTH_SHORT).show();
                    listCheckBox.clear();
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

                    dataTransfer.createListTimeTable(listCheckBox, repetition, sizePortion, time);

                    dismiss();
                }
            }
        });
    }

    static final int GRAPH_MAX_VERTICAL_VALUE = 120;

//    public Line getDummyLine(long startDateInMillis, long endDateInMillis, boolean showPoints) {
//        Line line = new Line();
//        Random random = new Random();
//
//        LinePoint point;
//        for (int i = 0; i < 10; i++) {
//            long x = startDateInMillis + (((endDateInMillis - startDateInMillis) / 10) * i);
//
//            point = new LinePoint(x, random.nextInt(GRAPH_MAX_VERTICAL_VALUE));
//            point.setDrawPoint(showPoints);
//
//            line.addPoint(point);
//        }
//
//        return line;
//    }

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
}
