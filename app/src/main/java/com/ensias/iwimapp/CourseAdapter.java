package com.ensias.iwimapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CourseAdapter extends ArrayAdapter<RowItem> {

    Context context;

    public CourseAdapter( Context context, int resourceId,
                                 List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }
    /*private view holder class*/
    private class ViewHolder {
        ImageView pdf;
        TextView course;
        TextView prof;
        TextView subject;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.course = (TextView) convertView.findViewById(R.id.course_name);
            holder.prof = (TextView) convertView.findViewById(R.id.prof);
            holder.subject = (TextView) convertView.findViewById(R.id.subject);
            holder.pdf = (ImageView) convertView.findViewById(R.id.pdf);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.course.setText(rowItem.getCourse());
        holder.prof.setText(rowItem.getProf());
        holder.subject.setText(rowItem.getSubject());
        holder.pdf.setImageResource(rowItem.getImageID());

        return convertView;
    }
}
