package com.antandbuffalo.birthdayreminder.today;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.database.DobDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 23/9/15.
 */
public class TodayListAdapter extends BaseAdapter {

    List<DateOfBirth> dobs = getDataForListView();
    @Override
    public int getCount() {
        return dobs.size();
    }

    @Override
    public DateOfBirth getItem(int position) {
        return dobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.today_listitem, parent, false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.textView1);
        TextView desc = (TextView)convertView.findViewById(R.id.textView2);

        TextView dateField = (TextView)convertView.findViewById(R.id.dateField);
        TextView monthField = (TextView)convertView.findViewById(R.id.monthField);
        TextView yearField = (TextView)convertView.findViewById(R.id.yearField);

        DateOfBirth dob = dobs.get(position);
        name.setText(dob.getName());
        desc.setText(dob.getDescription());
        Date date = dob.getDobDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM");
        dateField.setText(cal.get(Calendar.DATE) + "");
        monthField.setText(formatter.format(cal.getTime()));
        yearField.setText(cal.get(Calendar.YEAR) + "");

//        dateField.setText("27");
//        monthField.setText("Sep");
//        yearField.setText("2018");


        return convertView;
    }

    public List<DateOfBirth> getDataForListView()
    {
        List<DateOfBirth> allDobs = DobDBHelper.selectAll();
        return allDobs;
    }
    public void printingCheck() {
        Log.i("calling from out side", "same here too");
    }
}
