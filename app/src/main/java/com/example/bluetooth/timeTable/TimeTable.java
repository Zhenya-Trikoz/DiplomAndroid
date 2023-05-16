package com.example.bluetooth.timeTable;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth.Const;
import com.example.bluetooth.DataRead_Write;
import com.example.bluetooth.R;
import com.example.bluetooth.statistics.Static;
import com.example.bluetooth.alarmManager.BroadcastReceiverAlarmManager;
import com.example.bluetooth.bluetooth.ServiceBluetooth;
import com.example.bluetooth.dialog.DialogCreateTimeTable;
import com.example.bluetooth.dialog.DialogEditTimeTable;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressLint("MissingPermission")
public class TimeTable extends AppCompatActivity implements DataTransfer {

    private Button buttonMonday, buttonTuesday, buttonWednesday, buttonThursday, buttonFriday, buttonSaturday, buttonSunday;
    List<ListTimeTable> listTimeTables;
    List<ListTimeTable> buffListTimeTable;
    ListView listViewTimeTable;
    AdapterListTimeTable adapterListTimeTable;
    /////

    Button buttonAddTimeTable;
    int buttonIsNotSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);


        init();

        buttonIsNotSelect = buttonMonday.getId();

        buttonMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonIsNotSelect, buttonMonday.getId());
                list("Пн");
            }
        });
        buttonTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonIsNotSelect, buttonTuesday.getId());
//                DataRead_Write.dataReadListTimeTable(getApplicationContext());
                list("Вт");
            }
        });
        buttonWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonIsNotSelect, buttonWednesday.getId());
                list("Ср");
            }
        });
        buttonThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonIsNotSelect, buttonThursday.getId());
                list("Чт");
            }
        });
        buttonFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonIsNotSelect, buttonFriday.getId());
                list("Пт");
            }
        });
        buttonSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonIsNotSelect, buttonSaturday.getId());
                list("Сб");
