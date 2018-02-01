package com.antandbuffalo.birthdayreminder.addnew;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNew extends FragmentActivity {
    EditText name;
    Intent intent = null;
    Integer date, month, year, selectedDate;
    Calendar cal;
    SimpleDateFormat dateFormatter;
    int dayOfYear, currentDayOfYear, recentDayOfYear;
    AddNewViewModel addNewViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new);

        addNewViewModel = ViewModelProviders.of(this).get(AddNewViewModel.class);

        dateFormatter = new SimpleDateFormat("MMM");
        currentDayOfYear = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));

        cal = Util.getCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, Constants.RECENT_DURATION);
        recentDayOfYear = Integer.parseInt(Util.getStringFromDate(cal.getTime(), Constants.DAY_OF_YEAR));


        name = (EditText)findViewById(R.id.personName);

        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        final ImageButton cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        final Spinner monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        final Spinner datesSpinner = (Spinner) findViewById(R.id.dateSpinner);
        final Spinner yearSpinner = (Spinner) findViewById(R.id.yearSpinner);
        final Map<Integer, Integer> yearMapper = addNewViewModel.getYearsMapper(Constants.START_YEAR, cal.get(Calendar.YEAR));

        addMonthsToSpinner(monthSpinner);
        monthSpinner.setSelection(cal.get(Calendar.MONTH));

        addYearsToSpinner(yearSpinner, Constants.START_YEAR, cal.get(Calendar.YEAR));
        yearSpinner.setSelection(yearMapper.get(cal.get(Calendar.YEAR)));

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Item select", "" + position);
                addDatesToSpinner(datesSpinner, Constants.MONTH_DAYS.get(position));
                //to select today's date if the month is current month
                cal.setTime(new Date());
                if(date == null) {
                    datesSpinner.setSelection(cal.get(Calendar.DAY_OF_MONTH) - 1);
                }
                else {
                    datesSpinner.setSelection(date - 1);
                }
                //passing position here. Because the item value is text "Jan"
                month = monthSpinner.getSelectedItemPosition();
                preview(name.getText().toString(), date, month, year);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Item select", "" + parent);
            }
        });

        datesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                date = Integer.parseInt(datesSpinner.getSelectedItem().toString());
                preview(name.getText().toString(), date, month, year);
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
                preview(name.getText().toString(), date, month, year);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                preview(name.getText().toString(), date, month, year);
            }
        });

        CheckBox removeYear = (CheckBox) findViewById(R.id.removeYear);
        removeYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    yearSpinner.setVisibility(View.INVISIBLE);
                    preview(name.getText().toString(), date, month, Constants.NO_YEAR);
                }
                else {
                    yearSpinner.setVisibility(View.VISIBLE);
                    preview(name.getText().toString(), date, month, year);
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
                android.R.layout.simple_spinner_item, addNewViewModel.getMonths());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addDatesToSpinner(Spinner spinner, Integer maxValue) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, addNewViewModel.getDates(maxValue));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addYearsToSpinner(Spinner spinner, Integer minYear, Integer maxYear) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, addNewViewModel.getYears(minYear, maxYear));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void preview(String givenName, Integer date, Integer month, Integer year) {

        if(date == null || month == null || year == null) {
            return;
        }
        DateOfBirth dateOfBirth = new DateOfBirth();
        dateOfBirth.setDobDate(Util.getDateFromString(year + "-" + (month + 1) + "-" + date));

        dayOfYear = Integer.parseInt(Util.getStringFromDate(dateOfBirth.getDobDate(), Constants.DAY_OF_YEAR));

        TextView name = (TextView)findViewById(R.id.nameField);
        TextView desc = (TextView)findViewById(R.id.ageField);
        TextView dateField = (TextView)findViewById(R.id.dateField);
        TextView monthField = (TextView)findViewById(R.id.monthField);
        TextView yearField = (TextView)findViewById(R.id.yearField);

        LinearLayout circle = (LinearLayout)findViewById(R.id.circlebg);

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

        dateOfBirth.setAge(Util.getAge(dateOfBirth.getDobDate()));
        Util.setDescription(dateOfBirth, "Age");

        if(year == Constants.NO_YEAR) {
            yearField.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);
        }
        else {
            yearField.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
        }

        name.setText(givenName);
        dateField.setText(date + "");
        monthField.setText(dateFormatter.format(dateOfBirth.getDobDate().getTime()));
        yearField.setText(year + "");
        desc.setText(dateOfBirth.getDescription());

    }

    public void clearInputs() {
        name.setText("");
    }
}
