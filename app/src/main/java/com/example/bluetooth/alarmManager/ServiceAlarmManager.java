package com.example.bluetooth.alarmManager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.bluetooth.Const;
import com.example.bluetooth.DataRead_Write;
import com.example.bluetooth.R;
import com.example.bluetooth.statistics.ListDiagramSIzePortion;
import com.example.bluetooth.timeTable.ListTimeTable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceAlarmManager extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Створюємо канал увідомлень

//        Intent intent = new Intent(this, BroadcastReceiverAlarmManager.class);
////        // створити PendingIntent для розсилки
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    List<ListTimeTable> listTimeTables; //Список годувань
    List<ListDiagramSIzePortion> listDiagramSizePortions; //Список використання корму в день
    Calendar calendar;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            Log.d(Const.TAG, "Service start command: " + command);

            init();

            readData();

            switch (command) {
                case "midnight":
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                        for (int i = 0; i < listTimeTables.size(); i++) {
                            listTimeTables.get(i).setExecutable(false);
                        }
                        listDiagramSizePortions.clear();
                        Log.d(Const.TAG, "CLEAR:");
                        DataRead_Write.dataWriteListTimeTable(getApplicationContext(), listTimeTables);
                        DataRead_Write.dataWriteListDiagramSIzePortion(getApplicationContext(), listDiagramSizePortions);
                    }
                    intent = new Intent(this, BroadcastReceiverAlarmManager.class);
                    intent.putExtra("command", Const.COMMAND_SERVICE_CREATE_TIMETABLE_TODAY);
                    sendBroadcast(intent);
                    break;
                case "create timeTable today":
                    if (listTimeTables.size() != 0) {
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());

                        intent = new Intent(this, BroadcastReceiverAlarmManager.class);
                        intent.putExtra("command", Const.COMMAND_SERVICE_FINISH_TIMETABLE_TODAY);
                        for (int i = 0; i < listTimeTables.size(); i++) {
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                            intent.putExtra("id", listTimeTables.get(i).getId());
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), listTimeTables.get(i).getId(), intent,  PendingIntent.FLAG_IMMUTABLE);

                            alarmManager.cancel(pendingIntent);

                            pendingIntent.cancel();
                        }

                        listTimeTables = listTimeTables.stream()
                                .filter(e -> e.getWeekDay().equals(getWeekDayString(calendar.get(Calendar.DAY_OF_WEEK))))
                                .collect(Collectors.toList());

                        for (int i = 0; i < listTimeTables.size(); i++) {

                            calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            String s1 = listTimeTables.get(i).getTime();

                            String hour = "", minute = "";
                            if (calendar.get(Calendar.HOUR_OF_DAY) <= 9) {
                                hour = "0";
                            }
                            if (calendar.get(Calendar.MINUTE) <= 9) {
                                minute = "0";
                            }
                            String s2 = hour + calendar.get(Calendar.HOUR_OF_DAY) + ":" + minute + calendar.get(Calendar.MINUTE);

                            LocalTime t1 = LocalTime.parse(s1);
                            LocalTime t2 = LocalTime.parse(s2);

                            if (t1.isAfter(t2)) {

                                intent = new Intent(this, BroadcastReceiverAlarmManager.class);
                                intent.putExtra("command", Const.COMMAND_SERVICE_FINISH_TIMETABLE_TODAY);
                                intent.putExtra("id", listTimeTables.get(i).getId());

                                startAlarmManager(intent,
                                        listTimeTables.get(i).getId(),
                                        Integer.parseInt(listTimeTables.get(i).getTime().substring(0, 2)),
                                        Integer.parseInt(listTimeTables.get(i).getTime().substring(3)));
                            }
                        }

                        intent = new Intent(this, BroadcastReceiverAlarmManager.class);
                        intent.putExtra("command", Const.COMMAND_MIDNIGHT);

                        startAlarmManager(intent, 5555, 0, 0);

                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        boolean flag = true;
                        for (ListDiagramSIzePortion list : listDiagramSizePortions) {
                            if (list.getWeekDay().equals(getWeekDayString(calendar.get(Calendar.DAY_OF_WEEK)))) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            listDiagramSizePortions.add(
                                    new ListDiagramSIzePortion(
                                            getWeekDayString(calendar.get(Calendar.DAY_OF_WEEK)),
                                            new ArrayList<>()
                                    )
                            );
                        }

                        // Створюємо Comparator для порядку днів тижня
                        Comparator<ListDiagramSIzePortion> comparator = (o1, o2) -> {
                            List<String> daysOfWeek = Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд");
                            int index1 = daysOfWeek.indexOf(o1.getWeekDay());
                            int index2 = daysOfWeek.indexOf(o2.getWeekDay());
                            return Integer.compare(index1, index2);
                        };
                        listDiagramSizePortions.sort(comparator);
                        Log.d(Const.TAG, "listDiagramSIzePortions: " + listDiagramSizePortions);
                        DataRead_Write.dataWriteListDiagramSIzePortion(getApplicationContext(), listDiagramSizePortions);

                    }
                    break;

                case "finish timeTable today":
                    if (listTimeTables.size() != 0) {
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());

