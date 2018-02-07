package com.antandbuffalo.birthdayreminder.update;

import android.app.AlertDialog;
import android.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.TabsAdapter;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.addnew.AddNewViewModel;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.fragments.MyFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Update extends FragmentActivity {
    TextView selectedDate;
    EditText name;
    DatePicker datePicker;
    DateOfBirth currentDOB;
    Intent intent;
    Calendar cal;

    private UpdateViewModel updateViewModel;
    int dayOfYear, currentDayOfYear, recentDayOfYear;
    TextView namePreview, desc, dateField, monthField, yearField;
    Spinner yearSpinner, monthSpinner, datesSpinner;
    LinearLayout circle;
    ImageButton update, cancel, delete;
    Map<Integer, Integer> yearMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        initLayout();

        updateViewModel = ViewModelProviders.of(this).get(UpdateViewModel.class);

        currentDOB = (DateOfBirth)getIntent().getSerializableExtra("currentDOB");
        updateViewModel.setDateOfBirth(currentDOB);

        name = (EditText)findViewById(R.id.personName);
        name.setText(currentDOB.getName());

        currentDayOfYear = Util.getCurrentDayOfYear();
        recentDayOfYear = Util.getRecentDayOfYear();

        addYearsToSpinner(yearSpinner);
        addMonthsToSpinner(monthSpinner);
        addDatesToSpinner(datesSpinner);

        yearSpinner.setSelection(updateViewModel.getSelectedYearPosition());
        monthSpinner.setSelection(updateViewModel.getSelectedMonthPosition());
        datesSpinner.setSelection(updateViewModel.getSelectedDatePosition());

        delete = (ImageButton)findViewById(R.id.delete);
        delete.setBackgroundResource(R.drawable.delete_button);

        cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        update = (ImageButton)findViewById(R.id.save);
        update.setBackgroundResource(R.drawable.save_button);

        intent = new Intent();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new AlertDialog.Builder(Update.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to delete " + currentDOB.getName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DateOfBirthDBHelper.deleteRecordForTheId(currentDOB.getDobId());
                        currentDOB = null;
                        Toast toast = Toast.makeText(getApplicationContext(), Constants.NOTIFICATION_DELETE_MEMBER_SUCCESS, Toast.LENGTH_SHORT);
                        toast.show();
                        setResult(RESULT_OK, intent);
                        clearInputs();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
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
                    currentDOB.setName(plainName);
                    currentDOB.setDobDate(plainDate);

                    if (!DateOfBirthDBHelper.isUniqueDateOfBirth(currentDOB)) {
                        //put confirmation here
                        new AlertDialog.Builder(Update.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle(Constants.ERROR)
                                .setMessage(Constants.USER_EXIST)
                                .setPositiveButton(Constants.OK, null)
                                .show();
                    } else {
                        Log.i("after update", currentDOB.getName());
                        DateOfBirthDBHelper.updateDOB(currentDOB);
                        String status = Constants.NOTIFICATION_UPDATE_MEMBER_SUCCESS + ". You will get notified at 12:00am and 12:00pm on " + Util.getStringFromDate(currentDOB.getDobDate(), "dd MMM") + " every year";
                        Toast toast = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG);
                        toast.show();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

    public void initLayout() {
        name = (EditText)findViewById(R.id.personName);

        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        datesSpinner = (Spinner) findViewById(R.id.dateSpinner);
        yearSpinner = (Spinner) findViewById(R.id.yearSpinner);

        update = (ImageButton) findViewById(R.id.save);
        update.setBackgroundResource(R.drawable.save_button);

        cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        yearMapper = updateViewModel.getYearsMapper();

        namePreview = (TextView)findViewById(R.id.nameField);
        desc = (TextView)findViewById(R.id.ageField);
        dateField = (TextView)findViewById(R.id.dateField);
        monthField = (TextView)findViewById(R.id.monthField);
        yearField = (TextView)findViewById(R.id.yearField);

        circle = (LinearLayout)findViewById(R.id.circlebg);
    }

    public void addMonthsToSpinner(Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, updateViewModel.getMonths());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addDatesToSpinner(Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, updateViewModel.getDates());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void addYearsToSpinner(Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, updateViewModel.getYears());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void clearInputs() {
        name.setText("");
        cal = Calendar.getInstance();
        cal.setTime(new Date());
        datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        selectedDate.setText(Util.getStringFromDate(new Date(), Constants.ADD_NEW_DATE_FORMAT));
    }
}
