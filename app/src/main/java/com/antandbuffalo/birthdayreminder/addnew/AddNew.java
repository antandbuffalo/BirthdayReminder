package com.antandbuffalo.birthdayreminder.addnew;

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
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DBHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AddNew extends FragmentActivity {
    EditText name;
    Intent intent = null;
    int dayOfYear, currentDayOfYear, recentDayOfYear;
    AddNewViewModel addNewViewModel;

    TextView namePreview, desc, dateField, monthField, yearField;
    EditText dateInput, yearInput;
    LinearLayout circle;
    CheckBox removeYear;
    Spinner monthSpinner;

    ImageButton save, cancel;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new);

        DBHelper.createInstance(this);

        addNewViewModel = ViewModelProviders.of(this).get(AddNewViewModel.class);
        addNewViewModel.initDefaults();

        initLayout();

        currentDayOfYear = Util.getCurrentDayOfYear();
        recentDayOfYear = Util.getRecentDayOfYear();

        initViewValues();

        loadAd();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                addNewViewModel.setBirthdayInfoName(name.getText().toString());
                preview();
            }
        });

        dateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addNewViewModel.setBirthdayInfoDate(dateInput.getText().toString());
                preview();
            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Item select", "" + position);
                addNewViewModel.setBirthdayInfoMonth(position);
                preview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Item select", "" + parent);
            }
        });

        yearInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addNewViewModel.setBirthdayInfoYear(yearInput.getText().toString());
                preview();
            }
        });

        removeYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addNewViewModel.setBirthdayInfoRemoveYear(isChecked);
                if(isChecked) {
                    yearInput.setVisibility(View.INVISIBLE);
                }
                else {
                    yearInput.setVisibility(View.VISIBLE);
                }
                preview();
            }
        });

        intent = new Intent();
        intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
        setResult(RESULT_OK, intent);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateBirthdayInfo();

                addNewViewModel.setDateOfBirth(addNewViewModel.birthdayInfo);

                if (addNewViewModel.isNameEmpty()) {
                    //show error
                    Toast.makeText(getApplicationContext(), Constants.NAME_EMPTY, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!addNewViewModel.isValidDateOfBirth(addNewViewModel.birthdayInfo)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (addNewViewModel.isDOBAvailable(addNewViewModel.dateOfBirth)) {
                    //put confirmation here
                    new AlertDialog.Builder(AddNew.this)
                            //.setIcon(android.R.drawable.ic_dialog_info)
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setTitle(Constants.ERROR)
                            .setMessage(Constants.USER_EXIST)
                            .setPositiveButton(Constants.OK, null)
                            .show();
                } else {
                    final String fileName = addNewViewModel.getFileName();
                    if (fileName != null) {
                        //if(plainName.equalsIgnoreCase("csea")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddNew.this);
                        alertDialogBuilder.setTitle("Confirmation");
                        alertDialogBuilder.setMessage("Are you sure want to merge current data with " + addNewViewModel.birthdayInfo.name + " data?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                addNewViewModel.loadFromFileWithName(fileName);
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
                        if(addNewViewModel.isValidDateOfBirth(addNewViewModel.birthdayInfo)) {
                            addNewViewModel.saveToDB();
                            System.out.println("Inserted successfully");
                            String status = Constants.NOTIFICATION_ADD_MEMBER_SUCCESS + ". You will get notified at 12:00am and 12:00pm on " + Util.getStringFromDate(addNewViewModel.dateOfBirth.getDobDate(), "dd MMM") + " every year";
                            Toast toast = Toast.makeText(getApplicationContext(), addNewViewModel.getAddSuccessMessage(getApplicationContext()), Toast.LENGTH_LONG);
                            toast.show();
                            clearInputs();
                            intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_SUCCESS.toString());
                            setResult(RESULT_OK, intent);
                        }
                        else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Please enter valid date", Toast.LENGTH_LONG);
                            toast.show();
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

        removeYear.setChecked(addNewViewModel.birthdayInfo.isRemoveYear);
        yearInput.setVisibility(View.INVISIBLE);
    }

    public void populateBirthdayInfo() {
        addNewViewModel.setBirthdayInfoName(name.getText().toString());
        addNewViewModel.setBirthdayInfoDate(dateInput.getText().toString());
        addNewViewModel.setBirthdayInfoMonth(monthSpinner.getSelectedItemPosition());
        addNewViewModel.setBirthdayInfoRemoveYear(removeYear.isChecked());

        addNewViewModel.setBirthdayInfoYear(addNewViewModel.birthdayInfo.isRemoveYear? Constants.REMOVE_YEAR_VALUE.toString() : yearInput.getText().toString());
    }

    public void addMonthsToSpinner(Spinner spinner) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, addNewViewModel.getMonths());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void preview() {
        if(!addNewViewModel.isValidDateOfBirth(addNewViewModel.birthdayInfo)) {
            if(addNewViewModel.birthdayInfo.name == null || ("".equalsIgnoreCase(addNewViewModel.birthdayInfo.name))) {
                namePreview.setText("Enter Name");
                desc.setText("Age");
            } else {
                namePreview.setText("Error in birthdate");
                desc.setText("Please enter valid date");
            }
            return;
        }
        populateBirthdayInfo();
        namePreview.setText(addNewViewModel.birthdayInfo.name);
        addNewViewModel.setDateOfBirth(addNewViewModel.birthdayInfo);

        dayOfYear = Util.getDayOfYear(addNewViewModel.dateOfBirth.getDobDate());

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

        Util.setDescription(addNewViewModel.dateOfBirth, "Age");

        if(addNewViewModel.birthdayInfo.isRemoveYear) {
            yearField.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);
        }
        else {
            if(addNewViewModel.dateOfBirth.getAge() < 0) {
                desc.setVisibility(View.INVISIBLE);
            } else {
                desc.setVisibility(View.VISIBLE);
            }
            yearField.setVisibility(View.VISIBLE);
        }

        dateField.setText(addNewViewModel.birthdayInfo.date + "");
        monthField.setText(Util.getStringFromDate(addNewViewModel.dateOfBirth.getDobDate(), "MMM"));
        yearField.setText(addNewViewModel.birthdayInfo.year + "");
        desc.setText(addNewViewModel.dateOfBirth.getDescription());
    }

    public void initLayout() {
        name = (EditText)findViewById(R.id.personName);
        dateInput = (EditText) findViewById(R.id.date);
        monthSpinner = (Spinner) findViewById(R.id.monthSpinner);
        yearInput = findViewById(R.id.year);
        removeYear = (CheckBox) findViewById(R.id.removeYear);

        save = (ImageButton) findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        namePreview = (TextView)findViewById(R.id.nameField);
        desc = (TextView)findViewById(R.id.ageField);
        dateField = (TextView)findViewById(R.id.dateField);
        monthField = (TextView)findViewById(R.id.monthField);
        yearField = (TextView)findViewById(R.id.yearField);

        circle = (LinearLayout)findViewById(R.id.circlebg);
    }

    public void initViewValues() {
        name.setText(addNewViewModel.birthdayInfo.name);
        dateInput.setText(addNewViewModel.birthdayInfo.date);
        addMonthsToSpinner(monthSpinner);
        monthSpinner.setSelection(addNewViewModel.getMonthSpinnerPosition(addNewViewModel.birthdayInfo.month));
        yearInput.setText(addNewViewModel.birthdayInfo.year);
        removeYear.setChecked(addNewViewModel.birthdayInfo.isRemoveYear);
    }

    public void clearInputs() {
        addNewViewModel.initDefaults();
        initViewValues();
    }

    public void loadAd() {
        mAdView = this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
