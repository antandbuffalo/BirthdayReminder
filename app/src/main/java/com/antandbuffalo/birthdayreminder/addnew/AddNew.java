package com.antandbuffalo.birthdayreminder.addnew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNew extends Activity {
    EditText name;
    DatePicker datePicker;
    Intent intent = null;
    TextView selectedDate, dateView, monthView, yearView, nameView, descView;
    Integer date, month, year;
    Calendar cal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new);

        DateOfBirth dob = new DateOfBirth();

        selectedDate = (TextView)findViewById(R.id.selectedDate);
        selectedDate.setText(Util.getStringFromDate(new Date(), Constants.ADD_NEW_DATE_FORMAT));
        name = (EditText)findViewById(R.id.personName);

        cal = Util.getCalendar();

        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        ImageButton cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        final Spinner monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        final Spinner datesSpinner = (Spinner) findViewById(R.id.dateSpinner);
        final Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);

        addMonthsToSpinner(monthSpinner);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Item select", "" + position);
                addDatesToSpinner(datesSpinner, Constants.MONTH_DAYS.get(position));
                if(cal.get(Calendar.MONTH) == position) {
                    datesSpinner.setSelection(cal.get(Calendar.DAY_OF_MONTH) - 1);
                }
                month = monthSpinner.getSelectedItemPosition();
                preview("Test", date, month, year);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Item select", "" + parent);
            }
        });

        final Map<Integer, Integer> yearMapper = getYearMapper(Constants.START_YEAR, cal.get(Calendar.YEAR));

        addYearsToSpinner(yearSpinner, Constants.START_YEAR, cal.get(Calendar.YEAR));
        monthSpinner.setSelection(cal.get(Calendar.MONTH));
        yearSpinner.setSelection(yearMapper.get(cal.get(Calendar.YEAR)));

        datesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date = Integer.parseInt(datesSpinner.getSelectedItem().toString());
                preview("Test", date, month, year);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Item select", "" + parent);
            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = Integer.parseInt(yearSpinner.getSelectedItem().toString());
                preview("Test", date, month, year);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        datePicker.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                //Date selectedDateObject = Util.getDateFromString(year + "-" + (month + 1) + "-" + dayOfMonth);
//                //selectedDate.setText(Util.getStringFromDate(Util.getDateFromString(year + "-" + (month + 1) + "-" + dayOfMonth), Constants.ADD_NEW_DATE_FORMAT));
//                String strMonth = month >= 9? (month + 1) + "" : "0" + (month + 1);
//                selectedDate.setText(dayOfMonth + "/" + strMonth + "/" + year);
////                dateView.setText(String.valueOf(dayOfMonth));
////                monthView.setText(Util.getStringFromDate(selectedDateObject, "MMM"));
////                yearView.setText(String.valueOf(year));
//            }
//        });

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

        CheckBox removeYear = (CheckBox) findViewById(R.id.removeYear);
        removeYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    yearSpinner.setVisibility(View.INVISIBLE);
                }
                else {
                    yearSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        intent = new Intent();
        intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
        setResult(RESULT_OK, intent);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plainName = name.getText().toString().trim();
                Calendar cal = Util.getCalendar();
                if(yearSpinner.getVisibility() == View.INVISIBLE) {
                    year = Constants.NO_YEAR;
                }
                cal.set(year, month, date);
                Log.i("DATE", cal.getTime() + "");
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

    public void addDatesToSpinner(Spinner spinner, Integer maxValue) {
        List<String> datesList = new ArrayList<String>();
        for (int i = 1; i <= maxValue; i++) {
            datesList .add(i + "");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, datesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addYearsToSpinner(Spinner spinner, Integer minYear, Integer maxYear) {
        List<String> yearsList = new ArrayList<String>();
        for (int i = minYear; i <= maxYear; i++) {
            yearsList .add(i + "");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearsList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public Map<Integer, Integer> getYearMapper(Integer minYear, Integer maxYear) {
        Map<Integer, Integer> yearsMap = new HashMap<Integer, Integer>();
        Integer counter = maxYear - minYear;
        int j = minYear;
        for (int i=0; i<=counter; i++) {
            Log.i("YEAR", i + " -- " + j);
            yearsMap.put(j++, i);
        }
        return yearsMap;
    }

    public void preview(String givenName, Integer date, Integer month, Integer year) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM");
        TextView name = (TextView)findViewById(R.id.nameField);
        TextView desc = (TextView)findViewById(R.id.ageField);
        TextView dateField = (TextView)findViewById(R.id.dateField);
        TextView monthField = (TextView)findViewById(R.id.monthField);
        TextView yearField = (TextView)findViewById(R.id.yearField);

        name.setText(givenName);
        dateField.setText(date != null? date + "" : "");
        monthField.setText(month != null? month + "" : "");
        yearField.setText(year != null? year + "" : "");
    }

    public void clearInputs() {
        name.setText("");
    }
}
