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

import com.example.bluetooth.constFields.ConstFields;
import com.example.bluetooth.csv.DataRead_Write;
import com.example.bluetooth.R;
import com.example.bluetooth.statistics.ListDiagramSizePortion;
import com.example.bluetooth.timeTable.ListTimeTable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceAlarmManager extends Service {

    List<ListTimeTable> listTimeTables;
    List<ListDiagramSizePortion> listDiagramSizePortions;
    Calendar calendar;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            Log.d(ConstFields.TAG, "Service start command: " + command);

            init();

            readData();

            switch (command) {
                case ConstFields.COMMAND_MIDNIGHT:
            }

            switch (command) {
                case ConstFields.COMMAND_MIDNIGHT:
                    calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                        for (int i = 0; i < listTimeTables.size(); i++) {
                            listTimeTables.get(i).setExecutable(false);
                        }
                        listDiagramSizePortions.clear();
                        Log.d(ConstFields.TAG, "CLEAR:");
                        DataRead_Write.dataWriteListTimeTable(getApplicationContext(), listTimeTables);
                        DataRead_Write.dataWriteListDiagramSizePortion(getApplicationContext(), listDiagramSizePortions);
                    }
                    intent = new Intent(this, BroadcastReceiverAlarmManager.class);
                    intent.putExtra("command", ConstFields.COMMAND_SERVICE_CREATE_TIMETABLE_TODAY);
                    sendBroadcast(intent);
                    break;
                case ConstFields.COMMAND_SERVICE_CREATE_TIMETABLE_TODAY:
                    if (listTimeTables.size() != 0) {
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());

                        closeAllAlarmManager();

                        listTimeTables = listTimeTables.stream()
                                .filter(e -> e.getWeekDay().equals(getWeekDayString(calendar.get(Calendar.DAY_OF_WEEK))))
                                .collect(Collectors.toList());

                        for (int i = 0; i < listTimeTables.size(); i++) {

                            calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            String s1 = listTimeTables.get(i).getTime();

                            String hour = getStringLess(calendar.get(Calendar.HOUR_OF_DAY));
                            String minute = getStringLess(calendar.get(Calendar.MINUTE));

                            String s2 = hour + ":" + minute;

                            LocalTime t1 = LocalTime.parse(s1);
                            LocalTime t2 = LocalTime.parse(s2);

                            if (t1.isAfter(t2)) {

                                intent = new Intent(this, BroadcastReceiverAlarmManager.class);
                                intent.putExtra("command", ConstFields.COMMAND_SERVICE_FINISH_TIMETABLE_TODAY);
                                intent.putExtra("id", listTimeTables.get(i).getId());

                                String[] massTime = listTimeTables.get(i).getTime().split(":");
                                startAlarmManager(intent,
                                        listTimeTables.get(i).getId(),
                                        Integer.parseInt(massTime[0]),
                                        Integer.parseInt(massTime[1]));
                            }
                        }

                        intent = new Intent(this, BroadcastReceiverAlarmManager.class);
                        intent.putExtra("command", ConstFields.COMMAND_MIDNIGHT);

                        startAlarmManager(intent, 5555, 0, 0);

                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        boolean flag = true;
                        for (ListDiagramSizePortion list : listDiagramSizePortions) {
                            if (list.getWeekDay().equals(getWeekDayString(calendar.get(Calendar.DAY_OF_WEEK)))) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            listDiagramSizePortions.add(
                                    new ListDiagramSizePortion(
                                            getWeekDayString(calendar.get(Calendar.DAY_OF_WEEK)),
                                            0
                                    )
                            );
                        }

                        // Створюємо Comparator для порядку днів тижня
                        Comparator<ListDiagramSizePortion> comparator = (o1, o2) -> {
                            List<String> daysOfWeek = Arrays.asList("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд");
                            int index1 = daysOfWeek.indexOf(o1.getWeekDay());
                            int index2 = daysOfWeek.indexOf(o2.getWeekDay());
                            return Integer.compare(index1, index2);
                        };
                        listDiagramSizePortions.sort(comparator);
                        Log.d(ConstFields.TAG, "listDiagramSIzePortions: " + listDiagramSizePortions);
                        DataRead_Write.dataWriteListDiagramSizePortion(getApplicationContext(), listDiagramSizePortions);

                    }
                    break;

                case ConstFields.COMMAND_SERVICE_FINISH_TIMETABLE_TODAY:
                    if (listTimeTables.size() != 0) {
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());

                        for (int i = 0; i < listTimeTables.size(); i++) {
                            if (listTimeTables.get(i).getId() == intent.getIntExtra("id", -1)) {
                                for (int a = 0; a < listDiagramSizePortions.size(); a++) {
                                    if (listTimeTables.get(i).getWeekDay().equals(listDiagramSizePortions.get(a).getWeekDay())) {

                                        int sumUsedFeed = listDiagramSizePortions.get(a).getSumUsedFeed();
                                        sumUsedFeed += Integer.parseInt(listTimeTables.get(i).getSizePortion());

                                        listDiagramSizePortions.get(a).setSumUsedFeed(sumUsedFeed);

                                        String day = getStringLess(calendar.get(Calendar.DAY_OF_MONTH));
                                        String month = getStringLess(calendar.get(Calendar.MONTH) + 1);

                                        String data = day + ":" + month + ":" + calendar.get(Calendar.YEAR);
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
                    DataRead_Write.dataWriteListDiagramSizePortion(getApplicationContext(), listDiagramSizePortions);

                    intent = new Intent(ConstFields.UPDATE_LIST_VIEW);
                    sendBroadcast(intent);

                    break;
            }

        }
        return START_NOT_STICKY;
    }

    public void startAlarmManager(Intent intent, int requestCode, int hour, int minute) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String s = intent.getStringExtra("command");
        if (s.equals(ConstFields.COMMAND_MIDNIGHT)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Log.d(ConstFields.TAG, "Calendar.HOUR_OF_DAY: " + calendar.get(Calendar.HOUR_OF_DAY));
        Log.d(ConstFields.TAG, "Calendar.MINUTE: " + calendar.get(Calendar.MINUTE));

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void closeAllAlarmManager() {
        Intent intent = new Intent(this, BroadcastReceiverAlarmManager.class);
        intent.putExtra("command", ConstFields.COMMAND_SERVICE_FINISH_TIMETABLE_TODAY);
        for (int i = 0; i < listTimeTables.size(); i++) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            intent.putExtra("id", listTimeTables.get(i).getId());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), listTimeTables.get(i).getId(), intent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pendingIntent);

            pendingIntent.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ConstFields.TAG, "Service destroy");
    }

    public void init() {

        listTimeTables = new ArrayList<>();
        listDiagramSizePortions = new ArrayList<>();

        calendar = Calendar.getInstance();
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

    public void readData() {
        listTimeTables = DataRead_Write.dataReadListTimeTable(getApplicationContext());
        listDiagramSizePortions = DataRead_Write.dataReadListDiagramSizePortion(getApplicationContext());
    }

    public void notification(ListTimeTable listTimeTable) {
        NotificationChannel channel = new NotificationChannel("my_channel_id", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(System.currentTimeMillis());

        builder.setSmallIcon(R.drawable.bowl_food);
        builder.setContentTitle("Поповнення");
        builder.setContentText("Миска поповнена кормом на " + listTimeTable.getSizePortion() + "гр." + "\n " + getStringLess(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + getStringLess(calendar.get(Calendar.MINUTE))); // текст увідомлення
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify(listTimeTable.getId(), builder.build());
    }

}
