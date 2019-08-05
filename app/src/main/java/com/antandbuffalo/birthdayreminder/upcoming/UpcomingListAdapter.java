package com.antandbuffalo.birthdayreminder.upcoming;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Filter;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by i677567 on 23/9/15.
 */
public class UpcomingListAdapter extends BaseAdapter implements Filterable {

    int currentDayOfYear, dayOfYear, recentDayOfYear;
    Calendar cal;
    List<DateOfBirth> dobs;
    List<DateOfBirth> allDobs;
    SimpleDateFormat dateFormatter;
    List<DateOfBirth> filteredDobs = new ArrayList<DateOfBirth>();
    UpcomingListAdapter() {
        dateFormatter = new SimpleDateFormat("MMM");
        cal = Calendar.getInstance();
        setDefaultValues();
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
        noBirthday.setVisibility(View.INVISIBLE);

        TextView dateField = (TextView)convertView.findViewById(R.id.dateField);
        TextView monthField = (TextView)convertView.findViewById(R.id.monthField);
        TextView yearField = (TextView)convertView.findViewById(R.id.yearField);

        DateOfBirth dob = dobs.get(position);
        name.setText(dob.getName());
        desc.setText(dob.getDescription());
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
        else if(recentDayOfYear < currentDayOfYear) {   //year end case
            if(dayOfYear > currentDayOfYear || dayOfYear < recentDayOfYear) {
                circle.setBackgroundResource(R.drawable.cirlce_recent);
            }
            else {
                circle.setBackgroundResource(R.drawable.cirlce_normal);
            }
        }
        else if(dayOfYear <= recentDayOfYear && dayOfYear > currentDayOfYear ){
            circle.setBackgroundResource(R.drawable.cirlce_recent);
        }
        else {
            circle.setBackgroundResource(R.drawable.cirlce_normal);
        }

        if(dob.getRemoveYear()) {
            desc.setVisibility(View.INVISIBLE);
            yearField.setVisibility(View.INVISIBLE);
        }
        else {
            if(dob.getAge() < 0) {
                desc.setVisibility(View.INVISIBLE);
            } else {
                desc.setVisibility(View.VISIBLE);
            }
            yearField.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void refreshData() {
        setDefaultValues();
        this.notifyDataSetChanged();
    }

    public void setDefaultValues() {
        currentDayOfYear = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));
        cal.setTime(new Date());
        cal.add(Calendar.DATE, Constants.RECENT_DURATION);
        recentDayOfYear = Integer.parseInt(Util.getStringFromDate(cal.getTime(), Constants.DAY_OF_YEAR));
        allDobs = DateOfBirthDBHelper.selectUpcoming();
        dobs = DateOfBirthDBHelper.selectUpcoming();
    }

    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dobs = (List<DateOfBirth>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                filteredDobs.clear();
                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                DateOfBirth dob;
                String dateString;
                for (int i = 0; i < allDobs.size(); i++) {
                    dob = allDobs.get(i);
                    dateString = Util.getStringFromDate(dob.getDobDate());
                    if (dob.getName().toLowerCase().contains(constraint.toString()) || dateString.contains(constraint.toString()))  {
                        filteredDobs.add(dob);
                    }
                }

                results.count = filteredDobs.size();
                results.values = filteredDobs;
                Log.e("VALUES", results.count + "");
                //filteredDobs = null;
                return results;
            }
        };

        return filter;
    }
}
