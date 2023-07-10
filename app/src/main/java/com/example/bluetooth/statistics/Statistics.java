package com.example.bluetooth.statistics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth.R;
import com.example.bluetooth.constFields.ConstFields;
import com.example.bluetooth.csv.DataRead_Write;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Statistics extends AppCompatActivity {

    private List<ListDiagramSizePortion> listDiagramSizePortions; //Список використання корму в день

    private BarChart barChart;

    private TextView textStatusPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        init();

        scheduleUsedFeed();

    }

    public void init() {
        Intent intent = new Intent(ConstFields.ACTION_MEASURE_WEIGHT_SEND);
        sendBroadcast(intent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstFields.ACTION_MEASURE_WEIGHT_READ);
        registerReceiver(broadcastReceiverStatistic, intentFilter);

        listDiagramSizePortions = new ArrayList<>();

        listDiagramSizePortions = DataRead_Write.dataReadListDiagramSizePortion(getApplicationContext());
        textStatusPlate = findViewById(R.id.textViewStatusPlate);

        barChart = findViewById(R.id.barChart);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(true);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(true);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisLeft().setAxisMinimum(0f);
    }

    public void scheduleUsedFeed() {
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < listDiagramSizePortions.size(); i++) {

            entries.add(new BarEntry(i, listDiagramSizePortions.get(i).getSumUsedFeed()));
        }

        String[] massWeekDay = new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд"};

        if (!listDiagramSizePortions.get(0).getWeekDay().equals("Пн")) {
            int pos = 0;
            String[] buffMass = new String[massWeekDay.length];
            for (int i = 0; i < massWeekDay.length; i++) {
                if (massWeekDay[i].equals(listDiagramSizePortions.get(0).getWeekDay())) {
                    pos = i;
                    break;
                }
                buffMass[i] = massWeekDay[i];
            }
            System.arraycopy(massWeekDay, pos, massWeekDay, 0, massWeekDay.length - pos); // зсуваємо елементи
            System.arraycopy(buffMass, 0, massWeekDay, massWeekDay.length - pos, pos); // додаємо збережені елементи
        }


        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(massWeekDay));
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд"}));
        // Встановлюємо мінімальне значення на осі X
        xAxis.setAxisMinimum(-1f);
        // Встановлюємо максимальне значення на осі X
        xAxis.setAxisMaximum(7f);

        int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        BarDataSet dataSet = new BarDataSet(entries, "Label");
//        dataSet.setColors(colors, getApplicationContext());
        dataSet.setValueTextSize(20);
//        dataSet.setColors(colors);
        BarData lineData = new BarData(dataSet);

        barChart.setData(lineData);
        barChart.invalidate(); // refresh

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_static, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_downloadStatistic) {
            DataRead_Write.dataCopyStatistic(getApplicationContext());
            return true;
        } else if (id == R.id.action_addition) {
            additionPlate();
        }
        return super.onOptionsItemSelected(item);
    }

    public void additionPlate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Statistics.this);
        builder.setTitle("Поповнення миски");

        View view = LayoutInflater.from(Statistics.this).inflate(R.layout.layout_dialog_password, null);
        builder.setView(view);

        EditText editText = view.findViewById(R.id.editTextPassword);
        editText.setHint("Введіть порцію у грамах");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String additionPlate = editText.getText().toString();
                Toast.makeText(getApplicationContext(), "addPlate: " + additionPlate, Toast.LENGTH_SHORT).show();

                if (additionPlate.length() == 0 || Integer.parseInt(additionPlate) > 99) {
                    Toast.makeText(getApplicationContext(), "Невірно введені дані, спробуйте знову", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    for (int i = 0; i < listDiagramSizePortions.size(); i++) {
                        if (listDiagramSizePortions.get(i).getWeekDay().equals(getWeekDayString(calendar.get(Calendar.DAY_OF_WEEK)))) {
                            listDiagramSizePortions.get(i).setSumUsedFeed(listDiagramSizePortions.get(i).getSumUsedFeed() + Integer.parseInt(additionPlate));
                            scheduleUsedFeed();
                            saveStatistic(calendar, additionPlate);
                            DataRead_Write.dataWriteListDiagramSizePortion(getApplicationContext(), listDiagramSizePortions);
                            break;
                        }
                    }


//                    int feed = Integer.parseInt(textStatusPlate.getText().toString()) + Integer.parseInt(additionPlate);
//
//                    Log.d(ConstFields.TAG, "feed: " + textStatusPlate.getText().toString().length());
//                    textStatusPlate.setText(String.valueOf(feed));

                    Intent intent = new Intent(ConstFields.ACTION_ADDITION_SEND);
                    intent.putExtra("feed", additionPlate);
                    sendBroadcast(intent);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveStatistic(Calendar calendar, String additionPlate) {
        String day = getStringLess(calendar.get(Calendar.DAY_OF_MONTH));
        String month = getStringLess(calendar.get(Calendar.MONTH) + 1);

        String data = day + ":" + month + ":" + calendar.get(Calendar.YEAR);

        String hour = getStringLess(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = getStringLess(calendar.get(Calendar.MINUTE));

        String time = hour + ":" + minute;
        DataRead_Write.dataWriteStatistic(getApplicationContext(), data, time, additionPlate);
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

    public String getWeekDayString(int i) {

        switch (i) {
            case Calendar.MONDAY:
                return "Пн";
            case Calendar.TUESDAY:
                return "Вт";
            case Calendar.WEDNESDAY:
                return "Ср";
            case Calendar.THURSDAY:
                return "Чт";
            case Calendar.FRIDAY:
                return "Пт";
            case Calendar.SATURDAY:
                return "Сб";
            case Calendar.SUNDAY:
                return "Нд";
        }
        return "";
    }

    BroadcastReceiver broadcastReceiverStatistic = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstFields.ACTION_MEASURE_WEIGHT_READ)) {
                String weight = intent.getStringExtra(ConstFields.ACTION_MEASURE_WEIGHT_READ);
                textStatusPlate.setText(weight);
            }
        }
    };
}
