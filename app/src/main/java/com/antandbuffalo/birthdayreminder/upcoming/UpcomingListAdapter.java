package com.antandbuffalo.birthdayreminder.upcoming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DobDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 23/9/15.
 */
public class UpcomingListAdapter extends BaseAdapter {

    int currentDayOfYear, dayOfYear, recentDayOfYear;
    Calendar cal;
    List<DateOfBirth> dobs;
    UpcomingListAdapter() {
        cal = Calendar.getInstance();
        currentDayOfYear = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));

        cal.setTime(new Date());
        cal.add(Calendar.DATE, Constants.RECENT_DURATION);
        recentDayOfYear = Integer.parseInt(Util.getStringFromDate(cal.getTime(), Constants.DAY_OF_YEAR));
        dobs = DobDBHelper.selectAll();
    }

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

        TextView name = (TextView)convertView.findViewById(R.id.nameField);
        TextView desc = (TextView)convertView.findViewById(R.id.ageField);

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

        LinearLayout circle = (LinearLayout)convertView.findViewById(R.id.circlebg);
        dayOfYear = Integer.parseInt(Util.getStringFromDate(dob.getDobDate(), Constants.DAY_OF_YEAR));
        if(dayOfYear == currentDayOfYear) {
            circle.setBackgroundResource(R.drawable.cirlce_today);
        }
        else if(dayOfYear <= recentDayOfYear && dayOfYear > currentDayOfYear ){
            circle.setBackgroundResource(R.drawable.cirlce_recent);
        }
        else {
            circle.setBackgroundResource(R.drawable.cirlce_normal);
        }

//        dateField.setText("27");
//        monthField.setText("Sep");
//        yearField.setText("2018");

        return convertView;
    }

    public void updateData() {
        System.out.println("inside adapter");
        dobs.clear();
        dobs.addAll(DobDBHelper.selectAll());
        this.notifyDataSetChanged();
    }
}
