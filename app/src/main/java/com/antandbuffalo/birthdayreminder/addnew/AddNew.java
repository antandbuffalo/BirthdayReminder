package com.antandbuffalo.birthdayreminder.addnew;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.util.Calendar;
import java.util.Date;

public class AddNew extends Activity {
    EditText name;
    DatePicker datePicker;
    Intent intent = null;
    TextView selectedDate, dateView, monthView, yearView, nameView, descView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new);

        selectedDate = (TextView)findViewById(R.id.selectedDate);
        selectedDate.setText(Util.getStringFromDate(new Date(), Constants.ADD_NEW_DATE_FORMAT));
        name = (EditText)findViewById(R.id.personName);
        datePicker = (DatePicker)findViewById(R.id.perosnDateOfBirth);
        datePicker.setMaxDate(new Date().getTime());

        initViews();

        datePicker.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Date selectedDateObject = Util.getDateFromString(year + "-" + (month + 1) + "-" + dayOfMonth);
                selectedDate.setText(Util.getStringFromDate(Util.getDateFromString(year + "-" + (month + 1) + "-" + dayOfMonth), Constants.ADD_NEW_DATE_FORMAT));

//                dateView.setText(String.valueOf(dayOfMonth));
//                monthView.setText(Util.getStringFromDate(selectedDateObject, "MMM"));
//                yearView.setText(String.valueOf(year));
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                nameView.setText(name.getText());
            }
        });

        intent = new Intent();
        intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
        setResult(RESULT_OK, intent);

        Button save = (Button)findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plainName = name.getText().toString().trim();
                Calendar cal = Util.getCalendar();
                cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                Date plainDate = cal.getTime();

                if (plainName.equalsIgnoreCase("")) {
                    //show error
                    Toast.makeText(getApplicationContext(), Constants.ERROR_1001, Toast.LENGTH_LONG).show();
                } else {
                    DateOfBirth dob = new DateOfBirth();
                    dob.setName(plainName);
                    dob.setDobDate(plainDate);

                    DateOfBirthDBHelper.insertDOB(dob);
                    System.out.println("Inserted successfully");

                    Toast toast = Toast.makeText(getApplicationContext(), Constants.NOTIFICATION_ADD_MEMBER_SUCCESS, Toast.LENGTH_SHORT);
                    toast.show();
                    intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_SUCCESS.toString());
                    setResult(RESULT_OK, intent);
                    clearInputs();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initViews() {
        dateView = (TextView)findViewById(R.id.dateField);
        monthView = (TextView)findViewById(R.id.monthField);
        yearView = (TextView)findViewById(R.id.yearField);
        nameView = (TextView)findViewById(R.id.nameField);
        descView = (TextView)findViewById(R.id.ageField);

        dateView.setText("");
        monthView.setText("");
        yearView.setText("");
        nameView.setText("");
        descView.setText("");

        LinearLayout circle = (LinearLayout)findViewById(R.id.circlebg);
        circle.setBackgroundResource(R.drawable.cirlce_today);
        RelativeLayout summary = (RelativeLayout)findViewById(R.id.summaryLayout);
        summary.setVisibility(View.INVISIBLE);
    }

    public void clearInputs() {
        name.setText("");
    }
}