//                copyFile();
            }
        });
        buttonSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonIsNotSelect, buttonSunday.getId());
                list("Нд");
            }
        });
        buttonAddTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        listViewTimeTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Обробити натискання на елемент списку
                Toast.makeText(getApplicationContext(), "Натиснули на елемент " + buffListTimeTable.get(position).getTime(), Toast.LENGTH_SHORT).show();
                DialogEditTimeTable dialog = new DialogEditTimeTable(TimeTable.this);
                dialog.data(buffListTimeTable.get(position));
                dialog.show();
            }
        });

        listViewTimeTable.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // створення діалогу підтвердження видалення
                AlertDialog.Builder builder = new AlertDialog.Builder(TimeTable.this);
                builder.setTitle("Підтвердження видалення");
                builder.setMessage("Ви впевнені, що хочете видалити \"" + buffListTimeTable.get(position).getTime() + "\"?");

                // додавання кнопки Позитивної відповіді
                builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (ListTimeTable table : listTimeTables) {
                            if (table.getWeekDay().equals(buffListTimeTable.get(position).getWeekDay()) && table.getTime().equals(buffListTimeTable.get(position).getTime())) {
                                // видалення елемента зі списку
                                listTimeTables.remove(table);
                                break;
                            }
                        }
                        list(buffListTimeTable.get(position).getWeekDay());
                    }
                });
                // додавання кнопки Негативної відповіді
                builder.setNegativeButton("Ні", null);
                // відображення діалогу
                builder.show();
                return true;
            }
        });


    }

    public void startServiceBluetooth(String command) {
        Intent intent = new Intent(this, ServiceBluetooth.class);
        intent.putExtra("command", command);
        startService(intent);

    }

    public void buttonIsSelect(int buttonIsNotSelect, int buttonIsSelect) {
        Button ButtonIsNotSelect = findViewById(buttonIsNotSelect);
        Button ButtonIsSelect = findViewById(buttonIsSelect);

        ButtonIsNotSelect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonColorIsNotSelect)));
        ButtonIsSelect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonColorIsSelect)));
        this.buttonIsNotSelect = buttonIsSelect;

    }

    public void dialog() {
        DialogCreateTimeTable dialog = new DialogCreateTimeTable(this);
        dialog.show();
    }

    @Override
    public void createListTimeTable(List<String> list, boolean repetition, String sizePortion, String time) {
        int id = listTimeTables.size();
        for (String weekday : list) {
            Log.d(Const.TAG, "id = listTimeTables.size(): " + id);

            ListTimeTable table = new ListTimeTable(id++, weekday, time, sizePortion, repetition);
            boolean flag = true;
            for (ListTimeTable listTimeTable : listTimeTables) {
                if (listTimeTable.getWeekDay().equals(table.getWeekDay()) && listTimeTable.getTime().equals(table.getTime())) {
                    flag = false;
                    Toast.makeText(getApplicationContext(), "На " + listTimeTable.getWeekDay() + " у " + listTimeTable.getTime() + " вже запланована подія!", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            if (flag) {
                listTimeTables.add(table);
            }
        }

        Intent intent = new Intent(this, BroadcastReceiverAlarmManager.class);
        intent.setAction(Const.COMMAND_SERVICE_FINISH_TIMETABLE_TODAY);
        for (int i = 0; i < listTimeTables.size(); i++) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }

        sortListTimeTable();

        for (int i = 0; i < listTimeTables.size(); i++) {
            listTimeTables.get(i).setId(i);
        }

        Log.d(Const.TAG, "listTimeTables: " + listTimeTables);

        saveData();
        int buttonSelect = 0;
        if (buttonMonday.getText().equals(list.get(0))) {
            buttonSelect = buttonMonday.getId();
        } else if (buttonTuesday.getText().equals(list.get(0))) {
            buttonSelect = buttonTuesday.getId();
        } else if (buttonWednesday.getText().equals(list.get(0))) {
            buttonSelect = buttonWednesday.getId();
        } else if (buttonThursday.getText().equals(list.get(0))) {
            buttonSelect = buttonThursday.getId();
        } else if (buttonFriday.getText().equals(list.get(0))) {
            buttonSelect = buttonFriday.getId();
        } else if (buttonSaturday.getText().equals(list.get(0))) {
            buttonSelect = buttonSaturday.getId();
        } else if (buttonSunday.getText().equals(list.get(0))) {
            buttonSelect = buttonSunday.getId();
        }

        buttonIsSelect(buttonIsNotSelect, buttonSelect);
        list(list.get(0));
    }

    @Override
    public void editListTimeTable(int idElementListTimeTable, boolean repetition, String sizePortion, String time) {
        for (int i = 0; i < listTimeTables.size(); i++) {
            if (listTimeTables.get(i).getId() == idElementListTimeTable) {
                boolean flag = true;
                for (int a = 0; a < listTimeTables.size(); a++) {
                    if (listTimeTables.get(a).getWeekDay().equals(listTimeTables.get(i).getWeekDay()) && listTimeTables.get(a).getTime().equals(time) && listTimeTables.get(a).getId() != idElementListTimeTable) {

                        flag = false;
                        Toast.makeText(getApplicationContext(), "За вказаним часом є запланована подія!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (flag) {
                    listTimeTables.get(i).setRepetition(repetition);
                    listTimeTables.get(i).setSizePortion(sizePortion);
                    listTimeTables.get(i).setTime(time);
                    listTimeTables.get(i).setExecutable(false);
                }
                break;
            }
        }
        adapterListTimeTable.notifyDataSetChanged();

        sortListTimeTable();
        saveData();
    }

    public void sortListTimeTable() {
        listTimeTables.sort(new Comparator<ListTimeTable>() {
            public int compare(ListTimeTable p1, ListTimeTable p2) {
                return p1.getTime().compareTo(p2.getTime());
            }
        });
        Comparator<ListTimeTable> comparator = (o1, o2) -> {
            List<String> daysOfWeek = Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд");
            int index1 = daysOfWeek.indexOf(o1.getWeekDay());
            int index2 = daysOfWeek.indexOf(o2.getWeekDay());
            return Integer.compare(index1, index2);
        };
        listTimeTables.sort(comparator);
    }

    public void list(String weekday) {

        buffListTimeTable = listTimeTables.stream().filter(e -> e.getWeekDay().equals(weekday)).collect(Collectors.toList());
        adapterListTimeTable = new AdapterListTimeTable(this, R.layout.element_list_time_table, buffListTimeTable);
        listViewTimeTable.setAdapter(adapterListTimeTable);
        adapterListTimeTable.notifyDataSetChanged();
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
        if (id == R.id.action_static) {
            Intent intent = new Intent(TimeTable.this, Static.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendData() {

    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(Const.TAG, "Activity Stop");


        saveData();

        Intent intent = new Intent(this, BroadcastReceiverAlarmManager.class);
        intent.putExtra("command", Const.COMMAND_SERVICE_CREATE_TIMETABLE_TODAY);
        sendBroadcast(intent);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        startServiceBluetooth("Send data");

    }

    public void saveData() {
//        String json = gson.toJson(listTimeTables);
//        editor.putString(Const.PREF_LIST_TIME_TABLE, json);
//        editor.apply();

        DataRead_Write.dataWriteListTimeTable(getApplicationContext(), listTimeTables);
    }

    public int getWeekDay(String weekDay) {

        switch (weekDay) {
            case "Пн":
                return Calendar.MONDAY;
            case "Вт":
                return Calendar.TUESDAY;
            case "Ср":
                return Calendar.WEDNESDAY;
            case "Чт":
                return Calendar.THURSDAY;
            case "Пт":
                return Calendar.FRIDAY;
            case "Сб":
                return Calendar.SATURDAY;
            case "Нд":
                return Calendar.SUNDAY;
        }
        return 0;
    }

    public void init() {
        buttonMonday = findViewById(R.id.buttonMonday);
        buttonTuesday = findViewById(R.id.buttonTuesday);
        buttonWednesday = findViewById(R.id.buttonWednesday);
        buttonThursday = findViewById(R.id.buttonThusday);
        buttonFriday = findViewById(R.id.buttonFriday);
        buttonSaturday = findViewById(R.id.buttonSaturday);
        buttonSunday = findViewById(R.id.buttonSunday);

        buttonAddTimeTable = findViewById(R.id.buttonAddTimeTable);

        listViewTimeTable = findViewById(R.id.listTimeTable);


        listTimeTables = new ArrayList<>();
        buffListTimeTable = new ArrayList<>();

        listTimeTables = DataRead_Write.dataReadListTimeTable(getApplicationContext());

        list("Пн");
    }



}