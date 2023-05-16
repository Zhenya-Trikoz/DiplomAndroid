package com.example.bluetooth.timeTable;

import java.util.Calendar;

public class ListTimeTable {

    private int id;
    private String weekDay; //День тижня
    private String time; // Час виконання
    private String sizePortion; // Розмір порції
    private boolean repetition = false; // Повторюваний розклад
    private boolean executable = false; //Виконався розклад чи ні

    public ListTimeTable(int id, String weekDay, String time, String sizePortion, boolean repetition) {
        this.id = id;
        this.weekDay = weekDay;
        this.time = time;
        this.sizePortion = sizePortion;
        this.repetition = repetition;
    }

    public ListTimeTable(int id, String weekDay, String time, String sizePortion, boolean repetition, boolean executable) {
        this.id = id;
        this.weekDay = weekDay;
        this.time = time;
        this.sizePortion = sizePortion;
        this.repetition = repetition;
        this.executable = executable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSizePortion() {
        return sizePortion;
    }

    public void setSizePortion(String sizePortion) {
        this.sizePortion = sizePortion;
    }

    public boolean isRepetition() {
        return repetition;
    }

    public void setRepetition(boolean repetition) {
        this.repetition = repetition;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    @Override
    public String toString() {
        return "ListTimeTable{" +
                "id=" + id +
                ", weekDay='" + weekDay + '\'' +
                ", time='" + time + '\'' +
                ", sizePortion='" + sizePortion + '\'' +
                ", repetition=" + repetition +
                ", executable=" + executable +
                '}';
    }
}
