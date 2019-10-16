package com.antandbuffalo.birthdayreminder.update;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.antandbuffalo.birthdayreminder.database.DBHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Update extends FragmentActivity {
    EditText name;
    DateOfBirth currentDOB;
    Intent intent;

    private UpdateViewModel updateViewModel;
    int dayOfYear, currentDayOfYear, recentDayOfYear;
    TextView namePreview, desc, dateField, monthField, yearField;
    Spinner monthSpinner;
    EditText dateText, yearText;
    LinearLayout circle;
    ImageButton update, cancel, delete;
    CheckBox removeYear;

    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        DBHelper.createInstance(this);

        updateViewModel = ViewModelProviders.of(this).get(UpdateViewModel.class);

        currentDOB = (DateOfBirth)getIntent().getSerializableExtra("currentDOB");
        updateViewModel.initDefaults(currentDOB);
        initLayout();

        loadAd();

        name.setText(updateViewModel.birthdayInfo.name);
        dateText.setText(updateViewModel.birthdayInfo.date);
        yearText.setText(updateViewModel.birthdayInfo.year);
        removeYear.setChecked(updateViewModel.birthdayInfo.isRemoveYear);

        if(updateViewModel.birthdayInfo.isRemoveYear) {
            yearText.setVisibility(View.INVISIBLE);
        }
        else {
            yearText.setVisibility(View.VISIBLE);
        }

        currentDayOfYear = Util.getCurrentDayOfYear();
        recentDayOfYear = Util.getRecentDayOfYear();

        addMonthsToSpinner(monthSpinner);
        monthSpinner.setSelection(updateViewModel.getSelectedMonthPosition());

        preview();

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Item select", "" + position);
                updateViewModel.setSelectedMonth(monthSpinner.getSelectedItemPosition());
                preview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Item select", "" + parent);
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
                updateViewModel.setName(name.getText().toString());
                preview();
            }
        });

        dateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateViewModel.setBirthdayInfoDate(dateText.getText().toString());
                preview();
            }
        });

        yearText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateViewModel.setBirthdayInfoYear(yearText.getText().toString());
                preview();
            }
        });

        removeYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateViewModel.setRemoveYear(isChecked);
                if(isChecked) {
                    yearText.setVisibility(View.INVISIBLE);
                }
                else {
                    yearText.setVisibility(View.VISIBLE);
                }
                addMonthsToSpinner(monthSpinner);
                monthSpinner.setSelection(updateViewModel.getSelectedMonthPosition());
                preview();
            }
        });

        intent = new Intent();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new AlertDialog.Builder(Update.this)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to delete " + currentDOB.getName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateViewModel.delete(currentDOB.getDobId());
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
                populateBirthdayInfo();
                updateViewModel.setDateOfBirth(updateViewModel.birthdayInfo);

                if (updateViewModel.isNameEmpty()) {
                    //show error
                    Toast.makeText(getApplicationContext(), Constants.NAME_EMPTY, Toast.LENGTH_SHORT).show();
                } else {
                    if (updateViewModel.isDOBAvailable(updateViewModel.dateOfBirth)) {
                        //put confirmation here
                        new AlertDialog.Builder(Update.this)
                                //.setIcon(android.R.drawable.ic_dialog_info)
                                .setIconAttribute(android.R.attr.alertDialogIcon)
                                .setTitle(Constants.ERROR)
                                .setMessage(Constants.USER_EXIST)
                                .setPositiveButton(Constants.OK, null)
                                .show();
                    } else {
                        Log.i("after update", currentDOB.getName());
                        updateViewModel.update();
                        String status = Constants.NOTIFICATION_UPDATE_MEMBER_SUCCESS + ". You will get notified at 12:00am and 12:00pm on " + Util.getStringFromDate(currentDOB.getDobDate(), "dd MMM") + " every year";
                        status = Constants.NOTIFICATION_UPDATE_MEMBER_SUCCESS + ". " + Util.getNotificationMessageWithTime(getApplicationContext(), updateViewModel.dateOfBirth.getDobDate());
                        Toast toast = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG);
                        toast.show();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            }
        });
    }

    public void addMonthsToSpinner(Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, updateViewModel.getMonths());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void populateBirthdayInfo() {
        updateViewModel.setBirthdayInfoName(name.getText().toString());
        updateViewModel.setBirthdayInfoDate(dateText.getText().toString());
        updateViewModel.setBirthdayInfoMonth(monthSpinner.getSelectedItemPosition());
        updateViewModel.setBirthdayInfoRemoveYear(removeYear.isChecked());

        updateViewModel.setBirthdayInfoYear(updateViewModel.birthdayInfo.isRemoveYear? Constants.REMOVE_YEAR_VALUE.toString() : yearText.getText().toString());
    }

    public void preview() {
        if(!updateViewModel.isValidDateOfBirth(updateViewModel.birthdayInfo)) {
            if(updateViewModel.birthdayInfo.name == null || ("".equalsIgnoreCase(updateViewModel.birthdayInfo.name))) {
                namePreview.setText("Enter Name");
                desc.setText("Age");
            } else {
                namePreview.setText("Error in birthdate");
                desc.setText("Please enter valid date");
            }
            return;
        }
        populateBirthdayInfo();

        namePreview.setText(updateViewModel.birthdayInfo.name);
        updateViewModel.setDateOfBirth(updateViewModel.birthdayInfo);
        dayOfYear = Util.getDayOfYear(updateViewModel.dateOfBirth.getDobDate());

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

        Util.setDescription(updateViewModel.dateOfBirth, "Age");

        if(updateViewModel.getRemoveYear()) {
            yearField.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);
        }
        else {
            if(updateViewModel.dateOfBirth.getAge() < 0) {
                desc.setVisibility(View.INVISIBLE);
            } else {
                desc.setVisibility(View.VISIBLE);
            }
            yearField.setVisibility(View.VISIBLE);
        }

        dateField.setText(updateViewModel.birthdayInfo.date);
        monthField.setText(Util.getStringFromDate(updateViewModel.dateOfBirth.getDobDate(), "MMM"));
        yearField.setText(updateViewModel.birthdayInfo.year + "");
        desc.setText(updateViewModel.dateOfBirth.getDescription());
    }

    public void initLayout() {
        name = (EditText)findViewById(R.id.personName);

        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        dateText = findViewById(R.id.date);
        yearText = findViewById(R.id.year);

        removeYear = (CheckBox) findViewById(R.id.removeYear);

        update = (ImageButton) findViewById(R.id.save);
        update.setBackgroundResource(R.drawable.save_button);

        cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        namePreview = (TextView)findViewById(R.id.nameField);
        desc = (TextView)findViewById(R.id.ageField);
        dateField = (TextView)findViewById(R.id.dateField);
        monthField = (TextView)findViewById(R.id.monthField);
        yearField = (TextView)findViewById(R.id.yearField);

        circle = (LinearLayout)findViewById(R.id.circlebg);

        delete = (ImageButton)findViewById(R.id.delete);
        delete.setBackgroundResource(R.drawable.delete_button);

        cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        update = (ImageButton)findViewById(R.id.save);
        update.setBackgroundResource(R.drawable.save_button);

        mAdView = findViewById(R.id.adView);
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void clearInputs() {
        name.setText("");
    }
}
