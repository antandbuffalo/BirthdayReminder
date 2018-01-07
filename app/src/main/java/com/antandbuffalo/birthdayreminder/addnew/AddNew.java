package com.antandbuffalo.birthdayreminder.addnew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        ImageButton cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        Spinner monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        addMonthsToSpinner(monthSpinner);

        Spinner datesSpinner = (Spinner) findViewById(R.id.dateSpinner);
        addDatesToSpinner(datesSpinner);

        Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        addYearsToSpinner(yearSpinner);


//        Calendar currentDate = Util.getCalendar(new Date());
//        datePicker.updateDate(currentDate.get(Calendar.YEAR) - 1, currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

        datePicker.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //Date selectedDateObject = Util.getDateFromString(year + "-" + (month + 1) + "-" + dayOfMonth);
                //selectedDate.setText(Util.getStringFromDate(Util.getDateFromString(year + "-" + (month + 1) + "-" + dayOfMonth), Constants.ADD_NEW_DATE_FORMAT));
                String strMonth = month >= 9? (month + 1) + "" : "0" + (month + 1);
                selectedDate.setText(dayOfMonth + "/" + strMonth + "/" + year);
//                dateView.setText(String.valueOf(dayOfMonth));
//                monthView.setText(Util.getStringFromDate(selectedDateObject, "MMM"));
//                yearView.setText(String.valueOf(year));
            }
        });

        /*name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                nameView.setText(name.getText());
            }
        });*/

        intent = new Intent();
        intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
        setResult(RESULT_OK, intent);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plainName = name.getText().toString().trim();
                Calendar cal = Util.getCalendar();
                cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                Date plainDate = cal.getTime();

                if (plainName.equalsIgnoreCase("")) {
                    //show error
                    Toast.makeText(getApplicationContext(), Constants.NAME_EMPTY, Toast.LENGTH_SHORT).show();
                } else {
                    DateOfBirth dob = new DateOfBirth();
                    dob.setName(plainName);
                    dob.setDobDate(plainDate);
                    if (!DateOfBirthDBHelper.isUniqueDateOfBirthIgnoreCase(dob)) {
                        //put confirmation here
                        new AlertDialog.Builder(AddNew.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle(Constants.ERROR)
                                .setMessage(Constants.USER_EXIST)
                                .setPositiveButton(Constants.OK, null)
                                .show();
                    } else {
                        final String fileName = Util.fileToLoad(plainName);
                        if (fileName != null) {
                            //if(plainName.equalsIgnoreCase("csea")) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddNew.this);
                            alertDialogBuilder.setTitle("Confirmation");
                            alertDialogBuilder.setMessage("Are you sure want to merge current data with " + plainName + " data?");
                            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Util.readFromAssetFile(fileName);
                                    Toast toast = Toast.makeText(getApplicationContext(), Constants.NOTIFICATION_SUCCESS_DATA_LOAD, Toast.LENGTH_SHORT);
                                    toast.show();
                                    intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_SUCCESS.toString());
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                            alertDialogBuilder.setNegativeButton("No", null);
                            alertDialogBuilder.show();
                        } else {
                            DateOfBirthDBHelper.insertDOB(dob);
                            Util.updateFile(dob);
                            System.out.println("Inserted successfully");
                            clearInputs();
                            String status = Constants.NOTIFICATION_ADD_MEMBER_SUCCESS + ". You will get notified at 12:00am and 12:00pm on " + Util.getStringFromDate(dob.getDobDate(), "dd MMM") + " every year";
                            Toast toast = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG);
                            toast.show();
                            intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_SUCCESS.toString());
                            setResult(RESULT_OK, intent);
                        }

                    }
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

    public void addMonthsToSpinner(Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, Util.getMonths());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addDatesToSpinner(Spinner spinner) {
        List<String> datesList = new ArrayList<String>();
        for (int i = 1; i <= 31; i++) {
            datesList .add(i + "");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, datesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addYearsToSpinner(Spinner spinner) {
        List<String> yearsList = new ArrayList<String>();
        for (int i = 1901; i <= 2018; i++) {
            yearsList .add(i + "");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void clearInputs() {
        name.setText("");
    }
}
