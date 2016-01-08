package com.antandbuffalo.birthdayreminder.today;

import android.content.Context;
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
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 23/9/15.
 */
public class TodayListAdapter extends BaseAdapter {
    int currentDayOfYear, dayOfYear;
    Calendar cal;
    List<DateOfBirth> dobs;
    SimpleDateFormat dateFormatter;

    TodayListAdapter() {
        dobs = getDataForListView();
        currentDayOfYear = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));
        cal = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MMM");
    }

    @Override
    public int getCount() {
        if(dobs == null) {
            return 0;
        }
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
            convertView = inflater.inflate(R.layout.list_item_default, parent, false);
        }

        TextView name = (TextView)convertView.findViewById(R.id.nameField);
        TextView desc = (TextView)convertView.findViewById(R.id.ageField);
        TextView noBirthday = (TextView)convertView.findViewById(R.id.noBirthday);

        TextView dateField = (TextView)convertView.findViewById(R.id.dateField);
        TextView monthField = (TextView)convertView.findViewById(R.id.monthField);
        TextView yearField = (TextView)convertView.findViewById(R.id.yearField);

        DateOfBirth dob = dobs.get(position);
        name.setText(dob.getName());
        desc.setText(dob.getDescription());
        noBirthday.setText(dob.getName());
        if(dob.getDobId() == -1) {
            name.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);
            noBirthday.setVisibility(View.VISIBLE);
        }
        else {
            name.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
            noBirthday.setVisibility(View.INVISIBLE);
        }
        Date date = dob.getDobDate();
        cal.setTime(date);


        dateField.setText(cal.get(Calendar.DATE) + "");
        monthField.setText(dateFormatter.format(cal.getTime()));
        yearField.setText(cal.get(Calendar.YEAR) + "");

        LinearLayout circle = (LinearLayout)convertView.findViewById(R.id.circlebg);
        dayOfYear = Integer.parseInt(Util.getStringFromDate(dob.getDobDate(), Constants.DAY_OF_YEAR));
        if(dayOfYear == currentDayOfYear) {
            circle.setBackgroundResource(R.drawable.cirlce_today);
        }
        else {
            circle.setBackgroundResource(R.drawable.cirlce_missed);
        }

        return convertView;
    }

    public List<DateOfBirth> getDataForListView() {
        List<DateOfBirth> allDobs = DateOfBirthDBHelper.selectTodayAndBelated();
        DateOfBirth today = DateOfBirth.getInstance();
        today.setDobDate(new Date());
        today.setName("No Birthdays Today");
        today.setDobId(-1);
        if(allDobs != null && allDobs.size() > 0) {
            if(Util.compareDateAndMonth(allDobs.get(0).getDobDate(), new Date()) != 0) {
                allDobs.add(0, today);
            }
        }
        else {
            allDobs.add(today);
        }
        return allDobs;
    }
    public void refreshData() {
        currentDayOfYear = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));
        dobs = getDataForListView();
        this.notifyDataSetChanged();
    }
}