//                        int id = intent.getIntExtra("id", -1);
//                        Log.d(Const.TAG, "id: " + id);

                        for (int i = 0; i < listTimeTables.size(); i++) {
                            if (listTimeTables.get(i).getId() == intent.getIntExtra("id", -1)) {
                                for (int a = 0; a < listDiagramSizePortions.size(); a++) {
                                    if (listTimeTables.get(i).getWeekDay().equals(listDiagramSizePortions.get(a).getWeekDay())) {
//                                        Log.d(Const.TAG, "weekDay: " + listTimeTables.get(i).getWeekDay());
                                        Log.d(Const.TAG, "getSizePortion: " + Integer.valueOf(listTimeTables.get(i).getSizePortion()));
                                        listDiagramSizePortions.get(a).getListSizePortion().add(Integer.valueOf(listTimeTables.get(i).getSizePortion()));

                                        String day = "", month = "";
                                        if (calendar.get(Calendar.DAY_OF_MONTH) <= 9) {
                                            day = "0";
                                        }
                                        if (calendar.get(Calendar.MONTH) + 1 <= 9) {
                                            month = "0";
                                        }
                                        String data = day + calendar.get(Calendar.DAY_OF_MONTH) + ":"
                                                + month + (calendar.get(Calendar.MONTH) + 1) + ":"
                                                + calendar.get(Calendar.YEAR);
                                        DataRead_Write.dataWriteStatistic(getApplicationContext(), data, listTimeTables.get(i).getTime(), listTimeTables.get(i).getSizePortion());
                                        break;
                                    }
                                }
                                notification(listTimeTables.get(i)); //Зробить повідомлення про виконання
                                if (listTimeTables.get(i).isRepetition()) {
                                    listTimeTables.get(i).setExecutable(true);
                                } else {
                                    listTimeTables.remove(i);
                                }
                                break;
                            }
                        }
                    }

                    DataRead_Write.dataWriteListTimeTable(getApplicationContext(), listTimeTables);
                    DataRead_Write.dataWriteListDiagramSIzePortion(getApplicationContext(), listDiagramSizePortions);

                    break;
            }

        }
        return START_NOT_STICKY;
    }

    public void startAlarmManager(Intent intent, int requestCode, int hour, int minute) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        // створити календар для задання часу запуску розсилки
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());


        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String s = intent.getStringExtra("command");
        if (s.equals(Const.COMMAND_MIDNIGHT)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Log.d(Const.TAG, "Calendar.HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
        Log.d(Const.TAG, "Calendar.MINUTE: " + calendar.get(Calendar.MINUTE));

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Const.TAG, "Service destroy");
    }

    public void init() {

        listTimeTables = new ArrayList<>();
        listDiagramSizePortions = new ArrayList<>();

        calendar = Calendar.getInstance();
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

    public void readData() {
        listTimeTables = DataRead_Write.dataReadListTimeTable(getApplicationContext());
        listDiagramSizePortions = DataRead_Write.dataReadListDiagramSIzePortion(getApplicationContext());
    }

    public void notification(ListTimeTable listTimeTable) {
        NotificationChannel channel = new NotificationChannel("my_channel_id", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
        // Отримуємо посилання на службу NotificationManager
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Реєструємо канал у службі
        manager.createNotificationChannel(channel);
        // Створюємо об'єкт NotificationCompat.Builder з ідентифікатором каналу
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id");

        // Налаштовуємо параметри увідомлення
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());


        builder.setSmallIcon(R.drawable.bowl_food); // іконка увідомлення
        builder.setContentTitle("Поповнення"); // заголовок увідомлення
        builder.setContentText("Миска поповнена кормом на " + listTimeTable.getSizePortion() + "гр." + "\n " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE)); // текст увідомлення
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT); // пріоритет увідомлення
        // Додаємо дію "Відповісти" до увідомлення
        //        builder.addAction(R.drawable.ic_launcher_foreground, "pendingIntent", pendingIntent);
        // Надсилаємо увідомлення з унікальним ідентифікатором 1

        manager.notify(listTimeTable.getId(), builder.build());
    }
}
