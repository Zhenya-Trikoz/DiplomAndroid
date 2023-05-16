package com.example.bluetooth.statistics;

import java.util.List;

public class ListDiagramSIzePortion {

    private String weekDay;
    private List<Integer> listSizePortion; // Список розміру порції на день

    public ListDiagramSIzePortion(String weekDay, List<Integer> listSizePortion) {

        this.weekDay = weekDay;
        this.listSizePortion = listSizePortion;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public List<Integer> getListSizePortion() {
        return listSizePortion;
    }

    public void setListSizePortion(List<Integer> listSizePortion) {
        this.listSizePortion = listSizePortion;
    }

    @Override
    public String toString() {
        return "ListDiagramSIzePortion{" +
                "weekDay='" + weekDay + '\'' +
                ", listSizePortion=" + listSizePortion +
                '}';
    }
}
