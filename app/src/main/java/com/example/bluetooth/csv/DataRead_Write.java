package com.example.bluetooth.csv;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.bluetooth.constFields.ConstFields;
import com.example.bluetooth.statistics.ListDiagramSizePortion;
import com.example.bluetooth.timeTable.ListTimeTable;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DataRead_Write {

    public static String fileNameListTimeTable = "TimeTable.csv";

    public static String fileNameListDiagramSizePortion = "Diagram.csv";
    public static String fileNameStatistic = "Statistic.csv";


    public static List<ListTimeTable> dataReadListTimeTable(Context context) {
        List<ListTimeTable> listTimeTables = new ArrayList<>();
        try {
            File file = new File(context.getFilesDir(), fileNameListTimeTable);
            Scanner scanner = new Scanner(file);

            // Пропускаємо перший рядок (заголовок файлу)
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            // Зчитуємо дані
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                int id = Integer.parseInt(parts[0].replaceAll("\"", ""));
                String weekDay = parts[1].replaceAll("\"", "");
                String time = parts[2].replaceAll("\"", "");
                String sizePortion = parts[3].replaceAll("\"", "");
                boolean repetition = Boolean.parseBoolean(parts[4].replaceAll("\"", ""));
                boolean executable = Boolean.parseBoolean(parts[5].replaceAll("\"", ""));

                ListTimeTable listTimeTable = new ListTimeTable(id, weekDay, time, sizePortion, repetition, executable);

                listTimeTables.add(listTimeTable);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return listTimeTables;
    }

    public static void dataWriteListTimeTable(Context context, List<ListTimeTable> list) {

        File file = new File(context.getFilesDir(), fileNameListTimeTable);
        String path = file.getAbsolutePath();

        try {
            FileWriter fileWriter = new FileWriter(path);
            CSVWriter writer = new CSVWriter(fileWriter);

            // Записуємо заголовок файлу
            String[] HEADERS = {"id", "weekDay", "time", "sizePortion", "repetition", "executable"};
            writer.writeNext(HEADERS);

            for (ListTimeTable timeTable : list) {
                String[] data = {String.valueOf(timeTable.getId()), timeTable.getWeekDay(), timeTable.getTime(), timeTable.getSizePortion(), String.valueOf(timeTable.isRepetition()), String.valueOf(timeTable.isExecutable()),};
                writer.writeNext(data);
            }
            writer.close();
            fileWriter.close();

            Log.d(ConstFields.TAG, "Файл " + fileNameListTimeTable + " успішно збережено у внутрішньому сховищі.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(ConstFields.TAG, "Помилка збереження " + fileNameListTimeTable + " у внутрішньому сховищі.");
        }
    }

    public static List<ListDiagramSizePortion> dataReadListDiagramSizePortion(Context context) {
        List<ListDiagramSizePortion> listDiagramSizePortions = new ArrayList<>();
        try {
            File file = new File(context.getFilesDir(), fileNameListDiagramSizePortion);
            Scanner scanner = new Scanner(file);

            // Пропускаємо перший рядок (заголовок файлу)
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            // Зчитуємо дані
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String weekDay = line.substring(0, 4).replaceAll("\"", "");
                String sumUsedFeed = line.substring(5).replaceAll("\"", "");


                ListDiagramSizePortion listTimeTable = new ListDiagramSizePortion(weekDay, Integer.parseInt(sumUsedFeed));
                listDiagramSizePortions.add(listTimeTable);
            }
//            Log.d(Const.TAG, "listDiagramSIzePortions: " + listDiagramSIzePortions);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return listDiagramSizePortions;
    }

    public static void dataWriteListDiagramSizePortion(Context context, List<ListDiagramSizePortion> list) {
        File file = new File(context.getFilesDir(), fileNameListDiagramSizePortion);
        String path = file.getAbsolutePath();

        try {
            FileWriter fileWriter = new FileWriter(path);
            CSVWriter writer = new CSVWriter(fileWriter);

            // Записуємо заголовок файлу
            String[] HEADERS = {"weekDay", "sizePortion"};
            writer.writeNext(HEADERS);

            for (ListDiagramSizePortion listDiagramSIzePortion : list) {
                String[] data = {listDiagramSIzePortion.getWeekDay(), String.valueOf(listDiagramSIzePortion.getSumUsedFeed())};
                writer.writeNext(data);
            }
            writer.close();
            fileWriter.close();

            Log.d(ConstFields.TAG, "Файл " + fileNameListDiagramSizePortion + " успішно збережено у внутрішньому сховищі.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(ConstFields.TAG, "Помилка збереження " + fileNameListDiagramSizePortion + " у внутрішньому сховищі.");
        }
    }

    public static void dataWriteStatistic(Context context, String data, String time, String sizePortion) {
        File file = new File(context.getFilesDir(), fileNameStatistic);
        String path = file.getAbsolutePath();

        try {
            FileWriter fileWriter = new FileWriter(path, true);
            CSVWriter writer = new CSVWriter(fileWriter);

            if (file.length() == 0) {
                // Записуємо заголовок файлу
                String[] HEADERS = {"data", "time", "sizePortion"};
                writer.writeNext(HEADERS);
            }

            String[] statistic = {data, time, sizePortion};
            Log.d(ConstFields.TAG, "static: " + Arrays.toString(statistic));
            writer.writeNext(statistic);

            writer.close();
            fileWriter.close();

            Log.d(ConstFields.TAG, "Файл " + fileNameStatistic + " успішно збережено у внутрішньому сховищі.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(ConstFields.TAG, "Помилка збереження " + fileNameStatistic + " у внутрішньому сховищі.");
        }

    }

    public static void dataCopyStatistic(Context context) {

        File file = new File(context.getFilesDir(), fileNameStatistic);

        // Шлях до папки Downloads
        File newFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File newFile = new File(newFolder, file.getName());

        try {
            FileInputStream in = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(context, "Статистику було завантажено у папку Downloads", Toast.LENGTH_SHORT).show();

    }
}
