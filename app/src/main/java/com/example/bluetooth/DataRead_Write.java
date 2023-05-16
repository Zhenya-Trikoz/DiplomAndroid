package com.example.bluetooth;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.bluetooth.statistics.ListDiagramSIzePortion;
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

            Log.d(Const.TAG, "Файл " + fileNameListTimeTable + " успішно збережено у внутрішньому сховищі.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Const.TAG, "Помилка збереження " + fileNameListTimeTable + " у внутрішньому сховищі.");
        }
    }

    public static List<ListDiagramSIzePortion> dataReadListDiagramSIzePortion(Context context) {
        List<ListDiagramSIzePortion> listDiagramSIzePortions = new ArrayList<>();
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
                String sizePortion = line.substring(5).replaceAll("\"", "");

                sizePortion = sizePortion.replaceAll("[^\\d,]", "");

                List<Integer> numbers = new ArrayList<>();

                if (sizePortion.length() > 0) {
                    String[] mass = sizePortion.split(",");
//                    Log.d(Const.TAG, "mass: " + Arrays.toString(mass));

                    for (String part : mass) {
                        int num = Integer.parseInt(part.trim());
                        numbers.add(num);
                    }
                }

                ListDiagramSIzePortion listTimeTable = new ListDiagramSIzePortion(weekDay, numbers);
                listDiagramSIzePortions.add(listTimeTable);
            }
//            Log.d(Const.TAG, "listDiagramSIzePortions: " + listDiagramSIzePortions);
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return listDiagramSIzePortions;
    }

    public static void dataWriteListDiagramSIzePortion(Context context, List<ListDiagramSIzePortion> list) {
        File file = new File(context.getFilesDir(), fileNameListDiagramSizePortion);
        String path = file.getAbsolutePath();

        try {
            FileWriter fileWriter = new FileWriter(path);
            CSVWriter writer = new CSVWriter(fileWriter);

            // Записуємо заголовок файлу
            String[] HEADERS = {"weekDay", "sizePortion"};
            writer.writeNext(HEADERS);

            for (ListDiagramSIzePortion listDiagramSIzePortion : list) {
                String[] data = {listDiagramSIzePortion.getWeekDay(), String.valueOf(listDiagramSIzePortion.getListSizePortion())};
                writer.writeNext(data);
            }
            writer.close();
            fileWriter.close();

            Log.d(Const.TAG, "Файл " + fileNameListDiagramSizePortion + " успішно збережено у внутрішньому сховищі.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Const.TAG, "Помилка збереження " + fileNameListDiagramSizePortion + " у внутрішньому сховищі.");
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
            Log.d(Const.TAG, "static: " + Arrays.toString(statistic));
            writer.writeNext(statistic);

            writer.close();
            fileWriter.close();

            Log.d(Const.TAG, "Файл " + fileNameStatistic + " успішно збережено у внутрішньому сховищі.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Const.TAG, "Помилка збереження " + fileNameStatistic + " у внутрішньому сховищі.");
        }

    }

    public static void dataCopyListDiagramSIzePortion(Context context) {

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
