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

public class AddNew extends FragmentActivity {
    EditText name;
    Intent intent = null;
    int dayOfYear, currentDayOfYear, recentDayOfYear;
    AddNewViewModel addNewViewModel;

    TextView namePreview, desc, dateField, monthField, yearField;
    EditText dateInput, monthInput, yearInput;
    LinearLayout circle;
    CheckBox removeYear;

    ImageButton save, cancel;

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

        dateInput.setText(addNewViewModel.getSelectedDate() + "");
        monthInput.setText(addNewViewModel.getSelectedMonth() + "");
        yearInput.setText(addNewViewModel.getSelectedYear() + "");

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                addNewViewModel.setName(name.getText().toString());
                preview();
            }
        });

        removeYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addNewViewModel.setRemoveYear(isChecked);
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
                addNewViewModel.setName(name.getText().toString());
                addNewViewModel.setDateOfBirth();

                if (addNewViewModel.isNameEmpty()) {
                    //show error
                    Toast.makeText(getApplicationContext(), Constants.NAME_EMPTY, Toast.LENGTH_SHORT).show();
                } else {
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
                            alertDialogBuilder.setMessage("Are you sure want to merge current data with " + addNewViewModel.name + " data?");
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
                            addNewViewModel.saveToDB();
                            System.out.println("Inserted successfully");
                            String status = Constants.NOTIFICATION_ADD_MEMBER_SUCCESS + ". You will get notified at 12:00am and 12:00pm on " + Util.getStringFromDate(addNewViewModel.dateOfBirth.getDobDate(), "dd MMM") + " every year";
                            Toast toast = Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG);
                            toast.show();
                            addNewViewModel.clearInputs();
                            clearInputs();
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

        removeYear.setChecked(addNewViewModel.getRemoveYear());
    }

    public void preview() {
        addNewViewModel.setDateOfBirth();

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

        if(addNewViewModel.getRemoveYear()) {
            yearField.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);
        }
        else {
            yearField.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
        }

        namePreview.setText(addNewViewModel.name);
        dateField.setText(addNewViewModel.date + "");
        monthField.setText(Util.getStringFromDate(addNewViewModel.dateOfBirth.getDobDate(), "MMM"));
        yearField.setText(addNewViewModel.year + "");
        desc.setText(addNewViewModel.dateOfBirth.getDescription());
    }

    public void initLayout() {
        name = (EditText)findViewById(R.id.personName);

        dateInput = (EditText) findViewById(R.id.date);
        monthInput = (EditText)findViewById(R.id.month);
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

    public void clearInputs() {
        addNewViewModel.initDefaults();
        name.setText(addNewViewModel.name);
        removeYear.setChecked(addNewViewModel.getRemoveYear());

        yearInput.setText("");
        monthInput.setText("");
        dateInput.setText("");
    }
}
