package com.example.bluetooth.timeTable;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.example.bluetooth.constFields.ConstFields;
import com.example.bluetooth.csv.DataRead_Write;
import com.example.bluetooth.R;
import com.example.bluetooth.alarmManager.BroadcastReceiverAlarmManager;
import com.example.bluetooth.dialog.DialogCreateTimeTable;
import com.example.bluetooth.dialog.DialogEditTimeTable;
import com.example.bluetooth.statistics.Statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressLint("MissingPermission")
public class TimeTable extends AppCompatActivity {

    private Button buttonMonday, buttonTuesday, buttonWednesday, buttonThursday, buttonFriday, buttonSaturday, buttonSunday;
    private List<ListTimeTable> listTimeTables;
    private List<ListTimeTable> buffListTimeTable;
    private ListView listViewTimeTable;
    private AdapterListTimeTable adapterListTimeTable;
    private Button buttonAddTimeTable;
    private int buttonIsNotSelect;
    private MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        init();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstFields.CREATE_LIST_TIME_TABLE);
        intentFilter.addAction(ConstFields.EDIT_LIST_TIME_TABLE);
        intentFilter.addAction(ConstFields.UPDATE_LIST_VIEW);
        registerReceiver(broadcastReceiverTimeTable, intentFilter);


//        buttonMonday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonColorIsSelect)));
        buttonMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView("Пн");
            }
        });
        buttonTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView("Вт");
            }
        });
        buttonWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView("Ср");
            }
        });
        buttonThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView("Чт");
            }
        });
        buttonFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView("Пт");
            }
        });
        buttonSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIsSelect(buttonSaturday.getId());
                listView("Сб");
            }
        });
        buttonSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView("Нд");
            }
        });
        buttonAddTimeTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateTimeTable();
            }
        });

        listViewTimeTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Обробити натискання на елемент списку
                dialogEditTimeTable(buffListTimeTable.get(position));
            }
        });

        listViewTimeTable.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dialogDeleteTimeTable(buffListTimeTable.get(position));
                return true;
            }
        });


    }

    public void dialogCreateTimeTable() {
        DialogCreateTimeTable dialog = new DialogCreateTimeTable(TimeTable.this);
        dialog.show();
    }

    public void dialogEditTimeTable(ListTimeTable listTimeTable) {
        Toast.makeText(getApplicationContext(), "Ви натиснули на елемент " + listTimeTable.getTime(), Toast.LENGTH_SHORT).show();
        DialogEditTimeTable dialog = new DialogEditTimeTable(TimeTable.this);
        dialog.data(listTimeTable);
        dialog.show();
    }

    public void dialogDeleteTimeTable(ListTimeTable listTimeTable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TimeTable.this);
        builder.setTitle("Підтвердження видалення");
        builder.setMessage("Ви впевнені, що хочете видалити \"" + listTimeTable.getTime() + "\"?");

        builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (ListTimeTable table : listTimeTables) {
                    if (table.getWeekDay().equals(listTimeTable.getWeekDay()) && table.getTime().equals(listTimeTable.getTime())) {
                        // видалення елемента зі списку
                        listTimeTables.remove(table);
                        saveData();
                        break;
                    }
                }
                menuItem.setVisible(true);
                listView(listTimeTable.getWeekDay());
            }
        });

        builder.setNegativeButton("Ні", null);
        builder.show();
    }


    public void sendDataBluetooth() {
        Intent intent = new Intent(ConstFields.ACTION_DATA_SEND);
        sendBroadcast(intent);
    }

    public void buttonIsSelect(int buttonIsSelect) {
        Button ButtonIsNotSelect = findViewById(buttonIsNotSelect);
        Button ButtonIsSelect = findViewById(buttonIsSelect);

        ButtonIsNotSelect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonColorIsNotSelect)));
        ButtonIsSelect.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.buttonColorIsSelect)));
        this.buttonIsNotSelect = buttonIsSelect;

    }

    public void createListTimeTable(List<String> list, boolean repetition, String sizePortion, String time) {
        int id = listTimeTables.size();
        for (String weekday : list) {
            Log.d(ConstFields.TAG, "id = listTimeTables.size(): " + id);

            ListTimeTable table = new ListTimeTable(id++, weekday, time, sizePortion, repetition, false);
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

        sortListTimeTable();

        for (int i = 0; i < listTimeTables.size(); i++) {
            listTimeTables.get(i).setId(i);
        }

        saveData();

        listView(list.get(0));
    }

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

    public void listView(String weekday) {

        buffListTimeTable = listTimeTables.stream().filter(e -> e.getWeekDay().equals(weekday)).collect(Collectors.toList());
        adapterListTimeTable = new AdapterListTimeTable(this, R.layout.element_list_time_table, buffListTimeTable);
        listViewTimeTable.setAdapter(adapterListTimeTable);
        adapterListTimeTable.notifyDataSetChanged();

        int buttonSelect = 0;
        if (buttonMonday.getText().toString().equals(weekday)) {
            buttonSelect = buttonMonday.getId();
        } else if (buttonTuesday.getText().equals(weekday)) {
            buttonSelect = buttonTuesday.getId();
        } else if (buttonWednesday.getText().equals(weekday)) {
            buttonSelect = buttonWednesday.getId();
        } else if (buttonThursday.getText().equals(weekday)) {
            buttonSelect = buttonThursday.getId();
        } else if (buttonFriday.getText().equals(weekday)) {
            buttonSelect = buttonFriday.getId();
        } else if (buttonSaturday.getText().equals(weekday)) {
            buttonSelect = buttonSaturday.getId();
        } else if (buttonSunday.getText().equals(weekday)) {
            buttonSelect = buttonSunday.getId();
        }
        buttonIsSelect(buttonSelect);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_time_table, menu);

        menuItem = menu.findItem(R.id.upload);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_static) {
            Intent intent = new Intent(TimeTable.this, Statistics.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.upload) {
            menuItem.setVisible(false);

            sendDataBluetooth();

            Intent intent = new Intent(this, BroadcastReceiverAlarmManager.class);
            intent.putExtra("command", ConstFields.COMMAND_SERVICE_CREATE_TIMETABLE_TODAY);
            sendBroadcast(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(ConstFields.TAG, "Activity Stop");

        saveData();


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(menuItem.isVisible()){
            sendDataBluetooth();

            Intent intent = new Intent(this, BroadcastReceiverAlarmManager.class);
            intent.putExtra("command", ConstFields.COMMAND_SERVICE_CREATE_TIMETABLE_TODAY);
            sendBroadcast(intent);
        }

    }

    public void saveData() {
        DataRead_Write.dataWriteListTimeTable(getApplicationContext(), listTimeTables);
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

        buttonIsNotSelect = buttonMonday.getId();

        listTimeTables = new ArrayList<>();
        buffListTimeTable = new ArrayList<>();

        listTimeTables = DataRead_Write.dataReadListTimeTable(getApplicationContext());

        listView("Пн");
    }

    BroadcastReceiver broadcastReceiverTimeTable = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstFields.CREATE_LIST_TIME_TABLE)) {
                ArrayList<String> list = intent.getStringArrayListExtra("listCheckBox");
                boolean repetition = intent.getBooleanExtra("repetition", false);
                String sizePortion = intent.getStringExtra("sizePortion");
                String time = intent.getStringExtra("time");

                createListTimeTable(list, repetition, sizePortion, time);
                menuItem.setVisible(true);
            } else if (action.equals(ConstFields.EDIT_LIST_TIME_TABLE)) {
                int id = intent.getIntExtra("id", -1);
                boolean repetition = intent.getBooleanExtra("repetition", false);
                String sizePortion = intent.getStringExtra("sizePortion");
                String time = intent.getStringExtra("time");

                editListTimeTable(id, repetition, sizePortion, time);
                menuItem.setVisible(true);
            } else if (action.equals(ConstFields.UPDATE_LIST_VIEW)) {
                listTimeTables = DataRead_Write.dataReadListTimeTable(context);
                Button button = findViewById(buttonIsNotSelect);
                listView(button.getText().toString());
            }


        }
    };
}