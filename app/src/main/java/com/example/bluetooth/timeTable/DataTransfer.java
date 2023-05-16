package com.example.bluetooth.timeTable;

import java.util.List;

public interface DataTransfer {
    void createListTimeTable(List<String> list, boolean repetition, String sizePortion, String time);

    void editListTimeTable(int idElementListTimeTable, boolean repetition, String sizePortion, String time);
}
