package com.example.bluetooth.timeTable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bluetooth.R;

import java.util.List;

@SuppressLint("MissingPermission")
public class AdapterListTimeTable extends ArrayAdapter<ListTimeTable> {

    private List<ListTimeTable> list;

    public AdapterListTimeTable(@NonNull Context context, int resource, List<ListTimeTable> list) {
        super(context, resource, list);
        this.list = list;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new AdapterListTimeTable.ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_list_time_table, null, false);


            viewHolder.textTime = convertView.findViewById(R.id.textTime);
            viewHolder.textSizePortion = convertView.findViewById(R.id.textSizePortion);
            viewHolder.textRepetition = convertView.findViewById(R.id.textRepetition);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AdapterListTimeTable.ViewHolder) convertView.getTag();
        }
        viewHolder.textTime.setText(list.get(position).getTime());
        viewHolder.textSizePortion.setText(list.get(position).getSizePortion()+" гр.");

        if (list.get(position).isRepetition()) {
            viewHolder.textRepetition.setText("Повторюється щотижня");
        } else {
            viewHolder.textRepetition.setText("Не повторюється");
        }

        // Додати перевірку виконаності розкладу

        if (list.get(position).isExecutable()) {
            Drawable drawable = getContext().getDrawable(R.drawable.backgound_element_list_time_table_executable);
            convertView.setBackground(drawable);
        } else {
            Drawable drawable = getContext().getDrawable(R.drawable.backgound_element_list_time_table_dont_executable);
            convertView.setBackground(drawable);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView textTime;
        TextView textSizePortion;
        TextView textRepetition;

    }
}
