package com.example.bluetooth.statistics;

import java.util.List;

public class ListDiagramSizePortion {

    private String weekDay;
    private int sumUsedFeed; // Список розміру порції на день

    public ListDiagramSizePortion(String weekDay, int sumUsedFeed) {
        this.weekDay = weekDay;
        this.sumUsedFeed = sumUsedFeed;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public int getSumUsedFeed() {
        return sumUsedFeed;
    }

    public void setSumUsedFeed(int sumUsedFeed) {
        this.sumUsedFeed = sumUsedFeed;
    }

    @Override
    public String toString() {
        return "ListDiagramSizePortion{" +
                "weekDay='" + weekDay + '\'' +
                ", sumUsedFeed=" + sumUsedFeed +
                '}';
    }
}
