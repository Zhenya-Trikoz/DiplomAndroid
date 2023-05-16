package com.example.bluetooth.statistics;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth.DataRead_Write;
import com.example.bluetooth.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Static extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    Gson gson;
    List<ListDiagramSIzePortion> listDiagramSIzePortions; //Список використання корму в день

    BarChart barChart;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static);

        init();

        barChart = findViewById(R.id.barChart);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(true);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(true);
        barChart.getAxisRight().setEnabled(false);
        // Отримання об'єкту XAxis
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        List<BarEntry> entries = new ArrayList<>();

//        listDiagramSIzePortions.add(new ListDiagramSIzePortion("Вт", List.of(25, 45)));
//        listDiagramSIzePortions.add(new ListDiagramSIzePortion("Ср", List.of(45)));
//        listDiagramSIzePortions.add(new ListDiagramSIzePortion("Чт", List.of(10, 45, 66)));
//        listDiagramSIzePortions.add(new ListDiagramSIzePortion("Пт", List.of(5, 10, 45)));
//        listDiagramSIzePortions.add(new ListDiagramSIzePortion("Сб", List.of(35, 45)));
//        listDiagramSIzePortions.add(new ListDiagramSIzePortion("Нд", List.of(55, 25, 45)));

        for (int i = 0; i < listDiagramSIzePortions.size(); i++) {

            float[] mass = new float[listDiagramSIzePortions.get(i).getListSizePortion().size()];
            for (int a = 0; a < listDiagramSIzePortions.get(i).getListSizePortion().size(); a++) {
                mass[a] = listDiagramSIzePortions.get(i).getListSizePortion().get(a);
            }
            entries.add(new BarEntry(i, mass));

        }

        String[] massWeekDay = new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд"};

        if (!listDiagramSIzePortions.get(0).getWeekDay().equals("Пн")) {
            int pos = 0;
            String[] buffMass = new String[massWeekDay.length];
            for (int i = 0; i < massWeekDay.length; i++) {
                if (massWeekDay[i].equals(listDiagramSIzePortions.get(0).getWeekDay())) {
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


//        entries.add(new BarEntry(0, 55));

//        entries.add(new BarEntry(1, 40));
//        entries.add(new BarEntry(2, 13));
//        entries.add(new BarEntry(3, 110));
//        entries.add(new BarEntry(4, 99));
//        entries.add(new BarEntry(5, 5));
//        entries.add(new BarEntry(6, 42));

//        ArrayList<Integer> colors = new ArrayList<>();
//        colors.add(Color.RED);
//        colors.add(Color.BLUE);
//        colors.add(Color.GREEN);
        int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW};
        BarDataSet dataSet = new BarDataSet(entries, "Label");
//        dataSet.setColors(colors, getApplicationContext());
        dataSet.setValueTextSize(20);
//        dataSet.setColors(colors);
        BarData lineData = new BarData(dataSet);

        barChart.setData(lineData);
        barChart.invalidate(); // refresh

    }

    public void init() {
        listDiagramSIzePortions = new ArrayList<>();
        calendar = Calendar.getInstance();
        listDiagramSIzePortions = DataRead_Write.dataReadListDiagramSIzePortion(getApplicationContext());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_downloadStatistic) {
            DataRead_Write.dataCopyListDiagramSIzePortion(getApplicationContext());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
